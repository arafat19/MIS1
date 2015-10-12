package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of ptBug
 *  For details go through Use-Case doc named 'SearchPtBugActionService'
 */
class SearchPtBugActionService extends BaseService implements ActionIntf {

    PtBugService ptBugService
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load bug List"
    private static final String LST_BUGS = "lstBugs"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get ptBug list through specific search by backlogId and companyId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initSearch(parameters)
            long backlogId = Long.parseLong(parameterMap.backlogId.toString())
            List<GroovyRowResult> lstBugs = search(this, backlogId)          // Search ptBug from DB
            int count = ptBugService.searchCount(this, backlogId)              // get count for specific search
            result.put(LST_BUGS, lstBugs)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap ptBug list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstBugs = (List<GroovyRowResult>) executeResult.get(LST_BUGS)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedBugs = wrapBugs(lstBugs, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedBugs]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptBug in grid entity
     * @param lstBugs -list of ptBug object
     * @param start -starting index of the page
     * @return -list of wrapped ptBugs
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
                    groovyRowResult.has_attachment? Tools.YES : Tools.NO
            ]
            lstWrappedBugs << obj
            counter++
        }
        return lstWrappedBugs
    }

    /**
     * Get ptBug list through specific search by backlogId and companyId
     * @param baseService - BaseService object
     * @return - ptBug list
     */
    private List<GroovyRowResult> search(BaseService baseService, long backlogId) {
        String searchQuery =
                """
            SELECT bug.id, bug.version, bug.title title, bug.step_to_reproduce step_to_reproduce, status.key status, severity.key severity,
                         type.key as type, bug.created_on created_on, au.username created_by, bug.has_attachment has_attachment
            FROM pt_bug bug
                LEFT JOIN app_user au ON bug.created_by = au.id
                LEFT JOIN system_entity status ON bug.status = status.id
                LEFT JOIN system_entity severity ON bug.severity = severity.id
                LEFT JOIN system_entity type ON bug.type = type.id
            WHERE bug.company_id = :companyId
            AND bug.backlog_id = :backlogId
            AND ${baseService.queryType} ilike :query
            ORDER BY title
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                companyId: ptSessionUtil.appSessionUtil.getCompanyId(),
                backlogId: backlogId,
                query: Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE,
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstBugs = executeSelectSql(searchQuery, queryParams)
        return lstBugs
    }


}
