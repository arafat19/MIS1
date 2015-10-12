package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.CreateExhAgentCurrencyPostingActionService
import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.DeleteExhAgentCurrencyPostingActionService
import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.ListExhAgentCurrencyPostingActionService
import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.SearchExhAgentCurrencyPostingActionService
import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.SelectExhAgentCurrencyPostingActionService
import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.ShowExhAgentCurrencyPostingActionService
import com.athena.mis.exchangehouse.actions.agentCurrencyPosting.UpdateExhAgentCurrencyPostingActionService
import grails.converters.JSON

class ExhAgentCurrencyPostingController {

    static allowedMethods = [create: "POST", update: "POST", select: "POST", list: "POST", delete: "POST", show: "POST"];

    ShowExhAgentCurrencyPostingActionService showExhAgentCurrencyPostingActionService
    CreateExhAgentCurrencyPostingActionService createExhAgentCurrencyPostingActionService
    SearchExhAgentCurrencyPostingActionService searchExhAgentCurrencyPostingActionService
    SelectExhAgentCurrencyPostingActionService selectExhAgentCurrencyPostingActionService
    ListExhAgentCurrencyPostingActionService listExhAgentCurrencyPostingActionService
    UpdateExhAgentCurrencyPostingActionService updateExhAgentCurrencyPostingActionService
    DeleteExhAgentCurrencyPostingActionService deleteExhAgentCurrencyPostingActionService

    def show() {
        Map result;
        Map executeResult
        executeResult = (Map) showExhAgentCurrencyPostingActionService.execute(params, null)
        boolean isError = ((Boolean) executeResult.isError).booleanValue()
        if (isError) {
            result = (Map) showExhAgentCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render(view: '/exchangehouse/exhAgentCurrencyPosting/show', model: [output: output])
    }

    def create() {
        String output
        Map preResult = (Map) createExhAgentCurrencyPostingActionService.executePreCondition(params, null)
        Map result
        boolean isError = ((Boolean) preResult.isError).booleanValue()
        if (isError) {
            result = (Map) createExhAgentCurrencyPostingActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }
        Map executeResult
        executeResult = (Map) createExhAgentCurrencyPostingActionService.execute(null, preResult);
        if (executeResult) {
            executeResult = (Map) createExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult);
        } else {
            executeResult = (Map) createExhAgentCurrencyPostingActionService.buildFailureResultForUI(null);
        }
        output = executeResult as JSON
        render output

    }

    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        preResult = (LinkedHashMap) updateExhAgentCurrencyPostingActionService.executePreCondition(params, null)
        boolean isError = ((Boolean) preResult.isError).booleanValue()
        if (isError) {
            result = (LinkedHashMap) updateExhAgentCurrencyPostingActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }
        executeResult = (LinkedHashMap) updateExhAgentCurrencyPostingActionService.execute(params, preResult)
        isError = ((Boolean) executeResult.isError).booleanValue()
        if (isError) {
            result = (LinkedHashMap) updateExhAgentCurrencyPostingActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (LinkedHashMap) updateExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def delete() {
        Map preResult
        Map executeResult
        Map result
        preResult = (Map) deleteExhAgentCurrencyPostingActionService.executePreCondition(params, null)
        boolean isError = ((Boolean) preResult.isError).booleanValue()
        if (isError) {
            result = (Map) deleteExhAgentCurrencyPostingActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteExhAgentCurrencyPostingActionService.execute(params, preResult)
        isError = ((Boolean) executeResult.isError).booleanValue()
        if (isError) {
            result = (Map) deleteExhAgentCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def select() {
        Map result
        Map executeResult = (Map) selectExhAgentCurrencyPostingActionService.execute(params, null)
        boolean isError = ((Boolean) executeResult.isError).booleanValue()
        if (isError) {
            result = (LinkedHashMap) selectExhAgentCurrencyPostingActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
        }
        render (result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        boolean isError
        if (params.query) {
            executeResult = (Map) searchExhAgentCurrencyPostingActionService.execute(params, null);
            isError = ((Boolean) executeResult.isError).booleanValue()
            if (isError) {
                result = (Map) searchExhAgentCurrencyPostingActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listExhAgentCurrencyPostingActionService.execute(params, null)
            isError = ((Boolean) executeResult.isError).booleanValue()
            if (isError) {
                result = (Map) listExhAgentCurrencyPostingActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listExhAgentCurrencyPostingActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

}
