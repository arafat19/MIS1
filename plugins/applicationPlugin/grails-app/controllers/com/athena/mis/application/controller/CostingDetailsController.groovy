package com.athena.mis.application.controller

import com.athena.mis.application.actions.costingdetails.*
import grails.converters.JSON

class CostingDetailsController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST",
                             edit  : "POST", update: "POST", delete: "POST", list: "POST"]

    CreateCostingDetailsActionService createCostingDetailsActionService
    ShowCostingDetailsActionService showCostingDetailsActionService
    UpdateCostingDetailsActionService updateCostingDetailsActionService
    ListCostingDetailsActionService listCostingDetailsActionService
    DeleteCostingDetailsActionService deleteCostingDetailsActionService
    SelectCostingDetailsActionService selectCostingDetailsActionService


    def select() {
        Map executeResult
        executeResult = (Map) selectCostingDetailsActionService.execute(params, null)
        render(executeResult as JSON)
    }

    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteCostingDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCostingDetailsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteCostingDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCostingDetailsActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) deleteCostingDetailsActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

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

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateCostingDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCostingDetailsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateCostingDetailsActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCostingDetailsActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateCostingDetailsActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) listCostingDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listCostingDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listCostingDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listCostingDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listCostingDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listCostingDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
