package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accchartofaccount.*
import com.athena.mis.utility.UIConstants
import grails.converters.JSON

class AccChartOfAccountController {

    static allowedMethods = [show: "POST", create: "POST", select: "POST", update: "POST",
            delete: "POST", list: "POST", listSourceByCoaCode: "POST", listForVoucher: "POST",
            listForVoucherByGroupId: "POST", getSourceCategoryByAccSource: "POST"
    ]

    ShowAccChartOfAccountActionService showAccChartOfAccountActionService
    CreateAccChartOfAccountActionService createAccChartOfAccountActionService
    ListAccChartOfAccountActionService listAccChartOfAccountActionService
    SearchAccChartOfAccountActionService searchAccChartOfAccountActionService
    DeleteAccChartOfAccountActionService deleteAccChartOfAccountActionService
    SelectAccChartOfAccountActionService selectAccChartOfAccountActionService
    UpdateAccChartOfAccountActionService updateAccChartOfAccountActionService
    ListChartOfAccountForVoucherActionService listChartOfAccountForVoucherActionService
    SearchChartOfAccountForVoucherActionService searchChartOfAccountForVoucherActionService
    GetSourceListByCoaCodeActionService getSourceListByCoaCodeActionService
    SearchChartOfAccBankCashGroupActionService searchChartOfAccBankCashGroupActionService
    ListChartOfAccBankCashGroupActionService listChartOfAccBankCashGroupActionService
    ListAccChartOfAccountByAccGroupIdActionService listAccChartOfAccountByAccGroupIdActionService
    GetSourceCategoryByAccSourceIdActionService getSourceCategoryByAccSourceIdActionService

    /**
     *
     * @return - list of chart of account
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showAccChartOfAccountActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccChartOfAccountActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showAccChartOfAccountActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/accounting/accChartOfAccount/show', model: [output: result as JSON])
    }

    /**
     *
     * @return - newly created chart of account object
     */
    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) createAccChartOfAccountActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createAccChartOfAccountActionService.buildFailureResultForUI(preResult);
            render(result as JSON)
            return
        }

        executeResult = (Map) createAccChartOfAccountActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccChartOfAccountActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createAccChartOfAccountActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    /**
     *
     * @return- list of chart of account
     */
    def list() {
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchAccChartOfAccountActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchAccChartOfAccountActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchAccChartOfAccountActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listAccChartOfAccountActionService.execute(params, null);
            if (executeResult) {
                result = (Map) listAccChartOfAccountActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listAccChartOfAccountActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    /**
     *
     * @return - chart of account object excluding deleted one
     */
    def delete() {
        Map result
        Boolean isError
        Map preResult
        Map executeResult

        preResult = (Map) deleteAccChartOfAccountActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccChartOfAccountActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        executeResult = (Map) deleteAccChartOfAccountActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccChartOfAccountActionService.buildFailureResultForUI(null);
        } else {
            result = (Map) deleteAccChartOfAccountActionService.buildSuccessResultForUI(null);
        }

        render(result as JSON)
    }

    /**
     *
     * @return - object of selected chart of account
     */
    def select() {
        Map executeResult
        Map result
        executeResult = (Map) selectAccChartOfAccountActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccChartOfAccountActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectAccChartOfAccountActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    /**
     *
     * @return- list of chart of account including updated one
     */
    def update() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) updateAccChartOfAccountActionService.executePreCondition(params, null)
        hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccChartOfAccountActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) updateAccChartOfAccountActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccChartOfAccountActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccChartOfAccountActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /**
     *
     * @return - list of COA for grid in voucher create page
     */
    def listForVoucher() {
        Map result;
        Map executeResult;
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchChartOfAccountForVoucherActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchChartOfAccountForVoucherActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchChartOfAccountForVoucherActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listChartOfAccountForVoucherActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listChartOfAccountForVoucherActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listChartOfAccountForVoucherActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    /**
     * used in voucher pages
     * @return- COA object as well as SubAcc List (if acc source type =Sub-Acc)
     */
    def listSourceByCoaCode() {
        Map result
        Map executeResult
        Map preResult = (Map) getSourceListByCoaCodeActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceListByCoaCodeActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (LinkedHashMap) getSourceListByCoaCodeActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) getSourceListByCoaCodeActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) getSourceListByCoaCodeActionService.buildFailureResultForUI(executeResult);
        }
        render(result as JSON)
    }

    /**
     * @return -list of voucher by bank cash group
     */
    def listForVoucherByBankCashGroup() {
        Map result;
        Map executeResult;
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchChartOfAccBankCashGroupActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchChartOfAccBankCashGroupActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchChartOfAccBankCashGroupActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listChartOfAccBankCashGroupActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listChartOfAccBankCashGroupActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listChartOfAccBankCashGroupActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }

    /**
     * @return - chart of account list by accGroupId
     */
    def listAccChartOfAccountByAccGroupId() {
        Map result
        Map executeResult
        Map preResult = (Map) listAccChartOfAccountByAccGroupIdActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) listAccChartOfAccountByAccGroupIdActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (LinkedHashMap) listAccChartOfAccountByAccGroupIdActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listAccChartOfAccountByAccGroupIdActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listAccChartOfAccountByAccGroupIdActionService.buildFailureResultForUI(executeResult);
        }
        render(result as JSON)
    }

    /**
     * @return  -source category list : used in COA pages
     */
    def getSourceCategoryByAccSource() {
        Map result
        Map executeResult
        Boolean isError

        Map preResult = (Map) getSourceCategoryByAccSourceIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceCategoryByAccSourceIdActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (LinkedHashMap) getSourceCategoryByAccSourceIdActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getSourceCategoryByAccSourceIdActionService.buildFailureResultForUI(executeResult)
            render(result as JSON)
            return
        }
        render(executeResult as JSON)
    }
}
