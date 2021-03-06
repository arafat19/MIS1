package com.athena.mis.document.controller

import com.athena.mis.document.actions.dbinstance.CreateDocDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.DeleteDocDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.DownloadCsvResultForDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.ListDocDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.ListResultForDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.SearchDocDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.SelectDocDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.ShowDocDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.ShowResultForDbInstanceActionService
import com.athena.mis.document.actions.dbinstance.UpdateDocDbInstanceActionService
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class DocDbInstanceController {
    static allowedMethods = [
            show: "POST",
            create: "POST",
            update: "POST",
            delete: "POST",
            list: "POST",
            select: "POST",
            showResult: "POST",
            listResult: "POST"
    ]

    ShowDocDbInstanceActionService showDocDbInstanceActionService
    CreateDocDbInstanceActionService createDocDbInstanceActionService
    UpdateDocDbInstanceActionService updateDocDbInstanceActionService
    DeleteDocDbInstanceActionService deleteDocDbInstanceActionService
    SelectDocDbInstanceActionService selectDocDbInstanceActionService
    ListDocDbInstanceActionService listDocDbInstanceActionService
    SearchDocDbInstanceActionService searchDocDbInstanceActionService
    ShowResultForDbInstanceActionService showResultForDbInstanceActionService
    ListResultForDbInstanceActionService listResultForDbInstanceActionService
    DownloadCsvResultForDbInstanceActionService downloadCsvResultForDbInstanceActionService

    /*
    * Show DB Instance List
    * */

    def show() {
        Map executeResult, result
        Boolean isError

        executeResult = (Map) showDocDbInstanceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showDocDbInstanceActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/document/docDbInstance/show', model: [output: result as JSON])
    }

    /*
    * Create A New DB Instance
    * */

    def create() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) createDocDbInstanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDocDbInstanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(output)
            return
        }

        executeResult = (Map) createDocDbInstanceActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createDocDbInstanceActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /*
    * Update a DB Instance
    * */

    def update() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) updateDocDbInstanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDocDbInstanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateDocDbInstanceActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateDocDbInstanceActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * Delete a DB Instance
    * */

    def delete() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) deleteDocDbInstanceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteDocDbInstanceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteDocDbInstanceActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteDocDbInstanceActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * List/Search DB Instance
    * */

    def list() {
        Map executeResult, result
        String output
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchDocDbInstanceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDocDbInstanceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listDocDbInstanceActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listDocDbInstanceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }
    /*
    * Select a DB Instance object from grid
    * */

    def select() {
        Map executeResult, result
        Boolean isError
        String output

        executeResult = (Map) selectDocDbInstanceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectDocDbInstanceActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectDocDbInstanceActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showResult() {
        Map result = (Map) showResultForDbInstanceActionService.execute(params, null)
        String output = result as JSON
        render(view: '/document/docDbInstance/showQueryResult', model: [modelJson: output])
    }

    def listResult() {
        List lstResult = (List) listResultForDbInstanceActionService.execute(params, null)
        render lstResult as JSON
    }

    def downloadResultCsv() {
        Map result = (Map) downloadCsvResultForDbInstanceActionService.execute(params, null)
        Boolean isError = result.isError
        if(isError.booleanValue()){
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        String fileName = result.fileName
        response.contentType = 'text/csv'
        response.setHeader("Content-disposition", "attachment; filename=${fileName}")
        response.outputStream << result.outputBytes.toByteArray()
    }

}
