<script type="text/javascript">
    var dropDownItemType, dropDownProject, dropDownTransType, dropDownInventoryType, dropDownInventory, inventoryId,
        itemTypeId, inventoryTypeId, startDate, endDate, transactionTypeId, projectId;

    jQuery(function ($) {

        $('#printReport').click(function () {
            printInventoryTransaction();
            return false;
        });

        $('#printCSVBtn').click(function () {
            printInventoryTransactionCsv();
            return false;
        });

        onLoadInventoryTransactionList();
        initGridData();
    });

    function onLoadInventoryTransactionList() {
        initializeForm($("#searchForm"), getInventoryTransactionList);
        dropDownInventory = initKendoDropdown($('#inventoryId'), null, null, getKendoEmptyDataSource(dropDownInventory, "ALL"));

        // update page title
        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - All Inventory Transaction List");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInventoryTransactionList");
    }

    function printInventoryTransaction() {
        showLoadingSpinner(true);

        var confirmMsg = 'Do you want to download the inventory transaction report in PDF now?';

        var params = "?inventoryId=" + inventoryId + "&transactionTypeId=" + transactionTypeId + "&startDate=" + startDate + "&endDate=" + endDate + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId + "&itemTypeId=" + itemTypeId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventoryTransactionList')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);

        return false;
    }

    function printInventoryTransactionCsv() {
        showLoadingSpinner(true);

        var confirmMsg = 'Do you want to download the inventory transaction report in CSV now?';

        var params = "?inventoryId=" + inventoryId + "&transactionTypeId=" + transactionTypeId + "&startDate=" + startDate + "&endDate=" + endDate + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId + "&itemTypeId=" + itemTypeId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadInventoryTransactionListCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);

        return false;
    }

    function updateInventoryType() {
        dropDownInventoryType.value('');
        dropDownInventory.setDataSource(getKendoEmptyDataSource(null, 'ALL'));
    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        var projectId = dropDownProject.value();
        var inventoryTypeId = dropDownInventoryType.value();
        if (inventoryTypeId == '') {
            dropDownInventory.setDataSource(getKendoEmptyDataSource(null, 'ALL'));
            dropDownInventory.value('');
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listInventoryByTypeAndProject')}?inventoryTypeId=" + inventoryTypeId + "&projectId=" + projectId,
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

    function executePreConditionToInventoryTransactionList() {

        if (!customValidateDate($('#startDate'), 'From Date', $('#endDate'), 'To Date')) {
            return false;
        }

        return true;
    }

    function getInventoryTransactionList() {
        if (executePreConditionToInventoryTransactionList() == false) {
            return false;
        }
        projectId = dropDownProject.value();
        inventoryTypeId = dropDownInventoryType.value();
        inventoryId = dropDownInventory.value();
        transactionTypeId = dropDownTransType.value();
        itemTypeId = dropDownItemType.value();
        startDate = $('#startDate').val();
        endDate = $('#endDate').val();
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invReport', action: 'searchInventoryTransactionList')}?inventoryId=" + inventoryId + "&startDate=" + startDate + "&endDate=" + endDate + "&transactionTypeId=" + transactionTypeId + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId + "&itemTypeId=" + itemTypeId,
            success: executePostConditionForInventoryTransactionList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForInventoryTransactionList(data) {

        if (data.isError) {
            doGridEmpty();
            showError(data.message);
            $('.download_icon_set').hide();
            return false;
        }
        $('.download_icon_set').show();
        populateInventoryTransactionList(data);

        return false;
    }

    function initGridData() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Chalan No", name: "chalanNo", width: 60, sortable: false, align: "left"},
                        {display: "Date", name: "iitd.transaction_date", width: 100, sortable: true, align: "left"},
                        {display: "To/From Transaction", name: "transaction_entity_name", width: 150, sortable: true, align: "left"},
                        {display: "Item", name: "item_name", width: 100, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 100, sortable: false, align: "right"},
                        {display: "Rate", name: "rate", width: 100, sortable: false, align: "right"},
                        {display: "Total", name: "total", width: 100, sortable: false, align: "right"},
                        {display: "Transaction Type", name: "transaction_type", width: 100, sortable: false, align: "left"},
                        {display: "Inventory", name: "inventory_name", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {name: 'Report', bclass: 'addDoc', onpress: getInvoiceInventoryIn},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Chalan No", name: "chalanNo", width: 180, sortable: true, align: "left"},
                        {display: "Item", name: "item_name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "item.name, iitd.transaction_date",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Inventory Transaction List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10
                }
        );
    }

    function getInvoiceInventoryIn(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryInId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invReport', action: 'showInvoice')}?id=" + inventoryInId;
        $.post(loc, function (data) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        });
        return false;
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function populateInventoryTransactionList(data) {
        var strUrl = "${createLink(controller: 'invReport', action: 'searchInventoryTransactionList')}?inventoryId=" + inventoryId + "&startDate=" + startDate + "&endDate=" + endDate + "&transactionTypeId=" + transactionTypeId + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId + "&itemTypeId=" + itemTypeId;
        $("#flex1").flexOptions({url: strUrl});
        $("#flex1").flexAddData(data);
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>