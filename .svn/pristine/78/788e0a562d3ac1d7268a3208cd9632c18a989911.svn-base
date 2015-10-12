<script language="javascript">
    var dropDownMaintenanceType, itemId, selectedIndex;
    var output =${output ? output : ''};
    var categoryMaintenanceTypeListModel = false;
    itemId = output.fxdItemId;
    $(document).ready(function () {
        onLoadPage();
    });

    function onLoadPage() {
        initializeForm($("#categoryMaintenanceTypeForm"), onSubmitCategoryMaintenance);

        $('#itemId').val(itemId);
        $('#itemName').text(output.fxdItemName);

        if (output.isError) {
            showError(output.message);
        } else {
            categoryMaintenanceTypeListModel = output.fxdCategoryMaintenanceTypeList;
        }
        initGrid();
        // update page title
        $(document).attr('title', "MIS - Category-Maintenance Type Mapping");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fxdFixedAssetDetails/show");
    }

    function executePreCondition() {
        if (!validateForm($('#categoryMaintenanceTypeForm'))) {
            return false;
        }
        return true;
    }

    function onSubmitCategoryMaintenance() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'fxdCategoryMaintenanceType', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'fxdCategoryMaintenanceType', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#categoryMaintenanceTypeForm").serialize(),
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
                    var previousTotal = parseInt(categoryMaintenanceTypeListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (categoryMaintenanceTypeListModel.rows.length > 0) {
                        firstSerial = categoryMaintenanceTypeListModel.rows[0].cell[0];
                        regenerateSerial($(categoryMaintenanceTypeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    categoryMaintenanceTypeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        categoryMaintenanceTypeListModel.rows.pop();
                    }

                    categoryMaintenanceTypeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(categoryMaintenanceTypeListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(categoryMaintenanceTypeListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(categoryMaintenanceTypeListModel);
                }
                resetCreateForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#categoryMaintenanceTypeForm"), dropDownMaintenanceType);
        $('#itemId').val(itemId); // re-assign hidden field value
        $('#maintenanceTypeId').attr('default_value', '');
        $('#maintenanceTypeId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");

    }

    function resetCreateForm() {
        clearErrors('#categoryMaintenanceTypeForm');
        $('#id').val('');
        $('#version').val('');
        $('#maintenanceTypeId').attr('default_value', '');
        $('#maintenanceTypeId').reloadMe();
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "Maintenance Type", name: "maintenanceTypeId", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/fxdCategoryMaintenanceType/select,/fxdCategoryMaintenanceType/update">
                        {name: 'Edit', bclass: 'edit', onpress: editCategoryMaintenanceType},
                        </app:ifAllUrl>
                        <sec:access url="/fxdCategoryMaintenanceType/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteCategoryMaintenanceType},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Maintenance Type", name: "maintenanceType", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Category Maintenance Type Mapping',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight()-25,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadCategoryMaintenanceTypeListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadCategoryMaintenanceTypeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            categoryMaintenanceTypeListModel = null;
        } else {
            categoryMaintenanceTypeListModel = data;
        }
        return categoryMaintenanceTypeListModel;
    }

    function deleteCategoryMaintenanceType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var id = getSelectedIdFromGrid($('#flex1'))

        $.ajax({
            url: "${createLink(controller:'fxdCategoryMaintenanceType', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'category-maintenance type') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected category-maintenance type mapping?')) {
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
            categoryMaintenanceTypeListModel.total = parseInt(categoryMaintenanceTypeListModel.total) - 1;
            removeEntityFromGridRows(categoryMaintenanceTypeListModel, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editCategoryMaintenanceType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'maintenance type') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'fxdCategoryMaintenanceType', action: 'select')}?id=" + id,
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
    }

    function showCategoryMaintenanceType(data) {
        var entity = data.entity
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#maintenanceTypeId').attr('default_value', entity.maintenanceTypeId);
        $('#maintenanceTypeId').reloadMe();
        selectedIndex = dropDownMaintenanceType.select();
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <sec:access url="/fxdCategoryMaintenanceType/list">
    var strUrl = "${createLink(controller:'fxdCategoryMaintenanceType', action: 'list')}?itemId=" + itemId;
    $("#flex1").flexOptions({url: strUrl});

    if (categoryMaintenanceTypeListModel) {
        $("#flex1").flexAddData(categoryMaintenanceTypeListModel);
    }
    </sec:access>

</script>
