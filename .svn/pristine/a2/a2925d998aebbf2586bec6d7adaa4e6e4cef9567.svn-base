package com.athena.mis.inventory.controller

import com.athena.mis.inventory.actions.invinventorytransaction.ListUnApprovedConsumptionActionService
import com.athena.mis.inventory.actions.invinventorytransaction.ListUnApprovedInFromInventoryActionService
import com.athena.mis.inventory.actions.invinventorytransaction.ListUnApprovedInFromSupplierActionService
import com.athena.mis.inventory.actions.invinventorytransaction.ListUnApprovedInventoryOutActionService
import grails.converters.JSON
import grails.util.Environment

class InventoryController {
    ListUnApprovedConsumptionActionService listUnApprovedConsumptionActionService
    ListUnApprovedInFromSupplierActionService listUnApprovedInFromSupplierActionService
    ListUnApprovedInventoryOutActionService listUnApprovedInventoryOutActionService
    ListUnApprovedInFromInventoryActionService listUnApprovedInFromInventoryActionService

    static allowedMethods = [renderInventoryMenu: "POST"]

    def springSecurityService

    // Used to render left menu and dash board of Inventory
    def renderInventoryMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        // Now pull data for Dash board tab (List of Unapproved Consumption)
        Map resultCon
        Map executeResult
        Boolean isError

        executeResult = (Map) listUnApprovedConsumptionActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultCon = (Map) listUnApprovedConsumptionActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultCon = (Map) listUnApprovedConsumptionActionService.buildFailureResultForUI(executeResult);
        }
        String outputCon = resultCon as JSON

        // Now pull data for Dash board tab (List of Unapproved In from Supplier)
        Map resultInFromSupplier
        executeResult = (Map) listUnApprovedInFromSupplierActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultInFromSupplier = (Map) listUnApprovedInFromSupplierActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultInFromSupplier = (Map) listUnApprovedInFromSupplierActionService.buildFailureResultForUI(executeResult);
        }
        String outputSupplier = resultInFromSupplier as JSON

        // Now pull data for Dash board tab (List of Unapproved Inventory Out)
        Map resultInvOut
        executeResult = (Map) listUnApprovedInventoryOutActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultInvOut = (Map) listUnApprovedInventoryOutActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultInvOut = (Map) listUnApprovedInventoryOutActionService.buildFailureResultForUI(executeResult);
        }
        String outputInvOut = resultInvOut as JSON

        // Now pull data for Dash board tab (List of Unapproved In from Inventory)
        Map resultInFromInventory
        executeResult = (Map) listUnApprovedInFromInventoryActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultInFromInventory = (Map) listUnApprovedInFromInventoryActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultInFromInventory = (Map) listUnApprovedInFromInventoryActionService.buildFailureResultForUI(executeResult);
        }
        String outputInFromInventory = resultInFromInventory as JSON

        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'inventory',template: '/inventory/leftmenuInventory')])
                element([name: 'dashBoard', content: g.render(plugin: 'inventory',template: '/inventory/dashBoardInventory', model: [outputCon: outputCon, outputSupplier: outputSupplier, outputInvOut: outputInvOut, outputInFromInventory: outputInFromInventory])])
            }
        }
    }
}
