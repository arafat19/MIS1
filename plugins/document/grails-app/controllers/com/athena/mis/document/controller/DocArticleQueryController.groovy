package com.athena.mis.document.controller

import com.athena.mis.document.actions.articlequery.CreateDocArticleQueryActionService
import com.athena.mis.document.actions.articlequery.DeleteDocArticleQueryActionService
import com.athena.mis.document.actions.articlequery.ListDocArticleQueryActionService
import com.athena.mis.document.actions.articlequery.SearchDocArticleQueryActionService
import com.athena.mis.document.actions.articlequery.SelectDocArticleQueryActionService
import com.athena.mis.document.actions.articlequery.UpdateDocArticleQueryActionService
import grails.converters.JSON

class DocArticleQueryController {
    static allowedMethods = [
            show: 'post', list: 'post',
            create: 'post', select: 'post',
            update: 'post', delete: 'post'
    ]

    ListDocArticleQueryActionService listDocArticleQueryActionService
    SearchDocArticleQueryActionService searchDocArticleQueryActionService
    CreateDocArticleQueryActionService createDocArticleQueryActionService
    SelectDocArticleQueryActionService selectDocArticleQueryActionService
    UpdateDocArticleQueryActionService updateDocArticleQueryActionService
    DeleteDocArticleQueryActionService deleteDocArticleQueryActionService

    def show() {
        render(view: '/document/docArticleQuery/show')
    }

    def list() {
        Map executeResult, result
        String output
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchDocArticleQueryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDocArticleQueryActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDocArticleQueryActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listDocArticleQueryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listDocArticleQueryActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listDocArticleQueryActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def create() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) createDocArticleQueryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDocArticleQueryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(output)
            return
        }

        executeResult = (Map) createDocArticleQueryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createDocArticleQueryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createDocArticleQueryActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def select() {
        Map executeResult, result
        Boolean isError
        String output

        executeResult = (Map) selectDocArticleQueryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectDocArticleQueryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectDocArticleQueryActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) updateDocArticleQueryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDocArticleQueryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateDocArticleQueryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateDocArticleQueryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateDocArticleQueryActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) deleteDocArticleQueryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteDocArticleQueryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteDocArticleQueryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteDocArticleQueryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteDocArticleQueryActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
