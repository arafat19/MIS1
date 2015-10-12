package com.athena.mis.application.controller

import com.athena.mis.application.actions.itemtype.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ItemTypeController {
    static allowedMethods = [
            create: "POST", show: "POST", update: "POST",
            delete: "POST", list: "POST", select: "POST"
    ]

    ShowItemTypeActionService showItemTypeActionService
    CreateItemTypeActionService createItemTypeActionService
    UpdateItemTypeActionService updateItemTypeActionService
    ListItemTypeActionService listItemTypeActionService
    SearchItemTypeActionService searchItemTypeActionService
    SelectItemTypeActionService selectItemTypeActionService
    DeleteItemTypeActionService deleteItemTypeActionService
    /**
     * Show item type
     */
    def show() {
        Map result
        Boolean isError
        Map preResult = (Map) showItemTypeActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showItemTypeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showItemTypeActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showItemTypeActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/application/itemType/show', model: [output: result as JSON])
    }
    /**
     * Create item type
     */
    def create() {
        Map result
        Boolean isError

        Map preResult = (Map) createItemTypeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createItemTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) createItemTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createItemTypeActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createItemTypeActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }
    /**
     * Update item type
     */
    def update() {
        Map result
        Boolean isError

        Map preResult = (Map) updateItemTypeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateItemTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) updateItemTypeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateItemTypeActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateItemTypeActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * Delete item type
     */
    def delete() {
        Map result
        Map preResult = (Map) deleteItemTypeActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteItemTypeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        boolean deleteResult = ((Boolean) deleteItemTypeActionService.execute(params, null))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteItemTypeActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteItemTypeActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }
    /**
     * List & Search item type
     */
    def list() {
        Map preResult
        Boolean isError
        Map result
        Map executeResult
        if (params.query) {
            preResult = (Map) searchItemTypeActionService.executePreCondition(null, null)
            Boolean hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }

            executeResult = (Map) searchItemTypeActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchItemTypeActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchItemTypeActionService.buildFailureResultForUI(executeResult)
            }
        } else {
            preResult = (Map) listItemTypeActionService.executePreCondition(null, null)
            Boolean hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }

            executeResult = (Map) listItemTypeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listItemTypeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listItemTypeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
    /**
     * Select item type
     */
    def select() {
        Map executeResult = (Map) selectItemTypeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            Map result = (Map) selectItemTypeActionService.buildFailureResultForUI(executeResult)
            render result as JSON
        }
        render executeResult as JSON
    }

}
