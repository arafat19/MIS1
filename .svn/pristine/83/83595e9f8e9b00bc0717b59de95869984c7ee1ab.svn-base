package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.GetRmsInstrumentDropDownTagLibActionService
import com.athena.mis.utility.Tools

class RmsInstrumentDropDownTagLib {
    static namespace = "rms"

    GetRmsInstrumentDropDownTagLibActionService getRmsInstrumentDropDownTagLibActionService
    /**
     * Render html select of instrument
     * example: <rms:dropDownInstrument></rms:dropDownInstrument>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url - url for data source
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr process_type_id - processType from rms_process_instrument_mapping
     */
    def dropDownInstrument = { attrs, body ->
        Map preResult = (Map) getRmsInstrumentDropDownTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getRmsInstrumentDropDownTagLibActionService.execute(preResult, null)
        out << html
    }

}
