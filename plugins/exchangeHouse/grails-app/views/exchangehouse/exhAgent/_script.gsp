<script language="javascript" type="text/javascript">
    var output = false;
    var exhAgentListModel = false;

    $(document).ready(function () {
        onLoadExhAgent();
    });

    function onLoadExhAgent() {
        initializeForm($("#agentForm"), onSubmitExhAgentObject);
        output =${output };
        exhAgentListModel = output.agentList ? output.agentList : false;

        initGrid();
        populateFlex1();

        $(document).attr('title', "Exchange House - Create  Agent");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhAgent/show");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, hide: true, align: "right"},
                        {display: "Name", name: "name", width: 155, sortable: false, align: "left"},
                        {display: "Address", name: "address", width: 250, sortable: false, align: "left"},
                        {display: "City", name: "city", width: 150, sortable: false, align: "left"},
                        {display: "Phone No", name: "phone", width: 100, sortable: false, align: "left"},
                        {display: "Currency", name: "symbol", width: 100, sortable: false, align: "left"},
                        {display: "Balance", name: "balance", width: 120, sortable: false, align: "left"},
                        {display: "Credit Limit", name: "creditLimit", width: 100, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/exhAgent/select,/exhAgent/update">
                        {name: 'Edit', bclass: 'edit', onpress: editExhAgent},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/exhAgent/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteExhAgent},
                        </app:ifAllUrl>
                        {name: 'User', bclass: 'note', onpress: showUserAgent},
                        <app:ifAllUrl urls="/exhAgent/list">
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},
                        </app:ifAllUrl>
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Agents List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateGridForAgent
                }
        );
    }

    function editExhAgent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'agent') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var exhAgentId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhAgent', action: 'select')}?id="
                    + exhAgentId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function resetForm() {
        clearForm($("#agentForm"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
        $('#name').focus();
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showExhAgent(data);
        }
    }

    function showExhAgent(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#address').val(entity.address);
        $('#city').val(entity.city);
        $('#phone').val(entity.phone);
        $('#commissionLogic').val(entity.commissionLogic);
        $('#creditLimit').val(entity.creditLimit);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteExhAgent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'agent') == false) {
            return;
        }
        showLoadingSpinner(true);
        if (!confirm('Are you sure you want to delete the selected agent details?')) {
            return false;
        }
        var exhAgentId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhAgent', action:'delete')}?id=" + exhAgentId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            exhAgentListModel.total = parseInt(exhAgentListModel.total) - 1;
            removeEntityFromGridRows(exhAgentListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateGridForAgent(data) {
        if (data.isError) {
            showError(data.message);
            exhAgentListModel = getEmptyGridModel();
        } else {
            exhAgentListModel = data.agentList;
        }
        $("#flex1").flexAddData(exhAgentListModel);
    }

    function onSubmitExhAgentObject() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'exhAgent', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'exhAgent', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#agentForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePreCondition() {
        if (!validateForm("#agentForm")) {
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
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(exhAgentListModel.total);
                    var firstSerial = 1;

                    if (exhAgentListModel.rows.length > 0) {
                        firstSerial = exhAgentListModel.rows[0].cell[0];
                        regenerateSerial($(exhAgentListModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;
                    exhAgentListModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        exhAgentListModel.rows.pop();
                    }

                    exhAgentListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(exhAgentListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(exhAgentListModel, newEntry, 0);
                    $("#flex1").flexAddData(exhAgentListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function populateFlex1() {
        <sec:access url="/exhAgent/list">
        var strUrl = "${createLink(controller:'exhAgent', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (exhAgentListModel) {
            $("#flex1").flexAddData(exhAgentListModel);
        }
        </sec:access>
    }

    function showUserAgent(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'agent') == false) {
            return;
        }
        showLoadingSpinner(true);
        var agentId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'exhUserAgent', action: 'show')}?agentId=" + agentId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>