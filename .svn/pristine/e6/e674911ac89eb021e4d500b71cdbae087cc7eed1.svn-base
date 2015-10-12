package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.photoidtype.*
import com.athena.mis.exchangehouse.entity.ExhPhotoIdType
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ExhPhotoIdTypeController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", edit: "POST", list: "POST", delete: "POST"];


    ExhShowPhotoIdTypeActionService exhShowPhotoIdTypeActionService
    ExhListPhotoIdTypeActionService exhListPhotoIdTypeActionService
    ExhSearchPhotoIdTypeActionService exhSearchPhotoIdTypeActionService
    ExhCreatePhotoIdTypeActionService exhCreatePhotoIdTypeActionService
    ExhDeletePhotoIdTypeActionService exhDeletePhotoIdTypeActionService
    ExhUpdatePhotoIdTypeActionService exhUpdatePhotoIdTypeActionService
    ExhSelectPhotoIdTypeActionService exhSelectPhotoIdTypeActionService

    /**
     *
     * This function will return a list of all PhotoIdType via an Ajax call
     * in the form of JSON. A list can be normal list containing all the
     * objects in the back-end, or user can search based on a column,
     * in which case the function will run a case-insensitive LIKE query.
     *
     */
    def show() {

        Map executeResult = (Map) exhShowPhotoIdTypeActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue()
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        executeResult = (Map) exhShowPhotoIdTypeActionService.execute(params, null)
        executeResult = (Map) exhShowPhotoIdTypeActionService.buildSuccessResultForUI(executeResult)
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhPhotoIdType/show', model: [modelJson: modelJson])
    }

    def delete() {

        Map result = (Map) exhDeletePhotoIdTypeActionService.executePreCondition(params, null)
        if (result == null) {
            result = exhDeletePhotoIdTypeActionService.buildFailureResultForUI(null)
            render(result as JSON)
            return
        }

        boolean hasAccess = ((Boolean) result.hasAccess)
        if (hasAccess == false) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        if (result.hasAssociation != null) {
            result = exhDeletePhotoIdTypeActionService.buildFailureResultForUI((String) result.hasAssociation)
            render(result as JSON)
            return
        }

        boolean deleteResult = ((Boolean) exhDeletePhotoIdTypeActionService.execute(params, null)).booleanValue()
        if (deleteResult == false) {
            result = exhDeletePhotoIdTypeActionService.buildFailureResultForUI(deleteResult)
        } else {
            result = exhDeletePhotoIdTypeActionService.buildSuccessResultForUI(deleteResult)
        }
        render(result as JSON)
    }

    /*
    *build a closure to create a new photoIdType
     */

    def create() {

        ExhPhotoIdType objPhotoIdType = new ExhPhotoIdType(params)

        Map result = (Map) exhCreatePhotoIdTypeActionService.executePreCondition(null, objPhotoIdType)

        boolean isValid = ((Boolean) result.isValid)
        if (isValid == false) {
            result = (Map) exhCreatePhotoIdTypeActionService.formatValidationErrorsForUI(objPhotoIdType, { g.message(error: it) }, false)
            render(result as JSON)
            return;
        }

        ExhPhotoIdType savedPhotoIdType = (ExhPhotoIdType) exhCreatePhotoIdTypeActionService.execute(params, objPhotoIdType)
        if (savedPhotoIdType) {
            result = (Map) exhCreatePhotoIdTypeActionService.buildSuccessResultForUI(savedPhotoIdType)
        } else {
            result = (Map) exhCreatePhotoIdTypeActionService.buildFailureResultForUI(objPhotoIdType)
        }

        render(result as JSON)

    }


    def list() {

        Map executeResult = [] as LinkedHashMap
        // if it's a search request

        if (params.query) {
            executeResult = (Map) exhSearchPhotoIdTypeActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess)
            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (Map)exhSearchPhotoIdTypeActionService.execute(params, null)
            executeResult = (Map)exhSearchPhotoIdTypeActionService.buildSuccessResultForUI(executeResult)
        } else { // normal listing     */
            executeResult = (Map) exhListPhotoIdTypeActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess)

            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }
            executeResult = (Map) exhListPhotoIdTypeActionService.execute(params, null)
            executeResult = (Map) exhListPhotoIdTypeActionService.buildSuccessResultForUI(executeResult)
        }
        render(executeResult as JSON)
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) exhUpdatePhotoIdTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdatePhotoIdTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhUpdatePhotoIdTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdatePhotoIdTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhUpdatePhotoIdTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     *
     * Retrieves a PhotoIdType via Ajax call against the id attribute (default primary key)
     * If it finds an object against the provided id, it returns the PhotoIdType in the form
     * of JSON, null otherwise.
     * CAUTION:
     * 	1. 	It returns the version property of the entity, so make sure that version is
     * 		not false in the domain class definition
     *
     */
    def edit() {

        Map result = [] as LinkedHashMap;

        result = (Map) exhSelectPhotoIdTypeActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) result.hasAccess)
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        ExhPhotoIdType photoIdType = (ExhPhotoIdType)exhSelectPhotoIdTypeActionService.execute(params, null)
        if (photoIdType) {
            result = (Map) exhSelectPhotoIdTypeActionService.buildSuccessResultForUI(photoIdType)
        } else {
            result = (Map) exhSelectPhotoIdTypeActionService.buildFailureResultForUI(null)
        }
        String output = result as JSON
        render output
    }
}

