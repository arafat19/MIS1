<script type="text/javascript">
    var dropDownInventoryType, dropDownInventory, inventoryValuationReportGridUrl, inventoryId, inventoryTypeId;

    jQuery(function ($) {
        $('#printPdfBtn').click(function () {
            downloadInventoryValuationReport();
        });

        $('#printCsvBtn').click(function () {
            downloadInventoryValuationReportCsv();
        });

        onLoadInventoryValuation();
    });

    function onLoadInventoryValuation() {
        initializeForm($("#inventoryValuationSearchForm"), loadGridForInventoryValuationReport);
        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, null);

        initFlexiGrid();
        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Inventory Valuation");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInventoryValuation");
    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownInventoryType.value();
        if (inventoryTypeId == '') {
            dropDownInventory.setDataSource(getKendoEmptyDataSource());
            dropDownInventory.value('');
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryByType')}?inventoryTypeId=" + inventoryTypeId,
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
        dropDownInventory.value('');
    }

    function executePreCondition() {
        if (!validateForm($("#inventoryValuationSearchForm"))) {
            return false;
        }
        return true;
    }

    function loadGridForInventoryValuationReport() {

        if (executePreCondition() == false) {
            return false;
        }
        inventoryTypeId = dropDownInventoryType.value();
        inventoryId = dropDownInventory.value();

        resetGrid();
        inventoryValuationReportGridUrl = "${createLink(controller:'invReport', action: 'searchInventoryValuation')}?inventoryId=" + inventoryId;
        $('#flex1').flexOptions({url: inventoryValuationReportGridUrl});

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: inventoryValuationReportGridUrl,
            success: function (data) {
                populateValuationGrid(data);
            },
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });

        return false;
    }

    function resetGrid() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function populateValuationGrid(data) {
        if (data.isError) {
            showError(data.message);
            resetGrid();
            $('.download_icon_set').hide();
        }
        else if (
                data.gridOutput.total == 0
                ) {
            showInfo(' Inventory Valuation  not found');
            $('.download_icon_set').hide();

        }
        else {
            $('.download_icon_set').show();
            $("#flex1").flexAddData(data.gridOutput);
        }
    }

    function downloadInventoryValuationReport() {

        var confirmMsg = 'Do you want to download inventory valuation report in pdf format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventoryValuation')}?inventoryId=" + inventoryId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadInventoryValuationReportCsv() {

        var confirmMsg = 'Do you want to download inventory valuation report in csv format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventoryValuationCsv')}?inventoryId=" + inventoryId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function initFlexiGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right"},
                        {display: "Item Name", name: "item", width: 150, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 120, sortable: false, align: "right"},
                        {display: "Amount", name: "amount", width: 130, sortable: false, align: "right"},
                        {display: "Valuation Type", name: "valuationType", width: 130, sortable: false, align: "left"}
                    ],
                    sortname: "item_name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Inventory Valuation',
                    useRp: true,
                    rp: 20,
                    rpOptions: [15, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() + 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateValuationGrid
                }
        );
    }

</script>