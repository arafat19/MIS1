<%@ page import="com.athena.mis.PluginConnector" %>
<script language="javascript">
    var userGroupListModel = false;
    var entityTypeId;
    $(document).ready(function () {
        onLoadUserGroupPage();
    });

    function onLoadUserGroupPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#userGroupForm"), onSubmitUserGroup);
        var output =${output ? output : ''};

        userGroupListModel = false;
        if (output.isError) {
            showError(output.message);
        } else {
            userGroupListModel = output.appGroupList;
        }
        entityTypeId = $("#entityTypeId").val();
        // update page title
        $(document).attr('title', "MIS - Create Group");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appGroup/show");
    }

    function executePreCondition() {
        if (!validateForm($("#userGroupForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitUserGroup() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'appGroup', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'appGroup', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userGroupForm").serialize(),
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

                    var previousTotal = parseInt(userGroupListModel.total);
                    var firstSerial = 1;

                    if (userGroupListModel.rows.length > 0) {
                        firstSerial = userGroupListModel.rows[0].cell[0];
                        regenerateSerial($(userGroupListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    userGroupListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        userGroupListModel.rows.pop();
                    }

                    userGroupListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(userGroupListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(userGroupListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(userGroupListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#userGroupForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "Id", name: "id", width: 80, sortable: false, align: "right", hide: true},
                    {display: "Name", name: "name", width: 400, sortable: false, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/appGroup/select,/appGroup/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectUserGroup},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/appGroup/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteUserGroup},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/appUserEntity/show">
                    {name: 'User', bclass: 'note', onpress: viewUser},
                    </app:ifAllUrl>
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
                title: 'All Group List',
                useRp: true,
                rp: 20,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                preProcess: onLoadListJSON
            }
    );

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            userGroupListModel = null;
        } else {
            userGroupListModel = data;
        }
        return data;
    }

    function deleteUserGroup(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var userGroupId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'appGroup', action:  'delete')}?id=" + userGroupId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'group') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Group?')) {
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
            userGroupListModel.total = parseInt(userGroupListModel.total) - 1;
            removeEntityFromGridRows(userGroupListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectUserGroup(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'group') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var userGroupId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'appGroup', action: 'select')}?id="
                    + userGroupId,
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
            showUserGroup(data);
        }
    }

    function showUserGroup(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    var strUrl = "${createLink(controller:'appGroup',action:  'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (userGroupListModel) {
        $("#flex1").flexAddData(userGroupListModel);
    }

    function viewUser(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'group') == false) {
            return;
        }
        showLoadingSpinner(true);
        var groupId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'appUserEntity', action: 'show')}?entityId=" + groupId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>
