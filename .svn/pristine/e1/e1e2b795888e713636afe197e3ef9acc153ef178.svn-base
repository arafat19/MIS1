package com.athena.mis.document.taglib

import com.athena.mis.document.actions.taglib.GetDropDownDocCategoryTagLibActionService
import com.athena.mis.utility.Tools

class DocCategoryDropDownTagLib {

    static namespace = "doc"

    GetDropDownDocCategoryTagLibActionService getDropDownDocCategoryTagLibActionService

    /**
     * Render html select of DocCategory by entity type id and entity id
     * example: <app:dropDownCategory name="category"></app:dropDownCategory>
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
    def dropDownCategory = { attrs, body ->
        Map preResult = (Map) getDropDownDocCategoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownDocCategoryTagLibActionService.execute(preResult, null)
        out << html
    }
}
