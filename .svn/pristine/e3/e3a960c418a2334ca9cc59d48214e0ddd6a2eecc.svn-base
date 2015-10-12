package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.GetRmsExchangeHouseDropDownTagLibActionService
import com.athena.mis.arms.actions.taglib.GetRmsExchangeHouseFilteredDropDownTagLibActionService
import com.athena.mis.utility.Tools

/**
 * Created by user on 3/15/14.
 */
class RmsExchangeHouseDropDownTagLib {

    static namespace = "rms"

    GetRmsExchangeHouseDropDownTagLibActionService getRmsExchangeHouseDropDownTagLibActionService
    GetRmsExchangeHouseFilteredDropDownTagLibActionService getRmsExchangeHouseFilteredDropDownTagLibActionService

    /**
     * Render html select of exchange house
     * example: <rms:dropDownExchangeHouse></rms:dropDownExchangeHouse>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url - url for data source
     * @attr filter_mapping - default value true
     *      - if true shows list of all Exchange House mapped with user
     *      - if false shows list of all Exchange House by company id
     * @attr add_all_attributes - default value false
     *      - if true add all attributes of project in list
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr add_balance - add balance of exchange house
     */

    def dropDownExchangeHouse = { attrs, body ->
        Map preResult = (Map) getRmsExchangeHouseDropDownTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getRmsExchangeHouseDropDownTagLibActionService.execute(preResult, null)
        out << html
    }

    /**
     * Render html select of exchange house
     * example: <rms:dropDownExchangeHouseFiltered></rms:dropDownExchangeHouse>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr url REQUIRED - url of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr from_date REQUIRED - from date
     * @attr to_date REQUIRED - to date
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
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr filter_branch - (true/false)default value false
     */

    def dropDownExchangeHouseFiltered = { attrs, body ->
        Map preResult = (Map) getRmsExchangeHouseFilteredDropDownTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getRmsExchangeHouseFilteredDropDownTagLibActionService.execute(preResult, null)
        out << html
    }
}
