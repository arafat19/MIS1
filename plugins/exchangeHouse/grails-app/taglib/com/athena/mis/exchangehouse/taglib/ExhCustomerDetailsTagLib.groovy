package com.athena.mis.exchangehouse.taglib

import com.athena.mis.exchangehouse.actions.taglib.GetExhCustomerDetailsTagLibActionService

class ExhCustomerDetailsTagLib {

    static namespace = "exh"

    GetExhCustomerDetailsTagLibActionService getExhCustomerDetailsTagLibActionService

    /**
     * Return html of customer details
     * @attr id - id of html component
     * @attr property_name - property of exhCustomer
     * @attr property_value - value to search with
     * @attr from_date - start date
     * @attr to_date - end date
     * @attr url REQUIRED - url to reload Taglib
     */

    def customerDetails = { attrs, body ->

        String html = getExhCustomerDetailsTagLibActionService.execute(attrs, null)
        out << html
    }
}
