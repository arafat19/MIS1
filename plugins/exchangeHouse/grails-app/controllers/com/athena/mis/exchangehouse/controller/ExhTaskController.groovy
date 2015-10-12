package com.athena.mis.exchangehouse.controller

import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.actions.task.*
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import grails.gsp.PageRenderer
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.beans.factory.annotation.Autowired

class ExhTaskController {

    static allowedMethods = [
            create: "POST", createEntityNoteForTask: "POST",
            edit: "POST", selectEntityNoteForTask: "POST",
            update: "POST", updateEntityNoteForTask: "POST",
            showForAgent: "POST", showForCustomer: "POST", showExhTaskForCashier: "POST", showAgentTaskForCashier: "POST",
            showCustomerTaskForCashier: "POST", showForOtherBankUser: "POST", showForTaskSearch: "POST", showForTaskSearchForAgent: "POST", showEntityNoteForTask: "POST",
            searchTaskWithRefOrPin: "POST", searchTaskWithRefOrPinForCustomer: "POST", searchTaskWithRefOrPinForAgent: "POST",
            showUnApprovedTaskForCustomer: "POST", showApprovedTaskForCustomer: "POST", showDisbursedTaskForCustomer: "POST",
            showExhTaskForAdmin: "POST", showAgentTaskForAdmin: "POST", showCustomerTaskForAdmin: "POST", showTaskDetailsForAdmin: "POST",
            listForCustomer: "POST", listForAgent: "POST", listForOtherBankUser: "POST", listExhTaskForCashier: "POST",
            listUnApprovedTaskForCustomer: "POST", listApprovedTaskForCustomer: "POST", listDisbursedTaskForCustomer: "POST",
            listCustomerTaskForCashier: "POST", listCustomerTaskForAdmin: "POST", listAgentTaskForAdmin: "POST",
            listAgentTaskForCashier: "POST", listExhTaskForAdmin: "POST", listEntityNoteForTask: "POST",
            calculateFeesAndCommission: "POST",
            sendToBank: "POST", sendToExchangeHouse: "POST", sendToExhForCustomer: "POST",
            resolveTaskForOtherBank: "POST", cancelSpecificTask: "POST", approveTaskForCashier: "POST",
            deleteEntityNoteForTask: "POST", showForMakePayment: "POST", callbackForPayPointUserReturn: "POST",
            callbackForPayPointPRN: "POST",
            reloadBankByTaskStatusAndTaskType:"POST",
            showDetailsForReplaceTask: "POST"
    ]

    // auto-wiring

    ShowExhTaskForCashierActionService showExhTaskForCashierActionService
    ShowAgentTaskForCashierActionService showAgentTaskForCashierActionService
    ShowCustomerTaskForCashierActionService showCustomerTaskForCashierActionService
    ExhShowTaskForAgentActionService exhShowTaskForAgentActionService

    ShowExhTaskForAdminActionService showExhTaskForAdminActionService
    ShowAgentTaskForAdminActionService showAgentTaskForAdminActionService
    ShowCustomerTaskForAdminActionService showCustomerTaskForAdminActionService
    ExhCreateTaskActionService exhCreateTaskActionService
    ExhUpdateTaskActionService exhUpdateTaskActionService

    ExhListTaskForAgentActionService exhListTaskForAgentActionService

    ListExhTaskForAdminActionService listExhTaskForAdminActionService
    ListAgentTaskForAdminActionService listAgentTaskForAdminActionService
    ListCustomerTaskForAdminActionService listCustomerTaskForAdminActionService

    ListExhTaskForCashierActionService listExhTaskForCashierActionService
    ListAgentTaskForCashierActionService listAgentTaskForCashierActionService
    ListCustomerTaskForCashierActionService listCustomerTaskForCashierActionService

    SearchExhTaskForCashierActionService searchExhTaskForCashierActionService
    SearchAgentTaskForCashierActionService searchAgentTaskForCashierActionService
    SearchCustomerTaskForCashierActionService searchCustomerTaskForCashierActionService

