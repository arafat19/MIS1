<script language="javascript">
    var accGroupListModel = false;

    $(document).ready(function () {
        onLoadAccGroupPage();
    });

    function onLoadAccGroupPage() {
        initializeForm($("#accGroupForm"), onSubmitAccGroup);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accGroupListModel = output.accGroupList;
        }
        initFlexGrid()
        populateFlexGrid()
        // update page title
        $(document).attr('title', "MIS - Create Account Group");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accGroup/show");
    }

    function executePreCondition() {
        if (!validateForm($("#accGroupForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAccGroup() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accGroup', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accGroup', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accGroupForm").serialize(),
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
                var newEntry = result.accGroup;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accGroupListModel.total);
                    var firstSerial = 1;

                    if (accGroupListModel.rows.length > 0) {
                        firstSerial = accGroupListModel.rows[0].cell[0];
                        regenerateSerial($(accGroupListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accGroupListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accGroupListModel.rows.pop();
                    }

                    accGroupListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accGroupListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accGroupListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accGroupListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#accGroupForm"), $('#name'));
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
                        {display: "Active", name: "isActive", width: 180, sortable: true, align: "left"},
                        {display: "Reserved", name: "reserved", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: selectAccGroup},
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccGroup},
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
                    title: 'All Account Groups',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
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
            accGroupListModel = null;
        } else {
            accGroupListModel = data;
        }
        return data;
    }

    function deleteAccGroup(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accGroupId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accGroup', action: 'delete')}?id=" + accGroupId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'account group') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected account group?')) {
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
            accGroupListModel.total = parseInt(accGroupListModel.total) - 1;
            removeEntityFromGridRows(accGroupListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectAccGroup(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'account group') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accGroup', action: 'select')}?id="
                    + inventoryId,
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
            showAccGroup(data);
        }
    }

    function showAccGroup(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#description').val(entity.description);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'accGroup', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accGroupListModel) {
            $("#flex1").flexAddData(accGroupListModel);
        }
    }

</script>
