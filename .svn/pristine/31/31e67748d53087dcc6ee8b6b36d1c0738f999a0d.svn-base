<script type="text/javascript">
    var output = false;
    var systemEntityListModel = false;
    var systemEntityTypeId;

    $(document).ready(function () {
        onLoadSystemEntityPage()
    });

    function onLoadSystemEntityPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#systemEntityForm"), onSubmitSystemEntity);

        output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            systemEntityListModel = output.systemEntityList;
            systemEntityTypeId = output.systemEntityTypeId;
            $('#systemEntityTypeId').val(systemEntityTypeId);
            $('#systemEntityTypeName').text(output.systemEntityName);
        }

        initFlex1();
        populateFlex1();

        // update page title
        $(document).attr('title', "MIS - Create System Entity Information");
        loadNumberedMenu(MENU_ID_APPLICATION, "#systemEntityType/show");

    }

    function executePreCondition() {
        if (!validateForm($("#systemEntityForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitSystemEntity() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'systemEntity', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'systemEntity', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#systemEntityForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
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
                    var previousTotal = parseInt(systemEntityListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (systemEntityListModel.rows.length > 0) {
                        firstSerial = systemEntityListModel.rows[0].cell[0];
                        regenerateSerial($(systemEntityListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    systemEntityListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        systemEntityListModel.rows.pop();
                    }

                    systemEntityListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(systemEntityListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(systemEntityListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(systemEntityListModel);
                }

                resetSystemEntityForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetSystemEntityForm() {
        clearForm($("#systemEntityForm"), $('#key'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#systemEntityTypeId').val(systemEntityTypeId); // re-assign hidden field value
    }


    function initFlex1() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Key", name: "key", width: 180, sortable: false, align: "left"},
                        {display: "Value", name: "value", width: 60, sortable: false, align: "left"},
                        {display: "Active", name: "isActive", width: 60, sortable: false, align: "center"},
                        {display: "Reserved", name: "reservedId", width: 60, sortable: false, align: "center"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/systemEntity/select,/systemEntity/update">
                        {name: 'Edit', bclass: 'edit', onpress: editSystemEntity},
                        </app:ifAllUrl>
                        <sec:access url="/systemEntity/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteSystemEntity},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Key", name: "se.key", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "type",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All System Entity List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateSystemEntityGrid
                }
        );
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetSystemEntityForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            systemEntityListModel.total = parseInt(systemEntityListModel.total) - 1;
            removeEntityFromGridRows(systemEntityListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'system entity') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected system entity?')) {
            return false;
        }
        return true;
    }

    function deleteSystemEntity(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var systemEntityId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'systemEntity', action: 'delete')}?id=" + systemEntityId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function customPopulateSystemEntityGrid(data) {
        if (data.isError) {
            showError(data.message);
            systemEntityListModel = getEmptyGridModel();
        } else {
            systemEntityListModel = data;
        }
        $('#flex1').flexAddData(systemEntityListModel);
        return false;
    }

    function editSystemEntity(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'system entity ') == false) {
            return;
        }
        resetSystemEntityForm();
        showLoadingSpinner(true);
        var systemEntityId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'systemEntity', action: 'select')}?id=" + systemEntityId,
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
            showSystemEntity(data);
        }
    }

    function showSystemEntity(data) {
        var entity = data.entity;
        $('#id').val(entity.id);

        $('#key').val(entity.key);
        $('#value').val(entity.value);
        $('#isActive').attr('checked', entity.isActive);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <%-- End: Edit operation --%>

    function populateFlex1() {
        var strUrl = "${createLink(controller:'systemEntity', action: 'list')}?systemEntityTypeId=" + systemEntityTypeId;
        $("#flex1").flexOptions({url: strUrl});

        if (systemEntityListModel) {
            $("#flex1").flexAddData(systemEntityListModel);
        }

    }

</script>
