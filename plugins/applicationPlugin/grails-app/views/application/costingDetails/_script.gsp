<script language="javascript">
    var output =${output ? output : ''};
    var costingDetailsListModel = false;
    var dropDownCostingType;

    $(document).ready(function () {
        onLoadCostingDetailsPage();
    });

    function onLoadCostingDetailsPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#costingDetailsForm"), onSubmitCostingDetails);

        if (output.isError) {
            showError(output.message);
        } else {
            costingDetailsListModel = output.gridObject;
        }

        // update page title
        $(document).attr('title', "MIS - Add Costing Details");
        loadNumberedMenu(MENU_ID_APPLICATION, "#costingDetails/show");

    }
    function executePreCondition() {
        if (!validateForm($("#costingDetailsForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitCostingDetails() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'costingDetails', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'costingDetails', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#costingDetailsForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);
                showLoadingSpinner(false)
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
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(costingDetailsListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (costingDetailsListModel.rows.length > 0) {
                        firstSerial = costingDetailsListModel.rows[0].cell[0];
                        regenerateSerial($(costingDetailsListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    costingDetailsListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        costingDetailsListModel.rows.pop();
                    }

                    costingDetailsListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(costingDetailsListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(costingDetailsListModel, newEntry, 0);
                    $("#flex1").flexAddData(costingDetailsListModel);
                }

                clearCostingDetailsForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function clearCostingDetailsForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#costingDetailsForm"), dropDownCostingType);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }


    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    /*{display: "ID", name: "id", width: 40, sortable: false, align: "right", hide: true},*/
                    {display: "Costing Type", name: "costingTypeId", width: 180, sortable: true, align: "left"},
                    {display: "Description", name: "description", width: 250, sortable: true, align: "left"},
                    {display: "Costing Amount", name: "costingAmount", width: 250, sortable: true, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/costingDetails/select,/costingDetails/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectCostingDetails},
                    </app:ifAllUrl>
                    <sec:access url="/costingDetails/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteCostingDetails},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],

                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Costing Details',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                preProcess: onLoadCostingDetailsListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadCostingDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            costingDetailsListModel = null;
        } else {
            costingDetailsListModel = data;
        }
        return data;
    }


    function deleteCostingDetails(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true);

        var costingDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'costingDetails', action: 'delete')}?id=" + costingDetailsId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Costing Details') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Costing Details?')) {
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
            clearCostingDetailsForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            costingDetailsListModel.total = parseInt(costingDetailsListModel.total) - 1;
            removeEntityFromGridRows(costingDetailsListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }


    function selectCostingDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Costing Details') == false) {
            return;
        }

        clearCostingDetailsForm();
        showLoadingSpinner(true);
        var costingDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'costingDetails', action: 'select')}?id="
                    + costingDetailsId,
            success: executePostConditionForSelect,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForSelect(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showCostingType(data);
        }
    }

    function showCostingType(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownCostingType.value(entity.costingTypeId);
        $('#description').val(entity.description);
        $('#costingAmount').val(entity.costingAmount);
        $('#costingDate').val(data.costingDate);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }


    <sec:access url="/costingDetails/list">
    var strUrl = "${createLink(controller: 'costingDetails', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (costingDetailsListModel) {
        $("#flex1").flexAddData(costingDetailsListModel);
    }
    </sec:access>

</script>
