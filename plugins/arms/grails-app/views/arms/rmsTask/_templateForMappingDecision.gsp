<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div class="form-group">
    <div class="col-md-8" style="padding-left: 0">
        <table id="flex" style="display:none"></table>
    </div>

    <div class="col-md-4" style="padding-right: 0;padding-left: 0">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Mapping Decision
                </div>
            </div>

            <form id='mapTaskForm' name='mapTaskForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="processType">Process:</label>

                        <div class="col-md-9">
                            <app:dropDownSystemEntity dataModelName="dropDownProcessType" name="processType"
                                                      typeId="${SystemEntityTypeCacheUtility.ARMS_PROCESS_TYPE}"
                                                      tabindex="1" onchange="populateInstrument();">
                            </app:dropDownSystemEntity>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="instrumentType">Instrument:</label>

                        <div class="col-md-9">
                            <rms:dropDownInstrument
                                    data_model_name="dropDownInstrumentType" name="instrumentType"
                                    id="instrumentType" onchange="onChangeInstrument()"
                                    url="${createLink(controller: 'rmsInstrument', action: 'reloadInstrumentDropDown')}"
                                    tabindex="2">
                            </rms:dropDownInstrument>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="mappingBank">Bank:</label>

                        <div class="col-md-9">
                            <app:dropDownBank data_model_name="dropDownBank" id="mappingBank" name="mappingBank"
                                              tabindex="3" onchange="populateDistrict()"
                                              url="${createLink(controller: 'bank', action: 'reloadBankDropDownTagLib')}">
                            </app:dropDownBank>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="mappingDistrict">District:</label>

                        <div class="col-md-9">
                            <app:dropDownDistrict data_model_name="dropDownDistrict" name="mappingDistrict"
                                                  id="mappingDistrict"
                                                  tabindex="4" onchange="populateBranch();" bank_id=""
                                                  url="${createLink(controller: 'district', action: 'reloadDistrictDropDown')}">
                            </app:dropDownDistrict>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="mappingBranch">Branch:</label>

                        <div class="col-md-9">
                            <app:dropDownBranchesByBankAndDistrict data_model_name="dropDownBranch" name="mappingBranch"
                                                                   id="mappingBranch"
                                                                   tabindex="5"
                                                                   url="${createLink(controller: 'bankBranch', action: 'reloadBranchesDropDownByBankAndDistrict')}">
                            </app:dropDownBranchesByBankAndDistrict>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">

                    <button id="search" name="search" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="6"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Save
                    </button>
                    <button id="reset" name="reset" type="button" data-role="button" class="k-button k-button-icontext"
                            role="button" tabindex="7"
                            aria-disabled="false" onclick='resetMapTaskForm();'><span
                            class="k-icon k-i-close"></span>Reset
                    </button>

                </div>
            </form>
        </div>
    </div>
</div>