package com.athena.mis.document.controller

import com.athena.mis.document.actions.memberjoinrequest.ApplyDocMemberJoinRequestActionService
import com.athena.mis.document.actions.memberjoinrequest.ApplyDocMemberJoinRequestForSubCategoryActionService
import com.athena.mis.document.actions.memberjoinrequest.ApprovedDocMemberJoinRequestActionService
import com.athena.mis.document.actions.memberjoinrequest.ListDocMemberJoinRequestActionService
import com.athena.mis.document.actions.memberjoinrequest.SearchDocMemberJoinRequestActionService
import grails.converters.JSON

class DocMemberJoinRequestController {
    static allowedMethods = [show: 'POST',
                             searchRequestedMembers: 'POST',
                             applyForMembership: 'POST',
                             approvedForMembership: 'POST',
                             applyForSubCategoryMembership:"POST"]

    SearchDocMemberJoinRequestActionService searchDocMemberJoinRequestActionService
    ApplyDocMemberJoinRequestActionService applyDocMemberJoinRequestActionService
    ApplyDocMemberJoinRequestForSubCategoryActionService applyDocMemberJoinRequestForSubCategoryActionService
    ApprovedDocMemberJoinRequestActionService approvedDocMemberJoinRequestActionService
    ListDocMemberJoinRequestActionService listDocMemberJoinRequestActionService

    /*
    * Show Page for join request
    * */

    def show() {
        render(view: '/document/docMemberJoinRequest/show')
    }
    /*
    * Search join request members
    * */

    def searchRequestedMembers() {
        Map executeResult, result
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) searchDocMemberJoinRequestActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDocMemberJoinRequestActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDocMemberJoinRequestActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listDocMemberJoinRequestActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listDocMemberJoinRequestActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) listDocMemberJoinRequestActionService.buildFailureResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }
    /*
    * Apply for joined as member and mapping with category
    * */

    def applyForMembership() {
        Map preResult, executeResult, result
        Boolean isError
        flash.message = null

        preResult = (Map) applyDocMemberJoinRequestActionService.executePreCondition(params, request)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) applyDocMemberJoinRequestActionService.buildFailureResultForUI(preResult)
            flash.success = false
            flash.message = result.message
            render(view: '/document/docCategory/showForGenericUser', model: [modelJson: result as JSON])
            return
        }

        executeResult = (Map) applyDocMemberJoinRequestActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) applyDocMemberJoinRequestActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) applyDocMemberJoinRequestActionService.buildFailureResultForUI(executeResult)
            flash.success = false
            flash.message = result.message
            render(view: '/document/docCategory/showForGenericUser', model: [modelJson: result as JSON])
        }
        flash.success = (!isError.booleanValue())
        flash.message = result.message
        render(view: '/document/docCategory/showForGenericUser', model: [modelJson: result as JSON])
    }

    /*
    * Apply for joined as member and mapping with sub category
    * */

    def applyForSubCategoryMembership() {
        Map preResult, executeResult, result
        Boolean isError
        flash.message = null

        preResult = (Map) applyDocMemberJoinRequestForSubCategoryActionService.executePreCondition(params, request)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) applyDocMemberJoinRequestForSubCategoryActionService.buildFailureResultForUI(preResult)
            flash.success = false
            flash.message = result.message
            render(view: '/document/docSubCategory/showForGenericUser', model: [modelJson: result as JSON])
            return
        }

        executeResult = (Map) applyDocMemberJoinRequestForSubCategoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) applyDocMemberJoinRequestForSubCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) applyDocMemberJoinRequestForSubCategoryActionService.buildFailureResultForUI(executeResult)
            flash.success = false
            flash.message = result.message
            render(view: '/document/docSubCategory/showForGenericUser', model: [modelJson: result as JSON])
        }
        flash.success = (!isError.booleanValue())
        flash.message = result.message
        render(view: '/document/docSubCategory/showForGenericUser', model: [modelJson: result as JSON])
    }
    /*
    * Approve membership as user and mapping with category
    * */

    def approvedForMembership() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) approvedDocMemberJoinRequestActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) approvedDocMemberJoinRequestActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) approvedDocMemberJoinRequestActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) approvedDocMemberJoinRequestActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) approvedDocMemberJoinRequestActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

}
