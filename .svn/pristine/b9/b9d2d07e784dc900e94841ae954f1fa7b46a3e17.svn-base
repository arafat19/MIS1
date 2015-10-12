package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownDistrictTagLibActionService
import com.athena.mis.utility.Tools

class DistrictDropDownTagLib {
	static namespace = "app"

    GetDropDownDistrictTagLibActionService getDropDownDistrictTagLibActionService

    /**
     * Render html select of district filtered
     * example: <app:dropDownDistrict name ="districtId"></app:dropDownDistrict>
     * @attr name REQUIRED - name of html component
     * @attr bank_id REQUIRED- bank id to filter district by bank,0 to get all districts
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr id REQUIRED - id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropDownList
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text(Default is Please Select....)
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage- validation message to be shown (Default is 'Required')
     * @attr url - url for data source
     * @attr process - reserved id of RmsProcessType
     * @attr instrument - reserved id of RmsInstrumentType
     */

    def dropDownDistrict ={ attrs, body ->
        Map preResult =(Map) getDropDownDistrictTagLibActionService.executePreCondition(attrs,null)
        Boolean isError = preResult.isError
        if(isError.booleanValue()){
            out<<Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownDistrictTagLibActionService.execute(preResult,null)
        out<<html

    }
}
