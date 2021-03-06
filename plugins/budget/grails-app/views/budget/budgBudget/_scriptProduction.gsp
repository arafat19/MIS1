<script type="text/javascript">
    var budgetListModel, budgetListGrid;
    var dropDownProject, dropDownBudgetScope, dropDownUnit;
    var budgetQuantity, contractRate, entityTypeId, startDate, isProduction;

    $(document).ready(function () {
        $('#billable').click(function () {
            applyClassOnContractRate();
        });
        onLoadBudget();
    });

    function onLoadBudget() {
        initializeForm($("#budgetForm"), onSubmitBudget);
        dropDownBudgetScope = initKendoDropdown($('#budgetScopeId'), null, null, null);

        $('#budgetQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"
        });
        budgetQuantity = $("#budgetQuantity").data("kendoNumericTextBox");

        $('#contractRate').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"
        });
        contractRate = $("#contractRate").data("kendoNumericTextBox");

        // update page title
        $(document).attr('title', "MIS - Create Budget");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgBudget/show?isProduction=true");

        budgetListModel = ${output ? output : ''};
        var isError = budgetListModel.isError;
        if (isError == 'true') {
            var message = budgetListModel.message;
            showError(message);
            return;
        }
        budgetListGrid = budgetListModel.gridObject;
        isProduction = budgetListModel.isProduction;
        $("#isProduction").val(isProduction);
        initFlexGrid();
        entityTypeId = $("#entityTypeId").val();
        startDate = $('#startDate').attr('value');
    }

    function executePreCondition() {
        if (!validateForm($("#budgetForm"))) {
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start Date', $("#endDate"), 'End Date')) {
            return false;
        }
        var billableRate = parseFloat(contractRate.value());
        var billable = $('#billable').attr('checked');
        if (billable && ((billableRate <= 0) || (contractRate.value() == null))) {
            showError('Contract rate must be greater than zero for billable budgets');
            return false;
        }
        return true;
    }

    function onSubmitBudget() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'budgBudget', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'budgBudget', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#budgetForm").serialize(),
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
                    var previousTotal = parseInt(budgetListGrid.total);
                    // re-arranging serial
                    var firstSerial = 1;
                    if (budgetListGrid.rows.length > 0) {
                        firstSerial = budgetListGrid.rows[0].cell[0];
                        regenerateSerial($(budgetListGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    budgetListGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        budgetListGrid.rows.pop();
                    }

                    budgetListGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(budgetListGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(budgetListGrid, result.entity, 0);
                    $("#flex1").flexAddData(budgetListGrid);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function applyClassOnContractRate() {
        var checked = $("#billable").attr('checked');
        if (checked) {
            $("#lblContractrate").removeClass('label-optional');
            $("#lblContractrate").addClass('label-required');
            $("#contractRate").attr('required', true);
        } else {
            $("#lblContractrate").removeClass('label-required');
            $("#lblContractrate").addClass('label-optional');
            $("#contractRate").removeAttr('required');
        }
    }

    function resetForm() {
        clearForm($("#budgetForm"), dropDownProject);  // clear errors & form values
        $("#contractRate").removeAttr('required');
        //enable disabled fields
        dropDownProject.enable(true);
        dropDownBudgetScope.setDataSource(getKendoEmptyDataSource());
        dropDownBudgetScope.value('');
        $("#startDate").val(startDate);
        $("#endDate").val(startDate);
        // make contract rate as optional
        $("#lblContractrate").removeClass('label-required');
        $("#lblContractrate").addClass('label-optional');
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
                        {display: "Line Item", name: "budgetItem", width: 110, sortable: true, align: "left"},
                        {display: "Budget Scope", name: "budgetScopeId", width: 150, sortable: false, align: "left"},
                        {display: "Project Code", name: "projectCode", width: 90, sortable: false, align: "left"},
                        {display: "Budget Quantity", name: "budgetQuantity", width: 130, sortable: false, align: "right"},
                        {display: "Item(s)", name: "itemCount", width: 50, sortable: false, align: "right"},
                        {display: "Billable", name: "billable", width: 50, sortable: false, align: "center"},
                        {display: "Details", name: "details", width: 190, sortable: false, align: "left"},
                        {display: "Task(s)", name: "taskCount", width: 70, sortable: false, align: "right"},
                        {display: "Attachment(s)", name: "contentCount", width: 90, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgBudget/select,/budgBudget/update">
                        {name: 'Edit', bclass: 'edit', onpress: editBudget},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgBudget/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteBudget},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgBudgetDetails/show">
                        {name: 'Item(s)', bclass: 'addItem', onpress: addBudgetDetailsWithMaterial},
                        </app:ifAllUrl>
                        <sec:access url="/budgReport/showBudgetRpt">
                        {name: 'Report', bclass: 'report', onpress: viewBudgetReport},
                        </sec:access>
                        <sec:access url="/entityContent/show">
                        {name: 'Attachment(s)', bclass: 'attachment', onpress: addContent},
                        </sec:access>
                        <sec:access url="/budgTask/show">
                        {name: 'Task', bclass: 'addItem', onpress: addTask},
                        </sec:access>
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Line Item", name: "budgetItem", width: 150, sortable: true, align: "left"},
                        {display: "Project Code", name: "projectCode", width: 150, sortable: true, align: "left"},
                        {display: "Details", name: "budgetDetails", width: 150, sortable: true, align: "left"},
                        {display: "Material", name: "material", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Budgets',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    preProcess: onLoadBudgetListJSON
                }
        );
    }

    function onLoadBudgetListJSON(data) {
        if (data.isError) {
            showError(data.message);
            budgetListGrid = null;
        } else {
            budgetListGrid = data;
        }
        return budgetListGrid;
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Budget?')) {
            return false;
        }
        return true;
    }

    function deleteBudget(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var budgetId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'budgBudget', action: 'delete')}?id=" + budgetId,
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
            budgetListGrid.total = parseInt(budgetListGrid.total) - 1;
            removeEntityFromGridRows(budgetListGrid, selectedRow);
            resetForm();
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function editBudget(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var budgetId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgBudget', action: 'select')}?id=" + budgetId,
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
            populateBudget(data);
            onChangeProjectEvent(data.entity.budgetScopeId);
        }
    }

    function populateBudget(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownProject.value(entity.projectId);
        dropDownBudgetScope.value(entity.budgetScopeId);
        dropDownUnit.value(entity.unitId);
        $('#budgetItem').val(entity.budgetItem);
        $('#billable').attr('checked', entity.billable);
        applyClassOnContractRate();
        $('#details').val(entity.details);
        $('#startDate').val(data.startDate);
        $('#endDate').val(data.endDate);
        budgetQuantity.value(entity.budgetQuantity);
        contractRate.value(entity.contractRate);
        // disable not editable fields
        dropDownProject.enable(false);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function addBudgetDetailsWithMaterial(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'budgBudgetDetails', action: 'show')}?budgetId=" + budgetId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addSchema(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'budgSchema', action: 'show')}?budgetId=" + budgetId;
        $.history.load(formatLink(loc));
        return false;
    }

    function onChangeProjectEvent(budgetScopeId) {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            dropDownBudgetScope.setDataSource(getKendoEmptyDataSource());
            dropDownBudgetScope.value('');
            $("#startDate").val(startDate);
            return;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'projectBudgetScope', action: 'getBudgetScope')}?projectId=" + projectId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    dropDownBudgetScope.setDataSource(data.budgetScopeList);
                    dropDownBudgetScope.value('');
                    if (budgetScopeId) {
                        dropDownBudgetScope.value(budgetScopeId);
                    }
                    if ($('#id').val().isEmpty()) {
                        var startDate = dropDownProject.dataItem().startDate;
                        var endDate = dropDownProject.dataItem().endDate;
                        $("#startDate").val(startDate);
                        $("#endDate").val(endDate);
                    }
                }
            }, complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No budget data found');
        }
    }

    window.onload = populateBudgetGridOnPageLoad();

    function populateBudgetGridOnPageLoad() {
        var strUrl = "${createLink(controller:'budgBudget', action: 'list')}?isProduction=" + isProduction;
        $("#flex1").flexOptions({url: strUrl});
        if (budgetListGrid) {
            $("#flex1").flexAddData(budgetListGrid);
        }
    }

    function viewBudgetReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'budgReport', action: 'showBudgetRpt')}?budgetId=" + budgetId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addContent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'entityContent', action: 'show')}?entityId=" + budgetId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addTask(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetId = getSelectedIdFromGrid($('#flex1'));

        var loc = "${createLink(controller:'budgTask', action: 'show')}?budgetId=" + budgetId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>