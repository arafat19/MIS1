package com.athena.mis.inventory.taglib

import com.athena.mis.inventory.actions.taglib.GetDropDownInventoryTagLibActionService
import com.athena.mis.utility.Tools

class InvInventoryDropDownTagLib {

    static namespace = "inv"

    GetDropDownInventoryTagLibActionService getDropDownInventoryTagLibActionService

    /**
     * Render html select of inventory for the given type id (site/store)
     * example: <inv:dropDownInventory typeId="${InvInventoryTypeCacheUtility.TYPE_STORE}"></inv:dropDownInventory>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr typeId REQUIRED - id of systemEntity (inventory type: site/store)
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownInventory = { attrs, body ->
        Map preResult = (Map) getDropDownInventoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownInventoryTagLibActionService.execute(preResult, null)
        out << html
    }
}
