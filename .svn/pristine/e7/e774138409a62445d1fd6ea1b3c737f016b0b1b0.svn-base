package com.athena.mis.application.controller

import com.athena.mis.application.actions.entitynote.*
import grails.converters.JSON

class EntityNoteController {

    static allowedMethods = [
            list: "POST", select: "POST", show: "POST", create: "POST", update: "POST", delete: "POST",
            reloadEntityNote: "POST"
    ]

    CreateEntityNoteActionService createEntityNoteActionService
    ListEntityNoteActionService listEntityNoteActionService
    SelectEntityNoteActionService selectEntityNoteActionService
    ShowEntityNoteActionService showEntityNoteActionService
    UpdateEntityNoteActionService updateEntityNoteActionService
    DeleteEntityNoteActionService deleteEntityNoteActionService
    SearchEntityNoteActionService searchEntityNoteActionService

    def show() {
        Map result

        Map executeResult = (Map) showEntityNoteActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showEntityNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showEntityNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/entityNote/show', model: [output: result as JSON])
    }

    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createEntityNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createEntityNoteActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createEntityNoteActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createEntityNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createEntityNoteActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result

        Map executeResult = (Map) selectEntityNoteActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectEntityNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectEntityNoteActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    def update() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) updateEntityNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateEntityNoteActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateEntityNoteActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateEntityNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateEntityNoteActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError

        if (params.query) {
            executeResult = (Map) searchEntityNoteActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchEntityNoteActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchEntityNoteActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listEntityNoteActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listEntityNoteActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listEntityNoteActionService.buildSuccessResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteEntityNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteEntityNoteActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteEntityNoteActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteEntityNoteActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteEntityNoteActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def reloadEntityNote() {
        def a = params
        render app.entityNote(params, null)
    }
}
