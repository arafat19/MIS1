package com.athena.mis.application.controller

import com.athena.mis.application.actions.appgroup.*
import grails.converters.JSON

class AppGroupController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST", edit: "POST", update: "POST", delete: "POST", list: "POST"];

    ShowAppGroupActionService showAppGroupActionService
    CreateAppGroupActionService createAppGroupActionService
    ListAppGroupActionService listAppGroupActionService
    SelectAppGroupActionService selectAppGroupActionService
    UpdateAppGroupActionService updateAppGroupActionService
    DeleteAppGroupActionService deleteAppGroupActionService
    SearchAppGroupActionService searchAppGroupActionService
    /**
     * show user group list
     */
    def show() {
        Map result;
        Map executeResult
        Boolean isError

        executeResult = (Map) showAppGroupActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showAppGroupActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showAppGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/appGroup/show', model: [output: result as JSON])
    }
    /**
     * create user group
     */
    def create() {
        Map preResult
        Map executeResult;
        Map result;
        Boolean isError

        preResult = (Map) createAppGroupActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createAppGroupActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppGroupActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createAppGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * select user group
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) selectAppGroupActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAppGroupActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAppGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }
    /**
     * update user group
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateAppGroupActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateAppGroupActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppGroupActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAppGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * delete user group
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteAppGroupActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteAppGroupActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppGroupActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteAppGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * list and search user group
     */
    def list() {
        Map result;
        Map executeResult;
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchAppGroupActionService.execute(params, null);

            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchAppGroupActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchAppGroupActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listAppGroupActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listAppGroupActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listAppGroupActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
