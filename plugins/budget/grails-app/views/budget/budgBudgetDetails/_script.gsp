<script language="javascript">
    var budgetDetailsListModel, budgetDetailsGrid, budgetId, projectId, budgetItem;
    var dropDownItemType, dropDownItem;
    var itemQuantity, estimatedRate;

    $(document).ready(function () {
        onLoadBudgetDetails();
    });

    function onLoadBudgetDetails() {
        initializeForm($("#budgetDetailsForm"), onSubmitBudgetDetails);
        dropDownItem = initKendoDropdown($('#itemId'), null, null, null);

        $('#quantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"
        });
        itemQuantity = $("#quantity").data("kendoNumericTextBox");

        $('#rate').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"
        });
        estimatedRate = $("#rate").data("kendoNumericTextBox");

        initFlexGrid();
        budgetDetailsListModel = ${output ? output : ''};
        var isError = budgetDetailsListModel.isError;
        if (isError == 'true') {
            var message = budgetDetailsListModel.message;
            showError(message);
            return;
        }
        if (budgetDetailsListModel.budgetInfo) {
            setBudgetInfo(budgetDetailsListModel.budgetInfo);
        }
        budgetDetailsGrid = budgetDetailsListModel.budgetDetailsList;

        // update page title
        $(document).attr('title', "MIS - Create Budget Details");
        loadNumberedMenu(MENU_ID_BUDGET, budgetDetailsListModel.budgetInfo.leftMenu);
    }

    function setBudgetInfo(budgetInfo) {
        $('#projectId').val(budgetInfo.projectId);
        $('#projectName').text(budgetInfo.projectName);
        $('#budgetScope').text(budgetInfo.budgetScope);
        $('#budgetId').val(budgetInfo.budgetId);
        $('#budgetItemSpan').text(budgetInfo.budgetItem);
        $('#budgetItem').val(budgetInfo.budgetItem);
        $('#detailsSpan').text(budgetInfo.details);
        budgetId = budgetInfo.budgetId;
        projectId = budgetInfo.projectId;
        budgetItem = budgetInfo.budgetItem;
    }

    function resetFormBudgetDetails() {
        clearForm($("#budgetDetailsForm"), dropDownItemType);

        dropDownItem.setDataSource(getKendoEmptyDataSource());
        dropDownItem.value('');
        $('#unit').text('');
        $('#perUnitName').text('');
        $('#isConsumedAgainstFixedAsset').removeAttr('checked');
        $('#total').text('');
        dropDownItemType.enable(true);
        dropDownItem.enable(true);
        $('#projectId').val(projectId);
        $('#budgetId').val(budgetId);
        $('#budgetItem').val(budgetItem);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function onChangeItem() {
        var itemId = dropDownItem.value();
        if (itemId == '') {
            $('#unit').text('');
            $('#perUnitName').text('');
            return;
        }
        $('#unit').text(dropDownItem.dataItem().unit);
        $('#perUnitName').text('per ' + dropDownItem.dataItem().unit);
    }

    function onChangeItemType() {
        var itemTypeId = dropDownItemType.value();
        if (itemTypeId == '') {
            return;
        }

        $('#unit').text('');
        $('#perUnitName').text('');
        dropDownItem.setDataSource(getKendoEmptyDataSource());

        var budgetId = $('#budgetId').attr('value');
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'budgBudgetDetails', action: 'getItemListBudgetDetails')}?budgetId=" + budgetId + "&itemTypeId=" + itemTypeId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownItem.setDataSource([]);
                } else {
                    dropDownItem.setDataSource(data.itemList);
                    dropDownItem.value('');
                }
            }, complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreCondition() {
        if (!validateForm($("#budgetDetailsForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitBudgetDetails() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'budgBudgetDetails', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'budgBudgetDetails', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#budgetDetailsForm").serialize(),
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
                    var previousTotal = parseInt(budgetDetailsGrid.total);
                    // re-arranging serial
                    var firstSerial = 1;
                    if (budgetDetailsGrid.rows.length > 0) {
                        firstSerial = budgetDetailsGrid.rows[0].cell[0];
                        regenerateSerial($(budgetDetailsGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    budgetDetailsGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        budgetDetailsGrid.rows.pop();
                    }

                    budgetDetailsGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(budgetDetailsGrid);
                } else if (result.entity != null) { // updated existing
                    updateListModel(budgetDetailsGrid, result.entity, 0);
                    $("#flex1").flexAddData(budgetDetailsGrid);
                }
                resetFormBudgetDetails();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "Item Type", name: "itemTypeId", width: 120, sortable: false, align: "left"},
                        {display: "Item", name: "itemId", width: 270, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 100, sortable: false, align: "right"},
                        {display: "Total Consumption", name: "totalConsumption", width: 110, sortable: false, align: "right"},
                        {display: "Balance", name: "balance", width: 100, sortable: false, align: "right"},
                        {display: "Estimated Rate", name: "rate", width: 120, sortable: false, align: "right"},
                        {display: "Total", name: "total", width: 130, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgBudgetDetails/select,/budgBudgetDetails/update">
                        {name: 'Edit', bclass: 'edit', onpress: editBudgetDetails},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgBudgetDetails/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteBudgetDetails},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Item", name: "itemId", width: 150, sortable: true, align: "left"},
                        {display: "Item Type", name: "itemTypeId", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Budget Details',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    preProcess: onLoadBudgetDetailsListJSON
                }
        );
    }

    function onLoadBudgetDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            budgetDetailsGrid = null;
        } else {
            budgetDetailsGrid = data;
        }
        return budgetDetailsGrid;
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget details') == false) {
            return false;
        }
        if (!confirm("Are you sure you want to delete the selected Item?")) {
            return false;
        }
        return true;
    }

    function deleteBudgetDetails(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var budgetDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'budgBudgetDetails', action: 'delete')}?id=" + budgetDetailsId,
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
            budgetDetailsGrid.total = parseInt(budgetDetailsGrid.total) - 1;
            removeEntityFromGridRows(budgetDetailsGrid, selectedRow);
            resetFormBudgetDetails();
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No budget details data found');
        }
    }

    function editBudgetDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget details') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgBudgetDetails', action: 'select')}?id=" + budgetDetailsId,
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
            populateBudgetDetailsForEdit(data);
        }
    }

    function populateBudgetDetailsForEdit(data) {
        resetFormBudgetDetails();
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownItem.setDataSource(data.itemList);
        dropDownItem.value(entity.itemId);
        itemQuantity.value(entity.quantity);
        $('#isConsumedAgainstFixedAsset').attr('checked', entity.isConsumedAgainstFixedAsset);
        dropDownItemType.value(data.item.itemTypeId);
        estimatedRate.value(entity.rate);
        $('#total').text(entity.total);
        $('#comments').val(entity.comments);
        dropDownItem.enable(false);
        dropDownItemType.enable(false);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        $('#unit').text(dropDownItem.dataItem().unit);
        $('#perUnitName').text('per ' + dropDownItem.dataItem().unit);

        calculateTotal();
    }

    function executePreConditionForAddDetails(ids) {
        if (ids.length == 0) {
            showError("Please select a row of details to create purchase request");
            return false;
        }
        return true;
    }

    window.onload = populateBudgetDetailsGridOnPageLoad();

    function populateBudgetDetailsGridOnPageLoad() {
        var strUrl = "${createLink(controller:'budgBudgetDetails', action: 'list')}?budgetId=" + budgetId;
        $("#flex1").flexOptions({url: strUrl});

        if (budgetDetailsGrid) {
            $("#flex1").flexAddData(budgetDetailsGrid);
        }
        var gridTitle;
        if (budgetId) {
            gridTitle = "Budget Details for [Budget No - " + budgetId + " ]";
        }
        else {
            gridTitle = "All Budget Details";
        }
        $('div.mDiv > div.ftitle').text(gridTitle);
    }

    function calculateTotal() {
        var rate = $('#rate').val();
        var quantity = $('#quantity').val();
        var total = parseFloat(rate * quantity);
        if (total > 0) {
            $('#total').text(total.toFixed(4));
        }
        else {
            $('#total').text(0);
        }
    }

</script>