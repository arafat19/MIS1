<script language="javascript">
    var dropDownProject, dropDownType, entityTypeId;
    var inventoryListModel = false;

    $(document).ready(function () {
        onLoadInventory();
    });

    function onLoadInventory() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#inventoryForm"), onSubmitInventory);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            inventoryListModel = output.lstInventory;
        }
        entityTypeId = $("#entityTypeId").val();
        initInventoryGrid();
        populateInventoryGrid();
        // update page title
        $(document).attr('title', "MIS - Create Inventory");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventory/show");
    }

    function executePreCondition() {
        if (!validateForm($("#inventoryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitInventory() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invInventory', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'invInventory', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#inventoryForm").serialize(),
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
                var newEntry = result.invInventory;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(inventoryListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (inventoryListModel.rows.length > 0) {
                        firstSerial = inventoryListModel.rows[0].cell[0];
                        regenerateSerial($(inventoryListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    inventoryListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        inventoryListModel.rows.pop();
                    }

                    inventoryListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(inventoryListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(inventoryListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(inventoryListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#inventoryForm"), $('#name'));
        dropDownProject.enable(true);
        dropDownType.enable(true);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initInventoryGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Project", name: "projectId", width: 270, sortable: false, align: "left"},
                        {display: "Inventory", name: "name", width: 180, sortable: false, align: "left"},
                        {display: "Description", name: "description", width: 180, sortable: false, align: "left"},
                        {display: "Is Factory", name: "isFactory", width: 100, sortable: false, align: "left"},
                        {display: "Type", name: "type", width: 180, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/invInventory/select,/invInventory/update">
                        {name: 'Edit', bclass: 'edit', onpress: editInventory},
                        </app:ifAllUrl>
                        <sec:access url="/invInventory/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInventory},
                        </sec:access>
                        <app:ifAllUrl urls="/appUserEntity/show">
                        {name: 'User', bclass: 'note', onpress: viewInventory},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Inventories',
                    useRp: true,
                    rp: 15,
                    rpOptions: [10, 15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(6) - 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    preProcess: onLoadInventoryListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
            $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadInventoryListJSON(data) {
        if (data.isError) {
            showError(data.message);
            inventoryListModel = null;
        } else {
            inventoryListModel = data;
        }
        return data;
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the inventory?')) {
            return false;
        }
        return true;
    }

    function deleteInventory(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invInventory', action: 'delete')}?id=" + inventoryId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
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
            inventoryListModel.total = parseInt(inventoryListModel.total) - 1;
            removeEntityFromGridRows(inventoryListModel, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editInventory(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller: 'invInventory', action: 'select')}?id=" + inventoryId;
        $.ajax({
            url: url,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showInventory(data);
        }
    }

    function showInventory(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownProject.value(entity.projectId);
        dropDownType.value(entity.typeId);
        dropDownProject.enable(false);
        dropDownType.enable(false);
        $('#name').val(entity.name);
        $('#description').val(entity.description);
        $('#isFactory').attr('checked', entity.isFactory);
        $('#projectId').attr('disabled', 'disabled');
        $('#typeId').attr('disabled', 'disabled');
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <%-- End: Edit operation --%>

    function populateInventoryGrid() {
        var strUrl = "${createLink(controller: 'invInventory', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (inventoryListModel) {
            $("#flex1").flexAddData(inventoryListModel);
        }
    }

    function viewInventory(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'inventory') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'appUserEntity', action: 'show')}?entityId=" + inventoryId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>
