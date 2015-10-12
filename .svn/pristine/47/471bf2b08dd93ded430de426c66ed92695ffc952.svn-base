<script type="text/javascript">
    var sprintId,budgetTaskListGrid;
    jQuery(function ($) {
        onLoadSprintReport();
    });

    function onLoadSprintReport() {
        try {
            var output = ${output};
            initFlexGrid();
            if (output.sprintId) {
                sprintId = output.sprintId;
                budgetTaskListGrid = output.lstBudgetSprint
                populateTaskGrid(output);
            }
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Update Task");
        loadNumberedMenu(MENU_ID_QS, "#budgSprint/showForCurrentSprint");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "BOQ Line Item", name: "budget_line_item", width: 200, sortable: false, align: "left"},
                        {display: "Task", name: "task", width: 400, sortable: false, align: "left"},
                        {display: "Start Date", name: "start_date", width: 120, sortable: false, align: "left"},
                        {display: "End Date", name: "end_date", width: 120, sortable: false, align: "left"},
                        {display: "Status", name: "status", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'In-progress', bclass: 'edit', onpress: setStatusInprogress},
                        {name: 'Complete', bclass: 'addCost', onpress: setStatusComplete},
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "budget_line_item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Task List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadBudgetSprintListJSON
                }
        );
    }

    function onLoadBudgetSprintListJSON(data) {
        if (data.isError) {
            showError(data.message);
            doGridEmpty();
            return null;
        }
        $("#flex1").flexAddData(data.lstBudgetSprint);
    }


    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function executePreCondition() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'task') == false) {
            return;
        }
        if (!confirm('Are you sure you want to change selected task\'s status?')) {
            return false;
        }
        return true;
    }

    function setStatusInprogress(com, grid) {
        if (executePreCondition() == false) {
            return;
        }
        showLoadingSpinner(true);
        var taskId = getSelectedIdFromGrid($('#flex1'));
        var statusId = $("#inProgressStatusId").val();

        $.ajax({
            url: "${createLink(controller:'budgTask', action: 'updateTaskForSprint')}?id=" + taskId + "&statusId=" + statusId,
            success: executePostCondition,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }
    function setStatusComplete(com, grid) {
        if (executePreCondition() == false) {
            return;
        }
        showLoadingSpinner(true);
        var taskId = getSelectedIdFromGrid($('#flex1'));
        var statusId = $('#completeStatusId').val();

        $.ajax({
            url: "${createLink(controller:'budgTask', action: 'updateTaskForSprint')}?id=" + taskId + "&statusId=" + statusId,
            success: executePostCondition,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                // updated existing
                updateListModel(budgetTaskListGrid, result.entity, 0);
                $("#flex1").flexAddData(budgetTaskListGrid);
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }
    function populateTaskGrid(data) {
        var url = "${createLink(controller:'budgTask', action: 'listTaskForSprint')}?sprintId=" + sprintId;
        $("#flex1").flexOptions({url: url});
        $("#flex1").flexAddData(data.lstBudgetSprint);
    }
    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }


</script>