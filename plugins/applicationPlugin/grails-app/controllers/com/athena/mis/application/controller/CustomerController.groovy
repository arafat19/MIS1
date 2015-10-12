package com.athena.mis.application.controller

import com.athena.mis.application.actions.customer.*
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.DateUtility
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class CustomerController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"]

    @Autowired
    AppSessionUtil appSessionUtil

    CreateCustomerActionService createCustomerActionService
    DeleteCustomerActionService deleteCustomerActionService
    ListCustomerActionService listCustomerActionService
    SearchCustomerActionService searchCustomerActionService
    SelectCustomerActionService selectCustomerActionService
    ShowCustomerActionService showCustomerActionService
    UpdateCustomerActionService updateCustomerActionService

    def show() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showCustomerActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showCustomerActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showCustomerActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/application/customer/show', model: [output: result as JSON])
    }
    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        Customer customer = buildCustomer(params)
        preResult = (Map) createCustomerActionService.executePreCondition(params, customer)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createCustomerActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createCustomerActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createCustomerActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createCustomerActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }

    def list() {
        Map result
        Map executeResult
        Boolean isError

        if (params.query) {
            executeResult = (Map) searchCustomerActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchCustomerActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchCustomerActionService.buildFailureResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listCustomerActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listCustomerActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listCustomerActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    def delete() {
        Map result
        Map preResult = (Map) deleteCustomerActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteCustomerActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Boolean executeResult = (Boolean) deleteCustomerActionService.execute(params, null)
        if (executeResult.booleanValue()) {
            result = (Map) deleteCustomerActionService.buildSuccessResultForUI(null)
        } else {
            result = (Map) deleteCustomerActionService.buildFailureResultForUI(null)
        }
        render(result as JSON)
    }

    def select() {
        Map executeResult
        Map result
        executeResult = (Map) selectCustomerActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectCustomerActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectCustomerActionService.buildFailureResultForUI(executeResult)
        }
        render result as JSON
    }

    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        Customer customer = buildCustomer(params)
        customer.id = Long.parseLong(params.id.toString())
        customer.version = Integer.parseInt(params.version.toString())
        customer.updatedBy = appSessionUtil.getAppUser().id
        customer.updatedOn = new Date()
        preResult = (Map) updateCustomerActionService.executePreCondition(null, customer)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateCustomerActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateCustomerActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateCustomerActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateCustomerActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    private Customer buildCustomer(GrailsParameterMap params) {
        Customer customer = new Customer(params)
        customer.companyId = appSessionUtil.getAppUser().companyId
        customer.dateOfBirth = DateUtility.parseMaskedDate(params.dateOfBirth)
        return customer
    }
}
