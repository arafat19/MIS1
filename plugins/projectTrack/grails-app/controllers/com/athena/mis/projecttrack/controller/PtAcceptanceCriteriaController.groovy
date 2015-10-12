package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.ptacceptancecriteria.*
import grails.converters.JSON

class PtAcceptanceCriteriaController {

    ShowPtAcceptanceCriteriaActionService showPtAcceptanceCriteriaActionService
    ListPtAcceptanceCriteriaActionService listPtAcceptanceCriteriaActionService
    SelectPtAcceptanceCriteriaActionService selectPtAcceptanceCriteriaActionService
    CreatePtAcceptanceCriteriaActionService createPtAcceptanceCriteriaActionService
    UpdatePtAcceptanceCriteriaActionService updatePtAcceptanceCriteriaActionService
    DeletePtAcceptanceCriteriaActionService deletePtAcceptanceCriteriaActionService

    UpdatePtAcceptanceCriteriaForMyBacklogActionService updatePtAcceptanceCriteriaForMyBacklogActionService
    ListPtAcceptanceCriteriaForMyBacklogActionService listPtAcceptanceCriteriaForMyBacklogActionService
    ShowPtAcceptanceCriteriaForMyBacklogActionService showPtAcceptanceCriteriaForMyBacklogActionService

    static allowedMethods = [
            create: "POST",
            show: "POST",
            select: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            listForMyBacklog: "POST",
            updateForMyBacklog: "POST",
            showForMyBacklog: "POST"]

    def show() {
        Map result
        Boolean isError

        Map preResult = (Map) showPtAcceptanceCriteriaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtAcceptanceCriteriaActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) showPtAcceptanceCriteriaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtAcceptanceCriteriaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtAcceptanceCriteriaActionService.buildSuccessResultForUI(executeResult)
        }
        String leftMenu = params.leftMenu
        long backlogId = (long) result.backlogId
        render(view: '/projectTrack/ptAcceptanceCriteria/show', model: [output: result as JSON, leftMenu: leftMenu, backlogId: backlogId])
    }

    def showForMyBacklog() {
        Map result
        Boolean isError

        Map preResult = (Map) showPtAcceptanceCriteriaForMyBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtAcceptanceCriteriaForMyBacklogActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) showPtAcceptanceCriteriaForMyBacklogActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtAcceptanceCriteriaForMyBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtAcceptanceCriteriaForMyBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/projectTrack/ptAcceptanceCriteria/showForMyPtBacklog', model: [output: result as JSON])
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        executeResult = (Map) listPtAcceptanceCriteriaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listPtAcceptanceCriteriaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listPtAcceptanceCriteriaActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def listForMyBacklog() {
        Map result
        Map executeResult
        Boolean isError
        String output

        executeResult = (Map) listPtAcceptanceCriteriaForMyBacklogActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listPtAcceptanceCriteriaForMyBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listPtAcceptanceCriteriaForMyBacklogActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def select() {
        Map result
        String output
        Boolean isError

        Map preResult = (Map) selectPtAcceptanceCriteriaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectPtAcceptanceCriteriaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) selectPtAcceptanceCriteriaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectPtAcceptanceCriteriaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectPtAcceptanceCriteriaActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createPtAcceptanceCriteriaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtAcceptanceCriteriaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createPtAcceptanceCriteriaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createPtAcceptanceCriteriaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createPtAcceptanceCriteriaActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updatePtAcceptanceCriteriaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtAcceptanceCriteriaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updatePtAcceptanceCriteriaActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtAcceptanceCriteriaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtAcceptanceCriteriaActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def updateForMyBacklog() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updatePtAcceptanceCriteriaForMyBacklogActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtAcceptanceCriteriaForMyBacklogActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updatePtAcceptanceCriteriaForMyBacklogActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updatePtAcceptanceCriteriaForMyBacklogActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updatePtAcceptanceCriteriaForMyBacklogActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deletePtAcceptanceCriteriaActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtAcceptanceCriteriaActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deletePtAcceptanceCriteriaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deletePtAcceptanceCriteriaActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deletePtAcceptanceCriteriaActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

}
