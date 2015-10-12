package com.athena.mis.inventory.controller

import com.athena.mis.inventory.actions.invinventorytransactiondetails.*
import com.athena.mis.inventory.actions.invmodifyoverheadcost.GetInvProdFinishedMaterialByLineItemIdActionService
import com.athena.mis.inventory.actions.invmodifyoverheadcost.SearchInvModifyOverheadCostActionService
import com.athena.mis.inventory.actions.invmodifyoverheadcost.ShowInvModifyOverheadCostActionService
import com.athena.mis.inventory.actions.invmodifyoverheadcost.UpdateInvModifyOverheadCostActionService
import com.athena.mis.inventory.actions.report.invsupplierchalan.AcknowledgeInvoiceFromSupplierActionService
import com.athena.mis.utility.Tools
import grails.converters.JSON

class InvInventoryTransactionDetailsController {

    static allowedMethods = [
            showUnApprovedInventoryOutDetails: 'POST',
            showApprovedInventoryOutDetails: 'POST',
            listUnApprovedInventoryOutDetails: "POST",
            listApprovedInventoryOutDetails: "POST",
            approveInventoryOutDetails: "POST",
            createInventoryOutDetails: 'POST',
            selectInventoryOutDetails: "POST",
            updateInventoryOutDetails: "POST",
            deleteInventoryOutDetails: 'POST',
            dropDownInventoryItemOutReload: 'POST',

            //for inventory in from supplier
            showUnapprovedInvInFromSupplier: "POST",
            listUnapprovedInvInFromSupplier: "POST",
            createInventoryInDetailsFromSupplier: "POST",
            selectInventoryInDetailsFromSupplier: "POST",
            updateInventoryInDetailsFromSupplier: "POST",
            deleteInventoryInDetailsFromSupplier: 'POST',
            approveInventoryInDetailsFromSupplier: "POST",
            showApprovedInvInFromSupplier: "POST",
            listApprovedInvInFromSupplier: "POST",
            dropDownInventoryItemInFromSupplierReload: "POST",

            // For Inventory Consumption
            showUnApprovedInventoryConsumptionDetails: "POST",
            listUnApprovedInventoryConsumptionDetails: "POST",
            showApprovedInventoryConsumptionDetails: "POST",
            listApprovedInventoryConsumptionDetails: "POST",
            createInventoryConsumptionDetails: "POST",
            selectInventoryConsumptionDetails: "POST",
            deleteInventoryConsumptionDetails: "POST",
            updateInventoryConsumptionDetails: "POST",
            dropDownInventoryItemConsumptionReload: "POST",

            //For In from Inventory
            showUnapprovedInvInFromInventory: "POST",
            createInventoryInDetailsFromInventory: "POST",
            updateInventoryInDetailsFromInventory: "POST",
            selectInventoryInDetailsFromInventory: "POST",
            listUnapprovedInvInFromInventory: "POST",
            deleteInventoryInDetailsFromInventory: "POST",
            approveInventoryInDetailsFromInventory: "POST",
            showApprovedInvInFromInventory: "POST",
            listApprovedInvInFromInventory: "POST",
            dropDownInventoryItemInFromInventoryReload: "POST",

            approveInventoryConsumptionDetails: "POST",

            listFixedAssetByInventoryId: "POST",
            listFixedAssetByInventoryIdAndItemId: "POST",

            //adjustment
            //frm supplier
            showAdjustmentInFromSupplier: "POST",
            approveAdjustmentInFromSupplier: "POST",
            createAdjustmentInFromSupplier: "POST",
            deleteAdjustmentInFromSupplier: "POST",
            listAdjustmentForInvTransaction: "POST",
            searchAdjustmentInFromSupplier: "POST",
            selectAdjustmentInFromSupplier: "POST",
            updateAdjustmentInFromSupplier: "POST",

            adjustInvInFromSupplier: "POST",
            adjustInvOut: "POST",
            adjustInvConsumption: "POST",
            reverseAdjustInvConsumption: "POST",

            //reverse adjustment
            reverseAdjustInvInFromInventory: "POST",
            reverseAdjustInvInFromSupplier: "POST",
            reverseAdjustInvOut: "POST",

            //for acknowledge chalan
            acknowledgeInvoiceFromSupplier: "POST",

            //for Production: Modify Overhead Cost
            showInvModifyOverheadCost: "POST",
            searchInvModifyOverheadCost: "POST",
            updateInvModifyOverheadCost: "POST",
            getInvProdFinishedMaterialByLineItemId: "POST",
            dropDownInventoryProductionLineItemReload: "POST"
    ];

