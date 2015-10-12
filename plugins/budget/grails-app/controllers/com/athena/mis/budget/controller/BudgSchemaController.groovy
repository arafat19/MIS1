package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.budgschema.*
import grails.converters.JSON

class BudgSchemaController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", list: "POST", delete: "POST", select: "POST",
            listItemForBudgetSchema: "POST"
    ]

    ShowBudgSchemaActionService showBudgSchemaActionService
    CreateBudgSchemaActionService createBudgSchemaActionService
    UpdateBudgSchemaActionService updateBudgSchemaActionService
    SearchBudgSchemaActionService searchBudgSchemaActionService
    ListBudgSchemaActionService listBudgSchemaActionService
    DeleteBudgSchemaActionService deleteBudgSchemaActionService
    SelectBudgSchemaActionService selectBudgSchemaActionService
    GetItemListForBudgetSchemaActionService getItemListForBudgetSchemaActionService

    /**
     * Show budget schema
     */
    def show() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) showBudgSchemaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showBudgSchemaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(view: '/budget/budgSchema/show', model: [output: output])
            return
        }
        Map executeResult = (Map) showBudgSchemaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showBudgSchemaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render(view: '/budget/budgSchema/show', model: [output: output])
    }

    /**
     * Create budget schema
     */
    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createBudgSchemaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgSchemaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createBudgSchemaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBudgSchemaActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) createBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * List of budget schema
     */
    def list() {
        Map executeResult
        Map result
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchBudgSchemaActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBudgSchemaActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }
            result = (Map) searchBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        } else {
            executeResult = (Map) listBudgSchemaActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listBudgSchemaActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }
            result = (Map) listBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Select budget schema
     */
    def select() {
        Map result
        Boolean isError
        String output

        Map executeResult = (Map) selectBudgSchemaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectBudgSchemaActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) selectBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Update budget schema
     */
    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updateBudgSchemaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgSchemaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateBudgSchemaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBudgSchemaActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) updateBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Delete budget schema
     */
    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteBudgSchemaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgSchemaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteBudgSchemaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBudgSchemaActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) deleteBudgSchemaActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Get item list for budget schema
     */
    def listItemForBudgetSchema() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) getItemListForBudgetSchemaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getItemListForBudgetSchemaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) getItemListForBudgetSchemaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getItemListForBudgetSchemaActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }
        result = (Map) getItemListForBudgetSchemaActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
}
