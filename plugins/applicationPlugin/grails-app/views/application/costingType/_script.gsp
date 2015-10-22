<script language="javascript" type="text/javascript">

    var output = false;
    var costingTypeListModel = false;

    $(document).ready(function () {
        onLoadCostingTypePage();
    });

    function onLoadCostingTypePage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#costingTypeForm"), onSubmitCostingTypeForm);

        initGrid();
        output =${output ? output : ''};
        if (output.isError) {
            showError(data.message);
        } else {
            costingTypeListModel = output.gridObject;
        }

        populateFlex1();

        // update page title
        $(document).attr('title', "MIS - Create Costing Type");
        loadNumberedMenu(MENU_ID_APPLICATION, "#costingType/show");
    }

    function executePreCondition() {
        if (!validateForm($("#costingTypeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitCostingTypeForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'costingType', action: 'create')}";
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
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(costingTypeListModel.total);
                    var firstSerial = 1;

                    if (costingTypeListModel.rows.length > 0) {
                        firstSerial = costingTypeListModel.rows[0].cell[0];
                        regenerateSerial($(costingTypeListModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    costingTypeListModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        costingTypeListModel.rows.pop();
                    }

                    costingTypeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(costingTypeListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(costingTypeListModel, newEntry, 0);
                    $("#flex1").flexAddData(costingTypeListModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }


    function resetForm() {
        clearForm($("#costingTypeForm"), $("#name"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
//                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 150, sortable: false, align: "left"},
                        {display: "Description", name: "description", width: 300, sortable: false, align: "center"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/costingType/select,/costingType/update">
                        {name: 'Edit', bclass: 'edit', onpress: editCostingType},
                        </app:ifAllUrl>
                        <sec:access url="/costingType/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteCostingType},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],


                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Costing Type List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateCostingTypeGrid
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function customPopulateCostingTypeGrid(data) {
        if (data.isError) {
            showError(data.message);
            costingTypeListModel = getEmptyGridModel();
        } else {
            costingTypeListModel = data;
        }
        $("#flex1").flexAddData(costingTypeListModel);
        return false;
    }

    function editCostingType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'costingType') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);

        var costingTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'costingType', action: 'select')}?id=" + costingTypeId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select a costing type to edit the details");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showCountry(data);
        }
    }

    function showCountry(data) {
        var entity = data.entity;
        $("#id").val(entity.id);
        $('#version').val(data.version);
        $("#name").val(entity.name);
        $("#description").val(entity.description);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteCostingType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var costingTypeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'costingType', action:'delete')}?id=" + costingTypeId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });

    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'costingType') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected costing type?')) {
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
            costingTypeListModel.total = parseInt(costingTypeListModel.total) - 1;
            removeEntityFromGridRows(costingTypeListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }


    function populateFlex1() {
        <sec:access url="/costingType/list">
        var strUrl = "${createLink(controller:'costingType', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (costingTypeListModel) {
            $("#flex1").flexAddData(costingTypeListModel);
        }
        </sec:access>
    }


</script>