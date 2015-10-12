package com.athena.mis.fixedasset.taglib

import com.athena.mis.fixedasset.actions.taglib.GetDropDownFxdItemForConsumptionTagLibActionService
import com.athena.mis.utility.Tools

class FxdItemForConsumptionDropDownTagLib {

    static namespace = "fxd"

    GetDropDownFxdItemForConsumptionTagLibActionService getDropDownFxdItemForConsumptionTagLibActionService

    /**
     * Render html select of items for consumption
     * example: <fxd:dropDownFxdItemForConsumption name="itemId"}"></fxd:dropDownFxdItemForConsumption>
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
    def dropDownFxdItemForConsumption = { attrs, body ->
        Map preResult = (Map) getDropDownFxdItemForConsumptionTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownFxdItemForConsumptionTagLibActionService.execute(preResult, null)
        out << html
    }
}
