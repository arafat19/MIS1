package com.athena.mis.application.controller

import com.athena.mis.application.actions.costingdetails.CreateCostingDetailsActionService
import grails.converters.JSON

class CostingDetailsController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST",
                             edit  : "POST", update: "POST", delete: "POST", list: "POST"]

    CreateCostingDetailsActionService createCostingDetailsActionService


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
