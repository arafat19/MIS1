<script type="text/javascript">
    var indentDetailsListModel, indentDetailsGrid, prDetailsQuantity, indentId;
    var rate, quantity;
    $(document).ready(function () {
        onLoadIndentDetails();
    });

    function onLoadIndentDetails() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#indentDetailsForm"), onSubmitIndentDetails);

        $('#rate').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "৳ #.####"

        });
        rate = $("#rate").data("kendoNumericTextBox");

        $('#quantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"

        });
        quantity = $("#quantity").data("kendoNumericTextBox");

        // update page title
        $(document).attr('title', "MIS - Create Indent Details");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procIndent/show");

        indentDetailsListModel = ${output ? output : ''};
        var isError = indentDetailsListModel.isError;
        if (isError == 'true') {
            var message = indentDetailsListModel.message;
            showError(message);
            return;
        }
        if (indentDetailsListModel.indentInfo) {
            populateIndentInfo(indentDetailsListModel.indentInfo);
        }
        indentDetailsGrid = indentDetailsListModel.gridObj;
    }

    function populateIndentInfo(indentInfo) {
        $('#projectId').val(indentInfo.projectId);
        $('#lblProjectName').text(indentInfo.projectName);
        $('#lblIndentId').text(indentInfo.indentId);
        $('#indentId').val(indentInfo.indentId);
    }


    function executePreCondition() {
        if (!validateForm($("#indentDetailsForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitIndentDetails() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'procIndentDetails', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'procIndentDetails', action: 'update')}";
        }
        jQuery.ajax({
            type: 'post',
            data: jQuery("#indentDetailsForm").serialize(),
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
                    var previousTotal = parseInt(indentDetailsGrid.total);
                    // re-arranging serial
                    var firstSerial = 1;
                    if (indentDetailsGrid.rows.length > 0) {
                        firstSerial = indentDetailsGrid.rows[0].cell[0];
                        regenerateSerial($(indentDetailsGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    indentDetailsGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        indentDetailsGrid.rows.pop();
                    }

                    indentDetailsGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(indentDetailsGrid);
                } else if (result.entity != null) { // updated existing
                    updateListModel(indentDetailsGrid, result.entity, 0);
                    $("#flex1").flexAddData(indentDetailsGrid);
                }

                resetFormIndentDetails();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetFormIndentDetails() {
        clearForm($("#indentDetailsForm"), $('#itemDescription'));

        var indentInfo = indentDetailsListModel.indentInfo;

        $('#projectId').val(indentInfo.projectId);
        $('#indentId').val(indentInfo.indentId);

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#total').text('');
    }


    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "Item", name: "itemDescription", width: 150, sortable: false, align: "left"},
                    {display: "Quantity", name: "quantity", width: 150, sortable: false, align: "left"},
                    {display: "Rate", name: "rate", width: 150, sortable: false, align: "left"},
                    {display: "Total", name: "total", width: 150, sortable: false, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/procIndentDetails/select,/procIndentDetails/update">
                    {name: 'Edit', bclass: 'edit', onpress: editIndentDetails},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/procIndentDetails/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteIndentDetails},
                    </app:ifAllUrl>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Item", name: "itemDescription", width: 150, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Indent Details',
                useRp: true,
                rp: 15,
                rpOptions: [10, 15, 20, 25],
                showTableToggleBtn: false,
                height: getGridHeight(2),
                afterAjax: function () {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
//                            checkGrid();
                },
                preProcess: onLoadIndentDetailsListJSON
            }
    );

    function onLoadIndentDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            indentDetailsGrid = null;
        } else {
            indentDetailsGrid = data;
        }
        return indentDetailsGrid;
    }

    function deleteIndentDetails(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var indentDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'procIndentDetails', action: 'delete')}?id=" + indentDetailsId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent details') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected indent details?')) {
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
            resetFormIndentDetails();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            indentDetailsGrid.total = parseInt(indentDetailsGrid.total) - 1;
            removeEntityFromGridRows(indentDetailsGrid, selectedRow);
            resetFormIndentDetails();

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
            showInfo('No indent details found');
        }
    }

    function editIndentDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'indent details') == false) {
            return;
        }
        resetFormIndentDetails();
        showLoadingSpinner(true);
        var indentDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'procIndentDetails', action: 'select')}?id=" + indentDetailsId,
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
            populateIndentDetails(data);
        }
    }

    function populateIndentDetails(data) {
        resetFormIndentDetails();
        var entity = data.indentDetailsObj;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        quantity.value(entity.quantity);
        var trRate = parseFloat(entity.rate) > 0 ? entity.rate : 0.00;
        var total = (trRate * entity.quantity);
        rate.value(trRate);

        $('#total').text(total.toFixed(2));
        $('#itemDescription').val(entity.itemDescription);
        $('#unit').val(entity.unit);
        $('#comments').val(entity.comments);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");

        calculateTotal();

    }

    window.onload = populateIndentDetailsGridOnPageLoad();

    function populateIndentDetailsGridOnPageLoad() {
        <sec:access url="/procIndentDetails/list">
        var indentId = $('#indentId').attr('value');
        var strUrl = "${createLink(controller: 'procIndentDetails', action: 'list')}?indentId=" + indentId;
        $("#flex1").flexOptions({url: strUrl});

        if (indentDetailsGrid) {
            $("#flex1").flexAddData(indentDetailsGrid);
        }
        </sec:access>
    }
    function calculateTotal() {
        var quantity = $('#quantity').val();
        var rate = $('#rate').val();
        var total = quantity * rate;
        if (total > 0) {
            $('#total').text(total.toFixed(2));
        }
        else {
            $('#total').text(0.00);
        }
    }

</script>