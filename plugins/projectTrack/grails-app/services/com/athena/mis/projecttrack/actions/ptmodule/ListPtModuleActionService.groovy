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
 *  Show list of Module for grid
 *  For details go through Use-Case doc named 'ListPtModuleActionService'
 */
class ListPtModuleActionService extends BaseService implements ActionIntf{

    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility

	private final Logger log = Logger.getLogger(getClass())

    private static final String SHOW_MODULE_FAILURE_MESSAGE = "Failed to load module page"
    private static final String LST_MODULE = "lstModule"

    /**
     * Do nothing for list operation
     */
	public Object executePreCondition(Object params, Object obj) {

        return null
	}

    /**
     * Get Module list and count for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
	public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)                                     // initialize parameters for flexGrid
            List<PtModule> lstModule = (List<PtModule>) ptModuleCacheUtility.list(this)
            Integer count = (Integer) ptModuleCacheUtility.count()
            result.put(LST_MODULE, lstModule)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_MODULE_FAILURE_MESSAGE)
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
     * wrap Module list for grid
     * @param obj- return from execute method
     * @return- map containing all objects to indicate success event
     */
	public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj    // cast map returned from execute method
            List<PtModule> lstModule = (List<PtModule>) executeResult.get(LST_MODULE)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedModule = wrapModule(lstModule, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedModule]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_MODULE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SHOW_MODULE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SHOW_MODULE_FAILURE_MESSAGE)
            return result
        }
	}

    /**
     * Wrap list of Module in grid entity
     * @param lstModule -list of Module object
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
