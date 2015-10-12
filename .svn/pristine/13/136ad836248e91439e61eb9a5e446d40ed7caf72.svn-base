package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.agent.CreateExhAgentActionService
import com.athena.mis.exchangehouse.actions.agent.DeleteExhAgentActionService
import com.athena.mis.exchangehouse.actions.agent.ListExhAgentActionService
import com.athena.mis.exchangehouse.actions.agent.SearchExhAgentActionService
import com.athena.mis.exchangehouse.actions.agent.SelectExhAgentActionService
import com.athena.mis.exchangehouse.actions.agent.ShowExhAgentActionService
import com.athena.mis.exchangehouse.actions.agent.UpdateExhAgentActionService
import grails.converters.JSON

class ExhAgentController {

    static allowedMethods = [create: "POST", update: "POST", select: "POST", list: "POST", delete: "POST", show: "POST"];

    CreateExhAgentActionService createExhAgentActionService
    SelectExhAgentActionService selectExhAgentActionService
    UpdateExhAgentActionService updateExhAgentActionService
    DeleteExhAgentActionService deleteExhAgentActionService
    ShowExhAgentActionService showExhAgentActionService
    ListExhAgentActionService listExhAgentActionService
    SearchExhAgentActionService searchExhAgentActionService

    def show() {
        Map executeResult = (Map) showExhAgentActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        Map result
        if (isError.booleanValue()) {
            result = (Map) showExhAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showExhAgentActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/exchangehouse/exhAgent/show', model: [output: result as JSON])
    }

    def create() {
        Map preResult = (Map) createExhAgentActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        Map result
        if (isError.booleanValue()) {
            result = (Map) createExhAgentActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) createExhAgentActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createExhAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createExhAgentActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def select() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) selectExhAgentActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectExhAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectExhAgentActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateExhAgentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhAgentActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateExhAgentActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateExhAgentActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteExhAgentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteExhAgentActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteExhAgentActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteExhAgentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteExhAgentActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def list() {
        Map result;
        Map executeResult;
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchExhAgentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchExhAgentActionService.buildFailureResultForUI(executeResult)
                render(result as JSON)
                return
            } else {
                result = (Map) searchExhAgentActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listExhAgentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listExhAgentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listExhAgentActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

}
