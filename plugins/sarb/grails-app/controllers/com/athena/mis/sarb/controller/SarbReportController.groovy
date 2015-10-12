package com.athena.mis.sarb.controller

import com.athena.mis.sarb.actions.report.DownloadSarbTransactionSummaryActionService
import com.athena.mis.sarb.actions.report.ListSarbTransactionSummaryActionService
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SarbReportController {
    static allowedMethods = [
            showSarbTransactionSummery: "POST",
            listSarbTransactionSummery: "POST",
            downloadSarbTransactionSummery: "POST"
    ]

    ListSarbTransactionSummaryActionService listSarbTransactionSummaryActionService
    DownloadSarbTransactionSummaryActionService downloadSarbTransactionSummaryActionService

    def showSarbTransactionSummary() {
        render(view: '/sarb/report/transactionSummary/showSarbTransactionSummary')
    }

    def listSarbTransactionSummary() {
        Map result
        Boolean isError

        Map executeResult = (Map) listSarbTransactionSummaryActionService.execute(params, null)
        isError = executeResult.isError
        if(isError.booleanValue()) {
            result = (Map) listSarbTransactionSummaryActionService.buildFailureResultForUI(executeResult)
        }else{
            result = executeResult
        }
        String output = result as JSON
        render output

    }

    def downloadSarbTransactionSummary() {
        Map preResult = (Map) downloadSarbTransactionSummaryActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }
        Map executeResult = (Map) downloadSarbTransactionSummaryActionService.execute(null, preResult)
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
}
