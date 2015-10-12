<script type="text/javascript">
    var budgSprintListModel, budgetSprintListGrid;
    var dropDownProject, startDate, endDate, entityTypeId;

    $(document).ready(function () {
        onLoadBudgSprint();
    });

    function onLoadBudgSprint() {
        initializeForm($("#budgSprintForm"), onSubmitBudgSprint);
        // update page title
        $(document).attr('title', "MIS - Create Sprint");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgSprint/show");

        budgSprintListModel = ${output ? output : ''};
        var isError = budgSprintListModel.isError;
        if (isError == 'true') {
            var message = budgSprintListModel.message;
            showError(message);
            return;
        }
        budgetSprintListGrid = budgSprintListModel.gridObj;
        startDate = $("#startDate").val();
        endDate = $("#endDate").val();
        $("#name").text('( Auto Generated )');
        entityTypeId = $("#entityTypeId").val();
        initFlexGrid();
        populateBudgSprintGridOnPageLoad();
    }

    function executePreCondition() {
        if (!validateForm($("#budgSprintForm"))) {
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    function onSubmitBudgSprint() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'budgSprint', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'budgSprint', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#budgSprintForm").serialize(),
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
                    var previousTotal = parseInt(budgetSprintListGrid.total);
                    // re-arranging serial
                    var firstSerial = 1;
                    if (budgetSprintListGrid.rows.length > 0) {
                        firstSerial = budgetSprintListGrid.rows[0].cell[0];
                        regenerateSerial($(budgetSprintListGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    budgetSprintListGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        budgetSprintListGrid.rows.pop();
                    }

                    budgetSprintListGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(budgetSprintListGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(budgetSprintListGrid, result.entity, 0);
                    $("#flex1").flexAddData(budgetSprintListGrid);
                }
                resetBudgSprintForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetBudgSprintForm() {
        clearForm($("#budgSprintForm"), dropDownProject);  // clear errors & form values
        $('#name').text('( Auto Generated )');
        $("#startDate").val(startDate);
        $("#endDate").val(endDate);
        dropDownProject.enable(true);
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
                        {display: "Sprint ID", name: "id", width: 60, sortable: false, align: "right"},
                        {display: "Name", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Current", name: "isActive", width: 80, sortable: true, align: "left"},
                        {display: "Budget(s)", name: "budgetCount", width: 90, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgSprint/select,/budgSprint/update">
                        {name: 'Edit', bclass: 'edit', onpress: editBudgSprint},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgSprint/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteBudgSprint},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgReport/showBudgetSprint">
                        {name: 'Report', bclass: 'note', onpress: report},
                        </app:ifAllUrl>
                        <sec:access url="/entityContent/show">
                        {name: 'Attachment(s)', bclass: 'attachment', onpress: addContent},
                        </sec:access>
                        {name: 'Budget', bclass: 'addItem', onpress: addBudget},
                        <app:ifAllUrl urls="/budgSprint/setCurrentBudgSprint">
                        {name: 'Set Current', bclass: 'approve', onpress: setIsCurrent},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgReport/downloadForecastingReport">
                        {name: 'Forecasting Report', bclass: 'report', onpress: forecastingReport},
                        </app:ifAllUrl>
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: ".name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Sprints',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(5) - 45,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadBudgSprintListJSON
                }
        );
    }

    function setIsCurrent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }

        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgSprint', action:'setCurrentBudgSprint')}?id=" + sprintId,
            success: executePostConditionForIsCurrent,
            complete: function (XMLHttpOrder, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForIsCurrent(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                budgetSprintListGrid = result.gridObj;
                $("#flex1").flexAddData(budgetSprintListGrid);
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function addContent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'entityContent', action: 'show')}?entityId=" + sprintId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    function onLoadBudgSprintListJSON(data) {
        if (data.isError) {
            showError(data.message);
            budgetSprintListGrid = null;
        } else {
            budgetSprintListGrid = data;
        }
        return budgetSprintListGrid;
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected sprint?')) {
            return false;
        }
        return true;
    }

    function deleteBudgSprint(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var budgSprintId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'budgSprint', action: 'delete')}?id=" + budgSprintId,
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
            budgetSprintListGrid.total = parseInt(budgetSprintListGrid.total) - 1;
            removeEntityFromGridRows(budgetSprintListGrid, selectedRow);
            resetBudgSprintForm();
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function addBudget(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return false;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'budgSprintBudget', action: 'show')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    function editBudgSprint(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        resetBudgSprintForm();
        showLoadingSpinner(true);
        var budgSprintId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgSprint', action: 'select')}?id=" + budgSprintId,
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
            populateBudgSprint(data);
        }
    }

    function populateBudgSprint(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').text(entity.name);
        $('#startDate').val(data.startDate);
        $('#endDate').val(data.endDate);
        dropDownProject.value(entity.projectId);
        dropDownProject.enable(false);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateBudgSprintGridOnPageLoad() {
        var strUrl = "${createLink(controller:'budgSprint', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (budgetSprintListGrid) {
            $("#flex1").flexAddData(budgetSprintListGrid);
        }
    }

    function report() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'budgReport', action: 'showBudgetSprint')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    function forecastingReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var confirmMsg = 'Do you want to download the forecasting report in PDF format now?';

        showLoadingSpinner(true);
        var params = "?sprintId=" + sprintId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'budgReport', action: 'downloadForecastingReport')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

</script>