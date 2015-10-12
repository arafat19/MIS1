package com.athena.mis.budget.taglib

import com.athena.mis.budget.actions.taglib.GetDropDownBudgetForSprintTagLibActionService
import com.athena.mis.utility.Tools

class BudgBudgetForSprintDropDownTagLib {

    static namespace = "budg"

    GetDropDownBudgetForSprintTagLibActionService getDropDownBudgetForSprintTagLibActionService

    /**
     * Render html select of inventory item for the given transaction id
     * example:  <inv:dropDownBudgetForSprint sprintId="${sprintId}"></inv:dropDownBudgetForSprint>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name & id of html component
     * @attr sprint_id REQUIRED - sprint id
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropDownList
     * @attr url REQUIRED - url for data source
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     */
    def dropDownBudgetForSprint = { attrs, body ->
        Map preResult = (Map) getDropDownBudgetForSprintTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownBudgetForSprintTagLibActionService.execute(preResult, null)
        out << html
    }
}
