package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accbankstatement.ImportAccBankStatementActionService
import com.athena.mis.accounting.actions.accbankstatement.ShowAccBankStatementActionService
import grails.converters.JSON

class AccBankStatementController {

    static allowedMethods = [uploadBankStatementFile: "POST", show: "POST"]

    ImportAccBankStatementActionService importAccBankStatementActionService
    ShowAccBankStatementActionService showAccBankStatementActionService

    /**
     * show bank list and if the bank statement object contains error(s) then the gird of bank statement will be shown
     * bank statement grid with errors will show error list in the UI when the row of the grid will be selected
     */
    def show() {
        Map result
        Map executeResult
        Boolean isError

        executeResult = (Map) showAccBankStatementActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showAccBankStatementActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showAccBankStatementActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/accounting/accBankStatement/show', model: [output: result as JSON])
    }

    /**
     * Upload bank statement CSV file
     */
    def uploadBankStatementFile() {
        Map result
        String output
        Map preResult = (Map) importAccBankStatementActionService.executePreCondition(params, request)

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) importAccBankStatementActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        Map executeResult = (Map) importAccBankStatementActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) importAccBankStatementActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (Map) importAccBankStatementActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
}
