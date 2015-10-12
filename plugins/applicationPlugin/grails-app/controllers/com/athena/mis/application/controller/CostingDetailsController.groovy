package com.athena.mis.application.controller

import com.athena.mis.application.actions.costingdetails.CreateCostingDetailsActionService
import com.athena.mis.application.actions.costingdetails.ShowCostingDetailsActionService
import grails.converters.JSON

class CostingDetailsController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST",
                             edit  : "POST", update: "POST", delete: "POST", list: "POST"]

    CreateCostingDetailsActionService createCostingDetailsActionService
    ShowCostingDetailsActionService showCostingDetailsActionService


    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showCostingDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showCostingDetailsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showCostingDetailsActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/costingDetails/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createCostingDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCostingDetailsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createCostingDetailsActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCostingDetailsActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createCostingDetailsActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
}
