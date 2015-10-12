package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.project.*
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ProjectController {
    static allowedMethods = [show: "POST", edit: "POST", create: "POST", update: "POST",
            delete: "POST", list: "POST"];

    ShowProjectActionService showProjectActionService
    CreateProjectActionService createProjectActionService
    SelectProjectActionService selectProjectActionService
    UpdateProjectActionService updateProjectActionService
    DeleteProjectActionService deleteProjectActionService
    SearchProjectActionService searchProjectActionService
    ListProjectActionService listProjectActionService
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * Show project
     */
    def show() {
        Map preResult = (Map) showProjectActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        render(view: '/application/project/show', model: [output: preResult as JSON])
    }
    /**
     * Create new project
     */
    def create() {
        Map preResult
        Map result
        Map executeResult

        preResult = (Map) createProjectActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createProjectActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Project projectInstance = buildProject(params)
        Boolean isValid = (Boolean) preResult.isValid
        if (!isValid.booleanValue()) {
            result = (Map) createProjectActionService.formatValidationErrorsForUI(projectInstance, {
                g.message(error: it)
            }, false)
            render(result as JSON)
            return;
        }

        executeResult = (Map) createProjectActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createProjectActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createProjectActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     * Select project
     */
    def select() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) selectProjectActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        executeResult = (LinkedHashMap) selectProjectActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectProjectActionService.buildFailureResultForUI(executeResult)

        } else {
            result = (LinkedHashMap) selectProjectActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }
    /**
     * Update project
     */
    def update() {
        Map preResult
        Map result
        Map executeResult

        preResult = (Map) updateProjectActionService.executePreCondition(null, params)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateProjectActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Project projectInstance = buildProject(params)
        projectInstance.updatedOn = new Date()
        projectInstance.updatedBy = appSessionUtil.getAppUser().id
        projectInstance.id = Long.parseLong(params.id.toString())
        projectInstance.version = Integer.parseInt(params.version.toString())
        Boolean isValid = (Boolean) preResult.isValid
        if (!isValid.booleanValue()) {
            result = (Map) updateProjectActionService.formatValidationErrorsForUI(projectInstance, {
                g.message(error: it)
            }, false)
            render(result as JSON)
            return;
        }

        // pre-condition met, so we'll run the execute method of the action
        executeResult = (Map) updateProjectActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateProjectActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateProjectActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     * Delete project
     */
    def delete() {
        Map result
        Map preResult = (Map) deleteProjectActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteProjectActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        boolean deleteResult = ((Boolean) deleteProjectActionService.execute(params, null)).booleanValue();
        if (deleteResult.booleanValue()) {
            result = (Map) deleteProjectActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteProjectActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }
    /**
     * List & Search project
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult

        if (params.get(BaseService.QUERY_COLUMN_KENDO)) {
            preResult = (Map) searchProjectActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchProjectActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchProjectActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchProjectActionService.buildFailureResultForUI(null)
            }

        } else {
            preResult = (Map) listProjectActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listProjectActionService.execute(params, null)
            if (executeResult) {
                result = (Map) listProjectActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listProjectActionService.buildFailureResultForUI(null)
            }
        }
        render(result as JSON)
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
        return project
    }
}

