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
 *  Create new project object and show in grid
 *  For details go through Use-Case doc named 'CreateProjectActionService'
 */
class CreateProjectActionService extends BaseService implements ActionIntf {

    private static final String PROJECT = "project"
    private static final String PROJECT_SAVE_FAILURE_MESSAGE = "Project could not be saved"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same project code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same project name already exists"
    private static final String INVALID_INPUT_MSG = "Failed to create project due to invalid input"
    private static final String PROJECT_SAVE_SUCCESS_MESSAGE = "Project has been saved successfully"

    private final Logger log = Logger.getLogger(getClass())

    ProjectService projectService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. receive project object
     * 2. check user access as Admin role
     * 3. duplicate check for project-code
     * 4. duplicate check for project-name
     * 5. check input validation
     * @param parameterMap - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(true/false) depending on method success &  relevant message.
     */
    public Object executePreCondition(Object parameterMap, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.IS_VALID, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameterMap
            //Check parameters
            if ((!params.name) || (!params.startDate) || (!params.endDate) || (!params.code)) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            Project projectInstance = buildProject(params)

            // check project code
            int duplicateCount = projectCacheUtility.countByCode(projectInstance.code)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, PROJECT_CODE_ALREADY_EXISTS)
                return result
            }
            //check duplicate project Name
            int duplicateName = projectCacheUtility.countByName(projectInstance.name)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, PROJECT_NAME_ALREADY_EXISTS)
                return result
            }

            /* if (projectInstance.hasErrors()) {
                 result.put(Tools.IS_VALID, Boolean.FALSE)
                 return result
             }*/
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
     * 1. receive project object from controller
     * 2. create new project
     * 3. add new project to corresponding cache utility & sort cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - receive project object from executePreCondition
     * @return - newly built project object.
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Project projectInstance = (Project) preResult.get(PROJECT)
            Project newProject = projectService.create(projectInstance)  // save new project object in DB
            // add new project object in cache utility and keep the data sorted
            projectCacheUtility.add(newProject, projectCacheUtility.SORT_ON_NAME, projectCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PROJECT, newProject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(PROJECT_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * 1. Show newly created project object in grid
     * 2. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
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
            result.put(Tools.MESSAGE, PROJECT_SAVE_SUCCESS_MESSAGE)
            result.put(PROJECT, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_SAVE_FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj  // // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PROJECT_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PROJECT_SAVE_FAILURE_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Get project object
     * @param parameterMap - serialize parameters from UI
     * @return - project object
     */
    private Project buildProject(GrailsParameterMap parameterMap) {
        Project project = new Project(parameterMap)
        AppUser user = appSessionUtil.getAppUser()
        project.createdOn = new Date()
        project.createdBy = user.id
        project.companyId = user.companyId
        project.updatedBy = 0
        project.updatedOn = null
        project.contentCount = 0
        project.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        project.endDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        return project
    }
}
