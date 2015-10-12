<script language="javascript">
    var sprintBudgetListModel = false;
    var dropDownBudget,sprintId, numericQuantity;

    $(document).ready(function () {
        onLoadSprintBudget();
    });

    function onLoadSprintBudget() {
        initializeForm($("#sprintBudgetForm"), onSubmitSprintBudget);

        var output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            sprintBudgetListModel = output.sprintBudgetList;
            $('#lblSprintName').text(output.sprintName);
            $('#sprintId').val(output.sprintId);
            sprintId = output.sprintId;
        }
        initFlexGrid();
        initFlexBudget();
        populateFlexGrid();

        $('#quantity').kendoNumericTextBox({
            min: 0.001,
            decimals: 4,
            format: "#.####"

        });
        numericQuantity = $('#quantity').data("kendoNumericTextBox");

        // update page title
        $(document).attr('title', "MIS - Create Sprint Budget Mapping");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgSprint/show");
    }

    function initFlexBudget() {
        $("#flexBudget").flexigrid
        (
                {
                    url: "${createLink(controller: 'budgBudget', action: 'getBudgetGridForSprint')}?sprintId=" + sprintId,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right", hide: true},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Line Item", name: "budgetItem", width: 90, sortable: true, align: "left"},
                        {display: "Details", name: "details", width: 220, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Add', bclass: 'addItem', onpress: addBudget},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadBudgetGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "ALL", name: "budget.budget_item", width: 80, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Search Budget',
                    useRp: false,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getFullGridHeight() - 40,
                    resizable: false,
                    isSearchOpen: true,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function addBudget(com, grid) {
        if(executeCommonPreConditionForSelect($("#flexBudget"),'budget')==false){
            return;
        }
        // Prevent to add Budget in edit mode
        var hiddenId = $('#id').val();
        if (hiddenId > 0) {
            showError('Could not add budget on edit mode');
            return false;
        }

        var ids = $('.trSelected', grid);
        var budgetId = getSelectedIdFromGrid($('#flexBudget'));
        var lineItem =  $.trim($(ids[0]).find('td').eq(2).find('div').text());
        var details =  $.trim($(ids[0]).find('td').eq(3).find('div').text());
        var quantity =  $.trim($(ids[0]).find('td').eq(4).find('div').text());

        $('#budgetId').val(budgetId);
        $('#lblQuantity').text(quantity);
        $('#lblBudgetItem').text(lineItem);
        $('#lblBudgetDetails').text(details);

    }

    function reloadBudgetGrid(com, grid) {
        $('#flexBudget').flexOptions({query: ''}).flexReload();
    }

    function executePreCondition() {
        if (!validateForm($("#sprintBudgetForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSprintBudget() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'budgSprintBudget', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'budgSprintBudget', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#sprintBudgetForm").serialize(),
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
            showLoadingSpinner(false);
        } else {
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(sprintBudgetListModel.total);
                    var firstSerial = 1;

                    if (sprintBudgetListModel.rows.length > 0) {
                        firstSerial = sprintBudgetListModel.rows[0].cell[0];
                        regenerateSerial($(sprintBudgetListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    sprintBudgetListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        sprintBudgetListModel.rows.pop();
                    }

                    sprintBudgetListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(sprintBudgetListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(sprintBudgetListModel, result.entity, 0);
                    $("#flex1").flexAddData(sprintBudgetListModel);
                }
                resetForm();
                $('#flexBudget').flexOptions({query: ''}).flexReload();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#sprintBudgetForm"), $('#quantity'));  // clear errors & form values
        $('#lblQuantity').text('');
        $('#lblBudgetItem').text('');
        $('#lblBudgetDetails').text('');
        $('#sprintId').val(sprintId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 100, sortable: false, align: "right", hide: true},
                        {display: "Budget", name: "budgetName", width: 300, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 150, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgSprintBudget/select,/budgSprintBudget/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectSprintBudget},
                        </app:ifAllUrl>
                        {name: 'Delete', bclass: 'delete', onpress: deletedSprintBudget},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid },
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Sprint Budget',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadSprintBudgetListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadSprintBudgetListJSON(data) {
        if (data.isError) {
            showError(data.message);
            sprintBudgetListModel = null;
        } else {
            sprintBudgetListModel = data;
        }
        return data;
    }

    function selectSprintBudget(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintBudgetId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgSprintBudget', action: 'select')}?id=" + sprintBudgetId,
            success: executePostConditionForSelect,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForSelect(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSprintBudget(data);
        }
    }

    function showSprintBudget(data) {
        var entity = data.sprintBudget;
        $('#id').val(entity.id);
        $('#version').val(entity.version);
        numericQuantity.value(entity.currQuantity);
        $('#budgetId').val(entity.budgetId);
        $('#lblQuantity').text(entity.quantity + '  ' + entity.unit);
        $('#lblBudgetItem').text(entity.budgetItem);
        $('#lblBudgetDetails').text(entity.details);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deletedSprintBudget(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintBudgetId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'budgSprintBudget', action: 'delete')}?id=" + sprintBudgetId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected budget from sprint?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            sprintBudgetListModel.total = parseInt(sprintBudgetListModel.total) - 1;
            removeEntityFromGridRows(sprintBudgetListModel, selectedRow);
            $('#flexBudget').flexOptions({query: ''}).flexReload();
        } else {
            showError(data.message);
        }
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'budgSprintBudget', action: 'list')}?sprintId=" + sprintId;
        $("#flex1").flexOptions({url: strUrl});

        if (sprintBudgetListModel) {
            $("#flex1").flexAddData(sprintBudgetListModel);
        }
    }

</script>
