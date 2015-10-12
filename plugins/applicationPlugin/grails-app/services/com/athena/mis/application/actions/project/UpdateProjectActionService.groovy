package com.athena.mis.application.actions.project

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.ProjectService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update project object and grid data
 *  For details go through Use-Case doc named 'UpdateProjectActionService'
 */
class UpdateProjectActionService extends BaseService implements ActionIntf {

    private static final String PROJECT = "project"
    private static final String OBJ_NOT_FOUND = "Selected project not found"
    private static final String PROJECT_UPDATE_FAILURE_MESSAGE = "Project could not be updated"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same project code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same project name already exists"
    private static final String INVALID_INPUT_MSG = "Failed to create project due to invalid input"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "Project has been updated successfully"

    private final Logger log = Logger.getLogger(getClass())

    ProjectService projectService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. check user access as Admin role
     * 2. pull project object
     * 3. check project existence
     * 4. duplicate check for project-code
     * 5. duplicate check for project-name
     * 6. check input validation
     * @param parameters -N/A
     * @param obj - receive project object from controller
     * @return - a map containing isError(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.IS_VALID, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) obj

            //Check parameters
            if ((!params.name) || (!params.startDate) || (!params.endDate) || (!params.code)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            // check if project object exist
            long projectId = Long.parseLong(params.id.toString())
            Project existingProject = (Project) projectCacheUtility.read(projectId)
            if (!existingProject) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            Project projectInstance = buildProject(params, existingProject)

            // check project code
            int duplicateCount = projectCacheUtility.countByCodeAndIdNotEqual(projectInstance.code, projectInstance.id)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, PROJECT_CODE_ALREADY_EXISTS)
                return result
            }
            // check Unique project Name
            int duplicateName = projectCacheUtility.countByNameAndIdNotEqual(projectInstance.name, projectInstance.id)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, PROJECT_NAME_ALREADY_EXISTS)
                return result
            }

            result.put(PROJECT, projectInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            return result
        }
    }
    /**
     * 1. receive project object from pre execute method
     * 2. Update new project
     * 3. update new project to corresponding cache utility & sort cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - receive project object from pre execute method
     * @return - Integer value(e.g- 1 for success & 0 for failure)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Project projectInstance = (Project) preResult.get(PROJECT)
            projectService.update(projectInstance)
            projectInstance.version = projectInstance.version + 1
            projectCacheUtility.update(projectInstance, projectCacheUtility.SORT_ON_NAME, projectCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROJECT, projectInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(PROJECT_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap list of projects in grid entity
     * @param obj -project object
     * @return -list of wrapped project
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            Project project = (Project) receiveResult.get(PROJECT)
            Map gridObj = [
                    id: project.id,
                    name: project.name,
                    code: project.code,
                    description: project.description,
                    contentCount: project.contentCount,
                    createdOn: DateUtility.formatDateTimeLong(project.createdOn)
            ]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_SUCCESS_MESSAGE)
            result.put(PROJECT, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_UPDATE_FAILURE_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get project object
     * @param parameterMap - serialize parameters from UI
     * @return - project object
     */
    private Project buildProject(GrailsParameterMap parameterMap, Project oldProject) {
        Project newProject = new Project(parameterMap)
        oldProject.name = newProject.name
        oldProject.code = newProject.code
        oldProject.description = newProject.description
        oldProject.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        oldProject.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
        AppUser systemUser = appSessionUtil.getAppUser()
        oldProject.updatedOn = new Date()
        oldProject.updatedBy = systemUser.id

        // auto approval flag holds previous state if user is not development user.
        if(appSessionUtil.getAppUser().isConfigManager){
            oldProject.isApproveConsumption = newProject.isApproveConsumption
            oldProject.isApproveProduction = newProject.isApproveProduction
            oldProject.isApproveInvOut = newProject.isApproveInvOut
            oldProject.isApproveInFromInventory = newProject.isApproveInFromInventory
            oldProject.isApproveInFromSupplier = newProject.isApproveInFromSupplier
        }
        return oldProject
    }
}
