package com.athena.mis.procurement.controller

import com.athena.mis.procurement.actions.indentdetails.*
import grails.converters.JSON

class ProcIndentDetailsController {

    static allowedMethods = [
            show: "POST",
            create: "POST",
            select: "POST",
            update: "POST",
            delete: "POST",
            list: "POST"]

    ShowIndentDetailsActionService showIndentDetailsActionService
    CreateIndentDetailsActionService createIndentDetailsActionService
    ListIndentDetailsActionService listIndentDetailsActionService
    SearchIndentDetailsActionService searchIndentDetailsActionService
    SelectIndentDetailsActionService selectIndentDetailsActionService
    UpdateIndentDetailsActionService updateIndentDetailsActionService
    DeleteIndentDetailsActionService deleteIndentDetailsActionService

    /**
     * Show indent details
     */
    def show() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) showIndentDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showIndentDetailsActionService.buildFailureResultForUI(preResult);
            renderShow(result)
            return;
        }

        executeResult = (LinkedHashMap) showIndentDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showIndentDetailsActionService.buildFailureResultForUI(executeResult);
            renderShow(result)
            return;
        }
        result = (LinkedHashMap) showIndentDetailsActionService.buildSuccessResultForUI(executeResult);
        renderShow(result)
    }

    /**
     * Method for rendering the show
     * @param result - a map from caller method
     */
    private renderShow(Map result) {
        String output = result as JSON
        render(view: '/procurement/procIndentDetails/show', model: [output: output])
    }

    /**
     * Create indent details
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) createIndentDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createIndentDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createIndentDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createIndentDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createIndentDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * List and search indent details
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchIndentDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchIndentDetailsActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchIndentDetailsActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listIndentDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listIndentDetailsActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listIndentDetailsActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    /**
     * Select indent details
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectIndentDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectIndentDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectIndentDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Update indent details
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateIndentDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateIndentDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateIndentDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateIndentDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateIndentDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete indent details
     */
    def delete() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        preResult = (Map) deleteIndentDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteIndentDetailsActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteIndentDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteIndentDetailsActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) deleteIndentDetailsActionService.buildFailureResultForUI(executeResult);
        }
        render(result as JSON)
    }
}
