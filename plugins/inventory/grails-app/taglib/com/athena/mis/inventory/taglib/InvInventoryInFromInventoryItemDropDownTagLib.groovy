package com.athena.mis.inventory.taglib

import com.athena.mis.inventory.actions.taglib.GetDropDownInventoryItemInFromInventoryTagLibActionService
import com.athena.mis.utility.Tools

class InvInventoryInFromInventoryItemDropDownTagLib {

    static namespace = "inv"

    GetDropDownInventoryItemInFromInventoryTagLibActionService getDropDownInventoryItemInFromInventoryTagLibActionService

    /**
     * Render html select of inventory item for the given transaction id
     * example:  <inv:dropDownInventoryItemInFromInventory transactionId="${transactionId}"></inv:dropDownInventoryItemInFromSupplier>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name & id of html component
     * @attr transaction_id REQUIRED - id of systemEntity (inventory type: site/store)
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - url for data source
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr actual_quantity - actual quantity of selected item
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     */
    def dropDownInventoryItemInFromInventory = { attrs, body ->
        Map preResult = (Map) getDropDownInventoryItemInFromInventoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownInventoryItemInFromInventoryTagLibActionService.execute(preResult, null)
        out << html
    }


}
