package com.athena.mis.procurement.controller

import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.actions.purchaserequest.*
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ProcPurchaseRequestController {

    static allowedMethods = [
            show: "POST",
            select: "POST",
            create: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            approve: "POST",
            unApprovePR: "POST",
            listIndentByProject: "POST",
            listUnApprovedPR: "POST",
            approvePRDashBoard: "POST",
            sentMailForPRApproval: "POST"
    ]

    ShowPurchaseRequestActionService showPurchaseRequestActionService
    CreatePurchaseRequestActionService createPurchaseRequestActionService
    ListPurchaseRequestActionService listPurchaseRequestActionService
    ApprovePurchaseRequestActionService approvePurchaseRequestActionService
    UnApprovePurchaseRequestActionService unApprovePurchaseRequestActionService
    SelectPurchaseRequestActionService selectPurchaseRequestActionService
    UpdatePurchaseRequestActionService updatePurchaseRequestActionService
    DeletePurchaseRequestActionService deletePurchaseRequestActionService
    GetIndentListForProjectActionService getIndentListForProjectActionService
    ListUnApprovedPurchaseRequestActionService listUnApprovedPurchaseRequestActionService
    ApprovePRForDashBoardActionService approvePRForDashBoardActionService
    SentMailPurchaseRequestActionService sentMailPurchaseRequestActionService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Show purchase request
     */
    def show() {
        LinkedHashMap result
        Boolean isError
        Map preResult = (Map) showPurchaseRequestActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showPurchaseRequestActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showPurchaseRequestActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) showPurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        }
        render(view: '/procurement/procPurchaseRequest/show', model: [modelJson: result as JSON])
    }

    /**
     * List of purchase request
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult

        executeResult = (LinkedHashMap) listPurchaseRequestActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listPurchaseRequestActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listPurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Create purchase request
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        ProcPurchaseRequest newPurchaseRequest = buildPurchaseRequestObject(params)
        preResult = (LinkedHashMap) createPurchaseRequestActionService.executePreCondition(params, newPurchaseRequest)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseRequestActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createPurchaseRequestActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createPurchaseRequestActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (LinkedHashMap) createPurchaseRequestActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Update purchase request
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) updatePurchaseRequestActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseRequestActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) updatePurchaseRequestActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updatePurchaseRequestActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) updatePurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Select purchase request
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) selectPurchaseRequestActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPurchaseRequestActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) selectPurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Approve purchase request
     */
    def approve() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) approvePurchaseRequestActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePurchaseRequestActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approvePurchaseRequestActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePurchaseRequestActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approvePurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Un-Approve purchase request
     */
    def unApprovePR() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) unApprovePurchaseRequestActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) unApprovePurchaseRequestActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) unApprovePurchaseRequestActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) unApprovePurchaseRequestActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) unApprovePurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete purchase request
     */
    def delete() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) deletePurchaseRequestActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deletePurchaseRequestActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deletePurchaseRequestActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deletePurchaseRequestActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deletePurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Get indent list against project
     */
    def listIndentByProject() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) getIndentListForProjectActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getIndentListForProjectActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) getIndentListForProjectActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) getIndentListForProjectActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) getIndentListForProjectActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * List of un purchase request
     */
    def listUnApprovedPR() {
        Map result
        Map executeResult = (Map) listUnApprovedPurchaseRequestActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedPurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedPurchaseRequestActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    /**
     * Approve purchase request for dash board
     */
    def approvePRDashBoard() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) approvePRForDashBoardActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePRForDashBoardActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) approvePRForDashBoardActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) approvePRForDashBoardActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) approvePRForDashBoardActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def sentMailForPRApproval() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) sentMailPurchaseRequestActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sentMailPurchaseRequestActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) sentMailPurchaseRequestActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sentMailPurchaseRequestActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) sentMailPurchaseRequestActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * Build Purchase request object
     * @param parameterMap - serialized parameters from UI
     * @return - an object of purchase request
     */
    private ProcPurchaseRequest buildPurchaseRequestObject(GrailsParameterMap parameterMap) {
        AppUser user = procSessionUtil.appSessionUtil.getAppUser()
        ProcPurchaseRequest purchaseRequest = new ProcPurchaseRequest(parameterMap)
        purchaseRequest.createdOn = new Date()
        purchaseRequest.createdBy = user.id
        purchaseRequest.updatedOn = null
        purchaseRequest.updatedBy = 0
        purchaseRequest.approvedByDirectorId = 0
        purchaseRequest.approvedByProjectDirectorId = 0
        purchaseRequest.itemCount = 0
        purchaseRequest.indentId = purchaseRequest.indentId > 0 ? purchaseRequest.indentId : 0
        purchaseRequest.companyId = user.companyId
        return purchaseRequest
    }

}
