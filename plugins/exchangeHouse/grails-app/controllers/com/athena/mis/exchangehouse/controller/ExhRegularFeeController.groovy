package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.regularfee.CalculateExhRegularFeeActionService
import com.athena.mis.exchangehouse.actions.regularfee.ShowExhRegularFeeActionService
import com.athena.mis.exchangehouse.actions.regularfee.UpdateExhRegularFeeActionService
import grails.converters.JSON

class ExhRegularFeeController {

    static allowedMethods = [show: "POST", update: "POST", calculate: "POST"];

    ShowExhRegularFeeActionService showExhRegularFeeActionService
    UpdateExhRegularFeeActionService updateExhRegularFeeActionService
    CalculateExhRegularFeeActionService calculateExhRegularFeeActionService

    def show() {
        Map result;
        Map executeResult
        Boolean isError

        executeResult = (Map) showExhRegularFeeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showExhRegularFeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showExhRegularFeeActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/exchangehouse/regularfee/show', model: [output: result as JSON])
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateExhRegularFeeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhRegularFeeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateExhRegularFeeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhRegularFeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateExhRegularFeeActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def calculate() {
        Map result
        Boolean isError
        Map preResult = (Map) calculateExhRegularFeeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) calculateExhRegularFeeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) calculateExhRegularFeeActionService.execute(null, preResult)
        render(executeResult as JSON)
    }
}
