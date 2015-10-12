<script type="text/javascript">

    var jsonBankModel;
    $(document).ready(function () {
        onLoadBank();
    });

    function onLoadBank() {
        var output = ${modelJson};

        initializeForm($('#bankForm'), onSubmitBank);

        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            jsonBankModel = output.gridObj;    // set data in a global variable to populate
        }

        initFlexGrid();
        loadFlexGrid();
        $(document).attr('title', "ARMS - Create Bank");
        loadNumberedMenu(MENU_ID_APPLICATION, "#bank/show");
    }

    function executePreCondition() {
        // trim field vales before process.
        if (!validateForm($("#bankForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitBank() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'bank', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'bank', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#bankForm").serialize(),
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
        } else {
            // @todo-Azam to load grid without round trip
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(jsonBankModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (jsonBankModel.rows.length > 0) {
                        firstSerial = jsonBankModel.rows[0].cell[0];
                        regenerateSerial($(jsonBankModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    jsonBankModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        jsonBankModel.rows.pop();
                    }

                    jsonBankModel.total = ++previousTotal;
                    $("#flex1").flexAddData(jsonBankModel);

                } else if (result.entity != null) { // updated existing
                    updateListModel(jsonBankModel, result.entity, 0);
                    $("#flex1").flexAddData(jsonBankModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
            }
        }
    }

    function resetForm() {
        clearForm($("#bankForm"), $('#name'));
        setButtonDisabled($('.save'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
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
                        {display: "Code", name: "code", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editBank},
                        {name: 'Delete', bclass: 'delete', onpress: deleteBank},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Banks',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: customPopulateGrid
                }
        );
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            jsonBankModel = getEmptyGridModel();
        } else {
            jsonBankModel = data.gridObj;
        }
        $("#flex1").flexAddData(jsonBankModel);
    }


    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    <%-- Start : Delete operation of Bank --%>
    function deleteBank(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call

        var bankId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'bank', action: 'delete')}?id=" + bankId,
            success: executePostConditionForDelete,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'bank') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Bank?')) {
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
            // @todo-Azam managing grid to minimize round-trip
            jsonBankModel.total = parseInt(jsonBankModel.total) - 1;
            removeEntityFromGridRows(jsonBankModel, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }

    function editBank(com, grid) {
        //clear form before putting edited value
        clearForm($("#bankForm"));

        if (executeCommonPreConditionForSelect($('#flex1'), 'bank') == false) {
            return;
        }
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var bankId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'bank', action: 'edit')}?id="
                    + bankId,
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
            showBank(data);
        }
    }

    function showBank(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $('#isSystemBank').attr('checked',entity.isSystemBank);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'bank', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (jsonBankModel) {
            $("#flex1").flexAddData(jsonBankModel);
        }
    }

</script>
