package com.athena.mis.inventory.taglib

import com.athena.mis.inventory.actions.taglib.GetDropDownInventoryProductionLineItemTagLibActionService
import com.athena.mis.utility.Tools

class InvProductionLineItemDropDownTagLib {

    static namespace = "inv"

    GetDropDownInventoryProductionLineItemTagLibActionService getDropDownInventoryProductionLineItemTagLibActionService

    /**
     * Render html select of inventory item for the given transaction id
     * example: <inv:dropDownInventoryProductionLineItem></inv:dropDownInventoryProductionLineItem>
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
    def dropDownInventoryProductionLineItem = { attrs, body ->
        Map preResult = (Map) getDropDownInventoryProductionLineItemTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownInventoryProductionLineItemTagLibActionService.execute(preResult, null)
        out << html
    }

}
