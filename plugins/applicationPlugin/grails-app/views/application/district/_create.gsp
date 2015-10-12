<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Create District
		</div>
	</div>
	<form name='districtForm' id='districtForm' class="form-horizontal form-widgets" role="form">
		<div class="panel-body">
			<g:hiddenField name="id"/>
			<g:hiddenField name="version"/>
			<div class="col-md-6">
				<div class="form-group">
					<label class="col-md-2 control-label label-required" for="name">Name:</label>

					<div class="col-md-4">
						<input type="text" class="k-textbox" id="name" name="name" tabindex="1"
						       placeholder="" required validationMessage="Required"/>
					</div>

					<div class="col-md-1 pull-left">
						<span class="k-invalid-msg" data-for="name"></span>
					</div>

                    <label class="col-md-3 control-label label-optional" for="isGlobal">isGlobal:</label>

                    <div class="col-md-2">
                        <g:checkBox tabindex="3" name="isGlobal"/>
                    </div>

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