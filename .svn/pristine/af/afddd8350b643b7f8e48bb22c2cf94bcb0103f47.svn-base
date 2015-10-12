package com.athena.mis.projecttrack.actions.ptprojectmodule

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectModuleCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new PtProjectModule object and show in grid
 *  For details go through Use-Case doc named 'CreatePtProjectModuleActionService'
 */
class CreatePtProjectModuleActionService extends BaseService implements ActionIntf {

    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtProjectModuleCacheUtility ptProjectModuleCacheUtility
    @Autowired
    PtModuleCacheUtility ptModuleCacheUtility
    PtProjectModuleService ptProjectModuleService

    private final Logger log = Logger.getLogger(getClass())

    private static final String PROJECT_MODULE_CREATE_SUCCESS_MSG = "Project Module has been successfully saved"
    private static final String PROJECT_MODULE_CREATE_FAILURE_MSG = "Project Module has not been saved"
    private static final String ALREADY_EXIST = "This mapping already exists"
    private static final String PROJECT_MODULE = "Project module"
    /**
     * Get parameters from UI and build PtProjectModule object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            PtProjectModule projectModule = buildProjectModuleObject(parameterMap)   // build module object
            int count = ptProjectModuleService.countByProjectIdAndModuleIdAndCompanyId(projectModule) //If the object does not exist, count will be zero
            //check whether the object already exists or not
            if (count > 0) {
                result.put(Tools.MESSAGE, ALREADY_EXIST)
                return result
            }
            result.put(PROJECT_MODULE, projectModule)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_CREATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Save ptProjectModule object in DB and cacheUtility
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
            PtProjectModule projectModule = (PtProjectModule) preResult.get(PROJECT_MODULE)
            PtProjectModule savedProjectModuleObj = ptProjectModuleService.create(projectModule)
            // save new projectModule object in DB
            ptProjectModuleCacheUtility.add(projectModule, ptProjectModuleCacheUtility.SORT_ON_PROJECT_ID, ptProjectModuleCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROJECT_MODULE, savedProjectModuleObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_CREATE_FAILURE_MSG)
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
     * Show newly created ptProjectModule object in grid
     * 1.Get ptProject name by id(projectId)
     * 2.Get ptModule name by id(moduleId)
     * @param obj - map from execute method
     * @return - a map containing all objects to indicate a success event
     * map containing (true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            PtProjectModule projectModule = (PtProjectModule) executeResult.get(PROJECT_MODULE)
            GridEntity object = new GridEntity()                //build grid object
            PtModule module = (PtModule) ptModuleCacheUtility.read(projectModule.moduleId)
            object.id = projectModule.id
            object.cell = [
                    Tools.LABEL_NEW,
                    module.name
            ]
            result.put(Tools.MESSAGE, PROJECT_MODULE_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, PROJECT_MODULE_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_MODULE_CREATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Build ptProjectModule object
     * @param parameterMap -serialized parameters from UI
     * @return -new projectModule object
     */
    private PtProjectModule buildProjectModuleObject(GrailsParameterMap parameterMap) {
        PtProjectModule projectModule = new PtProjectModule(parameterMap)
        projectModule.companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        return projectModule
    }
}
