package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get list of Bug for my bug
 *  For details go through Use-Case doc named 'ListMyPtBugActionService'
 */
class ListMyPtBugActionService extends BaseService implements ActionIntf {

    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_BUG_FAILURE_MESSAGE = "Failed to load bug page"
    private static final String LST_BUG = "lstBug"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Bug list for my bug
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap)                                 // initialize parameters
            Map serviceReturn = listMyPtBug()
            List<GroovyRowResult> lstBug = serviceReturn.lstBug
            int count = serviceReturn.count
            result.put(LST_BUG, lstBug)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BUG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap Bug list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstBug = (List<GroovyRowResult>) executeResult.get(LST_BUG)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedBugs = wrapBugs(lstBug, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedBugs]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BUG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_BUG_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BUG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of Bugs in grid entity
     * @param lstBugs -list of Bug object
     * @param start -starting index of the page
     * @return -list of wrapped Bugs
     */
    private List wrapBugs(List<GroovyRowResult> lstBugs, int start) {
        List lstWrappedBugs = []
        int counter = start + 1
        for (int i = 0; i < lstBugs.size(); i++) {
            GroovyRowResult groovyRowResult = lstBugs[i]
            GridEntity obj = new GridEntity()
            obj.id = groovyRowResult.id
            obj.cell = [
                    counter,
                    groovyRowResult.id,
                    groovyRowResult.title,
                    groovyRowResult.step_to_reproduce,
                    groovyRowResult.status,
                    groovyRowResult.severity,
                    groovyRowResult.type,
                    DateUtility.getLongDateForUI(groovyRowResult.created_on),
                    groovyRowResult.created_by,
                    groovyRowResult.has_attachment
            ]
            lstWrappedBugs << obj
            counter++
        }
        return lstWrappedBugs
    }

    /**
     * Get list of bug and count
     * @return -a map containing list of bug and count
     */
    private LinkedHashMap listMyPtBug() {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity statusClosed = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.CLOSED_RESERVED_ID, companyId)

        String queryStr = """
            SELECT bug.id, bug.title, bug.step_to_reproduce, status.key AS status, severity.key AS severity, type.key AS type,
                bug.created_on, created_by.username AS created_by, bug.has_Attachment
            FROM pt_bug bug
            LEFT JOIN system_entity status ON status.id = bug.status
            LEFT JOIN system_entity severity ON severity.id = bug.severity
            LEFT JOIN system_entity type ON type.id = bug.type
            LEFT JOIN app_user created_by ON created_by.id = bug.created_by
            WHERE bug.owner_id = :ownerId
            AND bug.company_id = :companyId
            AND bug.status <> :closedStatusId
            LIMIT :resultPerPage OFFSET :start
        """

        String countQuery = """
            SELECT COUNT(bug.id)
            FROM pt_bug bug
            WHERE bug.owner_id = :ownerId
            AND bug.company_id = :companyId
            AND bug.status <> :closedStatusId
        """

        Map queryParams = [
                ownerId: ptSessionUtil.appSessionUtil.getAppUser().id,
                companyId: companyId,
                closedStatusId: statusClosed.id,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> lstBug = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(countQuery, queryParams)
        int count = countResults[0].count
        return [lstBug: lstBug, count: count]
    }
}
