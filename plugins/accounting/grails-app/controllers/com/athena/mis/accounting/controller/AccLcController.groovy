package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acclc.*
import grails.converters.JSON

class AccLcController {

    static allowedMethods = [create: "POST", delete: "POST", list: "POST", select: "POST", show: "POST", update: "POST"]

    ShowAccLcActionService showAccLcActionService
    CreateAccLcActionService createAccLcActionService
    DeleteAccLcActionService deleteAccLcActionService
    ListAccLcActionService listAccLcActionService
    SelectAccLcActionService selectAccLcActionService
    UpdateAccLcActionService updateAccLcActionService
    SearchAccLcActionService searchAccLcActionService

    def show() {
        Map result
        Map executeResult = (Map) showAccLcActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showAccLcActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showAccLcActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/accounting/accLc/show', model: [output: result as JSON])
    }

    def create() {
        Map result
        String output

        Map preResult = (Map) createAccLcActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccLcActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createAccLcActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccLcActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createAccLcActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        String output

        Map preResult = (Map) deleteAccLcActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccLcActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteAccLcActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccLcActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteAccLcActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (LinkedHashMap) searchAccLcActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccLcActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) searchAccLcActionService.buildSuccessResultForUI(executeResult)

        } else {
            executeResult = (Map) listAccLcActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listAccLcActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listAccLcActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result
        Map executeResult = (Map) selectAccLcActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccLcActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccLcActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    def update() {
        Map result
        String output

        Map preResult = (Map) updateAccLcActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccLcActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateAccLcActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccLcActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAccLcActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

}
