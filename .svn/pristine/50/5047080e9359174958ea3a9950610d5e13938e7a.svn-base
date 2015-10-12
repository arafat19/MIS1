package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.GetRmsTaskListDropDownTagLibActionService
import com.athena.mis.utility.Tools

class RmsTaskListDropDownTagLib {

    static namespace = "rms"

    GetRmsTaskListDropDownTagLibActionService getRmsTaskListDropDownTagLibActionService

    /**
     * Render html select of RmsTaskList
     * example: <rms:dropDownTaskList></rms:dropDownTaskList>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr url REQUIRED - url of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr from_date - from date
     * @attr to_date - to date
     * @attr exchange_house_id - RmsExchangeHouse.id
     * @attr task_status_list - list of Rms task status reserved id
     * @attr is_revised - isRevised true/false
     * @attr process - process reserved id
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr instrument - instrument reserved id
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr add_balance - add balance of exchange house
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr filter_branch - true/false- default value is false
     * @attr default_value - default value to be shown as selected (Default is '')
     */

    def dropDownTaskList = { attrs, body ->
        Map preResult = (Map) getRmsTaskListDropDownTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getRmsTaskListDropDownTagLibActionService.execute(preResult, null)
        out << html
    }

}
