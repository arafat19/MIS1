<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.application.utility.AppUserEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Create Bank Branch
		</div>
	</div>
    <app:systemEntityByReserved name="hidEntityType" reservedId="${AppUserEntityTypeCacheUtility.BANK_BRANCH}" typeId="${SystemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE}">
    </app:systemEntityByReserved>
	<form name='bankbranchForm' id='bankbranchForm' class="form-horizontal form-widgets" role="form">
		<div class="panel-body">
			<g:hiddenField name="id" value=""/>
			<g:hiddenField name="version" value=""/>
			<div class="form-group">
				<div class="col-md-6">
					<div class="form-group">
						<label class="col-md-3 control-label label-required" for="bankId">Bank:</label>

						<div class="col-md-6">
							<app:dropDownBank
									name="bankId"
									id="bankId"
									required="true"
									validationMessage="Required"
									data_model_name="dropDownBank"
									tabindex="1">
							</app:dropDownBank>
						</div>

						<div class="col-md-3 pull-left">
							<span class="k-invalid-msg" data-for="bankId"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-3 control-label label-required" for="districtId">District:</label>

						<div class="col-md-6">
                            <app:dropDownDistrict data_model_name="dropDownDistrict" name="districtId" id="districtId"
                                                   bank_id="bankId" tabindex="2">
                            </app:dropDownDistrict>
						</div>

						<div class="col-md-3 pull-left">
							<span class="k-invalid-msg" data-for="districtId"></span>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-3 control-label label-required" for="name">Name:</label>

						<div class="col-md-6">
							<input type="text" class="k-textbox" id="name" name="name" tabindex="3"
							       required validationMessage="Required"/>
						</div>

						<div class="col-md-3 pull-left">
							<span class="k-invalid-msg" data-for="name"></span>
						</div>
					</div>
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="code">Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="code" name="code" tabindex="4"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="code"></span>
                        </div>
                    </div>
				</div>

				<div class="col-md-6">

					<div class="form-group">
						<label class="col-md-4 control-label label-optional" for="address">Address:</label>

						<div class="col-md-8">
							<textarea type="text" class="k-textbox" id="address" name="address" rows="3"
							          placeholder="255 Char Max" tabindex="5"></textarea>
						</div>
					</div>
                    <div class="form-group">
                        <label class="col-md-4 control-label label-optional"
                               for="isPrincipleBranch">Principle Branch:</label>

                        <div class="col-md-6">
                            <g:checkBox name="isPrincipleBranch" tabindex="6"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-4 control-label label-optional"
                               for="isSmeServiceCenter">SME Service Center:</label>

                        <div class="col-md-6">
                            <g:checkBox name="isSmeServiceCenter" tabindex="7"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-4 control-label label-optional" for="isGlobal">Global:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="8" name="isGlobal"/>
                        </div>
                    </div>
				</div>
			</div>
		</div>
		<div class="panel-footer">

			<button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
			        role="button" tabindex="8"
			        aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
			</button>

			<button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
			        class="k-button k-button-icontext" role="button" tabindex="9"
			        aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
			</button>

		</div>
	</form>
</div>
