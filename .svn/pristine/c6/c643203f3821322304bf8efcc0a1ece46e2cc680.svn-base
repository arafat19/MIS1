package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownBankTagLibActionService
import com.athena.mis.application.actions.taglib.GetSystemBankTagLibActionService
import com.athena.mis.utility.Tools

class BankTagLib {

    static namespace = "app"

    GetDropDownBankTagLibActionService getDropDownBankTagLibActionService
    GetSystemBankTagLibActionService getSystemBankTagLibActionService

    /**
     * Render html select of Bank
     * example: <app:dropDownBank name="bankId"}"></app:dropDownBank>
     *
     * @attr name REQUIRED - name of html component
     * @attr id REQUIRED - id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr url - url to reload dropDown
     * @attr process - reserved id of RmsProcessType
     * @attr instrument - reserved id of RmsInstrumentType
     */
    def dropDownBank = { attrs, body ->
        Map preResult = (Map) getDropDownBankTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownBankTagLibActionService.execute(preResult, null)
        out << html
    }

    /**
     * Render hidden input html of system bank id
     * @attr name REQUIRED - name & id of html component
     */
    def systemBank = { attrs, body ->
        Map preResult = (Map) getSystemBankTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getSystemBankTagLibActionService.execute(null, preResult)
        out << html
    }
}
