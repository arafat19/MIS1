package com.athena.mis.fixedassetdetails.controller

import com.athena.mis.fixedasset.actions.fxdmaintenancetype.*
import com.athena.mis.fixedasset.entity.FxdMaintenanceType
import grails.converters.JSON

class FxdMaintenanceTypeController {

    static allowedMethods = [
            create: "POST",
            select: "POST",
            update: "POST",
            delete: 'POST',
            list: "POST"
    ]

    FxdShowFxdMaintenanceTypeActionService fxdShowFxdMaintenanceTypeActionService
    FxdCreateFxdMaintenanceTypeActionService fxdCreateFxdMaintenanceTypeActionService
    FxdUpdateFxdMaintenanceTypeActionService fxdUpdateFxdMaintenanceTypeActionService
    FxdDeleteFxdMaintenanceTypeActionService fxdDeleteFxdMaintenanceTypeActionService
    FxdListFxdMaintenanceTypeActionService fxdListFxdMaintenanceTypeActionService
    FxdSelectFxdMaintenanceTypeActionService fxdSelectFxdMaintenanceTypeActionService
    FxdSearchFxdMaintenanceTypeActionService fxdSearchFxdMaintenanceTypeActionService
    /**
     *
     * @return - render maintenance type page with maintenance-type list
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) fxdShowFxdMaintenanceTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdShowFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdShowFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render(view: '/fixedAsset/fixedMaintenanceType/show', model: [output: output])
    }
    /**
     *
     * @return - newly created maintenance type object
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdCreateFxdMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdCreateFxdMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdCreateFxdMaintenanceTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdCreateFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdCreateFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - object of selected maintenance type
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdSelectFxdMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdSelectFxdMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdSelectFxdMaintenanceTypeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdSelectFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdSelectFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return  list of maintenance type including updated one
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdUpdateFxdMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdUpdateFxdMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) fxdUpdateFxdMaintenanceTypeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdUpdateFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdUpdateFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return- maintenance type object excluding deleted one
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdDeleteFxdMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdDeleteFxdMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) fxdDeleteFxdMaintenanceTypeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdDeleteFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdDeleteFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - list of maintenance type
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Boolean isError
        Map result
        Map executeResult
        if (params.query) {
            executeResult = (Map) fxdSearchFxdMaintenanceTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) fxdSearchFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) fxdSearchFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) fxdListFxdMaintenanceTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) fxdListFxdMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) fxdListFxdMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
