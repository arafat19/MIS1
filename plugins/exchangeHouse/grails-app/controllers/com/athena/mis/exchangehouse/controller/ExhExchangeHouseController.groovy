package com.athena.mis.exchangehouse.controller

import com.athena.mis.application.actions.bankbranch.ListExhDistributionPointActionService
import grails.converters.JSON

class ExhExchangeHouseController {

    def springSecurityService
    ListExhDistributionPointActionService listExhDistributionPointActionService
    def renderExchangeHouseMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        Map result
        Map executeResult = (Map) listExhDistributionPointActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listExhDistributionPointActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) listExhDistributionPointActionService.buildSuccessResultForUI(executeResult);
        }
        String output = result as JSON
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'exchangeHouse', template: '/exchangehouse/leftmenuExchangeHouse')])
                element([name: 'dashBoard', content: g.render(plugin: 'exchangeHouse', template: '/exchangehouse/dashBoardExchangeHouse', model: [output: output])])
            }
        }
    }
}
