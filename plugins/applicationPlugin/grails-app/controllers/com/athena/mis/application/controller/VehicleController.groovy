package com.athena.mis.application.controller

import com.athena.mis.application.actions.vehicle.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class VehicleController {
    static allowedMethods = [show: "POST", select: "POST", create: "POST", update: "POST", delete: "POST"];

    CreateVehicleActionService createVehicleActionService
    ShowVehicleActionService showVehicleActionService
    SelectVehicleActionService selectVehicleActionService
    UpdateVehicleActionService updateVehicleActionService
    DeleteVehicleActionService deleteVehicleActionService
    SearchVehicleActionService searchVehicleActionService
    ListVehicleActionService listVehicleActionService
    /**
     *  Show vehicle
     */
    def show() {
        Map preResult = (Map) showVehicleActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showVehicleActionService.execute(params, null);
        Map result;
        if (executeResult) {
            result = (Map) showVehicleActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showVehicleActionService.buildFailureResultForUI(null);
        }
        render(view: '/application/vehicle/show', model: [output: result as JSON])
    }
    /**
     *  Create vehicle
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createVehicleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createVehicleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createVehicleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createVehicleActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createVehicleActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     *  Select vehicle
     */
    def select() {
        Map preResult
        Map executeResult
        Map result
        preResult = (LinkedHashMap) selectVehicleActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        executeResult = (Map) selectVehicleActionService.execute(params, null);
        Boolean selectResult = (Boolean) executeResult.isError
        if (!selectResult.booleanValue()) {
            result = (LinkedHashMap) selectVehicleActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectVehicleActionService.buildFailureResultForUI(executeResult);
        }
        render result as JSON
    }
    /**
     *  Update vehicle
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateVehicleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateVehicleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateVehicleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateVehicleActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateVehicleActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     *  Delete vehicle
     */
    def delete() {
        Map result;
        Map preResult = (Map) deleteVehicleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteVehicleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        Boolean deleteResult = (Boolean) deleteVehicleActionService.execute(params, null);
        if (!deleteResult.booleanValue()) {
            result = (Map) deleteVehicleActionService.buildFailureResultForUI(null);
        } else {
            result = (Map) deleteVehicleActionService.buildSuccessResultForUI(null);
        }
        render(result as JSON)
    }
    /**
     *  List & Search vehicle
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        if (params.query) {
            preResult = (Map) searchVehicleActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchVehicleActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchVehicleActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchVehicleActionService.buildFailureResultForUI(null)
            }

        } else {
            preResult = (Map) listVehicleActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listVehicleActionService.execute(params, null)
            if (executeResult) {
                result = (Map) listVehicleActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listVehicleActionService.buildFailureResultForUI(null)
            }
        }
        render(result as JSON)
    }
}
