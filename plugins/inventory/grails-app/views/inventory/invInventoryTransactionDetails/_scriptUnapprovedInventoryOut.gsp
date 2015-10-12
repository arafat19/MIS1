<script language="javascript">
    var inventoryOutDetailsListModel =${output ? output : ''};
    var previousMaterials, dropDownItemId, dropDownVehicle, numericActualQuantity, inventoryTransactionId;
    var itemId = '';

    $(document).ready(function () {
        onLoadInventoryOut();
    });

    function onLoadInventoryOut() {
        var isError = inventoryOutDetailsListModel.isError;
        if (isError == 'true') {
            var message = inventoryOutDetailsListModel.message;
            showError(message);
            return;
        }
        previousMaterials = null;
        initializeForm($("#inventoryOutDetailsForm"), onSubmitInventoryOutDetails);


        $('#actualQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"

        });
        numericActualQuantity = $("#actualQuantity").data("kendoNumericTextBox");

        populateInventoryOut(inventoryOutDetailsListModel.inventoryOutMap);

        $('#chalanNo').html('(Auto Generated)');

        // update page title
        $(document).attr('title', "MIS - Inventory-Out Item(s)");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryOut");

    }

    function populateInventoryOut(inventoryOutMap) {
        if (!inventoryOutMap) {
            return false;
        }
        inventoryTransactionId = inventoryOutMap.inventoryTransactionId;
        $('#inventoryTransactionId').val(inventoryOutMap.inventoryTransactionId);
        $('#purchaseOrderItem').text(inventoryOutMap.purchaseOrderId);
        $('#purchaseOrderId').val(inventoryOutMap.purchaseOrderId);
        $('#inventoryId').val(inventoryOutMap.inventoryId);
        $('#fromInventory').text(inventoryOutMap.fromInvName);
        $('#siteName').text(inventoryOutMap.toInvName);
        $('#lblBudgetLine').text(inventoryOutMap.budgetLineName);
        $('#transactionEntityId').val(inventoryOutMap.transactionEntityId);

        return true;
    }


    function executePreCondition() {
        if (!validateForm($("#inventoryOutDetailsForm"))) {
            return false;
        }
        if (checkQuantity() == false) {
            return false;
        }
        return true;
    }

    function checkQuantity() {
        var actualQuantityVar = parseFloat(numericActualQuantity.value());
        var quantity = parseFloat(dropDownItemId.dataItem().actual_quantity);
        if (actualQuantityVar > quantity) {
            showError("Quantity is greater then stock quantity");
            return false;
        }

        return true;
    }

    function onSubmitInventoryOutDetails() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#inventoryOutDetailsForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'createInventoryOutDetails')}";
        } else {
            actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'updateInventoryOutDetails')}";
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
                    var previousTotal = parseInt(inventoryOutDetailsListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryOutDetailsListModel.rows.length > 0) {
                        firstSerial = inventoryOutDetailsListModel.rows[0].cell[0];
                        regenerateSerial($(inventoryOutDetailsListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    inventoryOutDetailsListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryOutDetailsListModel.rows.pop();
                    }

                    inventoryOutDetailsListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryOutDetailsListModel);
                } else if (result.entity != null) { // updated existing
                    updateListModel(inventoryOutDetailsListModel, result.entity, 0);
                    $("#flex1").flexAddData(inventoryOutDetailsListModel);
                }

                previousMaterials = null;
                resetForCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForCreateAgain() {

        clearErrors('#inventoryOutDetailsForm');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#id').val('');
        $('#chalanNo').text('(Auto Generated)');
        $('#version').val('');
        $('#vehicleNumber').val('');
        $('#mrfNo').val('');
        $('#stackMeasurement').val('');
        $('#comments').val('');
        $('#shrinkage').val('');
        $('#SupplierChalan').val('');
        $('#unit').text('');
        numericActualQuantity.value('');
        dropDownVehicle.value('');
        $('#itemId').attr('default_value', '');
        $('#itemId').reloadMe();
        dropDownItemId.enable(true);
        $('#SupplierChalan').focus();
    }

    function resetInventoryOutForm() {
        clearForm($('#inventoryOutDetailsForm'), dropDownVehicle);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#unit').text('');
        $('#chalanNo').text('(Auto Generated)');
        $('#itemId').attr('default_value', '');
        $('#itemId').reloadMe();
        dropDownItemId.enable(true);
        $('#inventoryTransactionId').val(inventoryTransactionId);
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Chalan", name: "id", width: 50, sortable: false, align: "left"},
                    {display: "Item", name: "itemId", width: 150, sortable: false, align: "left"},
                    {display: "Quantity", name: "numericActualQuantity", width: 100, sortable: false, align: "right"},
                    {display: "Transaction Date", name: "transaction_date", width: 100, sortable: false, align: "left"},
                    {display: "MRF No.", name: "mrf", width: 100, sortable: false, align: "left"},
                    {display: "Vehicle", name: "vehicle", width: 100, sortable: false, align: "left"},
                    {display: "Vehicle No", name: "vehicleNo", width: 100, sortable: false, align: "left"},
                    {display: "Created By", name: "createdBy", width: 100, sortable: false, align: "left"},
                    {display: "Updated By", name: "updatedBy", width: 100, sortable: false, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/invInventoryTransactionDetails/selectInventoryOutDetails,/invInventoryTransactionDetails/updateInventoryOutDetails">
                    {name: 'Edit', bclass: 'edit', onpress: editInventoryOut},
                    </app:ifAllUrl>
                    <sec:access url="/invInventoryTransactionDetails/deleteInventoryOutDetails">
                    {name: 'Delete', bclass: 'delete', onpress: deleteInventoryOutDetails},
                    </sec:access>
                    <sec:access url="/invInventoryTransactionDetails/approveInventoryOutDetails">
                    {name: 'Approve', bclass: 'approve', onpress: approveInventoryOut},
                    </sec:access>
                    {name: 'Report', bclass: 'addDoc', onpress: getInvoiceInventoryOut},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Item", name: "item.name", width: 180, sortable: true, align: "left"},
                    {display: "Chalan No", name: "iitd.id", width: 180, sortable: true, align: "left"},
                    {display: "Quantity", name: "iitd.actual_quantity", width: 180, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Unapproved Item(s) For Inventory-Out',
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

                customPopulate: onLoadInventoryOutListJSON

            }
    );


    function deleteInventoryOutDetails(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryDetailId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransactionDetails', action: 'deleteInventoryOutDetails')}?id=" + inventoryDetailId,
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
        if (!confirm('Are you sure you want to delete the selected Inventory out details?')) {
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
            inventoryOutDetailsListModel.total = parseInt(inventoryOutDetailsListModel.total) - 1;
            removeEntityFromGridRows(inventoryOutDetailsListModel, selectedRow);
            resetInventoryOutForm();
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
            showInfo('No Inventory out data found');
        }
    }


    function editInventoryOut(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        previousMaterials = dropDownItemId;

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryOutDetails')}?id=" + inventoryOutId,
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
            populateInventoryOutDetails(data);
        }
    }

    function populateInventoryOutDetails(data) {
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#chalanNo').text(entity.id);
        $('#version').val(data.version);
        dropDownVehicle.value(entity.vehicleId);
        $('#vehicleNumber').val(entity.vehicleNumber);
        $('#mrfNo').val(entity.mrfNo);
        numericActualQuantity.value(entity.actualQuantity);
        $('#comments').val(entity.comments);

        $('#itemId').attr('default_value', entity.itemId);
        $('#itemId').attr('actual_quantity', entity.actualQuantity);
        $('#itemId').reloadMe(disableDropDownAndShowQuantitySpan);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    // disable item dropDown as well as show item actual quantity in the span
    function disableDropDownAndShowQuantitySpan(){
        onChangeItem();
        dropDownItemId.enable(false);
    }

    function onChangeItem() {
        if (dropDownItemId.value() == '') {
            $('#unit').text('');
            return false;
        }
        var unit = dropDownItemId.dataItem().unit;
        var quantity = dropDownItemId.dataItem().curr_quantity;
        $('#unit').text('of ' + quantity + ' ' + unit);
        return true;
    }

    //for approval
    function approveInventoryOut(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForApprove(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryOutDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransactionDetails', action: 'approveInventoryOutDetails')}?id=" + inventoryOutDetailsId,
            success: executePostConditionForApprove,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    function executePreConditionForApprove(ids) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return false;
        }
        if (!confirm('Do you want to approve the selected item?')) {
            return false;
        }

        var approvedByIA = $(ids[ids.length - 1]).find('td').eq(14).find('div').text();
        if (approvedByIA != "") {
            showError("This item already approved.");
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
            inventoryOutDetailsListModel.total = parseInt(inventoryOutDetailsListModel.total) - 1;
            removeEntityFromGridRows(inventoryOutDetailsListModel, selectedRow);
            resetForCreateAgain(data);
        } else {
            showError(data.message);
        }
    }

    function onLoadInventoryOutListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryOutDetailsListModel = getEmptyGridModel();
        } else {
            inventoryOutDetailsListModel = data;
        }
        $("#flex1").flexAddData(inventoryOutDetailsListModel);
    }


    function getInvoiceInventoryOut(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invReport', action: 'showInvoice')}?id=" + inventoryOutId;
        $.history.load(formatLink(loc));
        return false;
    }


    <sec:access url="/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails">
    var inventoryOutId = $('#inventoryTransactionId').val();
    var strUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'listUnApprovedInventoryOutDetails')}?inventoryOutId=" + inventoryOutId;
    $("#flex1").flexOptions({url: strUrl});

    if (inventoryOutDetailsListModel) {
        $("#flex1").flexAddData(inventoryOutDetailsListModel);
    }
    </sec:access>

</script>