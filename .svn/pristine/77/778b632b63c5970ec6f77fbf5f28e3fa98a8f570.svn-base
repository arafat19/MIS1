<script type="text/javascript">
    var hidProjectId, fromDate, toDate, inventoryTypeId, inventoryId, itemTypeId;
    var dropDownProject, dropDownInventoryType, dropDownInventory, dropDownItemType;

    jQuery(function ($) {
        $('#printPdfBtn').click(function () {
            downloadInventoryStatusWithValueReport();
        });
        $('#printCsvBtn').click(function () {
            downloadInventoryStatusWithValueCsvReport();
        });
        onLoadInventoryStatusWithValueReportPage();
    });

    function onLoadInventoryStatusWithValueReportPage() {
        initializeForm($("#invStatusWithValueForm"), loadGridForInventoryStatusWithValueReport);

        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, getKendoEmptyDataSource(dropDownInventory, 'ALL'));

        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Inventory Status With Value Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInventoryStatusWithValue");
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
        if(dropDownInventoryType.value=='') dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, 'ALL'));
        var projectId = dropDownProject.value() ;
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

    function loadGridForInventoryStatusWithValueReport() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        if (!validateForm($("#invStatusWithValueForm"))) {
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
            url: "${createLink(controller: 'invReport', action: 'listInventoryStatusWithValue')}?projectId=" + projectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId,
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
            var url = "${createLink(controller: 'invReport', action: 'listInventoryStatusWithValue')}?projectId=" + hidProjectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId;
            $("#flex1").flexOptions({url: url});
            $("#flex1").flexAddData(data.gridOutput);
            $('.download_icon_set').show();
        }
    }

    function downloadInventoryStatusWithValueReport() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'invReport', action: 'downloadInventoryStatusWithValue')}?projectId=" + hidProjectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadInventoryStatusWithValueCsvReport() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the report in csv format now?')) {
            var url = "${createLink(controller: 'invReport', action: 'downloadInventoryStatusWithValueCsv')}?projectId=" + hidProjectId + '&fromDate=' + fromDate + '&toDate=' + toDate + '&inventoryTypeId=' + inventoryTypeId + '&inventoryId=' + inventoryId + '&itemTypeId=' + itemTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 25, sortable: false, hide: true, align: "right"},
                    {display: "Item", name: "name", width: 220, sortable: false, align: "left"},
                    {display: "Previous Balance", name: "previous_amount", width: 150, sortable: false, align: "left"},
                    {display: "Received(Supplier) Amount", name: "received_amount", width: 150, sortable: false, align: "right"},
                    {display: "Production Amount", name: "total_production_amount", width: 150, sortable: false, align: "right"},
                    {display: "Consumed(Budget) Amount", name: "total_budget_consumption_amount", width: 150, sortable: false, align: "right"},
                    {display: "Consumed(Prod) Amount", name: "total_prod_consumption_amount", width: 150, sortable: false, align: "right"},
                    {display: "Balance Amount", name: "total_stock_amount", width: 140, sortable: false, align: "right"}
                ],
                sortname: "item.name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'Inventory Status With Value List',
                useRp: true,
                rp: 20,
                rpOptions: [20, 25, 30],
                showTableToggleBtn: false,
                height: getGridHeight() + 20,
                customPopulate: populateGridForInvStatusWithValueReport,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                    checkGrid();
                }
            }
    );

    function populateGridForInvStatusWithValueReport(data) {
        executePostConditionForLoadGrid(data);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Inventory Status With Value found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>