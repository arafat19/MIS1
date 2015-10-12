package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.report.*
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RmsReportController {

    static allowedMethods = [
            showTaskListPlan: "POST",
            searchTaskListPlan: "POST",
            showBeneficiaryDetails: "POST",
            searchBeneficiaryDetails: "POST",
            showForForwardUnpaidTask: "POST",
            listTaskForForwardUnpaidTask: "POST",
            listTaskDetailsForForwardedUnpaidTasks: "POST",
            showForListWiseStatusReport: "POST",
            listForListWiseStatusReport: "POST",
            listForViewCancelTask: "POST",
            showForViewCancelTask: "POST",
            showDecisionSummary: "POST",
            listDecisionSummary: "POST"
    ]

    SearchTaskListPlanActionService searchTaskListPlanActionService
    DownloadListWiseStatusReportActionService downloadListWiseStatusReportActionService
    SearchBeneficiaryDetailsActionService searchBeneficiaryDetailsActionService
    ListForListWiseStatusReportActionService listForListWiseStatusReportActionService
    ListTaskForForwardUnpaidTaskActionService listTaskForForwardUnpaidTaskActionService
    ListForViewCancelTaskActionService listForViewCancelTaskActionService
    ListTaskDetailsForForwardedUnpaidTasksActionService listTaskDetailsForForwardedUnpaidTasksActionService
    ListDecisionSummaryActionService listDecisionSummaryActionService
    DownloadDecisionSummaryReportActionService downloadDecisionSummaryReportActionService

    /**
     * Show for list wise status report
     */
    def showForListWiseStatusReport() {
        render(view: '/arms/report/task/showForListWiseStatusReport', model: [modelJson: null])
    }
    /**
     * List for list wise status report
     */
    def listForListWiseStatusReport() {
        Boolean isError
        Map result
        Map executeResult

        executeResult = (Map) listForListWiseStatusReportActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listForListWiseStatusReportActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listForListWiseStatusReportActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }
    // download list wise status report
    def downloadListWiseStatusReport() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadListWiseStatusReportActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadListWiseStatusReportActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadListWiseStatusReportActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadListWiseStatusReportActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    def showTaskListPlan() {
        render(view: '/arms/report/taskList/showTaskListPlan', model: null)
    }

    def searchTaskListPlan() {
        Boolean isError
        Map result
        Map preResult = (Map) searchTaskListPlanActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) searchTaskListPlanActionService.buildFailureResultForUI(preResult)
        } else {
            Map executeResult = (Map) searchTaskListPlanActionService.execute(null, preResult)
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchTaskListPlanActionService.buildFailureResultForUI(executeResult)
            } else {
                result = executeResult
            }
        }
        render(template: '/arms/report/taskList/tmplTaskListPlan', model: [model: result])
    }

    /**
     * Show beneficiary details page
     */
    def showBeneficiaryDetails() {
        render(view: '/arms/report/beneficiary/showBeneficiaryDetails', model: [modelJson: null])
    }
    /**
     * Search beneficiary details
     */
    def searchBeneficiaryDetails() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) searchBeneficiaryDetailsActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) searchBeneficiaryDetailsActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) searchBeneficiaryDetailsActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def searchBeneficiaryForGrid() {
        render(template: '/arms/report/beneficiary/tmplBeneficiaryDetails', model: [modelJson: null])
    }

    def showForForwardUnpaidTask() {
        render(view: '/arms/report/task/showForForwardUnpaidTask', model: [model: null])
    }

    def listTaskForForwardUnpaidTask() {
        Map result
        Boolean isError
        String output
        Map preResult = (Map) listTaskForForwardUnpaidTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskForForwardUnpaidTaskActionService.buildFailureResultForUI(null)
            output = result as JSON
            render output
        }
        Map executeResult = (Map) listTaskForForwardUnpaidTaskActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskForForwardUnpaidTaskActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) listTaskForForwardUnpaidTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showForViewCancelTask() {
        render(view: '/arms/report/task/showForViewCancelTask', model: [model: null])
    }

    def listForViewCancelTask() {
        Map result
        Boolean isError
        String output
        Map preResult = (Map) listForViewCancelTaskActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listForViewCancelTaskActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
        }
        Map executeResult = (Map) listForViewCancelTaskActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listForViewCancelTaskActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) listForViewCancelTaskActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def listTaskDetailsForForwardedUnpaidTasks() {
        Map result
        Boolean isError
        String output
        Map preResult = (Map) listTaskDetailsForForwardedUnpaidTasksActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskDetailsForForwardedUnpaidTasksActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
        }
        Map executeResult = (Map) listTaskDetailsForForwardedUnpaidTasksActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listTaskDetailsForForwardedUnpaidTasksActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) listTaskDetailsForForwardedUnpaidTasksActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showDecisionSummary() {
        render(view: '/arms/report/decisionSummary/show')
    }

    def listDecisionSummary() {
        Map result
        Map executeResult = (Map) listDecisionSummaryActionService.execute(params, null)
        Boolean isError = executeResult.isError
        if(isError.booleanValue()) {
            result = (Map) listDecisionSummaryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listDecisionSummaryActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    def downloadDecisionSummaryReport() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadDecisionSummaryReportActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadDecisionSummaryReportActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadDecisionSummaryReportActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadDecisionSummaryReportActionService.buildFailureResultForUI(executeResult)
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
