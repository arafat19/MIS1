<script language="javascript">
    var accIouPurposeModel =${output ? output : ''};
    var accIouPurposeGrid = null;
    var dropDownIndentDetails, amount, slipId;

    $(document).ready(function () {
        onLoadPurposeForm();
    });

    function onLoadPurposeForm() {
        initializeForm($("#accIouPurposeForm"), onSubmitPurposeForm);
        var isError = accIouPurposeModel.isError;
        if (isError == 'true') {
            var message = accIouPurposeModel.message;
            showError(message);
            return;
        }
        initGrid();
        dropDownIndentDetails = initKendoDropdown($('#indentDetailsId'), null, null, accIouPurposeModel.objectMap.itemList);

        $('#amount').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"
        });
        amount = $("#amount").data("kendoNumericTextBox");

        accIouPurposeGrid = accIouPurposeModel.gridOutput;
        $('#lblProjectName').html(accIouPurposeModel.objectMap.projectName);
        $('#accIouSlipId').html(accIouPurposeModel.objectMap.slipId);
        $('#slipId').val(accIouPurposeModel.objectMap.slipId);
        slipId = accIouPurposeModel.objectMap.slipId;
        $('#lblIndentTrace').html(accIouPurposeModel.objectMap.indentId);

        // update page title
        $(document).attr('title', "MIS - IOU Purpose");
        // parents left menu selected
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accIouSlip/show");
    }

    function populateItemDescription() {
        var indentDetailsId = dropDownIndentDetails.value();
        if (indentDetailsId == '') {
            $('#itemDescription').html('');
            return false;
        }

        var itemDescription = dropDownIndentDetails.dataItem().item_description
        $('#itemDescription').html(itemDescription);
        return true;
    }

    function executePreCondition() {
        if (!validateForm($("#accIouPurposeForm"))) {
            return false;
        }

        return true;
    }

    function onSubmitPurposeForm() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);

        var data = jQuery("#accIouPurposeForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'accIouPurpose', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'accIouPurpose', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: data,
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
        } else {
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(accIouPurposeGrid.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (accIouPurposeGrid.rows.length > 0) {
                        firstSerial = accIouPurposeGrid.rows[0].cell[0];
                        regenerateSerial($(accIouPurposeGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    accIouPurposeGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accIouPurposeGrid.rows.pop();
                    }

                    accIouPurposeGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(accIouPurposeGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(accIouPurposeGrid, result.entity, 0);
                    $("#flex1").flexAddData(accIouPurposeGrid);
                }
                dropDownIndentDetails.setDataSource(result.itemList);
                resetCreateForm(result);
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetCreateForm(result) {
        clearForm($('#accIouPurposeForm'), dropDownIndentDetails);
        $('#itemDescription').html('');
        $('#slipId').val(slipId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    //-------------
    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "center"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Purpose", name: "purpose", width: 550, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                        {display: "Created By", name: "username", width: 170, sortable: false, align: "left"},
                        {display: "Updated By", name: "username", width: 170, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accIouPurpose/select,/accIouPurpose/update">
                        {name: 'Edit', bclass: 'edit', onpress: editAccIouPurpose},
                        </app:ifAllUrl>
                        <sec:access url="/accIouPurpose/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccIouPurpose},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All IOU Purpose',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadAccIouPurpose
                }
        );
    }

    function deleteAccIouPurpose(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accIouPurposeId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accIouPurpose', action: 'delete')}?id=" + accIouPurposeId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'account IOU purpose') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected IOU Purpose?')) {
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
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            accIouPurposeGrid.total = parseInt(accIouPurposeGrid.total) - 1;
            removeEntityFromGridRows(accIouPurposeGrid, selectedRow);
            dropDownIndentDetails.setDataSource(data.itemList);
            resetCreateForm(data);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No IOU Purpose found');
        }
    }

    function editAccIouPurpose(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'account IOU purpose') == false) {
            return;
        }
        var accIouPurposeId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'accIouPurpose', action: 'select')}?id=" + accIouPurposeId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateAccIouPurpose(data);
        }
    }

    function populateAccIouPurpose(data) {
        resetCreateForm(data);

        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownIndentDetails.setDataSource(data.itemList);
        dropDownIndentDetails.value(entity.indentDetailsId);
        populateItemDescription();
        amount.value(entity.amount);
        $('#comments').val(entity.comments);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function onLoadAccIouPurpose(data) {
        if (data.isError) {
            showError(data.message);
            accIouPurposeGrid = getEmptyGridModel();
        } else {
            accIouPurposeGrid = data;
        }
        $("#flex1").flexAddData(accIouPurposeGrid);
    }

    <sec:access url="/accIouPurpose/list">
    var accIouSlipId = $('#slipId').val();
    var strUrl = "${createLink(controller: 'accIouPurpose', action: 'list')}?slipId=" + accIouSlipId;
    $("#flex1").flexOptions({url: strUrl});

    if (accIouPurposeGrid) {
        $("#flex1").flexAddData(accIouPurposeGrid);
    }
    </sec:access>

</script>
