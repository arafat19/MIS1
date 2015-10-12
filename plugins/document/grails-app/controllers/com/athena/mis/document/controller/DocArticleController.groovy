package com.athena.mis.document.controller

import com.athena.mis.document.actions.article.CreateDocArticleActionService
import com.athena.mis.document.actions.article.DeleteDocArticleActionService
import com.athena.mis.document.actions.article.ListDocArticleActionService
import com.athena.mis.document.actions.article.MovedToTrashDocArticleActionService
import com.athena.mis.document.actions.article.RestoreFromTrashDocArticleActionService
import com.athena.mis.document.actions.article.SearchDocArticleActionService
import com.athena.mis.document.actions.article.SelectDocArticleActionService
import com.athena.mis.document.actions.article.TrashListDocArticleActionService
import com.athena.mis.document.actions.article.TrashSearchDocArticleActionService
import com.athena.mis.document.actions.article.UpdateDocArticleActionService
import grails.converters.JSON

class DocArticleController {
    static allowedMethods = [
            show: 'post', list: 'post',
            create: 'post', select: 'post',
            update: 'post', movedToTrash: 'post',
            showTrash: 'post', listTrash: 'post',
            restoreFromTrash: 'post', delete: 'post'
    ]

    ListDocArticleActionService listDocArticleActionService
    SearchDocArticleActionService searchDocArticleActionService
    CreateDocArticleActionService createDocArticleActionService
    SelectDocArticleActionService selectDocArticleActionService
    UpdateDocArticleActionService updateDocArticleActionService
    MovedToTrashDocArticleActionService movedToTrashDocArticleActionService

    TrashListDocArticleActionService trashListDocArticleActionService
    TrashSearchDocArticleActionService trashSearchDocArticleActionService
    RestoreFromTrashDocArticleActionService restoreFromTrashDocArticleActionService
    DeleteDocArticleActionService deleteDocArticleActionService


    def show() {
        Map output = [categoryId: params.categoryId ? params.categoryId : 0L, subCategoryId: params.subCategoryId ? params.subCategoryId : '""']
        render(view: '/document/docArticle/show', model: [output: output])
    }

    def list() {
        Map executeResult, result
        String output
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchDocArticleActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDocArticleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDocArticleActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listDocArticleActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listDocArticleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listDocArticleActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def create() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) createDocArticleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDocArticleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createDocArticleActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createDocArticleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createDocArticleActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def select() {
        Map executeResult, result
        Boolean isError
        String output

        executeResult = (Map) selectDocArticleActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectDocArticleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectDocArticleActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def update() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) updateDocArticleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDocArticleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateDocArticleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateDocArticleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateDocArticleActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def movedToTrash() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) movedToTrashDocArticleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) movedToTrashDocArticleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) movedToTrashDocArticleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) movedToTrashDocArticleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) movedToTrashDocArticleActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def showTrash() {
        render(view: '/document/docArticle/showTrash')
    }

    def listTrash() {
        Map executeResult, result
        String output
        Boolean isError
        if (params.query) {
            executeResult = (Map) trashSearchDocArticleActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) trashSearchDocArticleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) trashSearchDocArticleActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) trashListDocArticleActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) trashListDocArticleActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) trashListDocArticleActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    def restoreFromTrash() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) restoreFromTrashDocArticleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) restoreFromTrashDocArticleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) restoreFromTrashDocArticleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) restoreFromTrashDocArticleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) restoreFromTrashDocArticleActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def delete() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) deleteDocArticleActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteDocArticleActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteDocArticleActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteDocArticleActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteDocArticleActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

}
