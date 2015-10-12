package com.athena.mis.inventory.controller

import com.athena.mis.inventory.actions.invinventory.*
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class InvInventoryController {
    static allowedMethods = [
            show: "POST",
            create: "POST",
            select: "POST",
            update: "POST",
            delete: "POST",
            list: "POST"
    ];

    ShowInvInventoryActionService showInvInventoryActionService
    CreateInvInventoryActionService createInvInventoryActionService
    SelectInvInventoryActionService selectInvInventoryActionService
    UpdateInvInventoryActionService updateInvInventoryActionService
    ListInvInventoryActionService listInvInventoryActionService
    SearchInvInventoryActionService searchInvInventoryActionService
    DeleteInvInventoryActionService deleteInvInventoryActionService

    def show() {
        Map preResult = (Map) showInvInventoryActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showInvInventoryActionService.execute(params, null)
        Map result;
        if (executeResult) {
            result = (Map) showInvInventoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showInvInventoryActionService.buildFailureResultForUI(null);
        }
        render(view: '/inventory/invInventory/show', model: [output: result as JSON])
    }

    def create() {
        Map result
        String output
        InvInventory inventoryInstance = new InvInventory(params);
        Map preResult = (Map) createInvInventoryActionService.executePreCondition(params, inventoryInstance)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createInvInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        Map executeResult = (Map) createInvInventoryActionService.execute(params, preResult)
        if (executeResult) {
            result = (Map) createInvInventoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createInvInventoryActionService.buildFailureResultForUI(inventoryInstance)
        }
        render(result as JSON)
    }

    def select() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        preResult = (LinkedHashMap) selectInvInventoryActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        executeResult = (LinkedHashMap) selectInvInventoryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectInvInventoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectInvInventoryActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateInvInventoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateInvInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateInvInventoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateInvInventoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateInvInventoryActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteInvInventoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteInvInventoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        boolean deleteResult = ((Boolean) deleteInvInventoryActionService.execute(params, preResult))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteInvInventoryActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteInvInventoryActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    def list() {
        Map preResult;
        Boolean hasAccess;
        Map result;
        Map executeResult;
        if (params.query) {
            preResult = (Map) searchInvInventoryActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchInvInventoryActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchInvInventoryActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchInvInventoryActionService.buildFailureResultForUI(null);
            }

        } else {
            preResult = (Map) listInvInventoryActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listInvInventoryActionService.execute(params, null);
            if (executeResult) {
                result = (Map) listInvInventoryActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listInvInventoryActionService.buildFailureResultForUI(null);
            }
        }
        render(result as JSON);
    }
}

