package com.athena.mis.projecttrack.actions.ptproject

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.service.PtProjectService
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new Project object
 *  For details go through Use-Case doc named 'CreatePtProjectActionService'
 */
class CreatePtProjectActionService extends BaseService implements ActionIntf {

    PtProjectService ptProjectService
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private final static String NAME_EXIST_MESSAGE = "Same project name already exists"
    private final static String CODE_EXIST_MESSAGE = "Same project code already exists"
    private static final String PROJECT = "project"
    private static final String PROJECT_CREATE_FAILURE_MSG = "Project has not been saved"
    private static final String PROJECT_CREATE_SUCCESS_MSG = "Project has been successfully saved"

    /**
     * Get parameters from UI and build Project object
     * 1. Check existence of name
     * 2. Check existence of code
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            PtProject project = buildProjectObject(parameterMap)        // build ptProject object
            int countName = ptProjectCacheUtility.countByNameIlike(project.name)
            if (countName > 0) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            int countCode = ptProjectCacheUtility.countByCode(project.code)
            if (countCode > 0) {
                result.put(Tools.MESSAGE, CODE_EXIST_MESSAGE)
                return result
            }
            result.put(PROJECT, project)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_CREATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * Do nothing for post operation
     * @return
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Save Project object in DB and cache utility
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            PtProject project = (PtProject) preResult.get(PROJECT)
            PtProject savedProjectObj = ptProjectService.create(project)    // save new Project object in DB
            // add new Project object in cache utility and keep the data sorted
            ptProjectCacheUtility.add(savedProjectObj, ptProjectCacheUtility.SORT_ON_NAME, ptProjectCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROJECT, savedProjectObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            PtProject project = (PtProject) executeResult.get(PROJECT)
            GridEntity object = new GridEntity()                            // build grid object
            object.id = project.id
            object.cell = [
                    Tools.LABEL_NEW,
                    project.id,
                    project.name,
                    project.code
            ]
            result.put(Tools.MESSAGE, PROJECT_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PROJECT_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new Project object
     * @param parameterMap -serialized parameters from UI
     * @return -new Project object
     */
    private PtProject buildProjectObject(GrailsParameterMap parameterMap) {
        PtProject project = new PtProject(parameterMap)
        project.companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        project.createdOn = new Date()
        project.createdBy = ptSessionUtil.appSessionUtil.getAppUser().id
        project.updatedOn = null
        project.updatedBy = 0
        return project
    }
}
