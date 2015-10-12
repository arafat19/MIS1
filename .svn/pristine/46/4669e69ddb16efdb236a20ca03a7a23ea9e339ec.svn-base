package com.athena.mis.application.controller

import com.athena.mis.application.actions.appuserentity.*
import grails.converters.JSON

class AppUserEntityController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", select: "POST",
            delete: "POST", list: "POST", search: "POST", dropDownAppUserEntityReload: "POST"];

    ShowAppUserEntityActionService showAppUserEntityActionService
    CreateAppUserEntityActionService createAppUserEntityActionService
    UpdateAppUserEntityActionService updateAppUserEntityActionService
    DeleteAppUserEntityActionService deleteAppUserEntityActionService
    ListAppUserEntityActionService listAppUserEntityActionService
    SearchAppUserEntityActionService searchAppUserEntityActionService
    SelectAppUserEntityActionService selectAppUserEntityActionService

    def show() {
        Map result
        Map executeResult = (Map) showAppUserEntityActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showAppUserEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showAppUserEntityActionService.buildSuccessResultForUI(executeResult)
        }
        Long entityId = (Long) result.entityId
        Long reservedId = (Long) result.reservedId
        render(view: '/application/appUserEntity/show', model: [entityId: entityId, reservedId: reservedId, output: result as JSON])
    }

    def select() {
        Map result

        Map executeResult = (Map) selectAppUserEntityActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAppUserEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAppUserEntityActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    def create() {
        Map result
        String output
        Boolean isError

        Map preResult = (Map) createAppUserEntityActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppUserEntityActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createAppUserEntityActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppUserEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createAppUserEntityActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updateAppUserEntityActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppUserEntityActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateAppUserEntityActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppUserEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAppUserEntityActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteAppUserEntityActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppUserEntityActionService.buildFailureResultForUI(null)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) deleteAppUserEntityActionService.execute(null, preResult))
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAppUserEntityActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteAppUserEntityActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError

        if (params.query) {
            executeResult = (Map) searchAppUserEntityActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchAppUserEntityActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchAppUserEntityActionService.buildFailureResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listAppUserEntityActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listAppUserEntityActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listAppUserEntityActionService.buildFailureResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    def dropDownAppUserEntityReload() {
        render app.dropDownAppUserEntity(params)
    }
}
