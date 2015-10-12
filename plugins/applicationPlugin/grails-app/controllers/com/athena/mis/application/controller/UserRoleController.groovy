package com.athena.mis.application.controller

import com.athena.mis.application.actions.userrole.*
import com.athena.mis.application.entity.UserRole
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class UserRoleController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST", select: "POST"]

    CreateUserRoleActionService createUserRoleActionService
    UpdateUserRoleActionService updateUserRoleActionService
    ListUserRoleActionService listUserRoleActionService
    SearchUserRoleActionService searchUserRoleActionService
    DeleteUserRoleActionService deleteUserRoleActionService
    ShowUserRoleActionService showUserRoleActionService
    SelectUserRoleActionService selectUserRoleActionService

    def show() {
        Map result

        Map preResult = (Map) showUserRoleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showUserRoleActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) showUserRoleActionService.execute(params, preResult)
        if (executeResult) {
            result = (Map) showUserRoleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showUserRoleActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/application/userRole/show', model: [output: result as JSON])
    }

    def create() {
        UserRole userRoleInstance = new UserRole(params)
        Map preResult = (Map) createUserRoleActionService.executePreCondition(params, userRoleInstance)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createUserRoleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        UserRole savedUserRole = (UserRole) createUserRoleActionService.execute(params, userRoleInstance)
        if (savedUserRole) {
            result = (Map) createUserRoleActionService.buildSuccessResultForUI(savedUserRole)
        } else {
            result = (Map) createUserRoleActionService.buildFailureResultForUI(null)
        }

        render(result as JSON)
    }

    def update() {
        Map preResult = (Map) updateUserRoleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map result
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateUserRoleActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) updateUserRoleActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateUserRoleActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateUserRoleActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteUserRoleActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteUserRoleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) deleteUserRoleActionService.execute(null, preResult))
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteUserRoleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteUserRoleActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map preResult
        Boolean hasAccess
        Boolean isError
        Map result
        Map executeResult
        String output

        if (params.query) {
            preResult = (Map) searchUserRoleActionService.executePreCondition(params, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchUserRoleActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) searchUserRoleActionService.execute(params, preResult)
            if (executeResult) {
                result = (Map) searchUserRoleActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchUserRoleActionService.buildFailureResultForUI(executeResult)
            }
        }
        else {
            preResult = (Map) listUserRoleActionService.executePreCondition(params, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) listUserRoleActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) listUserRoleActionService.execute(params, preResult)
            if (executeResult) {
                result = (Map) listUserRoleActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listUserRoleActionService.buildFailureResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result
        Map executeResult = (Map) selectUserRoleActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectUserRoleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectUserRoleActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }
}
