package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.sprintbudget.*
import grails.converters.JSON

class BudgSprintBudgetController {

    static allowedMethods = [
            show: "POST",
            create: "POST",
            list: "POST",
            select: 'POST',
            update: 'POST',
            delete: 'POST',
            dropDownBudgetForSprintReload: "POST"
    ]

    ShowSprintBudgetActionService showSprintBudgetActionService
    SelectSprintBudgetActionService selectSprintBudgetActionService
    ListSprintBudgetActionService listSprintBudgetActionService
    CreateSprintBudgetActionService createSprintBudgetActionService
    UpdateSprintBudgetActionService updateSprintBudgetActionService
    DeleteSprintBudgetActionService deleteSprintBudgetActionService

    def show(){
        LinkedHashMap executeResult
        LinkedHashMap preResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) showSprintBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showSprintBudgetActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) showSprintBudgetActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showSprintBudgetActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) showSprintBudgetActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        Long sprintId = (Long) result.sprintId
        render(view: '/budget/budgSprintBudget/show', model: [sprintId: sprintId, output: output])
    }

    def create(){
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) createSprintBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createSprintBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createSprintBudgetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createSprintBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createSprintBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def select(){
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectSprintBudgetActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSprintBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectSprintBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def update(){
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateSprintBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateSprintBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateSprintBudgetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateSprintBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateSprintBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def delete(){
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) deleteSprintBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteSprintBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteSprintBudgetActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteSprintBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deleteSprintBudgetActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def list(){
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) listSprintBudgetActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listSprintBudgetActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listSprintBudgetActionService.buildSuccessResultForUI(executeResult)
        }

        String output = result as JSON
        render output
    }

    def dropDownBudgetForSprintReload(){
        render budg.dropDownBudgetForSprint(params)
    }
}
