<script language="javascript">
    var output =${output ? output : ''};
    var vehicleListModel = false;

    $(document).ready(function () {
        onLoadVehiclePage();
    });

    function onLoadVehiclePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#vehicleForm"), onSubmitVehicle);

        if (output.isError) {
            showError(output.message);
        } else {
            vehicleListModel = output.vehicleList;
        }

        // update page title
        $(document).attr('title', "MIS - Add Vehicle");
        loadNumberedMenu(MENU_ID_APPLICATION, "#vehicle/show");

    }
    function executePreCondition() {
        if (!validateForm($("#vehicleForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitVehicle() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'vehicle', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'vehicle', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#vehicleForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                showLoadingSpinner(false)
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
                var newEntry = result.vehicle;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(vehicleListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (vehicleListModel.rows.length > 0) {
                        firstSerial = vehicleListModel.rows[0].cell[0];
                        regenerateSerial($(vehicleListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    vehicleListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        vehicleListModel.rows.pop();
                    }

                    vehicleListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(vehicleListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(vehicleListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(vehicleListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#vehicleForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }


    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Description", name: "description", width: 250, sortable: true, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/vehicle/select,/vehicle/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectVehicle},
                    </app:ifAllUrl>
                    <sec:access url="/vehicle/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteVehicle},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ], searchitems: [
                {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
            ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Vehicles',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                preProcess: onLoadVehicleListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadVehicleListJSON(data) {
        if (data.isError) {
            showError(data.message);
            vehicleListModel = null;
        } else {
            vehicleListModel = data;
        }
        return data;
    }


    function deleteVehicle(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true);

        var vehicleId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'vehicle', action: 'delete')}?id=" + vehicleId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'vehicle') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Vehicle?')) {
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
            vehicleListModel.total = parseInt(vehicleListModel.total) - 1;
            removeEntityFromGridRows(vehicleListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }


    function selectVehicle(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Vehicle') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var vehicleId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'vehicle', action: 'select')}?id="
                    + vehicleId,
            success: executePostConditionForSelect,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForSelect(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showVehicle(data);
        }
    }

    function showVehicle(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#description').val(entity.description);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }


    <sec:access url="/vehicle/list">
    var strUrl = "${createLink(controller: 'vehicle', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (vehicleListModel) {
        $("#flex1").flexAddData(vehicleListModel);
    }
    </sec:access>

</script>
