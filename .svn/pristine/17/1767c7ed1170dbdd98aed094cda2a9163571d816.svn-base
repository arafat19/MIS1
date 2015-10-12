<script type="text/javascript">
    var process, dropDownBank;
    var dropDownTaskStatus, dropDownExchangeHouse, dropDownTaskList, statusApproved;
    var taskListModel = false;

    $(document).ready(function () {
        onLoadPage();
    });
    // method called on page load
    function onLoadPage() {
        initializeForm($("#filterPanelInstrumentPurchaseForm"), onSubmitInstrumentPurchaseFilterPanel);

        process = $("#hidProcessType").val();
        statusApproved = $("#hidApprovedStatus").val();

        $('#printCsvBtn').click(function () {
            downloadInstrumentPurchaseCsv();
        });

        initFlex();

        // update page title
        $(document).attr('title', "ARMS - Purchase Instrument");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsInstrument/showForInstrumentPurchase");
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
        dropDownBank.setDataSource(getKendoEmptyDataSource());
        dropDownTaskList.refresh();
        dropDownBank.refresh();
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
        dropDownBank.setDataSource(getKendoEmptyDataSource());
        dropDownBank.refresh();
    }

    function populateBankList() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var taskListId = dropDownTaskList.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var currentStatus = dropDownTaskStatus.dataItem().reservedId;
        $('#bankId').attr('from_date', fromDate);
        $('#bankId').attr('to_date', toDate);
        $('#bankId').attr('task_status_list', currentStatus);
        $('#bankId').attr('task_list_id', taskListId);
        $('#bankId').reloadMe();
    }

    // method called  on submit of the filter panel form
    function onSubmitInstrumentPurchaseFilterPanel() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var taskListId = dropDownTaskList.value();
        var bankId = dropDownBank.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var currentStatus = dropDownTaskStatus.value();
        var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&currentStatus=" + currentStatus + "&process=" + process + "&bankId=" + bankId;
        var strUrl = "${createLink(controller: 'rmsInstrument', action: 'listTaskForProcessInstrument')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex').flexOptions({url: strUrl, query: ''}).flexReload();
        executePostConditionForSubmit();
        return false;
    }
    function executePostConditionForSubmit() {
        $('span.disbursed').show();
        $('span.delete').show();
        var status = dropDownTaskStatus.value();
        if (status == statusApproved) {
            $('span.disbursed').show();
            $('span.delete').show();
        } else {
            $('span.disbursed').hide();
            $('span.delete').hide();
        }
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSearchTask() {
        if (dropDownTaskStatus.value() == '') {
            showError('Please select task status');
            return false;
        }
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        if (dropDownExchangeHouse.value() == '') {
            showError('Please select exchange house');
            return false;
        }
        if (dropDownTaskList.value() == '') {
            showError('Please select task list');
            return false;
        }
        if (dropDownBank.value() == '') {
            showError('Please select bank');
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
                        {display: "Mapping Bank,Branch & District", name: "mapping", width: 280, sortable: false, align: "left"},
                        {display: "Instrument", name: "instrument", width: 150, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        {name: 'Revise', bclass: 'delete', onpress: addRevisionNote},
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
                    title: 'All Task of Instrument Purchase',
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
    function downloadInstrumentPurchaseCsv() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the CSV now?')) {
            var exhHouseId = dropDownExchangeHouse.value();
            var taskListId = dropDownTaskList.value();
            var bankId = dropDownBank.value();
            var currentStatus = dropDownTaskStatus.value();
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&process=" + process + "&currentStatus=" + currentStatus + "&bankId=" + bankId;
            var url = "${createLink(controller: 'rmsInstrument', action: 'downloadTaskReportForPurchaseInstrument')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    // Show modal
    function addRevisionNote() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return;
        }
        $('#reviseConfirmationModalTask').modal('show');    // show Modal
    }
    function executePreConditionForRevise() {
        $('#txtTaskReviseReason').val($.trim($('#txtTaskReviseReason').val()));
        if ($('#txtTaskReviseReason').val() == '') {
            showError('Revision note needed.');
            return false;
        }
        return true;
    }
    // Revise task
    function reviseTask() {
        if (executePreConditionForRevise() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var revisionNote = $('#txtTaskReviseReason').val();
        var currentStatus = dropDownTaskStatus.value();
        var params = "?taskIds=" + taskIds + "&revisionNote=" + revisionNote + "&currentStatus=" + currentStatus;
        var strUrl = "${createLink(controller:'rmsTask',action: 'reviseTask')}" + params;
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            success: executePostConditionForReviseTask,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        $('#txtTaskReviseReason').val('');
        $('#reviseConfirmationModalTask').modal('hide');
    }
    // execute post condition for revise task
    function executePostConditionForReviseTask(data) {
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
    // Hide modal
    function cancelReviseTask() {
        $('#txtTaskReviseReason').val('');
        $('#reviseConfirmationModalTask').modal('hide');
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