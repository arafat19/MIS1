package com.athena.mis.application.controller

import com.athena.mis.application.actions.employee.*
import grails.converters.JSON

class EmployeeController {

    static allowedMethods = [create: "POST", show: "POST", select: "POST", update: "POST", delete: "POST", list: "POST"];

    ShowEmployeeActionService showEmployeeActionService
    CreateEmployeeActionService createEmployeeActionService
    ListEmployeeActionService listEmployeeActionService
    SelectEmployeeActionService selectEmployeeActionService
    UpdateEmployeeActionService updateEmployeeActionService
    DeleteEmployeeActionService deleteEmployeeActionService
    SearchEmployeeActionService searchEmployeeActionService

    /**
     * show employee list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showEmployeeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showEmployeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showEmployeeActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/application/employee/show', model: [output: result as JSON])
    }

    /**
     * create employee
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createEmployeeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createEmployeeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createEmployeeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createEmployeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createEmployeeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * select employee
     */
    def select() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectEmployeeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectEmployeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectEmployeeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * update employee
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateEmployeeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateEmployeeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateEmployeeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateEmployeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateEmployeeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * delete employee
     */
    def delete() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteEmployeeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteEmployeeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteEmployeeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteEmployeeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteEmployeeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * list and search employee
     */
    def list() {
        Map result;
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchEmployeeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchEmployeeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchEmployeeActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listEmployeeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listEmployeeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listEmployeeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }
}
