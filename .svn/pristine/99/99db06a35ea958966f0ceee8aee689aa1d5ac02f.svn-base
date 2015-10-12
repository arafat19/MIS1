package com.athena.mis.document.controller

import com.athena.mis.document.actions.subcategory.ListSubCategoryFavouriteActionService
import grails.converters.JSON

class DocumentController {
    static allowedMethods = [renderDocumentMenu: "POST"]

    def springSecurityService
    ListSubCategoryFavouriteActionService listSubCategoryFavouriteActionService

    def renderDocumentMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        Map result
        Map executeResult = (Map) listSubCategoryFavouriteActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listSubCategoryFavouriteActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) listSubCategoryFavouriteActionService.buildSuccessResultForUI(executeResult);
        }
        String output = result as JSON
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin:'document', template: '/document/leftmenuDocument')])
                element([name: 'dashBoard', content: g.render(plugin: 'document', template: '/document/dashBoardDocument',  model: [output: output])])
            }
        }
    }
}
