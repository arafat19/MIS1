<script type="text/javascript">
    var process, instrument;
    var dropDownTaskStatus, dropDownExchangeHouse, dropDownTaskList, statusDecisionApproved;
    var taskListModel = false;

    $(document).ready(function () {
        onLoadForwardOnlinePage();
    });
    // method called on page load
    function onLoadForwardOnlinePage() {
        initializeForm($("#filterPanelForwardOnlineForm"), onSubmitForwardOnlineFilterPanel);

        process = $("#hidProcessType").val();
        instrument = $("#hidInstrumentType").val();
        statusDecisionApproved = $("#hidDecisionApprovedStatus").val();
        $('#printCsvBtn').click(function () {
            downloadForwardOnline();
        });

        initFlex();

        // update page title
        $(document).attr('title', "ARMS - Forward Online");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsInstrument/showForForwardOnline");
    }

    // Populate exchange house list onClick of exchange house refresh button
    function populateExchangeHouse() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var currentStatus = dropDownTaskStatus.dataItem().reservedId;
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        $('#exhHouseId').attr('from_date', fromDate);
        $('#exhHouseId').attr('to_date', toDate);
        $('#exhHouseId').attr('task_status_list', currentStatus);
        $('#exhHouseId').reloadMe();
        dropDownTaskList.setDataSource(getKendoEmptyDataSource());
        dropDownTaskList.refresh();
    }

    // Populate task list onchange of exchange house dropdown
    function populateTaskList() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var currentStatus = dropDownTaskStatus.dataItem().reservedId;
        $('#taskListId').attr('from_date', fromDate);
        $('#taskListId').attr('to_date', toDate);
        $('#taskListId').attr('task_status_list', currentStatus);
        $('#taskListId').attr('exchange_house_id', exhHouseId);
        $('#taskListId').reloadMe();
    }
    // check pre condition before populate task list
    function executePreConditionForPopulateList() {
        if (dropDownTaskStatus.value() == '') {
            showError('Please select status.');
            return false;
        }
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        return true;
    }

    // method called  on submit of the filter panel form
    function onSubmitForwardOnlineFilterPanel() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var taskListId = dropDownTaskList.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var currentStatus = dropDownTaskStatus.value();
        var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&currentStatus=" + currentStatus + "&process=" + process + "&instrument=" + instrument;
        var strUrl = "${createLink(controller: 'rmsInstrument', action: 'listTaskForProcessInstrument')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex').flexOptions({url: strUrl, query: ''}).flexReload();
        executePostConditionForSubmit();
        return false;
    }
    function executePostConditionForSubmit() {
        $('span.disbursed').show();
        var status = dropDownTaskStatus.value();
        if (status == statusDecisionApproved) {
            $('span.disbursed').show();
        } else {
            $('span.disbursed').hide();
        }
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSearchTask() {
        if (dropDownTaskStatus.value() == '') {
            showError('Please select status.');
            return false;
        }
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        if (dropDownExchangeHouse.value() == '') {
            showError('Please select exchange house.');
            return false;
        }
        if (dropDownTaskList.value() == '') {
            showError('Please select task list.');
            return false;
        }
        return true;
    }

    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 40, sortable: false, align: "right", hide: true},
                        {display: "Ref No", name: "refNo", width: 120, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                        {display: "Created Date", name: "createdOn", width: 100, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 200, sortable: false, align: "left"},
                        {display: "Mapping Bank,Branch & District", name: "mapping", width: 280, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        {name: 'Disburse', bclass: 'disbursed', onpress: disbursed},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    searchitems: [
                        {display: "Ref No", name: "refNo", width: 180, sortable: true, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "refNo",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All Task of Forward Online',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 40,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }
    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            taskListModel = getEmptyGridModel();
        } else {
            taskListModel = data.gridObj;
        }
        $("#flex").flexAddData(taskListModel);
    }

    // method call for download
    function downloadForwardOnline() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the CSV now?')) {
            var exhHouseId = dropDownExchangeHouse.value();
            var taskListId = dropDownTaskList.value();
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var currentStatus = dropDownTaskStatus.value();
            var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&currentStatus=" + currentStatus + "&process=" + process + "&instrument=" + instrument;
            var url = "${createLink(controller: 'rmsInstrument', action: 'downloadTaskReportForForwardOnline')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    //select all rows of the grid
    function doSelectAll(com, grid) {
        try {
            var rows = $('table#flex > tbody > tr');
            if (rows && rows.length > 0) {
                rows.addClass('trSelected');
            }
        } catch (e) {
        }
    }
    // deselect all rows of the grid
    function doDeselectAll() {
        try {
            var rows = $('table#flex > tbody > tr');
            if (rows && rows.length > 0) {
                rows.removeClass('trSelected');
            }
        } catch (e) {
        }
    }
    function disbursed() {
        showLoadingSpinner(true);
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var currentStatus = dropDownTaskStatus.value();
        var params = "?taskIds=" + taskIds + "&currentStatus=" + currentStatus+"&processTypeId="+process;
        var strUrl = "${createLink(controller:'rmsTask',action: 'disburseRmsTask')}" + params;
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            success: executePostConditionForDisbursement,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostConditionForDisbursement(data) {

        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            taskListModel.total = parseInt(taskListModel.total) - 1;
            removeEntityFromGridRows(taskListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }
    function reloadGrid(){
        $('#flex').flexOptions({query:''}).flexReload();
    }

</script>