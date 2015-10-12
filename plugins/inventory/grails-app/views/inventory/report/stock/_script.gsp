<script type="text/javascript">
    var dropDownProject, dropDownInventoryType, dropDownInventory, hidInventoryId;

    jQuery(function ($) {
        $('#printPdfBtn').click(function () {
            downloadInventoryStockReport();
        });
        $('#printCsvBtn').click(function () {
            downloadInventoryStockReportCsv();
        });

        onLoadInventoryStockReportPage();
    });

    function onLoadInventoryStockReportPage() {
        initGrid();
        initializeForm($("#stockSearchForm"), loadGridForInventoryStockReport);
        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, getKendoEmptyDataSource(dropDownInventory, 'ALL'));

        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Inventory Stock Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/inventoryStock");
    }

    function updateInventoryType() {
        dropDownInventoryType.value('');
        dropDownInventory.value('');
        dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory, 'ALL'));
        return true;
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

    function loadGridForInventoryStockReport() {
        var inventoryTypeId = dropDownInventoryType.value();
        var inventoryId = dropDownInventory.value();
        var projectId = dropDownProject.value();

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        doGridEmpty();
        $.ajax({
            url: "${createLink(controller: 'invReport', action: 'listInventoryStock')}?inventoryId=" + inventoryId + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId,
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
            var inventoryTypeId = dropDownInventoryType.value();
            var inventoryId = dropDownInventory.value();
            var projectId = dropDownProject.value();
            var url = "${createLink(controller: 'invReport', action: 'listInventoryStock')}?inventoryId=" + inventoryId + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId;
            $("#flex1").flexOptions({url: url});
            $("#flex1").flexAddData(data.gridOutput);
            $('.download_icon_set').show();

            $('#hidProjectId').val(data.projectId);
            $('#hidInventoryId').val(data.inventoryId);
            $('#hidInventoryTypeId').val(data.inventoryTypeId);
        }
    }

    function downloadInventoryStockReport() {

        var inventoryTypeId = $('#hidInventoryTypeId').val();
        var inventoryId = $('#hidInventoryId').val();
        var projectId = $('#hidProjectId').val();

        var confirmMsg = 'Do you want to download the report in pdf format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventoryStock')}?inventoryId=" + inventoryId + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadInventoryStockReportCsv() {

        var inventoryTypeId = $('#hidInventoryTypeId').val();
        var inventoryId = $('#hidInventoryId').val();
        var projectId = $('#hidProjectId').val();

        var confirmMsg = 'Do you want to download the report in csv format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventoryStockCsv')}?inventoryId=" + inventoryId + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right"},
                        {display: "Item", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Quantity", name: "curr_quantity", width: 150, sortable: false, align: "right"}
                    ],
                    sortname: "item.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Inventory Stock Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() + 20,
                    customPopulate: populateGridForInventoryStockReport,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );
    }

    function populateGridForInventoryStockReport(data) {
        executePostConditionForLoadGrid(data);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Stock material found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>