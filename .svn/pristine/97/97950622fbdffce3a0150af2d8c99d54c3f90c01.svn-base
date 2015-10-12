<script language="javascript">
    var inventoryOutDetailsListModel, dropDownItemId, dropDownVehicle, actualQuantity;

    var itemId = -1;
    $(document).ready(function () {
        $('#approvedInvOutDetailsForm').submit(function (e) {
            var reverse = $('#reverse').val();
            if (reverse == 'true') {
                onSubmitInvReverseAdjustment();
            } else {
                onSubmitInvAdjustment();
            }
            return false;
        });
        initGrid();
        onLoadInventoryOut();
    });

    function onLoadInventoryOut() {
        var output =${output ?output : ''};
        if (output.isError) {
            showError(output.message);
            return
        }
        initializeForm($("#approvedInvOutDetailsForm"), doNothing);

        dropDownItemId = initKendoDropdown($('#itemId'), null, null, output.lstItem);
        dropDownVehicle = initKendoDropdown($('#vehicleId'), null, null, output.lstVehicle);

        $('#actualQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"

        });
        actualQuantity = $("#actualQuantity").data("kendoNumericTextBox");

        inventoryOutDetailsListModel = output.gridObj;
        populateInventoryOut(output.inventoryOutMap);
        $('#chalanNo').html('');

        populateFlexGrid();

        dropDownItemId.enable(false);
        dropDownVehicle.enable(false);
        actualQuantity.enable(false);
        $('#mrfNo').attr("disabled", true);
        $('#comments').attr("disabled", true);
        $('#vehicleNumber').attr("disabled", true);
        $('#create').hide();

        // update page title
        $(document).attr('title', "MIS - Inventory-Out Approved Item(s)");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryOut");
    }

    function doNothing() {
        //do nothing
    }

    function populateInventoryOut(inventoryOutMap) {
        if (!inventoryOutMap) {
            return false;
        }

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
        // trim field vales before process.
        trimFormValues($("#approvedInvOutDetailsForm"));

        if (actualQuantity.value() == '') {
            showError("Actual Quantity not found.");
            return false;
        }

        if ($("#comments").val().isEmpty()) {
            showError("Comments not found.");
            return false;
        }

        if (checkQuantity() == false) {
            return false;
        }

        return true;
    }

    function checkQuantity() {
        var actualQuantityVar = parseFloat(actualQuantity.value());
        var quantity = parseFloat(dropDownItemId.dataItem().curr_quantity);
        if (actualQuantityVar > quantity) {
            showError("Quantity is grater then stock quantity");
            return false;
        }

        return true;
    }

    function onSubmitInvAdjustment() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#approvedInvOutDetailsForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'adjustInvOut')}";

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
                $(inventoryOutDetailsListModel.rows).each(function (e) {
                    if (this.id == result.adjustmentParentId) {
                        copyEntityProperties(this, result.entity, 0);
                        this.id = result.entity.id;
                    }
                });
                $("#flex1").flexAddData(inventoryOutDetailsListModel);
                resetInventoryOutForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function onSubmitInvReverseAdjustment() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#approvedInvOutDetailsForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'reverseAdjustInvOut')}";

        jQuery.ajax({
            type: 'post',
            data: data,
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForReverse(data);
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

    function executePostConditionForReverse(data) {
        if (data.reversed == true) {
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

    function resetInventoryOutForm() {
        clearErrors($('#approvedInvOutDetailsForm'));
        $('#create').hide();
        $('#chalanNo').text('');
        $('#id').val('');
        $('#version').val('');
        $('#vehicleNumber').val('');
        $('#mrfNo').val('');
        $('#lblComments').html('');
        $('#comments').val('');
        $('#SupplierChalan').val('');
        $('#unit').text('');
        actualQuantity.value('');
        dropDownVehicle.value('');
        dropDownItemId.value('');
        actualQuantity.enable(true);
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
                        {display: "Item", name: "itemId", width: 150, sortable: false, align: "left"},
                        {display: "Quantity", name: "actualQuantity", width: 100, sortable: false, align: "right"},
                        {display: "Rate", name: "rate", width: 50, sortable: false, align: "right"},
                        {display: "Transaction Date", name: "transaction_date", width: 100, sortable: false, align: "left"},
                        {display: "Mrf No", name: "mrf", width: 80, sortable: false, align: "left"},
                        {display: "Vehicle", name: "vehicle", width: 80, sortable: false, align: "left"},
                        {display: "Vehicle No", name: "vehicleNo", width: 80, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 100, sortable: false, align: "left"},
                        {display: "Approved On", name: "approveOn", width: 80, sortable: false, align: "left"},
                        {display: "Approved By", name: "approveBy", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/selectInventoryOutDetails">
                        {name: 'Details', bclass: 'details', onpress: editInventoryOut},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/adjustInvOut">
                        {name: 'Adjustment', bclass: 'adjustment', onpress: selectOutForAdjustment},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/reverseAdjustInvOut">
                        {name: 'Reverse', bclass: 'reverse', onpress: selectOutForReverseAdjustment},
                        </app:ifAllUrl>
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
                    title: 'All Approved Item(s) For Inventory-Out',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 45,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
//                            checkGrid();
                    },
                    customPopulate: onLoadInventoryOutListJSON
                }
        );
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
        $('#reverse').val(false);
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryOutDetails')}?id=" + inventoryOutId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function selectOutForAdjustment(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        $('#reverse').val(false);
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryOutDetails')}?id=" + inventoryOutId,
            success: executePostConditionForSelectAdjustment,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function selectOutForReverseAdjustment(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));
        $('#reverse').val(true);
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryOutDetails')}?id=" + inventoryOutId,
            success: executePostConditionForSelectAdjustment,
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
            $('#create').hide();
        }
    }

    function executePostConditionForSelectAdjustment(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateInventoryOutDetailsForSelectAdjustment(data);
            $('#create').show();
        }
    }

    function populateInventoryOutDetailsForSelectAdjustment(data) {
        resetInventoryOutForm();
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownVehicle.value(entity.vehicleId);
        $('#vehicleNumber').val(entity.vehicleNumber);
        $('#mrfNo').val(entity.mrfNo);
        actualQuantity.value(entity.actualQuantity);
        $('#lblComments').html(entity.comments);
        dropDownItemId.value(entity.itemId);
        updateQuantity();


        var reverse = $('#reverse').val();
        if (reverse == 'true') {
            actualQuantity.enable(false);
        } else {
            actualQuantity.enable(true);
        }

        $('#comments').attr('disabled', false);
        $('#chalanNo').html(entity.id);

        if (reverse == 'true') {
            $('#buttonReverse').show();
            $('#buttonAdjust').hide();
        } else {
            $('#buttonAdjust').show();
            $('#buttonReverse').hide();
        }
    }

    function populateInventoryOutDetails(data) {
        resetInventoryOutForm();
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownVehicle.value(entity.vehicleId);
        $('#vehicleNumber').val(entity.vehicleNumber);
        $('#mrfNo').val(entity.mrfNo);
        actualQuantity.value(entity.actualQuantity);
        $('#lblComments').html(entity.comments);
        dropDownItemId.value(entity.itemId);
        actualQuantity.enable(false);
        $('#comments').attr('disabled', true);
        $('#chalanNo').html(entity.id);
        updateQuantity();
    }

    function updateQuantity() {
        if (dropDownItemId.value() == '') {
            $('#unit').text('');
            return false;
        }

        var unit = dropDownItemId.dataItem().unit;
        var quantity = dropDownItemId.dataItem().curr_quantity;
        $('#unit').text('of ' + quantity + ' ' + unit);
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

    function populateFlexGrid() {
        var inventoryOutId = $('#inventoryTransactionId').val();
        var strUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'listApprovedInventoryOutDetails')}?inventoryOutId=" + inventoryOutId;
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryOutDetailsListModel) {
            $("#flex1").flexAddData(inventoryOutDetailsListModel);
        }
    }

</script>