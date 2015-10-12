package com.athena.mis.document.controller

import com.athena.mis.document.actions.invitedmembers.OutstandingInvitationsDocInvitedMemberActionService
import com.athena.mis.document.actions.invitedmembers.SearchOutstandingInvitationsDocInvitedActionService
import com.athena.mis.document.actions.invitedmembers.SendInvitationDocInvitedMembersActionService
import com.athena.mis.document.actions.invitedmembers.ShowResendInvitationDocInvitedMembersActionService
import grails.converters.JSON


class DocInvitedMembersController {

    static allowedMethods = [show                      : 'post', sendInvitation: 'post',
                             showOutStandingInvitations: 'post',
                             outStandingInvitations    : 'post']

    SendInvitationDocInvitedMembersActionService sendInvitationDocInvitedMembersActionService
    OutstandingInvitationsDocInvitedMemberActionService outstandingInvitationsDocInvitedMemberActionService
    SearchOutstandingInvitationsDocInvitedActionService searchOutstandingInvitationsDocInvitedActionService
    ShowResendInvitationDocInvitedMembersActionService showResendInvitationDocInvitedMembersActionService

    /*
    * Send Invitation Show Page
    * */

    def show() {
        Map output = [categoryId: 0L, lstDocSubCategory: '""']
        render(view: '/document/docInvitedMembers/show', model: [output: output])
    }
    /*
    * Send Invitation to email address(users)
    * */

    def sendInvitation() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) sendInvitationDocInvitedMembersActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) sendInvitationDocInvitedMembersActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(output)
            return
        }

        executeResult = (Map) sendInvitationDocInvitedMembersActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) sendInvitationDocInvitedMembersActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) sendInvitationDocInvitedMembersActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /*
    * Send Invitation Show Page
    * */

    def showResendInvitation() {
        Map executeResult = (Map) showResendInvitationDocInvitedMembersActionService.execute(params, null)
        render(view: '/document/docInvitedMembers/show', model: [output: executeResult])
    }

    /*
    * Show page Outstanding invitation
    * */

    def showOutStandingInvitations() {
        render(view: '/document/docOutstandingInvitations/show')
    }

    /*
    * Search Outstanding invitations
    * */

    def outStandingInvitations() {
        Map executeResult, result
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchOutstandingInvitationsDocInvitedActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchOutstandingInvitationsDocInvitedActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchOutstandingInvitationsDocInvitedActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) outstandingInvitationsDocInvitedMemberActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) outstandingInvitationsDocInvitedMemberActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) outstandingInvitationsDocInvitedMemberActionService.buildFailureResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }
}
