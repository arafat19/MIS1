package com.athena.mis.qs.controler

import com.athena.mis.qs.actions.qsmeasurement.*
import grails.converters.JSON

class QsMeasurementController {

    static allowedMethods = [
            create: "POST",
            select: "POST",
            update: "POST",
            delete: 'POST',
            list: "POST",
            getQsStatusForDashBoard: "POST"
    ];

    ShowQsMeasurementActionService showQsMeasurementActionService
    CreateQsMeasurementActionService createQsMeasurementActionService
    SelectQsMeasurementActionService selectQsMeasurementActionService
    UpdateQsMeasurementActionService updateQsMeasurementActionService
    DeleteQsMeasurementActionService deleteQsMeasurementActionService
    ListQsMeasurementActionService listQsMeasurementActionService
    SearchQsMeasurementActionService searchQsMeasurementActionService
    ShowGovtQsMeasurementActionService showGovtQsMeasurementActionService
    GetQsStatusForDashBoardActionService getQsStatusForDashBoardActionService

    // For showing Qs Measurement (Govt)
    def showGovt() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult
        preResult = (LinkedHashMap) showGovtQsMeasurementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showGovtQsMeasurementActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render(view: '/qs/qsMeasurement/showGovt', model: [output: output])
            return;
        }
        executeResult = (LinkedHashMap) showGovtQsMeasurementActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showGovtQsMeasurementActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) showGovtQsMeasurementActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render(view: '/qs/qsMeasurement/showGovt', model: [output: output])
    }

    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult
        preResult = (LinkedHashMap) showQsMeasurementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showQsMeasurementActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render(view: '/qs/qsMeasurement/show', model: [output: output])
            return;
        }
        executeResult = (LinkedHashMap) showQsMeasurementActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showQsMeasurementActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) showQsMeasurementActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render(view: '/qs/qsMeasurement/show', model: [output: output])
    }

    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createQsMeasurementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createQsMeasurementActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) createQsMeasurementActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createQsMeasurementActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) createQsMeasurementActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectQsMeasurementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectQsMeasurementActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) selectQsMeasurementActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectQsMeasurementActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectQsMeasurementActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateQsMeasurementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateQsMeasurementActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateQsMeasurementActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateQsMeasurementActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) updateQsMeasurementActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteQsMeasurementActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteQsMeasurementActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteQsMeasurementActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteQsMeasurementActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) deleteQsMeasurementActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def list() {
        Map preResult;
        Boolean hasAccess;
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchQsMeasurementActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchQsMeasurementActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchQsMeasurementActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listQsMeasurementActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listQsMeasurementActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listQsMeasurementActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON)
    }

    def getQsStatusForDashBoard() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) getQsStatusForDashBoardActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getQsStatusForDashBoardActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) getQsStatusForDashBoardActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
}
