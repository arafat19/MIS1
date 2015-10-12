package com.athena.mis.application.controller

import com.athena.mis.application.actions.entitycontent.*
import com.athena.mis.application.entity.EntityContent
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class EntityContentController {

    static allowedMethods = [list: "POST", select: "POST", show: "POST", create: "POST",
            update: "POST", delete: "POST"]

    ShowEntityContentActionService showEntityContentActionService
    CreateEntityContentActionService createEntityContentActionService
    SelectEntityContentActionService selectEntityContentActionService
    DeleteEntityContentActionService deleteEntityContentActionService
    SearchEntityContentActionService searchEntityContentActionService
    ListEntityContentActionService listEntityContentActionService
    UpdateEntityContentActionService updateEntityContentActionService
    DownloadEntityContentActionService downloadEntityContentActionService

    def show() {
        Map result
        Map executeResult = (Map) showEntityContentActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showEntityContentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showEntityContentActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/entityContent/show', model: [output: result as JSON])
    }

    def create() {
        Map result
        Boolean isError
        String output
        Map executeResult

        Map preResult = (Map) createEntityContentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createEntityContentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (Map) createEntityContentActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createEntityContentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createEntityContentActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def select() {
        Map result
        Map executeResult
        String output

        executeResult = (Map) selectEntityContentActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectEntityContentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectEntityContentActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateEntityContentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateEntityContentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateEntityContentActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateEntityContentActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateEntityContentActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchEntityContentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchEntityContentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchEntityContentActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listEntityContentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listEntityContentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listEntityContentActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteEntityContentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteEntityContentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteEntityContentActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteEntityContentActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteEntityContentActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def downloadContent() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) downloadEntityContentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) downloadEntityContentActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) downloadEntityContentActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) downloadEntityContentActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        EntityContent entityContent = (EntityContent) executeResult.entityContent

        response.contentType = ConfigurationHolder.config.grails.mime.types[entityContent.extension]
        response.setHeader("Content-disposition", "attachment;filename=${entityContent.fileName}")
        response.outputStream << entityContent.content
    }
}
