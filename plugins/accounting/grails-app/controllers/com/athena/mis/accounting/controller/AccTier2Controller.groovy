package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acctier2.*
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Autowired

class AccTier2Controller {
    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST",
            delete: "POST", list: "POST", getTier2ByAccTier1Id: "POST"]

    ShowAccTier2ActionService showAccTier2ActionService
    CreateAccTier2ActionService createAccTier2ActionService
    UpdateAccTier2ActionService updateAccTier2ActionService
    DeleteAccTier2ActionService deleteAccTier2ActionService
    ListAccTier2ActionService listAccTier2ActionService
    SelectAccTier2ActionService selectAccTier2ActionService
    SearchAccTier2ActionService searchAccTier2ActionService
    GetTier2ListByAccTier1IdActionService getTier2ListByAccTier1IdActionService
    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showAccTier2ActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccTier2ActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showAccTier2ActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/accounting/accTier2/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) createAccTier2ActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccTier2ActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }

        executeResult = (Map) createAccTier2ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccTier2ActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createAccTier2ActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    def list() {
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchAccTier2ActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchAccTier2ActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchAccTier2ActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listAccTier2ActionService.execute(params, null);
            if (executeResult) {
                result = (Map) listAccTier2ActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listAccTier2ActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccTier2ActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccTier2ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        boolean deleteResult = ((Boolean) deleteAccTier2ActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteAccTier2ActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteAccTier2ActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def select() {
        Map executeResult
        Map result
        executeResult = (Map) selectAccTier2ActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccTier2ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectAccTier2ActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) updateAccTier2ActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccTier2ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) updateAccTier2ActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccTier2ActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccTier2ActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def getTier2ByAccTier1Id() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        Boolean hasAccess
        String output

        preResult = (LinkedHashMap) getTier2ListByAccTier1IdActionService.executePreCondition(params, null)

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getTier2ListByAccTier1IdActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getTier2ListByAccTier1IdActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getTier2ListByAccTier1IdActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        output = executeResult as JSON
        render output
    }
}