    ExhSearchTaskForAgentActionService exhSearchTaskForAgentActionService
    SearchExhTaskForAdminActionService searchExhTaskForAdminActionService
    SearchCustomerTaskForAdminActionService searchCustomerTaskForAdminActionService
    SearchAgentTaskForAdminActionService searchAgentTaskForAdminActionService
    ExhSelectTaskActionService exhSelectTaskActionService
    ExhSendTaskToBankActionService exhSendTaskToBankActionService
    ExhSendTaskToExchangeHouseActionService exhSendTaskToExchangeHouseActionService
    ExhApproveTaskForCashierActionService exhApproveTaskForCashierActionService
    ExhGetDetailsByRefOrPinActionService exhGetDetailsByRefOrPinActionService
    ExhGetDetailsByRefOrPinForAgentActionService exhGetDetailsByRefOrPinForAgentActionService
    ExhCancelTaskActionService exhCancelTaskActionService
    ExhListTaskForCustomerActionService exhListTaskForCustomerActionService
    ExhSearchTaskForCustomerActionService exhSearchTaskForCustomerActionService
    ExhShowTaskForCustomerActionService exhShowTaskForCustomerActionService
    ExhShowTasksForOtherBankUserActionService exhShowTasksForOtherBankUserActionService
    ExhListTaskForOtherBankUserActionService exhListTaskForOtherBankUserActionService
    ExhSearchTaskForOtherBankUserActionService exhSearchTaskForOtherBankUserActionService
    ExhResolveTaskOtherBankActionService exhResolveTaskOtherBankActionService
    ExhDownloadCsvOtherBankActionService exhDownloadCsvOtherBankActionService
    ExhShowUnApprovedTaskForCustomerActionService exhShowUnApprovedTaskForCustomerActionService
    ExhShowApprovedTaskForCustomerActionService exhShowApprovedTaskForCustomerActionService
    ExhShowDisbursedTaskForCustomerActionService exhShowDisbursedTaskForCustomerActionService
    ExhListUnApprovedTaskForCustomerActionService exhListUnApprovedTaskForCustomerActionService
    ExhListApprovedTaskForCustomerActionService exhListApprovedTaskForCustomerActionService
    ExhListDisbursedTaskForCustomerActionService exhListDisbursedTaskForCustomerActionService
    ExhSendTaskToExhForCustomerActionService exhSendTaskToExhForCustomerActionService
    ExhGetFeeAndCommissionForTaskActionService exhGetFeeAndCommissionForTaskActionService
    ExhGetDetailsByRefOrPinForCustomerActionService exhGetDetailsByRefOrPinForCustomerActionService
    ShowExhTaskNoteActionService showExhTaskNoteActionService
    CreateExhTaskNoteActionService createExhTaskNoteActionService
    ListExhTaskNoteActionService listExhTaskNoteActionService
    EditExhTaskNoteActionService editExhTaskNoteActionService
    UpdateExhTaskNoteActionService updateExhTaskNoteActionService
    DeleteExhTaskNoteActionService deleteExhTaskNoteActionService
    ExhShowForMakePaymentActionService exhShowForMakePaymentActionService
    ExhProcessPaypointUserReturnActionService exhProcessPaypointUserReturnActionService
    ExhProcessPaypointPRNActionService exhProcessPaypointPRNActionService
    PageRenderer groovyPageRenderer
    ShowDetailsForReplaceTaskActionService showDetailsForReplaceTaskActionService

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * show exh task list for cashier
     */
    def showExhTaskForCashier() {
        Map result
        Map preResult
        String output
        if (params.customerId || params.beneficiaryId) {
            preResult = (Map) showExhTaskForCashierActionService.executePreCondition(params, null)
            Boolean isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) showExhTaskForCashierActionService.buildFailureResultForUI(preResult)
            } else {
                result = (Map) showExhTaskForCashierActionService.execute(params, null);
            }
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showExhTaskForCashier', model: [modelJson: output, customerId: result.customerId])
            return
        } else {
            result = (Map) showExhTaskForCashierActionService.execute(params, null);
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showExhTaskForCashier', model: [modelJson: output, customerId: result.customerId])
        }
    }

    /**
     * show agent task list for cashier
     */
    def showAgentTaskForCashier() {
        Map executeResult = (Map) showAgentTaskForCashierActionService.execute(params, null)
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhTask/showAgentTaskForCashier', model: [modelJson: modelJson, customerId: 0L])
    }

    /**
     * show customer task list for cashier
     */
    def showCustomerTaskForCashier() {
        Map executeResult = (Map) showCustomerTaskForCashierActionService.execute(params, null)
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhTask/showCustomerTaskForCashier', model: [modelJson: modelJson, customerId: 0L])
    }

    /**
     * show exh task list for admin
     */
    def showExhTaskForAdmin() {
        Map executeResult = (Map) showExhTaskForAdminActionService.execute(params, null)
        String modelJson = executeResult as JSON
        String startDate= executeResult.createdDateFrom
        render(view: '/exchangehouse/exhTask/showExhTaskForAdmin', model: [modelJson: modelJson, startDate:startDate])
    }

    /**
     * show agent task list for admin
     */
    def showAgentTaskForAdmin() {
        Map executeResult = (Map) showAgentTaskForAdminActionService.execute(params, null)
        String modelJson = executeResult as JSON
        String startDate=executeResult.createdDateFrom
        render(view: '/exchangehouse/exhTask/showAgentTaskForAdmin', model: [modelJson: modelJson, startDate:startDate ])
    }

    /**
     * show customer task list for admin
     */
    def showCustomerTaskForAdmin() {
        Map executeResult = (Map) showCustomerTaskForAdminActionService.execute(params, null)
        String modelJson = executeResult as JSON
        String startDate=executeResult.createdDateFrom
        render(view: '/exchangehouse/exhTask/showCustomerTaskForAdmin', model: [modelJson: modelJson, startDate:startDate])
    }

    /**
     * show task list for other bank
     */
    def showForOtherBankUser() {
        Map executeResult = (Map) exhShowTasksForOtherBankUserActionService.execute(params, null)
        String modelJson = executeResult as JSON
        String startDate=executeResult.createdDateFrom
        render(view: '/exchangehouse/exhTask/showForOtherBankUser', model: [modelJson: modelJson, startDate:startDate ])
    }

    /**
     * show task list for agent
     */
    def showForAgent() {
        String output
        Map preResult
        Map result
        if (params.customerId || params.beneficiaryId) {
            preResult = (Map) exhShowTaskForAgentActionService.executePreCondition(params, null)
            Boolean isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhShowTaskForAgentActionService.buildFailureResultForUI(preResult)
            } else {
                result = (Map) exhShowTaskForAgentActionService.execute(params, null);
            }
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showForAgent', model: [modelJson: output, customerId: result.customerId])
            return
        } else {
            result = (Map) exhShowTaskForAgentActionService.execute(params, null);
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showForAgent', model: [modelJson: output, customerId: 0L])
        }
    }

    /**
     * show task list for customer
     */
    def showForCustomer() {
        Map result
        Map preResult
        String output
        if (params.customerId || params.beneficiaryId) {
            preResult = (Map) exhShowTaskForCustomerActionService.executePreCondition(params, null)
            Boolean isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhShowTaskForCustomerActionService.buildFailureResultForUI(preResult)
            } else {
                result = (Map) exhShowTaskForCustomerActionService.execute(params, null);
            }
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showForCustomer', model: [modelJson: output, customerId: result.customerId])
            return
        } else {
            result = (Map) exhShowTaskForCustomerActionService.execute(params, null);
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showForCustomer', model: [modelJson: output, customerId: 0L])
        }
    }

    /**
     * show un-approved task list for customer
     */
    def showUnApprovedTaskForCustomer() {
        Map result
        Map executeResult
        executeResult = (Map) exhShowUnApprovedTaskForCustomerActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhShowUnApprovedTaskForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhShowUnApprovedTaskForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/exchangehouse/exhTask/showUnApprovedTaskForCustomer', model: [output: result as JSON])
    }

    /**
     * show approved list of task for customer
     */
    def showApprovedTaskForCustomer() {
        Map result
        Map executeResult
        executeResult = (Map) exhShowApprovedTaskForCustomerActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhShowApprovedTaskForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhShowApprovedTaskForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/exchangehouse/exhTask/showApprovedTaskForCustomer', model: [output: result as JSON])
    }

    /**
     * show Disbursed list of task for customer
     */
    def showDisbursedTaskForCustomer() {
        Map result
        Map executeResult
        executeResult = (Map) exhShowDisbursedTaskForCustomerActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhShowDisbursedTaskForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhShowDisbursedTaskForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/exchangehouse/exhTask/showDisbursedTaskForCustomer', model: [output: result as JSON])
    }

    /**
     * show task details for admin
     */
    def showTaskDetailsForAdmin() {
        Map executeResult
        String output
        executeResult = (Map) exhGetDetailsByRefOrPinActionService.executePreCondition(params, null)

        if (executeResult.isError == true) {
            executeResult = (Map) exhGetDetailsByRefOrPinActionService.buildFailureResultForUI(executeResult)
            output = executeResult as JSON
            render(view: '/exchangehouse/exhTask/showTaskDetails', model: [modelJson: output])
            return
        }

        executeResult = (Map) exhGetDetailsByRefOrPinActionService.execute(params, executeResult)
        output = executeResult as JSON
        if (executeResult.taskInfoMap == null) {
            output = executeResult as JSON
            render(view: '/exchangehouse/exhTask/showTaskDetails', model: [modelJson: output])
            return
        }

        int paymentMethod = executeResult.task.paymentMethod
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodBank = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCash = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        switch (paymentMethod) {
            case exhPaymentMethodBank.id:
                render(view: '/exchangehouse/exhTask/showTaskDetailsForBankDeposit', model: [modelJson: output])
                break
            case exhPaymentMethodCash.id:
                render(view: '/exchangehouse/exhTask/showTaskDetailsForCashCollection', model: [modelJson: output])
                break
        }
    }

    /**
     * show UI for task status search for cashier or admin
     */
    def showForTaskSearch() {
        render(view: '/exchangehouse/exhTask/showForSearchTask')
    }

    /**
     * show UI for task status search for agent
     */
    def showForTaskSearchForAgent() {
        render(view: '/exchangehouse/exhTask/showForSearchTaskForAgent')
    }

    /**
     * create task
     */
    def create() {
        Map result
        Map preResult = (Map) exhCreateTaskActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError

        if (isError.booleanValue()) {
            result = (Map) exhCreateTaskActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) exhCreateTaskActionService.execute(params, preResult)
        if (!executeResult) {
            result = (Map) exhCreateTaskActionService.buildFailureResultForUI(null)
            String output = result as JSON
            render output
            return
        }
        Map postResult = (Map) exhCreateTaskActionService.executePostCondition(null, executeResult)
        result = (Map) exhCreateTaskActionService.buildSuccessResultForUI(postResult)
        String output = result as JSON
        render output
    }

    /**
     * update task
     */
    def update() {
        Map result

        Map preResult = (Map) exhUpdateTaskActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateTaskActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return;
        }
        Map executeResult = (Map) exhUpdateTaskActionService.execute(params, preResult);
        if (!executeResult) {
            result = (Map) exhUpdateTaskActionService.buildFailureResultForUI(null)
            String output = result as JSON
            render output
            return
        }
        Map postResult = (Map) exhUpdateTaskActionService.executePostCondition(null, executeResult)
        result = (Map) exhUpdateTaskActionService.buildSuccessResultForUI(postResult)
        String output = result as JSON
        render output
    }

    /**
     * edit or select task
     */
    def edit() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) exhSelectTaskActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSelectTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) exhSelectTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * list and search exh task for cashier
     */
    def listExhTaskForCashier() {

        Map executeResult
        if (params.query) {
            executeResult = (Map) searchExhTaskForCashierActionService.execute(params, null);
            executeResult = (Map) searchExhTaskForCashierActionService.buildSuccessResultForUI(executeResult);
        } else {                         // normal listing
            executeResult = (Map) listExhTaskForCashierActionService.execute(params, null);
            executeResult = (Map) listExhTaskForCashierActionService.buildSuccessResultForUI(executeResult);
        }

        render(executeResult as JSON);
    }

    /**
     * list and search agent task for cashier
     */
    def listAgentTaskForCashier() {

        Map executeResult
        if (params.query) {
            executeResult = (Map) searchAgentTaskForCashierActionService.execute(params, null);
            executeResult = (Map) searchAgentTaskForCashierActionService.buildSuccessResultForUI(executeResult);
        } else {                // normal listing
            executeResult = (Map) listAgentTaskForCashierActionService.execute(params, null);
            executeResult = (Map) listAgentTaskForCashierActionService.buildSuccessResultForUI(executeResult);
        }

        render(executeResult as JSON);
    }

    /**
     * list and search customer task for cashier
     */
    def listCustomerTaskForCashier() {
        Map executeResult = null
        if (params.query) {
            executeResult = (Map) searchCustomerTaskForCashierActionService.execute(params, null);
            executeResult = (Map) searchCustomerTaskForCashierActionService.buildSuccessResultForUI(executeResult);
        } else {                        // normal listing
            executeResult = (Map) listCustomerTaskForCashierActionService.execute(params, null);
            executeResult = (Map) listCustomerTaskForCashierActionService.buildSuccessResultForUI(executeResult);
        }

        render(executeResult as JSON);
    }

    /**
     * list and search of agent task for agent
     */
    def listForAgent() {
        Map executeResult = null
        if (params.query) {
            executeResult = (Map) exhSearchTaskForAgentActionService.execute(params, null);
            executeResult = (Map) exhSearchTaskForAgentActionService.buildSuccessResultForUI(executeResult);
        } else { // normal listing
            executeResult = (Map) exhListTaskForAgentActionService.execute(params, null);
            executeResult = (Map) exhListTaskForAgentActionService.buildSuccessResultForUI(executeResult);
        }

        render(executeResult as JSON);
    }

    /**
     * search & list cashier task for admin
     */
    def listExhTaskForAdmin() {
        Map executeResult = null
        // if it's a search request
        if (params.query) {
            executeResult = (Map) searchExhTaskForAdminActionService.execute(params, null);
            executeResult = (Map) searchExhTaskForAdminActionService.buildSuccessResultForUI(executeResult);

        } else {                            // normal listing
            executeResult = (Map) listExhTaskForAdminActionService.execute(params, null);
            executeResult = (Map) listExhTaskForAdminActionService.buildSuccessResultForUI(executeResult);

        }
        render(executeResult as JSON);
    }

    /**
     * search & list agent task for admin
     */
    def listAgentTaskForAdmin() {

        Map executeResult = null
        // if it's a search request
        if (params.query) {
            executeResult = (Map) searchAgentTaskForAdminActionService.execute(params, null);
            executeResult = (Map) searchAgentTaskForAdminActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing
            executeResult = (Map) listAgentTaskForAdminActionService.execute(params, null);
            executeResult = (Map) listAgentTaskForAdminActionService.buildSuccessResultForUI(executeResult);
        }

        render(executeResult as JSON);
    }

    /**
     * search & list Customer task for admin
     */
    def listCustomerTaskForAdmin() {

        Map executeResult = null
        // if it's a search request
        if (params.query) {
            executeResult = (Map) searchCustomerTaskForAdminActionService.execute(params, null);
            executeResult = (Map) searchCustomerTaskForAdminActionService.buildSuccessResultForUI(executeResult);


        } else { // normal listing
            executeResult = (Map) listCustomerTaskForAdminActionService.execute(params, null);
            executeResult = (Map) listCustomerTaskForAdminActionService.buildSuccessResultForUI(executeResult);
        }
        render(executeResult as JSON);
    }

    /**
     * list of unapproved task for customer
     */
    def listUnApprovedTaskForCustomer() {
        Map result;
        Boolean isError;
        Map executeResult = (Map) exhListUnApprovedTaskForCustomerActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListUnApprovedTaskForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhListUnApprovedTaskForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * list of approved task for customer
     */
    def listApprovedTaskForCustomer() {
        Map result;
        Boolean isError;
        Map executeResult = (Map) exhListApprovedTaskForCustomerActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListApprovedTaskForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhListApprovedTaskForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * list of disbursed or paid task for customer
     */
    def listDisbursedTaskForCustomer() {
        Map result;
        Boolean isError;
        Map executeResult = (Map) exhListDisbursedTaskForCustomerActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListDisbursedTaskForCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhListDisbursedTaskForCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * list and search of customer task task for customer
     */
    def listForCustomer() {
        Map executeResult = null
        // if it's a search request
        if (params.query) {
            executeResult = (Map) exhSearchTaskForCustomerActionService.execute(params, null);
            executeResult = (Map) exhSearchTaskForCustomerActionService.buildSuccessResultForUI(executeResult);
        } else { // normal listing
            executeResult = (Map) exhListTaskForCustomerActionService.execute(params, null);
            executeResult = (Map) exhListTaskForCustomerActionService.buildSuccessResultForUI(executeResult);
        }
        render(executeResult as JSON);
    }

    /**
     * search & list task for other bank
     */
    def listForOtherBankUser() {
        Map executeResult = null
        Map result
        if (params.query) {
            Map preResult = (Map) exhSearchTaskForOtherBankUserActionService.executePreCondition(params, null)

            Boolean isError = preResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhSearchTaskForOtherBankUserActionService.buildFailureResultForUI(preResult)
                render(result as JSON);
                return
            }
            executeResult = (Map) exhSearchTaskForOtherBankUserActionService.execute(params, null)
            result = (Map) exhSearchTaskForOtherBankUserActionService.buildSuccessResultForUI(executeResult)
        } else {
            Map preResult = (Map) exhListTaskForOtherBankUserActionService.executePreCondition(params, null)

            Boolean isError = preResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhListTaskForOtherBankUserActionService.buildFailureResultForUI(preResult)
                render(result as JSON);
                return
            }
            executeResult = (Map) exhListTaskForOtherBankUserActionService.execute(params, null)
            result = (Map) exhListTaskForOtherBankUserActionService.buildSuccessResultForUI(executeResult)
        }

        render(result as JSON);
    }

    /**
     * send task to bank for admin
     */
    def sendToBank() {
        def result = null
        LinkedHashMap preResultMap = (LinkedHashMap) exhSendTaskToBankActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResultMap.isError
        if (isError.booleanValue()) {
            result = exhSendTaskToBankActionService.buildFailureResultForUI(preResultMap)
            String output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhSendTaskToBankActionService.execute(null, preResultMap.taskList)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = exhSendTaskToBankActionService.buildFailureResultForUI(executeResult)
            String output = result as JSON
            render output
            return
        }
        exhSendTaskToBankActionService.executePostCondition(null, executeResult)
        result = exhSendTaskToBankActionService.buildSuccessResultForUI(executeResult)
        String output = result as JSON
        render output
    }

    /**
     * send task to exchange house for agent
     */
    def sendToExchangeHouse() {
        Map result
        Map preResult = (Map) exhSendTaskToExchangeHouseActionService.executePreCondition(params, null)
        Boolean success = (Boolean) preResult.success
        if (!success.booleanValue()) {
            result = (Map) exhSendTaskToExchangeHouseActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhSendTaskToExchangeHouseActionService.execute(null, preResult)
        success = (Boolean) executeResult.success
        if (!success.booleanValue()) {
            result = (Map) exhSendTaskToExchangeHouseActionService.buildFailureResultForUI(executeResult)
            String output = result as JSON
            render output
            return
        }
        result = (Map) exhSendTaskToExchangeHouseActionService.buildSuccessResultForUI(executeResult)
        String output = result as JSON
        render output
    }

    /**
     * approved customer task for cashier
     */
    def approveTaskForCashier() {
        Map result
        Boolean isError
        Map preResult = (Map) exhApproveTaskForCashierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhApproveTaskForCashierActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhApproveTaskForCashierActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhApproveTaskForCashierActionService.buildFailureResultForUI(executeResult)
            String output = result as JSON
            render output
            return
        }
        result = (Map) exhApproveTaskForCashierActionService.buildSuccessResultForUI(executeResult)
        String output = result as JSON
        render output
    }

    /**
     * send task to exchange house for customer
     */
    def sendToExhForCustomer() {
        Map result
        Map preResult = (Map) exhSendTaskToExhForCustomerActionService.executePreCondition(params, null)
        Boolean success = (Boolean) preResult.success
        if (!success.booleanValue()) {
            result = (Map) exhSendTaskToExhForCustomerActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhSendTaskToExhForCustomerActionService.execute(null, preResult)
        success = (Boolean) executeResult.success
        if (!success.booleanValue()) {
            result = (Map) exhSendTaskToExhForCustomerActionService.buildFailureResultForUI(executeResult)
            String output = result as JSON
            render output
            return
        }
        result = (Map) exhSendTaskToExhForCustomerActionService.buildSuccessResultForUI(executeResult)
        String output = result as JSON
        render output
    }

    /**
     * search task by refNo or pinNo for cashier
     */
    def searchTaskWithRefOrPin() {
        Map result
        Map preResult
        String output

        preResult = (Map) exhGetDetailsByRefOrPinActionService.executePreCondition(params, null)

        if (preResult.isError == true) {
            result = (Map) exhGetDetailsByRefOrPinActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) exhGetDetailsByRefOrPinActionService.execute(params, preResult)
        output = result as JSON

        if (result.taskInfoMap == null) {
            render output
            return
        }

        int paymentMethod = result.task.paymentMethod
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodBank = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCash = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        switch (paymentMethod) {
            case exhPaymentMethodBank.id:
                output = groovyPageRenderer.render(view: '/exchangehouse/exhTask/showTaskDetailsForBankDeposit', model: [modelJson: output])
                render output
                break
            case exhPaymentMethodCash.id:
                output = groovyPageRenderer.render(view: '/exchangehouse/exhTask/showTaskDetailsForCashCollection', model: [modelJson: output])
                render output
                break
        }
    }

    /**
     * search task by refNo or pinNo for customer
     */
    def searchTaskWithRefOrPinForCustomer() {
        Map result
        Map preResult
        String output
        preResult = (Map) exhGetDetailsByRefOrPinForCustomerActionService.executePreCondition(params, null)
        if (preResult.isError) {
            result = (Map) exhGetDetailsByRefOrPinForCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showTaskDetails', model: [modelJson: output])
            return
        }
        result = (Map) exhGetDetailsByRefOrPinForCustomerActionService.execute(params, preResult)
        output = result as JSON
        if (result.taskInfoMap == null) {
            render output
            return
        }
        int paymentMethod = result.task.paymentMethod
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodBank = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCash = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        switch (paymentMethod) {
            case exhPaymentMethodBank.id:
                output = groovyPageRenderer.render(view: '/exchangehouse/exhTask/showTaskDetailsForBankDeposit', model: [modelJson: output])
                render output
                break
            case exhPaymentMethodCash.id:
                output = groovyPageRenderer.render(view: '/exchangehouse/exhTask/showTaskDetailsForCashCollection', model: [modelJson: output])
                render output
                break
        }
    }

    /**
     * search task by refNo or pinNo for agent
     */
    def searchTaskWithRefOrPinForAgent() {
        Map result
        Map preResult
        String output

        preResult = (Map) exhGetDetailsByRefOrPinForAgentActionService.executePreCondition(params, null)

        if (preResult.isError == true) {
            result = (Map) exhGetDetailsByRefOrPinForAgentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) exhGetDetailsByRefOrPinForAgentActionService.execute(params, preResult)
        output = result as JSON

        if (result.taskInfoMap == null) {
            render output
            return
        }

        int paymentMethod = result.task.paymentMethod
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPaymentMethodBank = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT, companyId)
        SystemEntity exhPaymentMethodCash = (SystemEntity) exhPaymentMethodCacheUtility.readByReservedAndCompany(exhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION, companyId)

        switch (paymentMethod) {
            case exhPaymentMethodBank.id:
                output = groovyPageRenderer.render(view: '/exchangehouse/exhTask/showTaskDetailsForBankDeposit', model: [modelJson: output])
                render output
                break
            case exhPaymentMethodCash.id:
                output = groovyPageRenderer.render(view: '/exchangehouse/exhTask/showTaskDetailsForCashCollection', model: [modelJson: output])
                render output
                break
        }
    }
    /**
     * Cancel task for Admin
     */
    def cancelSpecificTask() {
        LinkedHashMap result
        boolean isError
        LinkedHashMap preResult = (LinkedHashMap) exhCancelTaskActionService.executePreCondition(params, null)

        boolean hasAccess = ((Boolean) preResult.hasAccess).booleanValue();
        if (hasAccess == false) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        isError = ((Boolean) preResult.isError).booleanValue();
        if (isError == true) {
            result = (LinkedHashMap) exhCancelTaskActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        LinkedHashMap executeResult = (LinkedHashMap) exhCancelTaskActionService.execute(params, preResult)

        isError = ((Boolean) executeResult.isError).booleanValue();
        if (isError == true) {
            result = (LinkedHashMap) exhCancelTaskActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return;
        }

        result = (LinkedHashMap) exhCancelTaskActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    /**
     * resolved task for other bank
     */
    def resolveTaskForOtherBank() {
        Map result
        String output
        Map preResult = (Map) exhResolveTaskOtherBankActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhResolveTaskOtherBankActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhResolveTaskOtherBankActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhResolveTaskOtherBankActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        result = (Map) exhResolveTaskOtherBankActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    /**
     * download list of task in csv format for other bank user
     */
    def downloadCsvForOtherBank() {

        LinkedHashMap result
        Boolean isError
        Map preResult = (Map) exhDownloadCsvOtherBankActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        result = (LinkedHashMap) exhDownloadCsvOtherBankActionService.execute(params, null);

        Map reportResult = (Map) result.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Calculate fees and commission of Task
     */
    def calculateFeesAndCommission() {
        Map result
        Boolean isError
        String output
        Map preResult = (Map) exhGetFeeAndCommissionForTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhGetFeeAndCommissionForTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) exhGetFeeAndCommissionForTaskActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhGetFeeAndCommissionForTaskActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        output = executeResult as JSON
        render output
    }

    /**
     * show task note
     **/
    def showEntityNoteForTask() {
        Boolean isError
        Map result
        Map executeResult
        String output
        Map preResult
        preResult = (Map) showExhTaskNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showExhTaskNoteActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(view: '/exchangehouse/exhTask/showNote', model: [modelJson: output])
            return
        }
        executeResult = (Map) showExhTaskNoteActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showExhTaskNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showExhTaskNoteActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render(view: '/exchangehouse/exhTask/showNote', model: [modelJson: output])
    }

    /**
     * create task note
     **/
    def createEntityNoteForTask() {
        Map preResult = (Map) createExhTaskNoteActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        Map result
        if (isError.booleanValue()) {
            result = (Map) createExhTaskNoteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) createExhTaskNoteActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createExhTaskNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createExhTaskNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * update task entity note
     **/
    def updateEntityNoteForTask() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateExhTaskNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhTaskNoteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateExhTaskNoteActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhTaskNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateExhTaskNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)

    }

    /**
     * list task entity note
     */
    def listEntityNoteForTask() {
        Map result;
        Map executeResult = (Map) listExhTaskNoteActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listExhTaskNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listExhTaskNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * select task entity note
     */
    def selectEntityNoteForTask() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) editExhTaskNoteActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) editExhTaskNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) editExhTaskNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     *  delete task entity note
     */
    def deleteEntityNoteForTask() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteExhTaskNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteExhTaskNoteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteExhTaskNoteActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteExhTaskNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteExhTaskNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def showForMakePayment() {
        Map result = (Map) exhShowForMakePaymentActionService.execute(params, null)
        Boolean isError = result.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }
        render(view: '/exchangehouse/exhTask/showForMakePayment', model: result)
    }

    def callbackForPayPointUserReturn() {
        // redirect required for layout load with hashing
        redirect(uri: '/#exhTask/showForPayPointUserReturn?taskId=' + params.strCartID)
    }

    def showForPayPointUserReturn() {
        Map result = (Map) exhProcessPaypointUserReturnActionService.execute(params, null)
        Boolean isError = result.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }
        render(view: '/exchangehouse/exhTask/showForPaymentCompletion', model: result)
    }

    def callbackForPayPointPRN() {
        Map preResult = (Map) exhProcessPaypointPRNActionService.executePreCondition(params, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            render 'PRN processed'
            return
        }
        Map result = (Map) exhProcessPaypointPRNActionService.execute(null, preResult)
        render 'Successfully received PRN'
    }
    def reloadBankByTaskStatusAndTaskType(){
        render exh.dropDownBankByTaskStatusAndTaskType(params)
    }

    def showDetailsForReplaceTask() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showDetailsForReplaceTaskActionService.execute(params, null)
        isError = executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showDetailsForReplaceTaskActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showDetailsForReplaceTaskActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: "/exchangehouse/exhTask/showDetailsForReplaceTask", model: [modelJson: result as JSON])
    }
}
