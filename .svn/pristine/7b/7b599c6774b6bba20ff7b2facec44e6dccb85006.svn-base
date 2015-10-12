package com.athena.mis.projecttrack.actions.ptmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get list of Module
 *  For details go through Use-Case doc named 'ShowPtModuleActionService'
 */
class ShowPtModuleActionService extends BaseService implements ActionIntf {

    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG_SHOW_MODULE = "Failed to load module page"
    private static final String GRID_OBJ = "gridObj"
    private static final String LST_MODULE = "lstModule"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }

    /**
     * Get Module list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)                                 // initialize parameters for flexGrid
            Integer count = (Integer) ptModuleCacheUtility.count()
            List<PtModule> lstModule = (List<PtModule>) ptModuleCacheUtility.list(this)
            result.put(LST_MODULE, lstModule)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_MODULE)
            return result
        }
    }
    /**
     * Do nothing for post condition
     */

    public Object executePostCondition(Object params, Object obj) {
        return null
    }

    /**
     * Wrap Module list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj                        // cast map returned from execute method
            List<PtModule> lstModule = (List<PtModule>) executeResult.get(LST_MODULE)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedModule = wrapModule(lstModule, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedModule]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_MODULE)
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
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_MODULE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG_SHOW_MODULE)
            return result
        }
    }

    /**
     * Wrap list of Module in grid entity
     * @param lstModule -list of Module object(s)
     * @param start -starting index of the page
     * @return -list of wrapped Module
     */
    private List wrapModule(List<PtModule> lstModule, int start) {
        List lstWrappedModule = []
        int counter = start + 1
        for (int i = 0; i < lstModule.size(); i++) {
            PtModule module = lstModule[i]
            GridEntity obj = new GridEntity()
            obj.id = module.id
            obj.cell = [
                    counter,
                    module.name,
                    module.code
            ]
            lstWrappedModule << obj
            counter++
        }
        return lstWrappedModule
    }

}
