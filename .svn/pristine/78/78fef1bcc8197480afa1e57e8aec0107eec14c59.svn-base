package com.athena.mis.application.controller

import com.athena.mis.application.actions.district.*
import com.athena.mis.application.entity.District
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class DistrictController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST",
            edit: "POST", list: "POST", delete: "POST",
            reloadDistrictDropDown: "POST"];


    ShowDistrictActionService showDistrictActionService
    ListDistrictActionService listDistrictActionService
    SearchDistrictActionService searchDistrictActionService
    CreateDistrictActionService createDistrictActionService
    DeleteDistrictActionService deleteDistrictActionService
    UpdateDistrictActionService updateDistrictActionService
    SelectDistrictActionService selectDistrictActionService

    /**
     *
     * This function will return a list of all District via an Ajax call
     * in the form of JSON. A list can be normal list containing all the
     * objects in the back-end, or user can search based on a column,
     * in which case the function will run a case-insensitive LIKE query.
     *
     */
    def show() {
        Map executeResult = (Map) showDistrictActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue();
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        executeResult = (Map) showDistrictActionService.execute(params, null);
        executeResult = (Map) showDistrictActionService.buildSuccessResultForUI(executeResult);
        String modelJson = executeResult as JSON
        render(view: '/application/district/show', model: [modelJson: modelJson])
    }

    /**
     * delete District
     */
    def delete() {
        Map result = (Map) deleteDistrictActionService.executePreCondition(params, null);
        if (result == null) {
            result = (Map) deleteDistrictActionService.buildFailureResultForUI(null);
            render(result as JSON);
            return;
        }

        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue();
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        if (result.hasAssociation != null) {
            result = (Map) deleteDistrictActionService.buildFailureResultForUI((String) result.hasAssociation);
            render(result as JSON);
            return;
        }

        boolean deleteResult = ((Boolean) deleteDistrictActionService.execute(params, null)).booleanValue();
        if (!deleteResult) {
            result = (Map) deleteDistrictActionService.buildFailureResultForUI(deleteResult);
        } else {
            result = (Map) deleteDistrictActionService.buildSuccessResultForUI(deleteResult);
        }
        render(result as JSON)
    }

    /*
    *build a closure to create a new district
     */

    def create() {
        Map preResult
        Map result
        Map executeResult

        preResult = (Map) createDistrictActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDistrictActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        executeResult = (Map) createDistrictActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDistrictActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createDistrictActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    /**
     * list and search district
     */
    def list() {

        Map executeResult
        // if it's a search request

        if (params.query) {
            executeResult = (LinkedHashMap) searchDistrictActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue();
            if (!hasAccess) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (LinkedHashMap) searchDistrictActionService.execute(params, null);
            executeResult = (LinkedHashMap) searchDistrictActionService.buildSuccessResultForUI(executeResult);
        } else { // normal listing     */
            executeResult = (LinkedHashMap) listDistrictActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue();

            if (!hasAccess) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }

            executeResult = (Map) listDistrictActionService.execute(params, null);
            executeResult = (Map) listDistrictActionService.buildSuccessResultForUI(executeResult);

        }

        render(executeResult as JSON);
    }

    def update() {
        Map result
        Map preResult
        Map executeResult
        Boolean isError
        String output

        preResult = (Map) updateDistrictActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDistrictActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateDistrictActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDistrictActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateDistrictActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     *
     * Retrieves a District via Ajax call against the id attribute (default primary key)
     * If it finds an object against the provided id, it returns the District in the form
     * of JSON, null otherwise.
     * CAUTION:
     * 	1. 	It returns the version property of the entity, so make sure that version is
     * 		not false in the domain class definition
     *
     */
    def edit() {

        Map result = (Map) selectDistrictActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue();
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        District district = (District) selectDistrictActionService.execute(params, null);
        if (district) {
            result = (Map) selectDistrictActionService.buildSuccessResultForUI(district);
        } else {
            result = (Map) selectDistrictActionService.buildFailureResultForUI(null);
        }
        String output = result as JSON
        render output
    }

    def reloadDistrictDropDown() {
        render app.dropDownDistrict(params)
    }
}


