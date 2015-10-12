package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownBankBranchTagLibActionService
import com.athena.mis.utility.Tools

class BankBranchDropDownTagLib {

    static namespace = "app"

    GetDropDownBankBranchTagLibActionService getDropDownBankBranchTagLibActionService
    /**
     * Render html select of bankBranch
     * example: <app:dropDownBankBranch name ="bankBranchId"></app:dropDownBankBranch>
     * @attr name REQUIRED - name of html component
     * @attr id REQUIRED - id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropDownList
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text(Default is Please Select....)
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage- validation message to be shown (Default is 'Required')
     * @attr bank_id- bank id to filter bankBranch
     * @attr district_id- district id to filter bankBranch
     * @attr url - url for data source
     * @attr process - reserved id of RmsProcessType
     * @attr instrument - reserved id of RmsInstrumentType
     */

    def dropDownBranchesByBankAndDistrict ={ attrs, body ->
        Map preResult =(Map) getDropDownBankBranchTagLibActionService.executePreCondition(attrs,null)
        Boolean isError = preResult.isError
        if(isError.booleanValue()){
            out<<Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownBankBranchTagLibActionService.execute(preResult,null)
        out<<html

    }
}
