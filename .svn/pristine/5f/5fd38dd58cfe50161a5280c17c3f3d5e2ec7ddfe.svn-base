package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptSprint.*
import grails.converters.JSON

class PtSprintController {
    PtShowSprintActionService ptShowSprintActionService
    PtCreateSprintActionService ptCreateSprintActionService
    PtUpdateSprintActionService ptUpdateSprintActionService
    PtSelectSprintActionService ptSelectSprintActionService
    PtDeleteSprintActionService ptDeleteSprintActionService
    PtListSprintActionService ptListSprintActionService
    PtSearchSprintActionService ptSearchSprintActionService
    PtGetSprintListByProjectIdActionService ptGetSprintListByProjectIdActionService
    PtGetInActiveSprintListByProjectIdActionService ptGetInActiveSprintListByProjectIdActionService

    static allowedMethods = [create: "POST", select: "POST", update: "POST", delete: "POST", list: "POST",
            listSprintByProjectId: "POST", listInActiveSprintByProjectId: "POST"
    ]

    /**
     *  Show Sprint list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) ptShowSprintActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptShowSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) ptShowSprintActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptSprint/show', model: [output: result as JSON])
    }

    /**
     * Create Sprint
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) ptCreateSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptCreateSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) ptCreateSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptCreateSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) ptCreateSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output

    }

    /**
     *  Update Sprint
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) ptUpdateSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptUpdateSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) ptUpdateSprintActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptUpdateSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) ptUpdateSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output

    }

    /**
     *  Delete Sprint
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) ptDeleteSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptDeleteSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) ptDeleteSprintActionService.execute(null,preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptDeleteSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) ptDeleteSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output

    }

    /**
     *  List and Search Sprint
     */
    def list() {
        Map result;
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) ptSearchSprintActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) ptSearchSprintActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) ptSearchSprintActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) ptListSprintActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) ptListSprintActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) ptListSprintActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output

    }

    /**
     *  Select Sprint
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) ptSelectSprintActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) ptSelectSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) ptSelectSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output

    }

    /**
     * get list of Sprint by projectId
     */
    def listSprintByProjectId() {
        String output

        Map executeResult = (Map) ptGetSprintListByProjectIdActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            Map result = (Map) ptGetSprintListByProjectIdActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        output = executeResult as JSON
        render output
    }

    /**
     * get list of inactive Sprint by projectId
     */
    def listInActiveSprintByProjectId() {
        String output

        Map executeResult = (Map) ptGetInActiveSprintListByProjectIdActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            Map result = (Map) ptGetInActiveSprintListByProjectIdActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        output = executeResult as JSON
        render output
    }
}
