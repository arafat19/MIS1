<script language="javascript">
    var output =${output ? output : ''};
    var costingTypeListModel = false;

    $(document).ready(function () {
        onLoadCostingTypePage();
    });

    function onLoadCostingTypePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#costingTypeForm"), onSubmitCostingType);

        if (output.isError) {
            showError(output.message);
        } else {
            costingTypeListModel = output.gridObject;
        }

        // update page title
        $(document).attr('title', "MIS - Add Costing Type");
        loadNumberedMenu(MENU_ID_APPLICATION, "#costingType/show");

    }
    function executePreCondition() {
        if (!validateForm($("#costingTypeForm"))) {   // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitCostingType() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'costingType', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'costingType', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#costingTypeForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
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
                    var previousTotal = parseInt(costingTypeListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (costingTypeListModel.rows.length > 0) {
                        firstSerial = costingTypeListModel.rows[0].cell[0];
                        regenerateSerial($(costingTypeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    costingTypeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        costingTypeListModel.rows.pop();
                    }

                    costingTypeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(costingTypeListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(costingTypeListModel, newEntry, 0);
                    $("#flex1").flexAddData(costingTypeListModel);
                }

                clearCostingTypeForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function clearCostingTypeForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#costingTypeForm"), $('#name'));
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
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Description", name: "description", width: 250, sortable: true, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/costingType/select,/costingType/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectCostingType},
                    </app:ifAllUrl>
                    <sec:access url="/costingType/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteCostingType},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Costing Types',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                preProcess: onLoadCostingTypeListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadCostingTypeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            costingTypeListModel = null;
        } else {
            costingTypeListModel = data;
        }
        return data;
    }


    function deleteCostingType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true);

        var costingTypeId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'costingType', action: 'delete')}?id=" + costingTypeId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Costing Type') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Costing Type?')) {
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
            clearCostingTypeForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            costingTypeListModel.total = parseInt(costingTypeListModel.total) - 1;
            removeEntityFromGridRows(costingTypeListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }


    function selectCostingType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'Costing Type') == false) {
            return;
        }

        clearCostingTypeForm();
        showLoadingSpinner(true);
        var costingTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'costingType', action: 'select')}?id="
                    + costingTypeId,
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
        $('#name').val(entity.name);
        $('#description').val(entity.description);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }


    <sec:access url="/costingType/list">
    var strUrl = "${createLink(controller: 'costingType', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (costingTypeListModel) {
        $("#flex1").flexAddData(costingTypeListModel);
    }
    </sec:access>

</script>
