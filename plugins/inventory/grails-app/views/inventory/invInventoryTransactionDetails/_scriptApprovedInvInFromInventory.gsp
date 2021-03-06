<script language="javascript">
    var inventoryInDetailsListModel, dropDownItemId, actualQuantity, suppliedQuantity, transactionDate;

    $(document).ready(function () {
        initGrid();
        onLoadInventoryIn();
    });

    function onLoadInventoryIn() {
        var output =${output ?output : ''};
        console.log(output);

        initializeForm($("#inventoryInDetailsForm"), onSubmitReverseAdjustment);
        dropDownItemId = initKendoDropdown($('#transactionDetailsId'), null, null, null);

        $('#actualQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"

        });
        actualQuantity = $("#actualQuantity").data("kendoNumericTextBox");

        $('#suppliedQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"

        });
        suppliedQuantity = $("#suppliedQuantity").data("kendoNumericTextBox");

        if (output.isError) {
            showError(output.message);
            return
        }

        inventoryInDetailsListModel = output.gridObj;
        populateInventoryIn(output.inventoryInMap);
        transactionDate = output.transactionDate;
        $("#transactionDate").html(transactionDate);
        populateFlexGrid();

        dropDownItemId.enable(false);
        actualQuantity.enable(false);
        $('#comments').attr('disabled', true);
        $('#create').hide();

        // update page title
        $(document).attr('title', "MIS - Inventory-In from another Inventory");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInventoryInFromInventory");
    }

    function executePreConditionForReverse() {
        var adjustComments = $.trim($('#adjComments').val());
        if (adjustComments.length == 0) {
            showError('No reverse adjustment comments found');
            return false;
        }

        if (!validateForm($("#inventoryInDetailsForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitReverseAdjustment() {
        if (executePreConditionForReverse() == false) {
            return false;
        }

        var reverseConfirmMsg = "If you reverse this transaction related INVENTORY-OUT transaction will also be reversed.\n" +
                "Are you sure to reverse both transactions?";

        if (!confirm(reverseConfirmMsg)) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#inventoryInDetailsForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'reverseAdjustInvInFromInventory')}";

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
                setButtonDisabled($('.save'), false);
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
            inventoryInDetailsListModel.total = parseInt(inventoryInDetailsListModel.total) - 1;
            removeEntityFromGridRows(inventoryInDetailsListModel, selectedRow);
            resetForm();
        } else {
            showError(data.message);
        }
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

    function updateQuantity() {
        suppliedQuantity.value('');
        $('#unit').text('');

        if (dropDownItemId.value() == '') {
            $('#unit').text('');
            return false;
        }
        var unit = dropDownItemId.dataItem().unit;
        var quantity = dropDownItemId.dataItem().quantity;

        suppliedQuantity.value(quantity);
        $('#unit').text(" of " + quantity + " " + unit);
    }

    function resetForm() {
        clearForm($("#inventoryInDetailsForm"), $('#adjComments'));
        if (!$('#id').val().isEmpty()) {
            dropDownItemId.enable(true);
        }

        $('#comments').html('');
        $("#transactionDate").val(transactionDate);
        $('#unit').text('');

        dropDownItemId.enable(false);
        actualQuantity.enable(false);
        dropDownItemId.setDataSource(getKendoEmptyDataSource(dropDownItemId, null));
        $('#adjComments').attr('disabled', true);
        $('#create').hide();
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Chalan", name: "id", width: 40, sortable: false, align: "left"},
                        {display: "Item", name: "itemName", width: 150, sortable: false, align: "left"},
                        {display: "Supplied Quantity", name: "suppliedQuantity", width: 100, sortable: false, align: "right"},
                        {display: "Actual Quantity", name: "actualQuantity", width: 100, sortable: false, align: "right"},
                        {display: "Shrinkage", name: "shrinkage", width: 100, sortable: false, align: "right"},
                        {display: "Rate", name: "rate", width: 100, sortable: false, align: "right"},
                        {display: "Approved On", name: "approved_on", width: 80, sortable: false, align: "right"},
                        {display: "Approved By", name: "approvedBy", width: 120, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/selectInventoryInDetailsFromInventory,/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory">
                        {name: 'Details', bclass: 'details', onpress: editInventoryIn},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invInventoryTransactionDetails/reverseAdjustInvInFromInventory">
                        {name: 'Reverse', bclass: 'reverse', onpress: selectInvInForReverseAdjustment},
                        </app:ifAllUrl>
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
                    title: 'All Approved Item(s) From Inventory',
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

    function selectInvInForReverseAdjustment(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        $('#reverse').val(true);
        $('#buttonReverse').show();
        var inventoryOutId = getSelectedIdFromGrid($('#flex1'));

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'selectInventoryInDetailsFromInventory')}?id=" + inventoryOutId,
            success: executePostConditionForReverseAdjustment,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForReverseAdjustment(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateInventoryInDetailsForAdjustment(data);
            $('#create').show();
        }
    }

    function populateInventoryInDetailsForAdjustment(data) {
        clearErrors($("#inventoryInDetailsForm"));
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#inventoryTransactionId').val(entity.inventoryTransactionId);
        $('#inventoryId').val(entity.inventoryId);
        suppliedQuantity.value(entity.suppliedQuantity);
        actualQuantity.value(entity.actualQuantity);
        $('#comments').val(entity.comments);
        dropDownItemId.setDataSource(data.lstItem);
        dropDownItemId.value(entity.transactionDetailsId);
        dropDownItemId.enable(false);
        $('#transactionDate').html(data.transactionDate);
        var reverse = $('#reverse').val();
        if (reverse == 'true') {
            actualQuantity.enable(false);
            $('#buttonReverse').show();
        } else {
            actualQuantity.enable(true);
            $('#buttonReverse').hide();

        }
        updateQuantity();

        $('#adjComments').val('');
        $('#adjComments').removeAttr('disabled');
    }

    function editInventoryIn(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        var inventoryInId = getSelectedIdFromGrid($('#flex1'));
        $('#reverse').val(false);
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
            $('#create').hide();
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No inventory in data found');
        }
    }

    function populateInventoryInDetails(data) {
        clearErrors($("#inventoryInDetailsForm"));
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#inventoryTransactionId').val(entity.inventoryTransactionId);
        $('#inventoryId').val(entity.inventoryId);
        suppliedQuantity.value(entity.suppliedQuantity);
        actualQuantity.value(entity.actualQuantity);
        $('#comments').val(entity.comments);
        dropDownItemId.setDataSource(data.lstItem);
        dropDownItemId.value(entity.transactionDetailsId);
        dropDownItemId.enable(false);
        $('#transactionDate').html(data.transactionDate);
        updateQuantity();
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
        if (executeCommonPreConditionForSelect($('#flex1'), 'currency') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryInId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invReport', action: 'showInvoice')}?id=" + inventoryInId;
        $.history.load(formatLink(loc));
        return false;
    }

    function populateFlexGrid() {
        var inventoryInId = $('#inventoryTransactionId').val();
        var strUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'listApprovedInvInFromInventory')}?inventoryTransactionId=" + inventoryInId;
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryInDetailsListModel) {
            $("#flex1").flexAddData(inventoryInDetailsListModel);
        }
    }

</script>
