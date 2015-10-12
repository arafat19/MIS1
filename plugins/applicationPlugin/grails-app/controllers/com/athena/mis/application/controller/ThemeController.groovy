package com.athena.mis.application.controller

import com.athena.mis.application.actions.theme.*
import grails.converters.JSON

class ThemeController {

    static allowedMethods = [
            showTheme: "POST", updateTheme: "POST", listTheme: "POST", selectTheme: "POST"]


    ShowThemeActionService showThemeActionService
    UpdateThemeActionService updateThemeActionService
    ListThemeActionService listThemeActionService
    SelectThemeActionService selectThemeActionService
    SearchThemeActionService searchThemeActionService

    /**
     * Show theme
     */
    def showTheme() {
        Map result
        Map executePreResult = (Map) showThemeActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) executePreResult.hasAccess
        if (!hasAccess) {
            result = (Map) showThemeActionService.buildFailureResultForUI(executePreResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) showThemeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showThemeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showThemeActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/theme/show', model: [output: result as JSON])
    }

    /**
     * Select theme
     */
    def selectTheme() {
        Map result

        Map executePreResult = (Map) selectThemeActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) executePreResult.hasAccess
        if (!hasAccess) {
            result = (Map) selectThemeActionService.buildFailureResultForUI(executePreResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) selectThemeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectThemeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectThemeActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Update theme
     */
    def updateTheme() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateThemeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) updateThemeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateThemeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateThemeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateThemeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateThemeActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Search and List theme
     */
    def listTheme() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            preResult = (Map) searchThemeActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess) {
                result = (Map) searchThemeActionService.buildFailureResultForUI(preResult)
                render(result as JSON)
                return
            }

            executeResult = (Map) searchThemeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchThemeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchThemeActionService.buildSuccessResultForUI(executeResult)
            }

        } else {
            preResult = (Map) listThemeActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess) {
                result = (Map) listThemeActionService.buildFailureResultForUI(preResult)
                render(result as JSON)
                return
            }

            executeResult = (Map) listThemeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listThemeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listThemeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
