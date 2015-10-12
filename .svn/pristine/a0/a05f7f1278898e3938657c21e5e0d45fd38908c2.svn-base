package com.athena.mis.document.taglib

import com.athena.mis.document.actions.taglib.GetDropDownAppUserDocCategoryTagLibActionService
import com.athena.mis.document.actions.taglib.GetDropDownAppUserDocSubCategoryTagLibActionService
import com.athena.mis.utility.Tools

class DocSubCategoryUserMappingDropDownTagLib {

    static namespace = "doc"

    GetDropDownAppUserDocSubCategoryTagLibActionService getDropDownAppUserDocSubCategoryTagLibActionService


    /**
     * Render html select of AppUser by subCategoryId
     * example: <app:dropDownAppUserForSubCategory sub_category_id="1"></app:dropDownAppUserForSubCategory>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr sub_category_id REQUIRED - id of DocSubCategory (DocSubCategory.id)
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
    def dropDownAppUserForSubCategory ={ attrs, body ->
        Map preResult = (Map) getDropDownAppUserDocSubCategoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownAppUserDocSubCategoryTagLibActionService.execute(preResult, null)
        out << html
    }

}
