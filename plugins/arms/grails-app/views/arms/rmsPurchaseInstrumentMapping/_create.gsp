<%@ page import="com.athena.mis.arms.utility.RmsProcessTypeCacheUtility; com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Purchase Instrument Mapping
        </div>
    </div>

    <form id='instrumentMappingForm' name='instrumentMappingForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="col-md-6">
                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="bankId">Bank:</label>

                    <div class="col-md-6">
                        <app:dropDownBank data_model_name="dropDownBank" id="bankId" name="bankId"
                                          required="true" validationMessage="Required"
                                          tabindex="1" onchange="onChangeBank();">
                        </app:dropDownBank>
                    </div>

                    <div class="col-md-4 pull-left">
                        <span class="k-invalid-msg" data-for="bankId"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="districtId">District:</label>

                    <div class="col-md-6">
                        <app:dropDownDistrict data_model_name="dropDownDistrict" name="districtId" id="districtId"
                                              required="true" validationMessage="Required" bank_id=""
                                              tabindex="2" onchange="updateBranchList();"
                                              url="${createLink(controller: 'district', action: 'reloadDistrictDropDown')}">
                        </app:dropDownDistrict>
                    </div>

                    <div class="col-md-4 pull-left">
                        <span class="k-invalid-msg" data-for="districtId"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="bankBranchId">Branch:</label>

                    <div class="col-md-6">
                        <app:dropDownBranchesByBankAndDistrict data_model_name="dropDownBankBranch" name="bankBranchId"
                                                               id="bankBranchId"
                                                               tabindex="3" validationmessage="Required" required="true"
                                                               url="${createLink(controller: 'bankBranch', action: 'reloadBranchesDropDownByBankAndDistrict')}">
                        </app:dropDownBranchesByBankAndDistrict>
                    </div>

                    <div class="col-md-4 pull-left">
                        <span class="k-invalid-msg" data-for="bankBranchId"></span>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="form-group">
                    <label class="col-md-2 control-label label-optional">Process:</label>

                    <div class="col-md-6"><span><b>Purchase</b></span></div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="districtId">Instrument:</label>

                    <div class="col-md-6">
                        <rms:dropDownInstrument
                                data_model_name="dropDownInstrument" name="instrumentTypeId"
                                required="true" validationmessage="Required"
                                id="instrumentTypeId" process_type_id="${RmsProcessTypeCacheUtility.PURCHASE}"
                                url="${createLink(controller: 'rmsInstrument', action: 'reloadInstrumentDropDown')}"
                                tabindex="4">
                        </rms:dropDownInstrument>
                    </div>

                    <div class="col-md-4 pull-left">
                        <span class="k-invalid-msg" data-for="instrumentTypeId"></span>
                    </div>
                </div>
            </div>

            <div class="col-md-12">
                <div class="form-group">
                    <label class="col-md-1 control-label label-required" for="commissionScript">Logic:</label>

                    <div class="col-md-11">
                        <textarea tabindex="5" class='required k-textbox'
                                  id="commissionScript" name="commissionScript" rows="8"></textarea>
                    </div>
                </div>
            </div>

        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="7"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>

</div>