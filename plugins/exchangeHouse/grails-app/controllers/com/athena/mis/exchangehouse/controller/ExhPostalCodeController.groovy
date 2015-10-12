package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.postalCode.*
import grails.converters.JSON

class ExhPostalCodeController {

    static allowedMethods = [show: "POST", create: "POST", update: "POST", select: "POST", list: "POST", delete: "POST"];

    ExhShowPostalCodeActionService exhShowPostalCodeActionService
    ExhCreatePostalCodeActionService exhCreatePostalCodeActionService
    ExhSelectPostalCodeActionService exhSelectPostalCodeActionService
    ExhUpdatePostalCodeActionService exhUpdatePostalCodeActionService
    ExhDeletePostalCodeActionService exhDeletePostalCodeActionService
    ExhListPostalCodeActionService exhListPostalCodeActionService
    ExhSearchPostalCodeActionService exhSearchPostalCodeActionService

    def show(){
            Map result
            Map executeResult
            Boolean isError
            executeResult= (Map)exhShowPostalCodeActionService.execute(params,null)
            isError = (Boolean) executeResult.isError
            if(isError.booleanValue()){
                result=(Map)exhShowPostalCodeActionService.buildFailureResultForUI(executeResult)
            }
            else{
                result= (Map)exhShowPostalCodeActionService.buildSuccessResultForUI(executeResult)
            }
            render(view: '/exchangehouse/exhPostalCode/show', model: [output: result as JSON])
    }
    def create(){
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) exhCreatePostalCodeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhCreatePostalCodeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhCreatePostalCodeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhCreatePostalCodeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhCreatePostalCodeActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output


    }
    def select(){
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) exhSelectPostalCodeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSelectPostalCodeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) exhSelectPostalCodeActionService.buildSuccessResultForUI(executeResult)
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

        preResult = (Map) exhUpdatePostalCodeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdatePostalCodeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhUpdatePostalCodeActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdatePostalCodeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhUpdatePostalCodeActionService.buildSuccessResultForUI(executeResult)
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

        preResult = (Map) exhDeletePostalCodeActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhDeletePostalCodeActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhDeletePostalCodeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhDeletePostalCodeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhDeletePostalCodeActionService.buildSuccessResultForUI(executeResult)
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
            executeResult = (Map) exhSearchPostalCodeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhSearchPostalCodeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) exhSearchPostalCodeActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) exhListPostalCodeActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhListPostalCodeActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) exhListPostalCodeActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

}
