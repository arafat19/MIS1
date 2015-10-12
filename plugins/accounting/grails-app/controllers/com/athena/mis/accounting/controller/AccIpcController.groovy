package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accipc.*
import grails.converters.JSON

class AccIpcController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST",
            update: "POST", delete: "POST", list: "POST", search: "POST"
    ]

    ShowAccIpcActionService showAccIpcActionService
    CreateAccIpcActionService createAccIpcActionService
    SelectAccIpcActionService selectAccIpcActionService
    UpdateAccIpcActionService updateAccIpcActionService
    DeleteAccIpcActionService deleteAccIpcActionService
    ListAccIpcActionService listAccIpcActionService
    SearchAccIpcActionService searchAccIpcActionService

    def show() {
        Map result
        Map executeResult = (Map) showAccIpcActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccIpcActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccIpcActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accIpc/show', model: [output: result as JSON])
    }

    def create() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) createAccIpcActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccIpcActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        LinkedHashMap executeResult = (LinkedHashMap) createAccIpcActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccIpcActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createAccIpcActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchAccIpcActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccIpcActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) searchAccIpcActionService.buildSuccessResultForUI(executeResult)
        } else { // normal listing
            executeResult = (LinkedHashMap) listAccIpcActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccIpcActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccIpcActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def delete() {
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) deleteAccIpcActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccIpcActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        LinkedHashMap executeResult = (LinkedHashMap) deleteAccIpcActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccIpcActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) deleteAccIpcActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def select() {
        LinkedHashMap result
        String output
        LinkedHashMap executeResult = (LinkedHashMap) selectAccIpcActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccIpcActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccIpcActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateAccIpcActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccIpcActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateAccIpcActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccIpcActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateAccIpcActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
}
