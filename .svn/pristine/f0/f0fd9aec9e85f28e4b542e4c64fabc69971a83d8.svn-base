package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.projectbudgetscope.GetProjectBudgetScopeActionService
import com.athena.mis.budget.actions.projectbudgetscope.SelectProjectBudgetScopeActionService
import com.athena.mis.budget.actions.projectbudgetscope.UpdateProjectBudgetScopeActionService
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ProjectBudgetScopeController {

    static allowedMethods = [show: "POST", update: "POST", select: "POST", getBudgetScope: "POST"];

    SelectProjectBudgetScopeActionService selectProjectBudgetScopeActionService
    UpdateProjectBudgetScopeActionService updateProjectBudgetScopeActionService
    GetProjectBudgetScopeActionService getProjectBudgetScopeActionService

    /**
     * Show Project budget scope
     */
    def show() {
        render(view: '/budget/projectBudgetScope/show', model: [modelJoson: null])
    }

    /**
     * Select Project budget scope
     */
    def select() {
        Map preResult = (Map) selectProjectBudgetScopeActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Map result
        Map executeResult = (Map) selectProjectBudgetScopeActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectProjectBudgetScopeActionService.buildFailureResultForUI(null);
        } else {
            result = (Map) selectProjectBudgetScopeActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    /**
     * Update project budget scope
     */
    def update() {
        Map preResult = (Map) updateProjectBudgetScopeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result;
        Boolean isError = (Boolean) preResult.isError;
        if (isError.booleanValue()) {
            result = (Map) updateProjectBudgetScopeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        Boolean success = (Boolean) updateProjectBudgetScopeActionService.execute(params, preResult)

        if (success.booleanValue()) {
            result = (Map) updateProjectBudgetScopeActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) updateProjectBudgetScopeActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    /**
     * Get budget scope
     */
    def getBudgetScope() {
        Map result
        Map executeResult = (Map) getProjectBudgetScopeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getProjectBudgetScopeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getProjectBudgetScopeActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }
}
