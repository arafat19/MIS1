package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmstasklistsummarymodel.ListUnResolvedTaskListActionService
import grails.converters.JSON

class RmsTaskListSummaryModelController {

    static allowedMethods = [
        listUnResolvedTaskList: "POST"
    ]

    ListUnResolvedTaskListActionService listUnResolvedTaskListActionService

    def listUnResolvedTaskList() {
        Map result
        Map executeResult
        Boolean isError
        String output

        executeResult = (Map) listUnResolvedTaskListActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listUnResolvedTaskListActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listUnResolvedTaskListActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
