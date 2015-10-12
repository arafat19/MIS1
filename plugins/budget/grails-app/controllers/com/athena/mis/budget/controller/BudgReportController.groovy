package com.athena.mis.budget.controller

import com.athena.mis.budget.actions.report.budget.DownloadForBudgetActionService
import com.athena.mis.budget.actions.report.budget.SearchForBudgetActionService
import com.athena.mis.budget.actions.report.budget.ShowForBudgetActionService
import com.athena.mis.budget.actions.report.consumptiondeviation.DownloadForConsumptionDeviationActionService
import com.athena.mis.budget.actions.report.consumptiondeviation.DownloadForConsumptionDeviationCsvActionService
import com.athena.mis.budget.actions.report.consumptiondeviation.ListForConsumptionDeviationActionService
import com.athena.mis.budget.actions.report.projectbudget.DownloadForProjectBudgetActionService
import com.athena.mis.budget.actions.report.projectbudget.ListForProjectBudgetActionService
import com.athena.mis.budget.actions.report.projectbudget.SearchForProjectBudgetActionService
import com.athena.mis.budget.actions.report.projectcosting.DownloadForProjectCostingActionService
import com.athena.mis.budget.actions.report.projectcosting.ListForProjectCostingActionService
import com.athena.mis.budget.actions.report.projectstatus.DownloadForProjectStatusActionService
import com.athena.mis.budget.actions.report.projectstatus.SearchForProjectStatusActionService
import com.athena.mis.budget.actions.report.sprint.DownloadForBudgSprintActionService
import com.athena.mis.budget.actions.report.sprint.DownloadForecastingReportActionService
import com.athena.mis.budget.actions.report.sprint.SearchForBudgetSprintActionService
import com.athena.mis.budget.actions.report.sprint.ShowForBudgetSprintActionService
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class BudgReportController {

    static allowedMethods = [
            showBudgetRpt: "POST", searchBudgetRpt: "POST", showProjectStatus: "POST", searchProjectStatus: "POST",
            showConsumptionDeviation: "POST", listConsumptionDeviation: "POST", showProjectCosting: "POST",
            listProjectCosting: "POST", showBudgetSprint: "POST", searchBudgetSprint: "POST", downloadSprintReport: "GET",
            showProjectBudget: "POST", listProjectBudget: "POST", downloadProjectBudget: "GET", downloadForecastingReport: "GET"
    ]

    ShowForBudgetActionService showForBudgetActionService
    DownloadForBudgetActionService downloadForBudgetActionService
    SearchForBudgetActionService searchForBudgetActionService
    SearchForProjectStatusActionService searchForProjectStatusActionService
    DownloadForProjectStatusActionService downloadForProjectStatusActionService
    ListForConsumptionDeviationActionService listForConsumptionDeviationActionService
    DownloadForConsumptionDeviationActionService downloadForConsumptionDeviationActionService
    DownloadForConsumptionDeviationCsvActionService downloadForConsumptionDeviationCsvActionService
    ListForProjectCostingActionService listForProjectCostingActionService
    DownloadForProjectCostingActionService downloadForProjectCostingActionService
    DownloadForBudgSprintActionService downloadForBudgSprintActionService
    ShowForBudgetSprintActionService showForBudgetSprintActionService
    SearchForBudgetSprintActionService searchForBudgetSprintActionService
    ListForProjectBudgetActionService listForProjectBudgetActionService
    SearchForProjectBudgetActionService searchForProjectBudgetActionService
    DownloadForProjectBudgetActionService downloadForProjectBudgetActionService
    DownloadForecastingReportActionService downloadForecastingReportActionService

    /**
     * Show budget report
     */
    def showBudgetRpt() {
        Map result = (Map) showForBudgetActionService.execute(params, null)
        render(view: '/budget/report/budget/show', model: [result: result])
    }

    /**
     * Search budget report
     */
    def searchBudgetRpt() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) searchForBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForBudgetActionService.buildFailureResultForUI(preResult)
            render(template: '/budget/report/budget/tmpBudget', model: [result: result])
            return
        }

        result = (LinkedHashMap) searchForBudgetActionService.execute(params, preResult)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForBudgetActionService.buildFailureResultForUI(result)
        }
        render(template: '/budget/report/budget/tmpBudget', model: [result: result])
    }

    /**
     * Download Budget report
     */
    def downloadBudgetRpt() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForBudgetActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForBudgetActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForBudgetActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForBudgetActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    /**
     * Show Project status
     */
    def showProjectStatus() {
        render(view: '/budget/report/projectStatus/show', model: [result: null])
    }

    /**
     * Search project status
     */
    def searchProjectStatus() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError

        preResult = (LinkedHashMap) searchForProjectStatusActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForProjectStatusActionService.buildFailureResultForUI(preResult)
            render(template: '/budget/report/projectStatus/tmpProjectStatus', model: [result: result])
            return
        }

        result = (LinkedHashMap) searchForProjectStatusActionService.execute(params, preResult)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForProjectStatusActionService.buildFailureResultForUI(result)
        }
        render(template: '/budget/report/projectStatus/tmpProjectStatus', model: [result: result])
    }

    /**
     * Download Project status
     */
    def downloadProjectStatus() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForProjectStatusActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectStatusActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForProjectStatusActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectStatusActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    /**
     * Show consumption deviation
     */
    def showConsumptionDeviation() {
        render(view: '/budget/report/consumptionDeviation/show', model: [modelJson: null])
    }

    /**
     * List of consumption deviation
     */
    def listConsumptionDeviation() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForConsumptionDeviationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForConsumptionDeviationActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForConsumptionDeviationActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForConsumptionDeviationActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForConsumptionDeviationActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * Download Consumption deviation pdf report
     */
    def downloadConsumptionDeviation() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForConsumptionDeviationActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumptionDeviationActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForConsumptionDeviationActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumptionDeviationActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()

    }

    /**
     * Download Consumption deviation CSV report
     */
    def downloadConsumptionDeviationCsv() {
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForConsumptionDeviationCsvActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumptionDeviationCsvActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForConsumptionDeviationCsvActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForConsumptionDeviationCsvActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Show Project costing
     */
    def showProjectCosting() {
        render(view: '/budget/report/projectCosting/show', model: [modelJson: null])
    }

    def listProjectCosting(){
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) listForProjectCostingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectCostingActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) listForProjectCostingActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) listForProjectCostingActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        result = (LinkedHashMap) listForProjectCostingActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def downloadProjectCosting(){
        LinkedHashMap result
        Boolean isError
        String output
        LinkedHashMap preResult = (LinkedHashMap) downloadForProjectCostingActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectCostingActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return
        }

        LinkedHashMap executeResult = (LinkedHashMap) downloadForProjectCostingActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForProjectCostingActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Download budget sprint report
     */
    def downloadSprintReport() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) downloadForBudgSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForBudgSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadForBudgSprintActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForBudgSprintActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    /**
     * Show budget sprint
     */
    def showBudgetSprint() {
        Map result = (Map) showForBudgetSprintActionService.execute(params, null)
        String output = result as JSON
        render(view: '/budget/report/budgetSprint/show', model: [output: output])
    }

    /**
     * Search budget sprint
     */
    def searchBudgetSprint(){
        Map result
        Boolean isError
        String output

        Map preResult = (Map) searchForBudgetSprintActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) searchForBudgetSprintActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) searchForBudgetSprintActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchForBudgetSprintActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) searchForBudgetSprintActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showProjectBudget() {
        render(view: '/budget/report/projectBudget/show', model: [modelJson: null])
    }

    def listProjectBudget(){
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        if (params.query) {
            preResult = (Map) searchForProjectBudgetActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchForProjectBudgetActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) searchForProjectBudgetActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchForProjectBudgetActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchForProjectBudgetActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            preResult = (Map) listForProjectBudgetActionService.executePreCondition(params, null)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) listForProjectBudgetActionService.buildFailureResultForUI(preResult)
                output = result as JSON
                render output
                return
            }
            executeResult = (Map) listForProjectBudgetActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listForProjectBudgetActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listForProjectBudgetActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def downloadProjectBudget() {
        Map executeResult = (Map) downloadForProjectBudgetActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            Map result = (LinkedHashMap) downloadForProjectBudgetActionService.buildFailureResultForUI(executeResult)
            String output = result as JSON
            render output
            return
        }

        Map reportResult = (Map) executeResult.report
        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def downloadForecastingReport() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) downloadForecastingReportActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForecastingReportActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadForecastingReportActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadForecastingReportActionService.buildFailureResultForUI(executeResult)
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
