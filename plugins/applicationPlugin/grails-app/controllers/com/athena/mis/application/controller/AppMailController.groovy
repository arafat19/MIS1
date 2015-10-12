package com.athena.mis.application.controller

import com.athena.mis.application.actions.appMail.*
import grails.converters.JSON

class AppMailController {
    static allowedMethods = [
            create: "POST",
            show: "POST",
            select: "POST",
            edit: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            testAppMail: "POST"
    ];

    ShowAppMailActionService showAppMailActionService
    UpdateAppMailActionService updateAppMailActionService
    ListAppMailActionService listAppMailActionService
    SelectAppMailActionService selectAppMailActionService
    TestAppMailActionService testAppMailActionService

    def show() {
        Map result;
        Map executeResult
        Boolean isError

        executeResult = (Map) showAppMailActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showAppMailActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showAppMailActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/appMail/show', model: [output: result as JSON])
    }


    def select() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) selectAppMailActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAppMailActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAppMailActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateAppMailActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppMailActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateAppMailActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppMailActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAppMailActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def list() {
        Map result;
        Map executeResult;
        Boolean isError

        executeResult = (Map) listAppMailActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAppMailActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listAppMailActionService.buildSuccessResultForUI(executeResult)
        }

        render(result as JSON)
    }

    def testAppMail(){
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) testAppMailActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) testAppMailActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) testAppMailActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) testAppMailActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) testAppMailActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

}
