package com.athena.mis.inventory.controller

import com.athena.mis.inventory.actions.invproductiondetails.*
import com.athena.mis.inventory.entity.InvProductionDetails
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class InvProductionDetailsController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST",
            update: "POST", delete: "POST", list: "POST", getBothMaterials: "POST"]

    CreateInvProductionDetailsActionService createInvProductionDetailsActionService
    DeleteInvProductionDetailsActionService deleteInvProductionDetailsActionService
    ListInvProductionDetailsActionService listInvProductionDetailsActionService
    SearchInvProductionDetailsActionService searchInvProductionDetailsActionService
    SelectInvProductionDetailsActionService selectInvProductionDetailsActionService
    ShowInvProductionDetailsActionService showInvProductionDetailsActionService
    UpdateInvProductionDetailsActionService updateInvProductionDetailsActionService
    GetBothMaterialsForLineItemActionService getBothMaterialsForLineItemActionService

    def show() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        preResult = (Map) showInvProductionDetailsActionService.executePreCondition(params, null);
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showInvProductionDetailsActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }
        executeResult = (Map) showInvProductionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showInvProductionDetailsActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showInvProductionDetailsActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/inventory/invProductionDetails/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        InvProductionDetails invProductionDetails = buildInvProductionDetails(params)
        preResult = (Map) createInvProductionDetailsActionService.executePreCondition(params, invProductionDetails)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createInvProductionDetailsActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }
        executeResult = (Map) createInvProductionDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createInvProductionDetailsActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createInvProductionDetailsActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    def list() {
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchInvProductionDetailsActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchInvProductionDetailsActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchInvProductionDetailsActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listInvProductionDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listInvProductionDetailsActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) listInvProductionDetailsActionService.buildSuccessResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    def delete() {
        Map result
        Boolean isError
        Map preResult = (Map) deleteInvProductionDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteInvProductionDetailsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        Map deleteResult = (Map) deleteInvProductionDetailsActionService.execute(params, preResult);
        isError = (Boolean) deleteResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteInvProductionDetailsActionService.buildFailureResultForUI(deleteResult);
        } else {
            result = (Map) deleteInvProductionDetailsActionService.buildSuccessResultForUI(deleteResult);
        }
        render(result as JSON)
    }

    def select() {
        Map executeResult
        Map result
        executeResult = (Map) selectInvProductionDetailsActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectInvProductionDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectInvProductionDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        InvProductionDetails invProductionDetails = buildInvProductionDetails(params)
        invProductionDetails.id = Long.parseLong(params.id.toString());
        invProductionDetails.version = Integer.parseInt(params.version.toString());
        preResult = (Map) updateInvProductionDetailsActionService.executePreCondition(null, invProductionDetails)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateInvProductionDetailsActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateInvProductionDetailsActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateInvProductionDetailsActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateInvProductionDetailsActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    private InvProductionDetails buildInvProductionDetails(GrailsParameterMap params) {
        if (params.overheadCost.toString().length() == 0) {
            params.overheadCost = 0
        }
        InvProductionDetails invProductionDetails = new InvProductionDetails(params);

        return invProductionDetails
    }

    // Get a Map containing both List of raw material and finished products by Line Item
    def getBothMaterials() {
        Map executeResult = (Map) getBothMaterialsForLineItemActionService.execute(params, null);
        String output = executeResult as JSON
        render output
    }

}
