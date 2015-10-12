package com.athena.mis.procurement.controller

import com.athena.mis.procurement.actions.cancelledpo.ListCancelledPOActionService
import com.athena.mis.procurement.actions.cancelledpo.ShowCancelledPOActionService
import grails.converters.JSON

class ProcCancelledPOController {

    static allowedMethods = [ show: "POST", list: "POST" ]

    ShowCancelledPOActionService showCancelledPOActionService
    ListCancelledPOActionService listCancelledPOActionService

    def show() {
        Map result;
        Map executeResult
        Boolean isError

        executeResult = (Map) showCancelledPOActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showCancelledPOActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showCancelledPOActionService.buildFailureResultForUI(executeResult)
        }
        String output = result as JSON

        render(view: '/procurement/procCancelledPO/show', model: [output: output])
    }

    def list() {
        Map result;
        Map executeResult
        Boolean isError

        executeResult = (Map) listCancelledPOActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listCancelledPOActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) listCancelledPOActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }
}