    ApproveForInventoryOutDetailsActionService approveForInventoryOutDetailsActionService
    ShowForUnApprovedInventoryOutDetailsActionService showForUnApprovedInventoryOutDetailsActionService
    ShowForApprovedInventoryOutDetailsActionService showForApprovedInventoryOutDetailsActionService
    CreateForInventoryOutDetailsActionService createForInventoryOutDetailsActionService
    UpdateForInventoryOutDetailsActionService updateForInventoryOutDetailsActionService
    DeleteForInventoryOutDetailsActionService deleteForInventoryOutDetailsActionService
    ListForUnApprovedInventoryOutDetailsActionService listForUnApprovedInventoryOutDetailsActionService
    ListForApprovedInventoryOutDetailsActionService listForApprovedInventoryOutDetailsActionService
    SelectForInventoryOutDetailsActionService selectForInventoryOutDetailsActionService
    SearchForUnApprovedInventoryOutDetailsActionService searchForUnApprovedInventoryOutDetailsActionService
    SearchForApprovedInventoryOutDetailsActionService searchForApprovedInventoryOutDetailsActionService

    //inventory in
    CreateForInventoryInDetailsFromSupplierActionService createForInventoryInDetailsFromSupplierActionService
    DeleteForInventoryInDetailsFromSupplierActionService deleteForInventoryInDetailsFromSupplierActionService
    ListForUnapprovedInvInFromSupplierActionService listForUnapprovedInvInFromSupplierActionService
    SearchForUnapprovedInvInFromSupplierActionService searchForUnapprovedInvInFromSupplierActionService
    ShowForUnapprovedInvInFromSupplierActionService showForUnapprovedInvInFromSupplierActionService
    SelectForInventoryInDetailsFromSupplierActionService selectForInventoryInDetailsFromSupplierActionService
    UpdateForInventoryInDetailsFromSupplierActionService updateForInventoryInDetailsFromSupplierActionService
    ApproveForInventoryInDetailsFromSupplierActionService approveForInventoryInDetailsFromSupplierActionService
    ListForApprovedInvInFromSupplierActionService listForApprovedInvInFromSupplierActionService
    SearchForApprovedInvInFromSupplierActionService searchForApprovedInvInFromSupplierActionService
    ShowForApprovedInvInFromSupplierActionService showForApprovedInvInFromSupplierActionService

    CreateForInventoryInDetailsFromInventoryActionService createForInventoryInDetailsFromInventoryActionService
    DeleteForInventoryInDetailsFromInventoryActionService deleteForInventoryInDetailsFromInventoryActionService
    ListForUnapprovedInvInFromInventoryActionService listForUnapprovedInvInFromInventoryActionService
    SearchForUnapprovedInvInFromInventoryActionService searchForUnapprovedInvInFromInventoryActionService
    SelectForInventoryInDetailsFromInventoryActionService selectForInventoryInDetailsFromInventoryActionService
    ShowForUnapprovedInvInFromInventoryActionService showForUnapprovedInvInFromInventoryActionService
    UpdateForInventoryInDetailsFromInventoryActionService updateForInventoryInDetailsFromInventoryActionService
    ApproveForInvInDetailsFromInventoryActionService approveForInvInDetailsFromInventoryActionService
    ListForApprovedInvInFromInventoryActionService listForApprovedInvInFromInventoryActionService
    SearchForApprovedInvInFromInventoryActionService searchForApprovedInvInFromInventoryActionService
    ShowForApprovedInvInFromInventoryActionService showForApprovedInvInFromInventoryActionService

