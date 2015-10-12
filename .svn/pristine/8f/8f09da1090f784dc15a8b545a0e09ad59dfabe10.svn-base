package com.athena.mis.exchangehouse.controller

import com.athena.mis.exchangehouse.actions.beneficiary.ExhGetSanctionCountActionService
import com.athena.mis.exchangehouse.actions.sanction.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class ExhSanctionController {

    static allowedMethods = [show: "POST", list: "POST", create: "POST",
            update: "POST", select: "POST", delete: "POST", listForCustomer: "POST",
            uploadSanctionFile: "POST", showSanctionUpload:"POST",
            sanctionCountFromBeneficiary:"POST", sanctionCountFromCustomer:"POST"
    ]


    ExhShowSanctionActionService exhShowSanctionActionService
    ExhListSanctionActionService exhListSanctionActionService
    ExhSearchSanctionActionService exhSearchSanctionActionService
    ExhShowSanctionFromBeneficiaryActionService exhShowSanctionFromBeneficiaryActionService
    ExhImportSanctionFileActionService exhImportSanctionFileActionService
    ExhGetSanctionCountActionService exhGetSanctionCountActionService
    ExhGetSanctionCountForCustomerActionService exhGetSanctionCountForCustomerActionService
    ExhShowSanctionFromCustomerActionService exhShowSanctionFromCustomerActionService
    ExhSearchSanctionForCustomerActionService exhSearchSanctionForCustomerActionService
    ExhListSanctionForCustomerActionService exhListSanctionForCustomerActionService



    def show() {
//        if (request.method == UIConstants.REQUEST_METHOD_GET) {
//            render(view: UIConstants.NON_AJAX_TEMPLATE_URI, model: [page: g.createLink(action: 'show')])
//            return;
//        }
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        Boolean hasAccess
        String modelJson

        preResult = (LinkedHashMap) exhShowSanctionActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            return;
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhShowSanctionActionService.buildFailureResultForUI(preResult);
            modelJson = result as JSON
            render(view: '/exchangehouse/exhSanction/show', model: [modelJson: modelJson])
            return
        }

        executeResult = (LinkedHashMap) exhShowSanctionActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhShowSanctionActionService.buildFailureResultForUI(executeResult);
            modelJson = result as JSON
            render(view: '/exchangehouse/exhSanction/show', model: [modelJson: modelJson])
            return
        }

        result = (LinkedHashMap) exhShowSanctionActionService.buildSuccessResultForUI(executeResult);
        modelJson = result as JSON
        render(view: '/exchangehouse/exhSanction/show', model: [modelJson: modelJson])

    }

    def showFromBeneficiary() {
        if (request.method == UIConstants.REQUEST_METHOD_GET) {
            render(view: '/exchangehouse/exhSanction/noajaxtplSanction', model: [page: g.createLink(action: 'showFromBeneficiary'), fName: params.fName, mName: params.mName, lName: params.lName])
            return;
        }
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhShowSanctionFromBeneficiaryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            render(view: '/noAccess')
            return
        }

        executeResult = (LinkedHashMap) exhShowSanctionFromBeneficiaryActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            render(view: '/noAccess')
            return
        }

        result = (LinkedHashMap) exhShowSanctionFromBeneficiaryActionService.buildSuccessResultForUI(executeResult);
        String modelJson = result as JSON
        render(view: '/exchangehouse/exhSanction/show', model: [modelJson: modelJson])

    }

    def showFromCustomer() {
        if (request.method == UIConstants.REQUEST_METHOD_GET) {
            render(view: '/exchangehouse/exhSanction/noajaxtplSanction', model: [page: g.createLink(action: 'showFromCustomer'), customerName: params.customerName])
            return;
        }
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhShowSanctionFromCustomerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            render(view: '/noAccess')
            return
        }

        executeResult = (LinkedHashMap) exhShowSanctionFromCustomerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            render(view: '/noAccess')
            return
        }

        result = (LinkedHashMap) exhShowSanctionFromCustomerActionService.buildSuccessResultForUI(executeResult);
        String modelJson = result as JSON
        render(view: '/exchangehouse/exhSanction/showForCustomer', model: [modelJson: modelJson])
    }

    def list() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        Boolean hasAccess
        String output

        if (params.query) {    // Its a Search request
            preResult = (LinkedHashMap) exhSearchSanctionActionService.executePreCondition(params, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
                return;
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhSearchSanctionActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) exhSearchSanctionActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhSearchSanctionActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) exhSearchSanctionActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output
        } else {        // Normal List request

            preResult = (LinkedHashMap) exhListSanctionActionService.executePreCondition(params, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
                return;
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhListSanctionActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) exhListSanctionActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhListSanctionActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) exhListSanctionActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output
        }
    }

    def listForCustomer() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        Boolean hasAccess
        String output

        if (params.query) {    // Its a Search request
            preResult = (LinkedHashMap) exhSearchSanctionForCustomerActionService.executePreCondition(params, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
                return;
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhSearchSanctionForCustomerActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) exhSearchSanctionForCustomerActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhSearchSanctionActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) exhSearchSanctionForCustomerActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output
        } else {        // Normal List request

            preResult = (LinkedHashMap) exhListSanctionForCustomerActionService.executePreCondition(params, null)
            hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
                return;
            }
            isError = (Boolean) preResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhListSanctionForCustomerActionService.buildFailureResultForUI(preResult);
                output = result as JSON
                render output
                return;
            }

            executeResult = (LinkedHashMap) exhListSanctionForCustomerActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) exhListSanctionForCustomerActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) exhListSanctionForCustomerActionService.buildSuccessResultForUI(executeResult);
            output = result as JSON
            render output
        }
    }


    def showSanctionUpload() {
//        if (request.method == UIConstants.REQUEST_METHOD_GET) {
//            render(view: UIConstants.NON_AJAX_TEMPLATE_URI, model: [page: g.createLink(action: 'showSanctionUpload')])
//            return;
//        }
        render(view: '/exchangehouse/exhSanction/showUpload')
    }

    def uploadSanctionFile() {
        Map result = null
        String output
        Map preResult = (Map) exhImportSanctionFileActionService.executePreCondition(params, request);
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhImportSanctionFileActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) exhImportSanctionFileActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) exhImportSanctionFileActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) exhImportSanctionFileActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def sanctionCountFromBeneficiary() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhGetSanctionCountActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhGetSanctionCountActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) exhGetSanctionCountActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhGetSanctionCountActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) exhGetSanctionCountActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

    def sanctionCountFromCustomer() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) exhGetSanctionCountForCustomerActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhGetSanctionCountForCustomerActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) exhGetSanctionCountForCustomerActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) exhGetSanctionCountForCustomerActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) exhGetSanctionCountForCustomerActionService.buildSuccessResultForUI(executeResult);
        output = result as JSON
        render output
    }

}
