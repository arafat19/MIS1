package com.athena.mis.application.controller

import com.athena.mis.application.actions.role.*
import com.athena.mis.application.entity.Role
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class RoleController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", edit: "POST", delete: "POST", list: "POST"];

    CreateRoleActionService createRoleActionService
    UpdateRoleActionService updateRoleActionService
    SelectRoleActionService selectRoleActionService
    ListRoleActionService listRoleActionService
    SearchRoleActionService searchRoleActionService
    DeleteRoleActionService deleteRoleActionService
    ShowRoleActionService showRoleActionService

    def show() {
        Map preResult = (Map) showRoleActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showRoleActionService.execute(params, null);
        Map result;
        if (executeResult) {
            result = (Map) showRoleActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showRoleActionService.buildFailureResultForUI(null);
        }
        render(view: '/application/role/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult = (Map) createRoleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result;
        Boolean isError = (Boolean) preResult.isError;
        if (isError.booleanValue()) {
            result = (Map) createRoleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Role savedRole = (Role) createRoleActionService.execute(params, preResult);
        if (savedRole) {
            result = (Map) createRoleActionService.buildSuccessResultForUI(savedRole);
        } else {
            result = (Map) createRoleActionService.buildFailureResultForUI(null);
        }

        render(result as JSON)
    }

    def update() {
        Boolean isError
        Map result

        Map preResult = (Map) updateRoleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError;
        if (isError.booleanValue()) {
            result = (Map) updateRoleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Map executeResult = (Map) updateRoleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateRoleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateRoleActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }

    def select() {
        Map preResult = (Map) selectRoleActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Map result
        Map executeResult = (Map) selectRoleActionService.execute(params, null);
        if (executeResult) {
            result = (Map) selectRoleActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) selectRoleActionService.buildFailureResultForUI(null);
        }
        render result as JSON
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteRoleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteRoleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        boolean deleteResult = ((Boolean) deleteRoleActionService.execute(params, null)).booleanValue();
        if (deleteResult.booleanValue()) {
            result = (Map) deleteRoleActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteRoleActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def list() {
        Map preResult;
        Boolean hasAccess;
        Map result;
        Map executeResult;

        if (params.query) {
            preResult = (Map) searchRoleActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchRoleActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchRoleActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchRoleActionService.buildFailureResultForUI(null);
            }
        } else {
            preResult = (Map) listRoleActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listRoleActionService.execute(params, null);
            if (executeResult) {
                result = (Map) listRoleActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listRoleActionService.buildFailureResultForUI(null);
            }
        }
        render(result as JSON);
    }
}
