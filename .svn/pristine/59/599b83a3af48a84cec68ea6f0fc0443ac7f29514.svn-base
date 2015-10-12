<script language="javascript" type="text/javascript">
    var hidProjectId, fromDate, toDate, inventoryTypeId, inventoryId, itemTypeId;
    var dropDownProject, dropDownInventoryType, dropDownInventory, dropDownItemType;

    $(document).ready(function () {
        onLoadProjectInventoryWithQuantityPage();
        $('#printPdfBtn').click(function () {
            downloadProjectInventoryWithQuantityReport();
        });
        $('#printCsvBtn').click(function () {
            downloadProjectInventoryWithQuantityCsvReport();
        });
    });

    function onLoadProjectInventoryWithQuantityPage() {
        initializeForm($("#quantitySearchForm"), loadGridForProjectInventoryWithQuantity);

        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, getKendoEmptyDataSource(dropDownInventory, 'ALL'));
        initGrid();

        //update page title
        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Inventory Status With Quantity Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInventoryStatusWithQuantity");
    }

    function updateFromDateAndInventoryType() {
        if (dropDownProject.value() == '') {
            $("#fromDate").val("${new Date().format('dd/MM/yyyy')}");
            return false;
        }
        var createdOn = dropDownProject.dataItem().createdon;
        $('#fromDate').val(createdOn);

        dropDownInventoryType.value('');
        dropDownInventory.value('');
        dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, 'ALL'));
        dropDownInventory.refresh();
    }

    // To populate Inventory List
    function updateInventoryList() {
        if (dropDownInventoryType.value == '') dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, 'ALL'));
        var projectId = dropDownProject.value();
        var inventoryTypeId = dropDownInventoryType.value();

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryByTypeAndProject')}?inventoryTypeId=" + inventoryTypeId + "&projectId=" + projectId,
            success: function (data) {
                updateInventoryListDropDown(data);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateInventoryListDropDown(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownInventory.setDataSource(data.inventoryList);
        dropDownInventory.value('');
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, hide: true, align: "right"},
                        {display: "Item", name: "name", width: 220, sortable: false, align: "left"},
                        {display: "Previous Quantity", name: "previous_quantity", width: 120, sortable: false, align: "left"},
                        {display: "Received(Supplier)", name: "received_quantity", width: 120, sortable: false, align: "right"},
                        {display: "Production", name: "total_production_quantity", width: 120, sortable: false, align: "right"},
                        {display: "Consumed(Budget)", name: "total_budget_consumption_quantity", width: 120, sortable: false, align: "right"},
                        {display: "Consumed(Prod)", name: "total_prod_consumption_quantity", width: 120, sortable: false, align: "right"},
                        {display: "Balance", name: "total_stock_quantity", width: 120, sortable: false, align: "right"}
                    ],
                    sortname: "item.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Search Inventory Status With Quantity List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() + 20,
                    customPopulate: populateGridForInventoryWithQuantityReport,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );
    }

    function populateGridForInventoryWithQuantityReport(data) {
        executePostConditionForLoadGrid(data);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Stock found');
        }
    }

    function loadGridForProjectInventoryWithQuantity() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        var projectId = dropDownProject.value();
        fromDate = $('#fromDate').val();
        toDate = $('#toDate').val();
        inventoryTypeId = dropDownInventoryType.value();
        inventoryId = dropDownInventory.value();
        itemTypeId = dropDownItemType.value();

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        doGridEmpty();
        hidProjectId = projectId;
        $.ajax({
            url: "${createLink(controller: 'invReport', action: 'listInventoryStatusWithQuantity')}?projectId=" + projectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId,
            success: executePostConditionForLoadGrid,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForLoadGrid(data) {
        if (data.isError == true) {
            showError(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
        } else {
            var url = "${createLink(controller: 'invReport', action: 'listInventoryStatusWithQuantity')}?projectId=" + hidProjectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId;
            $("#flex1").flexOptions({url: url});
            $("#flex1").flexAddData(data.gridOutput);
            $('.download_icon_set').show();
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function downloadProjectInventoryWithQuantityReport() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'invReport', action: 'downloadInventoryStatusWithQuantity')}?projectId=" + hidProjectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadProjectInventoryWithQuantityCsvReport() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the report in csv format now?')) {
            var url = "${createLink(controller: 'invReport', action: 'downloadInventoryStatusWithQuantityCsv')}?projectId=" + hidProjectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>