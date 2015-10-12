package com.athena.mis.exchangehouse.controller

import com.athena.mis.application.entity.EntityContent
import com.athena.mis.exchangehouse.actions.customer.*
import grails.converters.JSON
import grails.gsp.PageRenderer
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ExhCustomerController {

    static allowedMethods = [
            show: "POST", showForAdmin: "POST", showForAgent: "POST", showCustomerUser: "POST",
            showEntityNote: "POST", showForCustomerByNameAndCode: "POST", activation: 'GET',
            create: "POST", createCustomerUser: "POST", createEntityNote: "POST",
            update: "POST", updateEntityNote: "POST", edit: "POST", editEntityNote: "POST",
            list: "POST", listForAgent: "POST", listEntityNote: "POST", signup: "POST",
            deleteEntityNote: "POST", searchCustomerUser: "POST", searchForCustomerByNameAndCode: "POST",
            blockExhCustomer: "POST", unblockExhCustomer: "POST", reloadCustomerDetails: "POST"
    ]

    private static final String IMAGE_NOT_AVAILABLE = "Image Not Available"
    private static final String REGISTRATION_SUCCESS_MSG = 'Registration Success! Check e-mail for activation.'

    ExhShowCustomerActionService exhShowCustomerActionService
    ExhShowRegistrationCustomerActionService exhShowRegistrationCustomerActionService
    ExhShowCustomerForAgentActionService exhShowCustomerForAgentActionService
    ExhCreateCustomerActionService exhCreateCustomerActionService
    ExhUpdateCustomerActionService exhUpdateCustomerActionService
    ExhSelectCustomerActionService exhSelectCustomerActionService
    ExhListCustomerActionService exhListCustomerActionService
    ExhListCustomerForAgentActionService exhListCustomerForAgentActionService
    ExhSearchCustomerActionService exhSearchCustomerActionService
    ExhSearchCustomerForAgentActionService exhSearchCustomerForAgentActionService
    ExhSearchForCustomerUserActionService exhSearchForCustomerUserActionService
    ExhCreateForCustomerUserActionService exhCreateForCustomerUserActionService
    ExhShowForCustomerUserActionService exhShowForCustomerUserActionService
    ExhShowCustomerForAdminActionService exhShowCustomerForAdminActionService
    ExhShowForCustomerByNameCodeActionService exhShowForCustomerByNameCodeActionService
    ExhSearchForCustomerByNameCodeActionService exhSearchForCustomerByNameCodeActionService
    ExhSignupForCustomerUserActionService exhSignupForCustomerUserActionService
    ExhActivateCustomerUserActionService exhActivateCustomerUserActionService
    DispatchImageForCustomerActionService dispatchImageForCustomerActionService
    BlockExhCustomerActionService blockExhCustomerActionService
    UnblockExhCustomerActionService unblockExhCustomerActionService

    // Customer Entity Note
    ShowExhCustomerNoteActionService showExhCustomerNoteActionService
    CreateExhCustomerNoteActionService createExhCustomerNoteActionService
    ListExhCustomerNoteActionService listExhCustomerNoteActionService
    EditExhCustomerNoteActionService editExhCustomerNoteActionService
    UpdateExhCustomerNoteActionService updateExhCustomerNoteActionService
    DeleteExhCustomerNoteActionService deleteExhCustomerNoteActionService
    PageRenderer groovyPageRenderer

    /**
     * show customer list for cashier
     */
    def show() {
        Map executeResult = (Map) exhShowCustomerActionService.execute(params, null);
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhCustomer/show', model: [modelJson: modelJson])
    }

    /**
     * show customer list for admin
     */
    def showForAdmin() {
        Map executeResult = (Map) exhShowCustomerForAdminActionService.execute(params, null);
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhCustomer/showForAdmin', model: [modelJson: modelJson])
    }

    /**
     * show customer list for agent
     */
    def showForAgent() {
        Map executeResult = (Map) exhShowCustomerForAgentActionService.execute(params, null);
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhCustomer/showForAgent', model: [modelJson: modelJson])
    }

    /**
     * create customer
     */
    def create() {
        Map result
        Map preResult
        Map executeResult
        String output
        preResult = (Map) exhCreateCustomerActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhCreateCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhCreateCustomerActionService.execute(params, preResult)
        //again set params for SarbCustomer create
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhCreateCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhCreateCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * update customer
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) exhUpdateCustomerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) exhUpdateCustomerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhUpdateCustomerActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) exhUpdateCustomerActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

    /**
     * edit or select customer
     */
    def edit() {
        Map executeResult
        Map result
        Boolean isError
        String output

        executeResult = (Map) exhSelectCustomerActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSelectCustomerActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) exhSelectCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     * list and search customers
     */
    def list() {
        Map result;
        Map executeResult
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) exhSearchCustomerActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhSearchCustomerActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) exhSearchCustomerActionService.buildSuccessResultForUI(executeResult)
            }
        } else {                                                                                  // normal listing
            executeResult = (Map) exhListCustomerActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhListCustomerActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) exhListCustomerActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    /**
     * list and search customers for agent
     */
    def listForAgent() {
        Map result
        Map executeResult
        Boolean isError
        String output
        if (params.query) {
            executeResult = (Map) exhSearchCustomerForAgentActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhSearchCustomerForAgentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) exhSearchCustomerForAgentActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (Map) exhListCustomerForAgentActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhListCustomerForAgentActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) exhListCustomerForAgentActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }

    /**
     * show UI for customer user account
     */
    def showCustomerUser() {
        Map preResult
        Map result
        if (params.customerCode) {
            preResult = (Map) exhShowForCustomerUserActionService.executePreCondition(params, null)
            Boolean isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (Map) exhShowForCustomerUserActionService.buildFailureResultForUI(preResult)
            } else {
                result = (Map) exhShowForCustomerUserActionService.execute(null, preResult);
            }
            String output = result as JSON
            render(view: '/exchangehouse/customerUser/show', model: [modelJson: output])
            return
        } else {
            render(view: '/exchangehouse/customerUser/show', model: [modelJson: false])
        }
    }

    /**
     * show customer note
     **/
    def showEntityNote() {
        Boolean isError
        Map result
        Map preResult
        Map executeResult
        String output
        preResult = (Map) showExhCustomerNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showExhCustomerNoteActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(view: '/exchangehouse/exhCustomer/showNote', model: [modelJson: output])
            return
        }
        executeResult = (Map) showExhCustomerNoteActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) showExhCustomerNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) showExhCustomerNoteActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render(view: '/exchangehouse/exhCustomer/showNote', model: [modelJson: output])
    }

    /**
     * create customer note
     **/
    def createEntityNote() {
        Boolean isError
        Map preResult
        Map executeResult
        Map result
        preResult = (Map) createExhCustomerNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError

        if (isError.booleanValue()) {
            result = (Map) createExhCustomerNoteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createExhCustomerNoteActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) createExhCustomerNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createExhCustomerNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * update customer entity note
     **/
    def updateEntityNote() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) updateExhCustomerNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhCustomerNoteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) updateExhCustomerNoteActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateExhCustomerNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateExhCustomerNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * list customer entity note
     */
    def listEntityNote() {
        Map result;
        Map executeResult = (Map) listExhCustomerNoteActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) listExhCustomerNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) listExhCustomerNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Edit/Select customer entity note
     */
    def editEntityNote() {
        Map executeResult
        Map result
        Boolean isError

        executeResult = (Map) editExhCustomerNoteActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) editExhCustomerNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (LinkedHashMap) editExhCustomerNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     *  Delete customer entity note
     */
    def deleteEntityNote() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError

        preResult = (Map) deleteExhCustomerNoteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteExhCustomerNoteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteExhCustomerNoteActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteExhCustomerNoteActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) deleteExhCustomerNoteActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * show customer details
     */
    def searchCustomerUser() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output
        preResult = (LinkedHashMap) exhSearchForCustomerUserActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSearchForCustomerUserActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) exhSearchForCustomerUserActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhSearchForCustomerUserActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) exhSearchForCustomerUserActionService.buildSuccessResultForUI(executeResult);
        if (params.isCashier) {
            render result as JSON
            return
        }
        def html = groovyPageRenderer.render(template: '/exchangehouse/exhCustomer/tmplCustomerDetails', model: [customerInfoMap: result.customerInfoMap])
        render html
    }

    /**
     * Create customer user account for cashier
     */
    def createCustomerUser() {
        Map preResult
        Map executeResult
        Map postResult
        Map result
        Boolean isError
        String output
        preResult = (LinkedHashMap) exhCreateForCustomerUserActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhCreateForCustomerUserActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) exhCreateForCustomerUserActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhCreateForCustomerUserActionService.buildFailureResultForUI(executeResult);
            output = result as JSON
            render output
            return;
        }

        postResult = (LinkedHashMap) exhCreateForCustomerUserActionService.executePostCondition(params, executeResult);
        isError = (Boolean) postResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhCreateForCustomerUserActionService.buildFailureResultForUI(postResult);
            output = result as JSON
            render output
            return;
        }
        result = (LinkedHashMap) exhCreateForCustomerUserActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    /**
     * show UI filter panel for customer search
     */
    def showForCustomerByNameAndCode() {
        Map executeResult = (Map) exhShowForCustomerByNameCodeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            Map result = (Map) exhShowForCustomerByNameCodeActionService.buildFailureResultForUI(executeResult)
            render(view: '/exchangehouse/exhCustomer/showForCustomerSearch', model: [output: result as JSON])
            return
        }
        render(view: '/exchangehouse/exhCustomer/showForCustomerSearch', model: [output: executeResult as JSON])
    }

    /**
     * list of customer(s) through specific search
     */
    def searchForCustomerByNameAndCode() {
        Map executeResult = (Map) exhSearchForCustomerByNameCodeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        Map result
        if (isError.booleanValue()) {
            result = (Map) exhSearchForCustomerByNameCodeActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) exhSearchForCustomerByNameCodeActionService.buildSuccessResultForUI(executeResult)
        }
        String output = result as JSON
        render output
    }

    /**
     * show customer registration UI
     */
    def registration() {
        Map executeResult = (Map) exhShowRegistrationCustomerActionService.execute(null, request);
        String modelJson = executeResult as JSON
        render(view: '/exchangehouse/exhCustomer/showCustomerRegistration', model: [modelJson: modelJson])
    }

    /**
     * customer sign up
     */
    def signup() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap postResult
        Boolean isError
        // todo check code for session
        try {   // catching captcha exception
            preResult = (LinkedHashMap) exhSignupForCustomerUserActionService.executePreCondition(params, session)
        } catch (Exception e) {
            LinkedHashMap result = (LinkedHashMap) exhSignupForCustomerUserActionService.buildFailureResultForUI(preResult);
            render(view: '/exchangehouse/exhCustomer/showCustomerRegistration', model: [modelJson: (result as JSON)])
            return;
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            LinkedHashMap result = (LinkedHashMap) exhSignupForCustomerUserActionService.buildFailureResultForUI(preResult);
            render(view: '/exchangehouse/exhCustomer/showCustomerRegistration', model: [modelJson: (result as JSON)])
            return;
        }
        executeResult = (LinkedHashMap) exhSignupForCustomerUserActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            LinkedHashMap result = (LinkedHashMap) exhSignupForCustomerUserActionService.buildFailureResultForUI(executeResult);
            render(view: '/exchangehouse/exhCustomer/showCustomerRegistration', model: [modelJson: (result as JSON)])
            return;
        }
        postResult = (LinkedHashMap) exhSignupForCustomerUserActionService.executePostCondition(null, executeResult);
        isError = (Boolean) postResult.isError
        if (isError.booleanValue()) {
            LinkedHashMap result = (LinkedHashMap) exhSignupForCustomerUserActionService.buildFailureResultForUI(postResult);
            render(view: '/exchangehouse/exhCustomer/showCustomerRegistration', model: [modelJson: (result as JSON)])
            return;
        }
        flash.message = REGISTRATION_SUCCESS_MSG
        flash.success = true
        redirect(controller: 'login', action: 'auth')
    }

    /**
     * Customer activation
     */
    def activation = {
        Boolean isError
        LinkedHashMap preResult = (LinkedHashMap) exhActivateCustomerUserActionService.executePreCondition(params, request)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            flash.message = preResult.message
            flash.success = false
            redirect(controller: 'login', action: 'auth')
            return;
        }
        LinkedHashMap executeResult = (LinkedHashMap) exhActivateCustomerUserActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            flash.message = executeResult.message
            flash.success = false
            redirect(controller: 'login', action: 'auth')
            return;
        }
        flash.message = executeResult.message
        flash.success = true
        redirect(controller: 'login', action: 'auth')
    }

    /**
     * Unused
     */
    def displayPhotoIdImage() {
        EntityContent entityImage = (EntityContent) dispatchImageForCustomerActionService.execute(params, null)
        if (!entityImage) {
            render IMAGE_NOT_AVAILABLE
            return
        }
        response.contentType = ConfigurationHolder.config.grails.mime.types[entityImage.extension]
        response.contentLength = entityImage.content.size()
        ByteArrayInputStream fileStream = new ByteArrayInputStream(entityImage.content)
        response.outputStream << fileStream
        fileStream.close()
    }

    def blockExhCustomer() {
        Map result
        Map preResult
        Map executeResult
        String output
        preResult = (Map) blockExhCustomerActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) blockExhCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) blockExhCustomerActionService.execute(preResult, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) blockExhCustomerActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) blockExhCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def unblockExhCustomer() {
        Map result
        Map preResult
        Map executeResult
        String output
        preResult = (Map) unblockExhCustomerActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) unblockExhCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) unblockExhCustomerActionService.execute(preResult, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) unblockExhCustomerActionService.buildFailureResultForUI(null)
        } else {
            result = (Map) unblockExhCustomerActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    def reloadCustomerDetails() {
        render exh.customerDetails(params, null)
    }
}
