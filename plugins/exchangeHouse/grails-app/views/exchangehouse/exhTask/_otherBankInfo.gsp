<div id="otherBankInfo" style="display:none">
	<div class="form-group">
		<label class="col-md-5 control-label label-required" for="outletBankId">Bank:</label>
		<div class="col-md-5">
            <app:dropDownBank data_model_name="dropDownBank" name="outletBankId" validationmessage=" "
                              id="outletBankId"
                              onchange="onChangeBank();">
            </app:dropDownBank>
		</div>
        <div class="col-md-2 pull-left">
            <span class="k-invalid-msg" data-for="outletBankId"></span>
        </div>
	</div>
	<div class="form-group">
		<label class="col-md-5 control-label label-required" for="outletDistrictId">District:</label>
		<div class="col-md-5">
            <app:dropDownDistrict data_model_name="dropDownDistrict" name="outletDistrictId" id="outletDistrictId"
                                  onchange="onChangeDistrict();" bank_id="" validationmessage=" "
                                  url="${createLink(controller: 'district',action: 'reloadDistrictDropDown')}">
            </app:dropDownDistrict>
		</div>
		<div class="col-md-2 pull-left">
			<span class="k-invalid-msg" data-for="outletDistrictId"></span>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-5 control-label label-required" for="outletBranchId">Bank Branch:</label>
		<div class="col-md-5">
            <app:dropDownBranchesByBankAndDistrict data_model_name="dropDownBankBranch" name="outletBranchId" id="outletBranchId"
                                                   validationmessage=" "
                                                   url="${createLink(controller: 'bankBranch', action: 'reloadBranchesDropDownByBankAndDistrict')}">
            </app:dropDownBranchesByBankAndDistrict>
		</div>
		<div class="col-md-2 pull-left">
			<span class="k-invalid-msg" data-for="outletBranchId"></span>
		</div>
	</div>
</div>
