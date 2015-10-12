package com.athena.mis.application.controller

import com.athena.mis.application.actions.sms.*
import grails.converters.JSON

class SmsController {

    static allowedMethods = [
            showSms: "POST", updateSms: "POST", listSms: "POST", selectSms: "POST", sendSms: "POST"
    ]

    ShowSmsActionService showSmsActionService
    SelectSmsActionService selectSmsActionService
    ListSmsActionService listSmsActionService
    UpdateSmsActionService updateSmsActionService
    SendSmsActionService sendSmsActionService

    /**
     * Show SMS
     */
    def showSms() {
        Map result
        Map executePreResult = (Map) showSmsActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) executePreResult.hasAccess
        if (!hasAccess) {
            result = (Map) showSmsActionService.buildFailureResultForUI(executePreResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) showSmsActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showSmsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showSmsActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/sms/show', model: [output: result as JSON])
    }

    /**
     * Select SMS
     */
    def selectSms() {
        Map result

        Map executePreResult = (Map) selectSmsActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) executePreResult.hasAccess
        if (!hasAccess) {
            result = (Map) selectSmsActionService.buildFailureResultForUI(executePreResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) selectSmsActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSmsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectSmsActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Update SMS
     */
    def updateSms() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateSmsActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) updateSmsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSmsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateSmsActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSmsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateSmsActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * List SMS
     */
    def listSms() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) listSmsActionService.executePreCondition(null, null)
        hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) listSmsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) listSmsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listSmsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listSmsActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Send Sms
     */
    def sendSms() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) sendSmsActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) sendSmsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        isError = (Boolean) preResult.isError
        if (isError) {
            result = (Map) sendSmsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) sendSmsActionService.execute(null, preResult)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (Map) sendSmsActionService.buildFailureResultForUI(result)
        }
        output = result as JSON
        render output
    }
}
