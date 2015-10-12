package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accvoucher.*
import grails.converters.JSON

class AccVoucherController {

    static allowedMethods = [show: "POST", postVoucher: "POST", create: "POST", update: "POST",
            select: "POST", list: "POST", showPayCash: "POST", listPayCash: "POST", showPayBank: "POST", listPayBank: "POST",
            showReceiveCash: "POST", listReceiveCash: "POST", showReceiveBank: "POST", listReceiveBank: "POST", unPostedVoucher: "POST",
            deleteVoucher: "POST", listOfUnApprovedPayCash: "POST", listOfUnApprovedPayBank: "POST", listOfUnApprovedReceiveCash: "POST",
            listOfUnApprovedReceiveBank: "POST", listOfUnApprovedJournal: "POST", cancelVoucher: "POST"
    ]

    ShowAccVoucherActionService showAccVoucherActionService
    ShowAccVoucherPayCashActionService showAccVoucherPayCashActionService
    ShowAccVoucherPayBankActionService showAccVoucherPayBankActionService
    ShowAccVoucherReceiveCashActionService showAccVoucherReceiveCashActionService
    ShowAccVoucherReceiveBankActionService showAccVoucherReceiveBankActionService
    CreateAccVoucherActionService createAccVoucherActionService
    UpdateAccVoucherActionService updateAccVoucherActionService
    ListAccVoucherActionService listAccVoucherActionService
    SearchAccVoucherActionService searchAccVoucherActionService
    ListAccVoucherPayCashActionService listAccVoucherPayCashActionService
    SearchAccVoucherPayCashActionService searchAccVoucherPayCashActionService
    SelectAccVoucherActionService selectAccVoucherActionService
    PostAccVoucherActionService postAccVoucherActionService
    ListAccVoucherPayBankActionService listAccVoucherPayBankActionService
    SearchAccVoucherPayBankActionService searchAccVoucherPayBankActionService
    ListAccVoucherReceiveCashActionService listAccVoucherReceiveCashActionService
    SearchAccVoucherReceiveCashActionService searchAccVoucherReceiveCashActionService
    ListAccVoucherReceiveBankActionService listAccVoucherReceiveBankActionService
    SearchAccVoucherReceiveBankActionService searchAccVoucherReceiveBankActionService
    UnPostVoucherActionService unPostVoucherActionService
    DeleteAccVoucherActionService deleteAccVoucherActionService
    CancelAccVoucherActionService cancelAccVoucherActionService

