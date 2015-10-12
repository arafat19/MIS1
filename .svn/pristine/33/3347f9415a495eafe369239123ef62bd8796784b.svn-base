<script language="javascript">
    var accCustomGroupListModel = false;
    $(document).ready(function () {
        onLoadCustomGroupPage();
    });
    function onLoadCustomGroupPage() {
        initializeForm($("#customGroupForm"), onSubmitCustomGroup);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accCustomGroupListModel = output.accCustomGroupList;
        }
        initFlexGrid()
        populateFlexGrid()
        // update page title
        $(document).attr('title', "MIS - Create custom group");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accCustomGroup/show");
    }

    function executePreCondition() {
        if (!validateForm($("#customGroupForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitCustomGroup() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accCustomGroup', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accCustomGroup', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#customGroupForm").serialize(),
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
                var newEntry = result.accCustomGroup;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accCustomGroupListModel.total);
                    var firstSerial = 1;

                    if (accCustomGroupListModel.rows.length > 0) {
                        firstSerial = accCustomGroupListModel.rows[0].cell[0];
                        regenerateSerial($(accCustomGroupListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accCustomGroupListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accCustomGroupListModel.rows.pop();
                    }

                    accCustomGroupListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accCustomGroupListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accCustomGroupListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accCustomGroupListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#customGroupForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Description", name: "description", width: 180, sortable: true, align: "left"},
                        {display: "Active", name: "isActive", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: selectCustomGroup},
                        {name: 'Delete', bclass: 'delete', onpress: deleteCustomGroup},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Custom Groups',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accCustomGroupListModel = null;
        } else {
            accCustomGroupListModel = data;
        }
        return data;
    }

    function deleteCustomGroup(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accCustomGroupId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accCustomGroup', action: 'delete')}?id=" + accCustomGroupId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'custom group') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected custom group?')) {
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
            accCustomGroupListModel.total = parseInt(accCustomGroupListModel.total) - 1;
            removeEntityFromGridRows(accCustomGroupListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectCustomGroup(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'custom group') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var customGroupId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accCustomGroup', action: 'select')}?id="
                    + customGroupId,
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
            showCustomGroup(data);
        }
    }

    function showCustomGroup(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#description').val(entity.description);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'accCustomGroup', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accCustomGroupListModel) {
            $("#flex1").flexAddData(accCustomGroupListModel);
        }
    }

</script>
