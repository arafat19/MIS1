package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.GetBankListDropDownTagLibActionService
import com.athena.mis.utility.Tools

class BankListDropDownTagLib {

    static namespace = "rms"

    GetBankListDropDownTagLibActionService getBankListDropDownTagLibActionService

    /**
     * Render html select of filtered bank list
     * example: <rms:dropDownBankListFiltered></rms:dropDownBankListFiltered>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr url REQUIRED - url of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr from_date - from date
     * @attr to_date - to date
     * @attr task_list_id - RmsTaskList.id
     * @attr task_status_list - list of Rms task status reserved id
     * @attr process - process reserved id
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     */

    def dropDownBankListFiltered = { attrs, body ->
        Map preResult = (Map) getBankListDropDownTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getBankListDropDownTagLibActionService.execute(preResult, null)
        out << html
    }

}
