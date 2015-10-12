package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accfinancialyear.*
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.DateUtility
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AccFinancialYearController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST", delete: "POST", list: "POST", setCurrentFinancialYear: "POST"]

    CreateAccFinancialYearActionService createAccFinancialYearActionService
    ShowAccFinancialYearActionService showAccFinancialYearActionService
    DeleteAccFinancialYearActionService deleteAccFinancialYearActionService
    ListAccFinancialYearActionService listAccFinancialYearActionService
    SelectAccFinancialYearActionService selectAccFinancialYearActionService
    UpdateAccFinancialYearActionService updateAccFinancialYearActionService
    SetForCurrentFinancialYearActionService setForCurrentFinancialYearActionService
    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showAccFinancialYearActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccFinancialYearActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccFinancialYearActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accFinancialYear/show', model: [output: result as JSON])
    }

    def select() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        String output
        Boolean isError
        executeResult = (LinkedHashMap) selectAccFinancialYearActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAccFinancialYearActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAccFinancialYearActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    def create() {
        Map result
        AccFinancialYear accFinancialYear = buildAccFinancialYear(params);
        Map preResult = (Map) createAccFinancialYearActionService.executePreCondition(params, accFinancialYear)
        Boolean isError = (Boolean) preResult.isError;
        if (isError.booleanValue()) {
            result = (Map) createAccFinancialYearActionService.buildFailureResultForUI(preResult)
            render result as JSON
            return
        }
        Map executeResult = (Map) createAccFinancialYearActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError;
        if (isError.booleanValue()) {
            result = (Map) createAccFinancialYearActionService.buildFailureResultForUI(executeResult)
            render result as JSON
            return
        }
        result = (Map) createAccFinancialYearActionService.buildSuccessResultForUI(executeResult);
        render(result as JSON)
    }

    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        preResult = (Map) updateAccFinancialYearActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccFinancialYearActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateAccFinancialYearActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccFinancialYearActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAccFinancialYearActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map executeResult;
        Map preResult
        Boolean isError

        preResult = (Map) deleteAccFinancialYearActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccFinancialYearActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }
        executeResult = (Map) deleteAccFinancialYearActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAccFinancialYearActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) deleteAccFinancialYearActionService.buildFailureResultForUI(executeResult);
        }
        render(result as JSON)
    }

    def list() {
        Map preResult;
        Boolean hasAccess;
        Map result;
        Map executeResult;
        Boolean isError;
        executeResult = (Map) listAccFinancialYearActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listAccFinancialYearActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) listAccFinancialYearActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def setCurrentFinancialYear() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        preResult = (Map) setForCurrentFinancialYearActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) setForCurrentFinancialYearActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) setForCurrentFinancialYearActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) setForCurrentFinancialYearActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) setForCurrentFinancialYearActionService.buildSuccessResultForUI(params)    // send params to build grid
        }
        render(result as JSON)
    }

    private AccFinancialYear buildAccFinancialYear(GrailsParameterMap parameterMap) {
        AccFinancialYear accFinancialYear = new AccFinancialYear(params)
        accFinancialYear.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        accFinancialYear.createdOn = new Date()
        accFinancialYear.startDate = DateUtility.parseMaskedDate(parameterMap.startDate)
        accFinancialYear.endDate = DateUtility.parseMaskedDate(parameterMap.endDate)
        accFinancialYear.isCurrent = false
        accFinancialYear.companyId = accSessionUtil.appSessionUtil.getAppUser().companyId
        accFinancialYear.version = 0
        accFinancialYear.updatedBy = 0
        return accFinancialYear
    }


}
