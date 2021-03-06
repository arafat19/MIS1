<%@ page import="com.athena.mis.application.utility.RoleUtility" %>
<script type="text/javascript">
    var modelJson =${modelJson ? modelJson : ''};
    var purchaseRequestDetailsListModel = false;

    var purchaseRequestMap, poQuantity, itemListModel;
    var dropDownItemType, dropDownItem, quantity, rate;


    function onLoadPurchaseRequest() {
        initializeForm($("#purchaseRequestDetailsForm"), onSubmitPurchaseRequest);

        $('#rate').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "৳ #.####"

        });
        rate = $("#rate").data("kendoNumericTextBox");

        $('#quantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"

        });
        quantity = $("#quantity").data("kendoNumericTextBox");
        dropDownItem = initKendoDropdown($('#itemId'), null, null, null);

        poQuantity = 0;
        purchaseRequestMap = false;
        initFlexGrid()
        if (modelJson.isError) {
            showError(modelJson.message);
            return;
        }
        if (modelJson.purchaseRequestMap) {
            purchaseRequestMap = modelJson.purchaseRequestMap;
        }
        purchaseRequestDetailsListModel = modelJson;

        populateFlexGrid();

        if (purchaseRequestMap) {
            clearPRDetails();
            populateHiddenFields(purchaseRequestMap);
        }
        // update page title
        $(document).attr('title', "MIS - Create Purchase Request Details");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procPurchaseRequest/show");
    }


    function clearPRDetails() {
        $('#projectName').html('');
        $('#purchaseRequestItem').html('');
        dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
        $('#item').html('');
        $('#itemUnitName').text('');
        $('#perItemUnitName').text('');
        poQuantity = 0;
    }

    function resetForCreateAgain() {
        clearForm($("#purchaseRequestDetailsForm"), $('#comments'));
        dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
        dropDownItemType.enable(true);
        dropDownItem.enable(true);
        $('#itemUnit').text('');
        $('#perItemUnitName').text('');
        $('#total').text('');
        $('#itemUnitName').html('');
        poQuantity = 0;
        purchaseRequestMap = modelJson.purchaseRequestMap;
        populateHiddenFields(purchaseRequestMap);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function populatePurchaseRequest(data) {
        resetCreateForm();
        var entity = data.entity;
        purchaseRequestMap = data.purchaseRequestMap;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        dropDownItemType.value(purchaseRequestMap.itemTypeId);
        dropDownItem.setDataSource(data.itemList);
        dropDownItem.value(entity.itemId);
        dropDownItemType.enable(false);
        dropDownItem.enable(false);
        quantity.value(entity.quantity);
        $('#comments').val(entity.comments);
        rate.value(entity.rate);
        $('#total').text(entity.total);
        populateHiddenFields(purchaseRequestMap);
        $('#itemUnitName').text('of ' + dropDownItem.dataItem().quantity + ' ' + dropDownItem.dataItem().unit);
        $('#perItemUnitName').text('per ' + dropDownItem.dataItem().unit);
        calculateTotal();
        poQuantity = entity.poQuantity;
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function onChangeItemType() {
        var itemTypeId = dropDownItemType.value();
        if (itemTypeId == '') {
            $('#itemUnitName').text('');
            $('#perItemUnitName').text('');
            dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
            return;
        }

        var projectId = $('#projectId').val();
        var purchaseRequestId = $('#purchaseRequestId').val();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'procPurchaseRequestDetails', action: 'getItemList')}?projectId=" + projectId + "&itemTypeId=" + itemTypeId + "&purchaseRequestId=" + purchaseRequestId,
            success: function (data) {
                updateItemListDropDown(data);
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function updateItemListDropDown(data) {
        if (data.isError) {
            showError(data.message);
            dropDownItem.setDataSource(getKendoEmptyDataSource());
            return false;
        } else {
            dropDownItem.setDataSource(data.itemList);
            dropDownItem.value('');
            $('#itemUnitName').text('');
            $('#perItemUnitName').text('');
        }
    }

    function onChangeItemDropDown(e) {
        $('#itemUnitName').text('');
        $('#perItemUnitName').text('');

        $('#itemUnitName').text('of ' + dropDownItem.dataItem().quantity + ' ' + dropDownItem.dataItem().unit);
        $('#perItemUnitName').text('per ' + dropDownItem.dataItem().unit);
    }

    function populateHiddenFields(purchaseRequestMap) {
        if (!purchaseRequestMap) {
            return false;
        }
        $('#projectId').val(purchaseRequestMap.projectId);
        $('#projectName').html(purchaseRequestMap.projectName);
        $('#purchaseRequestId').val(purchaseRequestMap.purchaseRequestId);
        $('#purchaseRequestItem').html(purchaseRequestMap.purchaseRequestId);

        return true;
    }

    function resetCreateForm() {
        clearForm($("#purchaseRequestDetailsForm"), $('#comments'));
        dropDownItem.enable(true);
        dropDownItemType.enable(true);
        $('#itemUnitName').text('');
        $('#perItemUnitName').text('');
        quantity.value('');
        rate.value('');
        $('#total').text('');
        poQuantity = 0;

        purchaseRequestMap = modelJson.purchaseRequestMap;
        populateHiddenFields(purchaseRequestMap);

        dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePreCondition() {
        if (!validateForm($("#purchaseRequestDetailsForm"))) {
            return false;
        }

        if (dropDownItemType.value() == '') {
            showError('Please select Item type');
            return false;
        }
        // check available quantity on Budget Details
        var quantityVar = parseFloat(quantity.value());
        var availableBDQuantity = dropDownItem.dataItem().quantity;
        if (quantityVar > availableBDQuantity) {
            showError("Purchase request quantity exceeds project\'s item quantity");
            return false;
        }
        // check poQuantity to edit limit
        if (quantityVar < parseFloat(poQuantity)) {
            showError("Can not less than already issued PO quantity");
            return false;
        }
        return true;
    }

    function onSubmitPurchaseRequest() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'procPurchaseRequestDetails', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'procPurchaseRequestDetails', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#purchaseRequestDetailsForm").serialize(),
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
            showLoadingSpinner(false);
        } else {
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(purchaseRequestDetailsListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (purchaseRequestDetailsListModel.rows.length > 0) {
                        firstSerial = purchaseRequestDetailsListModel.rows[0].cell[0];
                        regenerateSerial($(purchaseRequestDetailsListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    purchaseRequestDetailsListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        purchaseRequestDetailsListModel.rows.pop();
                    }

                    purchaseRequestDetailsListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(purchaseRequestDetailsListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(purchaseRequestDetailsListModel, result.entity, 0);
                    $("#flex1").flexAddData(purchaseRequestDetailsListModel);
                }
                resetForCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    //-------------
    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "PR Details ID", name: "id", width: 80, sortable: false, align: "left"},
                        {display: "Item Type", name: "itemTypeId", width: 80, sortable: false, align: "left"},
                        {display: "Item", name: "itemId", width: 110, sortable: true, align: "left"},
                        {display: "Quantity", name: "quantity", width: 110, sortable: true, align: "right"},
                        {display: "Estimated Rate", name: "rate", width: 100, sortable: true, align: "right"},
                        {display: "Total", name: "total", width: 110, sortable: false, align: "right"},
                        {display: "Created On", name: "createdOn", width: 120, sortable: true, align: "left"},
                        {display: "Created By", name: "created_by", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/procPurchaseRequestDetails/select,/procPurchaseRequestDetails/update, /procPurchaseRequestDetails/create">
                        {name: 'Edit', bclass: 'edit', onpress: editPurchaseRequestDetails},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/procPurchaseRequestDetails/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteItem},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Item", name: "itemId", width: 180, sortable: true, align: "left"},
                    {display: "Item Type", name: "itemTypeId", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Purchase Request Details',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 35,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadPurchaseRequestListJSON
                }
        );
    }

    function deleteItem(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);

        var purchaseRequestDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequestDetails', action: 'delete')}?id=" + purchaseRequestDetailsId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request details') == false) {
            return false;
        }
        if (!confirm('Do you want to delete the selected purchase request details?')) {
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
            resetCreateForm();
            $('#flex1').decreaseCount(1);
            purchaseRequestDetailsListModel.total = parseInt(purchaseRequestDetailsListModel.total) - 1;
            removeEntityFromGridRows(purchaseRequestDetailsListModel, selectedRow);
            showSuccess(data.message);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadPurchaseRequestListJSON(data) {
        if (data.isError) {
            showError(data.message);
            purchaseRequestDetailsListModel = null;
        } else {
            purchaseRequestDetailsListModel = data;
        }
        return data;
    }

    function editPurchaseRequestDetails(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase request details') == false) {
            return;
        }
        var purchaseRequestDetailsId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequestDetails', action: 'select')}?id=" + purchaseRequestDetailsId,
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
            populatePurchaseRequest(data);
        }
    }


    function calculateTotal() {
        var estimateCost = $('#rate').val();
        var quantity = $('#quantity').val();
        var total = estimateCost * quantity;
        if (total > 0) {
            $('#total').text(total);
        }
        else {
            $('#total').text(0);
        }
    }

    function populateFlexGrid() {

        var purchaseRequestId = purchaseRequestMap.purchaseRequestId;
        if (purchaseRequestId) {
            var strUrl = "${createLink(controller: 'procPurchaseRequestDetails', action: 'list')}?purchaseRequestId=" + purchaseRequestId;
        }
        $("#flex1").flexOptions({url: strUrl});

        if (purchaseRequestDetailsListModel) {
            $("#flex1").flexAddData(purchaseRequestDetailsListModel);
        }

        if (purchaseRequestDetailsListModel) {
            $("#flex1").flexAddData(purchaseRequestDetailsListModel);

            var gridTitle;
            if (purchaseRequestId) {
                gridTitle = "Purchase Request Details for [Purchase Request No - " + purchaseRequestId + " ]";
            }
            else {
                gridTitle = "All Purchase Request Details";
            }
            $('div.mDiv > div.ftitle').text(gridTitle);
        }
    }

    window.onload = onLoadPurchaseRequest();

</script>