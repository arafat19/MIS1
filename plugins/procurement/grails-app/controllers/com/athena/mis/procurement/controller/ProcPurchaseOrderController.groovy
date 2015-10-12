package com.athena.mis.procurement.controller

import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.procurement.actions.purchaseorder.*
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ProcPurchaseOrderController {
    static allowedMethods = [
            show: "POST", select: "POST", create: "POST", update: "POST", list: "POST", delete: "POST", approve: "POST",
            listUnApprovedPO: "POST", approvePODashBoard: "POST", getPOStatusForDashBoard: "POST", sendForPOApproval: "POST",
            cancelPO: "POST", unApprovePO: "POST"
    ]

    ShowPurchaseOrderActionService showPurchaseOrderActionService
    SelectPurchaseOrderActionService selectPurchaseOrderActionService
    UpdatePurchaseOrderActionService updatePurchaseOrderActionService
    CreatePurchaseOrderActionService createPurchaseOrderActionService
    DeletePurchaseOrderActionService deletePurchaseOrderActionService
    SearchPurchaseOrderActionService searchPurchaseOrderActionService
    ApprovePurchaseOrderActionService approvePurchaseOrderActionService
    ListPurchaseOrderActionService listPurchaseOrderActionService
    ListUnApprovedPurchaseOrderActionService listUnApprovedPurchaseOrderActionService
    ApprovePOForDashBoardActionService approvePOForDashBoardActionService
    GetPOStatusForDashBoardActionService getPOStatusForDashBoardActionService
    SendForApprovalPurchaseOrderActionService sendForApprovalPurchaseOrderActionService
    CancelPurchaseOrderActionService cancelPurchaseOrderActionService
    UnApprovePurchaseOrderActionService unApprovePurchaseOrderActionService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Show Purchase Order
     */
    def show() {
        Map executeResult = (Map) showPurchaseOrderActionService.execute(params, null)
        if (executeResult.isError) {
            Map result = (Map) showPurchaseOrderActionService.buildFailureResultForUI(executeResult)
            render(view: 'show', model: [output: result as JSON])
            return
        }

        if (executeResult) {
            executeResult = (Map) showPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        } else {
            executeResult = (Map) showPurchaseOrderActionService.buildFailureResultForUI(null)
        }

        render(view: '/procurement/procPurchaseOrder/show', model: [output: executeResult as JSON])
    }

    /**
     * Create Purchase Order
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        ProcPurchaseOrder purchaseOrderInstance = buildPurchaseOrder(params)

        preResult = (LinkedHashMap) createPurchaseOrderActionService.executePreCondition(params, purchaseOrderInstance)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createPurchaseOrderActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Update Purchase Order
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updatePurchaseOrderActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) updatePurchaseOrderActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) updatePurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Select Purchase Order
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectPurchaseOrderActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) selectPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * List Purchase Order
     */
    def list() {
        Map executeResult
        if (params.query) {
            executeResult = (Map) searchPurchaseOrderActionService.execute(params, null)

            if (executeResult) {
                executeResult = (Map) searchPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
            } else {
                executeResult = (Map) searchPurchaseOrderActionService.buildFailureResultForUI(null)
            }
        } else {
            executeResult = (Map) listPurchaseOrderActionService.execute(params, null)

            if (executeResult) {
                executeResult = (Map) listPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
            } else {
                executeResult = (Map) listPurchaseOrderActionService.buildFailureResultForUI(null)
            }
        }
        render(executeResult as JSON)
    }

    /**
     * Approve Purchase Order
     */
    def approve() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) approvePurchaseOrderActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) approvePurchaseOrderActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) approvePurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Delete Purchase Order
     */
    def delete() {
        Map result
        String output
        LinkedHashMap executeResult
        Map preResult = (Map) deletePurchaseOrderActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePurchaseOrderActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (LinkedHashMap) deletePurchaseOrderActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePurchaseOrderActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        }

        render(result as JSON)
    }

    /**
     * List Un Purchase Order
     */
    def listUnApprovedPO() {
        Map result
        Map executeResult = (Map) listUnApprovedPurchaseOrderActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) listUnApprovedPurchaseOrderActionService.buildFailureResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * Approve Purchase Order Dash Board
     */
    def approvePODashBoard() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) approvePOForDashBoardActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePOForDashBoardActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) approvePOForDashBoardActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePOForDashBoardActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) approvePOForDashBoardActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Get Purchase Order for Dash Board
     */
    def getPOStatusForDashBoard() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) getPOStatusForDashBoardActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getPOStatusForDashBoardActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getPOStatusForDashBoardActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /**
     * Build Purchase Order object
     * @param params - serialized parameters from UI
     * @return - an object of purchase order
     */
    private ProcPurchaseOrder buildPurchaseOrder(GrailsParameterMap params) {
        AppUser user = procSessionUtil.appSessionUtil.getAppUser()
        ProcPurchaseOrder purchaseOrder = new ProcPurchaseOrder(params)
        purchaseOrder.createdOn = new Date()
        purchaseOrder.createdBy = user.id
        purchaseOrder.updatedOn = null
        purchaseOrder.updatedBy = 0L
        purchaseOrder.approvedByDirectorId = 0L
        purchaseOrder.approvedByProjectDirectorId = 0L
        purchaseOrder.trCostCount = 0
        purchaseOrder.trCostTotal = 0.0d
        purchaseOrder.itemCount = 0
        purchaseOrder.comments ? purchaseOrder.comments : null
        purchaseOrder.companyId = user.companyId
        purchaseOrder.totalPrice = 0.0d
        if (params.discount.toString().length() > 0) {
            try {
                purchaseOrder.discount = Double.parseDouble(params.discount.toString())
            } catch (Exception e) {
                purchaseOrder.discount = 0.0d
            }
        } else {
            purchaseOrder.discount = 0.0d
        }
        return purchaseOrder
    }

    def sendForPOApproval() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) sendForApprovalPurchaseOrderActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) sendForApprovalPurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) sendForApprovalPurchaseOrderActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) sendForApprovalPurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) sendForApprovalPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def cancelPO() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) cancelPurchaseOrderActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) cancelPurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) cancelPurchaseOrderActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) cancelPurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) cancelPurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def unApprovePO() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) unApprovePurchaseOrderActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) unApprovePurchaseOrderActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) unApprovePurchaseOrderActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) unApprovePurchaseOrderActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) unApprovePurchaseOrderActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
}
