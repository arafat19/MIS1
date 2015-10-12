package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.acctier3.*
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.utility.AccSessionUtil
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AccTier3Controller {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST", delete: "POST",
            list: "POST", getTier3ByAccTier2Id: "POST"]

    CreateAccTier3ActionService createAccTier3ActionService
    DeleteAccTier3ActionService deleteAccTier3ActionService
    ListAccTier3ActionService listAccTier3ActionService
    SearchAccTier3ActionService searchAccTier3ActionService
    SelectAccTier3ActionService selectAccTier3ActionService
    ShowAccTier3ActionService showAccTier3ActionService
    UpdateAccTier3ActionService updateAccTier3ActionService
    GetTier3ListByAccTier2IdActionService getTier3ListByAccTier2IdActionService

    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult = (Map) showAccTier3ActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccTier3ActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccTier3ActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accTier3/show', model: [output: result as JSON])
    }

    def create() {
        Map result
        AccTier3 accTier3 = buildAccTier3(params)
        Map preResult = (Map) createAccTier3ActionService.executePreCondition(params, accTier3)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccTier3ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) createAccTier3ActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccTier3ActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createAccTier3ActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchAccTier3ActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchAccTier3ActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchAccTier3ActionService.buildFailureResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listAccTier3ActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listAccTier3ActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listAccTier3ActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccTier3ActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccTier3ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) deleteAccTier3ActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAccTier3ActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteAccTier3ActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def select() {
        Map result
        Map preResult = (Map) selectAccTier3ActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccTier3ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) selectAccTier3ActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccTier3ActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) selectAccTier3ActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        Map result

        AccTier3 accTier3 = buildAccTier3(params)
        accTier3.id = Integer.parseInt(params.id.toString())
        accTier3.version = Integer.parseInt(params.version.toString())

        Map preResult = (Map) updateAccTier3ActionService.executePreCondition(null, accTier3)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccTier3ActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) updateAccTier3ActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccTier3ActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccTier3ActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    private AccTier3 buildAccTier3(GrailsParameterMap params) {
        AccTier3 accTier3 = new AccTier3(params)
        accTier3.companyId = accSessionUtil.appSessionUtil.getCompanyId()
        return accTier3
    }

    // populate Tier3 for Tier2 id
    def getTier3ByAccTier2Id() {
        LinkedHashMap result
        String output

        Map preResult = (LinkedHashMap) getTier3ListByAccTier2IdActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getTier3ListByAccTier2IdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (LinkedHashMap) getTier3ListByAccTier2IdActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getTier3ListByAccTier2IdActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        output = executeResult as JSON
        render output
    }
}
