package com.athena.mis.qs.controler

import com.athena.mis.qs.actions.qsmeasurement.GetQsStatusForDashBoardActionService
import grails.converters.JSON
import grails.util.Environment

class QsController {

    GetQsStatusForDashBoardActionService getQsStatusForDashBoardActionService

    def springSecurityService

    // Used to render left menu and dash board of budget
    def renderQsMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        // Now pull data for first tab of Dash board
        Map resultQsStatus
        Map executeResult = (Map) getQsStatusForDashBoardActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultQsStatus = (Map) getQsStatusForDashBoardActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultQsStatus = (Map) getQsStatusForDashBoardActionService.buildFailureResultForUI(executeResult);
        }
        String outputQsStatus = resultQsStatus as JSON

        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'qs', template: '/qs/leftmenuQs')])
                element([name: 'dashBoard', content: g.render(plugin: 'qs',template: '/qs/dashBoardQs', model: [outputQsStatus: outputQsStatus])])
            }
        }
    }
}
