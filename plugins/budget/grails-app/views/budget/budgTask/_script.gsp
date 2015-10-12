<script type="text/javascript">
    var budgTaskListModel, budgetTaskListGrid, budgetId, statusId;
    var dropDownTaskStatus, startDate, endDate, budgetObj;

    $(document).ready(function () {
        onLoadBudgTask();
    });

    function onLoadBudgTask() {
        initializeForm($("#budgTaskForm"), onSubmitBudgTask);

        // update page title
        $(document).attr('title', "MIS - Create Task");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgBudget/show");

        budgTaskListModel = ${output ? output : ''};
        var isError = budgTaskListModel.isError;
        if (isError == 'true') {
            var message = budgTaskListModel.message;
            showError(message);
            return;
        }
        budgetTaskListGrid = budgTaskListModel.gridObject;
        startDate = $("#startDate").val();
        endDate = $("#endDate").val();
        $("#status").text('Defined');
        $('#budgetId').val(budgTaskListModel.budgetId);
        budgetId = budgTaskListModel.budgetId;
        budgetObj = budgTaskListModel.budgetObject;
        $('#budgetItemSpan').text(budgetObj.budgetItem);
        statusId = $('#hidBudgTaskStatusDefined').val();
        initFlexGrid();
    }

    function executePreCondition() {
        if (!validateForm($("#budgTaskForm"))) {
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitBudgTask() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'budgTask', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'budgTask', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#budgTaskForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);
                showLoadingSpinner(false);
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
                    var previousTotal = parseInt(budgetTaskListGrid.total);
                    // re-arranging serial
                    var firstSerial = 1;
                    if (budgetTaskListGrid.rows.length > 0) {
                        firstSerial = budgetTaskListGrid.rows[0].cell[0];
                        regenerateSerial($(budgetTaskListGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    budgetTaskListGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        budgetTaskListGrid.rows.pop();
                    }

                    budgetTaskListGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(budgetTaskListGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(budgetTaskListGrid, result.entity, 0);
                    $("#flex1").flexAddData(budgetTaskListGrid);
                }
                resetBudgTaskForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetBudgTaskForm() {
        clearForm($("#budgTaskForm"), $('#name'));  // clear errors & form values
        $("#startDate").val(startDate);
        $("#endDate").val(endDate);
        $('#budgetId').val(budgetId);
        $("#status").text('Defined');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "Name", name: "name", width: 150, sortable: true, align: "left"},
                        {display: "Status", name: "statusId", width: 300, sortable: false, align: "left"},
                        {display: "Start Date", name: "startDate", width: 180, sortable: false, align: "left"},
                        {display: "End Date", name: "endDate", width: 150, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgTask/select,/budgTask/update">
                        {name: 'Edit', bclass: 'edit', onpress: editBudgTask},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgTask/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteBudgTask},
                        </app:ifAllUrl>
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "task.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Tasks',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(5) - 45,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    preProcess: onLoadBudgTaskListJSON
                }
        );
    }

    function onLoadBudgTaskListJSON(data) {
        if (data.isError) {
            showError(data.message);
            budgetTaskListGrid = null;
        } else {
            budgetTaskListGrid = data;
        }
        return budgetTaskListGrid;
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'task') == false) {
            return false;
        }
        return true;
    }

    function deleteBudgTask(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        var ids = $('.trSelected', grid);
        var status = $.trim($(ids[0]).find('td').eq(2).find('div').text());
        if (status == "Defined") {
            if (!confirm('Are you sure you want to delete the selected task?')) {
                return ;
            }
        } else {
            if (!confirm('The selected task is ' + status + '. Are you sure you want to delete the task?')) {
                return ;
            }
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var budgTaskId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'budgTask', action: 'delete')}?id=" + budgTaskId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            budgetTaskListGrid.total = parseInt(budgetTaskListGrid.total) - 1;
            removeEntityFromGridRows(budgetTaskListGrid, selectedRow);
            resetBudgTaskForm();
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function editBudgTask(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'task') == false) {
            return;
        }
        resetBudgTaskForm();
        showLoadingSpinner(true);
        var budgTaskId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgTask', action: 'select')}?id=" + budgTaskId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(result.message);
        } else {
            populateBudgTask(data);
        }
    }

    function populateBudgTask(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#startDate').val(data.startDate);
        $('#endDate').val(data.endDate);
        $("#status").text(data.budgTaskStatusName);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No task data found');
        }
    }

    window.onload = populateBudgTaskGridOnPageLoad();

    function populateBudgTaskGridOnPageLoad() {
        var strUrl = "${createLink(controller:'budgTask', action: 'list')}?budgetId=" + budgetId;
        $("#flex1").flexOptions({url: strUrl});

        if (budgetTaskListGrid) {
            $("#flex1").flexAddData(budgetTaskListGrid);
        }
    }

</script>