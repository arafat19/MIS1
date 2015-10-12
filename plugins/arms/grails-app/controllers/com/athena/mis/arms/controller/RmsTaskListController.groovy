package com.athena.mis.arms.controller

import com.athena.mis.arms.actions.rmstasklist.*
import grails.converters.JSON

class RmsTaskListController {

    static allowedMethods = [
            show : "POST",
            create : "POST",
            showSearchTaskList : "POST",
            listSearchTaskList : "POST",
            showForManageTaskList: "POST",
            listForManageTaskList: "POST",
            reloadTaskListDropDown: "POST",
            removeFromList: "POST",
            renameTaskList:"POST",
            moveTaskToAnotherList:"POST"
    ]

    CreateRmsTaskListActionService createRmsTaskListActionService
    ListTaskListForSearchActionService listTaskListForSearchActionService
    SearchTaskListForSearchActionService searchTaskListForSearchActionService
    ListForManageTaskListActionService listForManageTaskListActionService
    RemoveTaskFromListActionService removeTaskFromListActionService
    RenameTaskListActionService renameTaskListActionService
    MoveTaskToAnotherListActionService moveTaskToAnotherListActionService

    /**
     * Show for create TaskList
     */
    def show() {
        render(view: '/arms/rmsTaskList/show', model: [modelJson: null])
    }

    /**
     * Create Task List object
     */
    def create() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createRmsTaskListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsTaskListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createRmsTaskListActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createRmsTaskListActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createRmsTaskListActionService.buildSuccessResultForUI(null)
        }
        output = result as JSON
        render output
    }

    /**
     * Show for search TaskList
     */
    def showSearchTaskList() {
        render(view: '/arms/rmsTaskList/showSearchTaskList', model: [modelJson: null])
    }

    /**
     * List TaskList
     */
    def listSearchTaskList() {
        Map result
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchTaskListForSearchActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchTaskListForSearchActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchTaskListForSearchActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listTaskListForSearchActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listTaskListForSearchActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listTaskListForSearchActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }
    def showForManageTaskList(){
        render(view: '/arms/rmsTaskList/showForManageTaskList', model: [model: null])
    }
    def listForManageTaskList(){
        Map result
        String output
        Boolean isError
        Map executeResult= (Map) listForManageTaskListActionService.execute(params,null)
        isError= (Boolean)executeResult.isError
        if(isError.booleanValue()){
            result= (Map)listForManageTaskListActionService.buildFailureResultForUI(null)
        }else{
            result= (Map)listForManageTaskListActionService.buildSuccessResultForUI(executeResult)
        }
        output= result as JSON
        render output

    }

    def reloadTaskListDropDown() {
        render rms.dropDownTaskList(params)
    }

    def removeFromList() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) removeTaskFromListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) removeTaskFromListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) removeTaskFromListActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) removeTaskFromListActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) removeTaskFromListActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def renameTaskList(){
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) renameTaskListActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) renameTaskListActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) renameTaskListActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) renameTaskListActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) renameTaskListActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def moveTaskToAnotherList(){

        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult=(Map)moveTaskToAnotherListActionService.executePreCondition(params,null)
        isError=(Boolean)preResult.isError
        if(isError.booleanValue()){
            result= (Map)moveTaskToAnotherListActionService.buildFailureResultForUI(preResult)
            output=result as JSON
            render output
            return
        }
        executeResult=(Map)moveTaskToAnotherListActionService.execute(preResult,null)
        isError=(Boolean)executeResult.isError
        if(isError.booleanValue()){
            result=(Map)moveTaskToAnotherListActionService.buildFailureResultForUI(executeResult)
        }else{
            result=(Map)moveTaskToAnotherListActionService.buildSuccessResultForUI(null)
        }
        output=result as JSON
        render output
    }
}
