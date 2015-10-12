package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmspurchaseinstrumentmapping.*
import grails.converters.JSON

class RmsPurchaseInstrumentMappingController {

    static allowedMethods = [
            show: "POST",
            create: "POST",
            select: "POST",
            update: "POST",
            list: "POST",
            delete: "POST",
            evaluateLogic:"POST"
    ]

    ShowRmsPurchaseInstrumentMappingActionService showRmsPurchaseInstrumentMappingActionService
    CreateRmsPurchaseInstrumentMappingActionService createRmsPurchaseInstrumentMappingActionService
    ListRmsPurchaseInstrumentMappingActionService listRmsPurchaseInstrumentMappingActionService
    SelectRmsPurchaseInstrumentMappingActionService selectRmsPurchaseInstrumentMappingActionService
    UpdateRmsPurchaseInstrumentMappingActionService updateRmsPurchaseInstrumentMappingActionService
    DeleteRmsPurchaseInstrumentMappingActionService deleteRmsPurchaseInstrumentMappingActionService
    EvaluateLogicForRmsPurchaseInstrumentMappingActionService evaluateLogicForRmsPurchaseInstrumentMappingActionService

    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showRmsPurchaseInstrumentMappingActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showRmsPurchaseInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/arms/rmsPurchaseInstrumentMapping/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createRmsPurchaseInstrumentMappingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createRmsPurchaseInstrumentMappingActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createRmsPurchaseInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectRmsPurchaseInstrumentMappingActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectRmsPurchaseInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateRmsPurchaseInstrumentMappingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateRmsPurchaseInstrumentMappingActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateRmsPurchaseInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map result
        Boolean isError
        String output
        Map executeResult = (Map) listRmsPurchaseInstrumentMappingActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listRmsPurchaseInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteRmsPurchaseInstrumentMappingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteRmsPurchaseInstrumentMappingActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteRmsPurchaseInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def evaluateLogic(){
        Map preResult
        Map result
        Boolean isError
        String output

        preResult=(Map) evaluateLogicForRmsPurchaseInstrumentMappingActionService.executePreCondition(params,null)
        isError=(Boolean)preResult.isError
        if(isError.booleanValue()){
            result=(Map) evaluateLogicForRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output=result as JSON
            render output
            return
        }
        result=(Map)evaluateLogicForRmsPurchaseInstrumentMappingActionService.execute(preResult,null)
        isError=(Boolean)result.isError
        if(isError.booleanValue()){
            result=(Map) evaluateLogicForRmsPurchaseInstrumentMappingActionService.buildFailureResultForUI(null)
        }
        output=result as JSON
        render output
    }
}
