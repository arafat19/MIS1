<script language="javascript">
    var output =${output ? output : ''};
    var maintenanceTypeListModel = false;
    $(document).ready(function () {
        onLoadPage()

    });
    function onLoadPage() {
        initializeForm($('#maintenanceTypeForm'), onSubmitPage);


        if (output.isError) {
            showError(output.message);
        } else {
            maintenanceTypeListModel = output.fxdMaintenanceTypeList;
        }
        $('#name').focus();
        initGrid();
        // update page title
        $('span.headingText').html('Create Maintenance Type');
        $('#icon_box').attr('class', 'pre-icon-header maintenance type');
        $(document).attr('title', "MIS - Create Maintenance Type");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fxdMaintenanceType/show");
    }

    function executePreCondition() {
        if ($('#name').length <= 0) {
            showError('Enter maintenance name');
            return false;
        }
        if (!validateForm($('#maintenanceTypeForm'))) {
            return false;
        }
        return true;
    }

    function onSubmitPage() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'fxdMaintenanceType', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'fxdMaintenanceType', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#maintenanceTypeForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
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


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.fxdMaintenanceType;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(maintenanceTypeListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (maintenanceTypeListModel.rows.length > 0) {
                        firstSerial = maintenanceTypeListModel.rows[0].cell[0];
                        regenerateSerial($(maintenanceTypeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    maintenanceTypeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        maintenanceTypeListModel.rows.pop();
                    }

                    maintenanceTypeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(maintenanceTypeListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(maintenanceTypeListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(maintenanceTypeListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#maintenanceTypeForm"), $('#name'));
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
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/fxdMaintenanceType/select,/fxdMaintenanceType/update">
                        {name: 'Edit', bclass: 'edit', onpress: editMaintenanceType},
                        </app:ifAllUrl>
                        <sec:access url="/fxdMaintenanceType/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteMaintenanceType},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Maintenance Type',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight()-15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadMaintenanceTypeListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadMaintenanceTypeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            maintenanceTypeListModel = null;
        } else {
            maintenanceTypeListModel = data;
        }
        return maintenanceTypeListModel;
    }


    function deleteMaintenanceType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var id = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'fxdMaintenanceType', action: 'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'maintenance type') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected maintenance type?')) {
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
            maintenanceTypeListModel.total = parseInt(maintenanceTypeListModel.total) - 1;
            removeEntityFromGridRows(maintenanceTypeListModel, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }


    function editMaintenanceType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'maintenance type') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'fxdMaintenanceType', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select a maintenance type to edit");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showMaintenanceType(data);
        }
    }

    function showMaintenanceType(data) {
        var entity = data.entity
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <sec:access url="/fxdMaintenanceType/list">
    var strUrl = "${createLink(controller:'fxdMaintenanceType', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (maintenanceTypeListModel) {
        $("#flex1").flexAddData(maintenanceTypeListModel);
    }
    </sec:access>

</script>
