package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownModulesTagLibActionService
import com.athena.mis.utility.Tools

class ModulesTagLib {
    static namespace = "app"

    GetDropDownModulesTagLibActionService getDropDownModulesTagLibActionService

    /**
     * Render html select of module
     * example: <app:dropDownModules name="pluginId"></app:dropDownModules>
     *
     * @attr name REQUIRED - name of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr categoryId - id of systemEntity (e.g. Inventory, Non-Inventory, Fixed Asset)
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText -No selection text (Default is Please Select...)
     * @attr showHints -Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownModules = { attrs, body ->
        Map preResult = (Map) getDropDownModulesTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownModulesTagLibActionService.execute(preResult, null)
        out << html
    }


}