    ListUnApprovedPayCashActionService listUnApprovedPayCashActionService
    ListUnApprovedPayBankActionService listUnApprovedPayBankActionService
    ListUnApprovedReceiveCashActionService listUnApprovedReceiveCashActionService
    ListUnApprovedReceiveBankActionService listUnApprovedReceiveBankActionService
    ListUnApprovedJournalActionService listUnApprovedJournalActionService
    /**
     * show voucher list
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showAccVoucherActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccVoucherActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccVoucherActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accVoucher/show', model: [output: result as JSON])
    }
    /**
     * show pay cash voucher list
     */
    def showPayCash() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showAccVoucherPayCashActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccVoucherPayCashActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccVoucherPayCashActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accVoucher/showPayCash', model: [output: result as JSON])
    }
    /**
     * show pay bank(cheque) voucher list
     */
    def showPayBank() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showAccVoucherPayBankActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccVoucherPayBankActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccVoucherPayBankActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accVoucher/showPayBank', model: [output: result as JSON])
    }
    /**
     * show receive cash voucher list
     */
    def showReceiveCash() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showAccVoucherReceiveCashActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccVoucherReceiveCashActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccVoucherReceiveCashActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accVoucher/showReceiveCash', model: [output: result as JSON])
    }
    /**
     * show receive bank(cheque) voucher list
     */
    def showReceiveBank() {
        Map result
        Map executeResult
        Boolean isError
        executeResult = (Map) showAccVoucherReceiveBankActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccVoucherReceiveBankActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccVoucherReceiveBankActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accVoucher/showReceiveBank', model: [output: result as JSON])
    }
    /**
     * create voucher
     */
    def create() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) createAccVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccVoucherActionService.buildSuccessResultForUI(preResult)
        } else {
            result = (Map) createAccVoucherActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) createAccVoucherActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createAccVoucherActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createAccVoucherActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * update voucher
     */
    def update() {
        Map result
        Boolean isError
        Map preResult = (Map) updateAccVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateAccVoucherActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) updateAccVoucherActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateAccVoucherActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateAccVoucherActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }
    /**
     * voucher list (if query available then its for search)
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchAccVoucherActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccVoucherActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchAccVoucherActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listAccVoucherActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccVoucherActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccVoucherActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
    /**
     * pay cash voucher list (if query available then its for search)
     */
    def listPayCash() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchAccVoucherPayCashActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccVoucherPayCashActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchAccVoucherPayCashActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listAccVoucherPayCashActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccVoucherPayCashActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccVoucherPayCashActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
    /**
     * pay bank(cheque) voucher list (if query available then its for search)
     */
    def listPayBank() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchAccVoucherPayBankActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccVoucherPayBankActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchAccVoucherPayBankActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listAccVoucherPayBankActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccVoucherPayBankActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccVoucherPayBankActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
    /**
     * receive cash voucher list (if query available then its for search)
     */
    def listReceiveCash() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchAccVoucherReceiveCashActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccVoucherReceiveCashActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchAccVoucherReceiveCashActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listAccVoucherReceiveCashActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccVoucherReceiveCashActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccVoucherReceiveCashActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
    /**
     * receive bank(cheque) voucher list (if query available then its for search)
     */
    def listReceiveBank() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchAccVoucherReceiveBankActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchAccVoucherReceiveBankActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }

            result = (LinkedHashMap) searchAccVoucherReceiveBankActionService.buildSuccessResultForUI(executeResult);

        } else { // normal listing

            executeResult = (LinkedHashMap) listAccVoucherReceiveBankActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listAccVoucherReceiveBankActionService.buildFailureResultForUI(executeResult);
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listAccVoucherReceiveBankActionService.buildSuccessResultForUI(executeResult);
        }

        output = result as JSON
        render output
    }
    /**
     * select voucher
     */
    def select() {
        Map preResult
        Map executeResult
        Map result
        Boolean isError
        preResult = (Map) selectAccVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectAccVoucherActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) selectAccVoucherActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectAccVoucherActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) selectAccVoucherActionService.buildFailureResultForUI(executeResult);
        }
        render result as JSON
    }
    /**
     * post voucher
     */
    def postVoucher() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError

        preResult = (Map) postAccVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) postAccVoucherActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) postAccVoucherActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) postAccVoucherActionService.buildFailureResultForUI(executeResult);
            render(result as JSON)
            return
        } else {
            result = (Map) postAccVoucherActionService.buildSuccessResultForUI(executeResult);
        }

        render(result as JSON)
    }
    /**
     * un-post posted voucher
     */
    def unPostedVoucher() {
        Map result
        Boolean isError

        Map preResult = (Map) unPostVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) unPostVoucherActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        Map executeResult = (Map) unPostVoucherActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) unPostVoucherActionService.buildFailureResultForUI(executeResult);
            render(result as JSON)
            return
        } else {
            result = (Map) unPostVoucherActionService.buildSuccessResultForUI(executeResult);
        }

        render(result as JSON)
    }
    /**
     * delete voucher
     */
    def deleteVoucher() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) deleteAccVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteAccVoucherActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) deleteAccVoucherActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteAccVoucherActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) deleteAccVoucherActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

     /* ------------- For showing on Accounting Dash Board ---------------- */


    /**
     * unapproved pay cash voucher list
     */
    def listOfUnApprovedPayCash() {
        Map result
        Map executeResult = (Map) listUnApprovedPayCashActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedPayCashActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedPayCashActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }
    /**
     * unapproved pay bank(cheque) voucher list
     */
    def listOfUnApprovedPayBank() {
        Map result
        Map executeResult = (Map) listUnApprovedPayBankActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedPayBankActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedPayBankActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }
    /**
     * unapproved receive cash voucher list
     */
    def listOfUnApprovedReceiveCash() {
        Map result
        Map executeResult = (Map) listUnApprovedReceiveCashActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedReceiveCashActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedReceiveCashActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }
    /**
     * unapproved receive bank(cheque) voucher list
     */
    def listOfUnApprovedReceiveBank() {
        Map result
        Map executeResult = (Map) listUnApprovedReceiveBankActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedReceiveBankActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedReceiveBankActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }
    /**
     * unapproved journal list
     */
    def listOfUnApprovedJournal() {
        Map result
        Map executeResult = (Map) listUnApprovedJournalActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listUnApprovedJournalActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) listUnApprovedJournalActionService.buildFailureResultForUI(executeResult);
        }
        String output = result as JSON
        render output
    }

    /* ------------- End for Accounting Dash Board ---------------- */

    // used to cancel Voucher and preserve it's information into AccCancelledVoucher
    def cancelVoucher() {
        Map result
        Boolean isError
        String output
        Map preResult = (Map) cancelAccVoucherActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) cancelAccVoucherActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) cancelAccVoucherActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) cancelAccVoucherActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) cancelAccVoucherActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }

}
