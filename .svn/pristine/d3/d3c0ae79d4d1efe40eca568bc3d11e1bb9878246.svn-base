<script language="javascript">
    var accVoucherTypeCoaListModel = false;
    var dropDownVoucherType;
    $(document).ready(function () {
        onLoadVoucherTypePage();
    });

    function onLoadVoucherTypePage() {
        initializeForm($("#accVoucherTypeCoaForm"), onSubmitVoucherType);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accVoucherTypeCoaListModel = output.accVoucherTypeCoaList;
        }
        // update page title
        $(document).attr('title', "MIS - Create VoucherTypeCOA");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accVoucherTypeCoa/show");
    }

    function executePreCondition() {
        if (!validateForm($("#accVoucherTypeCoaForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitVoucherType() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accVoucherTypeCoa', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accVoucherTypeCoa', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accVoucherTypeCoaForm").serialize(),
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
                var newEntry = result.accVoucherTypeCoa;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accVoucherTypeCoaListModel.total);
                    var firstSerial = 1;

                    if (accVoucherTypeCoaListModel.rows.length > 0) {
                        firstSerial = accVoucherTypeCoaListModel.rows[0].cell[0];
                        regenerateSerial($(accVoucherTypeCoaListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accVoucherTypeCoaListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accVoucherTypeCoaListModel.rows.pop();
                    }

                    accVoucherTypeCoaListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accVoucherTypeCoaListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accVoucherTypeCoaListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accVoucherTypeCoaListModel);
                }

                resetFormForCreate();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#accVoucherTypeCoaForm"), dropDownVoucherType);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetFormForCreate() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#id').val('');
        $('#version').val('');
        $('#code').val('');
        $('#code').focus();
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Voucher Type", name: "accVoucherTypeId", width: 180, sortable: false, align: "left"} ,
                    {display: "Account Code", name: "coaId", width: 180, sortable: false, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/accVoucherTypeCoa/select,/accVoucherTypeCoa/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectVoucherType},
                    </app:ifAllUrl>
                    <sec:access url="/accVoucherTypeCoa/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteVoucherType},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Voucher Type", name: "accVoucherTypeId", width: 180, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'All Voucher Types',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                customPopulate: populateVoucherTypeListJSON
            }
    );

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateVoucherTypeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accVoucherTypeCoaListModel = null;
        } else {
            accVoucherTypeCoaListModel = data.accVoucherTypeCoaList;
        }
        $("#flex1").flexAddData(accVoucherTypeCoaListModel);
    }

    function deleteVoucherType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accVoucherTypeCoaId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accVoucherTypeCoa', action: 'delete')}?id=" + accVoucherTypeCoaId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher type') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected VoucherType?')) {
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
            accVoucherTypeCoaListModel.total = parseInt(accVoucherTypeCoaListModel.total) - 1;
            removeEntityFromGridRows(accVoucherTypeCoaListModel, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    function selectVoucherType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' voucher type') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var accVoucherTypeCoaId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accVoucherTypeCoa', action: 'select')}?id="
                    + accVoucherTypeCoaId,

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
            showVoucherType(data);
        }
    }

    function showVoucherType(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#code').val(data.code);
        dropDownVoucherType.value(entity.accVoucherTypeId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    var strUrl = "${createLink(controller: 'accVoucherTypeCoa', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (accVoucherTypeCoaListModel) {
        $("#flex1").flexAddData(accVoucherTypeCoaListModel);
    }

</script>
