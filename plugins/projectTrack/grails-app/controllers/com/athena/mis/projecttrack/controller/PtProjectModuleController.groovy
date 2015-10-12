package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptprojectmodule.CreatePtProjectModuleActionService
import com.athena.mis.projecttrack.actions.ptprojectmodule.DeletePtProjectModuleActionService
import com.athena.mis.projecttrack.actions.ptprojectmodule.ListPtProjectModuleActionService
import com.athena.mis.projecttrack.actions.ptprojectmodule.SearchPtProjectModuleActionService
import com.athena.mis.projecttrack.actions.ptprojectmodule.SelectPtProjectModuleActionService
import com.athena.mis.projecttrack.actions.ptprojectmodule.ShowPtProjectModuleActionService
import com.athena.mis.projecttrack.actions.ptprojectmodule.UpdatePtProjectModuleActionService
import grails.converters.JSON

class PtProjectModuleController {

    ShowPtProjectModuleActionService showPtProjectModuleActionService
    ListPtProjectModuleActionService listPtProjectModuleActionService
    SearchPtProjectModuleActionService searchPtProjectModuleActionService
    SelectPtProjectModuleActionService selectPtProjectModuleActionService
    CreatePtProjectModuleActionService createPtProjectModuleActionService
    UpdatePtProjectModuleActionService updatePtProjectModuleActionService
    DeletePtProjectModuleActionService deletePtProjectModuleActionService

    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]

    def show() {
        Map result
        Map preResult
        Boolean isError
        String output

        preResult = (Map) showPtProjectModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtProjectModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) showPtProjectModuleActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtProjectModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtProjectModuleActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptProjectModule/show', model: [output: result as JSON])
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output
        Map preResult

        if (params.query) {
            executeResult = (Map) searchPtProjectModuleActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchPtProjectModuleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchPtProjectModuleActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            preResult = (Map) listPtProjectModuleActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) listPtProjectModuleActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) listPtProjectModuleActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listPtProjectModuleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listPtProjectModuleActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result
        String output

        Map executeResult = (Map) selectPtProjectModuleActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPtProjectModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectPtProjectModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createPtProjectModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtProjectModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createPtProjectModuleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtProjectModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createPtProjectModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updatePtProjectModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtProjectModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updatePtProjectModuleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtProjectModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtProjectModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deletePtProjectModuleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtProjectModuleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deletePtProjectModuleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtProjectModuleActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePtProjectModuleActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
