package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accvoucher.*
import grails.converters.JSON
import grails.util.Environment

class AccountingController {
    static allowedMethods = [renderAccountingMenu: "POST"]

    def springSecurityService

    ListUnApprovedPayCashActionService listUnApprovedPayCashActionService
    ListUnApprovedPayBankActionService listUnApprovedPayBankActionService
    ListUnApprovedReceiveCashActionService listUnApprovedReceiveCashActionService
    ListUnApprovedReceiveBankActionService listUnApprovedReceiveBankActionService
    ListUnApprovedJournalActionService listUnApprovedJournalActionService

    def renderAccountingMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        Map executeResult
        Boolean isError
        //----------- Pull data for Dash Board tab (List-UnApproved-Pay-Cash) -----------\\
        Map resultPayCash
        executeResult = (Map) listUnApprovedPayCashActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultPayCash = (Map) listUnApprovedPayCashActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultPayCash = (Map) listUnApprovedPayCashActionService.buildFailureResultForUI(executeResult);
        }
        String outputPayCash = resultPayCash as JSON

        //----------- Pull data for Dash Board tab (List-UnApproved-Pay-Bank) -----------\\
        Map resultPayBank
        executeResult = (Map) listUnApprovedPayBankActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultPayBank = (Map) listUnApprovedPayBankActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultPayBank = (Map) listUnApprovedPayBankActionService.buildFailureResultForUI(executeResult);
        }
        String outputPayBank = resultPayBank as JSON

        //----------- Pull data for Dash Board tab (List-UnApproved-Receive-Cash) -----------\\
        Map resultReceiveCash
        executeResult = (Map) listUnApprovedReceiveCashActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultReceiveCash = (Map) listUnApprovedReceiveCashActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultReceiveCash = (Map) listUnApprovedReceiveCashActionService.buildFailureResultForUI(executeResult);
        }
        String outputReceiveCash = resultReceiveCash as JSON

        //----------- Pull data for Dash Board tab (List-UnApproved-Receive-Bank) -----------\\
        Map resultReceiveBank
        executeResult = (Map) listUnApprovedReceiveBankActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultReceiveBank = (Map) listUnApprovedReceiveBankActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultReceiveBank = (Map) listUnApprovedReceiveBankActionService.buildFailureResultForUI(executeResult);
        }
        String outputReceiveBank = resultReceiveBank as JSON

        //----------- Pull data for Dash Board tab (List-UnApproved-Journal) -----------\\
        Map resultJournal
        executeResult = (Map) listUnApprovedJournalActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            resultJournal = (Map) listUnApprovedJournalActionService.buildSuccessResultForUI(executeResult);
        } else {
            resultJournal = (Map) listUnApprovedJournalActionService.buildFailureResultForUI(executeResult);
        }
        String outputJournal = resultJournal as JSON

        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin:'accounting', template: '/accounting/leftmenuAccounting')])
                element([name: 'dashBoard', content: g.render(plugin:'accounting',template: '/accounting/dashBoardAccounting', model: [outputPayCash: outputPayCash, outputPayBank: outputPayBank, outputReceiveCash: outputReceiveCash, outputReceiveBank: outputReceiveBank, outputJournal: outputJournal])])
            }
        }
    }
}
