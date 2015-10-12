<script type="text/javascript">
    var dropDownInventoryType,dropDownInventory,startDate,endDate;

    $(document).ready(function () {
        onLoadInventorySummary();

        $('#printBtn').click(function () {
            downloadInventorySummary();
            return false;
        });

        $('#printBtnCsv').click(function () {
            downloadInventorySummaryCsv();
            return false;
        });
    });

    function onLoadInventorySummary() {
        initializeForm($("#frmInventorySummary"),getInventorySummary);
        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, null);

        initGrid();

        // update page title
        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Inventory Summary");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInventorySummary");
    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownInventoryType.value();
        if (inventoryTypeId == '') {
            dropDownInventory.setDataSource(getKendoEmptyDataSource(dropDownInventory,null));
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

    function downloadInventorySummary() {

        showLoadingSpinner(true);
        var inventory = dropDownInventory.value();
        var params = "?inventoryId=" + inventory + '&startDate=' + startDate + '&endDate=' + endDate;
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventorySummary')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadInventorySummaryCsv() {

        showLoadingSpinner(true);
        var inventory = dropDownInventory.value();
        var params = "?inventoryId=" + inventory + '&startDate=' + startDate + '&endDate=' + endDate;
        if (confirm('Do you want to download the CSV now?')) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventorySummaryCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function executePreCondition() {
        var inventoryType = dropDownInventoryType.value();
        var inventory = dropDownInventory.value();

        if(inventoryType == ''){
            showError('Please select inventory type.');
            return false;
        }

        if(inventory == ''){
            showError('Please select inventory.');
            return false;
        }

        if (!customValidateDate($('#startDate'), 'From Date', $('#endDate'), 'To Date')) {
            return false;
        }
        if (!validateForm($("#frmInventorySummary"))) {
            return false;
        }
        return true;
    }
    // get data to populate grid
    function getInventorySummary() {
        if (executePreCondition() == false) {
            return false;
        }
        startDate = $('#startDate').val();
        endDate = $('#endDate').val();

        doGridEmpty();
        showLoadingSpinner(true);
        var inventory = dropDownInventory.value();
        var params = "?inventoryId=" + inventory + '&startDate=' + startDate + '&endDate=' + endDate;
        $.ajax({
            url: "${createLink(controller:'invReport', action: 'getInventorySummary')}" + params,
            success: executePostConditionForInventorySummary,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    // populate grid with data
    function executePostConditionForInventorySummary(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        var inventory = dropDownInventory.value();
        var params = "?inventoryId=" + inventory + '&startDate=' + startDate + '&endDate=' + endDate;
        var strUrl = "${createLink(controller:'invReport', action: 'getInventorySummary')}" + params;
        $("#flex1").flexOptions({url: strUrl});
        if (data.objGrid) {
            $('.download_icon_set').show();
            $("#flex1").flexAddData(data.objGrid);
        } else {
            $('.download_icon_set').hide();
            var emptyModel = getEmptyGridModel();
            $("#flex1").flexAddData(emptyModel);
            showInfo('No record found');
        }
        return false;
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    //flexigrid url is false due to remove round trip
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Date", name: "date", width: 180, sortable: false, align: "left"},
                        {display: "Item", name: "itemName", width: 180, sortable: false, align: "left"},
                        {display: "Total Quantity(IN)", name: "quantityIn", width: 180, sortable: false, align: "left"},
                        {display: "Count(IN)", name: "countIn", width: 60, sortable: false, align: "right"},
                        {display: "Total Quantity(OUT)", name: "quantityOut", width: 180, sortable: false, align: "left"},
                        {display: "Count(OUT)", name: "countOut", width: 65, sortable: false, align: "right"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Inventory Summary',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() + 20,
                    customPopulate: onLoadInventorySummaryListJSON,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    }
                }
        );
    }

    function onLoadInventorySummaryListJSON(data) {
        executePostConditionForInventorySummary(data)
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>
