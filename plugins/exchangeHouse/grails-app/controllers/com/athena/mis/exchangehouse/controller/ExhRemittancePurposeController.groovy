package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.remittancepurpose.*
import com.athena.mis.exchangehouse.entity.ExhRemittancePurpose
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ExhRemittancePurposeController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", edit: "POST", list: "POST", delete: "POST"];

    //auto-wiring required method

    ExhShowRemittancePurposeActionService exhShowRemittancePurposeActionService
    ExhListRemittancePurposeActionService exhListRemittancePurposeActionService
    ExhSearchRemittancePurposeActionService exhSearchRemittancePurposeActionService
    ExhCreateRemittancePurposeActionService exhCreateRemittancePurposeActionService
    ExhDeleteRemittancePurposeActionService exhDeleteRemittancePurposeActionService
    ExhUpdateRemittancePurposeActionService exhUpdateRemittancePurposeActionService
    ExhSelectRemittancePurposeActionService exhSelectRemittancePurposeActionService

    /**
     *
     * This function will return a list of all RemittancePurpose via an Ajax call
     * in the form of JSON. A list can be normal list containing all the
     * objects in the back-end, or user can search based on a column,
     * in which case the function will run a case-insensitive LIKE query.
     *
     */
    def show() {

        Map executeResult = (Map) exhShowRemittancePurposeActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) executeResult.hasAccess)
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        executeResult = (Map) exhShowRemittancePurposeActionService.execute(params, null)
        executeResult = (Map) exhShowRemittancePurposeActionService.buildSuccessResultForUI(executeResult)
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhRemittancePurpose/show', model: [modelJson: modelJson])
    }

    def delete() {
        Map result = (Map) exhDeleteRemittancePurposeActionService.executePreCondition(params, null)
        if (result == null) {
            result = (Map)exhDeleteRemittancePurposeActionService.buildFailureResultForUI(null)
            render(result as JSON)
            return
        }

        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue()
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        if (result.hasAssociation != null) {
            result = (Map)exhDeleteRemittancePurposeActionService.buildFailureResultForUI((String) result.hasAssociation)
            render(result as JSON)
            return
        }

        boolean deleteResult = ((Boolean) exhDeleteRemittancePurposeActionService.execute(params, null)).booleanValue()
        if (!deleteResult.booleanValue()) {
            result = (Map)exhDeleteRemittancePurposeActionService.buildFailureResultForUI(deleteResult)
        } else {
            result = (Map)exhDeleteRemittancePurposeActionService.buildSuccessResultForUI(deleteResult)
        }
        render(result as JSON)
    }

    /*
    *build a closure to create a new remittancePurpose
     */

    def create() {
        ExhRemittancePurpose objRemittancePurpose = new ExhRemittancePurpose(params)
        Map result = (Map) exhCreateRemittancePurposeActionService.executePreCondition(null, objRemittancePurpose)
        boolean isValid = ((Boolean) result.isValid)
        if (isValid == false) {
            result = (Map) exhCreateRemittancePurposeActionService.formatValidationErrorsForUI(objRemittancePurpose, { g.message(error: it) }, false)
            render(result as JSON)
            return
        }
        ExhRemittancePurpose savedRemittancePurpose = (ExhRemittancePurpose) exhCreateRemittancePurposeActionService.execute(params, objRemittancePurpose)
        if (savedRemittancePurpose) {
            result = (Map) exhCreateRemittancePurposeActionService.buildSuccessResultForUI(savedRemittancePurpose)
        } else {
            result = (Map) exhCreateRemittancePurposeActionService.buildFailureResultForUI(objRemittancePurpose)
        }

        render(result as JSON)

    }


    def list() {

        Map executeResult
        // if it's a search request

        if (params.query) {
            executeResult = (Map) exhSearchRemittancePurposeActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess)
            if (!hasAccess .booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = exhSearchRemittancePurposeActionService.execute(params, null)
            executeResult = exhSearchRemittancePurposeActionService.buildSuccessResultForUI(executeResult)
        } else {  /* normal listing */
            executeResult = (Map) exhListRemittancePurposeActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess)

            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (Map) exhListRemittancePurposeActionService.execute(params, null)
            executeResult = (Map) exhListRemittancePurposeActionService.buildSuccessResultForUI(executeResult)

        }

        render(executeResult as JSON)
    }

    def update() {
        Map result
        Map preResult
        Map executeResult
        Boolean isError
        String output

        preResult = (Map) exhUpdateRemittancePurposeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateRemittancePurposeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhUpdateRemittancePurposeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateRemittancePurposeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhUpdateRemittancePurposeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     *
     * Retrieves a RemittancePurpose via Ajax call against the id attribute (default primary key)
     * If it finds an object against the provided id, it returns the RemittancePurpose in the form
     * of JSON, null otherwise.
     * CAUTION:
     * 	1. 	It returns the version property of the entity, so make sure that version is
     * 		not false in the domain class definition
     *
     */
    def edit() {

        Map result = [] as LinkedHashMap

        result = (Map) exhSelectRemittancePurposeActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) result.hasAccess)
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        ExhRemittancePurpose remittancePurpose = (ExhRemittancePurpose)exhSelectRemittancePurposeActionService.execute(params, null)
        if (remittancePurpose) {
            result = (Map) exhSelectRemittancePurposeActionService.buildSuccessResultForUI(remittancePurpose)
        } else {
            result = (Map) exhSelectRemittancePurposeActionService.buildFailureResultForUI(null)
        }
        String output = result as JSON
        render output
    }

}
