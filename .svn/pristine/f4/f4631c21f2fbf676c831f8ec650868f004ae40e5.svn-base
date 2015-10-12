package com.athena.mis.application.controller

import com.athena.mis.application.actions.designation.*
import grails.converters.JSON

class DesignationController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST",
            edit: "POST", update: "POST", delete: "POST", list: "POST"]

    ShowDesignationActionService showDesignationActionService
    CreateDesignationActionService createDesignationActionService
    ListDesignationActionService listDesignationActionService
    SelectDesignationActionService selectDesignationActionService
    UpdateDesignationActionService updateDesignationActionService
    DeleteDesignationActionService deleteDesignationActionService
    SearchDesignationActionService searchDesignationActionService

    /**
     * show designation list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showDesignationActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showDesignationActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showDesignationActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/designation/show', model: [output: result as JSON])
    }
    /**
     * create designation
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createDesignationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showDesignationActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createDesignationActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDesignationActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) createDesignationActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     * select designation
     */
    def select() {
        Map executeResult
        executeResult = (Map) selectDesignationActionService.execute(params, null)
        render(executeResult as JSON)
    }
    /**
     * update designation
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateDesignationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDesignationActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateDesignationActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDesignationActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) updateDesignationActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     * delete designation
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteDesignationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteDesignationActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteDesignationActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteDesignationActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) deleteDesignationActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }
    /**
     * list and search designation
     */
    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchDesignationActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDesignationActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDesignationActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listDesignationActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listDesignationActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listDesignationActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
