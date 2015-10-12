package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetDropDownPtModuleTagLibActionService
import com.athena.mis.utility.Tools

class PtModuleDropDownTagLib {
    static namespace = "ptk"

    GetDropDownPtModuleTagLibActionService getDropDownPtModuleTagLibActionService

    /**
     * Render html select of module
     * example: <ptk:dropDownModule filterMapping="false"}"></ptk:dropDownModule>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr filterMapping - default value true
     *      - if true shows list of all module mapped with user
     *      - if false shows list of all module by company id
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownModule = { attrs, body ->
        Map preResult = (Map) getDropDownPtModuleTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownPtModuleTagLibActionService.execute(preResult, null)
        out << html
    }
}
