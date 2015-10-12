<script language="javascript" type="text/javascript">
    var dropDownProject;
    var output = false;
    var accIpcListModel = false;

    $(document).ready(function () {
        onLoadAccIpcPage();
    });

    function onLoadAccIpcPage() {
        initializeForm($("#accIpcForm"), onSubmitAccIpc);
        output = ${output ? output : ''};
        if (output.isError) {
            showError(data.message);
        } else {
            accIpcListModel = output.accIpcList;
        }
        initGrid();
        populateFlex1();

        //update page title
        $(document).attr('title', "Accounting - Create IPC");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accIpc/show");
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
                        {display: "IPC No", name: "ipcNo", width: 180, sortable: false, align: "left"},
                        {display: "Project Name", name: "project_id", width: 350, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accIpc/select,/accIpc/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccIpc},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accIpc/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccIpc},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "IPC No", name: "ipc.ipc_no", width: 180, sortable: true, align: "left"},
                        {display: "Project Name", name: "p.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All IPC List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateGridForAccIpc
                }
        );
    }

    function selectAccIpc(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'IPC') == false) {
            return;
        }
        resetAccIpcForm();
        showLoadingSpinner(true);
        var accIpcId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accIpc', action: 'select')}?id=" + accIpcId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function resetAccIpcForm() {
        clearForm($("#accIpcForm"), dropDownProject);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showAccIpc(data);
        }
    }

    function showAccIpc(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#ipcNo').val(entity.ipcNo);
        dropDownProject.value(entity.projectId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteAccIpc(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIpcId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accIpc', action:'delete')}?id=" + accIpcId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });

    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'IPC') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected IPC?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetAccIpcForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            accIpcListModel.total = parseInt(accIpcListModel.total) - 1;
            removeEntityFromGridRows(accIpcListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateGridForAccIpc(data) {
        if (data.isError) {
            showError(data.message);
            accIpcListModel = getEmptyGridModel();
        } else {
            accIpcListModel = data.accIpcList;
        }
        $("#flex1").flexAddData(accIpcListModel);
    }

    function onSubmitAccIpc() {

        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accIpc', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accIpc', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accIpcForm").serialize(),
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
        if (!validateForm($("#accIpcForm"))) {
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
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accIpcListModel.total);
                    var firstSerial = 1;

                    if (accIpcListModel.rows.length > 0) {
                        firstSerial = accIpcListModel.rows[0].cell[0];
                        regenerateSerial($(accIpcListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accIpcListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accIpcListModel.rows.pop();
                    }

                    accIpcListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accIpcListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accIpcListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accIpcListModel);
                }

                resetAccIpcForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function populateFlex1() {
        var strUrl = "${createLink(controller:'accIpc', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accIpcListModel) {
            $("#flex1").flexAddData(accIpcListModel);
        }
    }

</script>