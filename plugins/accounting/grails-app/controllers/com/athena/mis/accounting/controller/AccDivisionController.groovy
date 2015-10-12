package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accdivision.*
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.utility.AccSessionUtil
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class AccDivisionController {
    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST",
            delete: "POST", list: "POST", getDivisionListByProjectId: "POST"]

    CreateAccDivisionActionService createAccDivisionActionService
    DeleteAccDivisionActionService deleteAccDivisionActionService
    ListAccDivisionActionService listAccDivisionActionService
    SearchAccDivisionActionService searchAccDivisionActionService
    SelectAccDivisionActionService selectAccDivisionActionService
    ShowAccDivisionActionService showAccDivisionActionService
    UpdateAccDivisionActionService updateAccDivisionActionService
    GetAccDivisionByProjectActionService getAccDivisionByProjectActionService

    @Autowired
    AccSessionUtil accSessionUtil

    def show() {
        Map result
        Map executeResult = (Map) showAccDivisionActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccDivisionActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccDivisionActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accDivision/show', model: [output: result as JSON])
    }

    def create() {
        Map result
        AccDivision accDivision = buildAccDivision(params)
        Map preResult = (Map) createAccDivisionActionService.executePreCondition(params, accDivision)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccDivisionActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) createAccDivisionActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccDivisionActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createAccDivisionActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchAccDivisionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchAccDivisionActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchAccDivisionActionService.buildFailureResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listAccDivisionActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listAccDivisionActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listAccDivisionActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteAccDivisionActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccDivisionActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) deleteAccDivisionActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAccDivisionActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteAccDivisionActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    def select() {
        Map result
        Map executeResult = (Map) selectAccDivisionActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectAccDivisionActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectAccDivisionActionService.buildFailureResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        Map result
        AccDivision accDivision = buildAccDivision(params)
        accDivision.id = Long.parseLong(params.id.toString())
        accDivision.version = Integer.parseInt(params.version.toString())
        accDivision.updatedOn = new Date()
        accDivision.updatedBy = accSessionUtil.appSessionUtil.getCompanyId()

        Map preResult = (Map) updateAccDivisionActionService.executePreCondition(null, accDivision)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccDivisionActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) updateAccDivisionActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccDivisionActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccDivisionActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    private AccDivision buildAccDivision(GrailsParameterMap params) {
        AccDivision accDivision = new AccDivision(params)
        accDivision.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        accDivision.createdOn = new Date()
        accDivision.companyId = accSessionUtil.appSessionUtil.getCompanyId()
        return accDivision
    }

    /* get active accDivision list by a specific project
         Used in : AccVoucher CRUD show page(when project select from drop-down)
     */
    def getDivisionListByProjectId() {
        LinkedHashMap result
        String output
        Map preResult = (LinkedHashMap) getAccDivisionByProjectActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getAccDivisionByProjectActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = (LinkedHashMap) getAccDivisionByProjectActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getAccDivisionByProjectActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }
        output = executeResult as JSON
        render output
    }
}

