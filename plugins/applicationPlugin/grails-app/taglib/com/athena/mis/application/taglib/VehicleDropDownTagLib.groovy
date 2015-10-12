package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownVehicleTagLibActionService
import com.athena.mis.utility.Tools

class VehicleDropDownTagLib {

    static namespace = "app"

    GetDropDownVehicleTagLibActionService getDropDownVehicleTagLibActionService

    /**
     * Render html select of vehicle
     * example: <app:dropDownVehicle name="vehicleId"}"></app:dropDownVehicle>
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
    def dropDownVehicle = { attrs, body ->
        Map preResult = (Map) getDropDownVehicleTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownVehicleTagLibActionService.execute(preResult, null)
        out << html
    }
}
