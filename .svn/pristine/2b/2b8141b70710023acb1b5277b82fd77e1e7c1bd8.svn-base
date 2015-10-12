package com.athena.mis.projecttrack.actions.ptbug

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.projecttrack.entity.PtBug
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtBugService
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogPriorityCacheUtility
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class SearchPtBugForSprintActionService extends BaseService implements ActionIntf {

    PtSprintService ptSprintService
    PtBugService ptBugService
    SystemEntityService systemEntityService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtBacklogPriorityCacheUtility ptBacklogPriorityCacheUtility

    private final Logger log = Logger.getLogger(getClass());

    private static final String SHOW_FAILURE_MESSAGE = "Failed to load bug information page"
    private static final String LST_BUG = "lstBug"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for pre condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get sprint-bug mapped list through specific search by sprintId
     * @param parameter - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameter, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameter
            initSearch(params)
            long sprintId = Long.parseLong(params.sprintId.toString())
            Map serviceReturn = ptBugService.searchBugForSprint(this, sprintId)
            List<PtBug> bugList = (List<PtBug>) serviceReturn.lstPtBug
            Integer count = (Integer) serviceReturn.count
            result.put(LST_BUG, bugList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap Bug list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List<PtBug> lstBugs = (List<PtBug>) executeResult.get(LST_BUG)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List bugsList = wrapBugs(lstBugs, start)                    // warp Bug list for grid
            Map gridOutput = [page: pageNumber, total: count, rows: bugsList]
            return gridOutput
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
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
            if (!obj) {
                result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
                return result
            }
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
            result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ptBug in grid entity
     * @param lstBug - list of sprint-bug mapped objects
     * @param start - starting index of the page
     * @return - list of wrapped Bugs
     */
    private List wrapBugs(List<PtBug> lstBug, int start) {
        List lstWrappedBugs = []
        int counter = start + 1
        for (int i = 0; i < lstBug.size(); i++) {
            PtBug ptBug = lstBug[i]
            PtModule module = (PtModule) ptModuleCacheUtility.read(ptBug.moduleId)
            SystemEntity bugSeverity = systemEntityService.read(ptBug.severity)
            SystemEntity bugType = systemEntityService.read(ptBug.type)
            GridEntity obj = new GridEntity()
            obj.id = ptBug.id
            obj.cell = [
                    counter,
                    ptBug.id,
                    module.name,
                    ptBug.title,
                    bugSeverity.key,
                    bugType.key
            ]
            lstWrappedBugs << obj
            counter++
        }
        return lstWrappedBugs
    }
}
