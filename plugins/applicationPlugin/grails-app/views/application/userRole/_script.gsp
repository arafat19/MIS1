<script type="text/javascript">
    var dropDownAppUser, roleId;

    var userRoleListModel = false;
    $(document).ready(function () {
        onLoadUserRolePage();
    });

    function onLoadUserRolePage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#userRoleForm"), onSubmitUserRole);

        // update page title
        $(document).attr('title', "MIS - User Role Mapping");
        loadNumberedMenu(MENU_ID_APPLICATION, "#role/show");

        initFlexGridForUserRole();
        var output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            userRoleListModel = output.gridObj;
            $("#roleName").text(output.roleName);
            roleId = output.roleId;
            $('#role\\.id').attr('value', output.roleId);
            dropDownAppUser = initKendoDropdown($('#user\\.id'), null, null, output.lstAppUser);
        }
        populateFlexGridForRole();
    }

    function onSubmitUserRole() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if (($('#existingUserId').val().isEmpty()) && ($('#existingRoleId').val().isEmpty())) {
            actionUrl = "${createLink(controller: 'userRole', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'userRole', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userRoleForm").serialize(),
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
        if (!validateForm($("#userRoleForm"))) {
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
                if ($('#existingUserId').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(userRoleListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (userRoleListModel.rows.length > 0) {
                        firstSerial = userRoleListModel.rows[0].cell[0];
                        regenerateSerial($(userRoleListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    userRoleListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        userRoleListModel.rows.pop();
                    }

                    userRoleListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(userRoleListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModelUserRole(userRoleListModel, result.entity, 0);
                    $("#flex1").flexAddData(userRoleListModel);
                }

                var index = dropDownAppUser.select();
                dropDownAppUser.dataSource.remove(dropDownAppUser.dataSource.at(index));
                dropDownAppUser.readonly(false);

               resetFormForCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function updateListModelUserRole(listModel, entity, serialColumnIndex) {
        var existingUserId = $('#existingUserId').val();
        var existingRoleId = $('#existingRoleId').val();
        $(listModel.rows).each(function (e) {
            var userId = this.cell[1];
            var roleId = this.cell[2];
            if ((userId == existingUserId) && (roleId == existingRoleId)) {
                copyEntityProperties(this, entity, serialColumnIndex)
                return false;
            }
        });
    }

    function resetFormForCreateAgain() {
        clearForm($("#userRoleForm"), dropDownAppUser);
        $('#role\\.id').attr('value', roleId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetForm() {
        clearErrors($("#userRoleForm"));
        if ((!$('#existingUserId').val().isEmpty()) && (!$('#existingRoleId').val().isEmpty())) {
            var index = dropDownAppUser.select();
            dropDownAppUser.dataSource.remove(dropDownAppUser.dataSource.at(index));
            dropDownAppUser.readonly(false);
        }
        dropDownAppUser.value('');
        $('#existingUserId').val('');   // hidden field
        $('#existingRoleId').val('');   // hidden field
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGridForUserRole() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "User ID", name: "userId", width: 40, sortable: false, hide: true},
                        {display: "Role ID", name: "roleId", width: 40, sortable: false, hide: true},
                        {display: "User Name", name: "user", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editUserRole},
                        {name: 'Delete', bclass: 'delete', onpress: deleteUserRole},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "User Name", name: "username", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "user",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All User-Role Mapping',
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
            userRoleListModel = null;
        } else {
            userRoleListModel = data;
        }
        return data;
    }

    function editUserRole(com, grid) {
        var selectedRow = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'User-Role') == false) {
            return;
        }
        resetFormForCreateAgain();
        showLoadingSpinner(true);
        var userId = $(selectedRow).find("td").eq(1).text();
        var roleId = $(selectedRow).find("td").eq(2).text();

        $.ajax({
            url: "${createLink(controller:'userRole', action: 'select')}?userId=" + userId + "&roleId=" + roleId,
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
            showUserRole(data);
        }
    }

    function showUserRole(data) {
        var entity = data.entity;
        dropDownAppUser.setDataSource(data.lstAppUser);
        dropDownAppUser.value(entity.user.id);
        $('#existingUserId').val(entity.user.id);   // hidden field
        $('#existingRoleId').val(entity.role.id);   // hidden field
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteUserRole(com, grid) {
        var selectedRow = $('.trSelected', grid);
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var userId = $(selectedRow).find("td").eq(1).text();
        var roleId = $(selectedRow).find("td").eq(2).text();

        $.ajax({
            url: "${createLink(controller: 'userRole', action: 'delete')}?userId=" + userId + '&roleId=' + roleId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'user-role') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected user-role?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            dropDownAppUser.setDataSource(data.lstAppUser);   // re-populate kendo dropDown list
            resetFormForCreateAgain();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            userRoleListModel.total = parseInt(userRoleListModel.total) - 1;
            removeEntityFromGridRows(userRoleListModel, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateFlexGridForRole() {
        var strUrl = "${createLink(controller: 'userRole', action: 'list')}?roleId=" + roleId;
        $("#flex1").flexOptions({url: strUrl});

        if (userRoleListModel) {
            $("#flex1").flexAddData(userRoleListModel);
        }
    }

</script>
