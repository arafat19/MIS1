<script type="text/javascript">
    var dropDownInventoryType, dropDownInventory, dropDownSupplier, dropDownPO;
    var inventoryInListModel;
    $(document).ready(function () {
        onLoadInventoryIn();
    });

    function onLoadInventoryIn() {
        var output =${output ?output : ''};

        initializeForm($("#inventoryInForm"), onSubmitInventory);
        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, null);
        dropDownPO = initKendoDropdown($('#purchaseOrderId'), null, null, null);

        if (output.isError) {
            showError(output.message);
            return
        }
        inventoryInListModel = output.gridObj;
        initInventoryInGrid();

        // update page title
        $(document).attr('title', "MIS - Inventory In");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryInFromSupplier");
    }

    function executePreCondition() {
        if (!validateForm($("#inventoryInForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitInventory() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#inventoryInForm").serialize();

        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'createInventoryInFromSupplier')}";
        } else {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'updateInventoryInFromSupplier')}";
        }

        jQuery.ajax({
            type: 'post',
            data: data,
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
                    var previousTotal = parseInt(inventoryInListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryInListModel.rows.length > 0) {
                        firstSerial = inventoryInListModel.rows[0].cell[0];
                        regenerateSerial($(inventoryInListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    inventoryInListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryInListModel.rows.pop();
                    }

                    inventoryInListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryInListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(inventoryInListModel, result.entity, 0);
                    $("#flex1").flexAddData(inventoryInListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#inventoryInForm"), dropDownInventoryType);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");

        dropDownInventoryType.enable(true);
        dropDownInventory.enable(true);
        dropDownSupplier.enable(true);
        dropDownPO.enable(true);

        dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, null));
        dropDownPO.setDataSource(getKendoEmptyDataSource(dropDownPO, null));
    }

    function resetSupplierList() {
        dropDownSupplier.value('');
        dropDownPO.setDataSource(getKendoEmptyDataSource(dropDownPO, null));
    }

    function initInventoryInGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Transaction ID", name: "id", width: 80, sortable: false, align: "right"},
                        {display: "Inventory", name: "inventoryName", width: 200, sortable: false, align: "left"},
                        {display: "Supplier", name: "supplierName", width: 170, sortable: false, align: "left"},
                        {display: "PO No", name: "poId", width: 50, sortable: false, align: "right"},
                        {display: "Item Count", name: "itemCount", width: 60, sortable: false, align: "right"},
                        {display: "Approved", name: "totalApproved", width: 60, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 150, sortable: false, align: "left"},
                        {display: "Updated By", name: "updatedBy", width: 150, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransaction/selectInventoryInFromSupplier,/invInventoryTransaction/updateInventoryInFromSupplier">
                        {name: 'Edit', bclass: 'edit', onpress: editInventoryIn},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransaction/deleteInventoryInFromSupplier">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInventoryIn},
                        </app:ifAllUrl>
                        {name: 'New', bclass: 'addItem', onpress: addInventoryInDetails},
                        {name: 'Approved', bclass: 'fgrid-approve-item', onpress: showApprovedInventoryInDetails},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    searchitems: [
                        {display: "Inventory", name: "inventory.name", width: 180, sortable: true, align: "left"},
                        {display: "Supplier", name: "supplier.name", width: 180, sortable: true, align: "left"},
                        {display: "PO", name: "iit.transaction_id", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Inventory In From Supplier',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
//                            checkGrid();
                    },

                    customPopulate: onLoadInventoryInListJSON
                }
        );
    }

    function onLoadInventoryInListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryInListModel = getEmptyGridModel();
        } else {
            inventoryInListModel = data;
        }
        $("#flex1").flexAddData(inventoryInListModel);
    }

    function deleteInventoryIn(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransaction', action: 'deleteInventoryInFromSupplier')}?id=" + inventoryId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Inventory In?')) {
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
            inventoryInListModel.total = parseInt(inventoryInListModel.total) - 1;
            removeEntityFromGridRows(inventoryInListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function addInventoryInDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnapprovedInvInFromSupplier')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function showApprovedInventoryInDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showApprovedInvInFromSupplier')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No inventory in data found');
        }
    }

    function editInventoryIn(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return;
        }
        resetForm();
        var inventoryTransactionId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'selectInventoryInFromSupplier')}?id=" + inventoryTransactionId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    //To populate PO List
    function updatePurchaseOrder(defaultPurchaseOrderId) {
        var transactionEntityId = dropDownSupplier.value();
        var inventoryTypeId = dropDownInventoryType.value();
        var inventoryId = dropDownInventory.value();

        if (inventoryTypeId == '') {
            showError('Please select inventory type.');
            return false;
        }
        if (inventoryId == '') {
            showError('Please select inventory.');
            dropDownSupplier.value('');
            return false;
        }
        if (transactionEntityId == '') {
            dropDownPO.value('');
            dropDownPO.setDataSource(getKendoEmptyDataSource(dropDownPO, null));
            return false;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listPOBySupplier')}?supplierId=" + transactionEntityId + "&inventoryId=" + inventoryId,
            success: function (data) {
                updatePODropDown(data, defaultPurchaseOrderId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updatePODropDown(data, defaultPurchaseOrderId) {
        var result = data;
        if (result.isError) {
            showError(result.message);
        } else {
            dropDownPO.setDataSource(result.lstPurchaseOrder);

            if (defaultPurchaseOrderId) {
                dropDownPO.value(defaultPurchaseOrderId);
            }
        }

    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownInventoryType.value();
        dropDownInventory.value('');
        dropDownSupplier.value('');
        dropDownPO.value('');
        if (inventoryTypeId == '') {
            dropDownSupplier.value('');
            dropDownPO.value('');
            dropDownPO.setDataSource(getKendoEmptyDataSource(dropDownPO, null));
            dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, null));
            dropDownInventory.refresh();
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

    function updateDependency() {
        dropDownSupplier.value('');
        dropDownPO.value('');
        dropDownPO.setDataSource(getKendoEmptyDataSource());
    }

    function updateInventoryListDropDown(data, defaultInventoryTypeId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultInventoryTypeId) defaultInventoryTypeId = '';
        dropDownInventory.setDataSource(data.inventoryList);
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateInventoryIn(data);
        }
    }

    function populateInventoryIn(data) {
        clearErrors($("#inventoryInForm"));
        var entity = data.entity;
        var invInMap = data.inventoryInMap;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        dropDownInventoryType.value(entity.inventoryTypeId);
        dropDownInventory.setDataSource(invInMap.inventoryList);
        dropDownInventory.value(entity.inventoryId);
        dropDownPO.setDataSource(invInMap.purchaseOrderList);
        dropDownPO.value(entity.transactionId);
        dropDownSupplier.value(entity.transactionEntityId);

        $('#comments').val(entity.comments);

        var itemCount = parseInt(entity.itemCount);
        if (itemCount > 0) {
            dropDownInventoryType.enable(false);
            dropDownInventory.enable(false);
            dropDownSupplier.enable(false);
            dropDownPO.enable(false);
        }
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <sec:access url="/invInventoryTransaction/listInventoryInFromSupplier">
    var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryInFromSupplier')}";
    $("#flex1").flexOptions({url: strUrl});
    if (inventoryInListModel) {
        $("#flex1").flexAddData(inventoryInListModel);
    }
    </sec:access>

</script>
