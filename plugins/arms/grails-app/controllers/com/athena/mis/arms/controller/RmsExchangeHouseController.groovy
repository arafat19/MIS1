package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmsexchangehouse.*
import grails.converters.JSON

class RmsExchangeHouseController {

    static allowedMethods = [
            show : "POST",
            create : "POST",
            update : "POST",
            delete : "POST",
            select : "POST",
            list : "POST",
            reloadExchangeHouseDropDown: "POST",
            reloadExchangeHouseFilteredDropDown: "POST"
    ]

    ShowRmsExchangeHouseActionService showRmsExchangeHouseActionService
    CreateRmsExchangeHouseActionService createRmsExchangeHouseActionService
    UpdateRmsExchangeHouseActionService updateRmsExchangeHouseActionService
    DeleteRmsExchangeHouseActionService deleteRmsExchangeHouseActionService
    SelectRmsExchangeHouseActionService selectRmsExchangeHouseActionService
    SearchRmsExchangeHouseActionService searchRmsExchangeHouseActionService
    ListRmsExchangeHouseActionService listRmsExchangeHouseActionService

    /**
     * Show Exchange House list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showRmsExchangeHouseActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/arms/rmsExchangeHouse/show', model: [output: result as JSON])
    }

    /**
     * Create Exchange House object
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createRmsExchangeHouseActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsExchangeHouseActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createRmsExchangeHouseActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Update Exchange House object
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateRmsExchangeHouseActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsExchangeHouseActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateRmsExchangeHouseActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Delete Exchange House object
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteRmsExchangeHouseActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsExchangeHouseActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteRmsExchangeHouseActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Select Exchange House
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectRmsExchangeHouseActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Get list and search Exchange House
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchRmsExchangeHouseActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listRmsExchangeHouseActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listRmsExchangeHouseActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listRmsExchangeHouseActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def reloadExchangeHouseDropDown() {
        render rms.dropDownExchangeHouse(params)
    }

    def reloadExchangeHouseFilteredDropDown() {
        render rms.dropDownExchangeHouseFiltered(params)
    }
}
