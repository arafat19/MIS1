package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetLocalCurrencyTagLibActionService

class LocalCurrencyTagLib {

    static namespace = "app"

    GetLocalCurrencyTagLibActionService getLocalCurrencyTagLibActionService
    /**
     * Render currency code of country which belongs to logged in user
     * example: <app:localCurrency property="name"}"></app:localCurrency>
     * example: <app:localCurrency property="symbol"}"></app:localCurrency>
     *
     * @attr property REQUIRED - property name of Currency domain
     */
    def localCurrency = { attrs, body ->
        String  output = getLocalCurrencyTagLibActionService.execute(attrs, null)
        out << output
    }
}
