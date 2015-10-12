<script type="text/javascript">
    var dropDownExchangeHouse, dropDownTaskList, currentStatus;
    var taskListModel = false;

    $(document).ready(function () {
        onLoadTaskApprovePage();
    });

    function onLoadTaskApprovePage() {
        initializeForm($("#filterPanelApproveTaskForm"), onSubmitApproveTask);
        $("#isRevised").change(function () {
            populateExchangeHouse();
        });
        currentStatus = $("#currentStatus").val();
        initFlex();
        $(document).attr('title', "ARMS - Approve Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showForApproveTask");
    }

    // Populate exchange house list onClick of exchange house refresh button
    function populateExchangeHouse() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = false;
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = true;
        }
        $('#exhHouseId').removeAttr('default_value');
        $('#exhHouseId').attr('from_date', fromDate);
        $('#exhHouseId').attr('to_date', toDate);
        $('#exhHouseId').attr('is_revised', isRevised);
        $('#exhHouseId').reloadMe();
        dropDownExchangeHouse.setDataSource(getKendoEmptyDataSource());
        dropDownTaskList.setDataSource(getKendoEmptyDataSource());
        dropDownTaskList.refresh();
        $("#lblBalance").text('');
    }

    function onchangeExhHouse() {
        populateTaskList();
        if (dropDownExchangeHouse.value() == '') {
            $("#lblBalance").text('');
            return false;
        }
        $("#lblBalance").text("BDT " + dropDownExchangeHouse.dataItem().balance);
    }
    // Populate task list onchange of exchange house dropdown
    function populateTaskList() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = false;
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = true;
        }
        $('#taskListId').attr('from_date', fromDate);
        $('#taskListId').attr('to_date', toDate);
        $('#taskListId').attr('is_revised', isRevised);
        $('#taskListId').attr('exchange_house_id', exhHouseId);
        $('#taskListId').reloadMe();
    }

    // method called  on submit of the filter panel form
    function onSubmitApproveTask() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        setButtonDisabled($('#search'), true);  // disable the search button
        showLoadingSpinner(true);   // show loading spinner
        var strUrl = "${createLink(controller:'rmsTask',action: 'listTaskForApprove')}";
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            data: jQuery("#filterPanelApproveTaskForm").serialize(),
            success: function (data, textStatus) {
                executePostConditionForSearchTask(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
                setButtonDisabled($('#search'), false);
            },
            dataType: 'json',
            type: 'post'
        });
        return false;
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSearchTask() {
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
    // execute post condition for search task
    function executePostConditionForSearchTask(result) {
        $("#flex").flexAddData(getEmptyGridModel());
        if (result.isError) {
            showError(result.message);
            return false;
        }
        taskListModel = result.gridObj;
        populateFlex();
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
                        {display: "Process", name: "process", width: 80, sortable: false, align: "left"},
                        {display: "Instrument", name: "instrument", width: 100, sortable: false, align: "left"},
                        {display: "Mapping Bank, BankBranch & District", name: "mapping", width: 210, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/rmsTask/approve">
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsTask/approve">
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsTask/approve">
                        {name: 'Approve', bclass: 'approve', onpress: approveTask},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsTask/approve">
                        {name: 'Revise', bclass: 'delete', onpress: addRevisionNote},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    searchitems: [
                        {display: "Ref No", name: "refNo", width: 180, sortable: true, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "process",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All Task (Decision Taken)',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 35,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }

    function reloadGrid() {
        $('#flex').flexOptions({query: ''}).flexReload();
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
    // set link to grid url to populate data
    function populateFlex() {
        var exhHouseId = dropDownExchangeHouse.value();
        var isRevised = "";
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = "on";
        }
        var taskListId = dropDownTaskList.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?exhHouseId=" + exhHouseId + "&currentStatus=" + currentStatus + "&isRevised=" + isRevised + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsTask', action: 'listTaskForApprove')}" + params;
        $("#flex").flexOptions({url: strUrl});
        if (taskListModel) {
            $("#flex").flexAddData(taskListModel);
        }
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

    function approveTask() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return;
        }
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var exhHouseId = dropDownExchangeHouse.value();
        var params = "?taskIds=" + taskIds + "&exhHouseId=" + exhHouseId + "&currentStatus=" + currentStatus;
        var actionUrl = "${createLink(controller: 'rmsTask', action: 'approve')}" + params;
        // fire ajax method for approve task
        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            success: function (data) {
                executePostCondition(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);     // enable the create button
                showLoadingSpinner(false);                  // stop loading spinner
            },
            dataType: 'json'
        });
    }
    // execute post condition after approve task
    function executePostCondition(data) {
        if (data.isError == false) {
            var selectedRow = null;
            var selectedRowCount = $('#flex tr.trSelected').size();
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex').decreaseCount(selectedRowCount);
            showSuccess(data.message);
            $("#lblBalance").text('');
            taskListModel.total = parseInt(taskListModel.total) - selectedRowCount;
            removeEntityFromGridRows(taskListModel, selectedRow);
            $('#exhHouseId').attr('default_value', dropDownExchangeHouse.value());
            $('#taskListId').attr('default_value', dropDownTaskList.value());
            $('#exhHouseId').attr('add_balance', true);
            $('#exhHouseId').reloadMe(onchangeExhHouse);
        } else {
            showError(data.message);
        }
    }
    function addRevisionNote() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return;
        }
        $('#reviseConfirmationModalTask').modal('show');    // show Modal
    }
    function executePreConditionForRevise() {
        $('#txtTaskReviseReason').val($.trim($('#txtTaskReviseReason').val()));
        if ($('#txtTaskReviseReason').val() == '') {
            showError('Revision note needed');
            return false;
        }
        return true;
    }
    function reviseTask() {
        if (executePreConditionForRevise() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var revisionNote = $('#txtTaskReviseReason').val();
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
            $("#lblBalance").text('');
            showSuccess(data.message);
            taskListModel.total = parseInt(taskListModel.total) - 1;
            removeEntityFromGridRows(taskListModel, selectedRow);
            $('#exhHouseId').attr('default_value', dropDownExchangeHouse.value());
            $('#taskListId').attr('default_value', dropDownTaskList.value());
            $('#exhHouseId').attr('add_balance', true);
            $('#exhHouseId').reloadMe(onchangeExhHouse);
        } else {
            showError(data.message);
        }
    }
    function cancelReviseTask() {
        $('#txtTaskReviseReason').val('');
        $('#reviseConfirmationModalTask').modal('hide');
        return false;
    }

</script>