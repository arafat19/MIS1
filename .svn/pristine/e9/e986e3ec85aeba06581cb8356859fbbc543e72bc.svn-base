package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.CreateRmsProcessInstrumentMappingActionService
import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.DeleteRmsProcessInstrumentMappingActionService
import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.ListRmsProcessInstrumentMappingActionService
import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.SearchRmsProcessInstrumentMappingActionService
import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.SelectRmsProcessInstrumentMappingActionService
import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.ShowRmsProcessInstrumentMappingActionService
import com.athena.mis.arms.actions.rmsprocessinstrumentmapping.UpdateRmsProcessInstrumentMappingActionService
import grails.converters.JSON

class RmsProcessInstrumentMappingController {

    static allowedMethods = [
            show : "POST",
            create : "POST",
            update : "POST",
            delete : "POST",
            select : "POST",
            list : "POST"
    ]

    ShowRmsProcessInstrumentMappingActionService showRmsProcessInstrumentMappingActionService
    CreateRmsProcessInstrumentMappingActionService createRmsProcessInstrumentMappingActionService
    UpdateRmsProcessInstrumentMappingActionService updateRmsProcessInstrumentMappingActionService
    DeleteRmsProcessInstrumentMappingActionService deleteRmsProcessInstrumentMappingActionService
    SelectRmsProcessInstrumentMappingActionService selectRmsProcessInstrumentMappingActionService
    SearchRmsProcessInstrumentMappingActionService searchRmsProcessInstrumentMappingActionService
    ListRmsProcessInstrumentMappingActionService listRmsProcessInstrumentMappingActionService

    /**
     * Show Process Instrument Mapping list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showRmsProcessInstrumentMappingActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/arms/rmsProcessInstrumentMapping/show', model: [output: result as JSON])
    }

    /**
     * Create Process Instrument Mapping object
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createRmsProcessInstrumentMappingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsProcessInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createRmsProcessInstrumentMappingActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Update Process Instrument Mapping object
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateRmsProcessInstrumentMappingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsProcessInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateRmsProcessInstrumentMappingActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Delete Process Instrument Mapping object
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteRmsProcessInstrumentMappingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsProcessInstrumentMappingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteRmsProcessInstrumentMappingActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Select Process Instrument Mapping
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectRmsProcessInstrumentMappingActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Get list and search Process Instrument Mapping
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchRmsProcessInstrumentMappingActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listRmsProcessInstrumentMappingActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listRmsProcessInstrumentMappingActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listRmsProcessInstrumentMappingActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

}
