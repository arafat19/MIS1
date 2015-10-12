package com.athena.mis.accounting.controller

import com.athena.mis.accounting.actions.accCancelledVoucher.ListAccCancelledVoucherActionService
import grails.converters.JSON

class AccCancelledVoucherController {

    static allowedMethods =[showCancelledVoucher: "POST", listCancelledVoucher: "POST"]

    ListAccCancelledVoucherActionService listAccCancelledVoucherActionService
    /**
     * show UI to view list of cancelled voucher
     */
    def showCancelledVoucher() {
        render(view: '/accounting/accCancelledVoucher/show')
    }

    /**
     * show list of cancelled voucher
     */
    def listCancelledVoucher() {
        Map result
        String output

        Map executeResult = (Map) listAccCancelledVoucherActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) listAccCancelledVoucherActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) listAccCancelledVoucherActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
