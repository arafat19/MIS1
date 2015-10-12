<script type="text/javascript">
    var gridModelProduction, gridModelRawMaterial, gridModelFinishedProduct, dropDownInventoryType,
            dropDownInventory, dropDownProdLineItem, dropDownRawMaterials, rawMaterialQuantity,
            dropDownFinishedProducts, finishedProdQuantity, itemToStore;

    $(document).ready(function () {
        onLoadProduction();
    });

    function onLoadProduction() {
        initializeForm($("#frmProduction"), onSubmitProduction);
        initializeForm($("#frmRawMaterial"), onSubmitRawMaterials);
        initializeForm($("#frmFinishedMaterial"), onSubmitFinishedMaterials);

        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, null);
        dropDownRawMaterials = initKendoDropdown($('#rawMaterialId'), "name_quantity", null, null);
        dropDownRawMaterials.setDataSource(getKendoEmptyDataSource(dropDownRawMaterials, null));
        dropDownFinishedProducts = initKendoDropdown($('#finishedMaterialId'), null, null, null);

        $('#rawMaterialQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"
        });
        rawMaterialQuantity = $('#rawMaterialQuantity').data("kendoNumericTextBox");

        $('#finishedMaterialQuantity').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##"
        });
        finishedProdQuantity = $('#finishedMaterialQuantity').data("kendoNumericTextBox");

        initFlexProduction();   // init grid for production ( main grid )
        initFlexRawMaterials();  // init grid for raw materials ( in raw material tab)
        initFlexFinishedProducts();  // init grid for finished products ( in finished product tab)

        // populate form objects
        populateOnLoadObjects();


        // update page title
        $(document).attr('title', "MIS - Create Inventory Production");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showInvProductionWithConsumption");
    }

    function populateOnLoadObjects() {
        gridModelProduction = null;
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
            return;
        }
        gridModelProduction = output.objGridInvProduction;
        setUrlProductionGrid();

        // initialize Dr Cr grid models
        gridModelRawMaterial = getEmptyModelForRawFinished();
        gridModelFinishedProduct = getEmptyModelForRawFinished();
    }

    function getEmptyModelForRawFinished() {
        var emptyModel = new Object();
        emptyModel.page = 1;
        emptyModel.total = 0;
        emptyModel.rows = new Array();
        return emptyModel;
    }

    function customPopulateProductionGrid(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            gridModelProduction = data;
        }
        $("#flex1").flexAddData(gridModelProduction);
    }

    function editProduction(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production') == false) {
            return;
        }
        showLoadingSpinner(true);
        var consumptionId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller: 'invInventoryTransaction', action: 'selectInvProductionWithConsumption')}?id=" + consumptionId;
        $.ajax({
            url: url,
            success: executePostConForEditProduction,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForEditProduction(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showProduction(data);
        }
    }

    function showProduction(data) {
        clearFormProduction();
        var entityConsumption = data.entityConsumption;
        var entityProduction = data.entityProduction;
        $('#idCon').val(entityConsumption.id);
        $('#idProd').val(entityProduction.id);
        $('#versionCon').val(data.versionCon);
        $('#versionProd').val(data.versionProd);

        dropDownInventoryType.value(entityConsumption.inventoryTypeId);
        dropDownInventory.setDataSource(data.lstInventories);
        dropDownInventory.value(entityConsumption.inventoryId);
        dropDownProdLineItem.value(entityConsumption.invProductionLineItemId);
        $("#transactionDate").val(data.transactionDate);
        $('#comments').val(entityConsumption.comments);
        dropDownRawMaterials.setDataSource(data.lstRawMaterials);
        dropDownFinishedProducts.setDataSource(data.lstFinishedProducts);

        gridModelRawMaterial = data.gridModelConsumption;
        gridModelFinishedProduct = data.gridModelProduction;

        $('#flexRawMaterial').flexAddData(gridModelRawMaterial);
        $('#flexFinishedMaterial').flexAddData(gridModelFinishedProduct);
        dropDownProdLineItem.enable(false);
        dropDownInventoryType.enable(false);
        dropDownInventory.enable(false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");

    }

    function onSubmitProduction() {
        if (executePreConForSubmitProduction() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var formData = jQuery('#frmProduction').serializeArray();
        formData.push({name: 'gridModelRawMaterial', value: JSON.stringify(gridModelRawMaterial)});
        formData.push({name: 'gridModelFinishedProduct', value: JSON.stringify(gridModelFinishedProduct)});
        var actionUrl = null;
        if ($('#idCon').val().isEmpty()) {
            actionUrl = "${createLink(controller:'invInventoryTransaction', action: 'createInvProductionWithConsumption')}";
        } else {
            actionUrl = "${createLink(controller:'invInventoryTransaction', action: 'updateInvProductionWithConsumption')}";
            //also push values of read-only fields
            formData.push({name: 'productionLineItemId', value: $('#productionLineItemId').val()});
            formData.push({name: 'inventoryId', value: $('#inventoryId').val()});
        }

        jQuery.ajax({
            type: 'post',
            data: formData,
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConForSubmitProduction(data);
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

    function clearFormProduction() {
        $('#idCon').val('');
        $('#idProd').val('');
        $('#versionCon').val('');
        $('#versionProd').val('');
        $('#comments').val('');

        dropDownInventory.setDataSource(getKendoEmptyDataSource(null, null));
        resetRawMaterialPanel();
        resetFinishedProductPanel();
        clearForm($('#frmProduction'), dropDownInventoryType);
        $('#productionTabs a[href="#fragment-1"]').tab('show');
        $("#transactionDate").val('');

        dropDownProdLineItem.enable(true);
        dropDownInventoryType.enable(true);
        dropDownInventory.enable(true);
        dropDownRawMaterials.enable(true);
        dropDownFinishedProducts.enable(true);
        setButtonDisabled($('#create'), false);
        $('.edit').show();
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
    }

    function executePreConForDeleteDebit(ids) {
        var delCount = ids.length;
        if (delCount == 0) {
            showError("Please select a debit");
            return false;
        }

        if (!confirm('Are you sure you want to delete the selected debit?')) {
            return false;
        }
        return true;
    }

    function editRawMaterials(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flex1'), 'raw material') == false) {
            return;
        }
        var materialId = $(ids[0]).find('td').eq(0).find('div').text();
        var quantity = $(ids[0]).find('td').eq(3).find('div').text();
        var rawDetailsId = $(ids[0]).find('td').eq(4).find('div').text();

        $('#rawDetailsId').val(rawDetailsId);        // populate hidden field by tr details id
        dropDownRawMaterials.value(materialId);

        var currentQuantity = dropDownRawMaterials.dataItem().quantity;
        var remainingQuantity = parseFloat(currentQuantity) + parseFloat(quantity);
        // update dataSource with remaining quantity
        dropDownRawMaterials.dataItem().quantity = remainingQuantity;
        dropDownRawMaterials.dataItem().name_quantity = dropDownRawMaterials.dataItem().name + '(' + remainingQuantity.toFixed(4) + ')';
        dropDownRawMaterials.refresh();

        dropDownRawMaterials.enable(false);
        $('#lblUnitRaw').text(dropDownRawMaterials.dataItem().unit);
        setButtonDisabled($('#create'), true);
        $('.edit').hide();
        rawMaterialQuantity.value(quantity);

        $('.trSelected', $('#flexRawMaterial')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelRawMaterial.total; i++) {
            if (parseFloat(materialId) == parseFloat(gridModelRawMaterial.rows[i].cell[0])) {
                gridModelRawMaterial.rows.splice(i, 1);
                gridModelRawMaterial.total = gridModelRawMaterial.total - 1;
                return;
            }
        }
    }

    function editFinishedProducts(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flex1'), 'finished product') == false) {
            return;
        }
        var materialId = $(ids[0]).find('td').eq(0).find('div').text();
        var quantity = $(ids[0]).find('td').eq(3).find('div').text();
        var finishedDetailsId = $(ids[0]).find('td').eq(4).find('div').text();
        var overheadCost = $(ids[0]).find('td').eq(5).find('div').text();

        $('#finishedDetailsId').val(finishedDetailsId);
        dropDownFinishedProducts.value(materialId);
        dropDownFinishedProducts.enable(false);
        setButtonDisabled($('#create'), true);
        $('.edit').hide();
        finishedProdQuantity.value(quantity);
        $('#lblOverheadCost').text(overheadCost);
        $('#lblUnitFinished').text(dropDownFinishedProducts.dataItem().unit);

        $('.trSelected', $('#flexFinishedMaterial')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelFinishedProduct.total; i++) {
            if (parseFloat(materialId) == parseFloat(gridModelFinishedProduct.rows[i].cell[0])) {
                gridModelFinishedProduct.rows.splice(i, 1);
                gridModelFinishedProduct.total = gridModelFinishedProduct.total - 1;
                return;
            }
        }
    }

    function executePreConForDeleteCredit() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'credit') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected credit?')) {
            return false;
        }
        return true;
    }

    function deleteCredit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executePreConForDeleteCredit() == false) {
            return;
        }
        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        $('.trSelected', $('#flexCredit')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelFinishedProduct.total; i++) {
            if (parseFloat(coaId) == parseFloat(gridModelFinishedProduct.rows[i].cell[0])) {
                gridModelFinishedProduct.rows.splice(i, 1);
                gridModelFinishedProduct.total = gridModelFinishedProduct.total - 1;
                return;
            }
        }
    }


    function reloadProductionGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function setUrlProductionGrid() {
        var strUrl = "${createLink(controller:'invInventoryTransaction', action: 'listInvProductionWithConsumption')}";
        $("#flex1").flexOptions({url: strUrl});

        if (gridModelProduction) {
            $('#flex1').flexAddData(gridModelProduction);
        }
    }

    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ VALIDATION SCRIPT

    function executePreConForSubmitProduction() {
        if (!validateForm($("#frmProduction"))) {
            $('#productionTabs a[href="#fragment-1"]').tab('show') // Select tab by name
            return false;
        }

        if (!checkCustomDate($("#transactionDate"), "Inventory-Production")) {
            $('#productionTabs a[href="#fragment-1"]').tab('show') // Select tab by name
            $('#transactionDate').focus();
            return false;
        }

        if (gridModelRawMaterial.total == 0) {
            $('#productionTabs a[href="#fragment-2"]').tab('show') // Select tab by name

            if (!validateForm($("#frmRawMaterial"))) {
                return false;
            }
            return false;
        }
        if (gridModelFinishedProduct.total == 0) {
            $('#productionTabs a[href="#fragment-3"]').tab('show') // Select tab by name

            if (!validateForm($("#frmFinishedMaterial"))) {
                return false;
            }
            return false;
        }
        return true;
    }

    function executePreConForSubmitRaw() {
        if (!validateForm($("#frmRawMaterial"))) {
            return false;
        }

        var rawMaterialId = dropDownRawMaterials.value();
        var strQuantityRaw = $('#rawMaterialQuantity').val();
        var availableQuantity = dropDownRawMaterials.dataItem().quantity;

        if (parseFloat(strQuantityRaw) > parseFloat(availableQuantity)) {
            showError('Given quantity is not available in Inventory');
            return false;
        }
        for (var i = 0; i < gridModelRawMaterial.total; i++) {
            if (rawMaterialId == parseFloat(gridModelRawMaterial.rows[i].cell[0])) {
                showError('Material ' + gridModelRawMaterial.rows[i].cell[1] + ' already exists');
                return false;
            }
        }
        return true;
    }
    function onSubmitRawMaterials() {
        if (executePreConForSubmitRaw() == false) {
            return false;
        }
        setButtonDisabled($('#addRaw'), true);
        showLoadingSpinner(true);

        var materialId = dropDownRawMaterials.value();
        // add data into grid;
        var rows = new Array();
        rows.push(materialId);

        var rawMaterial = dropDownRawMaterials.text();
        var lastIndex = rawMaterial.lastIndexOf('(');
        rawMaterial = rawMaterial.substring(0, lastIndex);
        rows.push(rawMaterial);

        var quantity = rawMaterialQuantity.value();
        var unit = dropDownRawMaterials.dataItem().unit;
        rows.push(quantity + ' ' + unit);
        rows.push(quantity);
        var rawDetailsId = $('#rawDetailsId').val();
        rows.push(rawDetailsId);     // transaction details id (-1 for new)

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = materialId;

        gridModelRawMaterial.total = gridModelRawMaterial.total + 1;
        gridModelRawMaterial.rows.splice(gridModelRawMaterial.total - 1, 0, gridObj);
        $('#flexRawMaterial').flexAddData(gridModelRawMaterial);
        var currentQuantity = dropDownRawMaterials.dataItem().quantity;
        var remainingQuantity = parseFloat(currentQuantity) - parseFloat(quantity);
        // update dataSource with remaining quantity
        dropDownRawMaterials.dataItem().quantity = remainingQuantity;
        dropDownRawMaterials.dataItem().name_quantity = dropDownRawMaterials.dataItem().name + '(' + remainingQuantity.toFixed(4) + ')';
        dropDownRawMaterials.refresh();

        showLoadingSpinner(false);
        setButtonDisabled($('#addRaw'), false);
        executePostForSubmitRaw();     // clean form , span text*/
        return false;
    }
    function executePostForSubmitRaw() {
        clearForm($("#frmRawMaterial"), dropDownRawMaterials);
        $('#rawDetailsId').val(-1);
        dropDownRawMaterials.enable(true);
        setButtonDisabled($('#create'), false);
        $('.edit').show();
        $('#lblUnitRaw').text('');
    }

    function executePreConForSubmitFinished() {
        trimFormValues($("#frmFinishedMaterial"));

        var finishedMaterialId = dropDownFinishedProducts.value();

        for (var i = 0; i < gridModelFinishedProduct.total; i++) {
            if (finishedMaterialId == parseFloat(gridModelFinishedProduct.rows[i].cell[0])) {
                showError('Material ' + gridModelFinishedProduct.rows[i].cell[1] + ' already exists');
                return false;
            }
        }
    }
    function onSubmitFinishedMaterials() {
        setButtonDisabled($('#addRaw'), true);
        showLoadingSpinner(true);
        if (executePreConForSubmitFinished() == false) {
            setButtonDisabled($('#addRaw'), false);
            showLoadingSpinner(false);
            return false;
        }

        // add data into grid;
        var rows = new Array();
        rows.push(dropDownFinishedProducts.value());
        rows.push(dropDownFinishedProducts.text());
        var quantity = $('#finishedMaterialQuantity').val();
        var unit = dropDownFinishedProducts.dataItem().unit;
        rows.push(quantity + ' ' + unit);
        rows.push(quantity);
        var finishedDetailsId = $('#finishedDetailsId').val();
        rows.push(finishedDetailsId);     // transaction details id (-1 for new)

        var overheadCost = $('#lblOverheadCost').text();
        rows.push(overheadCost);

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = $('#finishedMaterialId').val();

        gridModelFinishedProduct.total = gridModelFinishedProduct.total + 1;
        gridModelFinishedProduct.rows.splice(gridModelFinishedProduct.total - 1, 0, gridObj);

        $('#flexFinishedMaterial').flexAddData(gridModelFinishedProduct);
        showLoadingSpinner(false);
        setButtonDisabled($('#addRaw'), false);
        executePostForSubmitFinished();
        return false;
    }
    function executePostForSubmitFinished() {
        clearForm($('#frmFinishedMaterial'), dropDownFinishedProducts);
        $('#finishedDetailsId').val(-1);
        $('#lblOverheadCost').text('');
        dropDownFinishedProducts.enable(true);
        setButtonDisabled($('#create'), false);
        $('.edit').show();
    }


    function executePostConForSubmitProduction(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.gridObject;
                if ($('#idCon').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(gridModelProduction.total);
                    var firstSerial = 1;

                    if (gridModelProduction.rows.length > 0) {
                        firstSerial = gridModelProduction.rows[0].cell[0];
                        regenerateSerial($(gridModelProduction.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    gridModelProduction.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        gridModelProduction.rows.pop();
                    }

                    gridModelProduction.total = ++previousTotal;
                    $("#flex1").flexAddData(gridModelProduction);

                } else if (newEntry != null) { // updated existing
                    updateListModel(gridModelProduction, newEntry, 0);
                    $("#flex1").flexAddData(gridModelProduction);
                }

                clearFormProduction();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function onChangeInventory() {
        dropDownProdLineItem.value();
        resetRawMaterialPanel();
        resetFinishedProductPanel();
    }


    function clearFormProductionOnChangeLineItem() {
        resetRawMaterialPanel();
        resetFinishedProductPanel();
        $("#transactionDate").val('');
    }

    function onChangeLineItem() {
        clearFormProductionOnChangeLineItem();
        var lineItemId = dropDownProdLineItem.value();
        if (lineItemId == '') {
            return false;
        }
        var inventoryId = dropDownInventory.value();
        if (inventoryId == '') {
            showError('Please select an inventory');
            return false;
        }


        showLoadingSpinner(true);
        var url = "${createLink(controller:'invProductionDetails', action: 'getBothMaterials')}?lineItemId=" + lineItemId + '&inventoryId=' + inventoryId;
        $.ajax({
            url: url,
            success: executePostConForChangeLineItem,
            complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForChangeLineItem(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownRawMaterials.setDataSource(data.lstRawMaterials);
        dropDownFinishedProducts.setDataSource(data.lstFinishedProducts);
        $('#lblOverheadCost').text(''); // populate overhead in span text
    }

    function onChangeRawMaterial() {
        if (dropDownRawMaterials.value() == '') {
            $('#lblUnitRaw').text('');
            return false;
        }
        var unitRaw = dropDownRawMaterials.dataItem().unit;
        $('#lblUnitRaw').text(unitRaw);
        rawMaterialQuantity.focus();
    }

    function onChangeFinishedMaterial() {
        if (dropDownFinishedProducts.value() == '') {
            $('#lblUnitFinished').text('');
            $('#lblOverheadCost').text('');
            return false;
        }
        var unitFinished = dropDownFinishedProducts.dataItem().unit;
        var overheadCost = dropDownFinishedProducts.dataItem().overheadcost;
        $('#lblUnitFinished').text(unitFinished);
        $('#lblOverheadCost').text(overheadCost);
        $('#finishedMaterialQuantity').focus();
    }

    function resetRawMaterialPanel() {
        clearForm($('#frmRawMaterial'), dropDownRawMaterials);
        $('#rawDetailsId').val(-1); // hidden field
        dropDownRawMaterials.setDataSource(getKendoEmptyDataSource(dropDownRawMaterials, null));
        gridModelRawMaterial = getEmptyModelForRawFinished();
        $('#flexRawMaterial').flexAddData(gridModelRawMaterial);
        $('#lblUnitRaw').text('');

    }

    function resetFinishedProductPanel() {
        clearForm($('#frmFinishedMaterial'), dropDownFinishedProducts);
        $('#finishedDetailsId').val(-1);    // hidden field
        dropDownFinishedProducts.setDataSource(getKendoEmptyDataSource(dropDownFinishedProducts, null));
        gridModelFinishedProduct = getEmptyModelForRawFinished();
        $('#flexFinishedMaterial').flexAddData(gridModelFinishedProduct);
        $('#lblUnitFinished').text('');
        $('#lblOverheadCost').text('');
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected production?')) {
            return false;
        }
        return true;
    }
    function deleteInventoryProduction(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var consumptionId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'deleteInvProductionWithConsumption')}?id=" + consumptionId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            clearFormProduction();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            gridModelProduction.total = parseInt(gridModelProduction.total) - 1;
            removeEntityFromGridRows(gridModelProduction, selectedRow);
        } else {
            // show delete error
            showError(data.message);
        }
    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownInventoryType.value();
        if (inventoryTypeId == '') {
            dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, null));
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryIsFactoryByType')}?inventoryTypeId=" + inventoryTypeId,
            success: function (data) {
                updateInventoryListDropDown(data, defaultInventoryTypeId);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateInventoryListDropDown(data, defaultInventoryTypeId) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        if (!defaultInventoryTypeId) defaultInventoryTypeId = '';
        dropDownInventory.setDataSource(data.inventoryList);
        dropDownInventory.value(defaultInventoryTypeId);
    }

    // ******** for Approval of Production with Consumption
    function approveProduction(com, grid) {
        if (executePreConditionForApprove() == false) {
            return;
        }
        showLoadingSpinner(true);
        var transactionId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invInventoryTransaction', action: 'approveInvProdWithConsumption')}?transactionId=" + transactionId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForApprove() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production') == false) {
            return false;
        }
        if (!confirm('Do you want to approve the selected production?')) {
            return false;
        }

        return true;
    }

</script>
