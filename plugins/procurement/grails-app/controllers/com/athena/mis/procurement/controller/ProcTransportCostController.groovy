package com.athena.mis.procurement.controller

import com.athena.mis.procurement.actions.transportcost.*
import grails.converters.JSON

class ProcTransportCostController {
    static allowedMethods = [edit: "POST", create: "POST", delete: "POST", update: "POST", list: "POST"]

    ShowTransportCostActionService showTransportCostActionService
    SelectTransportCostActionService selectTransportCostActionService
    UpdateTransportCostActionService updateTransportCostActionService
    DeleteTransportCostActionService deleteTransportCostActionService
    CreateTransportCostActionService createTransportCostActionService
    ListTransportCostActionService listTransportCostActionService

    /**
     * Show transport cost
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult
        preResult = (LinkedHashMap) showTransportCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showTransportCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render(view: '/procurement/procTransportCost/show', model: [output: output])
            return;
        }

        executeResult = (LinkedHashMap) showTransportCostActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showTransportCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render(view: '/procurement/procTransportCost/show', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showTransportCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render(view: '/procurement/procTransportCost/show', model: [output: output])
    }

    /**
     * Create transport cost
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createTransportCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createTransportCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createTransportCostActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createTransportCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createTransportCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Edit transport cost
     */
    def edit() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectTransportCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectTransportCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) selectTransportCostActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectTransportCostActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectTransportCostActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /**
     * Update transport cost
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateTransportCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateTransportCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateTransportCostActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateTransportCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) updateTransportCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete transport cost
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteTransportCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteTransportCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteTransportCostActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteTransportCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) deleteTransportCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * List transport cost
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) listTransportCostActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listTransportCostActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listTransportCostActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
}
