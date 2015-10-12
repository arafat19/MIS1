package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accgroup.*
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccSessionUtil
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AccGroupController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]

    CreateAccGroupActionService createAccGroupActionService
    DeleteAccGroupActionService deleteAccGroupActionService
    ListAccGroupActionService listAccGroupActionService
    SearchAccGroupActionService searchAccGroupActionService
    SelectAccGroupActionService selectAccGroupActionService
    ShowAccGroupActionService showAccGroupActionService
    UpdateAccGroupActionService updateAccGroupActionService

    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult = (Map) showAccGroupActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccGroupActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccGroupActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accGroup/show', model: [output: result as JSON])
    }

    def create() {
        Map result

        AccGroup accGroup = buildAccGroup(params)
        Map preResult = (Map) createAccGroupActionService.executePreCondition(params, accGroup)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) createAccGroupActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccGroupActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createAccGroupActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }

    def list() {
        Map result
        Boolean isError
        Map executeResult
        if (params.query) {
            executeResult = (Map) searchAccGroupActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchAccGroupActionService.buildFailureResultForUI(executeResult)
                render(result as JSON)
                return
            }
            result = (Map) searchAccGroupActionService.buildSuccessResultForUI(executeResult)

        } else {
            executeResult = (Map) listAccGroupActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listAccGroupActionService.buildFailureResultForUI(executeResult)
                render(result as JSON)
                return
            }
            result = (Map) listAccGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccGroupActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        boolean deleteResult = ((Boolean) deleteAccGroupActionService.execute(params, null))
        if (deleteResult.booleanValue()) {
            result = (Map) deleteAccGroupActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteAccGroupActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    def select() {
        Map result
        Map executeResult = (Map) selectAccGroupActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccGroupActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) selectAccGroupActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        Map result

        AccGroup accGroup = buildAccGroup(params)
        accGroup.id = Long.parseLong(params.id.toString())
        accGroup.version = Integer.parseInt(params.version.toString())

        Map preResult = (Map) updateAccGroupActionService.executePreCondition(null, accGroup)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccGroupActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) updateAccGroupActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccGroupActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccGroupActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    private AccGroup buildAccGroup(GrailsParameterMap params) {
        AccGroup accGroup = new AccGroup(params)
        accGroup.companyId = accSessionUtil.appSessionUtil.getCompanyId()
        return accGroup
    }
}
