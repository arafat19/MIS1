package com.athena.mis.inventory.controller

import com.athena.mis.inventory.actions.report.inventoryconsumption.DownloadForConsumedItemListActionService
import com.athena.mis.inventory.actions.report.inventoryconsumption.GetForConsumedItemListActionService
import com.athena.mis.inventory.actions.report.inventoryconsumption.ListForBudgetOfConsumptionActionService
import com.athena.mis.inventory.actions.report.inventoryconsumption.ShowForConsumedItemListActionService
import com.athena.mis.inventory.actions.report.inventoryproduction.DownloadForInventoryProductionActionService
import com.athena.mis.inventory.actions.report.inventoryproduction.SearchForInventoryProductionActionService
import com.athena.mis.inventory.actions.report.inventoryproduction.ShowForInventoryProductionActionService
import com.athena.mis.inventory.actions.report.inventorystatus.DownloadForInventoryStatusWithQuantityAndValueCsvActionService
import com.athena.mis.inventory.actions.report.inventorystatus.DownloadForInventoryStatusWithQuantityAndValuePdfActionService
import com.athena.mis.inventory.actions.report.inventorystatus.ListForInventoryStatusWithQuantityAndValueActionService
import com.athena.mis.inventory.actions.report.inventorystatuswithquantity.DownloadForInventoryStatusWithQuantityActionService
import com.athena.mis.inventory.actions.report.inventorystatuswithquantity.DownloadForInventoryStatusWithQuantityCsvActionService
import com.athena.mis.inventory.actions.report.inventorystatuswithquantity.ListForInventoryStatusWithQuantityActionService
import com.athena.mis.inventory.actions.report.inventorystatuswithvalue.DownloadForInventoryStatusWithValueActionService
import com.athena.mis.inventory.actions.report.inventorystatuswithvalue.DownloadForInventoryStatusWithValueCsvActionService
import com.athena.mis.inventory.actions.report.inventorystatuswithvalue.ListForInventoryStatusWithValueActionService
import com.athena.mis.inventory.actions.report.inventorystock.DownloadForInventoryStockActionService
import com.athena.mis.inventory.actions.report.inventorystock.DownloadForInventoryStockCsvActionService
import com.athena.mis.inventory.actions.report.inventorystock.ListForInventoryStockActionService
import com.athena.mis.inventory.actions.report.inventorysummary.DownloadForInventorySummaryActionService
import com.athena.mis.inventory.actions.report.inventorysummary.DownloadForInventorySummaryCsvActionService
import com.athena.mis.inventory.actions.report.inventorysummary.GetForInventorySummaryActionService
import com.athena.mis.inventory.actions.report.inventorytransactionlist.DownloadForInventoryTransactionListActionService
import com.athena.mis.inventory.actions.report.inventorytransactionlist.DownloadForInventoryTransactionListCsvActionService
import com.athena.mis.inventory.actions.report.inventorytransactionlist.SearchForInventoryTransactionListActionService
import com.athena.mis.inventory.actions.report.inventoryvaluation.DownloadForInventoryValuationActionService
import com.athena.mis.inventory.actions.report.inventoryvaluation.DownloadForInventoryValuationCsvActionService
import com.athena.mis.inventory.actions.report.inventoryvaluation.SearchForInventoryValuationActionService
import com.athena.mis.inventory.actions.report.invoice.DownloadForInvoiceActionService
import com.athena.mis.inventory.actions.report.invoice.SearchForInvoiceActionService
import com.athena.mis.inventory.actions.report.invoice.ShowForInvoiceActionService
import com.athena.mis.inventory.actions.report.invpoitemreceived.InvDownloadForPoItemReceivedActionService
import com.athena.mis.inventory.actions.report.invpoitemreceived.InvDownloadForPoItemReceivedCsvActionService
import com.athena.mis.inventory.actions.report.invpoitemreceived.InvListForPoItemReceivedActionService
import com.athena.mis.inventory.actions.report.invsupplierchalan.DownloadForInventorySupplierchalanActionService
import com.athena.mis.inventory.actions.report.invsupplierchalan.DownloadForInventorySupplierchalanCsvActionService
import com.athena.mis.inventory.actions.report.invsupplierchalan.InvListForSupplierChalanActionService
import com.athena.mis.inventory.actions.report.invsupplierchalan.InvShowForSupplierChalanActionService
import com.athena.mis.inventory.actions.report.itemreceivedstock.DownloadForGroupByItemReceivedStockActionService
import com.athena.mis.inventory.actions.report.itemreceivedstock.DownloadForItemReceivedStockActionService
import com.athena.mis.inventory.actions.report.itemreceivedstock.DownloadForItemReceivedStockCsvActionService
import com.athena.mis.inventory.actions.report.itemreceivedstock.ListForItemReceivedStockActionService
import com.athena.mis.inventory.actions.report.itemreceivedstock.SearchForItemReceivedStockActionService
import com.athena.mis.inventory.actions.report.itemreconciliation.DownloadForItemReconciliationActionService
import com.athena.mis.inventory.actions.report.itemreconciliation.DownloadForItemReconciliationCsvActionService
import com.athena.mis.inventory.actions.report.itemreconciliation.ListForItemReconciliationActionService
import com.athena.mis.inventory.actions.report.itemstock.GetForStockDetailsListByItemIdActionService
import com.athena.mis.inventory.actions.report.itemstock.ListForItemStockActionService
import com.athena.mis.inventory.actions.report.itemstock.SearchForItemStockListActionService
import com.athena.mis.inventory.actions.report.itemstock.ShowForItemStockActionService
import com.athena.mis.inventory.actions.report.itemwisebudgetsummary.DownloadForItemWiseBudgetSummaryActionService
import com.athena.mis.inventory.actions.report.itemwisebudgetsummary.DownloadForItemWiseBudgetSummaryCsvActionService
import com.athena.mis.inventory.actions.report.itemwisebudgetsummary.ListForItemWiseBudgetSummaryActionService
import com.athena.mis.utility.DateUtility
import grails.converters.JSON
import groovy.json.JsonBuilder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class InvReportController {

    static allowedMethods = [
            showInvoice: "POST", searchInvoice: "POST", showItemStock: "POST", listItem: "POST",
            getStockDetailsListByItemId: "POST", showInventoryValuation: "POST",
            searchInventoryValuation: "POST", showInventoryTransactionList: "POST",
            searchInventoryTransactionList: "POST", getInventorySummary: "POST",
            listInventoryStock: "POST", listItemStock: "POST", listBudgetOfConsumption: "POST",
            getConsumedItemList: "POST", showConsumedItemList: "POST", showItemReceivedStock: "POST",
            listItemReceivedStock: "POST", showItemWiseBudgetSummary: "POST", listItemWiseBudgetSummary: "POST",
            showInventoryStatusWithQuantityAndValue: "POST", listInventoryStatusWithQuantityAndValue: "POST", showInventoryProductionRpt: "POST",
            showInventoryStatusWithValue: "POST", listInventoryStatusWithValue: "POST",
            searchInventoryProductionRpt: "POST", showSupplierChalan: "POST", listSupplierChalan: "POST",
            showPoItemReceived: "POST", listPoItemReceived: "POST", showProjectInventoryWithQuantity: "POST",
            listProjectInventoryWithQuantity: "POST", showForItemReconciliation: "POST", listForItemReconciliation: "POST"
    ]

    ShowForInvoiceActionService showForInvoiceActionService
    SearchForInvoiceActionService searchForInvoiceActionService
    DownloadForInvoiceActionService downloadForInvoiceActionService

    ListForInventoryStockActionService listForInventoryStockActionService
    DownloadForInventoryStockActionService downloadForInventoryStockActionService
    DownloadForInventoryStockCsvActionService downloadForInventoryStockCsvActionService

    ListForInventoryStatusWithQuantityAndValueActionService listForInventoryStatusWithQuantityAndValueActionService
    DownloadForInventoryStatusWithQuantityAndValuePdfActionService downloadForInventoryStatusWithQuantityAndValuePdfActionService
    DownloadForInventoryStatusWithQuantityAndValueCsvActionService downloadForInventoryStatusWithQuantityAndValueCsvActionService

    ShowForItemStockActionService showForItemStockActionService
    ListForItemStockActionService listForItemStockActionService
    GetForStockDetailsListByItemIdActionService getForStockDetailsListByItemIdActionService
    SearchForItemStockListActionService searchForItemStockListActionService

    SearchForInventoryValuationActionService searchForInventoryValuationActionService
    DownloadForInventoryValuationActionService downloadForInventoryValuationActionService
    DownloadForInventoryValuationCsvActionService downloadForInventoryValuationCsvActionService

    SearchForInventoryTransactionListActionService searchForInventoryTransactionListActionService
    DownloadForInventoryTransactionListActionService downloadForInventoryTransactionListActionService
    DownloadForInventoryTransactionListCsvActionService downloadForInventoryTransactionListCsvActionService

    GetForInventorySummaryActionService getForInventorySummaryActionService
    DownloadForInventorySummaryActionService downloadForInventorySummaryActionService
    DownloadForInventorySummaryCsvActionService downloadForInventorySummaryCsvActionService

    ShowForConsumedItemListActionService showForConsumedItemListActionService
    ListForBudgetOfConsumptionActionService listForBudgetOfConsumptionActionService
    GetForConsumedItemListActionService getForConsumedItemListActionService
    DownloadForConsumedItemListActionService downloadForConsumedItemListActionService

    ListForItemReceivedStockActionService listForItemReceivedStockActionService
    SearchForItemReceivedStockActionService searchForItemReceivedStockActionService
    DownloadForGroupByItemReceivedStockActionService downloadForGroupByItemReceivedStockActionService
    DownloadForItemReceivedStockActionService downloadForItemReceivedStockActionService
    DownloadForItemReceivedStockCsvActionService downloadForItemReceivedStockCsvActionService

    ListForItemWiseBudgetSummaryActionService listForItemWiseBudgetSummaryActionService
    DownloadForItemWiseBudgetSummaryActionService downloadForItemWiseBudgetSummaryActionService
    DownloadForItemWiseBudgetSummaryCsvActionService downloadForItemWiseBudgetSummaryCsvActionService

    ShowForInventoryProductionActionService showForInventoryProductionActionService
    SearchForInventoryProductionActionService searchForInventoryProductionActionService
    DownloadForInventoryProductionActionService downloadForInventoryProductionActionService

    InvShowForSupplierChalanActionService invShowForSupplierChalanActionService
    InvListForSupplierChalanActionService invListForSupplierChalanActionService

    InvListForPoItemReceivedActionService invListForPoItemReceivedActionService
    InvDownloadForPoItemReceivedActionService invDownloadForPoItemReceivedActionService
    InvDownloadForPoItemReceivedCsvActionService invDownloadForPoItemReceivedCsvActionService

    //for downloading supplier chalan report
    DownloadForInventorySupplierchalanActionService downloadForInventorySupplierchalanActionService
    DownloadForInventorySupplierchalanCsvActionService downloadForInventorySupplierchalanCsvActionService

    // for inventory status with value
    ListForInventoryStatusWithValueActionService listForInventoryStatusWithValueActionService
    DownloadForInventoryStatusWithValueActionService downloadForInventoryStatusWithValueActionService
    DownloadForInventoryStatusWithValueCsvActionService downloadForInventoryStatusWithValueCsvActionService

    // for project inventory with Quantity
    ListForInventoryStatusWithQuantityActionService listForInventoryStatusWithQuantityActionService
    DownloadForInventoryStatusWithQuantityActionService downloadForInventoryStatusWithQuantityActionService
    DownloadForInventoryStatusWithQuantityCsvActionService downloadForInventoryStatusWithQuantityCsvActionService

    // Item-Reconciliation Report
    ListForItemReconciliationActionService listForItemReconciliationActionService
    DownloadForItemReconciliationActionService downloadForItemReconciliationActionService
    DownloadForItemReconciliationCsvActionService downloadForItemReconciliationCsvActionService

    def showInvoice() {
        Map executeResult = (Map) showForInvoiceActionService.execute(params, null);
        render(view: '/inventory/report/invoice/show', model: [result: executeResult])
    }

    def searchInvoice() {
        LinkedHashMap preResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) searchForInvoiceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInvoiceActionService.buildFailureResultForUI(preResult);
            render(template: '/inventory/report/invoice/tmpInvoice', model: [result: result])
            return
        }

        result = (LinkedHashMap) searchForInvoiceActionService.execute(params, preResult);
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInvoiceActionService.buildFailureResultForUI(result)
        }

        render(template: '/inventory/report/invoice/tmpInvoice', model: [result: result])
    }

    def downloadInvoice() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInvoiceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInvoiceActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInvoiceActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInvoiceActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def inventoryStock() {
        render(view: '/inventory/report/stock/show', model: [modelJson: null])
    }

    def listInventoryStock() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForInventoryStockActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStockActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForInventoryStockActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStockActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForInventoryStockActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadInventoryStock() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventoryStockActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStockActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventoryStockActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStockActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadInventoryStockCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventoryStockCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStockCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventoryStockCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStockCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // ----------- For Item Stock Report -----------
    def showItemStock() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        executeResult = (LinkedHashMap) showForItemStockActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForItemStockActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) showForItemStockActionService.buildSuccessResultForUI(executeResult);
        }
        String modelJson = result as JSON
        render(view: '/inventory/report/itemStock/show', model: [modelJson: modelJson])
    }

    def listItemStock() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (LinkedHashMap) searchForItemStockListActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForItemStockListActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (LinkedHashMap) searchForItemStockListActionService.buildSuccessResultForUI(executeResult);
            }
        } else {
            executeResult = (LinkedHashMap) listForItemStockActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForItemStockActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) listForItemStockActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON);

    }

    def getStockDetailsListByItemId() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) getForStockDetailsListByItemIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForStockDetailsListByItemIdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) getForStockDetailsListByItemIdActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForStockDetailsListByItemIdActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) getForStockDetailsListByItemIdActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    // ----------- For Inventory Valuation Report -----------
    def showInventoryValuation() {
        render(view: '/inventory/report/inventoryValuation/show', model: [modelJson: null])
    }

    def searchInventoryValuation() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) searchForInventoryValuationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInventoryValuationActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) searchForInventoryValuationActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInventoryValuationActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) searchForInventoryValuationActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def downloadInventoryValuation() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventoryValuationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryValuationActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventoryValuationActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryValuationActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadInventoryValuationCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventoryValuationCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryValuationCsvActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventoryValuationCsvActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryValuationCsvActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // ----------- For Inventory Transaction List Report -----------
    def showInventoryTransactionList() {
        render(view: '/inventory/report/inventoryTransaction/show', model: [modelJson: null])
    }

    def searchInventoryTransactionList() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) searchForInventoryTransactionListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInventoryTransactionListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) searchForInventoryTransactionListActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInventoryTransactionListActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) searchForInventoryTransactionListActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def downloadInventoryTransactionList() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryTransactionListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryTransactionListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryTransactionListActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryTransactionListActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadInventoryTransactionListCsv() {
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryTransactionListCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryTransactionListCsvActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryTransactionListCsvActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryTransactionListCsvActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /***************Inventory Summary list report ************************/

    def showInventorySummary() {
        render(view: '/inventory/report/inventorySummary/show', model: [modelJson: null])
    }

    def getInventorySummary() {
        Map result
        String output
        Map preResult = (Map) getForInventorySummaryActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getForInventorySummaryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) getForInventorySummaryActionService.execute(params, preResult);

        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getForInventorySummaryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }
        result = (Map) getForInventorySummaryActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadInventorySummary() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventorySummaryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySummaryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventorySummaryActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySummaryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    def downloadInventorySummaryCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventorySummaryCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySummaryCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventorySummaryCsvActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySummaryCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    /***************Item consumption list report ************************/
    def showConsumedItemList() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        executeResult = (LinkedHashMap) showForConsumedItemListActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForConsumedItemListActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) showForConsumedItemListActionService.buildSuccessResultForUI(executeResult);
        }
        String modelJson = result as JSON
        render(view: '/inventory/report/itemConsumption/show', model: [modelJson: modelJson])
    }


    def listBudgetOfConsumption() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) listForBudgetOfConsumptionActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForBudgetOfConsumptionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }


        result = (LinkedHashMap) listForBudgetOfConsumptionActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def getConsumedItemList() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) getForConsumedItemListActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getForConsumedItemListActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }


        result = (LinkedHashMap) getForConsumedItemListActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def downloadForConsumedItemList() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForConsumedItemListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumedItemListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForConsumedItemListActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumedItemListActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // -------- For Item wise Budget Summary Report

    def showItemWiseBudgetSummary() {
        render(view: '/inventory/report/itemWiseBudgetSummary/show', model: [modelJson: null])
    }

    def listItemWiseBudgetSummary() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForItemWiseBudgetSummaryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForItemWiseBudgetSummaryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForItemWiseBudgetSummaryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForItemWiseBudgetSummaryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForItemWiseBudgetSummaryActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output

    }

    def downloadItemWiseBudgetSummary() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForItemWiseBudgetSummaryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemWiseBudgetSummaryActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForItemWiseBudgetSummaryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemWiseBudgetSummaryActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    def downloadItemWiseBudgetSummaryCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForItemWiseBudgetSummaryCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemWiseBudgetSummaryCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForItemWiseBudgetSummaryCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemWiseBudgetSummaryCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showItemReceivedStock() {
        render(view: '/inventory/report/itemReceivedStock/show', model: [modelJson: null])
    }

    def listItemReceivedStock() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        if (params.query) {
            preResult = (LinkedHashMap) searchForItemReceivedStockActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForItemReceivedStockActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) searchForItemReceivedStockActionService.execute(params, preResult);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForItemReceivedStockActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchForItemReceivedStockActionService.buildSuccessResultForUI(executeResult);

        } else {
            preResult = (LinkedHashMap) listForItemReceivedStockActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForItemReceivedStockActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) listForItemReceivedStockActionService.execute(params, preResult);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForItemReceivedStockActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) listForItemReceivedStockActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    def downloadItemReceivedGroupBySupplier() {
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) downloadForGroupByItemReceivedStockActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForGroupByItemReceivedStockActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForGroupByItemReceivedStockActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForGroupByItemReceivedStockActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadItemReceivedStock() {
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) downloadForItemReceivedStockActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReceivedStockActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForItemReceivedStockActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReceivedStockActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadItemReceivedStockCsv() {
        LinkedHashMap result
        Boolean isError
        String output

        LinkedHashMap preResult = (LinkedHashMap) downloadForItemReceivedStockCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReceivedStockCsvActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForItemReceivedStockCsvActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReceivedStockCsvActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /// inventory status
    def showInventoryStatusWithQuantityAndValue() {
        render(view: '/inventory/report/inventoryStatus/show', model: [modelJson: null])
    }

    def listInventoryStatusWithQuantityAndValue() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForInventoryStatusWithQuantityAndValueActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStatusWithQuantityAndValueActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForInventoryStatusWithQuantityAndValueActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStatusWithQuantityAndValueActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForInventoryStatusWithQuantityAndValueActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadInventoryStatusWithQuantityAndValue() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValuePdfActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValuePdfActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValuePdfActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValuePdfActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadInventoryStatusWithQuantityAndValueCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValueCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValueCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValueCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityAndValueCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // inventory status with value
    def showInventoryStatusWithValue() {
        render(view: '/inventory/report/inventoryStatusWithValue/show', model: [modelJson: null])
    }

    def listInventoryStatusWithValue() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForInventoryStatusWithValueActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStatusWithValueActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForInventoryStatusWithValueActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStatusWithValueActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForInventoryStatusWithValueActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadInventoryStatusWithValue() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryStatusWithValueActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithValueActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryStatusWithValueActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithValueActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadInventoryStatusWithValueCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryStatusWithValueCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithValueCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryStatusWithValueCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithValueCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
    // ----------end of inventory status with value----------

    // project inventory with Quantity
    def showInventoryStatusWithQuantity() {
        render(view: '/inventory/report/inventoryStatusWithQuantity/show', model: [modelJson: null])
    }

    def listInventoryStatusWithQuantity() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForInventoryStatusWithQuantityActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStatusWithQuantityActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForInventoryStatusWithQuantityActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForInventoryStatusWithQuantityActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForInventoryStatusWithQuantityActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadInventoryStatusWithQuantity() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadInventoryStatusWithQuantityCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryStatusWithQuantityCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryStatusWithQuantityCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // ----------end of project inventory with Quantity----------
    def showInventoryProductionRpt() {
        Map executeResult = (Map) showForInventoryProductionActionService.execute(params, null);

        render(view: '/inventory/report/inventoryProduction/show', model: [result: executeResult])
    }

    def searchInventoryProductionRpt() {
        LinkedHashMap preResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) searchForInventoryProductionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInventoryProductionActionService.buildFailureResultForUI(preResult);
            render(template: '/inventory/report/inventoryProduction/tmpProduction', model: [result: result])
            return
        }

        result = (LinkedHashMap) searchForInventoryProductionActionService.execute(params, preResult);
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForInventoryProductionActionService.buildFailureResultForUI(result)
        }

        render(template: '/inventory/report/inventoryProduction/tmpProduction', model: [result: result])
    }

    def downloadInventoryProductionRpt() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventoryProductionActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryProductionActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventoryProductionActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventoryProductionActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // ------------------------------- Supplier Challan -------------------------------
    def showSupplierChalan() {
        Map executeResult = (Map) invShowForSupplierChalanActionService.execute(params, null)
        render(view: '/inventory/report/invSupplierChalan/show', model: [modelJson: executeResult as JSON])
    }

    def listSupplierChalan() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) invListForSupplierChalanActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invListForSupplierChalanActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) invListForSupplierChalanActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invListForSupplierChalanActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) invListForSupplierChalanActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    // ------------------------------- PO Item Received -------------------------------
    def showPoItemReceived() {
        render(view: '/inventory/report/poItemReceived/show', model: [modelJson: null])
    }

    def listPoItemReceived() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) invListForPoItemReceivedActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invListForPoItemReceivedActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) invListForPoItemReceivedActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invListForPoItemReceivedActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) invListForPoItemReceivedActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

    def downloadPoItemReceived() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) invDownloadForPoItemReceivedActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invDownloadForPoItemReceivedActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) invDownloadForPoItemReceivedActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invDownloadForPoItemReceivedActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadPoItemReceivedCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) invDownloadForPoItemReceivedCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invDownloadForPoItemReceivedCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) invDownloadForPoItemReceivedCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) invDownloadForPoItemReceivedCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSupplierChalanReport() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventorySupplierchalanActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySupplierchalanActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventorySupplierchalanActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySupplierchalanActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadSupplierChalanCsvReport() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForInventorySupplierchalanCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySupplierchalanCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForInventorySupplierchalanCsvActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForInventorySupplierchalanCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // Item-Reconciliation Report
    def showForItemReconciliation() {
        render(view: '/inventory/report/itemReconciliation/show', model: [modelJson: null])
    }

    def listForItemReconciliation() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForItemReconciliationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForItemReconciliationActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForItemReconciliationActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForItemReconciliationActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForItemReconciliationActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadForItemReconciliation() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForItemReconciliationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReconciliationActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForItemReconciliationActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReconciliationActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadForItemReconciliationCsv() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) downloadForItemReconciliationCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReconciliationCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) downloadForItemReconciliationCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForItemReconciliationCsvActionService.buildFailureResultForUI(executeResult);
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
