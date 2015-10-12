package com.athena.mis.projecttrack.actions.ptmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.service.PtModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new Module object and show in grid
 *  For details go through Use-Case doc named 'CreatePtModuleActionService'
 */

class CreatePtModuleActionService extends BaseService implements ActionIntf {

    PtModuleService ptModuleService
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private final static String NAME_EXIST_MESSAGE = "Same module name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same module code already exists"
    private static final String MODULE_CREATE_SUCCESS_MSG = "Module has been successfully saved"
    private static final String MODULE_CREATE_FAILURE_MSG = "Module has not been saved"
    private static final String MODULE = "module"

    /**
     * Get parameters from UI and build Module object
     * 1. Check existence of same name
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            PtModule module = buildModuleObject(parameterMap)   // build module object
            int count = ptModuleCacheUtility.countByNameIlike(module.name)
            if (count > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = ptModuleCacheUtility.countByCode(module.code)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }
            result.put(MODULE, module)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save Module object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            PtModule module = (PtModule) preResult.get(MODULE)
            PtModule savedModuleObj = ptModuleService.create(module)  // save new module object in DB
            ptModuleCacheUtility.add(savedModuleObj, ptModuleCacheUtility.SORT_ON_NAME, ptModuleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(MODULE, savedModuleObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_CREATE_FAILURE_MSG)
            return result
        }

    }

    /**
     * Do nothing for postCondition
     */
    public Object executePostCondition(Object params, Object obj) {
        return null
    }
    
    /**
     * Show newly created Module object in grid
     * Show success message
     * @param obj - map from execute method
     * @return - a map containing all objects necessary for grid view
     * map containing (true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtModule module = (PtModule) executeResult.get(MODULE)
            GridEntity object = new GridEntity()                //build grid object
            object.id = module.id
            object.cell = [
                    Tools.LABEL_NEW,
                    module.name,
                    module.code
            ]
            result.put(Tools.MESSAGE, MODULE_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, MODULE_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MODULE_CREATE_FAILURE_MSG)
            return result
        }

    }

    /**
     * Build Module object
     * @param parameterMap -serialized parameters from UI
     * @return -new module object
     */
    private PtModule buildModuleObject(GrailsParameterMap parameterMap) {
        PtModule module = new PtModule(parameterMap)
        module.version = 0
        module.companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        return module

    }


}
