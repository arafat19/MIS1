package com.athena.mis.application.controller

import com.athena.mis.application.actions.bankbranch.*
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.utility.Tools
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class BankBranchController {


    static allowedMethods = [create: "POST", update: "POST", edit: "POST", list: "POST", delete: "POST",
                            listDistributionPoint:"POST",
                            reloadBranchesDropDownByBankAndDistrict:"POST"
    ];

    ShowBankBranchActionService showBankBranchActionService
    CreateBankBranchActionService createBankBranchActionService
    UpdateBankBranchActionService updateBankBranchActionService
    DeleteBankBranchActionService deleteBankBranchActionService
    SelectBankBranchActionService selectBankBranchActionService
    ListBankBranchActionService listBankBranchActionService
    SearchBankBranchActionService searchBankBranchActionService
    ListExhDistributionPointActionService listExhDistributionPointActionService
    SearchDistributionPointActionService searchDistributionPointActionService



    def show() {
        Map executeResult = (Map) showBankBranchActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue()
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        executeResult = (Map) showBankBranchActionService.execute(params, null)
        long bankId=executeResult.bankId                // bankId required att of district taglib
        executeResult = (Map) showBankBranchActionService.buildSuccessResultForUI(executeResult)
        String modelJson = executeResult as JSON
        render(view: '/application/bankBranch/show', model: [modelJson: modelJson,bankId:bankId])
    }

    /**
     *
     * This function will return a list of all BankBranch via an Ajax call
     * in the form of JSON. A list can be normal list containing all the
     * objects in the back-end, or user can search based on a column,
     * in which case the function will run a case-insensitive LIKE query.
     *
     */
    def list() {

        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchBankBranchActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue()
            if (!hasAccess) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }
            executeResult = (Map) searchBankBranchActionService.execute(params, null);
            executeResult = (Map) searchBankBranchActionService.buildSuccessResultForUI(executeResult);
        } else {
            executeResult = (Map) listBankBranchActionService.executePreCondition(null, null)
            boolean hasAccess = ((Boolean) executeResult.hasAccess).booleanValue();
            if (!hasAccess) {
                redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
            }
            executeResult = (Map) listBankBranchActionService.execute(params, null);
            executeResult = (Map) listBankBranchActionService.buildSuccessResultForUI(executeResult);
        }

        render(executeResult as JSON);
    }


    def create() {
        Map result
        Boolean isError
        Map preResult = (Map) createBankBranchActionService.executePreCondition(params, null)
        boolean hasAccess = ((Boolean) preResult.hasAccess).booleanValue()
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        isError = (Boolean) preResult.get(Tools.IS_ERROR)
        if (isError.booleanValue()) {
            result = (Map) createBankBranchActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) createBankBranchActionService.execute(null, preResult)
        isError = (Boolean) executeResult.get(Tools.IS_ERROR)
        if (isError) {
            result = (Map) createBankBranchActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) createBankBranchActionService.buildSuccessResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     * Updates a BankBranch via Ajax call
     * Before committing, it tries to validate the BankBranch object, if
     * validation fails, it returns the error(s) back to the caller in the form
     * of JSON error object array.
     * If validation passes, it saves the entity and return the entity in the form
     * of JSON
     */
    def update() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        String output

        preResult = (Map) updateBankBranchActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBankBranchActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateBankBranchActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateBankBranchActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) updateBankBranchActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /**
     *
     * Retrieves a BankBranch via Ajax call against the id attribute (default primary key)
     * If it finds an object against the provided id, it returns the BankBranch in the form
     * of JSON, null otherwise.
     * CAUTION:
     * 	1. 	It returns the version property of the entity, so make sure that version is
     * 		not false in the domain class definition
     *
     */
    def edit() {

        Map result = (Map) selectBankBranchActionService.executePreCondition(null, null)
        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue();
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }
        BankBranch bankBranch = (BankBranch) selectBankBranchActionService.execute(params, null);
        if (bankBranch) {
            result = (Map) selectBankBranchActionService.buildSuccessResultForUI(bankBranch);
        } else {
            result = (Map) selectBankBranchActionService.buildFailureResultForUI(null);
        }
        String output = result as JSON
        render output
    }

    def delete() {

        Map result = (Map) deleteBankBranchActionService.executePreCondition(params, null);
        if (!result) {
            result = (Map) deleteBankBranchActionService.buildFailureResultForUI(null);
            render(result as JSON);
            return;
        }

        boolean hasAccess = ((Boolean) result.hasAccess).booleanValue();
        if (!hasAccess) {
            redirect(url: UIConstants.REDIRECT_NO_ACCESS_PAGE)
        }

        boolean isError = ((Boolean) result.isError).booleanValue();
        if (isError) {
            result = (Map) deleteBankBranchActionService.buildFailureResultForUI((String) result.message);
            render(result as JSON);
            return;
        }

        if (result.hasAssociation) {
            result = (Map) deleteBankBranchActionService.buildFailureResultForUI((String) result.hasAssociation);
            render(result as JSON);
            return;
        }

        boolean deleteResult = ((Boolean) deleteBankBranchActionService.execute(params, null)).booleanValue();
        if (!deleteResult) {
            result = (Map) deleteBankBranchActionService.buildFailureResultForUI(null);
        } else {
            result = (Map) deleteBankBranchActionService.buildSuccessResultForUI(null);
        }
        render(result as JSON)
    }

    /**
     * list & search bankBranch as distribution point
     */
    def listDistributionPoint() {
        Map result;
        Map executeResult
        String output
        Boolean isError
        if (params.query) {

            executeResult = (Map) searchDistributionPointActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDistributionPointActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDistributionPointActionService.buildSuccessResultForUI(executeResult)
            }

        } else {                            // normal listing
            executeResult = (Map) listExhDistributionPointActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listExhDistributionPointActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listExhDistributionPointActionService.buildSuccessResultForUI(executeResult)
            }

        }
        output = result as JSON
        render output
    }
    def reloadBranchesDropDownByBankAndDistrict(){
        render app.dropDownBranchesByBankAndDistrict(params)
    }
}
