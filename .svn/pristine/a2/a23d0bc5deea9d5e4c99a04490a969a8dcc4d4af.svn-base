<script type="text/javascript">
    var modelJsonForTaskAdmin = ${modelJson};
    var taskGridModel;
    var paramForSearch;
    var createdDateFrom, createdDateTo;
    var dropDownTaskStatus,dropDownOutletBank;
    var statusNewTask, statusSentToBank, statusCancelledTask;

    $(document).ready(function() {
	    onLoadTask();
    });

    function onLoadTask() {
	    try {
		    initializeForm($('#filterTaskForm'),populateTask)

		    statusNewTask = modelJsonForTaskAdmin.statusNewTask;
		    statusSentToBank = modelJsonForTaskAdmin.statusSentToBank;
		    statusCancelledTask = modelJsonForTaskAdmin.statusCancelledTask;


		    initFlexGrid();

		    $(document).attr('title', "ARMS - Show Task");
		    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showAgentTaskForAdmin");

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
	    var bankId = dropDownOutletBank.value();
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
        var strUrl = "${createLink(controller: 'exhTask', action: 'listAgentTaskForAdmin')}" + paramForSearch;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex1').flexOptions({url:strUrl, query:''}).flexReload();
        return false;
    }
    function populateBank(){
        if(dropDownTaskStatus.value()==''){
            dropDownOutletBank.setDataSource(getKendoEmptyDataSource(dropDownOutletBank,null));
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

    function isEmpty(val) {
        var val2;
        val2 = $.trim(val);
        return (val2.length == 0);
    }

</script>