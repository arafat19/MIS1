<script type="text/javascript">
    var modelJsonBankBranch, bankBranchListTotal, bankBranchListRows;
    var dropDownBank, dropDownDistrict, entityTypeId;

    $(document).ready(function () {
        onLoadBankBranch();
    });

    function onLoadBankBranch() {
        initializeForm($('#bankbranchForm'), onSubmitBankBranch);

        modelJsonBankBranch = '';
        modelJsonBankBranch =${modelJson};
        bankBranchListTotal = modelJsonBankBranch.bankBranchListJSON.total;
        bankBranchListRows = modelJsonBankBranch.bankBranchListJSON.rows;
        entityTypeId = $('#hidEntityType').val();

        initFlexGrid();
        loadFlexGrid();

        $(document).attr('title', "ARMS - Create Bank Branch");
        loadNumberedMenu(MENU_ID_APPLICATION, "#bankBranch/show");
    }

    function executePreCondition() {
        if (validateForm($("#bankbranchForm")) == false)
            return false;

        return true;
    }

    function onSubmitBankBranch() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'bankBranch', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'bankBranch', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#bankbranchForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
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
                    var previousTotal = parseInt(modelJsonBankBranch.bankBranchListJSON.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (modelJsonBankBranch.bankBranchListJSON.rows.length > 0) {
                        firstSerial = modelJsonBankBranch.bankBranchListJSON.rows[0].cell[0];
                        regenerateSerial($(modelJsonBankBranch.bankBranchListJSON.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    modelJsonBankBranch.bankBranchListJSON.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        modelJsonBankBranch.bankBranchListJSON.rows.pop();
                    }

                    modelJsonBankBranch.bankBranchListJSON.total = ++previousTotal;
                    $("#flex1").flexAddData(modelJsonBankBranch.bankBranchListJSON);

                } else if (result.entity != null) { // updated existing
                    updateListModel(modelJsonBankBranch.bankBranchListJSON, result.entity, 0);
                    $("#flex1").flexAddData(modelJsonBankBranch.bankBranchListJSON);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
            }
        }
    }

    function onSaveBankBranch(result) {

        if (result.isError) {
            // remove previous error if exists
            clearErrors($("#bankbranchForm"));

            var errors = $(result.errors);
            errors.each(function (i) {
                var err = $(this);
                var errStr = 'Error(s) occurred in some inputs';
                try {
                    if (err.length == 2) {
                        if ($("label[for='" + err[0] + "']").html() != null) {
                            errStr = $("label[for='" + err[0] + "']").html() + ' ' + err[1];
                        }
                    } else if (err.length == 1) {
                        errStr = err[0]
                    }
                } catch (e) { /** ignored */
                }
                showError(errStr);
            });
            showLoadingSpinner(false);
        } else if (result.entity != null) {

            $('#id').val(result.entity.id);
            $('#version').val(result.version);

            resetForm();
            $("#flex1").flexReload();
            showSuccess(result.message);
        }
    }

    function resetBankBranchForm() {
        clearForm($("#bankbranchForm"));
        $('#isSmeServiceCenter').attr('checked',false);
        $('#isPrincipleBranch').attr('checked',false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    //@todo: mamun - remove flexgrid roundTrip
                    url: "${createLink(controller: 'bankBranch', action: 'list')}",
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Bank", name: "bankId", width: 180, sortable: true, align: "left"},
                        {display: "District", name: "districtId", width: 120, sortable: true, align: "left"},
                        {display: "Name", name: "name", width: 160, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 100, sortable: true, align: "left"},
                        {display: "Address", name: "address", width: 180, sortable: true, align: "left"},
                        {display: "Principle Branch", name: "isPrincipleBranch", width: 120, sortable: true, align: "left"},
                        {display: "SME Service Center", name: "isSmeServiceCenter", width: 120, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editBankBranch},
                        {name: 'Delete', bclass: 'delete', onpress: deleteBankBranch},
                        <app:ifAllUrl urls="/appUserEntity/show">
                        {name: 'User', bclass: 'creatCustomeruser', onpress: viewUser},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: true, align: "left"},
                        {display: "Bank", name: "bankId", width: 180, sortable: true, align: "left"},
                        {display: "District", name: "districtId", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Bank Branches',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: showLoadingSpinner(false),
                    preProcess: onLoadBankBranchListJSON

                }
        );
    }

    function onLoadBankBranchListJSON(data) {
        modelJsonBankBranch.bankBranchListJSON = data;
        return data;
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function deleteBankBranch(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var bankBranchId = getSelectedIdFromGrid($('#flex1'));


        $.ajax({
            url: "${createLink(controller: 'bankBranch', action: 'delete')}?id=" + bankBranchId,
            success: executePostConditionForDelete,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'bank brunch') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Bank Branch?')) {
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
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            bankBranchListTotal = parseInt(bankBranchListTotal) - 1;
            removeEntityFromGridRows(modelJsonBankBranch.bankBranchListJSON, selectedRow);

        } else {
            showError(data.message);
        }
    }


    function editBankBranch(com, grid) {
        clearForm($("#bankbranchForm"));
        if (executeCommonPreConditionForSelect($('#flex1'), 'bank branch') == false) {
            return;
        }

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var bankBranchId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'bankBranch', action: 'edit')}?id="
                    + bankBranchId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForEdit(data) {
        if (data.entity == null) {
            showError(result.message);
        } else {
            showBankBranch(data);
        }
    }

    function resetForm() {
        clearForm($("#bankbranchForm"));
        setButtonDisabled($('.save'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function showBankBranch(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownBank.value(entity.bankId);
        dropDownDistrict.value(entity.districtId);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $('#address').val(entity.address);
        $('#isSmeServiceCenter').attr('checked',entity.isSmeServiceCenter);
        $('#isGlobal').attr('checked',entity.isGlobal);
        $('#isPrincipleBranch').attr('checked',entity.isPrincipleBranch);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }


    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'bankBranch', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
    }

    function viewUser(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'bank branch') == false) {
            return;
        }
        showLoadingSpinner(true);
        var entityId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'appUserEntity', action: 'show')}?entityTypeId=" + entityTypeId + "&entityId=" + entityId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>