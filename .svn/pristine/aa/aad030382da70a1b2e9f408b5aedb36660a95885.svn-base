<script type="text/javascript">
    var inventoryConsumptionGrid, dropDownInventoryType, dropDownInventory;
    var output = ${output ?output : ''};
    var budgetListModel = false;

    $(document).ready(function () {
        onLoadInventoryConsumption();
    });


    function onLoadInventoryConsumption() {
        initializeForm($("#frmInventoryConsumption"), saveInventoryConsumption);
        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, null);

        if (output.isError) {
            showError(output.message);
            return
        }

        initConsumptionGrid();
        initFlexBudget();

        inventoryConsumptionGrid = output.gridObjConsumption;
        budgetListModel = output.gridObjBudget;

        populateConsumptionGrid();
        populateBudgetGrid();

        // update page title
        $(document).attr('title', "MIS - Inventory-Consumption");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryConsumption");
    }

    // To populate Inventory List
    function updateInventoryList() {
        var inventoryTypeId = dropDownInventoryType.value();
        if (inventoryTypeId == '') {
            dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, null));
            dropDownInventory.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryByType')}?inventoryTypeId=" + inventoryTypeId,
            success: function (data) {
                updateInventoryListDropDown(data);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateInventoryListDropDown(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownInventory.setDataSource(data.inventoryList);
    }

    function preConditionOfInventoryConsumption() {
        if (!validateForm($("#frmInventoryConsumption"))) {
            return false;
        }

        if (dropDownInventoryType.value() == '') {
            showError('Please select an inventory type');
            return false;
        }
        if (dropDownInventory.value() == '') {
            showError('Please select an inventory');
            return false;
        }
        if ($('#budgetItem').val().isEmpty()) {
            showError('Please select a budget');
            return false;
        }
        return true;
    }

    function saveInventoryConsumption() {

        if (!preConditionOfInventoryConsumption()) {
            return false;
        }

        var data = $("#frmInventoryConsumption").serialize();
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'createInventoryConsumption')}";
        } else {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'updateInventoryConsumption')}";
        }

        showLoadingSpinner(true);
        $.ajax({
            data: data,
            url: actionUrl,
            success: executePostCondition,
            complete: function (XMLHttpOrder, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
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
                    var previousTotal = parseInt(inventoryConsumptionGrid.total);
                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryConsumptionGrid.rows.length > 0) {
                        firstSerial = inventoryConsumptionGrid.rows[0].cell[0];
                        regenerateSerial($(inventoryConsumptionGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    inventoryConsumptionGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryConsumptionGrid.rows.pop();
                    }

                    inventoryConsumptionGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryConsumptionGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(inventoryConsumptionGrid, result.entity, 0);
                    $("#flex1").flexAddData(inventoryConsumptionGrid);
                }

                resetCreateForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetCreateForm() {
        clearForm($("#frmInventoryConsumption"), dropDownInventoryType);
        dropDownInventoryType.enable(true);
        dropDownInventory.enable(true);
        dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, null));
        $('#lblProjectName').text('');
        $('#lblBudgetItem').text('');
        $('#lblBudgetDetails').text('');
        $('#itemCount').val(0);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexBudget() {
        $("#flexBudget").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Line Item", name: "budget_item", width: 90, sortable: false, align: "left"},
                        {display: "Details", name: "details", width: 220, sortable: false, align: "left"},
                        {display: "Project Id", name: "projectId", width: 30, sortable: false, align: "left", hide: true},
                        {display: "Project Name", name: "projectName", width: 50, sortable: false, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Add', bclass: 'addItem', onpress: addBudget},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadConsumptionGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "ALL", name: "budgetItem", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Search Budget',
                    useRp: false,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getFullGridHeight() - 35,
                    resizable: false,
                    isSearchOpen: true,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function initConsumptionGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Transaction Id", name: "id", width: 80, sortable: false, align: "right"},
                        {display: "Inventory", name: "inventory.name", width: 150, sortable: false, align: "left"},
                        {display: "Budget Line Item", name: "budget_item", width: 150, sortable: false, align: "left"},
                        {display: "Item Count", name: "itemCount", width: 80, sortable: false, align: "left"},
                        {display: "Approved", name: "totalApprove", width: 80, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 110, sortable: false, align: "left"},
                        {display: "Updated By", name: "updatedBy", width: 110, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransaction/selectInventoryConsumption,/invInventoryTransaction/updateInventoryConsumption">
                        {name: 'Edit', bclass: 'edit', onpress: editInventoryConsumption},
                        </app:ifAllUrl>
                        <sec:access url="/invInventoryTransaction/deleteInventoryConsumption">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInventoryConsumption},
                        </sec:access>
                        {name: 'New', bclass: 'addItem', onpress: addInventoryConsumptionDetails},
                        {name: 'Approved', bclass: 'fgrid-approve-item', onpress: approvedAddInventoryConsumptionDetails},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Inventory", name: "inventory.name", width: 150, sortable: true, align: "left"},
                        {display: "Budget Line Item", name: "budget.budget_item", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Inventory-consumption',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 55,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadInventoryConsumptionListJSON
                }
        );
    }

    function executePreConditionForAddBudget() {
        if (executeCommonPreConditionForSelect($('#flexBudget'), 'budget') == false) {
            return false;
        }

        var itemCount = $("#itemCount").val();
        if (itemCount > 0) {
            showError("Budget not changeable for the transaction");
            return false;
        }
        return true;
    }

    function addBudget(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForAddBudget() == false) {
            return;
        }

        var lblProjectName = $(ids[0]).find('td').eq(4).find('div').text();
        var lblBudgetItem = $(ids[0]).find('td').eq(1).find('div').text();
        var lblBudgetDetails = $(ids[0]).find('td').eq(2).find('div').text();
        var detailsLength = 100;

        if (lblBudgetDetails.length > detailsLength) {
            lblBudgetDetails = lblBudgetDetails.substring(0, detailsLength);
            lblBudgetDetails = lblBudgetDetails + '...'
        }

        $('#lblProjectName').text(lblProjectName);
        $('#lblBudgetItem').text(lblBudgetItem);
        $('#lblBudgetDetails').text(lblBudgetDetails);
        $('#budgetItem').val(lblBudgetItem);    // hidden field
    }

    function reloadConsumptionGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flexBudget').flexOptions({query: ''}).flexReload();
        }
    }

    function addInventoryConsumptionDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'consumption transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnApprovedInventoryConsumptionDetails')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function approvedAddInventoryConsumptionDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'consumption transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showApprovedInventoryConsumptionDetails')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function editInventoryConsumption(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'consumption transaction') == false) {
            return;
        }
        resetCreateForm();
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'selectInventoryConsumption')}?id=" + inventoryTransactionId,
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
            populateInventoryTransactionDetails(data);
        }
    }

    function populateInventoryTransactionDetails(data) {
        resetCreateForm();
        var entity = data.invInventoryTransaction;

        var budgetDetails = data.budgetDetails;
        var detailsLength = 100;

        if (budgetDetails.length > detailsLength) {
            budgetDetails = budgetDetails.substring(0, detailsLength);
            budgetDetails = budgetDetails + '...'
        }
        dropDownInventory.value(entity.inventoryId);
        $('#lblProjectName').text(data.projectName);
        $('#lblBudgetItem').text(data.budgetItem);
        $('#lblBudgetDetails').text(budgetDetails);
        $('#budgetItem').val(data.budgetItem);    // hidden field

        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#comments').val(entity.comments);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");

        dropDownInventoryType.value(entity.inventoryTypeId);
        dropDownInventory.setDataSource(data.inventoryList);
        dropDownInventory.value(entity.inventoryId);
        var itemCount = parseInt(entity.itemCount);
        $('#itemCount').val(entity.itemCount);
        if (itemCount > 0) {
            dropDownInventoryType.enable(false);
            dropDownInventory.enable(false);
        }
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'consumption transaction') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Inventory-Consumption transaction?')) {
            return false;
        }
        return true;
    }
    function deleteInventoryConsumption(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'deleteInventoryConsumption')}?id=" + inventoryTransactionId,
            success: executePostConditionForDelete,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
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
            resetCreateForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            inventoryConsumptionGrid.total = parseInt(inventoryConsumptionGrid.total) - 1;
            removeEntityFromGridRows(inventoryConsumptionGrid, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadInventoryConsumptionListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryConsumptionGrid = null;
        } else {
            inventoryConsumptionGrid = data;
        }
        return inventoryConsumptionGrid;
    }

    function populateConsumptionGrid() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'showInventoryConsumption')}";
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryConsumptionGrid) {
            $("#flex1").flexAddData(inventoryConsumptionGrid);
        }
    }

    function populateBudgetGrid() {
        var strBudgetUrl = "${createLink(controller: 'budgBudget', action: 'getBudgetGridByInventory')}";
        $("#flexBudget").flexOptions({url: strBudgetUrl});
        if (budgetListModel) {
            $("#flexBudget").flexAddData(budgetListModel);
        }
    }

    var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryConsumption')}";
    $("#flex1").flexOptions({url: strUrl});

    if (output) {
        $("#flex1").flexAddData(inventoryConsumptionGrid);
    }

</script>
