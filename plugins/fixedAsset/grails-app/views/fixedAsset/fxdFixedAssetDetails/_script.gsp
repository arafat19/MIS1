<script type="text/javascript">
    var dropDownPoId, dropDownItemId, dropDownInventoryTypeId, dropDownInventoryId,
            dropDownOwnerTypeId, fadListModel, ownerTypeListModel, poList, itemList, ownerTypeRentalId;
    var fadLisJsonModel =${output ? output : ''};

    $(document).ready(function () {
        onLoadFAD();
    });

    function onLoadFAD() {
        initializeForm($('#fixedAssetDetailsForm'), onSubmitFixedAssetDetails);

        var isError = fadLisJsonModel.isError;
        if (isError == 'true') {
            var message = fadLisJsonModel.message;
            showError(message);
            return;
        }

        dropDownPoId = initKendoDropdown($('#poId'), null, null, fadLisJsonModel.poList);
        dropDownItemId = initKendoDropdown($('#itemId'), null, null, null);
        dropDownInventoryId = initKendoDropdown($('#inventoryId'), null, null, null);

        fadListModel = fadLisJsonModel.fixedAssetDetailsList;
        ownerTypeListModel = fadLisJsonModel.ownerTypeList;
        ownerTypeRentalId = $('#ownerTypeRentalId').val();
        initFADetailsGrid();

        // update page title
        $(document).attr('title', "MIS - Fixed Asset Details");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fxdFixedAssetDetails/show");
    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryId) {
        var inventoryTypeId = dropDownInventoryTypeId.value();
        if (inventoryTypeId == '') {
            dropDownInventoryId.setDataSource(getKendoEmptyDataSource());
            return false;
        }
        showLoadingSpinner(true);

        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryByType')}?inventoryTypeId=" + inventoryTypeId,
            success: function (data) {
                updateInventoryListDropDown(data, defaultInventoryId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateInventoryListDropDown(data, defaultInventoryId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultInventoryId) defaultInventoryId = '';
        dropDownInventoryId.setDataSource(data.inventoryList);
        dropDownInventoryId.value(defaultInventoryId);
    }

    function executePreCondition() {
        if (!validateForm($('#fixedAssetDetailsForm'))) {
            return false;
        }

        if (dropDownOwnerTypeId.value() == ownerTypeRentalId) {
            if ($('#expireDate').val() == '') {
                showError('Please enter expire date');
                return false;
            }
            if (!getDate($('#expireDate'), 'expire date')) {
                $('#expireDate').focus();
                return false;
            }
        }

        if (!checkCustomDate($('#purchaseDate'), 'Purchase')) {
            return false;
        }
        return true;
    }

    function onSubmitFixedAssetDetails() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#fixedAssetDetailsForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'fxdFixedAssetDetails', action: 'create')}";
            beforeCreateOrUpdateFAD();
        } else {
            actionUrl = "${createLink(controller: 'fxdFixedAssetDetails', action: 'update')}";
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
            if ($('#id').val().isEmpty()) {
                dropDownPoId.enable(true);
                dropDownItemId.enable(true);
                dropDownInventoryTypeId.enable(true);
                dropDownInventoryId.enable(true);
            }
        } else {
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(fadListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (fadListModel.rows.length > 0) {
                        firstSerial = fadListModel.rows[0].cell[0];
                        regenerateSerial($(fadListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    fadListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        fadListModel.rows.pop();
                    }

                    fadListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(fadListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(fadListModel, result.entity, 0);
                    $("#flex1").flexAddData(fadListModel);
                }

                afterCreateOrUpdateFAD(result);
                showSuccess(result.message);
                applyClassOnExpireDate();
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function basicReset() {
        $('#id').val('');
        $('#version').val('');
        $('#name').val('');
        $('#description').val('');
        $('#purchaseDate').val('');
        $('#expireDate').val('');
        $('#cost').html('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function beforeCreateOrUpdateFAD() {
        dropDownPoId.enable(false);
        dropDownItemId.enable(false);
        dropDownInventoryTypeId.enable(false);
        dropDownInventoryId.enable(false);
    }

    function afterCreateOrUpdateFAD(data) {
        basicReset();
        var itemList = data.itemList;
        var fixedAssetDetails = data.fixedAssetDetails;
        dropDownPoId.enable(true);
        dropDownItemId.enable(true);
        dropDownInventoryTypeId.enable(true);
        dropDownInventoryId.enable(true);
        dropDownOwnerTypeId.value('');
        dropDownInventoryTypeId.value('');
        dropDownInventoryId.value('');
        dropDownInventoryId.setDataSource(getKendoEmptyDataSource());
        $('#cost').html('');

        dropDownItemId.setDataSource(getKendoEmptyDataSource(dropDownItemId, null));
        dropDownItemId.value('');
        dropDownPoId.setDataSource(data.poList);
        dropDownPoId.refresh();
        dropDownPoId.value('');

        updateCost();
        applyClassOnExpireDate();
        return false;
    }

    function resetCreateForm() {
        clearForm($("#fixedAssetDetailsForm"), dropDownPoId);

        dropDownPoId.enable(true);
        dropDownItemId.enable(true);
        dropDownInventoryTypeId.enable(true);
        dropDownInventoryId.enable(true);
        dropDownItemId.setDataSource(getKendoEmptyDataSource(dropDownItemId, null));
        dropDownItemId.value('');
        dropDownInventoryId.setDataSource(getKendoEmptyDataSource(dropDownInventoryId, null));
        dropDownInventoryId.value('');

        dropDownPoId.setDataSource(fadLisJsonModel.poList);
        dropDownPoId.refresh();

        updateCost();
        basicReset();
        applyClassOnExpireDate();
    }

    function initFADetailsGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Id", name: "fad.id", width: 30, sortable: false, align: "left", hide: true},
                        {display: "Category", name: "item.name", width: 150, sortable: false, align: "left"},
                        {display: "Model / Serial No.", name: "fad.name", width: 200, sortable: false, align: "left"},
                        {display: "Current Inventory", name: "inventory.name", width: 150, sortable: true, align: "left"},
                        {display: "Cost", name: "fad.cost", width: 120, sortable: false, align: "right"},
                        {display: "Purchase Date", name: "fad.purchase_date", width: 90, sortable: false, align: "left"},
                        {display: "PO No", name: "fad.po_id", width: 40, sortable: false, align: "left"},
                        {display: "Ownership Type", name: "ownerTypeId", width: 90, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/fxdFixedAssetDetails/select,/fxdFixedAssetDetails/update">
                        {name: 'Edit', bclass: 'edit', onpress: editFixedAssetDetails},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/fxdFixedAssetDetails/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteFAD},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/fxdFixedAssetDetails/delete">
                        {name: 'Maintenance', bclass: 'maintenance', onpress: addMaintenance},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/fxdCategoryMaintenanceType/show">
                        {name: 'Maintenance Type', bclass: 'maintenance', onpress: addMaintenanceType},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    searchitems: [
                        {display: "Model / Serial No.", name: "fad.name", width: 180, sortable: true, align: "left"},
                        {display: "Category", name: "item.name", width: 180, sortable: true, align: "left"},
                        {display: "Inventory", name: "inventory.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Fixed Asset Details List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },

                    customPopulate: onLoadFADListJSON
                }
        );
    }

    function onLoadFADListJSON(data) {
        if (data.isError) {
            showError(data.message);
            fadListModel = getEmptyGridModel();
        } else {
            fadListModel = data;
        }
        $("#flex1").flexAddData(fadListModel);
    }

    function addMaintenance(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Fixed Asset Details') == false) {
            return;
        }
        showLoadingSpinner(true);
        var fixedAssetDetailsId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'fxdMaintenance', action: 'show')}?fixedAssetDetailsId=" + fixedAssetDetailsId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addMaintenanceType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Fixed Asset Details') == false) {
            return;
        }
        showLoadingSpinner(true);
        var fixedAssetDetailsId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'fxdCategoryMaintenanceType', action: 'show')}?fixedAssetDetailsId=" + fixedAssetDetailsId;
        $.history.load(formatLink(loc));
        return false;
    }

    function deleteFAD(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var fixedAssetDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'fxdFixedAssetDetails', action: 'delete')}?id=" + fixedAssetDetailsId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Fixed Asset Details') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Fixed Asset Details?')) {
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
            resetCreateForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            fadListModel.total = parseInt(fadListModel.total) - 1;
            removeEntityFromGridRows(fadListModel, selectedRow);
        } else {
            showError(data.message);
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
            showInfo('No Fixed Asset Details data found');
        }
    }

    // To populate Inventory List
    function updateItemList(defaultItemId) {
        var poId = dropDownPoId.value();
        if (poId == '') {
            dropDownItemId.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'fxdFixedAssetDetails', action: 'getFixedAssetList')}?poId=" + poId,
            success: function (data) {
                updateItemListDropDown(data, defaultItemId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateItemListDropDown(data, defaultItemId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultItemId) defaultItemId = '';
        dropDownItemId.setDataSource(data.itemList);
        dropDownItemId.value(defaultItemId);
    }

    function updateCost() {
        var itemId = dropDownItemId.value();
        if (itemId == '') {
            $('#cost').html('');
            return false;
        }
        var cost = dropDownItemId.dataItem().rate;
        $('#cost').html(cost);
        return false;
    }

    function editFixedAssetDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Fixed Asset Details') == false) {
            return;
        }
        basicReset();
        var fixedAssetDetailsId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'fxdFixedAssetDetails', action: 'select')}?id=" + fixedAssetDetailsId,
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
            populateFixedAssetDetails(data);
        }
    }

    function populateFixedAssetDetails(data) {
        clearErrors($("#fixedAssetDetailsForm"));
        var entity = data.entity;
        var fixedAssetDetailsMap = data.fixedAssetDetailsMap;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        beforeCreateOrUpdateFAD();

        dropDownPoId.setDataSource(fixedAssetDetailsMap.poList);
        dropDownPoId.value(entity.poId);
        dropDownInventoryId.setDataSource(fixedAssetDetailsMap.inventoryList);
        dropDownInventoryId.value(entity.currentInventoryId);
        dropDownItemId.setDataSource(fixedAssetDetailsMap.itemList);
        dropDownItemId.value(entity.itemId);
        dropDownOwnerTypeId.value(entity.ownerTypeId);
        $('#name').val(entity.name);
        $('#description').val(entity.description);
        $('#purchaseDate').val(fixedAssetDetailsMap.purchaseDate);
        $('#expireDate').val(fixedAssetDetailsMap.expireDate);
        dropDownInventoryTypeId.value(fixedAssetDetailsMap.inventoryTypeId);

        updateCost();
        applyClassOnExpireDate();
        showLoadingSpinner(false);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function applyClassOnExpireDate() {
        var ownerTypeId = dropDownOwnerTypeId.value();
        $('#expireDate').attr('required', false);
        if (ownerTypeId == ownerTypeRentalId) {  // 552 is System Entity Rental Id
            $('#expireDateInfo').show();
            $('#expireDate').attr('required', true);
        } else {
            $('#expireDate').val('');
            $('#expireDateInfo').hide();
        }
    }

    <sec:access url="/fxdFixedAssetDetails/list">
    var strUrl = "${createLink(controller: 'fxdFixedAssetDetails', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});
    if (fadListModel) {
        $("#flex1").flexAddData(fadListModel);
    }
    </sec:access>

</script>
