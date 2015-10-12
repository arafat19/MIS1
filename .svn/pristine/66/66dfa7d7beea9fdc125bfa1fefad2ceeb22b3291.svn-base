package com.athena.mis.exchangehouse.taglib

import com.athena.mis.exchangehouse.actions.taglib.CustomerSummaryForTaskTagLibActionService
import com.athena.mis.utility.Tools

class ExhCustomerSummaryTagLib {

    static namespace = "exh"

    CustomerSummaryForTaskTagLibActionService customerSummaryForTaskTagLibActionService

    /**
     * Render html to show following information of customer
     * 1. transaction total of last 3 months
     * 2. transaction total of last 6 months
     * 3. transaction total of last 12 months
     * 4. last note of customer
     * example: <exh:customerSummaryForTask customerId="${customerId}"/>
     * @attr customerId REQUIRED - exhCustomer.id
     */
    def customerSummaryForTask = { attrs ->
        if ((attrs.customerId.toString()).isEmpty()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String customerSummaryTemplate = (String) customerSummaryForTaskTagLibActionService.execute(attrs, null)
        out << customerSummaryTemplate
    }
}
