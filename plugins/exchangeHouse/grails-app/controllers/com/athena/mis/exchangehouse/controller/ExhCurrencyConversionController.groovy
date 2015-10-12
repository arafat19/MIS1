package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.currencyconversion.*
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ExhCurrencyConversionController {

    static allowedMethods = [show:"POST",create: "POST", update: "POST", edit: "POST", list: "POST"]


    ExhShowCurrencyConversionActionService exhShowCurrencyConversionActionService
    ExhListCurrencyConversionActionService exhListCurrencyConversionActionService
    ExhCreateCurrencyConversionActionService exhCreateCurrencyConversionActionService
    ExhUpdateCurrencyConversionActionService exhUpdateCurrencyConversionActionService
    ExhSelectCurrencyConversionActionService exhSelectCurrencyConversionActionService

    /**
     *
     * This function will return a list of all CurrencyConversion via an Ajax call
     * in the form of JSON. A list can be normal list containing all the
     * objects in the back-end, or user can search based on a column,
     * in which case the function will run a case-insensitive LIKE query.
     *
     */
    def show() {

        Map executeResult = (Map) exhShowCurrencyConversionActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) executeResult.hasAccess)
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        executeResult = (Map) exhShowCurrencyConversionActionService.execute(params, null)
        executeResult = (Map) exhShowCurrencyConversionActionService.buildSuccessResultForUI(executeResult)
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhCurrencyConversion/show', model: [modelJson: modelJson])
    }
    /*
    *build a closure to create a new currencyConversion
     */


    def create() {

        ExhCurrencyConversion objCurrencyConversion = new ExhCurrencyConversion(params)

        Map result = (Map) exhCreateCurrencyConversionActionService.executePreCondition(null, objCurrencyConversion)

        boolean hasAccess = ((Boolean) result.hasAccess)
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }

        boolean isValid = ((Boolean) result.isValid)
        if (!isValid) {
            result = (Map) exhCreateCurrencyConversionActionService.formatValidationErrorsForUI(objCurrencyConversion, { g.message(error: it) }, false)
            render(result as JSON)
            return;
        }

        boolean isError = ((Boolean) result.isError)
        if (isError) {
            result = (Map) exhCreateCurrencyConversionActionService.buildFailureResultForUI(result)
            render(result as JSON)
            return
        }

        ExhCurrencyConversion savedCurrencyConversion = (ExhCurrencyConversion) exhCreateCurrencyConversionActionService.execute(params, result.currencyConversion)
        if (savedCurrencyConversion) {
            result = (Map) exhCreateCurrencyConversionActionService.buildSuccessResultForUI(savedCurrencyConversion)
        } else {
            result = (Map) exhCreateCurrencyConversionActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    /**
     * Updates a CurrencyConversion via Ajax call
     * Before committing, it tries to validate the CurrencyConversion object, if
     * validation fails, it returns the error(s) back to the caller in the form
     * of JSON error object array.
     * If validation passes, it saves the entity and return the entity in the form
     * of JSON
     */
    def update() {

        ExhCurrencyConversion currencyConversionInstance = new ExhCurrencyConversion(params)
        currencyConversionInstance.id = Long.parseLong(params.id)
        currencyConversionInstance.version = Long.parseLong(params.version)

        Map result = (Map) exhUpdateCurrencyConversionActionService.executePreCondition(null, currencyConversionInstance)

        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue()
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        boolean isValid = ((Boolean) result.isValid).booleanValue()
        if (!isValid) {
            result = (Map) exhUpdateCurrencyConversionActionService.formatValidationErrorsForUI(currencyConversionInstance, { g.message(error: it) }, false)
            render(result as JSON)
            return
        }

        boolean isError = ((Boolean) result.isError).booleanValue()
        if (isError) {
            result = (Map) exhUpdateCurrencyConversionActionService.buildFailureResultForUI(result)
            render(result as JSON)
        }

        Integer updateCount = (Integer) exhUpdateCurrencyConversionActionService.execute(null, result.currencyConversion)

        if (updateCount.intValue() > 0) {
            result = (Map) exhUpdateCurrencyConversionActionService.buildSuccessResultForUI(currencyConversionInstance)
        } else {
            result = (Map) exhUpdateCurrencyConversionActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }


    def list() {

        Map executeResult = [] as LinkedHashMap;
            executeResult = (Map) exhListCurrencyConversionActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue()

            if (hasAccess == false) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (Map) exhListCurrencyConversionActionService.execute(params, null)
            executeResult = (Map) exhListCurrencyConversionActionService.buildSuccessResultForUI(executeResult)
        render(executeResult as JSON)
    }

    /**
     *
     * Retrieves a CurrencyConversion via Ajax call against the id attribute (default primary key)
     * If it finds an object against the provided id, it returns the CurrencyConversion in the form
     * of JSON, null otherwise.
     * CAUTION:
     * 	1. 	It returns the version property of the entity, so make sure that version is
     * 		not false in the domain class definition
     *
     */
    def edit() {

        Map result = [] as LinkedHashMap

        result = (Map) exhSelectCurrencyConversionActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) result.hasAccess)
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        ExhCurrencyConversion currencyConversion = (ExhCurrencyConversion) exhSelectCurrencyConversionActionService.execute(params, null)
        if (currencyConversion) {
            result = (Map) exhSelectCurrencyConversionActionService.buildSuccessResultForUI(currencyConversion)
        } else {
            result = (Map) exhSelectCurrencyConversionActionService.buildFailureResultForUI(null)
        }
        String output = result as JSON
        render output
    }

}