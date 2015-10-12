package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.instrument.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RmsInstrumentController {

    static allowedMethods = [
            listTaskForProcessInstrument: "POST",
            showForIssuePo: "POST",
            showForIssueEft: "POST",
            showForIssueOnline: "POST",
            showForForwardOnline: "POST",
            showForForwardCashCollection: "POST",
            showForListWiseStatusReport: "POST",
            showForInstrumentPurchase: "POST",
            reloadInstrumentDropDown:"POST",
            reloadBankListFilteredDropDown: "POST"
    ]

    ListTaskForProcessInstrumentActionService listTaskForProcessInstrumentActionService
    SearchTaskForProcessInstrumentActionService searchTaskForProcessInstrumentActionService
    DownloadTaskReportForIssuePoActionService downloadTaskReportForIssuePoActionService
    DownloadTaskReportForIssueEftActionService downloadTaskReportForIssueEftActionService
    DownloadTaskReportForIssueOnlineActionService downloadTaskReportForIssueOnlineActionService
    DownloadTaskReportForForwardOnlineActionService downloadTaskReportForForwardOnlineActionService
    DownloadTaskReportForForwardCashCollectionActionService downloadTaskReportForForwardCashCollectionActionService
    DownloadTaskReportForPurchaseInstrumentActionService downloadTaskReportForPurchaseInstrumentActionService

    /**
     * Get list of task for process instrument
     */
    def listTaskForProcessInstrument() {
        Boolean isError
        Boolean hasAccess
        Map result
        Map executeResult
        Map preResult
        String output
        if (params.query) {
            preResult=(Map)searchTaskForProcessInstrumentActionService.executePreCondition(params,null)
            hasAccess = (Boolean) preResult.hasAccess
            if(!hasAccess.booleanValue()){
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
                return
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchTaskForProcessInstrumentActionService.buildFailureResultForUI(null)
                output = result as JSON
                render output
            }else{
                executeResult = (Map) searchTaskForProcessInstrumentActionService.execute(preResult, null)
                isError = (Boolean) executeResult.isError
                if (isError.booleanValue()) {
                    result = (Map) searchTaskForProcessInstrumentActionService.buildFailureResultForUI(executeResult)
                } else {
                    result = (Map) searchTaskForProcessInstrumentActionService.buildSuccessResultForUI(executeResult)
                }
            }
        } else {
            preResult=(Map)listTaskForProcessInstrumentActionService.executePreCondition(params,null)
            hasAccess = (Boolean) preResult.hasAccess
            if(!hasAccess.booleanValue()){
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
                return
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) listTaskForProcessInstrumentActionService.buildFailureResultForUI(null)
                output = result as JSON
                render output
            }else{
                executeResult = (Map) listTaskForProcessInstrumentActionService.execute(preResult, null)
                isError = (Boolean) executeResult.isError
                if (isError.booleanValue()) {
                    result = (Map) listTaskForProcessInstrumentActionService.buildFailureResultForUI(executeResult)
                } else {
                    result = (Map) listTaskForProcessInstrumentActionService.buildSuccessResultForUI(executeResult)
                }
            }
        }
        output = result as JSON
        render output

    }

    /**
     * Show issue PO page
     */
    def showForIssuePo() {
        render(view: '/arms/instrument/showForIssuePo', model: [modelJson: null])
    }
    // download task for issue PO
    def downloadTaskReportForIssuePo() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadTaskReportForIssuePoActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForIssuePoActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadTaskReportForIssuePoActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForIssuePoActionService.buildFailureResultForUI(executeResult)
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
     * Show issue EFT page
     */
    def showForIssueEft() {
        render(view: '/arms/instrument/showForIssueEft', model: [modelJson: null])
    }
    // download task for issue EFT
    def downloadTaskReportForIssueEft() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadTaskReportForIssueEftActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForIssueEftActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadTaskReportForIssueEftActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForIssueEftActionService.buildFailureResultForUI(executeResult)
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
     * Show issue online page
     */
    def showForIssueOnline() {
        render(view: '/arms/instrument/showForIssueOnline', model: [modelJson: null])
    }
    // download task for issue online
    def downloadTaskReportForIssueOnline() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadTaskReportForIssueOnlineActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForIssueOnlineActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadTaskReportForIssueOnlineActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForIssueOnlineActionService.buildFailureResultForUI(executeResult)
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
     * Show forward online page
     */
    def showForForwardOnline() {
        render(view: '/arms/instrument/showForForwardOnline', model: [modelJson: null])
    }
    // download task for forward online
    def downloadTaskReportForForwardOnline() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadTaskReportForForwardOnlineActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForForwardOnlineActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadTaskReportForForwardOnlineActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForForwardOnlineActionService.buildFailureResultForUI(executeResult)
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
     * Show forward cash collection page
     */
    def showForForwardCashCollection() {
        render(view: '/arms/instrument/showForForwardCashCollection', model: [modelJson: null])
    }
    // download task for forward cash collection
    def downloadTaskReportForForwardCashCollection() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadTaskReportForForwardCashCollectionActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForForwardCashCollectionActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadTaskReportForForwardCashCollectionActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForForwardCashCollectionActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }


    def showForInstrumentPurchase() {
        render(view: '/arms/instrument/showForInstrumentPurchase', model: [modelJson: null])
    }

    def downloadTaskReportForPurchaseInstrument() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadTaskReportForPurchaseInstrumentActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForPurchaseInstrumentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadTaskReportForPurchaseInstrumentActionService.execute(null, preResult)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadTaskReportForPurchaseInstrumentActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }
    def reloadInstrumentDropDown() {
        render rms.dropDownInstrument(params)
    }

    def reloadBankListFilteredDropDown() {
        render rms.dropDownBankListFiltered(params)
    }
}
