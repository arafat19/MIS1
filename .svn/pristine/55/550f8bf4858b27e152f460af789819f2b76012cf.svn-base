package com.athena.mis.inventory.controller

import com.athena.mis.inventory.actions.invinventorytransaction.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class InvInventoryTransactionController {

    static allowedMethods = [
            // For Inventory Out
            showInventoryOut: "POST",
            createInventoryOut: "POST",
            selectInventoryOut: "POST",
            updateInventoryOut: "POST",
            deleteInventoryOut: "POST",
            listInventoryOut: "POST",

            // For Inventory production
            showInvProductionWithConsumption: "POST",
            createInvProductionWithConsumption: "POST",
            selectInvProductionWithConsumption: "POST",
            updateInvProductionWithConsumption: "POST",
            listInvProductionWithConsumption: "POST",
            deleteInvProductionWithConsumption: "POST",
            adjustInvProductionWithConsumption: "POST",
            reverseAdjust: "POST",

            // For Inventory In from Supplier
            showInventoryInFromSupplier: "POST",
            createInventoryInFromSupplier: "POST",
            selectInventoryInFromSupplier: "POST",
            updateInventoryInFromSupplier: "POST",
            deleteInventoryInFromSupplier: "POST",
            listInventoryInFromSupplier: "POST",

            listInventoryByType: "POST",
            listPOBySupplier: "POST",
            listAllInventoryByType: "POST",
            listInventoryIsFactoryByType: "POST",

            // For Inventory In from Inventory
            showInventoryInFromInventory: "POST",
            createInventoryInFromInventory: "POST",
            selectInventoryInFromInventory: "POST",
            updateInventoryInFromInventory: "POST",
            deleteInventoryInFromInventory: "POST",
            listInventoryInFromInventory: "POST",

            listInventoryOfTransactionOut: "POST",
            listInvTransaction: "POST",

            // For Inventory Consumption
            showInventoryConsumption: "POST",
            createInventoryConsumption: "POST",
            selectInventoryConsumption: "POST",
            updateInventoryConsumption: "POST",
            deleteInventoryConsumption: "POST",
            listInventoryConsumption: "POST",
            listOfUnApprovedConsumption: "POST",
            approveInvProdWithConsumption: "POST",

            //For Approved Production with Consumption
            showApprovedProdWithConsump: "POST",
            listApprovedProdWithConsump: "POST",

            listInventoryByTypeAndProject: "POST",
            listFixedAssetByItemAndProject: "POST",

            // For ReCalculateAllValuation
            showReCalculateValuation: "POST",
            reCalculateValuation: "POST",
            // sendMailForInventoryTransaction
            sendMailForInventoryTransaction: "POST"
    ]

    //For Unapproved Inv. Production With Consumption
    ShowForInvProductionWithConsumptionActionService showForInvProductionWithConsumptionActionService
    CreateForInvProductionWithConsumptionActionService createForInvProductionWithConsumptionActionService
    UpdateForInvProductionWithConsumptionActionService updateForInvProductionWithConsumptionActionService
    SelectForInvProductionWithConsumptionActionService selectForInvProductionWithConsumptionActionService
    DeleteForInvProductionWithConsumptionActionService deleteForInvProductionWithConsumptionActionService
    ListForInvProductionWithConsumptionActionService listForInvProductionWithConsumptionActionService
    SearchForInvProductionWithConsumptionActionService searchForInvProductionWithConsumptionActionService

    AdjustForInvProductionWithConsumptionActionService adjustForInvProductionWithConsumptionActionService
    ReverseAdjustmentForInvProdWithConsumpActionService reverseAdjustmentForInvProdWithConsumpActionService

    //For Unapproved Inv. Production With Consumption
    ShowForApprovedProductionWithConsumptionActionService showForApprovedProductionWithConsumptionActionService
    ListForApprovedProductionWithConsumptionActionService listForApprovedProductionWithConsumptionActionService
    SearchForApprovedProductionWithConsumptionActionService searchForApprovedProductionWithConsumptionActionService

    //////*********** Inventory Out *********\\\\\\\\\\\\\\\\\\\\\
    CreateForInventoryOutActionService createForInventoryOutActionService
    ShowForInventoryOutActionService showForInventoryOutActionService
    ListForInventoryOutActionService listForInventoryOutActionService
    SelectForInventoryOutActionService selectForInventoryOutActionService
    UpdateForInventoryOutActionService updateForInventoryOutActionService
    DeleteForInventoryOutActionService deleteForInventoryOutActionService
    SearchForInventoryOutActionService searchForInventoryOutActionService
    GetInventoryListIsFactoryByInvTypeActionService getInventoryListIsFactoryByInvTypeActionService

    //////*********** Inventory In From Supplier *********\\\\\\\\\\\\\\\\\\\\\
    ShowForInventoryInFromSupplierActionService showForInventoryInFromSupplierActionService
    CreateForInventoryInFromSupplierActionService createForInventoryInFromSupplierActionService
    SelectForInventoryInFromSupplierActionService selectForInventoryInFromSupplierActionService
    UpdateForInventoryInFromSupplierActionService updateForInventoryInFromSupplierActionService
    DeleteForInventoryInFromSupplierActionService deleteForInventoryInFromSupplierActionService
    SearchForInventoryInFromSupplierActionService searchForInventoryInFromSupplierActionService
    ListForInventoryInFromSupplierActionService listForInventoryInFromSupplierActionService
    GetInventoryListByInventoryTypeActionService getInventoryListByInventoryTypeActionService
    GetPurchaseOrderActionService getPurchaseOrderActionService
    GetAllInventoryListByInventoryTypeActionService getAllInventoryListByInventoryTypeActionService

    //////*********** Inventory In From Inventory *********\\\\\\\\\\\\\\\\\\\\\
    ShowForInventoryInFromInventoryActionService showForInventoryInFromInventoryActionService
    CreateForInventoryInFromInventoryActionService createForInventoryInFromInventoryActionService
    DeleteForInventoryInFromInventoryActionService deleteForInventoryInFromInventoryActionService
    UpdateForInventoryInFromInventoryActionService updateForInventoryInFromInventoryActionService
    SelectForInventoryInFromInventoryActionService selectForInventoryInFromInventoryActionService
    ListForInventoryInFromInventoryActionService listForInventoryInFromInventoryActionService
    SearchForInventoryInFromInventoryActionService searchForInventoryInFromInventoryActionService
    GetForInvListOfTransactionOutActionService getForInvListOfTransactionOutActionService
    GetForInvTransactionListActionService getForInvTransactionListActionService

    //////*********** Inventory Consumption *********\\\\\\\\\\\\\\\\\\\\\
    ShowForInventoryConsumptionActionService showForInventoryConsumptionActionService
    CreateForInventoryConsumptionActionService createForInventoryConsumptionActionService
    DeleteForInventoryConsumptionActionService deleteForInventoryConsumptionActionService
    UpdateForInventoryConsumptionActionService updateForInventoryConsumptionActionService
    SelectForInventoryConsumptionActionService selectForInventoryConsumptionActionService
    ListForInventoryConsumptionActionService listForInventoryConsumptionActionService
    SearchForInventoryConsumptionActionService searchForInventoryConsumptionActionService

    ListUnApprovedConsumptionActionService listUnApprovedConsumptionActionService
    ListUnApprovedInFromSupplierActionService listUnApprovedInFromSupplierActionService
    ListUnApprovedInventoryOutActionService listUnApprovedInventoryOutActionService
    ListUnApprovedInFromInventoryActionService listUnApprovedInFromInventoryActionService

    ApproveForInvProductionWithConsumptionActionService approveForInvProductionWithConsumptionActionService
    GetInventoryListByInvTypeAndProjectActionService getInventoryListByInvTypeAndProjectActionService

    GetFixedAssetListByItemAndProjectActionService getFixedAssetListByItemAndProjectActionService

    // -------------- For ReCalculateAllValuation --------------
    RecalculateAllInvInventoryValuationActionService recalculateAllInvInventoryValuationActionService
    // send mail to authorized person
    SendMailForInventoryTransactionActionService sendMailForInventoryTransactionActionService

    // ********** For Unapproved Inv Production With Consumption ******** \\
    def showInvProductionWithConsumption() {
        Map result
        Map executeResult = (Map) showForInvProductionWithConsumptionActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/inventory/invInventoryTransaction/showProduction', model: [output: result as JSON])
    }

    def createInvProductionWithConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInvProductionWithConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInvProductionWithConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInvProductionWithConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def updateInvProductionWithConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateForInvProductionWithConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInvProductionWithConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInvProductionWithConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def deleteInvProductionWithConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) deleteForInvProductionWithConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInvProductionWithConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInvProductionWithConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) deleteForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def listInvProductionWithConsumption() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForInvProductionWithConsumptionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)

        } else { // normal listing
            executeResult = (LinkedHashMap) listForInvProductionWithConsumptionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output

    }

    def selectInvProductionWithConsumption() {
        LinkedHashMap result
        String output
        LinkedHashMap executeResult = (LinkedHashMap) selectForInvProductionWithConsumptionActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def approveInvProdWithConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) approveForInvProductionWithConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInvProductionWithConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) approveForInvProductionWithConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) approveForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    // ********** For Approved Inv Production With Consumption ******** \\
    def showApprovedProdWithConsump() {
        Map result
        Map executeResult = (Map) showForApprovedProductionWithConsumptionActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showForApprovedProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showForApprovedProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/inventory/invInventoryTransaction/showApprovedProdWithConsump', model: [output: result as JSON])
    }

    def listApprovedProdWithConsump() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForApprovedProductionWithConsumptionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForApprovedProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchForApprovedProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)

        } else { // normal listing
            executeResult = (LinkedHashMap) listForApprovedProductionWithConsumptionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForApprovedProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForApprovedProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output

    }

    //////*********** Inventory Out *********\\\\\\
    def showInventoryOut() {
        if (request.method == UIConstants.REQUEST_METHOD_GET) {
            render(view: UIConstants.NON_AJAX_TEMPLATE_URI, model: [page: g.createLink(action: 'showInventoryOut')])
            return;
        }

        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForInventoryOutActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (LinkedHashMap) showForInventoryOutActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) showForInventoryOutActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/inventory/invInventoryTransaction/showInventoryTransactionOut', model: [output: result as JSON])
    }
    def createInventoryOut() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInventoryOutActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryOutActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInventoryOutActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryOutActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createForInventoryOutActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
    def listInventoryOut() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForInventoryOutActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForInventoryOutActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForInventoryOutActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForInventoryOutActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForInventoryOutActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForInventoryOutActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output


    }
    def selectInventoryOut() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectForInventoryOutActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryOutActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) selectForInventoryOutActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryOutActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectForInventoryOutActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def updateInventoryOut() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateForInventoryOutActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryOutActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryOutActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryOutActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateForInventoryOutActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def deleteInventoryOut() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryOutActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryOutActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryOutActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryOutActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) deleteForInventoryOutActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    //##############closure below for manik & parvez#######################

    //////*********** Inventory In From Supplier *********\\\\\\

    def showInventoryInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForInventoryInFromSupplierActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render(view: '/inventory/invInventoryTransaction/showInventoryInFromSupplier', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render(view: '/inventory/invInventoryTransaction/showInventoryInFromSupplier', model: [output: output])
    }

    def createInventoryInFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInventoryInFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInFromSupplierActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createForInventoryInFromSupplierActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def selectInventoryInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectForInventoryInFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInFromSupplierActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) selectForInventoryInFromSupplierActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def updateInventoryInFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateForInventoryInFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInFromSupplierActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) updateForInventoryInFromSupplierActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) updateForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def deleteInventoryInFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryInFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInFromSupplierActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) deleteForInventoryInFromSupplierActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) deleteForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listInventoryInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForInventoryInFromSupplierActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForInventoryInFromSupplierActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForInventoryInFromSupplierActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForInventoryInFromSupplierActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def listInventoryByType() {
        String output
        Map result
        Map preResult = (Map) getInventoryListByInventoryTypeActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getInventoryListByInventoryTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getInventoryListByInventoryTypeActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getInventoryListByInventoryTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getInventoryListByInventoryTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listInventoryByTypeAndProject() {
        String output
        Map result
        Map preResult = (Map) getInventoryListByInvTypeAndProjectActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getInventoryListByInvTypeAndProjectActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getInventoryListByInvTypeAndProjectActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getInventoryListByInvTypeAndProjectActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getInventoryListByInvTypeAndProjectActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listFixedAssetByItemAndProject() {
        Map executeResult = ((Map) getFixedAssetListByItemAndProjectActionService.execute(params, null))
        render executeResult as JSON
    }

    def listAllInventoryByType() {
        String output
        Map result
        Map preResult = (Map) getAllInventoryListByInventoryTypeActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getAllInventoryListByInventoryTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getAllInventoryListByInventoryTypeActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getAllInventoryListByInventoryTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getAllInventoryListByInventoryTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listPOBySupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) getPurchaseOrderActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getPurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getPurchaseOrderActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getPurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def listInventoryIsFactoryByType() {
        String output
        Map result
        Map preResult = (Map) getInventoryListIsFactoryByInvTypeActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getInventoryListIsFactoryByInvTypeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getInventoryListIsFactoryByInvTypeActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getInventoryListIsFactoryByInvTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getInventoryListIsFactoryByInvTypeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    //////*********** Inventory In From Inventory *********\\\\\\

    def showInventoryInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForInventoryInFromInventoryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render(view: '/inventory/invInventoryTransaction/showInventoryInFromInventory', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render(view: '/inventory/invInventoryTransaction/showInventoryInFromInventory', model: [output: output])
    }

    def createInventoryInFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInventoryInFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInFromInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createForInventoryInFromInventoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output

    }

    def selectInventoryInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectForInventoryInFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInFromInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (LinkedHashMap) selectForInventoryInFromInventoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def updateInventoryInFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateForInventoryInFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInFromInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryInFromInventoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def deleteInventoryInFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) deleteForInventoryInFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInFromInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryInFromInventoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) deleteForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def listInventoryInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForInventoryInFromInventoryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)

        } else { // normal listing
            executeResult = (LinkedHashMap) listForInventoryInFromInventoryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForInventoryInFromInventoryActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForInventoryInFromInventoryActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def listInventoryOfTransactionOut() {
        String output
        Map result
        Map preResult = (Map) getForInvListOfTransactionOutActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getForInvListOfTransactionOutActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getForInvListOfTransactionOutActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getForInvListOfTransactionOutActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getForInvListOfTransactionOutActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listInvTransaction() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) getForInvTransactionListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForInvTransactionListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getForInvTransactionListActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForInvTransactionListActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getForInvTransactionListActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /************************************ Inventory Consumption ************************************/
    def showInventoryConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) showForInventoryConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForInventoryConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(view: '/inventory/invInventoryTransaction/showInventoryConsumption', model: [output: output])
            return
        }

        executeResult = (LinkedHashMap) showForInventoryConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render(view: '/inventory/invInventoryTransaction/showInventoryConsumption', model: [output: output])
            return
        }
        result = (LinkedHashMap) showForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render(view: '/inventory/invInventoryTransaction/showInventoryConsumption', model: [output: output])
        return
    }

    def createInventoryConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) createForInventoryConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInventoryConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def selectInventoryConsumption() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectForInventoryConsumptionActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def deleteInventoryConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deleteForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def updateInventoryConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateForInventoryConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def listInventoryConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForInventoryConsumptionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)

        } else { // normal listing
            executeResult = (LinkedHashMap) listForInventoryConsumptionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForInventoryConsumptionActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForInventoryConsumptionActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    /************************************ end of Inventory Consumption ************************************/

    // For Getting List of Unapproved Consumption at dash board
    def listOfUnApprovedConsumption() {
        Map result
        Map executeResult = (Map) listUnApprovedConsumptionActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedConsumptionActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedConsumptionActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    // For Getting List of Unapproved In from Supplier at dash board
    def listOfUnApprovedInFromSupplier() {
        Map result
        Map executeResult = (Map) listUnApprovedInFromSupplierActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedInFromSupplierActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedInFromSupplierActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    // For Getting List of Unapproved Inventory Out at dash board
    def listOfUnApprovedInventoryOut() {
        Map result
        Map executeResult = (Map) listUnApprovedInventoryOutActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedInventoryOutActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedInventoryOutActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    // For Getting List of Unapproved In From Inventory at dash board
    def listOfUnApprovedInFromInventory() {
        Map result
        Map executeResult = (Map) listUnApprovedInFromInventoryActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedInFromInventoryActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedInFromInventoryActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    //For Adjustment
    def adjustInvProductionWithConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) adjustForInvProductionWithConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustForInvProductionWithConsumptionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) adjustForInvProductionWithConsumptionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustForInvProductionWithConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) adjustForInvProductionWithConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    //For Reverse Adjustment (Inventory-Production-With-Consumption)
    def reverseAdjust() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) reverseAdjustmentForInvProdWithConsumpActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustmentForInvProdWithConsumpActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) reverseAdjustmentForInvProdWithConsumpActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustmentForInvProdWithConsumpActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) reverseAdjustmentForInvProdWithConsumpActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    // -------------- For ReCalculateAllValuation --------------
    def showReCalculateValuation () {
        render(view: '/inventory/reCalculateValuation/show')
    }

    def reCalculateValuation() {
        Map result
        Boolean isError
        Map preResult = (Map) recalculateAllInvInventoryValuationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) recalculateAllInvInventoryValuationActionService.buildFailureResultForUI(preResult)
            return result
        }
        Map executeResult = (Map) recalculateAllInvInventoryValuationActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) recalculateAllInvInventoryValuationActionService.buildFailureResultForUI(executeResult)
            return result
        } else {
            result = (Map) recalculateAllInvInventoryValuationActionService.buildSuccessResultForUI(executeResult)
        }
        String output= result as JSON
        render output
    }

    // send mail to authorized persons with all pending inventory transaction report
    def sendMailForInventoryTransaction() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) sendMailForInventoryTransactionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForInventoryTransactionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) sendMailForInventoryTransactionActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForInventoryTransactionActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) sendMailForInventoryTransactionActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
}
