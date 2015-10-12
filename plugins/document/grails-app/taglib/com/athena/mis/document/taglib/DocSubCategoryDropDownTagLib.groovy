package com.athena.mis.document.taglib

import com.athena.mis.document.actions.taglib.GetDropDownDocCategoryTagLibActionService
import com.athena.mis.document.actions.taglib.GetDropDownDocSubCategoryTagLibActionService
import com.athena.mis.utility.Tools

class DocSubCategoryDropDownTagLib {

    static namespace = "doc"

    GetDropDownDocSubCategoryTagLibActionService getDropDownDocSubCategoryTagLibActionService

    /**
     * Render html select of DocSubCategory by categoryId
     * example: <app:dropDownSubCategory id="mySubCategory" name="mySubCategory" category_id= '123' data_model_name="mySubCategory"></app:dropDownSubCategory>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr category_id REQUIRED - parent category id
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - controller action to render this taglib
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     * @attr is_multi_select - boolean value (true/false), if true multiple select will work
     */
    def dropDownSubCategory = { attrs, body ->
        Map preResult = (Map) getDropDownDocSubCategoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownDocSubCategoryTagLibActionService.execute(preResult, null)
        out << html
    }

}
