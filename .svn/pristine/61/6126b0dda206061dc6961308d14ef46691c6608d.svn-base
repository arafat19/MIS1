package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show the list of open backlog for grid
 *  For details go through Use-Case doc named 'ShowOpenBacklogReportActionService'
 */
class ShowOpenBacklogReportActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Failed to generate open backlog report";
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_BACKLOG = "lstBacklog"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get ptBacklog list for grid where PtSprint.id=0
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            initPager(params)
            Map serviceResult = ptBacklogService.showAllOpenBacklog(this)
            List<PtBacklog> lstOpenBacklog = (List<PtBacklog>) serviceResult.lstOpenBacklog
            Integer count = (Integer) serviceResult.count
            result.put(LST_BACKLOG, lstOpenBacklog)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap ptBacklog list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<PtBacklog> lstOpenBacklog = (List<PtBacklog>) executeResult.get(LST_BACKLOG)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List wrappedOpenBacklog = wrapOpenBacklogList(lstOpenBacklog, this.start)
            Map gridObj = [page: pageNumber, total: count, rows: wrappedOpenBacklog]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptBacklog in grid entity
     * @param lstPtBacklog -list of ptBacklog object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ptBacklogs
     */
    private List wrapOpenBacklogList(List<PtBacklog> lstBacklog, int start) {
        List backlogList = []
        int counter = start + 1
        for (int i = 0; i < lstBacklog.size(); i++) {
            PtBacklog backlog = lstBacklog[i]
            SystemEntity priority = (SystemEntity) ptBacklogPriorityCacheUtility.read(backlog.priorityId)
            GridEntity obj = new GridEntity()    // build grid object
            obj.id = backlog.id
            obj.cell = [
                    counter,
                    backlog.idea,
                    priority.key
            ]
            backlogList << obj
            counter++
        }
        return backlogList;
    }

}
