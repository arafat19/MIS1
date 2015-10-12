package com.athena.mis.application.controller

import com.athena.mis.application.actions.costingtype.CreateCostingTypeActionService
import com.athena.mis.application.actions.costingtype.DeleteCostingTypeActionService
import com.athena.mis.application.actions.costingtype.ListCostingTypeActionService
import com.athena.mis.application.actions.costingtype.SearchCostingTypeActionService
import com.athena.mis.application.actions.costingtype.SelectCostingTypeActionService
import com.athena.mis.application.actions.costingtype.ShowCostingTypeActionService
import com.athena.mis.application.actions.costingtype.UpdateCostingTypeActionService
import grails.converters.JSON

class CostingTypeController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST",
                             edit  : "POST", update: "POST", delete: "POST", list: "POST"]

    ShowCostingTypeActionService showCostingTypeActionService
    ListCostingTypeActionService listCostingTypeActionService
    CreateCostingTypeActionService createCostingTypeActionService
    DeleteCostingTypeActionService deleteCostingTypeActionService
    UpdateCostingTypeActionService updateCostingTypeActionService
    SelectCostingTypeActionService selectCostingTypeActionService
    SearchCostingTypeActionService searchCostingTypeActionService

    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showCostingTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showCostingTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showCostingTypeActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/costingType/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createCostingTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCostingTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createCostingTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCostingTypeActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createCostingTypeActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def select() {
        Map executeResult
        executeResult = (Map) selectCostingTypeActionService.execute(params, null)
        render(executeResult as JSON)
    }


    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateCostingTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCostingTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateCostingTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCostingTypeActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateCostingTypeActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteCostingTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCostingTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteCostingTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCostingTypeActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) deleteCostingTypeActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchCostingTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchCostingTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchCostingTypeActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listCostingTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listCostingTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listCostingTypeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
