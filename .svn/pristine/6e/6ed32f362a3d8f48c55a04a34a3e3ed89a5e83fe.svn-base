package com.athena.mis.application.controller

import com.athena.mis.application.actions.contentcategory.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ContentCategoryController {

    static allowedMethods = [list: "POST", select: "POST", show: "POST", update: "POST", delete: "POST",
            listContentCategoryByContentTypeId: "POST"
    ]

    CreateContentCategoryActionService createContentCategoryActionService
    DeleteContentCategoryActionService deleteContentCategoryActionService
    ListContentCategoryActionService listContentCategoryActionService
    SelectContentCategoryActionService selectContentCategoryActionService
    ShowContentCategoryActionService showContentCategoryActionService
    UpdateContentCategoryActionService updateContentCategoryActionService
    SearchContentCategoryActionService searchContentCategoryActionService
    GetContentCategoryListByContentTypeIdActionService getContentCategoryListByContentTypeIdActionService

    /**
     * show ContentCategory list
     */
    def show() {
        Map result

        Map preResult = (Map) showContentCategoryActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showContentCategoryActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showContentCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showContentCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/contentCategory/show', model: [output: result as JSON])
    }

    /**
     * create ContentCategory
     */
    def create() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) createContentCategoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createContentCategoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) createContentCategoryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createContentCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createContentCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * select ContentCategory
     */
    def select() {
        Map result
        String output

        Map preResult = (Map) selectContentCategoryActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) selectContentCategoryActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectContentCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectContentCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * update ContentCategory
     */
    def update() {
        Map preResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateContentCategoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateContentCategoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) updateContentCategoryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateContentCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateContentCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * list and search ContentCategory
     */
    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        Boolean isError

        if (params.query) {
            preResult = (Map) searchContentCategoryActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) searchContentCategoryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchContentCategoryActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchContentCategoryActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            preResult = (Map) listContentCategoryActionService.executePreCondition(null, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }
            executeResult = (Map) listContentCategoryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listContentCategoryActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listContentCategoryActionService.buildSuccessResultForUI(executeResult)
            }
        }
        String output = result as JSON
        render output
    }

    /**
     * delete ContentCategory
     */
    def delete() {
        Map result
        Boolean isError
        String output

        Map preResult = (Map) deleteContentCategoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteContentCategoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (Map) deleteContentCategoryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteContentCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteContentCategoryActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * get list of ContentCategory by contentTypeId
     */
    def listContentCategoryByContentTypeId() {
        String output

        Map executeResult = (Map) getContentCategoryListByContentTypeIdActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            Map result = (Map) getContentCategoryListByContentTypeIdActionService.buildSuccessResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        output = executeResult as JSON
        render output
    }
}
