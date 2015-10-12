package com.athena.mis.application.controller

import com.athena.mis.application.actions.appshellscript.CreateAppShellScriptActionService
import com.athena.mis.application.actions.appshellscript.DeleteAppShellScriptActionService
import com.athena.mis.application.actions.appshellscript.EvaluateAppShellScriptActionService
import com.athena.mis.application.actions.appshellscript.ListAppShellScriptActionService
import com.athena.mis.application.actions.appshellscript.SelectAppShellScriptActionService
import com.athena.mis.application.actions.appshellscript.ShowAppShellScriptActionService
import com.athena.mis.application.actions.appshellscript.UpdateAppShellScriptActionService
import grails.converters.JSON

class AppShellScriptController {
    static allowedMethods = [show    : "POST",
                             create  : "POST",
                             update  : "POST",
                             delete  : "POST",
                             list    : "POST",
                             select  : "POST",
                             evaluate: "POST"]

    ShowAppShellScriptActionService showAppShellScriptActionService
    CreateAppShellScriptActionService createAppShellScriptActionService
    ListAppShellScriptActionService listAppShellScriptActionService
    SelectAppShellScriptActionService selectAppShellScriptActionService
    DeleteAppShellScriptActionService deleteAppShellScriptActionService
    UpdateAppShellScriptActionService updateAppShellScriptActionService
    EvaluateAppShellScriptActionService evaluateAppShellScriptActionService

    /**
     * show ShellScript list
     */
    def show() {
        Map executeResult, result
        Boolean isError

        executeResult = (Map) showAppShellScriptActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAppShellScriptActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/application/appShellScript/show', model: [modelJson: result as JSON])
    }

    /*
    * Create a ShellScript object
    * */

    def create() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) createAppShellScriptActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppShellScriptActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(output)
            return
        }

        executeResult = (Map) createAppShellScriptActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createAppShellScriptActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /*
    * Update a ShellScript object
    * */

    def update() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) updateAppShellScriptActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppShellScriptActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateAppShellScriptActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAppShellScriptActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * Delete a ShellScript object
    * */

    def delete() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) deleteAppShellScriptActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppShellScriptActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteAppShellScriptActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteAppShellScriptActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * List ShellScript object
    * */

    def list() {
        Map executeResult, result
        Boolean isError
        executeResult = (Map) listAppShellScriptActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAppShellScriptActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /*
    * Select a ShellScript object from grid
    * */

    def select() {
        Map executeResult, result
        Boolean isError
        String output

        executeResult = (Map) selectAppShellScriptActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectAppShellScriptActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * Evaluate a ShellScript object from grid
    * */

     def evaluate() {
        Map executeResult, result
        Boolean isError

        executeResult = (Map) evaluateAppShellScriptActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) evaluateAppShellScriptActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) evaluateAppShellScriptActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }
}
