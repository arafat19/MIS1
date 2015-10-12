package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptmodule.*
import grails.converters.JSON

class PtModuleController {

    ShowPtModuleActionService showPtModuleActionService
    ListPtModuleActionService listPtModuleActionService
    SearchPtModuleActionService searchPtModuleActionService
    SelectPtModuleActionService selectPtModuleActionService
    CreatePtModuleActionService createPtModuleActionService
    UpdatePtModuleActionService updatePtModuleActionService
    DeletePtModuleActionService deletePtModuleActionService
    GetModuleListForBacklogReportActionService getModuleListForBacklogReportActionService

    static allowedMethods = [
            create: "POST",
            show: "POST",
            select: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            getModuleList: "POST"
    ]

    /**
     * Get module list by project id
     */
    def getModuleList() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) getModuleListForBacklogReportActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getModuleListForBacklogReportActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) getModuleListForBacklogReportActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getModuleListForBacklogReportActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) getModuleListForBacklogReportActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def show() {
        Map result

        Map executeResult = (Map) showPtModuleActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtModuleActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptModule/show', model: [output: result as JSON])
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchPtModuleActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchPtModuleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchPtModuleActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listPtModuleActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listPtModuleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listPtModuleActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result
        String output

        Map executeResult = (Map) selectPtModuleActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPtModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectPtModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createPtModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createPtModuleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createPtModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updatePtModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updatePtModuleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deletePtModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deletePtModuleActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePtModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
