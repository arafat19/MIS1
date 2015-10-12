package com.athena.mis.sarb.controller

import com.athena.mis.sarb.actions.province.*
import grails.converters.JSON

class SarbProvinceController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", select: "POST", list: "POST", delete: "POST"];

    ShowSarbProvinceActionService showSarbProvinceActionService
    CreateSarbProvinceActionService createSarbProvinceActionService
    SelectSarbProvinceActionService selectSarbProvinceActionService
    UpdateSarbProvinceActionService updateSarbProvinceActionService
    DeleteSarbProvinceActionService deleteSarbProvinceActionService
    SearchSarbProvinceActionService searchSarbProvinceActionService
    ListSarbProvinceActionService listSarbProvinceActionService


    def show(){
        Map result
        Map executeResult
        Boolean isError
        executeResult= (Map)showSarbProvinceActionService.execute(params,null)
        isError = (Boolean) executeResult.isError
        if(isError.booleanValue()){
            result=(Map)showSarbProvinceActionService.buildFailureResultForUI(executeResult)
        }
        else{
            result= (Map)showSarbProvinceActionService.buildSuccessResultForUI(executeResult)
        }
        render(view: '/sarb/sarbProvince/show', model: [output: result as JSON])
    }
    def create(){
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) createSarbProvinceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSarbProvinceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) createSarbProvinceActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createSarbProvinceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createSarbProvinceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output


    }
    def select(){
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) selectSarbProvinceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectSarbProvinceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectSarbProvinceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output

    }
    def update(){
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateSarbProvinceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSarbProvinceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateSarbProvinceActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateSarbProvinceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateSarbProvinceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def delete(){

        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) deleteSarbProvinceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteSarbProvinceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteSarbProvinceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteSarbProvinceActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteSarbProvinceActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
    def list(){
        Map result;
        Map executeResult
        Boolean isError
        String output

        if (params.query) {
            executeResult = (Map) searchSarbProvinceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchSarbProvinceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchSarbProvinceActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listSarbProvinceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listSarbProvinceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listSarbProvinceActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }


}
