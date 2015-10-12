package com.athena.mis.procurement.controller

import com.athena.mis.procurement.actions.proctermsandcondition.*
import grails.converters.JSON

class ProcTermsAndConditionController {

    static allowedMethods = [
            create: "POST",
            select: "POST",
            update: "POST",
            delete: 'POST',
            list: "POST",
            show: "POST"
    ]

    ShowProcTermsAndConditionActionService showProcTermsAndConditionActionService
    CreateProcTermsAndConditionActionService createProcTermsAndConditionActionService
    SelectProcTermsAndConditionActionService selectProcTermsAndConditionActionService
    UpdateProcTermsAndConditionActionService updateProcTermsAndConditionActionService
    DeleteProcTermsAndConditionActionService deleteProcTermsAndConditionActionService
    ListProcTermsAndConditionActionService listProcTermsAndConditionActionService

    /**
     * Show Procurement terms and condition
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        Map preResult
        preResult = (LinkedHashMap) showProcTermsAndConditionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showProcTermsAndConditionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render(view: '/procurement/procTermsAndCondition/show', model: [output: output])
            return;
        }

        executeResult = (LinkedHashMap) showProcTermsAndConditionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showProcTermsAndConditionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render(view: '/procurement/procTermsAndCondition/show', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showProcTermsAndConditionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render(view: '/procurement/procTermsAndCondition/show', model: [output: output])
    }

    /**
     * Create Procurement terms and condition
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createProcTermsAndConditionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createProcTermsAndConditionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) createProcTermsAndConditionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createProcTermsAndConditionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) createProcTermsAndConditionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Select Procurement terms and condition
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectProcTermsAndConditionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectProcTermsAndConditionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) selectProcTermsAndConditionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectProcTermsAndConditionActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectProcTermsAndConditionActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /**
     * Update Procurement terms and condition
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateProcTermsAndConditionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateProcTermsAndConditionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateProcTermsAndConditionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateProcTermsAndConditionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) updateProcTermsAndConditionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Delete Procurement terms and condition
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteProcTermsAndConditionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteProcTermsAndConditionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteProcTermsAndConditionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteProcTermsAndConditionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) deleteProcTermsAndConditionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * List Procurement terms and condition
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) listProcTermsAndConditionActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listProcTermsAndConditionActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listProcTermsAndConditionActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
}
