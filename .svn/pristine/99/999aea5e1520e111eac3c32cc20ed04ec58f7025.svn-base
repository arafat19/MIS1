package com.athena.mis.application.controller

import com.athena.mis.application.actions.systementity.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class SystemEntityController {

    static allowedMethods = [
            create: "POST", show: "POST",
            select: "POST", update: "POST",
            list: "POST", delete: "POST"]

    ShowSystemEntityActionService showSystemEntityActionService
    CreateSystemEntityActionService createSystemEntityActionService
    SelectSystemEntityActionService selectSystemEntityActionService
    UpdateSystemEntityActionService updateSystemEntityActionService
    SearchSystemEntityActionService searchSystemEntityActionService
    ListSystemEntityActionService listSystemEntityActionService
    DeleteSystemEntityActionService deleteSystemEntityActionService

    /**
     * Show system entity
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError
        Map preResult

        preResult = (LinkedHashMap) showSystemEntityActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showSystemEntityActionService.buildFailureResultForUI(preResult)
            renderShow(result)
            return
        }
        executeResult = (Map) showSystemEntityActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showSystemEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showSystemEntityActionService.buildSuccessResultForUI(executeResult)
        }
        renderShow(result)
    }

    /**
     * Render out put for show
     * @param result - map from caller method
     */
    private renderShow(Map result) {
        String output = result as JSON
        render(view: '/application/systemEntity/show', model: [output: output])
    }

    /**
     * Create system entity
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) createSystemEntityActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSystemEntityActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createSystemEntityActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSystemEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createSystemEntityActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Select system entity
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) selectSystemEntityActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSystemEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectSystemEntityActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Update system entity
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateSystemEntityActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSystemEntityActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) updateSystemEntityActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSystemEntityActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateSystemEntityActionService.buildSuccessResultForUI(executeResult)
        }

        render(result as JSON)
    }

    /**
     * Delete system entity
     */
    def delete() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) deleteSystemEntityActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteSystemEntityActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) deleteSystemEntityActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteSystemEntityActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteSystemEntityActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * List & Search system entity
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            preResult = (Map) searchSystemEntityActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess) {
                result = (Map) searchSystemEntityActionService.buildFailureResultForUI(preResult)
                render(result as JSON)
                return
            }
            executeResult = (Map) searchSystemEntityActionService.execute(params, null)

            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchSystemEntityActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchSystemEntityActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listSystemEntityActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listSystemEntityActionService.buildFailureResultForUI(executeResult)
                render(result as JSON)
                return

            } else {
                result = (Map) listSystemEntityActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