    //////*********** Inventory Consumption *********\\\\\\\\\\\\\\\\\\\\\
    ShowForUnApprovedInventoryConsumptionDetailsActionService showForUnApprovedInventoryConsumptionDetailsActionService
    ShowForApprovedInventoryConsumptionDetailsActionService showForApprovedInventoryConsumptionDetailsActionService
    CreateForInventoryConsumptionDetailsActionService createForInventoryConsumptionDetailsActionService
    DeleteForInventoryConsumptionDetailsActionService deleteForInventoryConsumptionDetailsActionService
    UpdateForInventoryConsumptionDetailsActionService updateForInventoryConsumptionDetailsActionService
    SelectForInventoryConsumptionDetailsActionService selectForInventoryConsumptionDetailsActionService
    ListForUnApprovedInventoryConsumptionDetailsActionService listForUnApprovedInventoryConsumptionDetailsActionService
    SearchForUnApprovedInventoryConsumptionDetailsActionService searchForUnApprovedInventoryConsumptionDetailsActionService
    ListForApprovedInventoryConsumptionDetailsActionService listForApprovedInventoryConsumptionDetailsActionService
    SearchForApprovedInventoryConsumptionDetailsActionService searchForApprovedInventoryConsumptionDetailsActionService

    AdjustmentForInvConsumptionActionService adjustmentForInvConsumptionActionService
    ReverseAdjustmentForInvConsumptionActionService reverseAdjustmentForInvConsumptionActionService

    ApproveForInventoryConsumptionDetailsActionService approveForInventoryConsumptionDetailsActionService
    GetFixedAssetListByInventoryIdActionService getFixedAssetListByInventoryIdActionService
    GetFixedAssetListByInventoryIdAndItemIdActionService getFixedAssetListByInventoryIdAndItemIdActionService

    AdjustForInvInFromSupplierActionService adjustForInvInFromSupplierActionService
    AdjustmentForInvOutActionService adjustmentForInvOutActionService
    ReverseAdjustForInvInFromSupplierActionService reverseAdjustForInvInFromSupplierActionService
    ReverseAdjustForInvInFromInventoryActionService reverseAdjustForInvInFromInventoryActionService
    ReverseAdjustForInvOutActionService reverseAdjustForInvOutActionService

    //////////////modification of production overhead cost

    ShowInvModifyOverheadCostActionService showInvModifyOverheadCostActionService
    SearchInvModifyOverheadCostActionService searchInvModifyOverheadCostActionService
    UpdateInvModifyOverheadCostActionService updateInvModifyOverheadCostActionService
    GetInvProdFinishedMaterialByLineItemIdActionService getInvProdFinishedMaterialByLineItemIdActionService

    //for acknowledge chalan
    AcknowledgeInvoiceFromSupplierActionService acknowledgeInvoiceFromSupplierActionService

