package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acctier1.*
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired

class AccTier1Controller {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST",
            delete: "POST", list: "POST", getTier1ByAccTypeId: "POST"]

    CreateAccTier1ActionService createAccTier1ActionService
    DeleteAccTier1ActionService deleteAccTier1ActionService
    ListAccTier1ActionService listAccTier1ActionService
    SearchAccTier1ActionService searchAccTier1ActionService
    SelectAccTier1ActionService selectAccTier1ActionService
    ShowAccTier1ActionService showAccTier1ActionService
    UpdateAccTier1ActionService updateAccTier1ActionService
    GetTier1ListByAccTypeIdActionService getTier1ListByAccTypeIdActionService
    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showAccTier1ActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccTier1ActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showAccTier1ActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/accounting/accTier1/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) createAccTier1ActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccTier1ActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }

        executeResult = (Map) createAccTier1ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccTier1ActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createAccTier1ActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    def list() {
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchAccTier1ActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchAccTier1ActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchAccTier1ActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listAccTier1ActionService.execute(params, null);
            if (executeResult) {
                result = (Map) listAccTier1ActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listAccTier1ActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccTier1ActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccTier1ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        boolean deleteResult = ((Boolean) deleteAccTier1ActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteAccTier1ActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteAccTier1ActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def select() {
        Map executeResult
        Map result
        executeResult = (Map) selectAccTier1ActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccTier1ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectAccTier1ActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) updateAccTier1ActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccTier1ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) updateAccTier1ActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccTier1ActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccTier1ActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def getTier1ByAccTypeId() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        Boolean hasAccess
        String output

        preResult = (LinkedHashMap) getTier1ListByAccTypeIdActionService.executePreCondition(params, null)

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getTier1ListByAccTypeIdActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getTier1ListByAccTypeIdActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getTier1ListByAccTypeIdActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        output = executeResult as JSON
        render output
    }
}

