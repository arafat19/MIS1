<script type="text/javascript">
	var jsonBeneficiaryList, strBeneficiaryGridUrl, verifierUserName, jsonLinkedBeneficiaryList, isSanctionStatus;
	var modelJson;
	$(document).ready(function (e) {
		modelJson = ${modelJson};
		onLoadDefaultData();
		if (modelJson.isError) {
			showError(modelJson.message);
			return false;
		}
	});

	function onLoadDefaultData() {
		initializeForm($('#basicInfoForm'), validateBasicInfo);
		initializeForm($('#disbursementInfoForm'));
		initializeForm($('#linkedTransactionForm'), validateLinkedTransaction);

		jsonBeneficiaryList = modelJson.beneficiaryListJSON ? modelJson.beneficiaryListJSON : '';
		verifierUserName = modelJson.userName ? modelJson.userName : '';
		isSanctionStatus = modelJson.isSanctionStatus;
		if (isSanctionStatus == true) {
			$('#lblSanctionException').addClass('label-holder-req');
		}
		var customerId = modelJson.customerId ? modelJson.customerId : '';
		var customerCode = modelJson.customerCode ? modelJson.customerCode : '';
		var customerName = modelJson.customerName ? modelJson.customerName : '';
		if (customerId) {
			$('#customerId').val(customerId);
			$('#customerName').val(customerName);
			$('#lblCustomerId').text(customerCode);
			$('#lblCustomerName').text(customerName);
			strBeneficiaryGridUrl = "${createLink(controller: 'exhBeneficiary', action: 'list')}?customerId=" + customerId;
		} else {
			strBeneficiaryGridUrl = "${createLink(controller: 'exhBeneficiary', action: 'list')}";
		}

		initBeneficiaryGrid();
		initLinkedTransactionGrid();
		bindEventsWithControl();
		loadFlexGrid();

		// update page title
		$(document).attr('title', "ARMS - Create Beneficiary");
		loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhBeneficiary/show");
	}

	function bindEventsWithControl() {
		$('#isSanctionException').click(function () {
			showVerifierName(verifierUserName);
		});
		$('#isLinkedTransactionVerified').click(function () {
			verifyLinkedTransaction();
		});
	}


	function showVerifierName(varifierName) {
		if ($('#isSanctionException').attr("checked")) {
			if (getSanctionCountForBeneficiary() == false) {
				$('#isSanctionException').attr("checked", false);
			}
		} else {
			$('#varifierNameDisplay').text('');
		}
	}


	function getSanctionCountForBeneficiary() {
		if (!validateForm($("#basicInfoForm"))) {
			return false;
		}

		// Spinner Show on AJAX Call
		showLoadingSpinner(true);
		var actionUrl = "${createLink(controller: 'exhSanction', action: 'sanctionCountFromBeneficiary')}";

		setButtonDisabled($('.save'), true);

		jQuery.ajax({
			type: 'post',
			data: jQuery("#basicInfoForm").serialize(),
			url: actionUrl,
			success: function (data, textStatus) {
				executePostConditionForForSanctionCount(data);
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
			},
			complete: function (XMLHttpRequest, textStatus) {
				showLoadingSpinner(false);
				setButtonDisabled($('.save'), false);
			},
			dataType: 'json'
		});

		return true;
	}

	function executePostConditionForForSanctionCount(data) {
		var verificationMsg = '  by ' + verifierUserName + "<span style='color: green;'>" + ' ( No match found )' + "</span>";

		if (data.isError == false) {
			// create link to sanction list
			var fName = $('#firstName').val();
			var mName = $('#middleName').val();
			var lName = $('#lastName').val();
			$('#hidFirstName').val(fName);
			$('#hidMiddleName').val(mName);
			$('#hidLastName').val(lName);

			var loc = '/exhSanction/showFromBeneficiary?fName=' + fName + '&mName=' + mName + '&lName=' + lName;
			loc = encodeURI(loc);
			if (data.count > 0) {
                verificationMsg = '  by ' + verifierUserName + '<a target="_blank" style="color: red;" title="Click to view details" href=' + loc + '> ( ' + data.count + ' match(es) found )</a>';
			}
		} else {
			verificationMsg = "<span style='color: red;'>  " + data.message + " </span>";
		}

		$('#varifierNameDisplay').html(verificationMsg);

	}
	/********************************end of retriving sanction info for a beneficiary***************************************/


	function onSubmitBeneficiary() {

		if (executePreCondition() == false) {
			return false;
		}
		if (!isConfirmedCreateNewBeneficiary()) {
			return false;
		}
		// Spinner Show on AJAX Call
		showLoadingSpinner(true);

		var actionUrl = null;
		if ($('#id').val().isEmpty()) {
			actionUrl = "${createLink(controller: 'exhBeneficiary', action: 'create')}";
		} else {
			actionUrl = "${createLink(controller: 'exhBeneficiary', action: 'update')}";
		}
		setButtonDisabled($('.save'), true);
		var formData = $('#basicInfoForm').serializeArray();
		formData = formData.concat($('#disbursementInfoForm').serializeArray());
		formData = formData.concat($('#linkedTransactionForm').serializeArray());
		jQuery.ajax({
			type: 'post',
			data: formData,
			url: actionUrl,
			success: function (data, textStatus) {
				executePostCondition(data);
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
			},
			complete: function (XMLHttpRequest, textStatus) {
				showLoadingSpinner(false);
				setButtonDisabled($('.save'), false);
			},
			dataType: 'json'
		});
		return false;
	}


	function executePostCondition(result) {
		if (result.isError) {
			showError(result.message);
		} else {
			try {
				if ($('#id').val().isEmpty() && result.entity != null) { // newly created
					var previousTotal = parseInt(jsonBeneficiaryList.total);

					// re-arranging serial
					var firstSerial = 1;
					if (jsonBeneficiaryList.rows.length > 0) {
						firstSerial = jsonBeneficiaryList.rows[0].cell[0];
						regenerateSerial($(jsonBeneficiaryList.rows), 0);
					}
					result.entity.cell[0] = firstSerial;

					jsonBeneficiaryList.rows.splice(0, 0, result.entity);

					if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
						jsonBeneficiaryList.rows.pop();
					}

					jsonBeneficiaryList.total = ++previousTotal;
					$("#flex1").flexAddData(jsonBeneficiaryList);

				} else if (result.entity != null) { // updated existing
					updateListModel(jsonBeneficiaryList, result.entity, 0);
					$("#flex1").flexAddData(jsonBeneficiaryList);
				}

				resetForm();
				showSuccess(result.message);

			} catch (e) {
			}
		}
	}

	function executePreCondition() {
		if (!validateBasicInfo()) {
			return false;
		}

		if (!validateLinkedTransaction()) {
			return false;
		}
		return true;
	}

	function validateLinkedTransaction() {
		if (!validateForm($("#linkedTransactionForm"))) {
			$('#beneficiaryTabs a[href="#fragmentLinkedTransaction"]').tab('show');
			return false;
		}

		var linkedChecked = $('#isLinkedTransactionVerified').attr("checked");
		if (!linkedChecked) {
			showError("Please confirm the linked transaction information is verified");
			$('#beneficiaryTabs a[href="#fragmentLinkedTransaction"]').tab('show');
			return false;
		}
		return true;
	}

	function validateBasicInfo() {
		if (!validateForm($("#basicInfoForm"))) {
			$('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
			return false;
		}


		if (isEmpty($('#customerId').val())) {
			$('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
			showError('Please select a customer');
			return false;
		}


		if ((($('#firstName').val().isEmpty()))) {
			$('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
			$('#firstName').focus();
			showError("Beneficiary name is required!");
			return false;
		} else if ((($('#relation').val().isEmpty()))) {
			$('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
			$('#relation').focus();
			showError("Relation is required");
			return false;
		}

		if ((isSanctionStatus == true) && (!$('#isSanctionException').attr("checked"))) {
			$('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
			showError("Please confirm the beneficiary as verified");
			return false;
		}

		if ((($('#firstName').val() != $('#hidFirstName').val()) ||
				($('#middleName').val() != $('#hidMiddleName').val()) ||
				($('#lastName').val() != $('#hidLastName').val())) && (isSanctionStatus == true)) {
			$('#isSanctionException').attr("checked", false);
			$('#varifierNameDisplay').text('');
			$('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
			showError("Change detected in one or more beneficiary name. Please verify again!");
			return false;
		}
		return true;
	}

	function initBeneficiaryGrid() {
		$("#flex1").flexigrid
		(
				{
					url: false,
					dataType: 'json',
					colModel: [
						{display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
						{display: "ID", name: "id", width: 100, sortable: true, align: "right", hide: true},
						{display: "Full Name", name: "full_name", width: 200, sortable: true, align: "left"},
						{display: "Approved", name: "approved", width: 65, sortable: true, align: "center"},
						{display: "Bank Info", name: "bankInfo", width: 240, sortable: false, align: "left"},
						{display: "A/C No", name: "accountNo", width: 150, sortable: true, align: "left"},
						{display: "Photo ID Type", name: "photoIdType", width: 100, sortable: false, align: "left"},
						{display: "Updated By", name: "updatedBy", width: 110, sortable: false, align: "left"},
						{display: "Updated On", name: "updatedOn", width: 110, sortable: false, align: "left"}
					],
					buttons: [
						{name: 'Edit', bclass: 'edit', onpress: editBeneficiary},
						<sec:access url="/exhTask/showExhTaskForCashier">
						{name: 'Create Task', bclass: 'rename', onpress: doCreateTask},
						</sec:access>
						<sec:access url="/exhBeneficiary/approveBeneficiary">
						{name: 'Approve', bclass: 'approve', onpress: approveBeneficiary},
						</sec:access>
						<sec:access url="/exhTask/showForAgent">
						{name: 'Create Task', bclass: 'rename', onpress: doCreateTaskForAgent},
						</sec:access>
						{name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
						{separator: true}
					],
					searchitems: [
						{display: "Full Name", name: "full_name", width: 180, sortable: true, align: "left"},
						{display: "Account No", name: "account_no", width: 180, sortable: true, align: "left"}
					],
					sortname: "approved",
					sortorder: "asc",
					usepager: true,
					singleSelect: true,
					title: 'All Beneficiaries',
					useRp: true,
					rp: 20,
					showTableToggleBtn: false,
					height: getGridHeight() - 15,
					afterAjax: function () {
						showLoadingSpinner(false);// Spinner hide after AJAX Call
					}
				}
		);
	}

	function resetForm() {
		clearForm($("#basicInfoForm"));
		clearForm($("#disbursementInfoForm"));
		clearForm($("#linkedTransactionForm"));

		setButtonDisabled($('.save'), false);
		$('#customerId').val(modelJson.customerId);
		$('#customerName').val(modelJson.customerName);
		$('#lblCustomerName').text(modelJson.customerName);
		$('#lblCustomerId').text(modelJson.customerCode);
		clearGrid();// empty the data from flexGridForLinkedBeneficiary
		$('#isLinkedTransactionVerified').attr('checked', false);
		$('#beneficiaryTabs a:first').tab('show') // Select first tab
		$("#create").html("<span class='k-icon k-i-plus'></span>Create");
		showVerifierName("");
	}

	<sec:access url="/exhTask/showExhTaskForCashier">
	function doCreateTask(com, grid) {
		var ids = $('.trSelected', grid);
		if (executePreConditionForCreateTask(ids) == false) {
			return;
		}
		showLoadingSpinner(true);
		var beneficiaryId = $(ids[ids.length - 1]).attr('id').replace('row', '');
		var customerId = $('#customerId').val();
		var loc = "${createLink(controller: 'exhTask', action: 'showExhTaskForCashier')}?beneficiaryId=" + beneficiaryId + "&customerId=" + customerId;
		$.history.load(formatLink(loc));
		return false;
	}
	</sec:access>

	<sec:access url="/exhTask/showForAgent">
	function doCreateTaskForAgent(com, grid) {
		var ids = $('.trSelected', grid);
		if (executePreConditionForCreateTask(ids) == false) {
			return;
		}
		showLoadingSpinner(true);
		var beneficiaryId = $(ids[ids.length - 1]).attr('id').replace('row', '');
		var customerId = $('#customerId').val();
		var loc = "${createLink(controller: 'exhTask', action: 'showForAgent')}?beneficiaryId=" + beneficiaryId + "&customerId=" + customerId;
		$.history.load(formatLink(loc));
		return false;
	}
	</sec:access>


	function executePreConditionForApprove(ids) {
		if (ids.length == 0) {
			showError("Please select a beneficiary to approve");
			return false;
		}
		var approvedStatus = $(ids[ids.length - 1]).find("td").eq(3).find("div").html();
		if (approvedStatus == 'YES' || approvedStatus == 'yes' || approvedStatus == 'Yes') {
			showInfo("Beneficiary is already approved.");
			return false;
		}
		return true;
	}

	function executePostConditionForApproveBeneficiary(data) {
		updateListModel(jsonBeneficiaryList, data.entity, 0);
		$("#flex1").flexAddData(jsonBeneficiaryList);
		showSuccess(data.message);
		onCompleteAjaxCall();
	}

	function approveBeneficiary(com, grid) {
		var ids = $('.trSelected', grid);
		if (!executePreConditionForApprove(ids)) {
			return false;
		}

		var beneficiaryId = $(ids[ids.length - 1]).attr('id').replace('row', '');
		showLoadingSpinner(true);
		$.ajax({
			type: 'post',
			dataType: 'json',
			url: "${createLink(controller: 'exhBeneficiary',action: 'approveBeneficiary')}?id=" + beneficiaryId,
			success: function (data, textStatus) {
				executePostConditionForApproveBeneficiary(data);
			},
			complete: function (XMLHttpRequest, textStatus) {
				onCompleteAjaxCall();
			}
		});
		return false;
	}

	function checkBeneficiaryApproved(ids) {
		var approvedStatus = $(ids[ids.length - 1]).find("td").eq(3).find("div").html();
		if (approvedStatus == 'NO' || approvedStatus == 'No' || approvedStatus == 'no') {
			showInfo("Beneficiary is not approved.");
			return false;
		}
		return true;
	}

	function executePreConditionForCreateTask(ids) {
		if (ids.length == 0) {
			showError("Please select a beneficiary to create task");
			return false;
		}

		if (!checkBeneficiaryApproved(ids)) {
			return false;
		}
		return true;
	}

	function editBeneficiary(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'beneficiary')==false){
            return;
        }
		resetForm();
		showLoadingSpinner(true);
        var beneficiaryId= getSelectedIdFromGrid($('#flex1'))
		$.ajax({
			url: "${createLink(controller: 'exhBeneficiary', action: 'edit')}?id=" + beneficiaryId,
			success: executePostConditionForEdit,
			complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
			dataType: 'json',
			type: 'post'
		});
	}


	function executePostConditionForEdit(data) {
		if (data.entity == null) {
			showError(result.message);
		} else {
			populateCustomer(data);
		}
	}

	function populateCustomer(data) {
		var entity = data.entity;
		$('#id').val(entity.id);
		$('#version').val(data.version);
		$('#hidBeneficiaryName').val(data.fullName);

		$('#customerId').val(modelJson.customerId);
		$('#customerName').val(modelJson.customerName);
		$('#lblCustomerName').text(modelJson.customerName);
		$('#lblCustomerId').text(modelJson.customerCode);

		$('#firstName').val(entity.firstName);
		$('#middleName').val(entity.middleName);
		$('#lastName').val(entity.lastName);
		$('#isSanctionException').attr('checked', false);
		$('#isLinkedTransactionVerified').attr('checked', false); // force to re-check again
		//$('#isLinkedTransactionVerified').attr('disabled', 'disabled');
		$('#accountNo').val(entity.accountNo);
		$('#phone').val(entity.phone);
		$('#relation').val(entity.relation);
		$('#email').val(entity.email);
		$('#address').val(entity.address);
		$('#bank').val(entity.bank);
		$('#bankBranch').val(entity.bankBranch);
		$('#district').val(entity.district);
		$('#thana').val(entity.thana);
		$('#photoIdType').val(entity.photoIdType);
		$('#photoIdNo').val(entity.photoIdNo);
		$("#create").html("<span class='k-icon k-i-plus'></span>Update");
	}

	function reloadGrid(com, grid) {
		if (com == 'Clear Results') {
			$('#flex1').flexOptions({query: ''}).flexReload();
		}
	}

	$('b.top').click
	(
			function () {
				$(this).parent().toggleClass('fh');
			}
	);
	window.onload = loadFlexGrid();
	function loadFlexGrid() {
		$("#flex1").flexOptions({url: strBeneficiaryGridUrl});
		if (jsonBeneficiaryList) {
			$("#flex1").flexAddData(jsonBeneficiaryList);
		}
	}

	function executePostConditionForLinkedTransaction(data) {

		if (data.entity == null) {
			showError(data.message);
			return false;
		}

		var previousTotal = parseInt(jsonBeneficiaryList.total);

		// re-arranging serial
		var firstSerial = 1;
		if (jsonBeneficiaryList.rows.length > 0) {
			firstSerial = jsonBeneficiaryList.rows[0].cell[0];
			regenerateSerial($(jsonBeneficiaryList.rows), 0);
		}
		data.entity.cell[0] = firstSerial;

		jsonBeneficiaryList.rows.splice(0, 0, data.entity);

		if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
			jsonBeneficiaryList.rows.pop();
		}

		jsonBeneficiaryList.total = ++previousTotal;
		$("#flex1").flexAddData(jsonBeneficiaryList);

		resetForm();
		showSuccess(data.message);

		return false;
	}

	function executePreConditionForLinkedTransaction(ids) {
		if (ids.length == 0) {
			showError("Please select a beneficiary to link with the customer");
			return false;
		}
		return true;
	}

	function linkBeneficiary(com, grid) {
		var ids = $('.trSelected', grid);
		if (executePreConditionForLinkedTransaction(ids) == false) {
			return false;
		}
		var existingBeneficiaryId = $('#id').val();
		var existingBeneficiaryName = $('#hidBeneficiaryName').val();
		var linkedBeneficiaryName = $(ids[0]).find("td").eq(2).text();

		if (existingBeneficiaryId > 0) {
			var msg = "Are you sure you want to replace existing beneficiary with " + linkedBeneficiaryName + " ?" +
					"\n\nPlease note that:\n" + "1. All other customers (if any) linked with this beneficiary should point to " + linkedBeneficiaryName + "." +
					"\n2. All tasks (if any) linked with this beneficiary should point to " + linkedBeneficiaryName + "." +
					"\n3. Existing beneficiary '" + existingBeneficiaryName + "' will be deleted." +
					"\n\n Agreed ?"
			if (!confirm(msg)) {
				return false;
			}
		} else {
			if (!confirm("Are you sure you want to link this customer with " + linkedBeneficiaryName + " ?")) {
				return false;
			}
			existingBeneficiaryId = 0;
		}

		showLoadingSpinner(true);
		var beneficiaryId = $(ids[ids.length - 1]).attr('id').replace('row', '');
		var customerId = $('#customerId').val();
		$.ajax({
			url: "${createLink(controller: 'exhCustomerBeneficiary', action: 'create')}?beneficiaryId=" +
					beneficiaryId + "&customerId=" + customerId + "&existingBeneficiaryId=" + existingBeneficiaryId,
			success: executePostConditionForLinkedTransaction,
			complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
			dataType: 'json',
			type: 'post'
		});
		return false;
	}
	function clearGrid() {
		jsonLinkedBeneficiaryList = getEmptyGridModel();
		$("#flexGridForLinkedBeneficiary").flexOptions({url: false}).flexAddData(getEmptyGridModel());
		$('#linkedTransactionInfo').text('');
		return true;
	}

	function isConfirmedCreateNewBeneficiary() {
		if (jsonLinkedBeneficiaryList != null && jsonLinkedBeneficiaryList.total > 0) {
			var id = $('#id').val();
			var msg;
			if (id > 0) {
				msg = '' + jsonLinkedBeneficiaryList.total + ' linked transaction(s) found!\nAre you sure you want to update existing beneficiary?';
			} else {
				msg = '' + jsonLinkedBeneficiaryList.total + ' linked transaction(s) found!\nAre you sure you want to create a new beneficiary?';
			}
			if (!confirm(msg)) {
				return false;
			}
		}

		return true;
	}

	function postConditionOnListLinkedBeneficiary(data, textStatus) {
		if (data) {
			jsonLinkedBeneficiaryList = data;
			(data.total >= 1) ? $('#linkedTransactionInfo').text(data.total + " match(es) found") : $('#linkedTransactionInfo').text('No match found');
			$("#flexGridForLinkedBeneficiary").flexAddData(data);
		}
	}
	function verifyLinkedTransaction() {
		$('#linkedTransactionInfo').text('');
		clearGrid();
		var linkedChecked = $('#isLinkedTransactionVerified').attr("checked");
		if (!linkedChecked) return false;

		var accountNo = $('#accountNo').val();
		var phone = $('#phone').val();
		var email = $('#email').val();
		var id = $('#id').val();
		if (isEmpty(accountNo) && isEmpty(phone) && isEmpty(email)) {
			return false;
		}

		var params = "?accountNo=" + accountNo + "&phone=" + phone + "&email=" + email;
		if (id > 0) params = params + "&beneficiaryId=" + id;
		var actionUrl = '${createLink([controller: "exhBeneficiary", action: "listLinkedBeneficiary"])}' + params;
		$("#flexGridForLinkedBeneficiary").flexOptions({url: actionUrl});
		showLoadingSpinner(true);
		jQuery.ajax({
			type: 'post',
			url: actionUrl,
			success: postConditionOnListLinkedBeneficiary,
			complete: function (XMLHttpRequest, textStatus) {
				showLoadingSpinner(false);
			},
			dataType: 'json'
		});
		return false;
	}
	function initLinkedTransactionGrid() {
		$("#flexGridForLinkedBeneficiary").flexigrid
		(
				{
					url: false,
					dataType: 'json',
					colModel: [
						{display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
						{display: "ID", name: "id", width: 100, sortable: true, align: "right", hide: true},
						{display: "Name", name: "first_name", width: 170, sortable: true, align: "left"},
						{display: "Email", name: "email", width: 150, sortable: true, align: "left"},
						{display: "Phone", name: "phone", width: 120, sortable: true, align: "left"},
						{display: "A/C No", name: "account_no", width: 120, sortable: true, align: "left"},
						{display: "Bank Info", name: "bank_info", width: 200, sortable: false, align: "left"}
					],
					buttons: [
						{name: 'Link Beneficiary', bclass: 'edit', onpress: linkBeneficiary},
						{separator: true}
					],
					sortname: "first_name",
					sortorder: "asc",
					singleSelect: true,
					title: 'Matching Beneficiaries',
					useRp: true,
					rp: 10,
					showTableToggleBtn: false,
					height: 80,
					resizable: false,
					afterAjax: function (XMLHttpRequest, textStatus) {
						afterAjaxError(XMLHttpRequest, textStatus);
						showLoadingSpinner(false);
					}
				}
		);
	}
</script>