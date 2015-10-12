package com.athena.mis.inventory.controller

import com.athena.mis.application.entity.AppUser
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.actions.invproductionlineitem.*
import com.athena.mis.inventory.entity.InvProductionLineItem
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class InvProductionLineItemController {
    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]


    CreateInvProductionLineItemActionService createInvProductionLineItemActionService
    DeleteInvProductionLineItemActionService deleteInvProductionLineItemActionService
    ListInvProductionLineItemActionService listInvProductionLineItemActionService
    SearchInvProductionLineItemActionService searchInvProductionLineItemActionService
    SelectInvProductionLineItemActionService selectInvProductionLineItemActionService
    ShowInvProductionLineItemActionService showInvProductionLineItemActionService
    UpdateInvProductionLineItemActionService updateInvProductionLineItemActionService

    @Autowired
    InvSessionUtil invSessionUtil

    def show() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showInvProductionLineItemActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showInvProductionLineItemActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showInvProductionLineItemActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/inventory/invProductionLineItem/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError
        InvProductionLineItem invProductionLineItem = buildInvProductionLineItem(params)
        preResult = (Map) createInvProductionLineItemActionService.executePreCondition(params, invProductionLineItem)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createInvProductionLineItemActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }
        executeResult = (Map) createInvProductionLineItemActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createInvProductionLineItemActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createInvProductionLineItemActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    def list() {
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchInvProductionLineItemActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchInvProductionLineItemActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchInvProductionLineItemActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listInvProductionLineItemActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listInvProductionLineItemActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) listInvProductionLineItemActionService.buildSuccessResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteInvProductionLineItemActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteInvProductionLineItemActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        boolean deleteResult = ((Boolean) deleteInvProductionLineItemActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteInvProductionLineItemActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteInvProductionLineItemActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def select() {
        Map executeResult
        Map result
        executeResult = (Map) selectInvProductionLineItemActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectInvProductionLineItemActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectInvProductionLineItemActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError
        InvProductionLineItem invProductionLineItem = buildInvProductionLineItem(params)
        invProductionLineItem.id = Long.parseLong(params.id.toString());
        invProductionLineItem.version = Integer.parseInt(params.version.toString());
        preResult = (Map) updateInvProductionLineItemActionService.executePreCondition(null, invProductionLineItem)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateInvProductionLineItemActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateInvProductionLineItemActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateInvProductionLineItemActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateInvProductionLineItemActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    private InvProductionLineItem buildInvProductionLineItem(GrailsParameterMap params) {
        InvProductionLineItem invProductionLineItem = new InvProductionLineItem(params);
        AppUser appUser = invSessionUtil.appSessionUtil.getAppUser()
        invProductionLineItem.companyId = appUser.companyId
        return invProductionLineItem
    }
}