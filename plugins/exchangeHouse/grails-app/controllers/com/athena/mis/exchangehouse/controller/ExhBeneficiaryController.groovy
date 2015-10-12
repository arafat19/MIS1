package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.beneficiary.*
import grails.converters.JSON

class ExhBeneficiaryController {

    static allowedMethods = [
            show: "POST",
            showNewForCustomer: "POST",
            showApprovedForCustomer: "POST",
            create: "POST",
            createForCustomer: "POST",
            update: "POST",
            updateForCustomer: "POST",
            delete: "POST",
            list: "POST",
            listForCustomer: "POST",
            listLinkedBeneficiary: "POST",
            selectForCustomer: "POST"
    ]

    ExhShowBeneficiaryActionService exhShowBeneficiaryActionService
    ExhSelectBeneficiaryActionService exhSelectBeneficiaryActionService
    ExhCreateBeneficiaryActionService exhCreateBeneficiaryActionService
    ExhCreateBeneficiaryForCustomerActionService exhCreateBeneficiaryForCustomerActionService
    ExhListBeneficiaryActionService exhListBeneficiaryActionService
    ExhUpdateBeneficiaryActionService exhUpdateBeneficiaryActionService
    ExhUpdateBeneficiaryForCustomerActionService exhUpdateBeneficiaryForCustomerActionService
    ExhSearchBeneficiaryActionService exhSearchBeneficiaryActionService
    ExhShowNewBeneficiariesForCustomerActionService exhShowNewBeneficiariesForCustomerActionService
    ExhShowApprovedBeneficiariesForCustomerActionService exhShowApprovedBeneficiariesForCustomerActionService
    ExhListsForCustomerActionService exhListsForCustomerActionService
    ExhSelectBeneficiaryForCustomerActionService exhSelectBeneficiaryForCustomerActionService
    ExhSearchForCustomerActionService exhSearchForCustomerActionService
    ExhListLinkedBeneficiaryActionService exhListLinkedBeneficiaryActionService
    ExhApproveBeneficiaryForCashierActionService exhApproveBeneficiaryForCashierActionService

    /**
     * show beneficiary list for cashier and agent
     */
    def show() {
        Map result
        Map preResult = (Map) exhShowBeneficiaryActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhShowBeneficiaryActionService.buildFailureResultForUI(preResult)
        } else {
            result = (Map) exhShowBeneficiaryActionService.execute(params, preResult);
        }
        String output = result as JSON
        render(view: '/exchangehouse/exhBeneficiary/show', model: [modelJson: output])
    }

    /**
     * show unapproved beneficiary list for customer user
     */
    def showNewForCustomer() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        executeResult = (LinkedHashMap) exhShowNewBeneficiariesForCustomerActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhShowNewBeneficiariesForCustomerActionService.buildFailureResultForUI(executeResult);
            String modelJson = result as JSON
            render(view: '/exchangehouse/exhBeneficiary/showNewForCustomer', model: [modelJson: modelJson])
            return;
        }
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhBeneficiary/showNewForCustomer', model: [modelJson: modelJson])
    }

    /**
     * show approved beneficiary list for customer user
     */
    def showApprovedForCustomer() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        executeResult = (LinkedHashMap) exhShowApprovedBeneficiariesForCustomerActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhShowApprovedBeneficiariesForCustomerActionService.buildFailureResultForUI(executeResult);
            String modelJson = result as JSON
            render(view: '/exchangehouse/exhBeneficiary/showApprovedForCustomer', model: [modelJson: modelJson])
            return;
        }
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhBeneficiary/showApprovedForCustomer', model: [modelJson: modelJson])
    }

    /**
     * create beneficiary
     */
    def create() {
        Map result
        Map preResult
        Map executeResult
        String output
        Boolean isError
        preResult = (Map) exhCreateBeneficiaryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhCreateBeneficiaryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhCreateBeneficiaryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhCreateBeneficiaryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhCreateBeneficiaryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * create beneficiary for customer
     */
    def createForCustomer() {
        Map result
        Map preResult
        Map executeResult
        String output
        Boolean isError
        preResult = (Map) exhCreateBeneficiaryForCustomerActionService.executePreCondition(params, null)
        isError = ((Boolean) preResult.isError).booleanValue()
        if (isError.booleanValue()) {
            result = (Map) exhCreateBeneficiaryForCustomerActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhCreateBeneficiaryForCustomerActionService.execute(params, preResult)
        if (isError.booleanValue()) {
            result = (Map) exhCreateBeneficiaryForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhCreateBeneficiaryForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * update beneficiary
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) exhUpdateBeneficiaryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateBeneficiaryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhUpdateBeneficiaryActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateBeneficiaryActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) exhUpdateBeneficiaryActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /**
     * update beneficiary for customer user
     */
    def updateForCustomer() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) exhUpdateBeneficiaryForCustomerActionService.executePreCondition(params, null)
        isError = ((Boolean) preResult.isError).booleanValue()
        if (isError.booleanValue()) {
            result = (Map) exhUpdateBeneficiaryForCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhUpdateBeneficiaryForCustomerActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateBeneficiaryForCustomerActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) exhUpdateBeneficiaryForCustomerActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /**
     * edit or select beneficiary
     */
    def edit() {
        Map executeResult
        Map result
        Boolean isError
        String output
        executeResult = (Map) exhSelectBeneficiaryActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSelectBeneficiaryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) exhSelectBeneficiaryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * list and search beneficiary
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        //if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) exhSearchBeneficiaryActionService.execute(params, null);
            isError = ((Boolean) executeResult.isError)
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhSearchBeneficiaryActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) exhSearchBeneficiaryActionService.buildSuccessResultForUI(executeResult);
        } else { // normal listing
            executeResult = (LinkedHashMap) exhListBeneficiaryActionService.execute(params, null);
            isError = ((Boolean) executeResult.isError)
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhListBeneficiaryActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) exhListBeneficiaryActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /**
     * show list of linked beneficiary
     */
    def listLinkedBeneficiary() {
        LinkedHashMap executeResult = (LinkedHashMap) exhListLinkedBeneficiaryActionService.execute(params, null);
        executeResult = (LinkedHashMap) exhListLinkedBeneficiaryActionService.buildSuccessResultForUI(executeResult);
        String output = executeResult as JSON
        render output
    }

    /**
     * select beneficiary for customer user
     */
    def selectForCustomer() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) exhSelectBeneficiaryForCustomerActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSelectBeneficiaryForCustomerActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) exhSelectBeneficiaryForCustomerActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * list and search beneficiary for customer user
     */
    def listForCustomer() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        if (params.query) {
            executeResult = (LinkedHashMap) exhSearchForCustomerActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhSearchForCustomerActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) exhSearchForCustomerActionService.buildSuccessResultForUI(executeResult);
        } else { // normal listing
            executeResult = (LinkedHashMap) exhListsForCustomerActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhListsForCustomerActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return
            }
            result = (LinkedHashMap) exhListsForCustomerActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def approveBeneficiary(){
        Map result
        Boolean isError
        Map preResult = (Map) exhApproveBeneficiaryForCashierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhApproveBeneficiaryForCashierActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhApproveBeneficiaryForCashierActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhApproveBeneficiaryForCashierActionService.buildFailureResultForUI(executeResult)
            String output = result as JSON
            render output
            return
        }
        result = (Map) exhApproveBeneficiaryForCashierActionService.buildSuccessResultForUI(executeResult)
        String output = result as JSON
        render output
    }
}