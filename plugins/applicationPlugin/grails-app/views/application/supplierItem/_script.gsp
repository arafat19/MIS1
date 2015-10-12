<script type="text/javascript">

    var dropDownItemType, dropDownItem, supplierItemListModel, supplierItemGrid, supplier, supplierId;

    $(document).ready(function () {
        onLoadSupplierItem();
    });

    function onLoadSupplierItem() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#supplierItemForm"), onSubmitSupplierItem);
        dropDownItem = initKendoDropdown($('#itemId'), null, null, null);

        // update page title
        $(document).attr('title', "MIS - Create Supplier's Item");
        loadNumberedMenu(MENU_ID_APPLICATION, "#supplier/show");

        supplierItemListModel = ${output ? output : ''};
        var isError = supplierItemListModel.isError;
        if (isError == 'true') {
            var message = supplierItemListModel.message;
            showError(message);
            return;
        }
        supplier = supplierItemListModel.supplier;
        if (supplier) {
            setSupplierInfo();
        }
        supplierItemGrid = supplierItemListModel.supplierItemList;
    }

    function onChangeItemType() {
        var supplierId = supplier.id;
        var itemTypeId = dropDownItemType.value();
        $('#itemCode').text('');
        $('#itemUnit').text('');
        dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
        dropDownItem.value('');

        if (itemTypeId == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'supplierItem', action: 'getItemListForSupplierItem')}?supplierId=" + supplierId + "&itemTypeId=" + itemTypeId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
                } else {
                    dropDownItem.setDataSource(data.itemList);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function setSupplierInfo() {
        $('#supplierId').val(supplier.id);
        $('#supplierName').text(supplier.name);
        if (supplier.bankAccount) {
            $('#supplierAccount').text(supplier.bankAccount);
        }
        if (supplier.address) {
            $('#supplierAddress').text(supplier.address);
        }
        if (supplier.bankName) {
            $('#supplierBankName').text(supplier.bankName);
        }

    }

    function resetFormForCreateAgain() {
        dropDownItemType.value('');
        dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
        dropDownItem.value('');
        $('#itemCode').text('');
        $('#itemUnit').text('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");

    }


    function onChangeItem() {
        $('#itemCode').text('');
        $('#itemUnit').text('');
        $('#itemCode').text(dropDownItem.dataItem().code);
        $('#itemUnit').text(dropDownItem.dataItem().unit);
    }

    function executePreCondition() {
        if (!validateForm($("#supplierItemForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSupplierItem() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'supplierItem', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'supplierItem', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#supplierItemForm").serialize(),
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
                    var previousTotal = parseInt(supplierItemGrid.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (supplierItemGrid.rows.length > 0) {
                        firstSerial = supplierItemGrid.rows[0].cell[0];
                        regenerateSerial($(supplierItemGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    supplierItemGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        supplierItemGrid.rows.pop();
                    }

                    supplierItemGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(supplierItemGrid);

                    resetFormForCreateAgain();
                } else if (result.entity != null) { // updated existing
                    updateListModel(supplierItemGrid, result.entity, 0);
                    $("#flex1").flexAddData(supplierItemGrid);
                    resetFormSupplierItem();
                }
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetFormSupplierItem() {
        clearForm($("#supplierItemForm"), dropDownItemType);
        dropDownItem.setDataSource(getKendoEmptyDataSource(dropDownItem, null));
        $('#itemCode').text('');
        $('#itemUnit').text('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#supplierId').val(supplier.id);  // assign value for hidden field


    }
    //-------------

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "Name", name: "name", width: 300, sortable: false, align: "left"},
                    {display: "Code", name: "code", width: 150, sortable: false, align: "left"},
                    {display: "Unit", name: "unit", width: 150, sortable: false, align: "left"} ,
                    {display: "Item Type", name: "type", width: 300, sortable: false, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/supplierItem/select,/supplierItem/update">
                    {name: 'Edit', bclass: 'edit', onpress: editSupplierItem},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/supplierItem/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteSupplierItem},
                    </app:ifAllUrl>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Item Name", name: "name", width: 150, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: "All Supplier's Item",
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function () {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                    checkGrid();
                },
                preProcess: onLoadSupplierItemListJSON
            }
    );

    function onLoadSupplierItemListJSON(data) {
        if (data.isError) {
            showError(data.message);
            supplierItemGrid = null;
        } else {
            supplierItemGrid = data;
        }
        return supplierItemGrid;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No supplier item data found');
        }
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'supplier item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Supplier Item?')) {
            return false;
        }
        return true;
    }

    function deleteSupplierItem(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var supplierItemId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'supplierItem', action: 'delete')}?id=" + supplierItemId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.deleted) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });

            resetFormSupplierItem();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            supplierItemGrid.total = parseInt(supplierItemGrid.total) - 1;
            removeEntityFromGridRows(supplierItemGrid, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function editSupplierItem(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'supplier item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierItemId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'supplierItem', action: 'select')}?id=" + supplierItemId,
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
            populateSupplierItem(data);
        }
    }

    function populateSupplierItem(data) {
        resetFormSupplierItem();
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#hidItemId').val(entity.itemId);
        supplier = data.supplier;
        setSupplierInfo();
        dropDownItemType.value(data.item.itemTypeId);
        dropDownItem.setDataSource(data.supplierItemList);
        dropDownItem.value(entity.itemId);
        $('#itemCode').text(data.item.code);
        $('#itemUnit').text(data.item.unit);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }


    window.onload = populateSupplierItemGridOnPageLoad();


    function populateSupplierItemGridOnPageLoad() {
        <sec:access url="/supplierItem/list">
        var supplierId = $('#supplierId').val();
        var strUrl = "${createLink(controller: 'supplierItem', action: 'list')}?supplierId=" + supplierId;
        $("#flex1").flexOptions({url: strUrl});

        if (supplierItemGrid) {
            $("#flex1").flexAddData(supplierItemGrid);
        }
        </sec:access>
    }


</script>