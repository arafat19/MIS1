package com.athena.mis.fixedassetdetails.controller

import com.athena.mis.fixedasset.actions.fxdcategorymaintenancetype.*
import grails.converters.JSON

class FxdCategoryMaintenanceTypeController {

    static allowedMethods = [
            create: "POST",
            select: "POST",
            update: "POST",
            delete: 'POST',
            list: "POST",
            dropDownFxdMaintenanceTypeReload: "POST"
    ]

    FxdShowCategoryMaintenanceTypeActionService fxdShowCategoryMaintenanceTypeActionService
    FxdCreateCategoryMaintenanceTypeActionService fxdCreateCategoryMaintenanceTypeActionService
    FxdUpdateCategoryMaintenanceTypeActionService fxdUpdateCategoryMaintenanceTypeActionService
    FxdDeleteCategoryMaintenanceTypeActionService fxdDeleteCategoryMaintenanceTypeActionService
    FxdListCategoryMaintenanceTypeActionService fxdListCategoryMaintenanceTypeActionService
    FxdSelectCategoryMaintenanceTypeActionService fxdSelectCategoryMaintenanceTypeActionService
    FxdSearchCategoryMaintenanceTypeActionService fxdSearchCategoryMaintenanceTypeActionService
    /**
     *
     * @return - render category maintenance type page with category-maintenance-type and fxd item list
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap preResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdShowCategoryMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdShowCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdShowCategoryMaintenanceTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdShowCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdShowCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        Long fxdItemId = (Long) result.fxdItemId
        render(view: '/fixedAsset/fxdCategoryMaintenanceType/show', model: [fxdItemId: fxdItemId, output: output])
    }
    /**
     *
     * @return - newly created category maintenance type object
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdCreateCategoryMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdCreateCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdCreateCategoryMaintenanceTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdCreateCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdCreateCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - object of selected category maintenance type
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdSelectCategoryMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdSelectCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) fxdSelectCategoryMaintenanceTypeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdSelectCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdSelectCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return  list of category maintenance type including updated one
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) fxdUpdateCategoryMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdUpdateCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) fxdUpdateCategoryMaintenanceTypeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdUpdateCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdUpdateCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return- category maintenance type object excluding deleted one
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) fxdDeleteCategoryMaintenanceTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdDeleteCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) fxdDeleteCategoryMaintenanceTypeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) fxdDeleteCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) fxdDeleteCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - list of category maintenance type
     */
    def list() {
        Map preResult
        Boolean isError
        Map result
        Map executeResult
        if (params.query) {
            preResult = (LinkedHashMap) fxdSearchCategoryMaintenanceTypeActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) fxdSearchCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
                render (result as JSON)
                return
            }
            executeResult = (Map) fxdSearchCategoryMaintenanceTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) fxdSearchCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) fxdSearchCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            preResult = (LinkedHashMap) fxdListCategoryMaintenanceTypeActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) fxdListCategoryMaintenanceTypeActionService.buildFailureResultForUI(preResult)
                render (result as JSON)
                return
            }
            executeResult = (Map) fxdListCategoryMaintenanceTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) fxdListCategoryMaintenanceTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) fxdListCategoryMaintenanceTypeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    def dropDownFxdMaintenanceTypeReload(){
        render fxd.dropDownFxdMaintenanceType(params)
    }
}
