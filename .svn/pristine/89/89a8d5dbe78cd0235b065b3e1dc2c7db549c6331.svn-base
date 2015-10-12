package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accvouchertypecoa.*
import com.athena.mis.accounting.entity.AccVoucherTypeCoa
import com.athena.mis.accounting.utility.AccSessionUtil
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AccVoucherTypeCoaController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]

    UpdateAccVoucherTypeCoaActionService updateAccVoucherTypeCoaActionService
    ShowAccVoucherTypeCoaActionService showAccVoucherTypeCoaActionService
    DeleteAccVoucherTypeCoaActionService deleteAccVoucherTypeCoaActionService
    SelectAccVoucherTypeCoaActionService selectAccVoucherTypeCoaActionService
    CreateAccVoucherTypeCoaActionService createAccVoucherTypeCoaActionService
    SearchAccVoucherTypeCoaActionService searchAccVoucherTypeCoaActionService
    ListAccVoucherTypeCoaActionService listAccVoucherTypeCoaActionService
    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult = (Map) showAccVoucherTypeCoaActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accVoucherTypeCoa/show', model: [output: result as JSON])
    }

    def select() {
        Map result
        Map preResult = (Map) selectAccVoucherTypeCoaActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccVoucherTypeCoaActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }
        Map executeResult = (LinkedHashMap) selectAccVoucherTypeCoaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
            render result as JSON
            return
        }
        result = (Map) selectAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult)
        render result as JSON
    }

    def create() {
        Map result
        AccVoucherTypeCoa voucherTypeCoa = buildAccVoucherTypeCoa(params)
        Map preResult = (Map) createAccVoucherTypeCoaActionService.executePreCondition(params, voucherTypeCoa)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccVoucherTypeCoaActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }
        Map executeResult = (Map) createAccVoucherTypeCoaActionService.execute(null, voucherTypeCoa)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
            render result as JSON
            return
        }
        result = (Map) createAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult.accVoucherTypeCoa)
        render(result as JSON)
    }

    def update() {
        Map result
        Map preResult = (Map) updateAccVoucherTypeCoaActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccVoucherTypeCoaActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }

        Map executeResult = (LinkedHashMap) updateAccVoucherTypeCoaActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (LinkedHashMap) updateAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccVoucherTypeCoaActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccVoucherTypeCoaActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) deleteAccVoucherTypeCoaActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        result = (Map) deleteAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult)
        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchAccVoucherTypeCoaActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listAccVoucherTypeCoaActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listAccVoucherTypeCoaActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listAccVoucherTypeCoaActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    private AccVoucherTypeCoa buildAccVoucherTypeCoa(GrailsParameterMap params) {
        AccVoucherTypeCoa accVoucherTypeCoa = new AccVoucherTypeCoa(params)
        accVoucherTypeCoa.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        accVoucherTypeCoa.createdOn = new Date()
        accVoucherTypeCoa.companyId = accSessionUtil.appSessionUtil.getCompanyId()
        return accVoucherTypeCoa
    }
}
