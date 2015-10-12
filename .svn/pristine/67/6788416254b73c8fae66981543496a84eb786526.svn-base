package com.athena.mis.fixedassetdetails.controller

import com.athena.mis.fixedasset.actions.fxdmaintenance.*
import grails.converters.JSON

class FxdMaintenanceController {

    static allowedMethods = [
            create: "POST",
            select: "POST",
            update: "POST",
            delete: 'POST',
            list: "POST",
            getMaintenanceTypeAndModelListByItemId: "POST"
    ]

    FxdShowMaintenanceActionService fxdShowMaintenanceActionService
    FxdCreateMaintenanceActionService fxdCreateMaintenanceActionService
    FxdUpdateMaintenanceActionService fxdUpdateMaintenanceActionService
    FxdDeleteMaintenanceActionService fxdDeleteMaintenanceActionService
    FxdListMaintenanceActionService fxdListMaintenanceActionService
    FxdSelectMaintenanceActionService fxdSelectMaintenanceActionService
    FxdSearchMaintenanceActionService fxdSearchMaintenanceActionService
    FxdGetMaintenanceTypeAndModelListByItemIdActionService fxdGetMaintenanceTypeAndModelListByItemIdActionService
    /**
     *
     * @return - render maintenance page with maintenance-type and fxd item list & maintenance type list
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) fxdShowMaintenanceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdShowMaintenanceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdShowMaintenanceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render(view: '/fixedAsset/fxdMaintenance/show', model: [output: output])
    }
    /**
     *
     * @return - newly created maintenance object
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdCreateMaintenanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdCreateMaintenanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdCreateMaintenanceActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdCreateMaintenanceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdCreateMaintenanceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - object of selected maintenance
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdSelectMaintenanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdSelectMaintenanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdSelectMaintenanceActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdSelectMaintenanceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdSelectMaintenanceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return  list of maintenance including updated one
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdUpdateMaintenanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdUpdateMaintenanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) fxdUpdateMaintenanceActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdUpdateMaintenanceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdUpdateMaintenanceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return- maintenance object excluding deleted one
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdDeleteMaintenanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdDeleteMaintenanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) fxdDeleteMaintenanceActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdDeleteMaintenanceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdDeleteMaintenanceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - list of maintenance
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Boolean isError
        Map result
        Map executeResult
        if (params.query) {
            executeResult = (Map) fxdSearchMaintenanceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) fxdSearchMaintenanceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) fxdSearchMaintenanceActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) fxdListMaintenanceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) fxdListMaintenanceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) fxdListMaintenanceActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
    /**
     *
     * @return - list of maintenance type
     */
    def getMaintenanceTypeAndModelListByItemId() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdGetMaintenanceTypeAndModelListByItemIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdGetMaintenanceTypeAndModelListByItemIdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdGetMaintenanceTypeAndModelListByItemIdActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdGetMaintenanceTypeAndModelListByItemIdActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdGetMaintenanceTypeAndModelListByItemIdActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
