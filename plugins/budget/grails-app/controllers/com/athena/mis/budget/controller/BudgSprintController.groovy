package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.budgsprint.*
import grails.converters.JSON

class BudgSprintController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", list: "POST", delete: "POST", select: "POST",
            setCurrentBudgSprint: "POST", showForCurrentSprint: "POST", listForCurrentSprint: "POST"]

    ShowBudgSprintActionService showBudgSprintActionService
    CreateBudgSprintActionService createBudgSprintActionService
    SearchBudgSprintActionService searchBudgSprintActionService
    ListBudgSprintActionService listBudgSprintActionService
    SelectBudgSprintActionService selectBudgSprintActionService
    UpdateBudgSprintActionService updateBudgSprintActionService
    DeleteBudgSprintActionService deleteBudgSprintActionService
    SetCurrentBudgSprintActionService setCurrentBudgSprintActionService
    ShowForCurrentSprintActionService showForCurrentSprintActionService
    ListForCurrentSprintActionService listForCurrentSprintActionService

    /**
     * Set Current sprint
     */
    def setCurrentBudgSprint() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        preResult = (Map) setCurrentBudgSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) setCurrentBudgSprintActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) setCurrentBudgSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) setCurrentBudgSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) setCurrentBudgSprintActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Show sprint
     */
    def show() {
        Map result

        Map executeResult = (Map) showBudgSprintActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showBudgSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showBudgSprintActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render(view: '/budget/budgSprint/show', model: [output: output])
    }

    /**
     * Create sprint
     */
    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createBudgSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createBudgSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createBudgSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * List and search sprint
     */
    def list() {
        Map executeResult
        Map result
        Boolean isError

        if (params.query) {
            executeResult = (Map) searchBudgSprintActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgSprintActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchBudgSprintActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listBudgSprintActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listBudgSprintActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listBudgSprintActionService.buildSuccessResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    /**
     * Select sprint
     */
    def select() {
        Map result

        Map executeResult = (Map) selectBudgSprintActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectBudgSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) selectBudgSprintActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * Update sprint
     */
    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updateBudgSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateBudgSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateBudgSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Delete sprint
     */
    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteBudgSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteBudgSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteBudgSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Show for current sprint
     */
    def showForCurrentSprint() {
        Map result

        Map executeResult = (Map) showForCurrentSprintActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showForCurrentSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showForCurrentSprintActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render(view: '/budget/budgSprint/showCurrentSprint', model: [output: output])
    }

    /**
     * List for current sprint
     */
    def listForCurrentSprint() {
        Map result

        Map executeResult = (Map) listForCurrentSprintActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listForCurrentSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listForCurrentSprintActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }
}
