package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.customer.ExhDownloadCustomerCsvActionService
import com.athena.mis.exchangehouse.actions.report.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ExhReportController {

    static allowedMethods = [
            getInvoiceDetails: "POST", getForCustomerRemittance: "POST", getRemittanceSummaryReport: "POST", invoiceDetailsForCustomer: "POST",
            showCustomerHistory: "POST", showInvoiceFromGridForCustomer: "POST",
            showInvoice: "POST", showInvoiceForCustomer: "POST", showInvoiceFromTaskGrid: "POST", showAgentWiseCommissionForAdmin: "POST",
            showAgentWiseCommissionForAgent: "POST", showRemittanceTransaction: "POST", showRemittanceSummary: "POST",
            showTransactionSummary: "POST", showCashierWiseReportForAdmin: "POST", showSummaryReportForAdmin: "POST",
            listAgentWiseCommissionForAdmin: "POST", listAgentWiseCommissionForAgent: "POST", listReportSummaryForAdmin: "POST",
            listRemittanceTransaction: "POST", listCashierWiseReportForCashier: "POST", listCashierWiseReportForAdmin: "POST",
            listForCustomerRemittance: "POST", listTransactionSummary: "POST"
    ]    // add other unlisted closures in allowMethods


    ExhGetRemittanceDetailsByCustomerActionService exhGetRemittanceDetailsByCustomerActionService
    ExhListTransactionSummaryActionService exhListTransactionSummaryActionService
    ExhRemittanceSummaryReportActionService exhRemittanceSummaryReportActionService
    ExhShowForCustomerRemittanceActionService exhShowForCustomerRemittanceActionService
    ExhGetInvoiceDetailsReportActionService exhGetInvoiceDetailsReportActionService
    ExhInvoiceDetailsForCustomerActionService exhInvoiceDetailsForCustomerActionService
    ExhListRemittanceDetailsByCustomerActionService exhListRemittanceDetailsByCustomerActionService
    ExhShowInvoiceDetailsReportActionService exhShowInvoiceDetailsReportActionService
    ExhShowInvoiceDetailsForCustomerActionService exhShowInvoiceDetailsForCustomerActionService
    ExhDownloadTaskInvoiceReportActionService exhDownloadTaskInvoiceReportActionService
    ExhDownloadTaskInvoiceForCustomerActionService exhDownloadTaskInvoiceForCustomerActionService
    ExhShowCashierWiseReportCashierActionService exhShowCashierWiseReportCashierActionService
    ExhListCashierWiseReportAdminActionService exhListCashierWiseReportAdminActionService
    ExhListCashierWiseReportSummaryAdminActionService exhListCashierWiseReportSummaryAdminActionService
    ExhListCashierWiseReportCashierActionService exhListCashierWiseReportCashierActionService
    ExhDownloadForCashierWiseTaskReportActionService exhDownloadForCashierWiseTaskReportActionService
    ExhDownloadForCashierWiseRemittanceSummaryReportActionService exhDownloadForCashierWiseRemittanceSummaryReportActionService

    ShowAgentWiseCommissionForAdminActionService showAgentWiseCommissionForAdminActionService
    DownloadAgentWiseCommissionForAdminActionService downloadAgentWiseCommissionForAdminActionService
    DownloadCustomerHistoryActionService downloadCustomerHistoryActionService
    ExhDownloadTransactionSummaryActionService exhDownloadTransactionSummaryActionService
    DownloadAgentWiseCommissionForAgentActionService downloadAgentWiseCommissionForAgentActionService
    ShowAgentWiseCommissionForAgentActionService showAgentWiseCommissionForAgentActionService
    ListAgentWiseCommissionForAdminActionService listAgentWiseCommissionForAdminActionService
    ListAgentWiseCommissionForAgentActionService listAgentWiseCommissionForAgentActionService

    ExhShowRemittanceTransactionActionService exhShowRemittanceTransactionActionService
    ExhListRemittanceTransactionActionService exhListRemittanceTransactionActionService
    ExhDownloadRemittanceTransactionActionService exhDownloadRemittanceTransactionActionService
    ExhDownloadRemittanceTransactionCsvActionService exhDownloadRemittanceTransactionCsvActionService
    ExhDownloadCustomerCsvActionService exhDownloadCustomerCsvActionService

    /**
     * Show customer history
     */
    def showCustomerHistory() {
        Map preResult
        Map result
        String output
        String startDate
        if (params.customerId) {
            preResult = (Map) exhShowForCustomerRemittanceActionService.executePreCondition(params, null)
            Boolean isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhShowForCustomerRemittanceActionService.buildFailureResultForUI(preResult)
            } else {
                result = (Map) exhShowForCustomerRemittanceActionService.execute(params, null);
            }
            output = result as JSON
            startDate=result.createdDateFrom
            render(view: '/exchangehouse/report/customerHistory/show', model: [modelJson: output, startDate:startDate])
            return
        } else {
            result = (Map) exhShowForCustomerRemittanceActionService.execute(params, null);
            output = result as JSON
            startDate=result.createdDateFrom
            render(view: '/exchangehouse/report/customerHistory/show', model: [modelJson: output, startDate:startDate])
        }
    }

    /**
     * Download customer in CSV for Admin
     */
    def downloadCustomerCSV() {
        LinkedHashMap result
        Boolean isError
        result = (LinkedHashMap) exhDownloadCustomerCsvActionService.execute(null, null);
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }
        Map reportResult = (Map) result.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * show transaction summary for admin & cashier
     */
    def showTransactionSummary() {
        render(view: '/exchangehouse/report/transactionSummary/show', model: [modelJson: null])
    }

    /**
     * list transaction summary
     */
    def listTransactionSummary() {
        Map result
        Map preResult = (Map) exhListTransactionSummaryActionService.executePreCondition(params, null);

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListTransactionSummaryActionService.buildFailureResultForUI(preResult)
            render(result as JSON);
            return
        }

        Map executeResult = (Map) exhListTransactionSummaryActionService.execute(params, null);

        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListTransactionSummaryActionService.buildFailureResultForUI(executeResult)
            render(result as JSON);
            return
        }

        result = (Map) exhListTransactionSummaryActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON);
    }

    /**
     * Get customer remittance
     */
    def getForCustomerRemittance() {

        Map result = null

        Map preResult = (Map) exhGetRemittanceDetailsByCustomerActionService.executePreCondition(params, null);
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhGetRemittanceDetailsByCustomerActionService.buildFailureResultForUI(preResult)
            render(result as JSON);
            return
        }

        Map executeResult = (Map) exhGetRemittanceDetailsByCustomerActionService.execute(params, preResult);

        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhGetRemittanceDetailsByCustomerActionService.buildFailureResultForUI(executeResult)
            render(result as JSON);
            return
        }

        result = (Map) exhGetRemittanceDetailsByCustomerActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON);
    }

    /**
     * Get invoice details for admin or cashier or Agent
     */
    def getInvoiceDetails() {
        Map result
        Map preResult = (Map) exhGetInvoiceDetailsReportActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhGetInvoiceDetailsReportActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }
        result = (Map) exhGetInvoiceDetailsReportActionService.execute(null, preResult)
        String output = result as JSON
        render output
    }

    /**
     * Get daily remittance summary report
     */
    def getRemittanceSummaryReport() {
        Map result = (Map) exhRemittanceSummaryReportActionService.execute(params, null);
        render(result as JSON);
    }

    /**
     * list remittance details of customer for cashier or admin
     */
    def listForCustomerRemittance() {
        Map result = null

        Map preResult = (Map) exhListRemittanceDetailsByCustomerActionService.executePreCondition(params, null);
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListRemittanceDetailsByCustomerActionService.buildFailureResultForUI(preResult)
            render(result as JSON);
            return
        }
        Map executeResult = (Map) exhListRemittanceDetailsByCustomerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhListRemittanceDetailsByCustomerActionService.buildFailureResultForUI(executeResult)
            render(result as JSON);
            return
        }
        result = (Map) exhListRemittanceDetailsByCustomerActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON);
    }

    /**
     * Show daily remittance summary report UI
     */
    def showRemittanceSummary() {
        render(view: '/exchangehouse/report/summary/showDailyRemittanceSummary', model: [modelJson: false])
    }

    /**
     * show agent wise commission report for UI for Admin
     */
    def showAgentWiseCommissionForAdmin() {
        Map executeResult = (Map) showAgentWiseCommissionForAdminActionService.execute(params, null)
        String modelJson = executeResult as JSON
        String startDate= executeResult.createdDateFrom
        render(view: '/exchangehouse/report/agentWiseCommission/showForAdmin', model: [modelJson: modelJson,startDate:startDate])
    }

    /**
     * list agent wise commission for Admin
     */
    def listAgentWiseCommissionForAdmin() {
        Map result
        Map preResult = (Map) listAgentWiseCommissionForAdminActionService.executePreCondition(params, null);

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAgentWiseCommissionForAdminActionService.buildFailureResultForUI(preResult)
            render(result as JSON);
            return
        }
        Map executeResult = (Map) listAgentWiseCommissionForAdminActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAgentWiseCommissionForAgentActionService.buildFailureResultForUI(executeResult)
            render(result as JSON);
            return
        }
        result = (Map) listAgentWiseCommissionForAdminActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON);

    }

    /**
     * show agent commission report for UI for Agent
     */
    def showAgentWiseCommissionForAgent() {
        Map executeResult = (Map) showAgentWiseCommissionForAgentActionService.execute(params, null)
        String startDate= executeResult.createdDateFrom
        render(view: '/exchangehouse/report/agentWiseCommission/showForAgent', model: [startDate:startDate ])
    }

    /**
     * list agent commission for Agent
     */
    def listAgentWiseCommissionForAgent() {
        Map result
        Map preResult = (Map) listAgentWiseCommissionForAgentActionService.executePreCondition(params, null);

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAgentWiseCommissionForAgentActionService.buildFailureResultForUI(preResult)
            render(result as JSON);
            return
        }
        Map executeResult = (Map) listAgentWiseCommissionForAgentActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAgentWiseCommissionForAgentActionService.buildFailureResultForUI(executeResult)
            render(result as JSON);
            return
        }
        result = (Map) listAgentWiseCommissionForAgentActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON);

    }

    /**
     * Show remittance summary UI panel for Admin
     */
    def showSummaryReportForAdmin() {
        render(view: '/exchangehouse/report/remittanceSummaryReport/showForAdmin', model: [modelJson: null])
    }

    /**
     *  Show list of remittance summary report for Admin
     */
    def listReportSummaryForAdmin() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhListCashierWiseReportSummaryAdminActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListCashierWiseReportSummaryAdminActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) exhListCashierWiseReportSummaryAdminActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListCashierWiseReportSummaryAdminActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) exhListCashierWiseReportSummaryAdminActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Show cashier wise task report for Admin
     */
    def showCashierWiseReportForAdmin() {
        render(view: '/exchangehouse/report/cashierwisereport/showForAdmin', model: [modelJson: null])
    }

    /**
     * Show list of cashier wise task report for Admin
     */
    def listCashierWiseReportForAdmin() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) exhListCashierWiseReportAdminActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListCashierWiseReportAdminActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) exhListCashierWiseReportAdminActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListCashierWiseReportAdminActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) exhListCashierWiseReportAdminActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Show cashier task report for Cashier
     */
    def showCashierWiseReportForCashier() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        executeResult = (LinkedHashMap) exhShowCashierWiseReportCashierActionService.execute(null, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhShowCashierWiseReportCashierActionService.buildFailureResultForUI(executeResult);
            String modelJson = result as JSON
            render(view: '/exchangehouse/report/cashierwisereport/showForCashier', model: [modelJson: modelJson])
            return;
        }
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/report/cashierwisereport/showForCashier', model: [modelJson: modelJson])
    }

    /**
     * Show list of cashier task report for Cashier
     */
    def listCashierWiseReportForCashier() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhListCashierWiseReportCashierActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListCashierWiseReportCashierActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) exhListCashierWiseReportCashierActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListCashierWiseReportCashierActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) exhListCashierWiseReportCashierActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * show remittance transaction for admin & cashier
     */
    def showRemittanceTransaction() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String startDate
        executeResult = (LinkedHashMap) exhShowRemittanceTransactionActionService.execute(null, null);
        startDate=executeResult.fromDate
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhShowRemittanceTransactionActionService.buildFailureResultForUI(executeResult);
            String modelJson = result as JSON
            render(view: '/exchangehouse/report/remittanceTransaction/show', model: [modelJson: modelJson, startDate:startDate])
            return;
        }
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/report/remittanceTransaction/show', model: [modelJson: modelJson, startDate:startDate])
    }

    /**
     * show list of remittance transaction for admin & cashier
     */
    def listRemittanceTransaction() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhListRemittanceTransactionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListRemittanceTransactionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) exhListRemittanceTransactionActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhListRemittanceTransactionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) exhListRemittanceTransactionActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Show UI for invoice
     */
    def showInvoice() {
        render(view: '/exchangehouse/report/invoice/show', model: [modelJson: false])
    }

    /**
     * Show UI for invoice for Customer
     */
    def showInvoiceForCustomer() {
        render(view: '/exchangehouse/report/invoice/showForCustomer', model: [modelJson: false])
    }

    /**
     * Show invoice details from task grid for Admin, Cashier & Agent
     */
    def showInvoiceFromTaskGrid() {
        String output
        Map preResult
        Map executeResult
        Boolean isError
        preResult = (Map) exhShowInvoiceDetailsReportActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            output = preResult as JSON
            render(view: '/exchangehouse/report/invoice/show', model: [modelJson: output])
            return
        }
        executeResult = (Map) exhShowInvoiceDetailsReportActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            output = executeResult as JSON
            render(view: '/exchangehouse/report/invoice/show', model: [modelJson: output])
            return
        }
        output = executeResult as JSON
        render(view: '/exchangehouse/report/invoice/show', model: [modelJson: output])
    }

    /**
     * Show invoice details from task grid for Customer
     */
    def showInvoiceFromGridForCustomer() {
        Map result
        String output
        Map preResult
        preResult = (Map) exhShowInvoiceDetailsForCustomerActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhShowInvoiceDetailsForCustomerActionService.buildFailureResultForUI(preResult)
        } else {
            result = (Map) exhShowInvoiceDetailsForCustomerActionService.execute(null, preResult);
        }
        output = result as JSON
        render(view: '/exchangehouse/report/invoice/showForCustomer', model: [modelJson: output])
    }

    /**
     * Show invoice details for Customer
     */
    def invoiceDetailsForCustomer() {
        Map result
        Map preResult = (Map) exhInvoiceDetailsForCustomerActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhInvoiceDetailsForCustomerActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }
        result = (Map) exhInvoiceDetailsForCustomerActionService.execute(null, preResult)
        String output = result as JSON
        render output
    }

    /**
     * Download invoice for Agent, Cashier & Admin
     */
    def downloadInvoice() {
        Map result
        String output
        Map preResult = (Map) exhDownloadTaskInvoiceReportActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) exhDownloadTaskInvoiceReportActionService.execute(params, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    /**
     * Download invoice for Customer
     */
    def downloadInvoiceForCustomer() {
        Map result
        String output
        Map preResult = (Map) exhDownloadTaskInvoiceForCustomerActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) exhDownloadTaskInvoiceForCustomerActionService.execute(params, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()


    }

    /**
     * Download remittance summary for Admin
     */
    def downloadRemittanceSummaryReport() {
        LinkedHashMap result
        Boolean isError
        LinkedHashMap preResult = (LinkedHashMap) exhDownloadForCashierWiseRemittanceSummaryReportActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) exhDownloadForCashierWiseRemittanceSummaryReportActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download cashier wise report
     */
    def downloadCashierWiseTaskReport() {
        LinkedHashMap result
        Boolean isError
        LinkedHashMap preResult = (LinkedHashMap) exhDownloadForCashierWiseTaskReportActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) exhDownloadForCashierWiseTaskReportActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download agent wise commission report for Admin
     */
    def downloadAgentWiseCommissionForAdmin() {

        Map preResult = (Map) downloadAgentWiseCommissionForAdminActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) downloadAgentWiseCommissionForAdminActionService.execute(params, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download agent commission report for Agent
     */
    def downloadAgentWiseCommissionForAgent() {

        Map preResult = (Map) downloadAgentWiseCommissionForAgentActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) downloadAgentWiseCommissionForAgentActionService.execute(params, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download customer history report for Admin & Cashier
     */
    def downloadCustomerHistory() {
        Map preResult = (Map) downloadCustomerHistoryActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) downloadCustomerHistoryActionService.execute(params, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download transaction summary report for Admin & Cashier
     */
    def downloadTransactionSummary() {
        Map preResult = (Map) exhDownloadTransactionSummaryActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) exhDownloadTransactionSummaryActionService.execute(params, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download remittance transaction report in pdf format for Admin & Cashier
     */
    def downloadRemittanceTransaction() {
        Boolean isError
        LinkedHashMap preResult = (LinkedHashMap) exhDownloadRemittanceTransactionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) exhDownloadRemittanceTransactionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download remittance transaction report in csv format for Admin & Cashier
     */
    def downloadRemittanceTransactionCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        Boolean isError

        preResult = (LinkedHashMap) exhDownloadRemittanceTransactionCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }

        executeResult = (LinkedHashMap) exhDownloadRemittanceTransactionCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }
        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
}
