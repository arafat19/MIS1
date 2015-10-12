package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of open ptBacklog for grid
 *  For details go through Use-Case doc named 'ListOpenBacklogReportActionService'
 */
class ListOpenBacklogReportActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    PtProjectModuleService ptProjectModuleService
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_BACKLOG_FAILURE_MESSAGE = "Failed to load backlog page"
    private static final String LST_BACKLOG = "lstPtBacklog"
    private static final String GRID_OBJ = "gridObj"
    private static final String NO_MODULE = "No module found associated this project"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get ptBacklog list and count for grid where PtSprint.id=0
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        Map serviceResult
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid
            long moduleId
            long projectId = Long.parseLong(parameterMap.projectId)
            if (parameterMap.moduleId.equals(Tools.EMPTY_SPACE)) {
                moduleId = 0L
            } else {
                moduleId = Long.parseLong(parameterMap.moduleId)
            }
            List<Long> lstModuleIds = []
            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            if (moduleId <= 0) {
                List<PtProjectModule> lstModule = ptProjectModuleService.findByProjectIdAndCompanyId(projectId, companyId)
                if (lstModule.size() == 0) {
                    result.put(Tools.MESSAGE, NO_MODULE)
                    return result
                }
                for (int i = 0; i < lstModule.size(); i++) {
                    lstModuleIds << lstModule[i].moduleId
                }
            } else {
                lstModuleIds << new Long(moduleId)
            }
            String moduleIds = Tools.buildCommaSeparatedStringOfIds(lstModuleIds)

            serviceResult = showAllOpenBacklog(moduleIds)
            List<GroovyRowResult> OpenBacklogList = serviceResult.lstOpenBacklog
            Integer count = (Integer) serviceResult.count
            result.put(LST_BACKLOG, OpenBacklogList)
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
     * Wrap ptBacklog list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<GroovyRowResult> lstPtBacklog = (List<GroovyRowResult>) executeResult.get(LST_BACKLOG)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedBacklogs = wrapBacklogs(lstPtBacklog, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedBacklogs]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, output)
            return result
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
     * @param lstPtBacklog -list of ptBacklog object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptBacklogs
     */
    private List wrapBacklogs(List<GroovyRowResult> lstBacklog, int start) {
        List backlogList = []
        int counter = start + 1
        GroovyRowResult backlog
        for (int i = 0; i < lstBacklog.size(); i++) {
            backlog = lstBacklog[i]
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = backlog.id
            backlogList << obj
            obj.cell = [
                    counter,
                    backlog.idea,
                    backlog.key
            ]
            counter++
        }
        return backlogList;
    }

    private Map showAllOpenBacklog(String moduleIds) {
        String SELECT_BACKLOG_QUERY = """
            SELECT backlog.id AS id, backlog.module_id module_id, module.name as module_name,
                   backlog.idea AS idea, se.key AS key
            FROM pt_backlog backlog
                LEFT JOIN pt_module module ON backlog.module_id = module.id
                LEFT JOIN system_entity se ON backlog.priority_id = se.id
            WHERE sprint_id <= 0
                AND backlog.module_id IN(${moduleIds})
            ORDER BY module_name
            LIMIT :resultPerPage OFFSET :start
        """
        String COUNT_QUERY = """
            SELECT COUNT(backlog.id)
            FROM pt_backlog backlog
            WHERE sprint_id <= 0
                AND backlog.module_id IN(${moduleIds})
        """
        Map params = [
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstOpenBacklog = executeSelectSql(SELECT_BACKLOG_QUERY, params)
        int count = executeSelectSql(COUNT_QUERY).first().count
        Map result = [lstOpenBacklog: lstOpenBacklog, count: count.toInteger()]
        return result
    }


}
