<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Create Task Note
		</div>
	</div>
	<form id='createNoteForm' name="createNoteForm"
	      class="form-horizontal form-widgets" role="form">
		<g:hiddenField name="id"/>
		<g:hiddenField name="version"/>
		<g:hiddenField name="taskId"/>
		<div class="panel-body">
			<div class="form-group">
				<label class="col-md-2 control-label" for="lblCustomerId">Customer Code:</label>
				<div class="col-md-3">
					<span id="lblCustomerId">None</span>
				</div>

				<label class="col-md-2 control-label" for="lblRefNo">Ref No:</label>
				<div class="col-md-3">
					<span id="lblRefNo">None</span>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-2 control-label" for="lblCustomerName">Name:</label>
				<div class="col-md-3">
					<span id="lblCustomerName">None</span>
				</div>

				<label class="col-md-2 control-label" for="lblAmount">Amount:</label>
				<div class="col-md-3">
					<span id="lblAmount">None</span>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-2 control-label" for="lblBeneficiaryName">Beneficiary Name:</label>
				<div class="col-md-3">
					<span id="lblBeneficiaryName">None</span>
				</div>

				<label class="col-md-2 control-label" for="lblPaymentType">Payment Type:</label>
				<div class="col-md-3">
					<span id="lblPaymentType">None</span>
				</div>
			</div>
			<div class="form-group">
				<label class="col-md-2 control-label label-required" for="lblBeneficiaryName">Note:</label>
				<div class="col-md-10">
					<textarea class="required k-textbox" id="note" name="note" rows="2"
					          maxlength="1000" required validationMessage="Required"  tabindex="1"></textarea>
                    <span class="k-invalid-msg" data-for="lblBeneficiaryName"></span>
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
			        role="button" tabindex="2"
			        aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
			</button>

			<button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
			        class="k-button k-button-icontext" role="button" tabindex="3"
			        aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
			</button>
		</div>
	</form>

</div>