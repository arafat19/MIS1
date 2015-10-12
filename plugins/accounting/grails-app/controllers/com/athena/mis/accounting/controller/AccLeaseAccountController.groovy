package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accleaseaccount.*
import grails.converters.JSON

class AccLeaseAccountController {
    static allowedMethods = [show: "POST", create: "POST", select: "POST",
            update: "POST", delete: "POST", list: "POST", search: "POST"
    ]

    ShowAccLeaseAccountActionService showAccLeaseAccountActionService
    CreateAccLeaseAccountActionService createAccLeaseAccountActionService
    SelectAccLeaseAccountActionService selectAccLeaseAccountActionService
    UpdateAccLeaseAccountActionService updateAccLeaseAccountActionService
    DeleteAccLeaseAccountActionService deleteAccLeaseAccountActionService
    ListAccLeaseAccountActionService listAccLeaseAccountActionService
    SearchAccLeaseAccountActionService searchAccLeaseAccountActionService

    def show() {
        Map result
        Map executeResult = (Map) showAccLeaseAccountActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accLeaseAccount/show', model: [output: result as JSON])
    }

    def create() {
        LinkedHashMap result
        String output
        Map preResult = (LinkedHashMap) createAccLeaseAccountActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccLeaseAccountActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (LinkedHashMap) createAccLeaseAccountActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
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
            executeResult = (LinkedHashMap) searchAccLeaseAccountActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }
            result = (LinkedHashMap) searchAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
        } else { // normal listing
            executeResult = (LinkedHashMap) listAccLeaseAccountActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }
            result = (LinkedHashMap) listAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def delete() {
        LinkedHashMap result
        String output
        Map preResult = (LinkedHashMap) deleteAccLeaseAccountActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccLeaseAccountActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (LinkedHashMap) deleteAccLeaseAccountActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (LinkedHashMap) deleteAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def select() {
        LinkedHashMap result
        String output
        LinkedHashMap executeResult = (LinkedHashMap) selectAccLeaseAccountActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        LinkedHashMap result
        String output
        Map preResult = (LinkedHashMap) updateAccLeaseAccountActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccLeaseAccountActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (LinkedHashMap) updateAccLeaseAccountActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccLeaseAccountActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) updateAccLeaseAccountActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
}
