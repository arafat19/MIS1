package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acccustomgroup.*
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class AccCustomGroupController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST",delete: "POST" ,list: "POST" ,search: "POST" ]

    ShowAccCustomGroupActionService showAccCustomGroupActionService
    CreateAccCustomGroupActionService createAccCustomGroupActionService
    SelectAccCustomGroupActionService selectAccCustomGroupActionService
    UpdateAccCustomGroupActionService updateAccCustomGroupActionService
    DeleteAccCustomGroupActionService deleteAccCustomGroupActionService
    ListAccCustomGroupActionService listAccCustomGroupActionService
    SearchAccCustomGroupActionService searchAccCustomGroupActionService

    def show() {
        Map preResult = (Map) showAccCustomGroupActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showAccCustomGroupActionService.execute(params, null);
        Map result;
        if (executeResult) {
            result = (Map) showAccCustomGroupActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showAccCustomGroupActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/accounting/accCustomGroup/show', model: [output: result as JSON])
    }

    def create() {
        AccCustomGroup accCustomGroup = new AccCustomGroup(params);
        Map preResult = (Map) createAccCustomGroupActionService.executePreCondition(null, accCustomGroup)
        Boolean hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result;
        Boolean isError = (Boolean) preResult.isError;
        if (isError.booleanValue()) {
//            result = Map) createAccCustomGroupActionService.formatValidationErrorsForUI(accCustomGroup, {g.message(error: it)}, false)
            result = (Map) createAccCustomGroupActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return;
        }
        AccCustomGroup savedAccCustomGroup = (AccCustomGroup) createAccCustomGroupActionService.execute(params, accCustomGroup);
        if (savedAccCustomGroup) {
            result = (Map) createAccCustomGroupActionService.buildSuccessResultForUI(savedAccCustomGroup);
        } else {
            result = (Map) createAccCustomGroupActionService.buildFailureResultForUI(null);
        }

        render(result as JSON)
    }

    def select() {
        Map preResult
        Map executeResult
        Map result
        preResult = (Map) selectAccCustomGroupActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        executeResult = (Map) selectAccCustomGroupActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccCustomGroupActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectAccCustomGroupActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    def update() {
        AccCustomGroup accCustomGroup = new AccCustomGroup(params)
        accCustomGroup.id = Long.parseLong(params.id.toString());
        accCustomGroup.version = Integer.parseInt(params.version.toString());
        Map preResult = (Map) updateAccCustomGroupActionService.executePreCondition(null, accCustomGroup)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result;
        Boolean isError = (Boolean) preResult.isError;
        if (isError.booleanValue()) {
//            result = (Map) updateAccCustomGroupActionService.formatValidationErrorsForUI(accCustomGroup, {g.message(error: it)}, false)
            result = (Map) updateAccCustomGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
       Map executeResult = (Map) updateAccCustomGroupActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccCustomGroupActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccCustomGroupActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccCustomGroupActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccCustomGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        boolean deleteResult = ((Boolean) deleteAccCustomGroupActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteAccCustomGroupActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteAccCustomGroupActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def list() {
        Map preResult;
        Boolean hasAccess;
        Map result;
        Map executeResult;
        if (params.query) {
            preResult = (Map) searchAccCustomGroupActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchAccCustomGroupActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchAccCustomGroupActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchAccCustomGroupActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            preResult = (Map) listAccCustomGroupActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listAccCustomGroupActionService.execute(params, null);
            if (executeResult) {
                result = (Map) listAccCustomGroupActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listAccCustomGroupActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

}

