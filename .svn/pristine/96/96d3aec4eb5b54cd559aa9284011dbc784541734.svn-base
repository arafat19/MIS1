<script type="text/javascript">
    var dropDownInventoryType, dropDownToInventory, dropDownFromInventory,
            dropDownTransDate, inventoryInFromInventoryGrid;
    var inventoryOutToInventoryListModel = false;

    $(document).ready(function () {
        onLoadInventoryInFromInventory();
        initGridData();
        populateFlex();
    });

    function onLoadInventoryInFromInventory() {
        var output =${output ?output : ''};
        initializeForm($("#frmInventoryInFromInventory"), saveInventoryInFromInventory);

        dropDownToInventory = initKendoDropdown($('#inventoryId'), null, null, null);
        dropDownFromInventory = initKendoDropdown($('#fromInventoryId'), null, null, null);
        dropDownTransDate = initKendoDropdown($('#transactionId'), "transaction_date_str", null, null);
        dropDownTransDate.setDataSource(getKendoEmptyDataSource(dropDownTransDate, null));

        initDateControl($("#transactionDate"));
        $("#transactionDate").val('');

        if (output.isError) {
            showError(output.message);
            return
        }
        //  resetTransactionForm();
        inventoryInFromInventoryGrid = output.gridObj;

        // update page title
        $(document).attr('title', "MIS - Inventory-In from Inventory");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryInFromInventory");
    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        // dropDownToInventory.enable(true);
        var inventoryTypeId = dropDownInventoryType.value();
        if (inventoryTypeId == '') {
            dropDownToInventory.setDataSource(getKendoEmptyDataSource(dropDownToInventory, null));
            dropDownFromInventory.setDataSource(getKendoEmptyDataSource(dropDownFromInventory, null));
            dropDownTransDate.setDataSource(getKendoEmptyDataSource(dropDownTransDate, null));
            dropDownToInventory.value('');
            dropDownFromInventory.value('');
            dropDownTransDate.value('');
            dropDownToInventory.refresh();
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryByType')}?inventoryTypeId=" + inventoryTypeId,
            success: function (data) {
                updateInventoryListDropDown(data, defaultInventoryTypeId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function populateTransferDateList() {
        dropDownTransDate.setDataSource(getKendoEmptyDataSource(dropDownTransDate));
        dropDownTransDate.value('');
        var inventoryId = dropDownFromInventory.value();
        var transactionEntityId = dropDownToInventory.value();

        if (inventoryId == '' || transactionEntityId == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invInventoryTransaction', action: 'listInvTransaction')}?inventoryId=" + inventoryId + "&transactionEntityId=" + transactionEntityId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownTransDate.setDataSource(getKendoEmptyDataSource(dropDownTransDate));
                } else {
                    dropDownTransDate.setDataSource(data.lstTransactionDates);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function updateInventoryListDropDown(data, defaultInventoryTypeId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultInventoryTypeId) defaultInventoryTypeId = '';
        dropDownToInventory.setDataSource(data.inventoryList);
        dropDownToInventory.value(defaultInventoryTypeId);
    }

    // To populate From Inventory List
    function updateFromInventoryList(defaultInventoryTypeId) {
        var inventoryId = dropDownToInventory.value();
        if (inventoryId == '') {
            dropDownFromInventory.setDataSource(getKendoEmptyDataSource());
            dropDownFromInventory.value('');
            dropDownTransDate.setDataSource(getKendoEmptyDataSource());
            dropDownTransDate.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryOfTransactionOut')}?inventoryId=" + inventoryId,
            success: function (data) {
                updateFromInventoryListDropDown(data, defaultInventoryTypeId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateFromInventoryListDropDown(data, defaultInventoryTypeId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultInventoryTypeId) defaultInventoryTypeId = '';
        dropDownFromInventory.setDataSource(data.lstInventory);
        dropDownFromInventory.value(defaultInventoryTypeId);
    }

    function preConditionOfInventoryInFromInventory() {
        if (!validateForm($("#frmInventoryInFromInventory"))) {
            return false;
        }
        var transferredDate = dropDownTransDate.dataItem().transfer_date;
        var receivedDate = $('#transactionDate').val();

        transferredDate = getDateObjectFromString(transferredDate);
        receivedDate = getDateObjectFromString(receivedDate);
        if (receivedDate < transferredDate) {
            showError('Transfer Date can not be greater than Received Date');
            return false;
        }

        if (receivedDate > new Date()) {
            showError('Received Date can not be future date');
            return false;
        }
        return true;
    }
    function saveInventoryInFromInventory() {
        if (!preConditionOfInventoryInFromInventory()) {
            return false;
        }
        var transactionId = dropDownTransDate.value();
        var inventoryId = dropDownToInventory.value();
        var transactionEntityId = dropDownFromInventory.value();
        var comments = $("#comments").val();
        var transactionDate = $("#transactionDate").val();

        var params = "?inventoryId=" + inventoryId + "&transactionId=" + transactionId + "&transactionEntityId=" + transactionEntityId + "&comments=" + comments + "&transactionDate=" + transactionDate;
        var id = $('#id').val();
        var version = $('#version').val();
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'createInventoryInFromInventory')}" + params;
        } else {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'updateInventoryInFromInventory')}" + params + '&id=' + id + '&version=' + version;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: actionUrl,
            success: executePostConditionForSave,
            complete: function (XMLHttpOrder, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForSave(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(inventoryInFromInventoryGrid.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryInFromInventoryGrid.rows.length > 0) {
                        firstSerial = inventoryInFromInventoryGrid.rows[0].cell[0];
                        regenerateSerial($(inventoryInFromInventoryGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    inventoryInFromInventoryGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryInFromInventoryGrid.rows.pop();
                    }

                    inventoryInFromInventoryGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryInFromInventoryGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(inventoryInFromInventoryGrid, result.entity, 0);
                    $("#flex1").flexAddData(inventoryInFromInventoryGrid);
                }

                resetTransactionForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetDisableFields() {
        dropDownInventoryType.enable(true);
        dropDownToInventory.enable(true);
        dropDownFromInventory.enable(true);
        dropDownTransDate.enable(true);
    }

    function resetTransactionForm() {
        resetDisableFields();
        clearForm($('#frmInventoryInFromInventory'), dropDownInventoryType);
        dropDownFromInventory.setDataSource(getKendoEmptyDataSource(dropDownFromInventory, null));
        dropDownToInventory.setDataSource(getKendoEmptyDataSource(dropDownToInventory, null));
        dropDownTransDate.setDataSource(getKendoEmptyDataSource(dropDownTransDate));

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initGridData() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Transaction ID", name: "iit.id", width: 75, sortable: false, align: "right"},
                        {display: "Transfer Date", name: "iit_out.transaction_date", width: 80, sortable: false, align: "left"},
                        {display: "Received Date", name: "iit.transaction_date", width: 80, sortable: false, align: "left"},
                        {display: "To Inventory", name: "toInventory", width: 170, sortable: false, align: "left"},
                        {display: "From Inventory", name: "fromInventory", width: 170, sortable: false, align: "left"},
                        {display: "Item Count", name: "itemCount", width: 60, sortable: false, align: "right"},
                        {display: "Approved", name: "approvedCount", width: 60, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"} ,
                        {display: "Updated By", name: "updatedBy", width: 120, sortable: false, align: "left"},
                        {display: "Transfer Id", name: "iit_out.id", width: 50, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransaction/selectInventoryInFromInventory,/invInventoryTransaction/updateInventoryInFromInventory">
                        {name: 'Edit', bclass: 'edit', onpress: editInventoryInFromInventory},
                        </app:ifAllUrl>
                        <sec:access url="/invInventoryTransaction/deleteInventoryInFromInventory">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInventoryInFromInventory},
                        </sec:access>
                        {name: 'New', bclass: 'addItem', onpress: addInventoryInDetails},
                        {name: 'Approved', bclass: 'approvedItem', onpress: showApprovedInventoryInDetails},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Transfer Id", name: "transactionId", width: 180, align: "left"},
                        {display: "To Inventory", name: "to_inventory.name", width: 180, align: "left"},
                        {display: "From Inventory", name: "from_inventory.name", width: 180, align: "left"}
                    ],
                    sortname: "iit.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Inventory-In from Inventory',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadInventoryOutListJSON
                }
        );
    }

    function addInventoryInDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnapprovedInvInFromInventory')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function showApprovedInventoryInDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showApprovedInvInFromInventory')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }


    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'transaction') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected transaction?')) {
            return false;
        }
        return true;
    }

    function editInventoryInFromInventory(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'transaction') == false) {
            return;
        }
        resetTransactionForm();
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'selectInventoryInFromInventory')}?id=" + inventoryTransactionId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }
    function enableTransactionForm() {

    }
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateInventoryTransaction(data);
        }
    }
    function populateInventoryTransaction(data) {
        resetTransactionForm();
        var entity = data.entity;
        var inventoryInMap = data.inventoryInMap;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownInventoryType.value(entity.inventoryTypeId);
        dropDownToInventory.setDataSource(inventoryInMap.invnetoryList);
        dropDownToInventory.value(entity.inventoryId);
        dropDownFromInventory.setDataSource(inventoryInMap.transactionEntityList);
        dropDownFromInventory.value(entity.transactionEntityId);
        dropDownTransDate.setDataSource(inventoryInMap.transactionList);
        dropDownTransDate.value(entity.transactionId);
        $("#transactionDate").val(inventoryInMap.transactionDate);
        $('#comments').val(entity.comments);
        if (entity.itemCount > 0) {
            dropDownInventoryType.enable(false);
            dropDownToInventory.enable(false);
            dropDownFromInventory.enable(false);
            dropDownTransDate.enable(false);
        }
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteInventoryInFromInventory(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var storeTransactionDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'deleteInventoryInFromInventory')}?id=" + storeTransactionDetailsId,
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
            resetTransactionForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            inventoryInFromInventoryGrid.total = parseInt(inventoryInFromInventoryGrid.total) - 1;
            removeEntityFromGridRows(inventoryInFromInventoryGrid, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadInventoryOutListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryInFromInventoryGrid = null;
        } else {
            inventoryInFromInventoryGrid = data;
        }
        return inventoryInFromInventoryGrid;
    }

    function populateFlex() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryInFromInventory')}";
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryInFromInventoryGrid) {
            $("#flex1").flexAddData(inventoryInFromInventoryGrid);
        }
    }

</script>
