package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.budgetdetails.*
import grails.converters.JSON

class BudgBudgetDetailsController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", list: "POST", delete: "POST", select: "POST",
            getItemListBudgetDetails: "POST", generateBudgetRequirement: "POST"]

    ShowBudgetDetailsActionService showBudgetDetailsActionService
    CreateBudgetDetailsActionService createBudgetDetailsActionService
    ListBudgetDetailsActionService listBudgetDetailsActionService
    SearchBudgetDetailsActionService searchBudgetDetailsActionService
    SelectBudgetDetailsActionService selectBudgetDetailsActionService
    UpdateBudgetDetailsActionService updateBudgetDetailsActionService
    DeleteBudgetDetailsActionService deleteBudgetDetailsActionService
    GetForItemListForBudgetDetailsActionService getForItemListForBudgetDetailsActionService
    GenerateBudgetRequirementActionService generateBudgetRequirementActionService

    /**
     * Show budget details
     */
    def show() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) showBudgetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showBudgetDetailsActionService.buildFailureResultForUI(preResult);
            renderShowWithMaterial(result)
            return;
        }

        executeResult = (LinkedHashMap) showBudgetDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showBudgetDetailsActionService.buildFailureResultForUI(executeResult);
            renderShowWithMaterial(result)
            return;
        }
        result = (LinkedHashMap) showBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        renderShowWithMaterial(result)
        return
    }

    /**
     * Render Budget details show with Material
     * @param result - a map from caller method
     */
    private renderShowWithMaterial(Map result) {
        String output = result as JSON
        if (result.isProduction) {
            render(view: '/budget/budgBudgetDetails/show', model: [output: output])
        } else {
            render(view: '/budget/budgBudgetDetails/showForProcurement', model: [output: output])
        }

    }

    /**
     * Create Budget details
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) createBudgetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createBudgetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createBudgetDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createBudgetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * List of Budget details
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        if (params.query) {
            executeResult = (LinkedHashMap) searchBudgetDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchBudgetDetailsActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        } else {

            executeResult = (LinkedHashMap) listBudgetDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listBudgetDetailsActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    /**
     * Select Budget details
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectBudgetDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectBudgetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Update Budget details
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateBudgetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateBudgetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateBudgetDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateBudgetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete Budget details
     */
    def delete() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) deleteBudgetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteBudgetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteBudgetDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteBudgetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deleteBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Get item list by budget details
     */
    def getItemListBudgetDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) getForItemListForBudgetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForItemListForBudgetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getForItemListForBudgetDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForItemListForBudgetDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getForItemListForBudgetDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def generateBudgetRequirement() {
        Map result
        String output
        Boolean isError

        Map preResult = (Map) generateBudgetRequirementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) generateBudgetRequirementActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) generateBudgetRequirementActionService.execute(null, preResult)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (Map) generateBudgetRequirementActionService.buildFailureResultForUI(result)
        }
        output = result as JSON
        render output
    }
}
