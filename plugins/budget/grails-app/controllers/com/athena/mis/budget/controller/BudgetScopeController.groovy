package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.budgetscope.*
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired

class BudgetScopeController {
    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]

    @Autowired
    BudgSessionUtil budgSessionUtil

    ShowBudgetScopeActionService showBudgetScopeActionService
    CreateBudgetScopeActionService createBudgetScopeActionService
    ListBudgetScopeActionService listBudgetScopeActionService
    SearchBudgetScopeActionService searchBudgetScopeActionService
    SelectBudgetScopeActionService selectBudgetScopeActionService
    UpdateBudgetScopeActionService updateBudgetScopeActionService
    DeleteBudgetScopeActionService deleteBudgetScopeActionService

    /**
     * Show budget scope
     */
    def show() {
        Map preResult = (Map) showBudgetScopeActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showBudgetScopeActionService.execute(params, null)
        Map result;
        if (executeResult) {
            result = (Map) showBudgetScopeActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showBudgetScopeActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/budget/budgetScope/show', model: [output: result as JSON])

    }

    /**
     * Create budget scope
     */
    def create() {
        Map executeResult

        BudgBudgetScope budgetScope = new BudgBudgetScope(params)
        Map preResult = (Map) createBudgetScopeActionService.executePreCondition(null, budgetScope)
        Boolean hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result;
        Boolean isValid = (Boolean) preResult
        if (!isValid.booleanValue()) {
            result = (Map) createBudgetScopeActionService.formatValidationErrorsForUI(budgetScope, { g.message(error: it) }, false)
            render(result as JSON)
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgetScopeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) createBudgetScopeActionService.execute(null, budgetScope)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgetScopeActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createBudgetScopeActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    /**
     * Delete budget scope
     */
    def delete() {
        Map result
        Map preResult = (Map) deleteBudgetScopeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgetScopeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Boolean deleteResult = ((Boolean) deleteBudgetScopeActionService.execute(params, null))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteBudgetScopeActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteBudgetScopeActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    /**
     * List and search of budget scope
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        if (params.query) {
            preResult = (Map) searchBudgetScopeActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchBudgetScopeActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchBudgetScopeActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchBudgetScopeActionService.buildFailureResultForUI(null)
            }

        } else {
            preResult = (Map) listBudgetScopeActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listBudgetScopeActionService.execute(params, null)
            if (executeResult) {
                result = (Map) listBudgetScopeActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listBudgetScopeActionService.buildFailureResultForUI(null)
            }
        }
        render(result as JSON)
    }

    /**
     * Select budget scope
     */
    def select() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        String output
        Boolean isError
        preResult = (LinkedHashMap) selectBudgetScopeActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        executeResult = (LinkedHashMap) selectBudgetScopeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectBudgetScopeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectBudgetScopeActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON

    }

    /**
     * Update budget scope
     */
    def update() {
        BudgBudgetScope budgetScopeInstance = new BudgBudgetScope(params)
        budgetScopeInstance.id = Long.parseLong(params.id.toString())
        budgetScopeInstance.version = Integer.parseInt(params.version.toString())
        budgetScopeInstance.companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        Map preResult = (Map) updateBudgetScopeActionService.executePreCondition(null, budgetScopeInstance)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result;
        Boolean isValid = (Boolean) preResult
        if (!isValid.booleanValue()) {
            result = (Map) updateBudgetScopeActionService.formatValidationErrorsForUI(budgetScopeInstance, { g.message(error: it) }, false)
            render(result as JSON)
            return;
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgetScopeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        // pre-condition met, so we'll run the execute method of the action
        Integer updateCount = (Integer) updateBudgetScopeActionService.execute(null, budgetScopeInstance)
        if (updateCount.intValue() > 0) {
            result = (Map) updateBudgetScopeActionService.buildSuccessResultForUI(budgetScopeInstance)
        } else {
            result = (Map) updateBudgetScopeActionService.buildFailureResultForUI(null)
        }
        String output = result as JSON
        render output
    }

}
