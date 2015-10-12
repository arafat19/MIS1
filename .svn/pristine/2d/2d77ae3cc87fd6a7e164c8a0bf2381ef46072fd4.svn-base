package com.athena.mis.application.controller

import com.athena.mis.application.actions.currency.*
import com.athena.mis.application.entity.Currency
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class CurrencyController {

    static allowedMethods = [create: "POST", update: "POST", edit: "POST", list: "POST", delete: "POST"];


    ShowCurrencyActionService showCurrencyActionService
    ListCurrencyActionService listCurrencyActionService
    SearchCurrencyActionService searchCurrencyActionService
    CreateCurrencyActionService createCurrencyActionService
    DeleteCurrencyActionService deleteCurrencyActionService
    UpdateCurrencyActionService updateCurrencyActionService
    SelectCurrencyActionService selectCurrencyActionService

    /**
     *
     * This function will return a list of all Currency via an Ajax call
     * in the form of JSON. A list can be normal list containing all the
     * objects in the back-end, or user can search based on a column,
     * in which case the function will run a case-insensitive LIKE query.
     *
     */
    def show() {

        Map executeResult = (Map) showCurrencyActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue()
        if (hasAccess == false) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        executeResult = (Map) showCurrencyActionService.execute(params, null)
        executeResult = (Map) showCurrencyActionService.buildSuccessResultForUI(executeResult)
        String modelJson = executeResult as JSON
        render(view: '/application/currency/show', model: [modelJson: modelJson])
    }
    /**
     * delete currency
     */
    def delete() {
        Map result = (Map) deleteCurrencyActionService.executePreCondition(params, null)
        if (result == null) {
            result = (Map) deleteCurrencyActionService.buildFailureResultForUI(null)
            render(result as JSON)
            return;
        }
        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue()
        if (hasAccess == false) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        if (result.hasAssociation != null) {
            result = (Map) deleteCurrencyActionService.buildFailureResultForUI((String) result.hasAssociation)
            render(result as JSON)
            return
        }

        boolean deleteResult = ((Boolean) deleteCurrencyActionService.execute(params, null)).booleanValue()
        if (deleteResult == false) {
            result = (Map) deleteCurrencyActionService.buildFailureResultForUI(deleteResult)
        } else {
            result = (Map) deleteCurrencyActionService.buildSuccessResultForUI(deleteResult)
        }
        render(result as JSON)
    }
    /**
     * create currency
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createCurrencyActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCurrencyActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createCurrencyActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createCurrencyActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createCurrencyActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * list & search currency
     */
    def list() {

        Map executeResult = [] as LinkedHashMap

        // if it's a search request
        if (params.query) {
            executeResult = (Map) searchCurrencyActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue()
            if (hasAccess == false) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (Map) searchCurrencyActionService.execute(params, null)
            executeResult = (Map) searchCurrencyActionService.buildSuccessResultForUI(executeResult)
        } else { /* normal listing     */
            executeResult = (Map) listCurrencyActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue();

            if (hasAccess == false) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (Map) listCurrencyActionService.execute(params, null)
            executeResult = (Map) listCurrencyActionService.buildSuccessResultForUI(executeResult)
        }

        render(executeResult as JSON)
    }

    /**
     * Updates a Currency via Ajax call
     * Before committing, it tries to validate the Currency object, if
     * validation fails, it returns the error(s) back to the caller in the form
     * of JSON error object array.
     * If validation passes, it saves the entity and return the entity in the form
     * of JSON
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateCurrencyActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCurrencyActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateCurrencyActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateCurrencyActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateCurrencyActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }

    /**
     *
     * Retrieves a Currency via Ajax call against the id attribute (default primary key)
     * If it finds an object against the provided id, it returns the Currency in the form
     * of JSON, null otherwise.
     * CAUTION:
     * 	1. 	It returns the version property of the entity, so make sure that version is
     * 		not false in the domain class definition
     *
     */
    def edit() {

        Map result = [] as LinkedHashMap
        result = (Map) selectCurrencyActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue()
        if (hasAccess == false) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        Currency currency = (Currency) selectCurrencyActionService.execute(params, null)
        if (currency) {
            result = (Map) selectCurrencyActionService.buildSuccessResultForUI(currency)
        } else {
            result = (Map) selectCurrencyActionService.buildFailureResultForUI(null)
        }
        String output = result as JSON
        render output
    }

}

