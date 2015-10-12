<script type="text/javascript">
    var dropDownExchangeHouse, dropDownTaskList, dropDownTaskStatus, selectedTaskList;
    var taskListModel = false;
    $(document).ready(function () {
        onLoadManageTaskListPage();
    });

    function onLoadManageTaskListPage() {
        initializeForm($("#filterPanelForManageTaskList"), onSubmitManageTaskList);
        taskListModel = false;
        renameList();
        hideRenamePanel();
        moveTaskToAnotherList();
        hideMoveListPanel();
        initFlex();
        $('#closeInput').hide();
        $('#closeInput').click(function () {
            hideMoveListPanel();
            hideRenamePanel();
        });
        $(document).attr('title', "ARMS - Create Task List");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTaskList/showForManageTaskList");
    }
    function renameList() {
        $('#newTaskList').keypress(function (event) {
            if (event.which == '13') {
                processForRenameList();
                return false;
            } else if (event.which == '0') {
                hideRenamePanel();
            }
        });
    }
    function moveTaskToAnotherList() {
        $('#anotherTaskList').keypress(function (event) {
            if (event.which == '13') {
                processForMoveTaskToAnotherList();
                return false;
            } else if (event.which == '0') {
                hideMoveListPanel();
            }
        });
    }
    // Populate exchange house list onClick of exchange house refresh button
    function populateExchangeHouse() {
        if (executePreConditionForPopulateDropDown() == false) {
            return false;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var currentStatus = dropDownTaskStatus.dataItem().reservedId;
        $('#exchangeHouseId').attr('from_date', fromDate);
        $('#exchangeHouseId').attr('to_date', toDate);
        $('#exchangeHouseId').attr('task_status_list', currentStatus);
        $('#exchangeHouseId').reloadMe();
        return false;
    }

    // check pre condition before populate dropDown
    function executePreConditionForPopulateDropDown() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        return true;
    }
    // method called  on submit of the filter panel form
    function onSubmitManageTaskList() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        showLoadingSpinner(true);   // show loading spinner
        var exhHouseId = dropDownExchangeHouse.value();
        var currentStatus = dropDownTaskStatus.value();
        var taskListId = dropDownTaskList.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?exchangeHouseId=" + exhHouseId + "&currentStatus=" + currentStatus + "&fromDate=" + fromDate + "&toDate=" + toDate + "&taskListId=" + taskListId;
        var strUrl = "${createLink(controller: 'rmsTaskList', action: 'listForManageTaskList')}" + params;
        $("#flex").flexOptions({url: strUrl}).flexReload();
        hideRenamePanel();
        hideMoveListPanel();
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
            showError('Please select task list name.');
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
                        {display: "Ref No", name: "refNo", width: 150, sortable: false, align: "left"},
                        {display: "Account No", name: "accountNo", width: 100, sortable: false, align: "right"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 110, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 100, sortable: false, align: "left"},
                        {display: "Mapping Bank Info", name: "bank", width: 230, sortable: false, align: "left"},
                        {display: "Mapping Decision", name: "decision", width: 230, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: selectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: deselectAll},
                        {name: 'Remove From List', bclass: 'delete', onpress: addRemoveFromListNote},
                        {name: 'Rename List', bclass: 'edit', onpress: renameTaskList},
                        {name: 'Move to Another List', bclass: 'send', onpress: moveToAnotherList},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: clearResults}
                    ],

                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All Task in task list',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateFlex
                }
        );
    }

    // set link to grid url to populate data
    function populateFlex(result) {
        if (result.isError) {
            showError(result.message);
            taskListModel = getEmptyGridModel();
        } else {
            taskListModel = result.gridObj;
        }
        $("#flex").flexAddData(taskListModel);
        showLoadingSpinner(false);
    }

    function populateTaskList() {
        if (dropDownExchangeHouse.value() == '') {
            dropDownTaskList.setDataSource(getKendoEmptyDataSource());
            return;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var exchangeHouseId = dropDownExchangeHouse.value();
        var currentStatus = dropDownTaskStatus.dataItem().reservedId;
        $('#taskListId').attr('from_date', fromDate);
        $('#taskListId').attr('to_date', toDate);
        $('#taskListId').attr('task_status_list', currentStatus);
        $('#taskListId').attr('exchange_house_id', exchangeHouseId);
        $('#taskListId').reloadMe();
        return false;
    }
    function addRemoveFromListNote() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return;
        }
        $('#noteForRemoveFromListModal').modal('show');    // show Modal
    }
    function executePreConditionForRemoveFromList() {
        $('#txtTaskRemoveFromListReason').val($.trim($('#txtTaskRemoveFromListReason').val()));
        if ($('#txtTaskRemoveFromListReason').val() == '') {
            showError('Please enter reason for removing from list');
            return false;
        }
        return true;
    }
    function removeFromList() {
        if (executePreConditionForRemoveFromList() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var revisionNote = $('#txtTaskRemoveFromListReason').val();
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var currentStatus = dropDownTaskStatus.value();
        var taskListId = dropDownTaskList.value();
        var params = "?taskIds=" + taskIds + "&currentStatus=" + currentStatus + "&taskListId=" + taskListId + "&revisionNote=" + revisionNote;
        $.ajax({
            url: "${createLink(controller:'rmsTaskList', action: 'removeFromList')}" + params,
            success: executePostConditionForRemoveFromList,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        $('#txtTaskRemoveFromListReason').val('');
        $('#noteForRemoveFromListModal').modal('hide');
    }
    function executePostConditionForRemoveFromList(data) {
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
    function closeRemoveListModal() {
        $('#txtTaskRemoveFromListReason').val('');
        $('#noteForRemoveFromListModal').modal('hide');
        return false;
    }
    function clearResults() {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    function selectAll() {
        try {
            var rows = $('table#flex > tbody > tr');
            if (rows && rows.length > 0) {
                rows.addClass('trSelected');
            }
        } catch (e) {
        }
    }
    function deselectAll() {
        try {
            var rows = $('table#flex > tbody > tr');
            if (rows && rows.length > 0) {
                rows.removeClass('trSelected');
            }
        } catch (e) {
        }

    }
    function renameTaskList() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        showRenamePanel();
    }
    function processForRenameList() {
        var taskListId = dropDownTaskList.value();
        $('#newTaskList').val($.trim($('#newTaskList').val()));
        if ($('#newTaskList').val() == '') {
            showError('Please enter valid task list name.');
            return false;
        }
        var taskListName = $('#newTaskList').val();
        if (!confirm("Are you sure you want to rename list from '" + dropDownTaskList.text() + "' to '" + taskListName + "' ?")) {
            hideRenamePanel();
            return false;
        }
        var params = "?taskListId=" + taskListId + "&taskListName=" + taskListName;
        $.ajax({
            url: "${createLink(controller:'rmsTaskList', action: 'renameTaskList')}" + params,
            success: executePostConditionForRenameList,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return false;
    }
    function executePostConditionForRenameList(data) {
        if (data.isError == false) {
            showSuccess(data.message);
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var exchangeHouseId = dropDownExchangeHouse.value();
            var currentStatus = dropDownTaskStatus.dataItem().reservedId;
            selectedTaskList = dropDownTaskList.value();
            $('#taskListId').attr('from_date', fromDate);
            $('#taskListId').attr('to_date', toDate);
            $('#taskListId').attr('task_status_list', currentStatus);
            $('#taskListId').attr('exchange_house_id', exchangeHouseId);
            $('#taskListId').reloadMe(onCompleteTaskListReload);

        } else {
            showError(data.message);
        }

        hideRenamePanel();
        return;
    }
    function moveToAnotherList() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        if (executeCommonPreConditionForSelect($('#flex'), 'task(s)') == false) {
            return false;
        }
        showMoveListPanel();
    }
    function processForMoveTaskToAnotherList() {

        var currentStatus = dropDownTaskStatus.value();
        var exchangeHouseId = dropDownExchangeHouse.value();
        var taskListId = dropDownTaskList.value();
        var taskListName = $.trim($('#anotherTaskList').val());
        $('#anotherTaskList').val($.trim($('#anotherTaskList').val()));
        if (taskListName == '') {
            showError('Please enter list name.');
            return false;
        }
        if (!confirm("Are you sure you want to rename list from '" + dropDownTaskList.text() + "' to '" + taskListName + "' ?")) {
            hideRenamePanel();
            return false;
        }
        var taskId = getSelectedIdFromGrid($('#flex'));
        var params = "?taskId=" + taskId + "&taskListName=" + taskListName + "&exchangeHouseId=" + exchangeHouseId + "&currentStatus=" + currentStatus
                + "&taskListId=" + taskListId;
        $.ajax({
            url: "${createLink(controller:'rmsTaskList', action: 'moveTaskToAnotherList')}" + params,
            success: executePostConditionForMoveList,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return false;
    }
    function executePostConditionForMoveList(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            taskListModel.total = parseInt(taskListModel.total) - 1;
            removeEntityFromGridRows(taskListModel, selectedRow);

            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var exchangeHouseId = dropDownExchangeHouse.value();
            var currentStatus = dropDownTaskStatus.dataItem().reservedId;
            selectedTaskList = dropDownTaskList.value();
            $('#taskListId').attr('from_date', fromDate);
            $('#taskListId').attr('to_date', toDate);
            $('#taskListId').attr('task_status_list', currentStatus);
            $('#taskListId').attr('exchange_house_id', exchangeHouseId);
            $('#taskListId').reloadMe(onCompleteTaskListReload);

        } else {
            showError(data.message);
        }
        hideMoveListPanel();
        return;
    }
    function onCompleteTaskListReload() {
        dropDownTaskList.value(selectedTaskList);
    }

    function showRenamePanel() {
        hideMoveListPanel();
        $('#lblRenameList').show();
        $('#newTaskList').val('');
        $('#newTaskList').show();
        $('#closeInput').show();
    }
    function hideRenamePanel() {
        $('#lblRenameList').hide();
        $('#newTaskList').hide();
        $('#closeInput').hide();
    }
    function showMoveListPanel() {
        hideRenamePanel();
        $('#lblMoveToAnotherList').show();
        $('#anotherTaskList').val('');
        $('#anotherTaskList').show();
        $('#closeInput').show();
    }
    function hideMoveListPanel() {
        $('#lblMoveToAnotherList').hide();
        $('#anotherTaskList').hide();
        $('#closeInput').hide();
    }
</script>