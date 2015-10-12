package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.report.accFinancialStatement.*
import com.athena.mis.accounting.actions.report.accbankreconciliationcheque.AccDownloadForBankReconciliationChequeActionService
import com.athena.mis.accounting.actions.report.accbankreconciliationcheque.AccDownloadForBankReconciliationChequeCsvActionService
import com.athena.mis.accounting.actions.report.accbankreconciliationcheque.AccListForBankReconciliationChequeActionService
import com.athena.mis.accounting.actions.report.accchartofaccount.DownloadChartOfAccountActionService
import com.athena.mis.accounting.actions.report.acccustomgroupbalance.AccDownloadForCustomGroupBalanceActionService
import com.athena.mis.accounting.actions.report.acccustomgroupbalance.AccDownloadForCustomGroupBalanceCsvActionService
import com.athena.mis.accounting.actions.report.acccustomgroupbalance.AccListForCustomGroupBalanceActionService
import com.athena.mis.accounting.actions.report.acccustomgroupbalance.AccShowForCustomGroupBalanceActionService
import com.athena.mis.accounting.actions.report.accincomestatement.*
import com.athena.mis.accounting.actions.report.acciouslip.DownloadForAccIouSlipActionService
import com.athena.mis.accounting.actions.report.acciouslip.ListForAccIouSlipActionService
import com.athena.mis.accounting.actions.report.acciouslip.ShowForAccIouSlipActionService
import com.athena.mis.accounting.actions.report.accledger.*
import com.athena.mis.accounting.actions.report.acctrialbalance.*
import com.athena.mis.accounting.actions.report.accvoucher.*
import com.athena.mis.accounting.actions.report.accvoucherlist.DownloadForVoucherListActionService
import com.athena.mis.accounting.actions.report.accvoucherlist.GetVoucherDetailsListActionService
import com.athena.mis.accounting.actions.report.accvoucherlist.SearchForVoucherListActionService
import com.athena.mis.accounting.actions.report.accvoucherlist.ShowForVoucherListActionService
import com.athena.mis.accounting.actions.report.accvouchertotal.GetTotalOfPayCashActionService
import com.athena.mis.accounting.actions.report.accvouchertotal.GetTotalOfPayChequeActionService
import com.athena.mis.accounting.actions.report.accvouchertotal.GetTotalOfPayChequeOnChequeDateActionService
import com.athena.mis.accounting.actions.report.projectfundflow.DownloadForProjectFundFlowActionService
import com.athena.mis.accounting.actions.report.projectfundflow.ListForProjectFundFlowActionService
import com.athena.mis.accounting.actions.report.projectwiseexpense.*
import com.athena.mis.accounting.actions.report.sourcewisebalance.DownloadForSourceWiseBalanceActionService
import com.athena.mis.accounting.actions.report.sourcewisebalance.DownloadForSourceWiseBalanceCsvActionService
import com.athena.mis.accounting.actions.report.sourcewisebalance.DownloadForVoucherListBySourceIdActionService
import com.athena.mis.accounting.actions.report.sourcewisebalance.GetSourceCategoryForSourceWiseBalanceActionService
import com.athena.mis.accounting.actions.report.sourcewisebalance.ListForSourceWiseBalanceActionService
import com.athena.mis.accounting.actions.report.supplierwisepayable.*
import com.athena.mis.accounting.actions.report.supplierwisepayment.DownloadForSupplierWisePaymentActionService
import com.athena.mis.accounting.actions.report.supplierwisepayment.DownloadForSupplierWisePaymentCsvActionService
import com.athena.mis.accounting.actions.report.supplierwisepayment.ListForSupplierWisePaymentActionService
import com.athena.mis.accounting.actions.report.supplierwisepayment.ShowForSupplierWisePaymentActionService
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AccReportController {

    static allowedMethods = [
            showVoucher: "POST", searchVoucher: "POST", showLedger: "POST",
            listLedger: "POST", showVoucherList: "POST", searchVoucherList: "POST",

            showCustomGroupBalance: "POST", listCustomGroupBalance: "POST", showForGroupLedgerRpt: "POST",
            listForGroupLedgerRpt: "POST", showSourceLedger: "POST", listSourceLedger: "POST", listForVoucherDetails: "POST",
            listSupplierWisePayment: "POST", showProjectWiseExpense: "POST", listProjectWiseExpense: "POST",
            listProjectWiseExpenseDetails: "POST", showSourceWiseBalance: "POST", listSourceWiseBalance: "POST",
            showSupplierWisePayable: "POST", listSupplierWisePayable: "POST", showSupplierWisePayment: "POST",
            showBankReconciliationCheque: "POST", listBankReconciliationCheque: "POST", listSourceByCategoryAndType: "POST",
            listSourceCategoryForSourceLedger: "POST", listSourceCategoryForSourceWiseBalance:"POST", showAccIouSlipRpt: "POST", listAccIouSlipRpt: "POST",
            retrieveTotalOfPayChequeOnChequeDate: "POST",retrieveTotalOfPayCheque: "POST",retrieveTotalOfPayCash: "POST",

            showFinancialStatementOfLevel2: "POST", listFinancialStatementOfLevel2: "POST",
            showFinancialStatementOfLevel3: "POST", listFinancialStatementOfLevel3: "POST",
            showFinancialStatementOfLevel4: "POST", listFinancialStatementOfLevel4: "POST",
            showFinancialStatementOfLevel5: "POST", listFinancialStatementOfLevel5: "POST",

            showTrialBalanceOfLevel2: "POST", listTrialBalanceOfLevel2: "POST",
            showTrialBalanceOfLevel3: "POST", listTrialBalanceOfLevel3: "POST",
            showTrialBalanceOfLevel4: "POST", listTrialBalanceOfLevel4: "POST",
            showTrialBalanceOfLevel5: "POST", listTrialBalanceOfLevel5: "POST",

            showIncomeStatementOfLevel4: "POST", listIncomeStatementOfLevel4: "POST",
            showIncomeStatementOfLevel5: "POST", listIncomeStatementOfLevel5: "POST",

            showProjectFundFlowReport: "POST", listProjectFundFlowReport: "POST",
            sendMailForProjectWiseExpense: "POST", sendMailForUnpostedVoucher: "POST",
            sendMailForSupplierWisePayable: "POST"
    ]

    DownloadChartOfAccountActionService downloadChartOfAccountActionService
    ShowForVoucherActionService showForVoucherActionService
    ShowForCancelledVoucherActionService showForCancelledVoucherActionService
    SearchForVoucherActionService searchForVoucherActionService
    SearchForCancelledVoucherActionService searchForCancelledVoucherActionService
    DownloadForVoucherActionService downloadForVoucherActionService
    DownloadForVoucherBankChequeActionService downloadForVoucherBankChequeActionService
    DownloadForVoucherBankChequePreviewActionService downloadForVoucherBankChequePreviewActionService
    SendMailForUnpostedVoucherActionService sendMailForUnpostedVoucherActionService

    ShowForLedgerActionService showForLedgerActionService
    DownloadForLedgerActionService downloadForLedgerActionService
    DownloadForLedgerCsvActionService downloadForLedgerCsvActionService
    ListForLedgerActionService listForLedgerActionService
    ShowForVoucherListActionService showForVoucherListActionService
    SearchForVoucherListActionService searchForVoucherListActionService
    DownloadForVoucherListActionService downloadForVoucherListActionService

    ShowForGroupLedgerActionService showForGroupLedgerActionService
    ListForGroupLedgerActionService listForGroupLedgerActionService
    DownloadForGroupLedgerActionService downloadForGroupLedgerActionService
    DownloadForGroupLedgerCsvActionService downloadForGroupLedgerCsvActionService
    ShowForSourceLedgerActionService showForSourceLedgerActionService
    ListForSourceLedgerActionService listForSourceLedgerActionService
    DownloadForSourceLedgerActionService downloadForSourceLedgerActionService
    DownloadForSourceLedgerGroupBySourceActionService downloadForSourceLedgerGroupBySourceActionService
    DownloadForSourceLedgerCsvActionService downloadForSourceLedgerCsvActionService
    GetVoucherDetailsListActionService getVoucherDetailsListActionService
    ShowForSupplierWisePaymentActionService showForSupplierWisePaymentActionService
    ListForSupplierWisePaymentActionService listForSupplierWisePaymentActionService
    DownloadForSupplierWisePaymentActionService downloadForSupplierWisePaymentActionService
    DownloadForSupplierWisePaymentCsvActionService downloadForSupplierWisePaymentCsvActionService

    ShowForProjectWiseExpenseActionService showForProjectWiseExpenseActionService
    ListForProjectWiseExpenseDetailsActionService listForProjectWiseExpenseDetailsActionService
    ListForProjectWiseExpenseActionService listForProjectWiseExpenseActionService
    DownloadForProjectWiseExpenseActionService downloadForProjectWiseExpenseActionService
    DownloadForProjectWiseExpenseCsvActionService downloadForProjectWiseExpenseCsvActionService
    SendMailForProjectWiseExpenseActionService sendMailForProjectWiseExpenseActionService

    ListForSourceWiseBalanceActionService listForSourceWiseBalanceActionService
    DownloadForSourceWiseBalanceActionService downloadForSourceWiseBalanceActionService
    DownloadForSourceWiseBalanceCsvActionService downloadForSourceWiseBalanceCsvActionService
    DownloadForVoucherListBySourceIdActionService downloadForVoucherListBySourceIdActionService

    ShowForAccIouSlipActionService showForAccIouSlipActionService
    ListForAccIouSlipActionService listForAccIouSlipActionService
    DownloadForAccIouSlipActionService downloadForAccIouSlipActionService
    GetTotalOfPayCashActionService getTotalOfPayCashActionService
    GetTotalOfPayChequeActionService getTotalOfPayChequeActionService
    GetTotalOfPayChequeOnChequeDateActionService getTotalOfPayChequeOnChequeDateActionService
    AccShowForSupplierWisePayableActionService accShowForSupplierWisePayableActionService
    AccListForSupplierWisePayableActionService accListForSupplierWisePayableActionService
    AccDownloadForSupplierWisePayableActionService accDownloadForSupplierWisePayableActionService
    AccDownloadForSupplierWisePayableCsvActionService accDownloadForSupplierWisePayableCsvActionService

    AccShowForCustomGroupBalanceActionService accShowForCustomGroupBalanceActionService
    AccListForCustomGroupBalanceActionService accListForCustomGroupBalanceActionService
    AccDownloadForCustomGroupBalanceActionService accDownloadForCustomGroupBalanceActionService
    AccDownloadForCustomGroupBalanceCsvActionService accDownloadForCustomGroupBalanceCsvActionService

    AccListForBankReconciliationChequeActionService accListForBankReconciliationChequeActionService
    AccDownloadForBankReconciliationChequeActionService accDownloadForBankReconciliationChequeActionService
    AccDownloadForBankReconciliationChequeCsvActionService accDownloadForBankReconciliationChequeCsvActionService

    GetSourceListBySourceCategoryAndAccSourceIdActionService getSourceListBySourceCategoryAndAccSourceIdActionService
    GetSourceCategoryForSourceLedgerActionService getSourceCategoryForSourceLedgerActionService
    GetSourceCategoryForSourceWiseBalanceActionService getSourceCategoryForSourceWiseBalanceActionService

    //For financial statement report
    ShowForFinancialStatementActionService showForFinancialStatementActionService

    DownloadForFinancialStatementOfLevel5ActionService downloadForFinancialStatementOfLevel5ActionService
    ListForFinancialStatementOfLevel5ActionService listForFinancialStatementOfLevel5ActionService
    DownloadForFinancialStatementCsvOfLevel5ActionService downloadForFinancialStatementCsvOfLevel5ActionService

    DownloadForFinancialStatementOfLevel4ActionService downloadForFinancialStatementOfLevel4ActionService
    ListForFinancialStatementOfLevel4ActionService listForFinancialStatementOfLevel4ActionService
    DownloadForFinancialStatementCsvOfLevel4ActionService downloadForFinancialStatementCsvOfLevel4ActionService

    DownloadForFinancialStatementOfLevel3ActionService downloadForFinancialStatementOfLevel3ActionService
    ListForFinancialStatementOfLevel3ActionService listForFinancialStatementOfLevel3ActionService
    DownloadForFinancialStatementCsvOfLevel3ActionService downloadForFinancialStatementCsvOfLevel3ActionService

    ListForFinancialStatementOfLevel2ActionService listForFinancialStatementOfLevel2ActionService
    DownloadForFinancialStatementOfLevel2ActionService downloadForFinancialStatementOfLevel2ActionService
    DownloadForFinancialStatementCsvOfLevel2ActionService downloadForFinancialStatementCsvOfLevel2ActionService

    //For trial balance report
    ShowForTrialBalanceActionService showForTrialBalanceActionService

    ListForTrialBalanceOfLevel3ActionService listForTrialBalanceOfLevel3ActionService
    DownloadForTrialBalanceOfLevel3ActionService downloadForTrialBalanceOfLevel3ActionService
    DownloadForTrialBalanceCsvOfLevel3ActionService downloadForTrialBalanceCsvOfLevel3ActionService

    ListForTrialBalanceOfLevel4ActionService listForTrialBalanceOfLevel4ActionService
    DownloadForTrialBalanceOfLevel4ActionService downloadForTrialBalanceOfLevel4ActionService
    DownloadForTrialBalanceCsvOfLevel4ActionService downloadForTrialBalanceCsvOfLevel4ActionService

    ListForTrialBalanceOfLevel5ActionService listForTrialBalanceOfLevel5ActionService
    DownloadForTrialBalanceOfLevel5ActionService downloadForTrialBalanceOfLevel5ActionService
    DownloadForTrialBalanceCsvOfLevel5ActionService downloadForTrialBalanceCsvOfLevel5ActionService

    ListForTrialBalanceOfLevel2ActionService listForTrialBalanceOfLevel2ActionService
    DownloadForTrialBalanceOfLevel2ActionService downloadForTrialBalanceOfLevel2ActionService
    DownloadForTrialBalanceCsvOfLevel2ActionService downloadForTrialBalanceCsvOfLevel2ActionService

    //For income-statement report
    ShowForAccIncomeStatementActionService showForAccIncomeStatementActionService

    ListForIncomeStatementOfLevel4ActionService listForIncomeStatementOfLevel4ActionService
    DownloadForIncomeStatementOfLevel4ActionService downloadForIncomeStatementOfLevel4ActionService
    DownloadForIncomeStatementCsvOfLevel4ActionService downloadForIncomeStatementCsvOfLevel4ActionService

    ListForIncomeStatementOfLevel5ActionService listForIncomeStatementOfLevel5ActionService
    DownloadForIncomeStatementOfLevel5ActionService downloadForIncomeStatementOfLevel5ActionService
    DownloadForIncomeStatementCsvOfLevel5ActionService downloadForIncomeStatementCsvOfLevel5ActionService

    //For project fund flow
    ListForProjectFundFlowActionService listForProjectFundFlowActionService
    DownloadForProjectFundFlowActionService downloadForProjectFundFlowActionService

    //Send mail for SupplierWisePayable
    SendMailForSupplierWisePayableActionService sendMailForSupplierWisePayableActionService

    def downloadChartOfAccounts() {
        Map result
        String output
        LinkedHashMap executeResult = (LinkedHashMap) downloadChartOfAccountActionService.execute(null, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadChartOfAccountActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    def showVoucher() {
        Map executeResult

        if(params.cancelledVoucher == 'true'){
            executeResult = (Map) showForCancelledVoucherActionService.execute(params, null)
        } else {
            executeResult = (Map) showForVoucherActionService.execute(params, null)
        }

        render(view: '/accounting/report/voucher/show', model: [result: executeResult])
    }

    def searchVoucher() {
        LinkedHashMap preResult
        LinkedHashMap result
        Boolean isError
        if(params.cancelledVoucher){
            preResult = (LinkedHashMap) searchForCancelledVoucherActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForCancelledVoucherActionService.buildFailureResultForUI(preResult);
                render(template: '/accounting/report/voucher/tmpVoucher', model: [result: result])
                return
            }

            result = (LinkedHashMap) searchForCancelledVoucherActionService.execute(params, preResult);
            isError = (Boolean) result.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForCancelledVoucherActionService.buildFailureResultForUI(result)
            }

            render(template: '/accounting/report/voucher/tmpVoucher', model: [result: result])
        }else{
            preResult = (LinkedHashMap) searchForVoucherActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForVoucherActionService.buildFailureResultForUI(preResult);
                render(template: '/accounting/report/voucher/tmpVoucher', model: [result: result])
                return
            }

            result = (LinkedHashMap) searchForVoucherActionService.execute(params, preResult);
            isError = (Boolean) result.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForVoucherActionService.buildFailureResultForUI(result)
            }

            render(template: '/accounting/report/voucher/tmpVoucher', model: [result: result])
        }
    }

    def downloadVoucher() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

            preResult = (LinkedHashMap) downloadForVoucherActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) downloadForVoucherActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return
            }

            executeResult = (LinkedHashMap) downloadForVoucherActionService.execute(params, preResult);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) downloadForVoucherActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return
            }


        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    def downloadVoucherBankCheque() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForVoucherBankChequeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherBankChequeActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForVoucherBankChequeActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherBankChequeActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadVoucherBankChequePreview() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForVoucherBankChequePreviewActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherBankChequePreviewActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForVoucherBankChequePreviewActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherBankChequePreviewActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showLedger() {
        Map executeResult = (Map) showForLedgerActionService.execute(params, null);
        render(view: '/accounting/report/accLedger/show', model: [modelJson: executeResult as JSON])
    }

    def listLedger() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForLedgerActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForLedgerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForLedgerActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForLedgerActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadLedger() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForLedgerActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForLedgerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForLedgerActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadLedgerCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForLedgerCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForLedgerCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForLedgerCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForLedgerCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showVoucherList() {
        Map executeResult = (Map) showForVoucherListActionService.execute(params, null);
        render(view: '/accounting/report/accVoucherList/show', model: [modelJson: executeResult as JSON])
    }

    def searchVoucherList() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) searchForVoucherListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForVoucherListActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) searchForVoucherListActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForVoucherListActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) searchForVoucherListActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadVoucherList() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForVoucherListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherListActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForVoucherListActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherListActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def listForVoucherDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) getVoucherDetailsListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getVoucherDetailsListActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getVoucherDetailsListActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getVoucherDetailsListActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getVoucherDetailsListActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    //---------------------Start : Trial Balance Report---------------------\\
    def showTrialBalanceOfLevel2() {
        Map executeResult = (Map) showForTrialBalanceActionService.execute(params, null);
        render(view: '/accounting/report/accTrialBalance/showLevel2', model: [modelJson: executeResult as JSON])
    }

    def listTrialBalanceOfLevel2() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForTrialBalanceOfLevel2ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel2ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForTrialBalanceOfLevel2ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel2ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForTrialBalanceOfLevel2ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadTrialBalanceOfLevel2() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceOfLevel2ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel2ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceOfLevel2ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel2ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showTrialBalanceOfLevel3() {
        Map executeResult = (Map) showForTrialBalanceActionService.execute(params, null);
        render(view: '/accounting/report/accTrialBalance/showLevel3', model: [modelJson: executeResult as JSON])
    }

    def listTrialBalanceOfLevel3() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForTrialBalanceOfLevel3ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForTrialBalanceOfLevel3ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel3ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForTrialBalanceOfLevel3ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadTrialBalanceOfLevel3() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceOfLevel3ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceOfLevel3ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel3ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadTrialBalanceCsvOfLevel2() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel2ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel2ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel2ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel2ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadTrialBalanceCsvOfLevel3() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel3ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel3ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel3ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showTrialBalanceOfLevel4() {
        Map executeResult = (Map) showForTrialBalanceActionService.execute(params, null);
        render(view: '/accounting/report/accTrialBalance/showLevel4', model: [modelJson: executeResult as JSON])
    }

    def listTrialBalanceOfLevel4() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForTrialBalanceOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForTrialBalanceOfLevel4ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel4ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForTrialBalanceOfLevel4ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadTrialBalanceOfLevel4() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceOfLevel4ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel4ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadTrialBalanceCsvOfLevel4() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel4ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel4ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showTrialBalanceOfLevel5() {
        Map executeResult = (Map) showForTrialBalanceActionService.execute(params, null);
        render(view: '/accounting/report/accTrialBalance/showLevel5', model: [modelJson: executeResult as JSON])
    }

    def listTrialBalanceOfLevel5() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForTrialBalanceOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForTrialBalanceOfLevel5ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForTrialBalanceOfLevel5ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForTrialBalanceOfLevel5ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadTrialBalanceOfLevel5() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceOfLevel5ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceOfLevel5ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadTrialBalanceCsvOfLevel5() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel5ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForTrialBalanceCsvOfLevel5ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
    //---------------------End : Trial Balance Report---------------------\\

    def showCustomGroupBalance() {
        Map executeResult = (Map) accShowForCustomGroupBalanceActionService.execute(params, null);
        render(view: '/accounting/report/accCustomGroupBalance/show', model: [modelJson: executeResult as JSON])
    }

    def listCustomGroupBalance() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) accListForCustomGroupBalanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accListForCustomGroupBalanceActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) accListForCustomGroupBalanceActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accListForCustomGroupBalanceActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) accListForCustomGroupBalanceActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadCustomGroupBalance() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) accDownloadForCustomGroupBalanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForCustomGroupBalanceActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) accDownloadForCustomGroupBalanceActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForCustomGroupBalanceActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadCustomGroupBalanceCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) accDownloadForCustomGroupBalanceCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForCustomGroupBalanceCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) accDownloadForCustomGroupBalanceCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForCustomGroupBalanceCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showForGroupLedgerRpt() {
        Map executeResult = (Map) showForGroupLedgerActionService.execute(params, null);
        render(view: '/accounting/report/accLedger/showGroupLedger', model: [modelJson: executeResult as JSON])
    }

    def listForGroupLedgerRpt() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForGroupLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForGroupLedgerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForGroupLedgerActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForGroupLedgerActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForGroupLedgerActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadForGroupLedgerRpt() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForGroupLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForGroupLedgerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForGroupLedgerActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForGroupLedgerActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadForGroupLedgerCsvRpt() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForGroupLedgerCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForGroupLedgerCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForGroupLedgerCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForGroupLedgerCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showSourceLedger() {
        Map executeResult = (Map) showForSourceLedgerActionService.execute(params, null);
        render(view: '/accounting/report/accSourceLedger/show', model: [modelJson: executeResult as JSON])
    }

    def listSourceLedger() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForSourceLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForSourceLedgerActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForSourceLedgerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForSourceLedgerActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForSourceLedgerActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadSourceLedger() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSourceLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceLedgerActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSourceLedgerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceLedgerActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSourceLedgeReportGroupBySource() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSourceLedgerGroupBySourceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceLedgerGroupBySourceActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSourceLedgerGroupBySourceActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceLedgerGroupBySourceActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSourceLedgerCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSourceLedgerCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceLedgerCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSourceLedgerCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceLedgerCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showSupplierWisePayment() {
        Map result = (Map) showForSupplierWisePaymentActionService.execute(params, null);
        render(view: '/accounting/report/supplierWisePayment/show', model: [modelJson: result as JSON])
    }

    def listSupplierWisePayment() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForSupplierWisePaymentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForSupplierWisePaymentActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForSupplierWisePaymentActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForSupplierWisePaymentActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForSupplierWisePaymentActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadSupplierWisePayment() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSupplierWisePaymentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSupplierWisePaymentActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSupplierWisePaymentActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSupplierWisePaymentActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSupplierWisePaymentCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSupplierWisePaymentCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSupplierWisePaymentCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSupplierWisePaymentCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSupplierWisePaymentCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    //---------------------Start : Income Statement Report---------------------\\
    def showIncomeStatementOfLevel4() {
        Map executeResult = (Map) showForAccIncomeStatementActionService.execute(params, null);
        render(view: '/accounting/report/accIncomeStatement/showLevel4', model: [modelJson: executeResult as JSON])
    }

    def listIncomeStatementOfLevel4() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForIncomeStatementOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForIncomeStatementOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForIncomeStatementOfLevel4ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForIncomeStatementOfLevel4ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForIncomeStatementOfLevel4ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadIncomeStatementOfLevel4() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForIncomeStatementOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForIncomeStatementOfLevel4ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementOfLevel4ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadIncomeStatementCsvOfLevel4() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel4ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel4ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showIncomeStatementOfLevel5() {
        Map executeResult = (Map) showForAccIncomeStatementActionService.execute(params, null);
        render(view: '/accounting/report/accIncomeStatement/showLevel5', model: [modelJson: executeResult as JSON])
    }

    def listIncomeStatementOfLevel5() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForIncomeStatementOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForIncomeStatementOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForIncomeStatementOfLevel5ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForIncomeStatementOfLevel5ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForIncomeStatementOfLevel5ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadIncomeStatementOfLevel5() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForIncomeStatementOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForIncomeStatementOfLevel5ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementOfLevel5ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadIncomeStatementCsvOfLevel5() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel5ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForIncomeStatementCsvOfLevel5ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
    //---------------------End : Income Statement Report---------------------\\

    def showFinancialStatementOfLevel5() {
        Map executeResult = (Map) showForFinancialStatementActionService.execute(params, null);
        render(view: '/accounting/report/accFinancialStatement/showForLevel5', model: [modelJson: executeResult as JSON])
    }

    def listFinancialStatementOfLevel5() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForFinancialStatementOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForFinancialStatementOfLevel5ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel5ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForFinancialStatementOfLevel5ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadFinancialStatementOfLevel5() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementOfLevel5ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel5ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadFinancialStatementCsvOfLevel2() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel2ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel2ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel3ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadFinancialStatementCsvOfLevel3() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel3ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel3ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel3ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadFinancialStatementCsvOfLevel4() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel4ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel4ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadFinancialStatementCsvOfLevel5() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel5ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel5ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel5ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementCsvOfLevel5ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showFinancialStatementOfLevel4() {
        Map executeResult = (Map) showForFinancialStatementActionService.execute(params, null);
        render(view: '/accounting/report/accFinancialStatement/showForLevel4', model: [modelJson: executeResult as JSON])
    }

    def listFinancialStatementOfLevel4() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForFinancialStatementOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForFinancialStatementOfLevel4ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel4ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForFinancialStatementOfLevel4ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadFinancialStatementOfLevel4() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementOfLevel4ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel4ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementOfLevel4ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel4ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showFinancialStatementOfLevel3() {
        Map executeResult = (Map) showForFinancialStatementActionService.execute(params, null);
        render(view: '/accounting/report/accFinancialStatement/showForLevel3', model: [modelJson: executeResult as JSON])
    }

    def listFinancialStatementOfLevel3() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForFinancialStatementOfLevel3ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForFinancialStatementOfLevel3ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel3ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForFinancialStatementOfLevel3ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadFinancialStatementOfLevel3() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementOfLevel3ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel3ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementOfLevel3ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel3ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showFinancialStatementOfLevel2() {
        Map executeResult = (Map) showForFinancialStatementActionService.execute(params, null);
        render(view: '/accounting/report/accFinancialStatement/showForLevel2', model: [modelJson: executeResult as JSON])
    }

    def listFinancialStatementOfLevel2() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForFinancialStatementOfLevel2ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel2ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForFinancialStatementOfLevel2ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForFinancialStatementOfLevel2ActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForFinancialStatementOfLevel2ActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadFinancialStatementOfLevel2() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForFinancialStatementOfLevel2ActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel2ActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForFinancialStatementOfLevel2ActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForFinancialStatementOfLevel2ActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showProjectWiseExpense() {
        Map executeResult = (Map) showForProjectWiseExpenseActionService.execute(params, null);
        render(view: '/accounting/report/projectWiseExpense/show', model: [modelJson: executeResult as JSON])
    }

    def listProjectWiseExpense() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForProjectWiseExpenseActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectWiseExpenseActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForProjectWiseExpenseActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectWiseExpenseActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForProjectWiseExpenseActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def sendMailForProjectWiseExpense() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) sendMailForProjectWiseExpenseActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForProjectWiseExpenseActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) sendMailForProjectWiseExpenseActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForProjectWiseExpenseActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) sendMailForProjectWiseExpenseActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def sendMailForSupplierWisePayable() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) sendMailForSupplierWisePayableActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForSupplierWisePayableActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) sendMailForSupplierWisePayableActionService.execute(null, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForSupplierWisePayableActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) sendMailForSupplierWisePayableActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def sendMailForUnpostedVoucher() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) sendMailForUnpostedVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForUnpostedVoucherActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) sendMailForUnpostedVoucherActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) sendMailForUnpostedVoucherActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) sendMailForUnpostedVoucherActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def listProjectWiseExpenseDetails() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForProjectWiseExpenseDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectWiseExpenseDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForProjectWiseExpenseDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectWiseExpenseDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) listForProjectWiseExpenseDetailsActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadProjectWiseExpense() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForProjectWiseExpenseActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectWiseExpenseActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForProjectWiseExpenseActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectWiseExpenseActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadProjectWiseExpenseCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForProjectWiseExpenseCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectWiseExpenseCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForProjectWiseExpenseCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectWiseExpenseCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showSourceWiseBalance() {
        render(view: '/accounting/report/sourceWiseBalance/show', model: [modelJson: null])
    }

    def listSourceWiseBalance() {
        LinkedHashMap result
        String output
        LinkedHashMap executeResult
        Boolean isError

        Map preResult = (LinkedHashMap) listForSourceWiseBalanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForSourceWiseBalanceActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForSourceWiseBalanceActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForSourceWiseBalanceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) listForSourceWiseBalanceActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def downloadSourceWiseBalance() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSourceWiseBalanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceWiseBalanceActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSourceWiseBalanceActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceWiseBalanceActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSourceWiseBalanceCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForSourceWiseBalanceCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceWiseBalanceCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForSourceWiseBalanceCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForSourceWiseBalanceCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadVoucherListBySourceId() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForVoucherListBySourceIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherListBySourceIdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForVoucherListBySourceIdActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForVoucherListBySourceIdActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showAccIouSlipRpt() {
        Map executeResult = (Map) showForAccIouSlipActionService.execute(params, null)
        render(view: '/accounting/report/accIouSlip/show', model: [result: executeResult])
    }

    def listAccIouSlipRpt() {
        LinkedHashMap result
        LinkedHashMap executeResult
        Boolean isError

        Map preResult = (LinkedHashMap) listForAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForAccIouSlipActionService.buildFailureResultForUI(preResult);
            render(template: '/accounting/report/accIouSlip/tmpIouSlip', model: [result: result])
            return
        }

        result = (LinkedHashMap) listForAccIouSlipActionService.execute(params, preResult);
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForAccIouSlipActionService.buildFailureResultForUI(result)
        }

        render(template: '/accounting/report/accIouSlip/tmpIouSlip', model: [result: result])
    }

    def downloadAccIouSlipRpt() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForAccIouSlipActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForAccIouSlipActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForAccIouSlipActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForAccIouSlipActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def retrieveTotalOfPayCash() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) getTotalOfPayCashActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) getTotalOfPayCashActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        isError = (Boolean) preResult.isError
        if (isError) {
            result = (Map) getTotalOfPayCashActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) getTotalOfPayCashActionService.execute(null, preResult)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (Map) getTotalOfPayCashActionService.buildFailureResultForUI(result)
        }
        output = result as JSON
        render output
    }

    def retrieveTotalOfPayCheque() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) getTotalOfPayChequeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess) {
            result = (Map) getTotalOfPayChequeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        isError = (Boolean) preResult.isError
        if (isError) {
            result = (Map) getTotalOfPayChequeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) getTotalOfPayChequeActionService.execute(null, preResult)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (Map) getTotalOfPayChequeActionService.buildFailureResultForUI(result)
        }
        output = result as JSON
        render output
    }

    def retrieveTotalOfPayChequeOnChequeDate() {
        Map result = (LinkedHashMap) getTotalOfPayChequeOnChequeDateActionService.execute(null, null)
        String output = result as JSON
        render output
    }

    def showSupplierWisePayable() {
        Map result
        Boolean isError

        result = (Map) accShowForSupplierWisePayableActionService.execute(params, null);
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (Map) accShowForSupplierWisePayableActionService.buildFailureResultForUI(result)
        }
        render(view: '/accounting/report/supplierWisePayable/show', model: [modelJson: result as JSON])
    }

    def listSupplierWisePayable() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) accListForSupplierWisePayableActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accListForSupplierWisePayableActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) accListForSupplierWisePayableActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accListForSupplierWisePayableActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) accListForSupplierWisePayableActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadSupplierWisePayable() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) accDownloadForSupplierWisePayableActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForSupplierWisePayableActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) accDownloadForSupplierWisePayableActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForSupplierWisePayableActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        /* send byteArray to outputStream */
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSupplierWisePayableCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) accDownloadForSupplierWisePayableCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForSupplierWisePayableCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) accDownloadForSupplierWisePayableCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForSupplierWisePayableCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        /* send byteArray to outputStream */
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // ---------------- For Bank Statement Cheque ----------------
    def showBankReconciliationCheque() {
        render(view: '/accounting/report/accBankReconciliationCheque/show', model: [modelJson: null])
    }

    def listBankReconciliationCheque() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) accListForBankReconciliationChequeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accListForBankReconciliationChequeActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) accListForBankReconciliationChequeActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accListForBankReconciliationChequeActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) accListForBankReconciliationChequeActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }

    def downloadBankReconciliationCheque() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) accDownloadForBankReconciliationChequeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForBankReconciliationChequeActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) accDownloadForBankReconciliationChequeActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForBankReconciliationChequeActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadBankReconciliationChequeCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) accDownloadForBankReconciliationChequeCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForBankReconciliationChequeCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) accDownloadForBankReconciliationChequeCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) accDownloadForBankReconciliationChequeCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // used in Source Ledger Report : return SourceList-By-SourceCategoryId-And-AccSourceId
    def listSourceByCategoryAndType() {
        Map result
        Map executeResult
        Boolean isError

        Map preResult = (Map) getSourceListBySourceCategoryAndAccSourceIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceListBySourceCategoryAndAccSourceIdActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (LinkedHashMap) getSourceListBySourceCategoryAndAccSourceIdActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceListBySourceCategoryAndAccSourceIdActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        render(executeResult as JSON)
    }

    // used in AccReportController pages : return SourceCategoryList and SourceList
    def listSourceCategoryForSourceLedger() {
        Map result
        Map executeResult
        Boolean isError

        Map preResult = (Map) getSourceCategoryForSourceLedgerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceCategoryForSourceLedgerActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (LinkedHashMap) getSourceCategoryForSourceLedgerActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceCategoryForSourceLedgerActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        render(executeResult as JSON)
    }

    // used in AccReportController pages : return SourceCategoryList
    def listSourceCategoryForSourceWiseBalance() {
        Map result
        Map executeResult
        Boolean isError

        Map preResult = (Map) getSourceCategoryForSourceWiseBalanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceCategoryForSourceWiseBalanceActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (LinkedHashMap) getSourceCategoryForSourceWiseBalanceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceCategoryForSourceWiseBalanceActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        render(executeResult as JSON)
    }

    def showProjectFundFlowReport() {
        render(view: '/accounting/report/projectFundFlow/show', model: [modelJson: null])
    }

    def listProjectFundFlowReport() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForProjectFundFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectFundFlowActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) listForProjectFundFlowActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectFundFlowActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) listForProjectFundFlowActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def downloadProjectFundFlowReport() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) downloadForProjectFundFlowActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) downloadForProjectFundFlowActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) downloadForProjectFundFlowActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) downloadForProjectFundFlowActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
}
