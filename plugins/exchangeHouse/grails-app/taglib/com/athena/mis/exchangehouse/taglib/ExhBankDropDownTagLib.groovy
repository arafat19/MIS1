package com.athena.mis.exchangehouse.taglib

import com.athena.mis.exchangehouse.actions.taglib.GetDropDownBankByTaskStatusTagLibActionService
import com.athena.mis.utility.Tools

class ExhBankDropDownTagLib {

    static namespace = "exh"
    GetDropDownBankByTaskStatusTagLibActionService getDropDownBankByTaskStatusTagLibActionService

    /**
     * Render html select of agent
     *
     * @attr name REQUIRED - name of html component
     * @attr id REQUIRED - id of html component
     * @attr task_type REQUIRED - type of task example:(exh task, customer task, agent task)
     * @attr task_status - status of task (new task,sent to bank, sent ot other bank, resolved by other bank, cancel)
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr from_date  - start date
     * @attr to_date  - end date
     * @attr url- url to reload bank dropDown
     * @attr tabindex - component tab index
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr isOtherBank - true/false
     */
    def dropDownBankByTaskStatusAndTaskType = { attrs ->
        Map preResult = (Map) getDropDownBankByTaskStatusTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownBankByTaskStatusTagLibActionService.execute(preResult, null)
        out << html
    }
}
