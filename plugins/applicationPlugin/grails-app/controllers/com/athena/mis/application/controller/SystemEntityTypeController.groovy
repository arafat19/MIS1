package com.athena.mis.application.controller

import com.athena.mis.application.actions.systementitytype.*
import grails.converters.JSON

class SystemEntityTypeController {

    static allowedMethods = [
            show: "POST", select: "POST",
            update: "POST", list: "POST"
    ]

    ShowSystemEntityTypeActionService showSystemEntityTypeActionService
    SelectSystemEntityTypeActionService selectSystemEntityTypeActionService
    UpdateSystemEntityTypeActionService updateSystemEntityTypeActionService
    SearchSystemEntityTypeActionService searchSystemEntityTypeActionService
    ListSystemEntityTypeActionService listSystemEntityTypeActionService

    def show() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showSystemEntityTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showSystemEntityTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showSystemEntityTypeActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/systemEntityType/show', model: [output: result as JSON])
    }

    def select() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) selectSystemEntityTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSystemEntityTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectSystemEntityTypeActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateSystemEntityTypeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSystemEntityTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateSystemEntityTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSystemEntityTypeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateSystemEntityTypeActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchSystemEntityTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchSystemEntityTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchSystemEntityTypeActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listSystemEntityTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listSystemEntityTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listSystemEntityTypeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
