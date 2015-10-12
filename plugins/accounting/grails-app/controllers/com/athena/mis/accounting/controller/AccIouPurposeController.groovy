package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accioupurpose.*
import grails.converters.JSON

class AccIouPurposeController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST",
            update: "POST", delete: "POST", list: "POST"]

    ShowAccIouPurposeActionService showAccIouPurposeActionService
    CreateAccIouPurposeActionService createAccIouPurposeActionService
    SelectAccIouPurposeActionService selectAccIouPurposeActionService
    UpdateAccIouPurposeActionService updateAccIouPurposeActionService
    DeleteAccIouPurposeActionService deleteAccIouPurposeActionService
    ListAccIouPurposeActionService listAccIouPurposeActionService

    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult
        preResult = (LinkedHashMap) showAccIouPurposeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showAccIouPurposeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(view: '/accounting/accIouPurpose/show', model: [output: output])
            return
        }

        executeResult = (LinkedHashMap) showAccIouPurposeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showAccIouPurposeActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render(view: '/accounting/accIouPurpose/show', model: [output: output])
            return
        }
        result = (LinkedHashMap) showAccIouPurposeActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render(view: '/accounting/accIouPurpose/show', model: [output: output])
    }

    def create() {
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult = (LinkedHashMap) createAccIouPurposeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccIouPurposeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (LinkedHashMap) createAccIouPurposeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccIouPurposeActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createAccIouPurposeActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectAccIouPurposeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccIouPurposeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) selectAccIouPurposeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccIouPurposeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccIouPurposeActionService.buildSuccessResultForUI(executeResult)
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
        preResult = (LinkedHashMap) updateAccIouPurposeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccIouPurposeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) updateAccIouPurposeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccIouPurposeActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) updateAccIouPurposeActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def delete() {
        LinkedHashMap result
        Boolean isError
        String output

        Map preResult = (LinkedHashMap) deleteAccIouPurposeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccIouPurposeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (LinkedHashMap) deleteAccIouPurposeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccIouPurposeActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) deleteAccIouPurposeActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def list() {
        LinkedHashMap result

        Map executeResult = (LinkedHashMap) listAccIouPurposeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listAccIouPurposeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) listAccIouPurposeActionService.buildSuccessResultForUI(executeResult)
        }

        String output = result as JSON
        render output
    }
}
