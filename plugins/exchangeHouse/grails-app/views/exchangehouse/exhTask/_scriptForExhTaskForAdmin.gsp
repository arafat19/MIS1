<script type="text/javascript">
	var modelJsonForTaskAdmin = ${modelJson};
	var taskGridModel;
	var paramForSearch, dropDownBank;
	var createdDateFrom, createdDateTo;
	var dropDownTaskStatus,dropDownOutletBank;
	var statusNewTask,statusSentToBank;

	$(document).ready(function() {
		onLoadTask();
	});


	function onLoadTask() {
		try {
			initializeForm($('#filterTaskForm'),populateTask)

			statusNewTask = modelJsonForTaskAdmin.statusNewTask;
			statusSentToBank = modelJsonForTaskAdmin.statusSentToBank;

			initFlexGrid() ;

			$(document).attr('title', "ARMS - Show Task");
			loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showExhTaskForAdmin");

		} catch (e) {
			showError('Error occurred on page load');
		}
	}

	function populateTask() {
		var status = dropDownTaskStatus.value();
		if (!checkDates($('#createdDateFrom'), $('#createdDateTo'))) return false;
		if (status == '') {
			showError('Select task status');
			return false;
		}
		var bankId = dropDownBank.value();
		if (!bankId || bankId == '') {
			showError('Select bank');
			return false;
		}

		$('span.send').hide();
		$('span.do_search').hide();
		$('span.delete').hide();
		if (status == statusNewTask) {
			$('span.send').show();
			$('span.delete').show();
		}
		else if (status == statusSentToBank) {
			$('span.do_search').show();
		}

		createdDateFrom = $('#createdDateFrom').val();
		createdDateTo = $('#createdDateTo').val();

		paramForSearch = "?taskStatus=" + status + "&outletBankId="+ bankId + "&createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo;
		// First reset the grid
		var obj = getEmptyGridModel();
		$('#flex1').flexOptions({url:false}).flexAddData(obj);
		$('#flex1').flexOptions({url:false, query:''}).flexReload();
		var strUrl = "${createLink(controller: 'exhTask', action: 'listExhTaskForAdmin')}" + paramForSearch;
		showLoadingSpinner(true);	// Spinner Show on AJAX Call
		$('#flex1').flexOptions({url:strUrl, query:''}).flexReload();
		return false;
	}

    function populateBank(){
        if(dropDownTaskStatus.value()==''){
            dropDownBank.setDataSource(getKendoEmptyDataSource(dropDownBank,null));
            return false;
        }
        var fromDate=$("#createdDateFrom").val();
        var toDate=$("#createdDateTo").val();
        var taskStatus= dropDownTaskStatus.dataItem().reservedId;
        $('#bankId').attr('from_date', fromDate);
        $('#bankId').attr('to_date', toDate);
        $('#bankId').attr('task_status', taskStatus);
        $('#bankId').reloadMe();
    }

	function resetFilterTaskForm() {
		clearForm($('#filterTaskForm'));
	}

	function isEmpty(val) {
		var val2;
		val2 = $.trim(val);
		return (val2.length == 0);
	}

	function initFlexGrid() {
		$("#flex1").flexigrid
		(
				{
					url:false,
					dataType:'json',
					colModel:[
						{display:"Serial", name:"serial", width:40, sortable:false, align:"right"},
						{display:"ID", name:"id", width:30, sortable:false, align:"right", hide:true},
						{display:"Ref No", name:"refNo", width:100, sortable:true, align:"left"},
						{display:"Amount(BDT)", name:"amountInForeignCurrency", width:85, sortable:true, align:"right"},
						{display:"Amount("+ $('#hidLocalCurrency').val() + ")", name:"amountInLocalCurrency", width:85, sortable:true, align:"right"},
						{display:"Total Due", name:"total_due", width:80, sortable:false, align:"right"},
						{display:"Customer Name", name:"customerName", width:130, sortable:true, align:"left"},
						{display:"Beneficiary Name", name:"beneficiaryName", width:130, sortable:true, align:"left"},
						{display:"Payment Method", name:"paymentMethod", width:100, sortable:true, align:"left"},
						{display:"Regular Fee", name:"regularFee", width:80, sortable:true, align:"right"},
						{display:"Discount", name:"discount", width:70, sortable:true, align:"right"},
						{display: "Paid", name: "customerName", width: 100, sortable: true, align: "center"}
					],
					buttons:[
						{name:'Select All', bclass:'select-all', onpress:doSelectAll},
						{name:'Deselect All', bclass:'deselect-all', onpress:doDeselectAll},
						{name:'Send To Bank', bclass:'send', onpress:sendTaskToBank},
						{name:'Cancel Task', bclass:'delete', onpress:doCancelTask},
						{name:'Show Task Details', bclass:'do_search', onpress:viewTaskDetails},
						{name:'Show Invoice', bclass:'rename', onpress:viewInvoice},
						{name: 'Note', bclass: 'note', onpress: viewTaskNote},
						{name:'Clear Results', bclass:'clear-results', onpress:reloadGrid},
						{separator:true}
					],
					searchitems:[
						{display:"Ref No", name:"refNo", width:100, sortable:true, align:"left"},
						{display:"Customer Name", name:"customerName", width:180, sortable:true, align:"left"},
						{display:"Amount(BDT)", name:"amountInForeignCurrency", width:120, sortable:true, align:"left"}
					],
					sortname:"refNo",
					sortorder:"desc",
					usepager:true,
					singleSelect:false,
					title:'All Tasks',
					useRp:true,
					rp:15,
					showTableToggleBtn:false,
					//width: 725,
					height:getGridHeight()-15,
					initHidden:['do_search', 'send', 'cancel'],
					afterAjax:function () {
						showLoadingSpinner(false);// Spinner hide after AJAX Call
					}
				}
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

	function executePreConditionForSentToBank(selectedIds) { //
		if (selectedIds.length == 0) {
			showError("Please select a Task to send");
			return false;
		}

		if (!confirm('Are you sure you want to send ' + selectedIds.length + ' task(s) to Bank?')) {
			return false;
		}
		return true;

	}
	//
	function executePostConditionForSentToBank(ids) {

        $(ids).each(function (e) {
			$(this).remove();
		});
        $('#flex1').decreaseCount(ids.length);
	}

	function sendTaskToBank(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'task',false)==false){
            return;
        }
        var ids=getSelectedIdFromGrid($('#flex1'));
        var selectedIds = $('.trSelected', grid);

        if (!confirm('Are you sure you want to send ' + selectedIds.length + ' task(s) to Bank?')) {
            return false;
        }

        $('span.send').hide();

		showLoadingSpinner(true);
		$.ajax({
			type:'post',
			dataType:'json',
			url:"${createLink(controller: 'exhTask',action: 'sendToBank')}?ids=" + ids,
			success:function (data) {
				if (data.isError == false) {
					showSuccess(data.message);
					executePostConditionForSentToBank(selectedIds);
				} else {
					showError(data.message);
				}
			},
			complete:function (XMLHttpRequest, textStatus) {
				$('span.send').show();
				onCompleteAjaxCall();
			}
		});
	}

	// Cancel Task Script Block Start here
	function doCancelTask(com, grid) {
		if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
		$('#exhCancelTaskConfirmationModal').modal('show');    // show Modal
		return false;
	}

	function executeCancelTask(reason) {
        var selectedIds = $('.trSelected', $('#flex1'));
        var ids=getSelectedIdFromGrid($('#flex1'))
		$('span.cancel').hide();

		showLoadingSpinner(true);
		$.ajax({
			type:'post',
			dataType:'json',
			url:"${createLink(controller: 'exhTask',action: 'cancelSpecificTask')}?ids=" + ids + '&reason=' + reason,
			success:function (data) {
				if (data.isError == false) {
					showSuccess(data.message);
					executePostConditionForCancelTask(selectedIds);
				} else {
					showError(data.message);
				}
			},
			complete:function (XMLHttpRequest, textStatus) {
				$('span.cancel').show();
				onCompleteAjaxCall();
			}
		});
	}

	function executePostConditionForCancelTask(ids) {
        $(ids).each(function (e) {
			$(this).remove();
		});
		$('#cancellationReason').val('');
		$('#flex1').decreaseCount(ids.length);
	}
	// Cancel Task Script Block end here

	function viewInvoice(com, grid) {

        if (executeCommonPreConditionForSelect($('#flex1'),'task',true)==false) {
            return;
        }
        var taskId=getSelectedIdFromGrid($('#flex1'));
		showLoadingSpinner(true);
		var loc = "${createLink(controller: 'exhReport', action: 'showInvoiceFromTaskGrid')}?taskId=" + taskId;
		$.history.load(formatLink(loc));
		return false;
	}

	function viewTaskNote(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
        var taskId=getSelectedIdFromGrid($('#flex1'));
		showLoadingSpinner(true);
		var loc = "${createLink(controller: 'exhTask', action: 'showEntityNoteForTask')}?taskId=" + taskId;
		$.history.load(formatLink(loc));
		return false;
	}

	// view task details
	function viewTaskDetails(com, grid) {

        //var ids=getSelectedIdFromGrid($('#flex1'));
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'),'task',true)==false) {
            return;
        }
        var securityNo = $(ids[ids.length - 1]).find("td").eq(2).find("div").html();
		var securityType = "Ref No";

		showLoadingSpinner(true);
		var loc = "${createLink(controller: 'exhTask', action: 'showTaskDetailsForAdmin')}?securityNo=" + securityNo + "&securityType=" + securityType + "&createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo;
		$.history.load(formatLink(loc));
		return false;
	}

	function reloadGrid(com, grid) {
		$('#flex1').flexOptions({query:''}).flexReload();
		populateGridButtons();
	}

	function populateGridButtons() {
		var status = $('#taskStatus').val();
		if (status == statusNewTask) {
			$('span.send').show();
			$('span.cancel').show();
			$('span.do_search').hide();
		}
		else if (status == statusSentToBank) {
			$('span.send').hide();
			$('span.cancel').hide();
			$('span.do_search').show();
		} else if (status == statusCancelledTask) {
			$('span.send').hide();
			$('span.cancel').hide();
			$('span.do_search').hide();
		}
	}

	//    window.onload = loadFlexGrid();
	function loadFlexGrid() {

		var strUrl = "${createLink(controller: 'exhTask', action: 'listExhTaskForAdmin')}" + paramForSearch;
		$("#flex1").flexOptions({url:strUrl});
		if (taskGridModel) {
			$("#flex1").flexAddData(taskGridModel);
			populateGridButtons();
		}
	}

</script>