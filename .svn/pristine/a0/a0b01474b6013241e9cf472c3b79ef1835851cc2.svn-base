package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownItemTagLibActionService
import com.athena.mis.utility.Tools

class ItemDropDownTagLib {

    static namespace = "app"

    GetDropDownItemTagLibActionService getDropDownItemTagLibActionService

    /**
     * Render html select of item for the given type id (optional: Material, Fixed Asset etc.)
     * example: <app:dropDownItem typeId="401"></app:dropDownItem>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr typeId - id of item type (e.g. Material, Fixed Asset etc.)
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownItem = { attrs, body ->
        Map preResult = (Map) getDropDownItemTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownItemTagLibActionService.execute(preResult, null)
        out << html
    }
}
