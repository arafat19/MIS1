<script type="text/javascript">

	var uploading = false;
	var validatorSanctionUpload;
	$(document).ready(function () {
		onLoadSanctionUpload();

	});

	function onLoadSanctionUpload() {
		validatorSanctionUpload = $("#sanctionUploadFrm").kendoValidator({
			validateOnBlur: false,
			rules: {
				upload: function (input) {
					if (input[0].type == "file" && ($(input[0]).is('[validationMessage]'))) {
						return input.closest(".k-upload").find(".k-file").length;
					}
					return true;
				}
			}
		}).data("kendoValidator");
		bindFormSubmit();
		$("#sanctionFile").kendoUpload({multiple: false});

		// update page title
		$(document).attr('title', "ARMS - Upload Sanction File");
		loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhSanction/showSanctionUpload");
	}

	function bindFormSubmit() {
		// bind form submit event
		var actionUrl = "${createLink(controller: 'exhSanction',action: 'uploadSanctionFile')}";
		$("#sanctionUploadFrm").attr('action', actionUrl);

		$('#sanctionUploadFrm').iframePostForm({
			post: function () {
				uploading = true;
				showLoadingSpinner(true);
				setButtonDisabled($('.save'), true);
			},
			complete: function (response) {
				if (uploading == true) {
					showLoadingSpinner(false);
					onCSVFileUpload(response);
					uploading = false;
					setButtonDisabled($('.save'), false);
				}
			},
			beforePost: function () {
				if (!validatorSanctionUpload.validate()) {
					return false;
				}
				return true;
			}
		});
	}

	function resetForm() {
		validatorSanctionUpload.hideMessages();
	}

	function onCSVFileUpload(resp) {
		$('#errorListShow').hide();
		try {
			var data = eval('(' + resp + ')');
		} catch (e) {
			showError('Error evaluating server response. Refresh the page and try again.');
			return false;
		}
		if (data.isError == true) {
			if (data.generalError == true) {
				showError(data.message);
			} else {
				renderError(data.errors);
			}

		} else {
			$('#sanctionFile').val('');
			showSuccess(data.message);
		}
	}

	var errorList = false;
	function renderError(errors) {
		$('#errorListShow').hide();
		$('#csvErrorDiv').html('');
		$('#csvErrorDiv').html("<table id='flex1' style='display:none;'><thead><tr><th width='30'>SL</th><th width='100'>Name 6</th><th width='80'>Name 1</th><th width='80'>Name 2</th><th width='80'>Name 3</th><th width='80'>Name 4</th><th width='80'>Name 5</th><th width='80'>DOB</th><th width='80'>Country</th><th width='80'>Listed On</th><th width='80'>Last Updated</th></tr></thead><tbody></tbody></table>");

		var tbody = $('#flex1 > tbody');

		errorList = $(errors);
		errorList.each(function (idx) {
			var uploadSanctions = errorList[idx];
			tbody.append($('<tr onclick="showCsvError(this.id)"></tr>').attr('id', uploadSanctions.serial)
					.append($('<td></td>').html(uploadSanctions.serial))
					.append($('<td></td>').html(uploadSanctions.name6 ? uploadSanctions.name6 : ''))
					.append($('<td></td>').html(uploadSanctions.name1 ? uploadSanctions.name1 : ''))
					.append($('<td></td>').html(uploadSanctions.name2 ? uploadSanctions.name2 : ''))
					.append($('<td></td>').html(uploadSanctions.name3 ? uploadSanctions.name3 : ''))
					.append($('<td></td>').html(uploadSanctions.name4 ? uploadSanctions.name4 : ''))
					.append($('<td></td>').html(uploadSanctions.name5 ? uploadSanctions.name5 : ''))
					.append($('<td></td>').html(uploadSanctions.dob ? uploadSanctions.dob : ''))
					.append($('<td></td>').html(uploadSanctions.country ? uploadSanctions.country : ''))
					.append($('<td></td>').html(uploadSanctions.strCreatedOn ? uploadSanctions.strCreatedOn : ''))
					.append($('<td></td>').html(uploadSanctions.strLastUpdate ? uploadSanctions.strLastUpdate : ''))
			);
		});

		$('#flex1').flexigrid({
			singleSelect: true,
			title: 'Following sanctions contain error',
			afterAjax: function () {
				showLoadingSpinner(false);// Spinner hide after AJAX Call
			}
		});

		$('#csvErrorDiv_Option_Pane').show();
	}

	function showCsvError(id) {
		$('#errorListShow').hide();
		if (errorList == false) {
			showError('No error to display');
			return;
		}
		var selectedRows = $('table#flex1 > tbody > tr.trSelected');
		try {
			if (id) {
				for (i = 0; i < errorList.length; i++) {
					var sanction = errorList[i];
					if (sanction.serial == id) {
						$('#csv-error-list').html('');
						var sanctionErrors = $(sanction.errors);
						sanctionErrors.each(function (idx) {
							$('#csv-error-list').append($('<li class="error"></li>').html(sanctionErrors[idx]));
						});
						$('#errorListShow').show();
						break;
					}
				}
			}
			return;
		} catch (e) {
		}
	}

</script>

<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Upload Sanction File
		</div>
	</div>
	<g:form id='sanctionUploadFrm' name='sanctionUploadFrm' class="form-horizontal form-widgets" role="form"
	        enctype="multipart/form-data">
		<div class="panel-body">
			<div class="form-group">

				<div class="form-group">
					<label class="col-md-2 control-label label-optional"
					       for="sanctionFile">Sanction File:</label>

					<div class="col-md-3">
						<input type="file" id="sanctionFile" name="sanctionFile" validationMessage="Required"/>
					</div>

					<div class="col-md-2 pull-left">
						<span class="k-invalid-msg" data-for="sanctionFile"></span>
					</div>
				</div>

			</div>
		</div>


		<div class="panel-footer">

			<button id="uploadButton" name="uploadButton" type="submit" data-role="button"
			        class="k-button k-button-icontext"
			        role="button" tabindex="3"
			        aria-disabled="false"><span class="k-icon k-i-plus"></span>Upload Sanction(s)
			</button>

			<button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
			        class="k-button k-button-icontext" role="button" tabindex="4" onclick="resetForm();"
			        aria-disabled="false"><span class="k-icon k-i-close"></span>Cancel
			</button>

		</div>

	</g:form>
</div>

<div id='csvErrorDiv'>

</div>

<div id="errorDivDupRefNo" class="buttons" style="display: none;"></div>
