package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetDropDownPtOwnerTagLibActionService
import com.athena.mis.utility.Tools

class PtOwnerDropDownTagLib {

    static namespace = "pt"

    GetDropDownPtOwnerTagLibActionService getDropDownPtOwnerTagLibActionService

    /**
     * Render html select of owner for the given ownerType id
     * example: <pt:dropDownOwner typeId="${RoleTypeCacheUtility.ROLE_SOFTWARE_ENGINEER}"></pt:dropDownOwner>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr typeId REQUIRED - id of systemEntityType domain
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText -No selection text (Default is Please Select...)
     * @attr showHints -Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownOwner = { attrs, body ->
        Map preResult = (Map) getDropDownPtOwnerTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownPtOwnerTagLibActionService.execute(preResult, null)
        out << html
    }
}
