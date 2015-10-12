package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.budgtask.CreateBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.DeleteBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.ListBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.ListTaskForBudgetSprintActionService
import com.athena.mis.budget.actions.budgtask.SearchBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.SelectBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.ShowBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.ShowTaskForBudgetSprintActionService
import com.athena.mis.budget.actions.budgtask.UpdateBudgTaskActionService
import com.athena.mis.budget.actions.budgtask.UpdateTaskForBudgetSprintActionService
import grails.converters.JSON

class BudgTaskController {

    static allowedMethods = [
            show: "POST",
            create: "POST",
            list: "POST",
            select: 'POST',
            update: 'POST',
            delete: 'POST',
            showTaskForSprint: "POST",
            listTaskForSprint: "POST",
            updateTaskForSprint: "POST"
    ]

    ShowBudgTaskActionService showBudgTaskActionService
    CreateBudgTaskActionService createBudgTaskActionService
    ListBudgTaskActionService listBudgTaskActionService
    SearchBudgTaskActionService searchBudgTaskActionService
    SelectBudgTaskActionService selectBudgTaskActionService
    UpdateBudgTaskActionService updateBudgTaskActionService
    DeleteBudgTaskActionService deleteBudgTaskActionService
    ShowTaskForBudgetSprintActionService showTaskForBudgetSprintActionService
    ListTaskForBudgetSprintActionService listTaskForBudgetSprintActionService
    UpdateTaskForBudgetSprintActionService updateTaskForBudgetSprintActionService

    /**
     * show budget task list
     */
    def show() {
        LinkedHashMap preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (LinkedHashMap) showBudgTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showBudgTaskActionService.buildFailureResultForUI(preResult)
            render(view: '/budget/budgTask/show', model: [output: result as JSON])
            return;
        }

        executeResult = (Map) showBudgTaskActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showBudgTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showBudgTaskActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/budget/budgTask/show', model: [output: result as JSON])
    }

    /**
     * create budg task
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createBudgTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgTaskActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createBudgTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgTaskActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createBudgTaskActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    /**
     * Select budg task
     */
    def select() {
        LinkedHashMap preResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectBudgTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectBudgTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectBudgTaskActionService.buildSuccessResultForUI(preResult)
        output = result as JSON
        render output
    }

    /**
     * list budg task
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchBudgTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchBudgTaskActionService.buildSuccessResultForUI(executeResult)
            }
        } else {

            executeResult = (Map) listBudgTaskActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listBudgTaskActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listBudgTaskActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)

    }

    /**
     * update budg task
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateBudgTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgTaskActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateBudgTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgTaskActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateBudgTaskActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    /**
     * delete budg task
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteBudgTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgTaskActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteBudgTaskActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgTaskActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) deleteBudgTaskActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def showTaskForSprint() {
        Map result = (Map) showTaskForBudgetSprintActionService.execute(params, null)
        String output = result as JSON
        render(view: '/budget/budgTask/showForSprint', model: [output: output])
    }

    def listTaskForSprint() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) listTaskForBudgetSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskForBudgetSprintActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) listTaskForBudgetSprintActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskForBudgetSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listTaskForBudgetSprintActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)

    }

    def updateTaskForSprint() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) updateTaskForBudgetSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateTaskForBudgetSprintActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateTaskForBudgetSprintActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateTaskForBudgetSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateTaskForBudgetSprintActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)

    }
}
