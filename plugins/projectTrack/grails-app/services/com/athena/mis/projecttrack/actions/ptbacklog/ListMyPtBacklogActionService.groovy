package com.athena.mis.projecttrack.actions.ptbacklog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of ptBacklog for grid
 *  For details go through Use-Case doc named 'ListMyPtBacklogActionService'
 */
class ListMyPtBacklogActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_BACKLOG_FAILURE_MESSAGE = "Failed to load backlog page"
    private static final String LST_BACKLOG = "lstBacklog"
    private static final String SPAN_START = "<span style='color:red'>"
    private static final String SPAN_END = "</span>"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get ptBacklog list and count for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid

            Map serviceReturn = listMyPtBacklog()   //get map of sub list of ptBacklog & total count
            List<GroovyRowResult> lstBacklog = serviceReturn.backlogList   // get ptBacklog List from map
            Integer count = (Integer) serviceReturn.count                         // get total count from map
            result.put(LST_BACKLOG, lstBacklog)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Wrap ptBacklog list for grid in MyBacklog
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List lstBacklog = (List) executeResult.get(LST_BACKLOG)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedBacklogs = wrapBacklogs(lstBacklog, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedBacklogs]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_BACKLOG_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptBacklog in grid entity
     * @param lstBacklog -list of ptBacklog object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptBacklogs
     */
    private List wrapBacklogs(List<GroovyRowResult> lstBacklog, int start) {
        List lstWrappedBacklogs = []
        int counter = start + 1
        for (int i = 0; i < lstBacklog.size(); i++) {
            GroovyRowResult backlog = lstBacklog[i]
            GridEntity obj = new GridEntity()
            obj.id = backlog.id
            obj.cell = [
                    counter,
                    backlog.actor,
                    backlog.purpose,
                    backlog.benefit,
                    backlog.priority,
                    backlog.status,
                    backlog.hours,
                    backlog.pac_count,
                    backlog.bug_count,
                    SPAN_START + backlog.unresolved + SPAN_END
            ]
            lstWrappedBacklogs << obj
            counter++
        }
        return lstWrappedBacklogs
    }


    private static final String QUERY_FOR_COUNT = """
        SELECT COUNT(backlog.id) AS count
        FROM pt_backlog backlog
        WHERE  backlog.owner_id =:ownerId
        AND backlog.status_id !=:statusAccepted
        AND backlog.company_id =:companyId
    """
    /**
     * @return -a map contains list of backlog and count
     */
    private LinkedHashMap listMyPtBacklog() {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity statusAccepted = (SystemEntity) ptBacklogStatusCacheUtility.readByReservedAndCompany(ptBacklogStatusCacheUtility.ACCEPTED_RESERVED_ID, companyId)
        SystemEntity statusReopened = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.RE_OPENED_RESERVED_ID, companyId)
        SystemEntity statusSubmitted = (SystemEntity) ptBugStatusCacheUtility.readByReservedAndCompany(ptBugStatusCacheUtility.SUBMITTED_RESERVED_ID, companyId)

        String queryStr = """
            SELECT backlog.id,backlog.actor,backlog.benefit,backlog.hours,backlog.idea,backlog.purpose,
            se.key AS priority, sen.key AS status, COUNT(pac.id) AS pac_count,
            (SELECT COUNT(pbug.id) FROM pt_bug pbug WHERE pbug.backlog_id = backlog.id) AS bug_count,
            (SELECT COUNT(pbug.id) FROM pt_bug pbug WHERE pbug.status
                               IN (${statusReopened.id},${statusSubmitted.id})
            AND pbug.backlog_id = backlog.id) AS unresolved
            FROM pt_backlog backlog
            LEFT JOIN pt_acceptance_criteria pac ON pac.backlog_id = backlog.id
            LEFT JOIN system_entity se on se.id = backlog.priority_id
            LEFT JOIN system_entity sen on sen.id = backlog.status_id
            WHERE  backlog.owner_id =:ownerId
            AND backlog.company_id =:companyId
            AND backlog.status_id !=:statusAccepted
            GROUP BY backlog.actor,backlog.benefit,backlog.hours,backlog.idea,backlog.purpose,backlog.id, se.key,sen.key
            ORDER BY backlog.id
            LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                ownerId: ptSessionUtil.appSessionUtil.getAppUser().id,
                companyId: companyId,
                statusAccepted: statusAccepted.id,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> backlogList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = countResults[0].count
        return [backlogList: backlogList, count: count]
    }

}
