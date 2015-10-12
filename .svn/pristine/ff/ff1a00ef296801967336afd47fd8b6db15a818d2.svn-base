package com.athena.mis.application.controller

import com.athena.mis.application.actions.bank.*
import grails.converters.JSON

class BankController {

    static allowedMethods = [
            show:"POST",create: "POST", update: "POST", edit: "POST", list: "POST", delete: "POST", reloadBankDropDownTagLib: "POST"
    ];


    ShowBankActionService showBankActionService
    CreateBankActionService createBankActionService
    UpdateBankActionService updateBankActionService
    DeleteBankActionService deleteBankActionService
    SelectBankActionService selectBankActionService
    ListBankActionService listBankActionService
    SearchBankActionService searchBankActionService

    /**
     * show bank list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showBankActionService.execute(params, null);
        isError = ((Boolean) executeResult.isError).booleanValue()
        if (isError.booleanValue()) {
            result = (Map) showBankActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showBankActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/bank/show', model: [modelJson: result as JSON])
    }

    /**
     * list and search bank
     */
    def list() {
        Map result;
        Map executeResult
        String output
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchBankActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchBankActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchBankActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listBankActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listBankActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listBankActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    /**
     * create Bank
     */
    def create() {
        Map result
        Map preResult
        Map executeResult
        Boolean isError
        String output
        preResult = (Map) createBankActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBankActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createBankActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createBankActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createBankActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * update bank
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateBankActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBankActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateBankActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBankActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateBankActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def edit() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectBankActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectBankActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectBankActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * delete bank
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteBankActionService.executePreCondition(params, null);
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBankActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteBankActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteBankActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteBankActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def reloadBankDropDownTagLib() {
        render app.dropDownBank(params, null)
    }

}
