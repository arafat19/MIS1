<script type="text/javascript">
    var accSubAccountListListModel = false;

    $(document).ready(function () {
        onLoadAccSubAccountPage();
    });

    function onLoadAccSubAccountPage() {
        initializeForm($("#accSubAccountForm"), onSubmitAccSubAccount);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accSubAccountListListModel = output.accSubAccountList;
        }
        initFlexGrid()
        populateFlexGrid()
        // update page title
        $(document).attr('title', "MIS - Create Sub Account");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accSubAccount/show");
    }

    function executePreCondition() {
        if (!validateForm($("#accSubAccountForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAccSubAccount() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accSubAccount', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accSubAccount', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accSubAccountForm").serialize(),
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
                var newEntry = result.accSubAccount;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accSubAccountListListModel.total);
                    var firstSerial = 1;

                    if (accSubAccountListListModel.rows.length > 0) {
                        firstSerial = accSubAccountListListModel.rows[0].cell[0];
                        regenerateSerial($(accSubAccountListListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accSubAccountListListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accSubAccountListListModel.rows.pop();
                    }

                    accSubAccountListListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accSubAccountListListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accSubAccountListListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accSubAccountListListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#accSubAccountForm"), $("#code"));
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
                        {display: "Description", name: "description", width: 180, sortable: true, align: "left"} ,
                        {display: "Account Code", name: "coaId", width: 180, sortable: false, align: "left"},
                        {display: "Active", name: "isActive", width: 50, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accSubAccount/select,/accSubAccount/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccSubAccount},
                        </app:ifAllUrl>
                        <sec:access url="/accSubAccount/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccSubAccount},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Description", name: "description", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Sub Accounts',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateAccSubAccountListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateAccSubAccountListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accSubAccountListListModel = null;
        } else {
            accSubAccountListListModel = data.accSubAccountList;
        }
        $("#flex1").flexAddData(accSubAccountListListModel);
    }

    function deleteAccSubAccount(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accSubAccountId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accSubAccount', action: 'delete')}?id=" + accSubAccountId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sub account') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Sub Account?')) {
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
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            accSubAccountListListModel.total = parseInt(accSubAccountListListModel.total) - 1;
            removeEntityFromGridRows(accSubAccountListListModel, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function selectAccSubAccount(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' sub account') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var accSubAccountId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accSubAccount', action: 'select')}?id="
                    + accSubAccountId,

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
            showAccSubAccount(data);
        }
    }

    function showAccSubAccount(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#description').val(entity.description);
        $('#code').val(data.code);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'accSubAccount', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accSubAccountListListModel) {
            $("#flex1").flexAddData(accSubAccountListListModel);
        }
    }

</script>
