package com.athena.mis.application.controller

import com.athena.mis.application.actions.country.*
import grails.converters.JSON

class CountryController {
    static allowedMethods = [show: 'POST', create: 'POST', delete: 'POST', update: 'POST', select: 'POST', list: 'POST']

    ShowCountryActionService showCountryActionService
    CreateCountryActionService createCountryActionService
    UpdateCountryActionService updateCountryActionService
    DeleteCountryActionService deleteCountryActionService
    SelectCountryActionService selectCountryActionService
    ListCountryActionService listCountryActionService
    SearchCountryActionService searchCountryActionService
    /**
     * show country list
     */
    def show() {
        Map result;
        Map executeResult
        Boolean isError

        executeResult = (Map) showCountryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showCountryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showCountryActionService.buildFailureResultForUI(executeResult)
        }
        String output = result as JSON
        render(view: '/application/country/show', model: [output: output])

    }
    /**
     * create country
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError


        preResult = (Map) createCountryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCountryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createCountryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createCountryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createCountryActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * update country
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateCountryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCountryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateCountryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateCountryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateCountryActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }
    /**
     * delete country
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteCountryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCountryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteCountryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteCountryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteCountryActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }
    /**
     * select country
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) selectCountryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (LinkedHashMap) selectCountryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectCountryActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }
    /**
     * list and search country
     */
    def list() {
        Map result;
        Map executeResult;
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchCountryActionService.execute(params, null)

            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchCountryActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchCountryActionService.buildFailureResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listCountryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listCountryActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listCountryActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
