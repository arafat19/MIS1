package com.athena.mis.procurement.controller

import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.actions.purchaserequestdetails.*
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ProcPurchaseRequestDetailsController {

    static allowedMethods = [
            show: "POST",
            select: "POST",
            create: "POST",
            update: "POST",
            list: "POST",
            delete: "POST",
            getItemList: "POST"
    ]

    @Autowired
    ProcSessionUtil procSessionUtil
    ShowPurchaseRequestDetailsActionService showPurchaseRequestDetailsActionService
    CreatePurchaseRequestDetailsActionService createPurchaseRequestDetailsActionService
    ListPurchaseRequestDetailsActionService listPurchaseRequestDetailsActionService
    SearchPurchaseRequestDetailsActionService searchPurchaseRequestDetailsActionService
    SelectPurchaseRequestDetailsActionService selectPurchaseRequestDetailsActionService
    UpdatePurchaseRequestDetailsActionService updatePurchaseRequestDetailsActionService
    DeletePurchaseRequestDetailsActionService deletePurchaseRequestDetailsActionService
    GetItemListForPRDetailsActionService getItemListForPRDetailsActionService

    /**
     * Show Purchase request details
     */
    def show() {
        LinkedHashMap result
        Boolean isError
        Map preResult = (Map) showPurchaseRequestDetailsActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showPurchaseRequestDetailsActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showPurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) showPurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        render(view: '/procurement/procPurchaseRequestDetails/show', model: [modelJson: result as JSON])
    }

    /**
     * Create Purchase request details
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        ProcPurchaseRequestDetails newPurchaseRequestDetails = buildPurchaseRequestDetailsObject(params)
        preResult = (LinkedHashMap) createPurchaseRequestDetailsActionService.executePreCondition(params, newPurchaseRequestDetails)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseRequestDetailsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createPurchaseRequestDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (LinkedHashMap) createPurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * List & search Purchase request details
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        if (params.query) {
            executeResult = (LinkedHashMap) searchPurchaseRequestDetailsActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchPurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchPurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) listPurchaseRequestDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listPurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listPurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Update Purchase request details
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updatePurchaseRequestDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseRequestDetailsActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) updatePurchaseRequestDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (LinkedHashMap) updatePurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Select Purchase request details
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectPurchaseRequestDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectPurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete Purchase request details
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deletePurchaseRequestDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deletePurchaseRequestDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deletePurchaseRequestDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deletePurchaseRequestDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deletePurchaseRequestDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Get item list
     */
    def getItemList() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) getItemListForPRDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getItemListForPRDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getItemListForPRDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getItemListForPRDetailsActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getItemListForPRDetailsActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Build Purchase Request details object
     * @param parameterMap - serialized parameters from UI
     * @return - an object of purchase request details
     */
    private ProcPurchaseRequestDetails buildPurchaseRequestDetailsObject(GrailsParameterMap parameterMap) {
        AppUser user = procSessionUtil.appSessionUtil.getAppUser()

        ProcPurchaseRequestDetails purchaseRequestDetails = new ProcPurchaseRequestDetails(parameterMap)
        purchaseRequestDetails.createdOn = new Date()
        purchaseRequestDetails.createdBy = user.id
        purchaseRequestDetails.updatedOn = null
        purchaseRequestDetails.updatedBy = 0
        purchaseRequestDetails.poQuantity = 0.00
        purchaseRequestDetails.itemId = purchaseRequestDetails.itemId ? purchaseRequestDetails.itemId : 0
        purchaseRequestDetails.companyId = user.companyId
        return purchaseRequestDetails
    }
}







