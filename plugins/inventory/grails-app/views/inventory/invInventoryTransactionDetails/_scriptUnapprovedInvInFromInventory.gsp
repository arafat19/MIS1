<script language="javascript">
    var inventoryInDetailsListModel, dropDownItemId, numericActualQuantity, transactionDate;
    var output =${output ?output : ''};

    $(document).ready(function () {
        initGrid();
        onLoadInventoryIn();
    });

    function onLoadInventoryIn() {

        initializeForm($("#inventoryInDetailsForm"), onSubmitInventory);

        $('#actualQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"

        });
        numericActualQuantity = $("#actualQuantity").data("kendoNumericTextBox");

        if (output.isError) {
            showError(output.message);
            return
        }

        inventoryInDetailsListModel = output.gridObj;
        populateInventoryIn(output.inventoryInMap);
        transactionDate = output.transactionDate;
        $("#transactionDate").html(transactionDate);
        populateFlex();

        // update page title
        $(document).attr('title', "MIS - Inventory-In from another Inventory");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryInFromInventory");
    }

    function calculateShrinkage() {
        var suppliedQuantity;
        var actualQuantity;

        suppliedQuantity = $("#suppliedQuantity").text();
        actualQuantity = numericActualQuantity.value();

        //checking valid supplied quantity
        if (isNaN(suppliedQuantity) || suppliedQuantity <= 0) {
            showError("Invalid Supplied Quantity!");
            $("#suppliedQuantity").focus();
            return false;
        }

        //checking valid actual quantity
        if (isNaN(actualQuantity) || actualQuantity <= 0) {
            showError("Invalid Actual Quantity!");
            $("#actualQuantity").focus();
            return false;
        }

        suppliedQuantity = parseFloat(suppliedQuantity);
        actualQuantity = parseFloat(actualQuantity);

        //actual quantity can't be bigger than Inventory out Quantity
        if (actualQuantity > suppliedQuantity) {
            showError("Actual Quantity can't be bigger than Inventory out Quantity");
            $("#actualQuantity").focus();
            return false;
        }
        return true;
    }

    function populateInventoryIn(inventoryInMap) {
        if (!inventoryInMap) {
            return false;
        }
        $('#inventoryTransactionId').val(inventoryInMap.inventoryTransactionId);
        $('#transactionId').html(inventoryInMap.transactionId);
        $('#transactionIdLabel').html(inventoryInMap.inventoryTransactionId);
        $('#inventoryId').val(inventoryInMap.inventoryId);
        $('#inventoryName').html(inventoryInMap.inventoryName);
        $('#fromInventoryName').html(inventoryInMap.fromInventoryName);
        return true;
    }

    function onChangeItem() {
        $('#suppliedQuantity').text('');
        $('#unit').text('');
        $('#unitForSupplied').text('');

        if (dropDownItemId.value() == '') {
            $('#unit').text('');
            $('#unitForSupplied').text('');
            return false;
        }
        var unit = dropDownItemId.dataItem().unit;
        var quantity = dropDownItemId.dataItem().quantity;

        $('#suppliedQuantity').text(quantity);
        $('#unit').text(unit);
        $('#unitForSupplied').text(' ' + unit);
    }

    function executePreCondition() {
        if (!validateForm($("#inventoryInDetailsForm"))) {
            return false;
        }
        if (calculateShrinkage() == false) {
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

        var data = jQuery("#inventoryInDetailsForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'createInventoryInDetailsFromInventory')}";
        } else {
            actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'updateInventoryInDetailsFromInventory')}";
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
                    var previousTotal = parseInt(inventoryInDetailsListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryInDetailsListModel.rows.length > 0) {
                        firstSerial = inventoryInDetailsListModel.rows[0].cell[0];
                        regenerateSerial($(inventoryInDetailsListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    inventoryInDetailsListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryInDetailsListModel.rows.pop();
                    }

                    inventoryInDetailsListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryInDetailsListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(inventoryInDetailsListModel, result.entity, 0);
                    $("#flex1").flexAddData(inventoryInDetailsListModel);
                }

                resetForCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForCreateAgain() {
        clearErrors($("#inventoryInDetailsForm"));
        $('#create').html("<span class='k-icon k-i-plus'></span>Receive");

        $('#id').val('');
        $('#version').val('');
        $('#transactionDetailsId').attr('default_value', '');
        $('#transactionDetailsId').reloadMe();
        dropDownItemId.value('');
        dropDownItemId.enable(true);
        $('#suppliedQuantity').text('');
        numericActualQuantity.value('');
        $('#comments').val('');
        $('#unit').html('');
        $('#unitForSupplied').html('');
        $("#transactionDate").html(transactionDate);
    }

    function resetForm() {
        clearForm($("#inventoryInDetailsForm"), dropDownItemId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Receive");
        $('#suppliedQuantity').text('');
        $('#transactionDetailsId').attr('default_value', '');
        $('#transactionDetailsId').reloadMe();
        dropDownItemId.enable(true);
        $('#unit').text('');
        $('#unitForSupplied').text('');
        populateInventoryIn(output.inventoryInMap);
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Chalan", name: "id", width: 50, sortable: false, align: "left"},
                        {display: "Item", name: "itemName", width: 150, sortable: false, align: "left"},
                        {display: "Supplied Quantity", name: "suppliedQuantity", width: 130, sortable: false, align: "right"},
                        {display: "Actual Quantity", name: "numericActualQuantity", width: 130, sortable: false, align: "right"},
                        {display: "Shrinkage", name: "shrinkage", width: 130, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"},
                        {display: "Updated By", name: "updatedBy", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/selectInventoryInDetailsFromInventory,/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory">
                        {name: 'Edit', bclass: 'edit', onpress: editInventoryIn},
                        </app:ifAllUrl>
                        <sec:access url="/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInventoryInDetails},
                        </sec:access>
                        <sec:access url="/invInventoryTransactionDetails/approveInventoryInDetailsFromInventory">
                        {name: 'Approve', bclass: 'approve', onpress: approveInventoryIn},
                        </sec:access>

                        {name: 'Report', bclass: 'addDoc', onpress: getInvoiceInventoryIn},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Chalan", name: "iitd.id", width: 180, sortable: true, align: "left"},
                        {display: "Item", name: "item.name", width: 180, sortable: true, align: "left"},
                        {display: "Actual Quantity", name: "iitd.actual_quantity", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "iitd.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unapproved Item(s) From Inventory',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 40,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
//                            checkGrid();
                    },
                    customPopulate: onLoadInventoryInListJSON
                }
        );
    }

    function approveInventoryIn(com, grid) {
        if (executePreConditionForApprove() == false) {
            return;
        }
        showLoadingSpinner(true);

        var inventoryInDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransactionDetails', action: 'approveInventoryInDetailsFromInventory')}?id=" + inventoryInDetailsId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForApprove() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return false;
        }
        if (!confirm('Do you want to approve the selected item?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForApprove(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            inventoryInDetailsListModel.total = parseInt(inventoryInDetailsListModel.total) - 1;
            removeEntityFromGridRows(inventoryInDetailsListModel, selectedRow);
            resetForCreateAgain();
        } else {
            showError(data.message);
        }
    }

    function deleteInventoryInDetails(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryDetailId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransactionDetails', action: 'deleteInventoryInDetailsFromInventory')}?id=" + inventoryDetailId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected item?')) {
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
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            inventoryInDetailsListModel.total = parseInt(inventoryInDetailsListModel.total) - 1;
            removeEntityFromGridRows(inventoryInDetailsListModel, selectedRow);
            resetForCreateAgain();
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
            showInfo('No inventory-in data found');
        }
    }


    function editInventoryIn(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        var inventoryInId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryInDetailsFromInventory')}?id=" + inventoryInId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateInventoryInDetails(data);
        }
    }

    function populateInventoryInDetails(data) {
        clearErrors($("#inventoryInDetailsForm"));
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#inventoryTransactionId').val(entity.inventoryTransactionId);
        $('#inventoryId').val(entity.inventoryId);
        $('#suppliedQuantity').text(entity.suppliedQuantity);
        numericActualQuantity.value(entity.actualQuantity);
        $('#comments').val(entity.comments);

        $('#transactionDetailsId').attr('default_value', entity.itemId);
        $('#transactionDetailsId').attr('supplied_quantity', entity.suppliedQuantity);
        $('#transactionDetailsId').reloadMe(disableDropDownAndShowQuantitySpan);

        $('#transactionDate').html(data.transactionDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    // disable item dropDown as well as show item actual quantity in the span
    function disableDropDownAndShowQuantitySpan(){
        onChangeItem();
        dropDownItemId.enable(false);
    }

    function onLoadInventoryInListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryInDetailsListModel = getEmptyGridModel();
        } else {
            inventoryInDetailsListModel = data;
        }
        $("#flex1").flexAddData(inventoryInDetailsListModel);
    }

    function getInvoiceInventoryIn(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryInId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invReport', action: 'showInvoice')}?id=" + inventoryInId;
        $.history.load(formatLink(loc));
        return false;
    }

    function populateFlex() {
        var inventoryInId = $('#inventoryTransactionId').val();
        var strUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'listUnapprovedInvInFromInventory')}?inventoryTransactionId=" + inventoryInId;
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryInDetailsListModel) {
            $("#flex1").flexAddData(inventoryInDetailsListModel);
        }
    }

</script>
