package com.athena.mis.projecttrack.controller

import com.athena.mis.projecttrack.actions.report.*
import com.athena.mis.projecttrack.actions.report.backlog.DownloadPtBacklogDetailsReportActionService
import com.athena.mis.projecttrack.actions.report.backlog.SearchPtBacklogDetailsActionService
import com.athena.mis.projecttrack.actions.report.backlog.ShowPtBacklogDetailsActionService
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class PtReportController {
    static allowedMethods = [
            showReportSprint: "POST",
            listSprintDetails: "POST",
            showReportOpenBacklog: "POST",
            listReportOpenBacklog: "POST",
            showReportBug: "POST",
            listBugDetails: "POST",
            showForBacklogDetails: "POST",
            searchForBacklogDetails: "POST"
    ]

    PtDownloadSprintDetailsActionService ptDownloadSprintDetailsActionService
    PtListSprintDetailsReportActionService ptListSprintDetailsReportActionService
    PtShowSprintDetailsReportActionService ptShowSprintDetailsReportActionService
    DownloadOpenBacklogReportActionService downloadOpenBacklogReportActionService
    ListOpenBacklogReportActionService listOpenBacklogReportActionService
    ShowOpenBacklogReportActionService showOpenBacklogReportActionService
    DownloadPtBugDetailsReportActionService downloadPtBugDetailsReportActionService
    ShowPtBugDetailsReportActionService showPtBugDetailsReportActionService
    ListPtBugDetailsReportActionService listPtBugDetailsReportActionService
    ShowPtBacklogDetailsActionService showPtBacklogDetailsActionService
    DownloadPtBacklogDetailsReportActionService downloadPtBacklogDetailsReportActionService
    SearchPtBacklogDetailsActionService searchPtBacklogDetailsActionService
    DownloadPtBugDetailsActionService downloadPtBugDetailsActionService

    // download sprint report
    def downloadSprintDetails() {

        Map preResult = (Map) ptDownloadSprintDetailsActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }
        Map executeResult = (Map) ptDownloadSprintDetailsActionService.execute(null, preResult)
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

    // shows sprint details in navigation
    def showReportSprint() {
        Map result = (Map) ptShowSprintDetailsReportActionService.execute(params, null)
        String output = result as JSON
        render(view: '/projectTrack/ptReport/sprint/show', model: [output: output])
    }

    // shows sprint details when user gives input
    def listSprintDetails() {
        Map result
        Map executeResult
        Map preResult = (Map) ptListSprintDetailsReportActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptListSprintDetailsReportActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }
        executeResult = (Map) ptListSprintDetailsReportActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) ptListSprintDetailsReportActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) ptListSprintDetailsReportActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    // download open backlog report
    def downloadOpenBacklogReport() {
        LinkedHashMap result
        String output
        Map preResult = (Map) downloadOpenBacklogReportActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) downloadOpenBacklogReportActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadOpenBacklogReportActionService.execute(null, params)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (LinkedHashMap) downloadOpenBacklogReportActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        Map reportResult = (Map) executeResult.report

        response.contentType = ConfigurationHolder.config.grails.mime.types[reportResult.format]
        response.setHeader("Content-disposition", "attachment;filename=${reportResult.reportFileName}")
        response.outputStream << reportResult.report.toByteArray()
    }

    // shows open backlog list
    def showReportOpenBacklog() {
        Map result

        Map executeResult = (Map) showOpenBacklogReportActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showOpenBacklogReportActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showOpenBacklogReportActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render(view: '/projectTrack/ptReport/openBacklog/show', model: [output: output])
    }

    // shows open backlog list
    def listReportOpenBacklog() {
        Map result
        Boolean isError
        String output

        Map executeResult = (Map) listOpenBacklogReportActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listOpenBacklogReportActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listOpenBacklogReportActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    // download bug report
    def downloadBugDetails() {

        Map preResult = (Map) downloadPtBugDetailsReportActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) downloadPtBugDetailsReportActionService.execute(params, preResult)
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

    // download individual bug report
    def downloadPtBugDetails() {
        Map preResult = (Map) downloadPtBugDetailsActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return
        }

        Map executeResult = (Map) downloadPtBugDetailsActionService.execute(params, preResult)
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

    // shows bug details in navigation
    def showReportBug() {
        Map executeResult = (Map) showPtBugDetailsReportActionService.execute(params, null)
        Map result
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showPtBugDetailsReportActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showPtBugDetailsReportActionService.buildSuccessResultForUI(executeResult)
        }

        render(view: '/projectTrack/ptReport/bug/show', model: [output: result as JSON])
    }

    // shows bug details when user gives input
    def listBugDetails() {
        Map result
        Map executeResult
        Map preResult = (Map) listPtBugDetailsReportActionService.executePreCondition(params, null)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listPtBugDetailsReportActionService.buildFailureResultForUI(preResult)
            String output = result as JSON
            render output
            return
        }
        executeResult = (Map) listPtBugDetailsReportActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listPtBugDetailsReportActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listPtBugDetailsReportActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * show backlog details
     */
    def showForBacklogDetails() {
        Map result = (Map) showPtBacklogDetailsActionService.execute(params, null)

        String leftMenu = ''
        if(params.leftMenu){
            leftMenu = params.leftMenu
        }else{
            leftMenu = 'ptReport/showForBacklogDetails'  // while clicking on 'Search Task' from Left Menu
        }

        render(view: '/projectTrack/ptReport/backlog/showForSearchTask', model: [result: result, leftMenu: leftMenu])
    }

    /**
     * search backlog details
     */
    def searchForBacklogDetails() {
        Map result

        Map preResult = (Map) searchPtBacklogDetailsActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) searchPtBacklogDetailsActionService.buildFailureResultForUI(preResult)
            render(template: '/projectTrack/ptReport/backlog/tmpBacklogDetails', model: [result: result])
            return
        }
        result = (Map) searchPtBacklogDetailsActionService.execute(params, null)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            result = (Map) searchPtBacklogDetailsActionService.buildFailureResultForUI(result)
        }
        String leftMenu = params.leftMenu
        render(template: '/projectTrack/ptReport/backlog/tmpBacklogDetails', model: [result: result, leftMenu: leftMenu])
    }

    /**
     * download backlog details in pdf format
     */
    def downloadBacklogDetailsReport() {
        Map result
        String output

        Map preResult = (Map) downloadPtBacklogDetailsReportActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) downloadPtBacklogDetailsReportActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) downloadPtBacklogDetailsReportActionService.execute(params, null)
        Boolean executeIsError = (Boolean) executeResult.isError
        if (executeIsError.booleanValue()) {
            result = (Map) downloadPtBacklogDetailsReportActionService.buildFailureResultForUI(preResult)
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
