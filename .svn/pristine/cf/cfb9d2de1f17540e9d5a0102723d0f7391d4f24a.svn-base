package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownCurrencyTagLibActionService
import com.athena.mis.utility.Tools

class CurrencyDropDownTagLib {

    static namespace = "app"

    GetDropDownCurrencyTagLibActionService getDropDownCurrencyTagLibActionService

    /**
     * Render html select of currency
     * example: <app:dropDownCurrency name="currencyId"}"></app:dropDownCurrency>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownCurrency = { attrs, body ->
        Map preResult = (Map) getDropDownCurrencyTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownCurrencyTagLibActionService.execute(preResult, null)
        out << html
    }
}
