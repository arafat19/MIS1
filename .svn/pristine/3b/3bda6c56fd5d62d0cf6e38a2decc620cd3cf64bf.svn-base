<script language="javascript">
    var output =${output ? output : ''};
    var dropDownProdItemType, dropDownMaterial, rawMaterialList, finishedMaterialList, productionLineItemId, numericOverHeadCost;
    var productionDetailsListModel = false;
    var rawMaterialId, finishedProductId;

    $(document).ready(function () {
        onLoadProductionDetails();
    });

    function onLoadProductionDetails() {
        initializeForm($("#productionDetailsForm"), onSubmitProductionDetails);
        dropDownMaterial = initKendoDropdown($('#materialId'), null, null, null);

        $('#overheadCost').kendoNumericTextBox({
            decimals: 0,
            min: 0,
            format: "#.##"
        });
        numericOverHeadCost = $('#overheadCost').data("kendoNumericTextBox");

        if (output.isError) {
            showError(output.message);
        } else {
            productionDetailsListModel = output.productionDetailsList;
            rawMaterialList = output.rawMaterialList;
            finishedMaterialList = output.finishedMaterialList;
            $('#productionLineItemName').text(output.productionLineItemInfo.productionLineItemname);
            $('#productionLineItemId').val(output.productionLineItemInfo.productionLineItemid);
            rawMaterialId = $('#rawMaterialId').val();
            finishedProductId = $('#finishedProductId').val();;
        }

        // update page title
        $(document).attr('title', "MIS - Create Production Details");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invProductionLineItem/show");
    }

    function executePreCondition() {
        if (!validateForm($("#productionDetailsForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitProductionDetails() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'invProductionDetails', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'invProductionDetails', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#productionDetailsForm").serialize(),
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
                var newEntry = result.productionDetails;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(productionDetailsListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (productionDetailsListModel.rows.length > 0) {
                        firstSerial = productionDetailsListModel.rows[0].cell[0];
                        regenerateSerial($(productionDetailsListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    productionDetailsListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        productionDetailsListModel.rows.pop();
                    }

                    productionDetailsListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(productionDetailsListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(productionDetailsListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(productionDetailsListModel);
                }

                resetForCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForCreateAgain() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#id').val('');
        $('#version').val('');
        numericOverHeadCost.value('');
        dropDownMaterial.value('');
    }

    function resetForm() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#id').val('');
        $('#version').val('');

        dropDownMaterial.setDataSource(getKendoEmptyDataSource(dropDownMaterial, null));
        $('#trOverheadCost').hide();

        clearForm($("#productionDetailsForm"), dropDownProdItemType);
        $('#productionLineItemId').val(productionLineItemId);
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Production Item Type", name: "productionItemTypeId", width: 150, sortable: false, align: "left"},
                    {display: "Material", name: "materialId", width: 300, sortable: false, align: "left"},
                    {display: "Overhead Cost", name: "overheadCost", width: 100, sortable: false, align: "right"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/invProductionDetails/select,/invProductionDetails/update">
                    {name: 'Edit', bclass: 'edit', onpress: editProductionDetails},
                    </app:ifAllUrl>
                    <sec:access url="/invProductionDetails/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteProductionDetails},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ], searchitems: [
                {display: "Material", name: "material.name", width: 180, sortable: true, align: "left"}
            ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Production Line Item Details',
                Rp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight(5) - 50,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                preProcess: onLoadProductionDetailsListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadProductionDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            productionDetailsListModel = null;
        } else {
            productionDetailsListModel = data;
        }
        return data;
    }

    function deleteProductionDetails(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true); // Spinner Show on AJAX Call
        var productionDetailsId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invProductionDetails', action: 'delete')}?id=" + productionDetailsId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production details') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Production Details?')) {
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
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            productionDetailsListModel.total = parseInt(productionDetailsListModel.total) - 1;
            removeEntityFromGridRows(productionDetailsListModel, selectedRow);
            dropDownMaterial.setDataSource(getKendoEmptyDataSource());
            resetForm();
        } else {
            showError(data.message);
        }
    }

    function editProductionDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production details') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var productionDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invProductionDetails', action: 'select')}?id=" + productionDetailsId,
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
            showProductionDetails(data);
        }
    }

    function showProductionDetails(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#productionLineItemId').val(entity.productionLineItemId);
        dropDownProdItemType.value(entity.productionItemTypeId);
        dropDownMaterial.setDataSource(data.materialList);
        dropDownMaterial.value(entity.materialId);
        numericOverHeadCost.value(entity.overheadCost);

        if (entity.productionItemTypeId == finishedProductId) {
            $('#trOverheadCost').show();
        } else {
            $('#trOverheadCost').hide();
        }
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    %{--<sec:access url="/invProductionDetails/list">--}%

    productionLineItemId = $('#productionLineItemId').val();
    var strUrl = "${createLink(controller: 'invProductionDetails', action: 'list')}?productionLineItemId=" + productionLineItemId;
    $("#flex1").flexOptions({url: strUrl});

    if (productionDetailsListModel) {
        $("#flex1").flexAddData(productionDetailsListModel);
    }

    // To populate Material List
    function updateMaterialList() {
        $('#trOverheadCost').hide();
        dropDownMaterial.setDataSource(getKendoEmptyDataSource(dropDownMaterial, null));
        dropDownMaterial.value('');
        var productionItemTypeId = dropDownProdItemType.value();
        if (productionItemTypeId == '') {
            dropDownMaterial.setDataSource(getKendoEmptyDataSource(dropDownMaterial, null));
            dropDownMaterial.value('');
            return false;
        }

        if (productionItemTypeId == rawMaterialId) {//ProductionItemType--> Raw material ID
            dropDownMaterial.setDataSource(rawMaterialList);
        } else if (productionItemTypeId == finishedProductId) {//ProductionItemType--> Finished material ID
            dropDownMaterial.setDataSource(finishedMaterialList);
            $('#trOverheadCost').show();
        }
        return true;
    }

</script>
