package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accsubaccount.*
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.utility.AccSessionUtil
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AccSubAccountController {
    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST",
            list: "POST", getListByCoaId: "POST"]

    UpdateAccSubAccountActionService updateAccSubAccountActionService
    ShowAccSubAccountActionService showAccSubAccountActionService
    DeleteAccSubAccountActionService deleteAccSubAccountActionService
    SelectAccSubAccountActionService selectAccSubAccountActionService
    CreateAccSubAccountActionService createAccSubAccountActionService
    SearchAccSubAccountActionService searchAccSubAccountActionService
    ListAccSubAccountActionService listAccSubAccountActionService
    GetAccSubAccountByCoaIdActionService getAccSubAccountByCoaIdActionService
    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult = (Map) showAccSubAccountActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccSubAccountActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccSubAccountActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accSubAccount/show', model: [output: result as JSON])
    }

    def select() {
        LinkedHashMap result
        Map preResult = (Map) selectAccSubAccountActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccSubAccountActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }

        Map executeResult = (LinkedHashMap) selectAccSubAccountActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccSubAccountActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccSubAccountActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def create() {
        Map result
        AccSubAccount accSubAccountInstance = buildAccSubAccount(params)
        Map preResult = (Map) createAccSubAccountActionService.executePreCondition(params, accSubAccountInstance)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccSubAccountActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }
        Map executeResult = (Map) createAccSubAccountActionService.execute(null, accSubAccountInstance)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccSubAccountActionService.buildFailureResultForUI(executeResult)
            render result as JSON
            return
        }
        result = (Map) createAccSubAccountActionService.buildSuccessResultForUI(executeResult.accSubAccount)
        render(result as JSON)
    }

    def update() {
        Map result
        AccSubAccount accSubAccountInstance = new AccSubAccount(params)
        accSubAccountInstance.id = Long.parseLong(params.id.toString())
        accSubAccountInstance.version = Integer.parseInt(params.version.toString())
        Map preResult = (Map) updateAccSubAccountActionService.executePreCondition(params, accSubAccountInstance)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccSubAccountActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }
        Map executeResult = (Map) updateAccSubAccountActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccSubAccountActionService.buildFailureResultForUI(accSubAccountInstance)
        } else {
            result = (Map) updateAccSubAccountActionService.buildSuccessResultForUI(accSubAccountInstance)
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map executeResult
        Map preResult
        Boolean isError

        preResult = (Map) deleteAccSubAccountActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccSubAccountActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteAccSubAccountActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAccSubAccountActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteAccSubAccountActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def list() {
        Map preResult
        Boolean hasAccess
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchAccSubAccountActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchAccSubAccountActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchAccSubAccountActionService.buildFailureResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listAccSubAccountActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listAccSubAccountActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listAccSubAccountActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    private AccSubAccount buildAccSubAccount(GrailsParameterMap params) {
        AccSubAccount accSubAccount = new AccSubAccount(params)
        accSubAccount.companyId = accSessionUtil.appSessionUtil.getAppUser().companyId
        accSubAccount.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        accSubAccount.createdOn = new Date()
        accSubAccount.updatedBy = 0
        return accSubAccount
    }

    def getListByCoaId() {
        Map result
        Map preResult = (Map) getAccSubAccountByCoaIdActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getAccSubAccountByCoaIdActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        result = (LinkedHashMap) getAccSubAccountByCoaIdActionService.execute(params, null)
        render(result as JSON)

    }


}
