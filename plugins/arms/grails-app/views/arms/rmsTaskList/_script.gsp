<script type="text/javascript">
    var dropDownExchangeHouse, currentStatus;
    var taskListModel = false;
    $(document).ready(function () {
        onLoadTaskListPage();
    });

    function onLoadTaskListPage() {
        initializeForm($("#filterPanelTaskListForm"), onSubmitTask);
        currentStatus = $("#currentStatus").val();
        taskListModel = false;

        $("#isRevised").change(function() {
            populateExchangeHouse();
        });

        initFlex();
        $(document).attr('title', "ARMS - Create Task List");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTaskList/show");
    }

    // Populate exchange house list onClick of exchange house refresh button
    function populateExchangeHouse() {
        if (executePreConditionForPopulateDropdown() == false) {
            return false;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = false;
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = true;
        }
        $('#exhHouseId').attr('from_date', fromDate);
        $('#exhHouseId').attr('to_date', toDate);
        $('#exhHouseId').attr('is_revised', isRevised);
        $('#exhHouseId').reloadMe();
        return false;
    }

    // check pre condition before populate dropdown
    function executePreConditionForPopulateDropdown() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        return true;
    }
    // method called  on submit of the filter panel form
    function onSubmitTask() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        setButtonDisabled($('#search'), true);  // disable the search button
        showLoadingSpinner(true);   // show loading spinner
        var strUrl = "${createLink(controller:'rmsTask',action: 'listTaskForTaskList')}";
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            data: jQuery("#filterPanelTaskListForm").serialize(),
            success: function (data) {
                executePostConditionForSearchTask(data);
            },
            complete: function () {
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
        $("#exchangeHouseId").val(dropDownExchangeHouse.value());
        $("#lblPreviousName").html(result.previousTaskListName);
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
                        {display: "Ref No", name: "refNo", width: 150, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                        {display: "Payment Method", name: "paymentMethod", width: 110, sortable: false, align: "left"},
                        {display: "Created Date", name: "createdOn", width: 100, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All New Task',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 200,
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
    // select all rows of the grid
    function doSelectAll() {
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
    // set link to grid url to populate data
    function populateFlex() {
        var exhHouseId = dropDownExchangeHouse.value();
        var isRevised = "";
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = "on";
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?exhHouseId=" + exhHouseId + "&currentStatus=" + currentStatus + "&isRevised=" + isRevised + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsTask', action: 'listTaskForTaskList')}" + params;
        $("#flex").flexOptions({url: strUrl});
        if (taskListModel) {
            $("#flex").flexAddData(taskListModel);
        }
    }
    // method called for create taskList and update task
    function createTaskList() {
        if (executePreConditionToCreateList() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);          // disable the create button
        //showLoadingSpinner(true);                       // show loading spinner
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = "";
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = "on";
        }
        var params = "?taskIds=" + taskIds + "&currentStatus=" + currentStatus + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isRevised=" + isRevised;
        var actionUrl = "${createLink(controller: 'rmsTaskList', action: 'create')}" + params;
        // fire ajax method for create
        jQuery.ajax({
            type: 'post',
            data: jQuery("#taskListForm").serialize(),      // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data) {
                executePostConditionForCreateList(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);     // enable the create button
                showLoadingSpinner(false);                  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }
    // check pre condition before submitting the taskListForm
    function executePreConditionToCreateList() {
        $('#name').val($.trim($('#name').val()))
        if ($("#name").val() == '') {
            showError('Please enter list name.');
            return false;
        }
        if ((!taskListModel) || (taskListModel.total == 0)) {
            showError('No task found to create list.');
            return false;
        }
        if ($("#applyToAllTask").attr('checked') != 'checked') {
            if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
                return false;
            }
        }
        if(!confirm("Are you sure you want to create a task list named "+'"'+$('#name').val()+'"?')){
            return false;
        }
        return true;
    }
    // execute post condition after create taskList
    function executePostConditionForCreateList(data) {
        if (data.isError == false) {
            if ($("#applyToAllTask").attr('checked') == 'checked') {
                $("#flex").flexAddData(getEmptyGridModel());
            }
            else {
                var selectedRow = null;
                var selectedRowCount = $('#flex tr.trSelected').size();
                $('.trSelected', $('#flex')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                $('#flex').decreaseCount(selectedRowCount);
                taskListModel.total = parseInt(taskListModel.total) - selectedRowCount;
                removeEntityFromGridRows(taskListModel, selectedRow);
            }
            showSuccess(data.message);
            resetFormForCreateList();
        } else {
            showError(data.message);
        }
    }
    // reset the form
    function resetFormForCreateList() {
        $("#lblPreviousName").text($('#name').val());
        $('#name').val('');
        $("#applyToAllTask").attr('checked', false);
    }

</script>