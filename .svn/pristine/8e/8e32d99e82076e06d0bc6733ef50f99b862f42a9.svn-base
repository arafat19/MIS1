package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownAppUserEntityTagLibActionService
import com.athena.mis.utility.Tools

class AppUserEntityDropDownTagLib {

    static namespace = "app"

    GetDropDownAppUserEntityTagLibActionService getDropDownAppUserEntityTagLibActionService
    /**
     * Render html select of AppUser by entity type id and entity id
     * example: <app:dropDownAppUserEntity typeId="${AppUserEntityTypeCacheUtility.PROJECT}"></app:dropDownAppUserEntity>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name & id of html component
     * @attr entity_type_id REQUIRED - id of entity type (PROJECT, INVENTORY, GROUP etc.)
     * @attr entity_id REQUIRED - id of entity (Project.id, InvInventory.id, AppGroup.id etc.)
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - url for data source
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     */
    def dropDownAppUserEntity = { attrs, body ->
        Map preResult = (Map) getDropDownAppUserEntityTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownAppUserEntityTagLibActionService.execute(preResult, null)
        out << html
    }
}
