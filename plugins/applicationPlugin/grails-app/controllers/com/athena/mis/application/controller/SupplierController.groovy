package com.athena.mis.application.controller

import com.athena.mis.application.actions.supplier.*
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired

class SupplierController {

    static allowedMethods = [show: "POST", edit: "POST", create: "POST",
            update: "POST", delete: "POST", list: "POST",
            getAllSupplierList: "POST"];

    @Autowired
    AppSessionUtil appSessionUtil

    ShowSupplierActionService showSupplierActionService
    CreateSupplierActionService createSupplierActionService
    SelectSupplierActionService selectSupplierActionService
    UpdateSupplierActionService updateSupplierActionService
    DeleteSupplierActionService deleteSupplierActionService
    SearchSupplierActionService searchSupplierActionService
    ListSupplierActionService listSupplierActionService
    GetSupplierListActionService getSupplierListActionService

    def show() {
        Map preResult = (Map) showSupplierActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showSupplierActionService.execute(params, null)
        if (executeResult) {
            executeResult = (Map) showSupplierActionService.buildSuccessResultForUI(executeResult)
        } else {
            executeResult = (Map) showSupplierActionService.buildFailureResultForUI(null)
        }
        render(view: '/application/supplier/show', model: [output: executeResult as JSON])
    }

    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createSupplierActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSupplierActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createSupplierActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSupplierActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createSupplierActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def select() {

        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        preResult = (LinkedHashMap) selectSupplierActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        executeResult = (LinkedHashMap) selectSupplierActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSupplierActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectSupplierActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateSupplierActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSupplierActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateSupplierActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSupplierActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateSupplierActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def delete() {

        Map result
        Map preResult = (Map) deleteSupplierActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteSupplierActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        boolean deleteResult = ((Boolean) deleteSupplierActionService.execute(params, null))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteSupplierActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteSupplierActionService.buildFailureResultForUI(deleteResult)
        }
        render(result as JSON)
    }

    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        if (params.query) {
            preResult = (Map) searchSupplierActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchSupplierActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchSupplierActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchSupplierActionService.buildFailureResultForUI(null)
            }

        } else {
            preResult = (Map) listSupplierActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listSupplierActionService.execute(params, null)
            if (executeResult) {
                result = (Map) listSupplierActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listSupplierActionService.buildFailureResultForUI(null)
            }
        }
        render(result as JSON)
    }

    /* get wrappedSupplierList to show on grid
       Used to populate right grid on Procurement->SupplierWise PO report
     */

    def getAllSupplierList() {
        LinkedHashMap result
        String output

        LinkedHashMap executeResult = (LinkedHashMap) getSupplierListActionService.execute(params, null)
        if (!executeResult) {
            result = (LinkedHashMap) getSupplierListActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getSupplierListActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
}
