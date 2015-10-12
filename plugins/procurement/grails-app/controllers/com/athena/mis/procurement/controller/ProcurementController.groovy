package com.athena.mis.procurement.controller

import com.athena.mis.procurement.actions.indent.ListUnApprovedIndentActionService
import com.athena.mis.procurement.actions.purchaseorder.GetPOStatusForDashBoardActionService
import com.athena.mis.procurement.actions.purchaseorder.ListUnApprovedPurchaseOrderActionService
import com.athena.mis.procurement.actions.purchaserequest.ListUnApprovedPurchaseRequestActionService
import grails.converters.JSON
import grails.util.Environment

class ProcurementController {

    static allowedMethods = [renderProcurementMenu: "POST"]

    ListUnApprovedPurchaseOrderActionService listUnApprovedPurchaseOrderActionService
    ListUnApprovedPurchaseRequestActionService listUnApprovedPurchaseRequestActionService
    ListUnApprovedIndentActionService listUnApprovedIndentActionService
    GetPOStatusForDashBoardActionService getPOStatusForDashBoardActionService

    def springSecurityService

    /**
     * Used to render left menu and dash board of procurement
     */
    def renderProcurementMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        // Now pull data for first tab of Dash board        
        Map resultPO
        Map executeResult = (Map) listUnApprovedPurchaseOrderActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultPO = (Map) listUnApprovedPurchaseOrderActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultPO = (Map) listUnApprovedPurchaseOrderActionService.buildFailureResultForUI(executeResult);
        }
        String outputPO = resultPO as JSON

        Map resultPR
        executeResult = (Map) listUnApprovedPurchaseRequestActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultPR = (Map) listUnApprovedPurchaseRequestActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultPR = (Map) listUnApprovedPurchaseRequestActionService.buildFailureResultForUI(executeResult);
        }
        String outputPR = resultPR as JSON

        Map resultIndent
        executeResult = (Map) listUnApprovedIndentActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultIndent = (Map) listUnApprovedIndentActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultIndent = (Map) listUnApprovedIndentActionService.buildFailureResultForUI(executeResult);
        }
        String outputIndent = resultIndent as JSON

        Map resultPOStatus
        executeResult = (Map) getPOStatusForDashBoardActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultPOStatus = (Map) getPOStatusForDashBoardActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultPOStatus = (Map) getPOStatusForDashBoardActionService.buildFailureResultForUI(executeResult);
        }
        String outputPOStatus = resultPOStatus as JSON

        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'procurement',template: '/procurement/leftmenuProcurement')])
                element([name: 'dashBoard', content: g.render(plugin: 'procurement',template: '/procurement/dashBoardProcurement', model: [outputPR: outputPR, outputPO: outputPO, outputIndent: outputIndent, outputPOStatus: outputPOStatus])])
            }
        }
    }


}
