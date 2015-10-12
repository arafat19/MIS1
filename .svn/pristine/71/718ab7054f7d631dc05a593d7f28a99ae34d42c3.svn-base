package com.athena.mis.document.taglib

import com.athena.mis.document.actions.taglib.GetDropDownAppUserDocCategoryTagLibActionService
import com.athena.mis.utility.Tools

class DocCategoryUserMappingDropDownTagLib {

    static namespace = "doc"

    GetDropDownAppUserDocCategoryTagLibActionService getDropDownAppUserDocCategoryTagLibActionService

    /**
     * Render html select of AppUser by categoryId
     * example: <app:dropDownAppUserForCategory categoryId="1"></app:dropDownAppUserForCategory>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr category_id REQUIRED - id of DocCategory (DocCategory.id)
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
    def dropDownAppUserForCategory ={ attrs, body ->
        Map preResult = (Map) getDropDownAppUserDocCategoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownAppUserDocCategoryTagLibActionService.execute(preResult, null)
        out << html
    }

}
