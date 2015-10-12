<script type="text/javascript">
    var dropDownFromInventoryType, dropDownFromInventory, transferDate,
            dropDownToInventoryType, dropDownToInventory;
    var inventoryOutListModel = false;

    $(document).ready(function () {
        onLoadInventoryOut();
    });

    function onLoadInventoryOut() {
        var output =${output ? output : ''};

        initializeForm($("#inventoryOutForm"), onSubmitInventory);

        $('#inventoryId').kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownFromInventory = $('#inventoryId').data("kendoDropDownList");
        dropDownFromInventory.setDataSource(getKendoEmptyDataSource(dropDownFromInventory, null));

        $('#transactionEntityId').kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownToInventory = $('#transactionEntityId').data("kendoDropDownList");
        dropDownToInventory.setDataSource(getKendoEmptyDataSource(dropDownToInventory, null));

        transferDate = $("#transactionDate").data("kendoDatePicker");

        initFlexInvOut();
        var isError = output.isError;
        if (isError == 'true') {
            var message = output.message;
            showError(message);
            return;
        }
        inventoryOutListModel = output.gridObj;
        populateInvOutGrid();
        // update page title
        $(document).attr('title', "MIS - Create Inventory-Out");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryOut");

    }


    // To populate Inventory List
    function updateFromInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownFromInventoryType.value();
        dropDownFromInventory.value('');
        if (inventoryTypeId == '') {
            dropDownFromInventory.setDataSource(getKendoEmptyDataSource(dropDownFromInventory, null));
            dropDownFromInventory.value('');
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

    function updateInventoryListDropDown(data, defaultInventoryTypeId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultInventoryTypeId) defaultInventoryTypeId = '';
        dropDownFromInventory.setDataSource(data.inventoryList);
    }

    // To populate Inventory List
    function updateToInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownToInventoryType.value();
        dropDownToInventory.value('');
        if (inventoryTypeId == '') {
            dropDownToInventory.setDataSource(getKendoEmptyDataSource(dropDownToInventory, null));
            dropDownToInventory.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listAllInventoryByType')}?inventoryTypeId=" + inventoryTypeId,
            success: function (data) {
                updateToInventoryListDropDown(data, defaultInventoryTypeId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateToInventoryListDropDown(data, defaultInventoryTypeId) {
        var result = data;
        if (result.isError) {
            showError(result.message);
        } else {
            dropDownToInventory.setDataSource(result.inventoryList);
            if (defaultInventoryTypeId) {
                dropDownToInventory.value(defaultInventoryTypeId);
            }
        }

    }

    function executePreCondition() {
        if (!validateForm($("#inventoryOutForm"))) {
            return false;
        }

        if (dropDownFromInventory.value() == dropDownToInventory.value()) {
            showError("Inventory-Out transaction can't be occurred in same inventory");
            return false;
        }

        return true;
    }

    function onSubmitInventory() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        var data = jQuery("#inventoryOutForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'createInventoryOut')}";
        } else {
            actionUrl = "${createLink(controller: 'invInventoryTransaction', action: 'updateInventoryOut')}";
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
                setButtonDisabled($('.save'), false);
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
                    var previousTotal = parseInt(inventoryOutListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryOutListModel.rows.length > 0) {
                        firstSerial = inventoryOutListModel.rows[0].cell[0];
                        regenerateSerial($(inventoryOutListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    inventoryOutListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryOutListModel.rows.pop();
                    }

                    inventoryOutListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryOutListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(inventoryOutListModel, result.entity, 0);
                    $("#flex1").flexAddData(inventoryOutListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#inventoryOutForm"), transferDate);
        transferDate.enable(true);
        dropDownFromInventoryType.enable(true);
        dropDownFromInventory.enable(true);
        dropDownToInventoryType.enable(true);
        dropDownToInventory.enable(true);

        dropDownFromInventory.setDataSource(getKendoEmptyDataSource(dropDownFromInventory, null));
        dropDownToInventory.setDataSource(getKendoEmptyDataSource(dropDownToInventory, null));

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexInvOut() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Transaction Id", name: "id", width: 80, sortable: false, align: "right"},
                        {display: "From Inventory", name: "inventoryId", width: 150, sortable: false, align: "left"},
                        {display: "To Inventory", name: "transactionEntityId", width: 180, sortable: false, align: "left"},
                        {display: "Budget Line Item", name: "budgetItem", width: 180, sortable: false, align: "left"},
                        {display: "Item Count", name: "itemCount", width: 70, sortable: false, align: "left"},
                        {display: "Approved", name: "approvedItemCount", width: 70, sortable: false, align: "left"},
                        {display: "Transfer Date", name: "transactionDate", width: 80, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 100, sortable: false, align: "left"} ,
                        {display: "Updated By", name: "updatedBy", width: 100, sortable: false, align: "left"}

                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransaction/selectInventoryOut,/invInventoryTransaction/updateInventoryOut">
                        {name: 'Edit', bclass: 'edit', onpress: editInventoryOut},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransaction/deleteInventoryOut">
                        {name: 'Delete', bclass: 'delete', onpress: deletenventoryOut},
                        </app:ifAllUrl>
                        {name: 'New', bclass: 'addItem', onpress: addInventoryOutDetails},
                        {name: 'Approved', bclass: 'approved', onpress: approvedInventoryOutDetails},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "From Inventory", name: "frm_inv.name", width: 180, sortable: true, align: "left"},
                        {display: "To Inventory", name: "to_inv.name", width: 180, sortable: true, align: "left"}

                    ],
                    sortname: "it.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Inventory-Out',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 35,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },

                    customPopulate: onLoadInventoryOutListJSON

                }
        );
    }

    function deletenventoryOut(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransaction', action: 'deleteInventoryOut')}?id=" + inventoryOutId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory-out') == false) {
            return false;
        }

        if (!confirm('Are you sure you want to delete the selected Inventory-Out?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            inventoryOutListModel.total = parseInt(inventoryOutListModel.total) - 1;
            removeEntityFromGridRows(inventoryOutListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function addInventoryOutDetails(com, grid) {
        if (executePreConditionForAddDetails() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnApprovedInventoryOutDetails')}?inventoryOutId=" + inventoryOutId;
        $.history.load(formatLink(loc));
        return false;
    }

    function executePreConditionForAddDetails() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory-out') == false) {
            return false;
        }
        return true;
    }
    function approvedInventoryOutDetails(com, grid) {
        if (executePreConditionForApprovedDetails() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showApprovedInventoryOutDetails')}?inventoryOutId=" + inventoryOutId;
        $.history.load(formatLink(loc));
        return false;
    }

    function executePreConditionForApprovedDetails() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory-out') == false) {
            return false;
        }
        return true;
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function executePreConditionForEdit() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory-out') == false) {
            return false;
        }
        return true;
    }
    function editInventoryOut(com, grid) {
        if (executePreConditionForEdit() == false) {
            return;
        }
        resetForm();
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'selectInventoryOut')}?id=" + inventoryOutId,
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
            populateInventoryOut(data);
        }
    }

    function populateInventoryOut(data) {
        clearErrors($("#inventoryOutForm"));
        var entity = data.entity;
        var invMap = data.inventoryOutMap;

        $('#id').val(entity.id);
        $('#version').val(data.version);

        dropDownFromInventoryType.value(invMap.fromInventoryTypeId);
        dropDownFromInventory.setDataSource(invMap.invnetoryList);
        dropDownFromInventory.value(entity.inventoryId);

        dropDownToInventoryType.value(invMap.toInventoryTypeId);
        dropDownToInventory.setDataSource(invMap.transactionEntityList);
        dropDownToInventory.value(entity.transactionEntityId);

        $('#budgetItem').val(data.budgetItem);
        $('#comments').val(entity.comments);
        $('#transactionDate').val(data.transactionDate);
        var itemCount = parseInt(entity.itemCount);
        if (itemCount > 0) {
            transferDate.enable(false);
            dropDownFromInventoryType.enable(false);
            dropDownFromInventory.enable(false);
            dropDownToInventoryType.enable(false);
            dropDownToInventory.enable(false);
        }

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function onLoadInventoryOutListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryOutListModel = getEmptyGridModel();
        } else {
            inventoryOutListModel = data;
        }
        $("#flex1").flexAddData(inventoryOutListModel);
    }

    function populateInvOutGrid() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryOut')}";
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryOutListModel) {
            $("#flex1").flexAddData(inventoryOutListModel);
        }
    }


</script>
