<script type="text/javascript">

    var roleListModel = false;

    $(document).ready(function () {
        onLoadRolePage();
    });

    function onLoadRolePage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#roleForm"), onSubmitRole);

        // update page title
        $(document).attr('title', "MIS - Role");
        loadNumberedMenu(MENU_ID_APPLICATION, "#role/show");

        initFlexGridForRole();
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            roleListModel = output.roleList;
        }
        populateFlexGridForRole();
    }

    function onSubmitRole() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'role', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'role', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#roleForm").serialize(),
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

    function executePreCondition() {
        if (!validateForm($("#roleForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);

        } else {
            try {

                var newEntry = result.role;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(roleListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (roleListModel.rows.length > 0) {
                        firstSerial = roleListModel.rows[0].cell[0];
                        regenerateSerial($(roleListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    roleListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        roleListModel.rows.pop();
                    }

                    roleListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(roleListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(roleListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(roleListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#roleForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGridForRole() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editRole},
                        {name: 'Delete', bclass: 'delete', onpress: deleteRole},
                        {name: 'User', bclass: 'note', onpress: showUserRole},
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
                    title: 'All Roles',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);

                    },
                    preProcess: onLoadRoleListJSON
                }
        );
    }

    function onLoadRoleListJSON(data) {
        if (data.isError) {
            showError(data.message);
            roleListModel = null;
        } else {
            roleListModel = data;
        }
        return data;
    }

    function editRole(com, grid) {
        if (executePreConditionForEdit() == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var roleId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'role', action: 'select')}?id=" + roleId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForEdit() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'role') == false) {
            return false;
        }
        var roleId = getSelectedIdFromGrid($('#flex1'));
        if (roleId < -1) {
            showError('Selected role is reserved and can not be edited');
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.entity == null) {
            showError(result.message);
        } else {
            showRole(data);
        }
    }

    function showRole(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#hidPreviousRole').val(entity.authority);
        $('#name').val(entity.name);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteRole(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var roleId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'role', action: 'delete')}?id=" + roleId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'role') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected role? \n Related right permission will be delete accordingly')) {
            return false;
        }

        var roleId = getSelectedIdFromGrid($('#flex1'));
        if (roleId < -1) {
            showError('Selected role is reserved and can not be deleted');
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
            roleListModel.total = parseInt(roleListModel.total) - 1;
            removeEntityFromGridRows(roleListModel, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function populateFlexGridForRole() {
        var strUrl = "${createLink(controller: 'role', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (roleListModel) {
            $("#flex1").flexAddData(roleListModel);
        }
    }

    function showUserRole(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'role') == false) {
            return;
        }
        showLoadingSpinner(true);
        var roleId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'userRole', action: 'show')}?roleId=" + roleId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>
