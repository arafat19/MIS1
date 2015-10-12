package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acctype.*
import grails.converters.JSON

class AccTypeController {

    static allowedMethods = [list: "POST", select: "POST", show: "POST", update: "POST", delete: "POST"]

    ShowAccTypeActionService showAccTypeActionService
    ListAccTypeActionService listAccTypeActionService
    SelectAccTypeActionService selectAccTypeActionService
    UpdateAccTypeActionService updateAccTypeActionService
    DeleteAccTypeActionService deleteAccTypeActionService

    /**
     * show account type(accType) list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showAccTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showAccTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showAccTypeActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/accounting/accType/show', model: [output: result as JSON])
    }

    /**
     * delete account type(accType)
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteAccTypeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) deleteAccTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteAccTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteAccTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * list account type(accType)
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        executeResult = (Map) listAccTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAccTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listAccTypeActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /**
     * Select account type(accType)
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectAccTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * update account type(accType)
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateAccTypeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) updateAccTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateAccTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAccTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
