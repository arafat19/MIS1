<script language="javascript">
    var dropDownItemId, dropDownFixedAsset, dropDownFixedAssetDetails, actualQuantity;
    var invConsumptionDetailsModel =${output ? output : ''};
    var inventoryConsumptionDetailsGrid = null;

    $(document).ready(function () {
        onLoadInventoryConsumption();
        initGrid();
    });

    function onLoadInventoryConsumption() {
        initializeForm($("#approvedConsumptionDetailsForm"), onSubmitInventoryConsumption);

        dropDownItemId = initKendoDropdown($('#itemId'), null, null, invConsumptionDetailsModel.inventoryTransactionMap.itemList);
        dropDownFixedAsset = initKendoDropdown($('#fixedAssetId'), null, null, null);
        dropDownFixedAssetDetails = initKendoDropdown($('#fixedAssetDetailsId'), null, null, null);

        $('#actualQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"
        });
        actualQuantity = $("#actualQuantity").data("kendoNumericTextBox");

        var isError = invConsumptionDetailsModel.isError;
        if (isError == 'true') {
            var message = invConsumptionDetailsModel.message;
            showError(message);
            return;
        }

        inventoryConsumptionDetailsGrid = invConsumptionDetailsModel.inventoryTransactionDetailsList;
        populateInventoryConsumption(invConsumptionDetailsModel.inventoryTransactionMap);

        $('#create').hide();

        dropDownItemId.enable(false);
        $('#comments').attr("disabled", true);
        dropDownFixedAsset.enable(false);
        dropDownFixedAssetDetails.enable(false);
        actualQuantity.enable(false);

        // update page title
        $(document).attr('title', "MIS - Item(s) of Approved-Inventory-Consumption Transaction");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryConsumption");
    }

    function populateInventoryConsumption(inventoryConsumptionMap) {
        if (!inventoryConsumptionMap) {
            return false;
        }

        $('#budgetId').val(inventoryConsumptionMap.inventoryTransaction.budgetId);
        $('#inventoryTransactionId').val(inventoryConsumptionMap.inventoryTransaction.id);
        $('#lblInventoryTransactionId').html(inventoryConsumptionMap.inventoryTransaction.id);
        $('#inventoryId').val(inventoryConsumptionMap.inventory.id);
        $('#inventoryName').html(inventoryConsumptionMap.inventory.name);
        $('#budgetItem').html(inventoryConsumptionMap.budgetItem);
        return true;
    }

    function executePreCondition() {
        if (actualQuantity.value() == '') {
            showError("Actual Quantity not found.");
            return false;
        }

        var availableQuantity = parseFloat($('#availableQuantity').val());
        var actualQuantityVar = parseFloat(actualQuantity.value());
        if (actualQuantityVar > availableQuantity) {
            showError("Available quantity is lower than given quantity");
            return false;
        }
        return true;
    }

    function onSubmitInventoryConsumption() {

        if (executePreCondition() == false) {
            return false;
        }
        if (!validateForm($("#approvedConsumptionDetailsForm"))) {
            return false;
        }
        var data = jQuery("#approvedConsumptionDetailsForm").serialize();
        showLoadingSpinner(true);
        setButtonDisabled($('#create'), true);
        var isReverse = $('#isReverse').val();
        if (isReverse == 'false') {
            var actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'adjustInvConsumption')}";
        } else
            var actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'reverseAdjustInvConsumption')}";

        jQuery.ajax({
            type: 'post',
            data: data,
            url: actionUrl,
            success: function (data, textStatus) {
                if (isReverse == 'false') {
                    executePostConditionForAdjustment(data);
                } else
                    executePostConditionForReverseAdjustment(data);
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

    function executePostConditionForAdjustment(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                $(inventoryConsumptionDetailsGrid.rows).each(function (e) {
                    if (this.id == result.adjustmentParentId) {
                        copyEntityProperties(this, result.entity, 0);
                        this.id = result.entity.id;
                    }
                });
                $("#flex1").flexAddData(inventoryConsumptionDetailsGrid);
                resetCreateForm();
                showSuccess(result.message);
                resetCreateForm(result);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function executePostConditionForReverseAdjustment(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetCreateForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            inventoryConsumptionDetailsGrid.total = parseInt(inventoryConsumptionDetailsGrid.total) - 1;
            removeEntityFromGridRows(inventoryConsumptionDetailsGrid, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function resetCreateForm() {
        clearErrors($('#approvedConsumptionDetailsForm'));

        $('#id').val('');
        $('#version').val('');
        $('#comments').val('');
        $('#availableQuantity').val('');
        $('#inventoryId').val('');
        actualQuantity.value('');

        dropDownItemId.value('');
        dropDownFixedAsset.value('');
        dropDownFixedAssetDetails.value('');

        //dropDownItemId.enable(false);
        dropDownFixedAsset.enable(false);
        dropDownFixedAssetDetails.enable(false);
        actualQuantity.enable(false);

        $('#unit').text('');
        $("#transactionDate").val('');
        $('#create').hide();
        $('#comments').attr('disabled', true);
        $('span.lblComments').html('');
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
                        {display: "Rate", name: "rate", width: 80, sortable: false, align: "right"},
                        {display: "Consumption Date", name: "ConsumptionDate", width: 100, sortable: false, align: "left"},
                        {display: "Approved Date", name: "approvedDate", width: 85, sortable: false, align: "left"},
                        {display: "Fixed Asset", name: "fixedAsset", width: 100, sortable: false, align: "left"},
                        {display: "Fixed Asset Details", name: "fixedAssetDetails", width: 100, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"},
                        {display: "Approved By", name: "approvedBy", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/selectInventoryConsumptionDetails">
                        {name: 'Details', bclass: 'details', onpress: selectInventoryConsumptionDetails},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/adjustInvConsumption">
                        {name: 'Adjustment', bclass: 'adjustment', onpress: selectInventoryInForAdjustment},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/reverseAdjustInvConsumption">
                        {name: 'Reverse', bclass: 'reverse', onpress: selectInventoryInForReverse},
                        </app:ifAllUrl>
                        {name: 'Report', bclass: 'addDoc', onpress: getInvoiceInventoryConsumption},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Item", name: "item.name", width: 180, sortable: true, align: "left"},
                        {display: "Chalan No", name: "iitd.id", width: 180, sortable: true, align: "left"},
                        {display: "Quantity", name: "iitd.actual_quantity", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "iitd.approved_by",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Approved Item(s) For Inventory-Consumption ',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight()-15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
//                            checkGrid();
                    },

                    customPopulate: onLoadInventoryConsumptionListJSON

                }
        );
    }

    function selectInventoryConsumptionDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        var inventoryInId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryConsumptionDetails')}?id=" + inventoryInId,
            success: executePostConditionForEditInventory,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEditInventory(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateInvConDetails(data);
            $('#create').hide();
        }
    }

    function populateInvConDetails(data) {
        clearErrors($('#approvedConsumptionDetailsForm'));
        resetCreateForm(data);
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);

        dropDownItemId.setDataSource(data.itemList);
        dropDownItemId.value(entity.itemId);
        dropDownItemId.readonly(true);

        dropDownFixedAsset.enable(false);
        dropDownFixedAssetDetails.enable(false);

        if (data.isConsumedAgainstFixedAsset) {
            if (entity.fixedAssetId > 0) {
                dropDownFixedAsset.setDataSource(data.lstFixedAssetItems);
                dropDownFixedAsset.value(entity.fixedAssetId);
            } else {
                dropDownFixedAsset.setDataSource(data.lstFixedAssetItems);
            }
        }

        if (entity.fixedAssetDetailsId > 0) {
            dropDownFixedAssetDetails.setDataSource(data.lstFixedAssetDetailsItems);
            dropDownFixedAssetDetails.value(entity.fixedAssetDetailsId);
        }

        actualQuantity.value(entity.actualQuantity);
        $("#transactionDate").val(data.transactionDate);

        $('#create').show();
        $('#comments').attr('disabled', true);
        $('span.lblComments').html(entity.comments);
        updateItemDetails();
    }

    function selectInventoryInForAdjustment(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        $('#isReverse').val('false');
        var inventoryConsumptionDetailsId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryConsumptionDetails')}?id=" + inventoryConsumptionDetailsId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function selectInventoryInForReverse(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        $('#isReverse').val('true');
        var inventoryConsumptionDetailsId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryConsumptionDetails')}?id=" + inventoryConsumptionDetailsId,
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
            populateInvConDetailsForAdj(data);
            $('#create').show();
        }
    }

    function populateInvConDetailsForAdj(data) {
        clearErrors($('#approvedConsumptionDetailsForm'));
        resetCreateForm(data);
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownItemId.setDataSource(data.itemList);
        dropDownItemId.value(entity.itemId);
        dropDownItemId.readonly(true);

        dropDownFixedAsset.enable(false);
        dropDownFixedAssetDetails.enable(false);

        if (data.isConsumedAgainstFixedAsset) {

            if (entity.fixedAssetId > 0) {
                dropDownFixedAsset.setDataSource(data.lstFixedAssetItems);
                dropDownFixedAsset.value(entity.fixedAssetId);
            } else {
                dropDownFixedAsset.setDataSource(data.lstFixedAssetItems);
            }
        }

        if (entity.fixedAssetDetailsId > 0) {
            dropDownFixedAssetDetails.setDataSource(data.lstFixedAssetDetailsItems);
            dropDownFixedAssetDetails.value(entity.fixedAssetDetailsId);
        }

        actualQuantity.value(entity.actualQuantity);
        $("#transactionDate").val(data.transactionDate);

        $('#comments').attr('disabled', false);
        $('span.lblComments').html(entity.comments);

        var isReverse = $('#isReverse').val();
        if (isReverse == 'true') {
            actualQuantity.enable(false);
            $('#buttonReverse').show();
            $('#buttonAdjust').hide();
        } else {
            actualQuantity.enable(true);
            $('#buttonAdjust').show();
            $('#buttonReverse').hide();
        }

        updateItemDetails();
    }

    function updateItemDetails() {
        $('#availableQuantity').val('');
        $('#unit').text('');
        var itemId = dropDownItemId.value();
        if (itemId == '') {
            return;
        }
        var availableQuantity = dropDownItemId.dataItem().quantity;
        var unit = dropDownItemId.dataItem().unit;
        $('#availableQuantity').val(availableQuantity);
        $('#unit').text(' of ' + availableQuantity + ' ' + unit);
    }

    function onLoadInventoryConsumptionListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryConsumptionDetailsGrid = getEmptyGridModel();
        } else {
            inventoryConsumptionDetailsGrid = data;
        }
        $("#flex1").flexAddData(inventoryConsumptionDetailsGrid);
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Item found in the Inventory-Out Transaction');
        }
    }

    function getInvoiceInventoryConsumption(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var consumptionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invReport', action: 'showInvoice')}?id=" + consumptionId;
        $.history.load(formatLink(loc));
        return false;
    }


    //********* On Change Item *********************
    function onChangeItem() {
        clearErrors($('#approvedConsumptionDetailsForm'));
        updateItemDetails(); // update/reset item quantity in span
        var itemId = dropDownItemId.value();
        dropDownFixedAsset.value('');
        dropDownFixedAssetDetails.setDataSource(getKendoEmptyDataSource(dropDownFixedAssetDetails));
        dropDownFixedAsset.enable(false);
        dropDownFixedAssetDetails.enable(false);
        if (itemId == '') {
            return;
        }
        var isConsumedAgainstFixedAsset = dropDownFixedAsset.dataItem().isConsumedAgainstFixedAsset;
        if (isConsumedAgainstFixedAsset == 'true') {
            dropDownFixedAsset.enable(true);
            dropDownFixedAssetDetails.enable(true);   // enable the controls
            // check if fixed asset list already populated
            if (dropDownFixedAsset.dataSource.data().length <= 1) {
                showLoadingSpinner(true);
                $.ajax({
                    url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'listFixedAssetByInventoryId')}",
                    success: executePostConditionForChangeItem,
                    complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
                    dataType: 'json',
                    type: 'post'
                });
            }
            $('#fixedAssetId').focus();
        } else {
            $('#actualQuantity').focus();
        }
    }
    function executePostConditionForChangeItem(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateFixedAsset(data);
        }
    }
    function populateFixedAsset(data) {
        clearErrors($('#approvedConsumptionDetailsForm'));
        dropDownFixedAsset.setDataSource(data.lstFixedAssetItems);
    }
    //***********************************

    //********* On Change Fixed Asset **************
    function onChangeFixedAsset1() {
        var inventoryId = $('#inventoryId').val();
        var itemId = dropDownItemId.value();
        if (itemId == '') {
            dropDownFixedAssetDetails.setDataSource(getKendoEmptyDataSource(dropDownFixedAssetDetails));
            return;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'listFixedAssetByInventoryIdAndItemId')}?itemId=" + itemId + "&inventoryId=" + inventoryId,
            success: executePostConditionForChangeFixedAsset,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostConditionForChangeFixedAsset(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            clearErrors($('#approvedConsumptionDetailsForm'));
            dropDownFixedAssetDetails.setDataSource(data.lstFixedAssetDetailsItems);
            updateItemDetails();
        }
    }
    //***********************************

    <sec:access url="/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails">
    var inventoryTransactionId = $('#inventoryTransactionId').val();
    var strUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'listApprovedInventoryConsumptionDetails')}?inventoryTransactionId=" + inventoryTransactionId;
    $("#flex1").flexOptions({url: strUrl});

    if (inventoryConsumptionDetailsGrid) {
        $("#flex1").flexAddData(inventoryConsumptionDetailsGrid);
    }
    </sec:access>

</script>