    /////*******inv  Inventory Out Details ******///////
    def approveInventoryOutDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) approveForInventoryOutDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInventoryOutDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approveForInventoryOutDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approveForInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def showApprovedInventoryOutDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForApprovedInventoryOutDetailsActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (LinkedHashMap) showForApprovedInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
        }
        result = (LinkedHashMap) showForApprovedInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render(view: '/inventory/invInventoryTransactionDetails/showApprovedInventoryOut', model: [output: output])
    }

    def listApprovedInventoryOutDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForApprovedInventoryOutDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForApprovedInventoryOutDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForApprovedInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForApprovedInventoryOutDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForApprovedInventoryOutDetailsActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForApprovedInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def showUnApprovedInventoryOutDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForUnApprovedInventoryOutDetailsActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (LinkedHashMap) showForUnApprovedInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
        }
        result = (LinkedHashMap) showForUnApprovedInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        Long transactionId = (Long) result.inventoryTransactionId    // add separate non-json key for tagLib parameter
        render(view: '/inventory/invInventoryTransactionDetails/showInventoryOutDetails', model: [transactionId: transactionId, output: output])
    }

    def createInventoryOutDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInventoryOutDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryOutDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInventoryOutDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) createForInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def listUnApprovedInventoryOutDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForUnApprovedInventoryOutDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForUnApprovedInventoryOutDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForUnApprovedInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForUnApprovedInventoryOutDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForUnApprovedInventoryOutDetailsActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForUnApprovedInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def deleteInventoryOutDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryOutDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryOutDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryOutDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) deleteForInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def selectInventoryOutDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectForInventoryOutDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryOutDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) selectForInventoryOutDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectForInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def updateInventoryOutDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateForInventoryOutDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryOutDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryOutDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryOutDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) updateForInventoryOutDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /////*******inv  Transaction Details (IN)******///////
    def showUnapprovedInvInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForUnapprovedInvInFromSupplierActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForUnapprovedInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForUnapprovedInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        Long transactionId = (Long) result.inventoryTransactionId    // add separate non-json key for tagLib parameter
        render(view: '/inventory/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier', model: [transactionId: transactionId, output: output])
    }

    def createInventoryInDetailsFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInventoryInDetailsFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInventoryInDetailsFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) createForInventoryInDetailsFromSupplierActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def updateInventoryInDetailsFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateForInventoryInDetailsFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryInDetailsFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) updateForInventoryInDetailsFromSupplierActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def selectInventoryInDetailsFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) selectForInventoryInDetailsFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) selectForInventoryInDetailsFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectForInventoryInDetailsFromSupplierActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }


    def listUnapprovedInvInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (LinkedHashMap) searchForUnapprovedInvInFromSupplierActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForUnapprovedInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) searchForUnapprovedInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) listForUnapprovedInvInFromSupplierActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForUnapprovedInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) listForUnapprovedInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }

    def deleteInventoryInDetailsFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryInDetailsFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryInDetailsFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) deleteForInventoryInDetailsFromSupplierActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def approveInventoryInDetailsFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) approveForInventoryInDetailsFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approveForInventoryInDetailsFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInventoryInDetailsFromSupplierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approveForInventoryInDetailsFromSupplierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def showApprovedInvInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForApprovedInvInFromSupplierActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForApprovedInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showApprovedInvInFromSupplier', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForApprovedInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render(view: '/inventory/invInventoryTransactionDetails/showApprovedInvInFromSupplier', model: [output: output])
    }

    def listApprovedInvInFromSupplier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (LinkedHashMap) searchForApprovedInvInFromSupplierActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForApprovedInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) searchForApprovedInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) listForApprovedInvInFromSupplierActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForApprovedInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) listForApprovedInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }

    /////*******inv  Transaction Details (IN) from inventory ******///////
    def showUnapprovedInvInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForUnapprovedInvInFromInventoryActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForUnapprovedInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showUnapprovedInvInFromInventory', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForUnapprovedInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        Long transactionId = (Long) result.inventoryTransactionId    // add separate non-json key for tagLib parameter
        render(view: '/inventory/invInventoryTransactionDetails/showUnapprovedInvInFromInventory', model: [transactionId: transactionId, output: output])
    }

    def createInventoryInDetailsFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForInventoryInDetailsFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInventoryInDetailsFromInventoryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) createForInventoryInDetailsFromInventoryActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def updateInventoryInDetailsFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateForInventoryInDetailsFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryInDetailsFromInventoryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) updateForInventoryInDetailsFromInventoryActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def selectInventoryInDetailsFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) selectForInventoryInDetailsFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) selectForInventoryInDetailsFromInventoryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectForInventoryInDetailsFromInventoryActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def listUnapprovedInvInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (LinkedHashMap) searchForUnapprovedInvInFromInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForUnapprovedInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) searchForUnapprovedInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) listForUnapprovedInvInFromInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForUnapprovedInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) listForUnapprovedInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }

    def deleteInventoryInDetailsFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryInDetailsFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryInDetailsFromInventoryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryInDetailsFromInventoryActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) deleteForInventoryInDetailsFromInventoryActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def approveInventoryInDetailsFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) approveForInvInDetailsFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInvInDetailsFromInventoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) approveForInvInDetailsFromInventoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInvInDetailsFromInventoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) approveForInvInDetailsFromInventoryActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def showApprovedInvInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForApprovedInvInFromInventoryActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForApprovedInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showApprovedInvInFromInventory', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForApprovedInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render(view: '/inventory/invInventoryTransactionDetails/showApprovedInvInFromInventory', model: [output: output])
    }

    def listApprovedInvInFromInventory() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (LinkedHashMap) searchForApprovedInvInFromInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForApprovedInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) searchForApprovedInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) listForApprovedInvInFromInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForApprovedInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) listForApprovedInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        }

        output = result as JSON
        render output
    }

    /************************************ Inventory Consumption Details ************************************/
    def listApprovedInventoryConsumptionDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        if (params.query) {
            executeResult = (LinkedHashMap) searchForApprovedInventoryConsumptionDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForApprovedInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForApprovedInventoryConsumptionDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForApprovedInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def showApprovedInventoryConsumptionDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) showForApprovedInventoryConsumptionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(preResult);
            String output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showApprovedInventoryConsumption', model: [output: output])
            return;
        }

        executeResult = (LinkedHashMap) showForApprovedInventoryConsumptionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            String output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showApprovedInventoryConsumption', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForApprovedInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        String output = result as JSON
        render(view: '/inventory/invInventoryTransactionDetails/showApprovedInventoryConsumption', model: [output: output])
    }

    def showUnApprovedInventoryConsumptionDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) showForUnApprovedInventoryConsumptionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForUnApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(preResult);
            String output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showInventoryConsumptionDetails', model: [output: output])
            return;
        }

        executeResult = (LinkedHashMap) showForUnApprovedInventoryConsumptionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForUnApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            String output = result as JSON
            render(view: '/inventory/invInventoryTransactionDetails/showInventoryConsumptionDetails', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForUnApprovedInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        String output = result as JSON
        Long transactionId = (Long) result.inventoryTransactionId    // add separate non-json key for tagLib parameter
        render(view: '/inventory/invInventoryTransactionDetails/showInventoryConsumptionDetails', model: [transactionId: transactionId, output: output])
    }

    def createInventoryConsumptionDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        Map postResult = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.executePostCondition(null, executeResult);
        isError = (Boolean) postResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
            result.put(Tools.MAIL_SENDING_ERR_MSG, postResult.message)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createForInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def listUnApprovedInventoryConsumptionDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        if (params.query) {
            executeResult = (LinkedHashMap) searchForUnApprovedInventoryConsumptionDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForUnApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForUnApprovedInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForUnApprovedInventoryConsumptionDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForUnApprovedInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForUnApprovedInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def selectInventoryConsumptionDetails() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectForInventoryConsumptionDetailsActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectForInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectForInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def deleteInventoryConsumptionDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteForInventoryConsumptionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryConsumptionDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteForInventoryConsumptionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteForInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deleteForInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def updateInventoryConsumptionDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateForInventoryConsumptionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryConsumptionDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateForInventoryConsumptionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateForInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateForInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def approveInventoryConsumptionDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) approveForInventoryConsumptionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInventoryConsumptionDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approveForInventoryConsumptionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approveForInventoryConsumptionDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approveForInventoryConsumptionDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /************************************ end of Inventory Consumption Details ************************************/

    def listFixedAssetByInventoryId() {
        String output
        Map result
        Map executeResult = ((Map) getFixedAssetListByInventoryIdActionService.execute(params, null))
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getFixedAssetListByInventoryIdActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getFixedAssetListByInventoryIdActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listFixedAssetByInventoryIdAndItemId() {
        String output
        Map result
        Map preResult = (Map) getFixedAssetListByInventoryIdAndItemIdActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getFixedAssetListByInventoryIdAndItemIdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getFixedAssetListByInventoryIdAndItemIdActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getFixedAssetListByInventoryIdAndItemIdActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getFixedAssetListByInventoryIdAndItemIdActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    //***************  Adjustment For Inv Transaction From Supplier ***************\\
    def adjustInvInFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) adjustForInvInFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustForInvInFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) adjustForInvInFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustForInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) adjustForInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }
    //*************************  Adjustment for Inventory Out   ******************************
    def adjustInvOut() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) adjustmentForInvOutActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustmentForInvOutActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) adjustmentForInvOutActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustmentForInvOutActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) adjustmentForInvOutActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }
    //***************************************************************************************************

    //*************************  Adjustment for Inventory Consumption   ******************************
    def adjustInvConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) adjustmentForInvConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustmentForInvConsumptionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) adjustmentForInvConsumptionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) adjustmentForInvConsumptionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) adjustmentForInvConsumptionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    //*************************  Reverse Adjustment for Inventory Consumption   ******************************
    def reverseAdjustInvConsumption() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) reverseAdjustmentForInvConsumptionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustmentForInvConsumptionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) reverseAdjustmentForInvConsumptionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustmentForInvConsumptionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) reverseAdjustmentForInvConsumptionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    //reverse adjustment of inventory in from supplier
    def reverseAdjustInvInFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) reverseAdjustForInvInFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustForInvInFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) reverseAdjustForInvInFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustForInvInFromSupplierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) reverseAdjustForInvInFromSupplierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    //reverse adjustment of inventory in from inventory
    def reverseAdjustInvInFromInventory() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) reverseAdjustForInvInFromInventoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustForInvInFromInventoryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) reverseAdjustForInvInFromInventoryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustForInvInFromInventoryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) reverseAdjustForInvInFromInventoryActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    //reverse adjustment of inventory out
    def reverseAdjustInvOut() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) reverseAdjustForInvOutActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustForInvOutActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) reverseAdjustForInvOutActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) reverseAdjustForInvOutActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) reverseAdjustForInvOutActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def acknowledgeInvoiceFromSupplier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) acknowledgeInvoiceFromSupplierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) acknowledgeInvoiceFromSupplierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) acknowledgeInvoiceFromSupplierActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) acknowledgeInvoiceFromSupplierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) acknowledgeInvoiceFromSupplierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /*********************************  Production Modify Overhead Cost  ************************************/

    def showInvModifyOverheadCost() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showInvModifyOverheadCostActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showInvModifyOverheadCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) showInvModifyOverheadCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render(view: '/inventory/invModifyOverheadCost/show', model: [output: output])
    }

    def searchInvModifyOverheadCost() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) searchInvModifyOverheadCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchInvModifyOverheadCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) searchInvModifyOverheadCostActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchInvModifyOverheadCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) searchInvModifyOverheadCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def updateInvModifyOverheadCost() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updateInvModifyOverheadCostActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateInvModifyOverheadCostActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateInvModifyOverheadCostActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateInvModifyOverheadCostActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateInvModifyOverheadCostActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def getInvProdFinishedMaterialByLineItemId() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) getInvProdFinishedMaterialByLineItemIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getInvProdFinishedMaterialByLineItemIdActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getInvProdFinishedMaterialByLineItemIdActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getInvProdFinishedMaterialByLineItemIdActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getInvProdFinishedMaterialByLineItemIdActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def dropDownInventoryItemConsumptionReload(){
        render inv.dropDownInventoryItemConsumption(params)
    }

    def dropDownInventoryItemInFromInventoryReload(){
        render inv.dropDownInventoryItemInFromInventory(params)
    }

    def dropDownInventoryItemInFromSupplierReload(){
        render inv.dropDownInventoryItemInFromSupplier(params)
    }

    def dropDownInventoryItemOutReload(){
        render inv.dropDownInventoryItemOut(params)
    }

    def dropDownInventoryProductionLineItemReload(){
        render inv.dropDownInventoryProductionLineItem(params)
    }
}