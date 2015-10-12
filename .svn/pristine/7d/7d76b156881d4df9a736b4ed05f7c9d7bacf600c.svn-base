package com.athena.mis.procurement.controller

import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.actions.purchaseorderdetails.*
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.procurement.utility.ProcSessionUtil
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ProcPurchaseOrderDetailsController {

    static allowedMethods = [show: "POST",
            create: "POST",
            select: "POST",
            update: "POST",
            list: "POST",
            delete: "POST",
            getItemListPurchaseOrderDetails: "POST"
    ]

    @Autowired
    ProcSessionUtil procSessionUtil

    ShowPurchaseOrderDetailsActionService showPurchaseOrderDetailsActionService
    SelectPurchaseOrderDetailsActionService selectPurchaseOrderDetailsActionService
    UpdatePurchaseOrderDetailsActionService updatePurchaseOrderDetailsActionService
    CreatePurchaseOrderDetailsActionService createPurchaseOrderDetailsActionService
    SearchPurchaseOrderDetailsActionService searchPurchaseOrderDetailsActionService
    ListPurchaseOrderDetailsActionService listPurchaseOrderDetailsActionService
    DeletePurchaseOrderDetailsActionService deletePurchaseOrderDetailsActionService
    GetForItemListForPurchaseOrderDetailsActionService getForItemListForPurchaseOrderDetailsActionService

    /**
     * Show purchase order details
     */
    def show() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (LinkedHashMap) showPurchaseOrderDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showPurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
            render(view: '/procurement/procPurchaseOrderDetails/show', model: [output: result as JSON])
            return
        }

        executeResult = (LinkedHashMap) showPurchaseOrderDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            executeResult = (Map) showPurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
        } else {
            executeResult = (Map) showPurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/procurement/procPurchaseOrderDetails/show', model: [output: executeResult as JSON])
    }

    /**
     * Create purchase order details
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        ProcPurchaseOrderDetails purchaseOrderDetailsInstance = buildPurchaseOrderDetails(params)
        preResult = (LinkedHashMap) createPurchaseOrderDetailsActionService.executePreCondition(params, purchaseOrderDetailsInstance)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createPurchaseOrderDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createPurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Update purchase order details
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updatePurchaseOrderDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) updatePurchaseOrderDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) updatePurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Select purchase order details
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectPurchaseOrderDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) selectPurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * List and search purchase order details
     */
    def list() {
        Map executeResult
        Map preResult
        Boolean isError
        if (params.query) {
            preResult = (LinkedHashMap) searchPurchaseOrderDetailsActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                executeResult = (LinkedHashMap) searchPurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
                render(executeResult as JSON)
                return
            }

            executeResult = (Map) searchPurchaseOrderDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                executeResult = (Map) searchPurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                executeResult = (Map) searchPurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            preResult = (LinkedHashMap) listPurchaseOrderDetailsActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                executeResult = (LinkedHashMap) listPurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
                render(executeResult as JSON)
                return
            }

            executeResult = (Map) listPurchaseOrderDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                executeResult = (Map) listPurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
            } else {
                executeResult = (Map) listPurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
            }
        }

        render(executeResult as JSON)
    }

    /**
     * Delete purchase order details
     */
    def delete() {
        String output
        Map result
        Map preResult = (Map) deletePurchaseOrderDetailsActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) deletePurchaseOrderDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePurchaseOrderDetailsActionService.buildSuccessResultForUI(preResult)
        }
        output = result as JSON
        render output
    }

    /***********************************end of PO of work ************************************************/

    /**
     * Get item list for purchase order details
     */
    def getItemListPurchaseOrderDetails() {

        Map result
        Boolean isError
        Map executeResult
        Map preResult = (Map) getForItemListForPurchaseOrderDetailsActionService.executePreCondition(params, null)

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getForItemListForPurchaseOrderDetailsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) getForItemListForPurchaseOrderDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getForItemListForPurchaseOrderDetailsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getForItemListForPurchaseOrderDetailsActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Build Purchase order details object
     * @param params - serialized parameters from UI
     * @return - object of purchase order details
     */
    private ProcPurchaseOrderDetails buildPurchaseOrderDetails(GrailsParameterMap params) {
        if ((!params.vatTax) || (params.vatTax == '')) {
            params.vatTax = 0.0d
        }
        ProcPurchaseOrderDetails purchaseOrderDetails = new ProcPurchaseOrderDetails(params)
        AppUser user = procSessionUtil.appSessionUtil.getAppUser()
        purchaseOrderDetails.createdOn = new Date()
        purchaseOrderDetails.createdBy = user.id
        purchaseOrderDetails.updatedOn = null
        purchaseOrderDetails.updatedBy = 0
        purchaseOrderDetails.companyId = user.companyId

        return purchaseOrderDetails
    }
}
