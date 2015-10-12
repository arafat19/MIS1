package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetDropDownPtProjectTagLibActionService
import com.athena.mis.utility.Tools

class PtProjectDropDownTagLib {
    static namespace = "pt"

    GetDropDownPtProjectTagLibActionService getDropDownPtProjectTagLibActionService

    /**
     * Render html select of project
     * example: <pt:dropDownProject filterMapping="false"}"></pt:dropDownProject>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr filterMapping - default value true
     *      - if true shows list of all project mapped with user
     *      - if false shows list of all project by company id
     * @attr addAllAttributes - default value false
     *      - if true add all attributes of project in list
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownProject = { attrs, body ->
        Map preResult = (Map) getDropDownPtProjectTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownPtProjectTagLibActionService.execute(preResult, null)
        out << html
    }
}
