package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmstransactionday.CloseRmsTransactionDayActionService
import com.athena.mis.arms.actions.rmstransactionday.ListRmsTransactionDayActionService
import com.athena.mis.arms.actions.rmstransactionday.OpenRmsTransactionDayActionService
import com.athena.mis.arms.actions.rmstransactionday.ReOpenRmsTransactionDayActionService
import grails.converters.JSON

class RmsTransactionDayController {

    static allowedMethods = [
            show: "POST",
            list: "POST",
            openTransactionDay: "POST",
            closeTransactionDay: "POST",
            reOpenTransactionDay: "POST"
    ]

    ListRmsTransactionDayActionService listRmsTransactionDayActionService
    OpenRmsTransactionDayActionService openRmsTransactionDayActionService
    CloseRmsTransactionDayActionService closeRmsTransactionDayActionService
    ReOpenRmsTransactionDayActionService reOpenRmsTransactionDayActionService

    def show() {
        render(view: '/arms/rmsTransactionDay/show')
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        executeResult = (Map) listRmsTransactionDayActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listRmsTransactionDayActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listRmsTransactionDayActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def openTransactionDay() {
        Map result
        Boolean isError
        Map preResult = (Map) openRmsTransactionDayActionService.executePreCondition(params, null)
        isError = preResult.isError
        if(isError.booleanValue()) {
            result = (Map) openRmsTransactionDayActionService.buildFailureResultForUI(preResult)
        } else{
            Map executeResult = (Map) openRmsTransactionDayActionService.execute(null, preResult)
            isError = executeResult.isError
            if(isError.booleanValue()) {
                result = (Map) openRmsTransactionDayActionService.buildFailureResultForUI(executeResult)
            } else {
                result = executeResult
            }
        }
        String output = result as JSON
        render output
    }

    def closeTransactionDay() {
        Map result
        Boolean isError
        Map preResult = (Map) closeRmsTransactionDayActionService.executePreCondition(params, null)
        isError = preResult.isError
        if(isError.booleanValue()) {
            result = (Map) closeRmsTransactionDayActionService.buildFailureResultForUI(preResult)
        } else{
            Map executeResult = (Map) closeRmsTransactionDayActionService.execute(null, preResult)
            isError = executeResult.isError
            if(isError.booleanValue()) {
                result = (Map) closeRmsTransactionDayActionService.buildFailureResultForUI(executeResult)
            } else {
                result = executeResult
            }
        }
        String output = result as JSON
        render output
    }

    def reOpenTransactionDay() {
        Map result
        Boolean isError
        Map preResult = (Map) reOpenRmsTransactionDayActionService.executePreCondition(params, null)
        isError = preResult.isError
        if(isError.booleanValue()) {
            result = (Map) reOpenRmsTransactionDayActionService.buildFailureResultForUI(preResult)
        } else{
            Map executeResult = (Map) reOpenRmsTransactionDayActionService.execute(null, preResult)
            isError = executeResult.isError
            if(isError.booleanValue()) {
                result = (Map) reOpenRmsTransactionDayActionService.buildFailureResultForUI(executeResult)
            } else {
                result = executeResult
            }
        }
        String output = result as JSON
        render output
    }
}
