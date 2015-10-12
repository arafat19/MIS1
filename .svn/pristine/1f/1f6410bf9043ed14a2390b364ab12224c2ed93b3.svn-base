package com.athena.mis.accounting.taglib

import com.athena.mis.accounting.actions.taglib.GetDropDownAccGroupTagLibActionService
import com.athena.mis.utility.Tools

class AccGroupDropDownTagLib {

    static namespace = "acc"

    GetDropDownAccGroupTagLibActionService getDropDownAccGroupTagLibActionService

    /**
     * Render html select of accGroup
     * example: <acc:dropDownAccGroup name="groupId"}"></acc:dropDownAccGroup>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr elements - comma separated individual elements to populate
     * (objects should be available in List of given type)
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownAccGroup = { attrs, body ->
        Map preResult = (Map) getDropDownAccGroupTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownAccGroupTagLibActionService.execute(preResult, null)
        out << html
    }
}
