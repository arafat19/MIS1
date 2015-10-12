package com.athena.mis.application.controller

import com.athena.mis.application.actions.requestmap.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class RequestMapController {

    static allowedMethods = [show: "POST", update: "POST",
            select: "POST",resetRequestMap: "POST"];

    ShowRequestMapActionService showRequestMapActionService
    UpdateRequestMapActionService updateRequestMapActionService
    SelectRequestMapActionService selectRequestMapActionService
    ResetRequestMapByPluginIdActionService resetRequestMapByPluginIdActionService

    /**
     * Show Request Map
     */
    def show() {
        Map preResult = (Map) showRequestMapActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showRequestMapActionService.execute(params, null)
        Map result
        if (executeResult) {
            result = (Map) showRequestMapActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showRequestMapActionService.buildFailureResultForUI(null)
        }
        render(view: '/application/requestMap/show', model: [output: result as JSON])
    }

    /**
     * Update Request Map
     */
    def update() {
        Map preResult = (Map) updateRequestMapActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateRequestMapActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Boolean success = (Boolean) updateRequestMapActionService.execute(params, preResult)

        if (success.booleanValue()) {
            result = (Map) updateRequestMapActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) updateRequestMapActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    /**
     * Select Request Map
     */
    def select() {
        Map preResult = (Map) selectRequestMapActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Map result
        Map executeResult = (Map) selectRequestMapActionService.execute(params, null)
        if (executeResult) {
            result = (Map) selectRequestMapActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectRequestMapActionService.buildFailureResultForUI(null)
        }
        render result as JSON
    }

    /**
     * Reset Request Map
     */
    def resetRequestMap() {
        Map preResult = (Map) resetRequestMapByPluginIdActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        Boolean success = (Boolean) resetRequestMapByPluginIdActionService.execute(params, null)
        if (!success.booleanValue()) {
            result = (Map) resetRequestMapByPluginIdActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) resetRequestMapByPluginIdActionService.buildSuccessResultForUI(null)
        }
        render(result as JSON)
    }

}
