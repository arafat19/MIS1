<script language="javascript">
    var dropDownItemId, dropDownFixedAssetDetailsId, dropDownMaintenanceTypeId;
    var output =${output ? output : ''};
    var maintenanceListModel = false;
    var itemId = null, fixedAssetDetailsId = null;

    $(document).ready(function () {
        onLoadPage()

    });
    function onLoadPage() {
        initializeForm($("#maintenanceForm"), onSubmitPage);

        if (output.isError) {
            showError(output.message);
        } else {
            maintenanceListModel = output.fxdMaintenanceList;
        }

        dropDownFixedAssetDetailsId = initKendoDropdown($('#fixedAssetDetailsId'), null, null, null);
        dropDownMaintenanceTypeId = initKendoDropdown($('#maintenanceTypeId'), null, null, null);

        if (output.itemId && output.fixedAssetDetailsId) {
            itemId = output.itemId;
            fixedAssetDetailsId = output.fixedAssetDetailsId;
            dropDownItemId.value(itemId);
            dropDownItemId.trigger('change');
            dropDownFixedAssetDetailsId.value(fixedAssetDetailsId);
        }


        initGrid();
        // update page title
        $(document).attr('title', "MIS - Fixed Asset Maintenance");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fxdMaintenance/show");
    }

    function executePreCondition() {
        if (!validateForm($("#maintenanceForm"))) {
            return false;
        }
        if (!checkCustomDate($('#maintenanceDate'), 'Maintenance')) {
            $('#maintenanceDate').focus();
            return false;
        }
        return true;
    }

    function onSubmitPage() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'fxdMaintenance', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'fxdMaintenance', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#maintenanceForm").serialize(),
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
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(maintenanceListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (maintenanceListModel.rows.length > 0) {
                        firstSerial = maintenanceListModel.rows[0].cell[0];
                        regenerateSerial($(maintenanceListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    maintenanceListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        maintenanceListModel.rows.pop();
                    }

                    maintenanceListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(maintenanceListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(maintenanceListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(maintenanceListModel);
                }

                resetFormToRecreate();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetFormToRecreate() {
        clearErrors($("#maintenanceForm"));
        $('#maintenanceDate').val('');
        $('#amount').val('');
        $('#description').val('');
        $('#id').val('');
        $('#version').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        resetForm();
    }

    function resetForm() {
        clearForm($("#maintenanceForm"), dropDownItemId);
        dropDownFixedAssetDetailsId.setDataSource(getKendoEmptyDataSource());
        dropDownMaintenanceTypeId.setDataSource(getKendoEmptyDataSource());
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }


    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Category (Model / Serial No)", name: "itemName", width: 270, sortable: false, align: "left"},
                        {display: "Maintenance Type", name: "maintenanceTypeName", width: 120, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 90, sortable: false, align: "right"},
                        {display: "Date", name: "strMaintenanceDate", width: 80, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 140, sortable: false, align: "left"},
                        {display: "Description", name: "description", width: 210, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/fxdMaintenance/select,/fxdMaintenance/update">
                        {name: 'Edit', bclass: 'edit', onpress: editMaintenance},
                        </app:ifAllUrl>
                        <sec:access url="/fxdMaintenance/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteMaintenance},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Category", name: "itemName", width: 180, sortable: true, align: "left"},
                        {display: "Model", name: "modelName", width: 180, sortable: true, align: "left"},
                        {display: "Maintenance Type", name: "maintenanceTypeName", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Fixed Asset Maintenance',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadCategoryMaintenanceTypeListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadCategoryMaintenanceTypeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            maintenanceListModel = null;
        } else {
            maintenanceListModel = data;
        }
        return maintenanceListModel;
    }


    function deleteMaintenance(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var id = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'fxdMaintenance', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'maintenance') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected fixed asset maintenance?')) {
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
            maintenanceListModel.total = parseInt(maintenanceListModel.total) - 1;
            removeEntityFromGridRows(maintenanceListModel, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }


    function editMaintenance(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'maintenance') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'fxdMaintenance', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showCategoryMaintenanceType(data);
        }
//        resetFormToRecreate();
    }

    function showCategoryMaintenanceType(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownItemId.setDataSource(data.itemList);
        dropDownItemId.value(entity.itemId);
        dropDownMaintenanceTypeId.setDataSource(data.maintenanceTypeList);
        dropDownMaintenanceTypeId.value(entity.maintenanceTypeId);
        dropDownFixedAssetDetailsId.setDataSource(data.fixedAssetDetailsList);
        dropDownFixedAssetDetailsId.value(entity.fixedAssetDetailsId);
        $('#amount').val(entity.amount);
        $('#description').val(entity.description);
        $('#maintenanceDate').val(data.maintenanceDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    // To populate Inventory List
    function updateModelAndMaintenanceList() {
        var itemId = dropDownItemId.value();
        if (itemId == '') {
            dropDownFixedAssetDetailsId.setDataSource(getKendoEmptyDataSource());
            dropDownMaintenanceTypeId.setDataSource(getKendoEmptyDataSource());
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'fxdMaintenance', action: 'getMaintenanceTypeAndModelListByItemId')}?itemId=" + itemId,
            success: function (data) {
                updateModelAndMaintenanceDropDown(data);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateModelAndMaintenanceDropDown(data) {
        if (data.isError) {
            showError(data.message);
            dropDownFixedAssetDetailsId.setDataSource(getKendoEmptyDataSource());
            dropDownMaintenanceTypeId.setDataSource(getKendoEmptyDataSource());
            return false;
        }
        dropDownFixedAssetDetailsId.setDataSource(data.fixedAssetDetailsList);
        dropDownMaintenanceTypeId.setDataSource(data.maintenanceTypeList);
    }

    <sec:access url="/fxdMaintenance/list">
    var strUrl;
    if (fixedAssetDetailsId) {
        strUrl = "${createLink(controller:'fxdMaintenance', action: 'list')}?fixedAssetDetailsId=" + fixedAssetDetailsId;
    } else {
        strUrl = "${createLink(controller:'fxdMaintenance', action: 'list')}";
    }
    $("#flex1").flexOptions({url: strUrl});

    if (maintenanceListModel) {
        $("#flex1").flexAddData(maintenanceListModel);
    }
    </sec:access>

</script>
