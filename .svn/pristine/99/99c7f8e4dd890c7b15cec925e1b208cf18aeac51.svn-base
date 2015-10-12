package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.useragent.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ExhUserAgentController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST", select: "POST"]

    CreateUserAgentActionService createUserAgentActionService
    UpdateUserAgentActionService updateUserAgentActionService
    ListUserAgentActionService listUserAgentActionService
    SearchUserAgentActionService searchUserAgentActionService
    DeleteUserAgentActionService deleteUserAgentActionService
    ShowUserAgentActionService showUserAgentActionService
    SelectUserAgentActionService selectUserAgentActionService

    def show() {
        Map result

        Map executeResult = (Map) showUserAgentActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showUserAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showUserAgentActionService.buildSuccessResultForUI(executeResult)
        }
        Long agentId = (Long) executeResult.agentId
        render(view: '/exchangehouse/userAgent/show', model: [agentId: agentId, output: result as JSON])
    }

    def create() {
        Boolean isError
        Map result
        String output

        Map preResult = (Map) createUserAgentActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createUserAgentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createUserAgentActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createUserAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createUserAgentActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Boolean isError
        Map result
        String output

        Map preResult = (Map) updateUserAgentActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateUserAgentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateUserAgentActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateUserAgentActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) updateUserAgentActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteUserAgentActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteUserAgentActionService.buildFailureResultForUI(null)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteUserAgentActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteUserAgentActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteUserAgentActionService.buildFailureResultForUI(null)
        }
        output = result as JSON
        render output
    }

    def list() {
        Boolean isError
        Map result
        Map executeResult
        if (params.query) {
            executeResult = (Map) searchUserAgentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchUserAgentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchUserAgentActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listUserAgentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listUserAgentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listUserAgentActionService.buildSuccessResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    def select() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) selectUserAgentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectUserAgentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) selectUserAgentActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectUserAgentActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        output = executeResult as JSON
        render output
    }
}
