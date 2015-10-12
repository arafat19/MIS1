package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptproject.CreatePtProjectActionService
import com.athena.mis.projecttrack.actions.ptproject.DeletePtProjectActionService
import com.athena.mis.projecttrack.actions.ptproject.ListPtProjectActionService
import com.athena.mis.projecttrack.actions.ptproject.SearchPtProjectActionService
import com.athena.mis.projecttrack.actions.ptproject.SelectPtProjectActionService
import com.athena.mis.projecttrack.actions.ptproject.ShowPtProjectActionService
import com.athena.mis.projecttrack.actions.ptproject.UpdatePtProjectActionService
import grails.converters.JSON

class PtProjectController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST", select: "POST", list: "POST"]

    ShowPtProjectActionService showPtProjectActionService
    CreatePtProjectActionService createPtProjectActionService
    UpdatePtProjectActionService updatePtProjectActionService
    DeletePtProjectActionService deletePtProjectActionService
    SelectPtProjectActionService selectPtProjectActionService
    ListPtProjectActionService listPtProjectActionService
    SearchPtProjectActionService searchPtProjectActionService

    /**
     * Show project list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showPtProjectActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtProjectActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtProjectActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptProject/show', model: [output: result as JSON])
    }

    /**
     * Create Project object
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createPtProjectActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtProjectActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createPtProjectActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtProjectActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createPtProjectActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Update project object
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updatePtProjectActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtProjectActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updatePtProjectActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtProjectActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtProjectActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Delete project object
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deletePtProjectActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtProjectActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deletePtProjectActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtProjectActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePtProjectActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Select project
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectPtProjectActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPtProjectActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectPtProjectActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * Get list and search project
     */
    def list() {
        Map result;
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchPtProjectActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchPtProjectActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchPtProjectActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listPtProjectActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listPtProjectActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listPtProjectActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }



}
