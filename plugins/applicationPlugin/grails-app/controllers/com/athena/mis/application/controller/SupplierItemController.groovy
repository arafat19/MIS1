package com.athena.mis.application.controller

import com.athena.mis.application.actions.supplieritem.*
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired

class SupplierItemController {

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST",
            list: "POST", select: "POST", delete: "POST",
            getItemListForSupplierItem: "POST"
    ]

    ShowSupplierItemActionService showSupplierItemActionService
    CreateSupplierItemActionService createSupplierItemActionService
    UpdateSupplierItemActionService updateSupplierItemActionService
    DeleteSupplierItemActionService deleteSupplierItemActionService
    SelectSupplierItemActionService selectSupplierItemActionService
    ListSupplierItemActionService listSupplierItemActionService
    SearchSupplierItemActionService searchSupplierItemActionService
    GetItemListSupplierItemActionService getItemListSupplierItemActionService

    @Autowired
    AppSessionUtil appSessionUtil

    def show() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        preResult = (LinkedHashMap) showSupplierItemActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showSupplierItemActionService.buildFailureResultForUI(preResult)
            renderShow(result)
            return
        }

        executeResult = (LinkedHashMap) showSupplierItemActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showSupplierItemActionService.buildFailureResultForUI(executeResult)
            renderShow(result)
            return
        }
        result = (LinkedHashMap) showSupplierItemActionService.buildSuccessResultForUI(executeResult)
        renderShow(result)
    }

    private renderShow(Map result) {
        String output = result as JSON
        render(view: '/application/supplierItem/show', model: [output: output])
    }

    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createSupplierItemActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSupplierItemActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createSupplierItemActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSupplierItemActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createSupplierItemActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchSupplierItemActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchSupplierItemActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }

            result = (LinkedHashMap) searchSupplierItemActionService.buildSuccessResultForUI(executeResult)

        } else { // normal listing

            executeResult = (LinkedHashMap) listSupplierItemActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listSupplierItemActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return
            }
            result = (LinkedHashMap) listSupplierItemActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectSupplierItemActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSupplierItemActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) selectSupplierItemActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }


    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateSupplierItemActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSupplierItemActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateSupplierItemActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSupplierItemActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateSupplierItemActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }


    def delete() {
        Map result
        Boolean isError
        Map preResult = (Map) deleteSupplierItemActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteSupplierItemActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) deleteSupplierItemActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteSupplierItemActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteSupplierItemActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def getItemListForSupplierItem() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) getItemListSupplierItemActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getItemListSupplierItemActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) getItemListSupplierItemActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getItemListSupplierItemActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) getItemListSupplierItemActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output

    }

}
