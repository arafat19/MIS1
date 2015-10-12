package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownCompanyTagLibActionService
import com.athena.mis.utility.Tools

class CompanyDropDownTagLib {

    static namespace = "app"

    GetDropDownCompanyTagLibActionService getDropDownCompanyTagLibActionService

    /**
     * Render html select of company
     * example: <app:dropDownCompany name="companyId"}"></app:dropDownCompany>
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
    def dropDownCompany = { attrs, body ->
        Map preResult = (Map) getDropDownCompanyTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownCompanyTagLibActionService.execute(preResult, null)
        out << html
    }
}
