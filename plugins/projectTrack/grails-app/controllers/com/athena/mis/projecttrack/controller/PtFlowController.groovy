package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptflow.*
import grails.converters.JSON

class PtFlowController {

    ShowPtFlowActionService showPtFlowActionService
    ListPtFlowActionService listPtFlowActionService
    SelectPtFlowActionService selectPtFlowActionService
    CreatePtFlowActionService createPtFlowActionService
    UpdatePtFlowActionService updatePtFlowActionService
    DeletePtFlowActionService deletePtFlowActionService

    static allowedMethods = [
            create: "POST",
            show: "POST",
            select: "POST",
            update: "POST",
            delete: "POST",
            list: "POST"
    ]

    def show() {
        Map result
        Boolean isError

        Map preResult = (Map) showPtFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtFlowActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) showPtFlowActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtFlowActionService.buildSuccessResultForUI(executeResult)
        }
        long backlogId = (long) result.backlogId
        render(view: '/projectTrack/ptFlow/show', model: [output: result as JSON, backlogId: backlogId])
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        executeResult = (Map) listPtFlowActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listPtFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listPtFlowActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def select() {
        Map result
        String output
        Boolean isError

        Map preResult = (Map) selectPtFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectPtFlowActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) selectPtFlowActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPtFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectPtFlowActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createPtFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtFlowActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createPtFlowActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createPtFlowActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updatePtFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtFlowActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updatePtFlowActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtFlowActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deletePtFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtFlowActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deletePtFlowActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePtFlowActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
