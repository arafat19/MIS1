<script type="text/javascript">

	var modelJsonShowTaskOtherBank = ${modelJson}, dropDownTaskStatus, dropDownBank;
	var createdDateFrom, createdDateTo;

	var statusSendToOtherBank, statusResolvedByOtherBank;


	$(document).ready(function () {
		onLoadTask();
	});
	function populateTask() {

		if (!checkDates($('#createdDateFrom'), $('#createdDateTo'))) return false;
		var status = dropDownTaskStatus.value();
		if (status == '') {
			showError('Select task status');
			return false;
		}
		var bankId = dropDownBank.value();
		if (bankId == '') {
			showError('Select bank');
			return false;
		}

		if (status == statusSendToOtherBank) {
			$('span.send').show();
		} else {
			$('span.send').hide();
		}

		createdDateFrom = $('#createdDateFrom').val();
		createdDateTo = $('#createdDateTo').val();

		var paramForSearch = "?taskStatus=" + $('#taskStatus').val() + "&outletBankId=" + bankId + "&createdDateFrom=" + $('#createdDateFrom').val() + "&createdDateTo=" + $('#createdDateTo').val();

		// First reset the grid
		$('#flex1').flexAddData(getEmptyGridModel());
		$("#flex1").flexOptions({url: ''});
		var strUrl = "${createLink(controller: 'exhTask', action: 'listForOtherBankUser')}" + paramForSearch;
		showLoadingSpinner(true);	// Spinner Show on AJAX Call
		$('#flex1').flexOptions({url: strUrl, query: ''}).flexReload();
		return false;
	}

	function onLoadTask() {
		try {
			initializeForm($('#filterTaskForm'), populateTask)

			if (modelJsonShowTaskOtherBank.isError) {
				showError(modelJsonShowTaskOtherBank.message);
				return false;
			}
			statusSendToOtherBank =  modelJsonShowTaskOtherBank.statusSentToOtherBank;
			$('#printBtn').click(function () {
				downloadCsv();
			});
			initFlexGrid();

			// update page title
			$(document).attr('title', "ARMS - Show Task");
			loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showForOtherBankUser");

		} catch (e) {
			showError('Error occurred on page load');
		}
	}

	function downloadCsv() {

		if (!checkDates($('#createdDateFrom'), $('#createdDateTo'))) return false;
		var status = dropDownTaskStatus.value();
		if (status == '') {
			showError('Select task status');
			return false;
		}
		var bankId = dropDownBank.value();
		if (bankId == '') {
			showError('Select bank');
			return false;
		}

		showLoadingSpinner(true);
		var params = "?createdDateFrom=" + $('#createdDateFrom').val() + "&createdDateTo=" + $('#createdDateTo').val() + "&taskStatus=" + status + "&outletBankId=" + bankId;
		if (confirm('Do you want to download the CSV now?')) {
			var url = "${createLink(controller: 'exhTask', action: 'downloadCsvForOtherBank')}" + params;
			document.location = url;
		}
		showLoadingSpinner(false);
	}

	function isEmpty(val) {
		var val2;
		val2 = $.trim(val);
		return (val2.length == 0);
	}

	function initFlexGrid() {
		$("#flex1").flexigrid({
					url: false,
					dataType: 'json',
					colModel: [
						{display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
						{display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
						{display: "Ref No", name: "refNo", width: 100, sortable: true, align: "left"},
						{display: "Amount(BDT)", name: "amountInForeignCurrency", width: 90, sortable: true, align: "right"},
						{display: "Amount(" + $('#hidLocalCurrency').val() + ")", name: "amountInLocalCurrency", width: 90, sortable: true, align: "right"},
						{display: "Total Due", name: "total_due", width: 90, sortable: false, align: "right"},
						{display: "Customer Name", name: "customerName", width: 150, sortable: true, align: "left"},
						{display: "Beneficiary Name", name: "beneficiaryName", width: 150, sortable: true, align: "left"},
						{display: "Payment Method", name: "paymentMethod", width: 100, sortable: true, align: "left"},
						{display: "Regular Fee", name: "regularFee", width: 80, sortable: true, align: "right"},
						{display: "Discount", name: "discount", width: 80, sortable: true, align: "right"}
					],
					buttons: [
						{name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
						{name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
						{name: 'Mark As Resolved ', bclass: 'send', onpress: resolveTask},
						{name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
						{separator: true}
					],
					searchitems: [
						{display: "Ref No", name: "refNo", width: 100, sortable: true, align: "left"},
						{display: "Customer Name", name: "customerName", width: 180, sortable: true, align: "left"},
						{display: "Amount(BDT)", name: "amountInForeignCurrency", width: 120, sortable: true, align: "left"}
					],
					sortname: "refNo",
					sortorder: "desc",
					usepager: true,
					singleSelect: false,
					title: 'All Tasks',
					useRp: true,
					rp: 15,

					showTableToggleBtn: false,
					//width: 725,
					height: getGridHeight(),
					initHidden: ['send'],
					afterAjax: function () {
						showLoadingSpinner(false);// Spinner hide after AJAX Call
					}}
		);
	}


	function doSelectAll(com, grid) {
		try {
			var rows = $('table#flex1 > tbody > tr');
			if (rows && rows.length > 0) {
				rows.addClass('trSelected');
			}
		} catch (e) {
		}
	}
	function doDeselectAll(com, grid) {
		try {
			var rows = $('table#flex1 > tbody > tr');
			if (rows && rows.length > 0) {
				rows.removeClass('trSelected');
			}
		} catch (e) {
		}
	}

	function executePreConditionForResolveTask(selectedIds) {
		if (selectedIds.length == 0) {
			showError("Please select task to resolve");
			return false;
		}

		if (!confirm('Are you sure you want to resolve ' + selectedIds.length + ' task(s)?')) {
			return false;
		}
		return true;
	}

	function executePostConditionForResolveTask(selectedIds) {
		selectedIds.each(function (e) {
			$(this).remove();
		});
		$('#flex1').decreaseCount(selectedIds.length);
	}

	function resolveTask(com, grid) {
		var selectedIds = $('.trSelected', grid);

		if (!executePreConditionForResolveTask(selectedIds)) {
			return;
		}

		var ids = '';
		selectedIds.each(function (e) {
			var id = $(this).attr('id').replace('row', '');
			ids += id + '_';
		});


		showLoadingSpinner(true);
		$.ajax({
			type: 'post',
			dataType: 'json',
			url: "${createLink(controller: 'exhTask',action: 'resolveTaskForOtherBank')}?ids=" + ids,
			success: function (data) {
				if (data.isError == false) {
					showSuccess(data.message);
					executePostConditionForResolveTask(selectedIds);
				} else {
					showError(data.message);
				}
			},
			complete: function (XMLHttpRequest, textStatus) {
				onCompleteAjaxCall();
			}
		});
	}

	/* function executeCommonPreConditionForGrid(selectedIds) {
	 if (selectedIds.length == 0) {
	 showError("Please select a row to perform this operation");
	 return false;
	 } else if (selectedIds.length > 1) {
	 showError("Multiple rows can not be selected for this operation.");
	 return false;
	 } else {
	 return true;
	 }
	 }*/

	function reloadGrid(com, grid) {
		$('#flex1').flexOptions({query: ''}).flexReload();
		populateGridButtons();
	}

	function populateGridButtons() {
		var status = dropDownTaskStatus.value();
		if (status == statusSendToOtherBank) {
			$('span.send').show();
		}
		else {
			$('span.send').hide();
		}
	}
    function populateBank(){
        if(dropDownTaskStatus.value()==''){
            dropDownBank.setDataSource(getKendoEmptyDataSource(dropDownBank,null));
            return false;
        }
        var fromDate=$("#createdDateFrom").val();
        var toDate=$("#createdDateTo").val();
        var taskStatus= dropDownTaskStatus.dataItem().reservedId;
        $('#outletBankId').attr('from_date', fromDate);
        $('#outletBankId').attr('to_date', toDate);
        $('#outletBankId').attr('task_status', taskStatus);
        $('#outletBankId').reloadMe();
    }

</script>