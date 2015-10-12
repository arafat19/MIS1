package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acciouslip.*
import grails.converters.JSON

class AccIouSlipController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST",
            update: "POST", delete: "POST", list: "POST",
            approve: "POST", sentNotification: "POST", getIndentList: "POST"
    ]

    ApproveAccIouSlipActionService approveAccIouSlipActionService
    CreateAccIouSlipActionService createAccIouSlipActionService
    DeleteAccIouSlipActionService deleteAccIouSlipActionService
    ListAccIouSlipActionService listAccIouSlipActionService
    SearchAccIouSlipActionService searchAccIouSlipActionService
    SelectAccIouSlipActionService selectAccIouSlipActionService
    SentNotificationAccIouSlipActionService sentNotificationAccIouSlipActionService
    ShowAccIouSlipActionService showAccIouSlipActionService
    UpdateAccIouSlipActionService updateAccIouSlipActionService
    GetIndentListByProjectIdActionService getIndentListByProjectIdActionService

    def show() {
        Map result
        Map executeResult = (Map) showAccIouSlipActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccIouSlipActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccIouSlipActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accIouSlip/show', model: [output: result as JSON])
    }

    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccIouSlipActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createAccIouSlipActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createAccIouSlipActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createAccIouSlipActionService.buildSuccessResultForUI(executeResult)
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
            executeResult = (LinkedHashMap) searchAccIouSlipActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccIouSlipActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchAccIouSlipActionService.buildSuccessResultForUI(executeResult)

        } else { // normal listing
            executeResult = (LinkedHashMap) listAccIouSlipActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccIouSlipActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccIouSlipActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) deleteAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccIouSlipActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteAccIouSlipActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteAccIouSlipActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) deleteAccIouSlipActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def select() {
        LinkedHashMap result
        String output
        LinkedHashMap executeResult = (LinkedHashMap) selectAccIouSlipActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccIouSlipActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccIouSlipActionService.buildSuccessResultForUI(executeResult)
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
        preResult = (LinkedHashMap) updateAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccIouSlipActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateAccIouSlipActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccIouSlipActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateAccIouSlipActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def approve() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) approveAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveAccIouSlipActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approveAccIouSlipActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveAccIouSlipActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approveAccIouSlipActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def sentNotification() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) sentNotificationAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sentNotificationAccIouSlipActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) sentNotificationAccIouSlipActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sentNotificationAccIouSlipActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) sentNotificationAccIouSlipActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def getIndentList() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) getIndentListByProjectIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getIndentListByProjectIdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) getIndentListByProjectIdActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getIndentListByProjectIdActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) getIndentListByProjectIdActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
