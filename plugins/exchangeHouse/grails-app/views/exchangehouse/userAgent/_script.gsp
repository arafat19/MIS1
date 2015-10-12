<script type="text/javascript">

    var userAgentListModel = false;
    var dropDownAppUser, agentId;
    $(document).ready(function () {
        onLoadUserAgentPage();
    });

    function onLoadUserAgentPage() {
        initializeForm($("#userAgentForm"), onSubmitUserAgent);
        var output = ${output};
        userAgentListModel = output.gridObj ? output.gridObj : false;
        agentId = output.agentId;
        $('#agentId').val(agentId);
        $('#agentName').text(output.agentName);

        initFlexGridForUserAgent();
        populateFlexGridForUserAgent();

        // update page title
        $(document).attr('title', "ARMS - User-Agent Mapping");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhAgent/show");
    }

    function executePreCondition() {
        if (!validateForm("#userAgentForm")) {
            return false;
        }
        return true;
    }

    function onSubmitUserAgent() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'exhUserAgent', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'exhUserAgent', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#userAgentForm").serialize(),
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
                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(userAgentListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (userAgentListModel.rows.length > 0) {
                        firstSerial = userAgentListModel.rows[0].cell[0];
                        regenerateSerial($(userAgentListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    userAgentListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        userAgentListModel.rows.pop();
                    }

                    userAgentListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(userAgentListModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(userAgentListModel, result.entity, 0);
                    $("#flex1").flexAddData(userAgentListModel);
                }

                var index = dropDownAppUser.select();
                dropDownAppUser.dataSource.remove(dropDownAppUser.dataSource.at(index));
                dropDownAppUser.readonly(false);
                resetCreateForm();
                showSuccess(result.message);
            } catch (e) {
            }
        }
    }

    function resetUserAgentForm() {
        clearErrors($("#userAgentForm"));
        if (!$('#id').val().isEmpty()) {
            var index = dropDownAppUser.select();
            dropDownAppUser.dataSource.remove(dropDownAppUser.dataSource.at(index));
            dropDownAppUser.readonly(false);
        }
        dropDownAppUser.value('');
        $('#id').val('');   // hidden field
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetCreateForm() {
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
        clearForm($("#userAgentForm"), dropDownAppUser);
        $('#agentId').val(agentId);
    }

    function initFlexGridForUserAgent() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "User ID", name: "userId", width: 30, sortable: false, hide: true},
                        {display: "User Name", name: "user", width: 180, sortable: false, align: "left"},
                        {display: "Agent Id", name: "agentId", width: 30, sortable: false, hide: true}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editUserAgent},
                        {name: 'Delete', bclass: 'delete', onpress: deleteUserAgent},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "User Name", name: "au.username", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All User-Agent Mappings',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                    },
                    preProcess: onLoadUserAgentListJSON
                }
        );
    }

    function onLoadUserAgentListJSON(data) {
        if (data.isError) {
            showError(data.message);
            userAgentListModel = getEmptyGridModel();
        } else {
            userAgentListModel = data;
        }
        return userAgentListModel;
    }

    function editUserAgent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1')) == false) {
            return;
        }
        resetCreateForm();
        showLoadingSpinner(true);
        var mappingId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhUserAgent', action: 'select')}?id=" + mappingId,
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
            showUserAgent(data);
        }
    }

    function showUserAgent(data) {
        var entity = data.entity;
        dropDownAppUser.setDataSource(data.lstAppUser);
        dropDownAppUser.value(entity.appUserId);
        $('#id').val(entity.id);   // hidden field
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteUserAgent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1')) == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var userAgentMappingId = getSelectedIdFromGrid($('#flex1'));
        if (!confirm('Are you sure to delete the selected user-agent-mapping?')) {
            return false;
        }
        $.ajax({
            url: "${createLink(controller:'exhUserAgent', action: 'delete')}?id=" + userAgentMappingId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            dropDownAppUser.setDataSource(data.lstAppUser);   // re-populate kendo dropDown list
            resetCreateForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            userAgentListModel.total = parseInt(userAgentListModel.total) - 1;
            removeEntityFromGridRows(userAgentListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateFlexGridForUserAgent() {
        var strUrl = "${createLink(controller:'exhUserAgent', action: 'list')}?agentId=" + agentId;
        $("#flex1").flexOptions({url: strUrl});

        if (userAgentListModel) {
            $("#flex1").flexAddData(userAgentListModel);
        }
    }

</script>
