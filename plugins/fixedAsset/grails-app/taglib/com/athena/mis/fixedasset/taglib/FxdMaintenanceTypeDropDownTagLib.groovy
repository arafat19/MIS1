package com.athena.mis.fixedasset.taglib

import com.athena.mis.fixedasset.actions.taglib.GetDropDownFxdMaintenanceTypeTagLibActionService
import com.athena.mis.utility.Tools

class FxdMaintenanceTypeDropDownTagLib {

    static namespace = "fxd"

    GetDropDownFxdMaintenanceTypeTagLibActionService getDropDownFxdMaintenanceTypeTagLibActionService
    /**
     * Render html select of inventory item for the given transaction id
     * example: <fxd:dropDownFxdMaintenanceType name="maintenanceTypeId"}"></fxd:dropDownFxdMaintenanceType>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name & id of html component
     * @attr item_id REQUIRED - id of fixed asset
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - url for data source
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     */
    def dropDownFxdMaintenanceType = { attrs, body ->
        Map preResult = (Map) getDropDownFxdMaintenanceTypeTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownFxdMaintenanceTypeTagLibActionService.execute(preResult, null)
        out << html
    }
}
