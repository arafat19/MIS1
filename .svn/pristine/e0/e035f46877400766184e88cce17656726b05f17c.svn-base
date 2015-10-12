package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.CreateRmsExchangeHouseCurrencyPostingActionService
import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.DeleteRmsExchangeHouseCurrencyPostingActionService
import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.ListRmsExchangeHouseCurrencyPostingActionService
import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.SearchRmsExchangeHouseCurrencyPostingActionService
import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.SelectRmsExchangeHouseCurrencyPostingActionService
import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.ShowRmsExchangeHouseCurrencyPostingActionService
import com.athena.mis.arms.actions.rmsexchangehousecurrencyposting.UpdateRmsExchangeHouseCurrencyPostingActionService
import grails.converters.JSON

class RmsExchangeHouseCurrencyPostingController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST", select: "POST", list: "POST"]

    ShowRmsExchangeHouseCurrencyPostingActionService showRmsExchangeHouseCurrencyPostingActionService
    CreateRmsExchangeHouseCurrencyPostingActionService createRmsExchangeHouseCurrencyPostingActionService
    UpdateRmsExchangeHouseCurrencyPostingActionService updateRmsExchangeHouseCurrencyPostingActionService
    DeleteRmsExchangeHouseCurrencyPostingActionService deleteRmsExchangeHouseCurrencyPostingActionService
    SelectRmsExchangeHouseCurrencyPostingActionService selectRmsExchangeHouseCurrencyPostingActionService
    SearchRmsExchangeHouseCurrencyPostingActionService searchRmsExchangeHouseCurrencyPostingActionService
    ListRmsExchangeHouseCurrencyPostingActionService listRmsExchangeHouseCurrencyPostingActionService

    /**
     * Show Exchange House Currency Posting list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showRmsExchangeHouseCurrencyPostingActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/arms/rmsExchangeHouseCurrencyPosting/show', model: [output: result as JSON])
    }

    /**
     * Create Exchange House Currency Posting object
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createRmsExchangeHouseCurrencyPostingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createRmsExchangeHouseCurrencyPostingActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Update Exchange House Currency Posting object
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateRmsExchangeHouseCurrencyPostingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateRmsExchangeHouseCurrencyPostingActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Delete Exchange House Currency Posting object
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteRmsExchangeHouseCurrencyPostingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteRmsExchangeHouseCurrencyPostingActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Select Exchange House Currency Posting
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectRmsExchangeHouseCurrencyPostingActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Get list and search Exchange House Currency Posting
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchRmsExchangeHouseCurrencyPostingActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listRmsExchangeHouseCurrencyPostingActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listRmsExchangeHouseCurrencyPostingActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listRmsExchangeHouseCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }
}
