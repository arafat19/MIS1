package com.athena.mis.document.controller

import com.athena.mis.document.actions.appuserdoccategory.*
import com.athena.mis.document.actions.appuserdocsubcategory.*
import grails.converters.JSON

class DocSubCategoryUserMappingController {
    static allowedMethods = [showForSubCategory: "post", createForSubCategory: "post",
            updateForSubCategory: "post", deleteForSubCategory: "post", listForSubCategory: "post", selectForSubCategory: "post"]

    ShowAppUserDocSubCategoryActionService showAppUserDocSubCategoryActionService
    CreateAppUserDocSubCategoryActionService createAppUserDocSubCategoryActionService
    SelectAppUserDocSubCategoryActionService selectAppUserDocSubCategoryActionService
    UpdateAppUserDocSubCategoryActionService updateAppUserDocSubCategoryActionService
    DeleteAppUserDocSubCategoryActionService deleteAppUserDocSubCategoryActionService
    ListAppUserDocSubCategoryActionService listAppUserDocSubCategoryActionService


    ///***************  For Sub Category *************/////////////////

    /*
    * Show AppUser DocSubCategory mapping list
    * */

    def showForSubCategory() {
        Map result, executeResult
        Boolean isError
        String output

        executeResult = (Map) showAppUserDocSubCategoryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showAppUserDocSubCategoryActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render(view: '/document/appUserDocSubCategoryMapping/show', model: [output: output])
            return;
        } else {
            result = (Map) showAppUserDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        Long subCategoryId = (Long) result.subCategoryId    // add separate non-json key for tagLib parameter
        render(view: '/document/appUserDocSubCategoryMapping/show', model: [subCategoryId: subCategoryId, output: output])
    }

    /*
    * Create AppUser DocSubCategory Mapping object
    * */

    def createForSubCategory() {
        Map preResult, executeResult, result
        Boolean isError

        preResult = (Map) createAppUserDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppUserDocSubCategoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createAppUserDocSubCategoryActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAppUserDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createAppUserDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /*
    * Select AppUser DocSubCategory mapping object
    * */

    def selectForSubCategory() {
        Map result
        Map executeResult = (Map) selectAppUserDocSubCategoryActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectAppUserDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) selectAppUserDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        render result as JSON
    }

    /*
    * Update AppUser DocSubCategory Mapping object
    * */

    def updateForSubCategory() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateAppUserDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppUserDocSubCategoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateAppUserDocSubCategoryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAppUserDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateAppUserDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /*
    * Delete AppUser DocSubCategory Mapped object
    * */

    def deleteForSubCategory() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteAppUserDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppUserDocSubCategoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteAppUserDocSubCategoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAppUserDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteAppUserDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /*
    * List of AppUser DocSubCategory mapping
    * */

    def listForSubCategory() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) listAppUserDocSubCategoryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAppUserDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listAppUserDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /*
    * Dropdown AppUser for SubCategory reload
    * */

    def dropDownAppUserForSubCategoryReload() {
        render doc.dropDownAppUserForSubCategory(params)
    }
}
