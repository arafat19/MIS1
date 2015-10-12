<script type="text/javascript">

    var dropDownProject, dropDownInventoryType, dropDownInventory, startDate,
            endDate, modelJsonForInventoryConsumption;

    jQuery(function ($) {
        modelJsonForInventoryConsumption = ${modelJson};

        $("#flexForBudgetItemList").click(function () {
            populateItemListGrid();
        });

        $('#printPdfReport').click(function () {
            printInventoryConsumptionReport();
            return false;
        });
        onLoadInventoryConsumption();
    });


    function onLoadInventoryConsumption() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#consumptionSearchForm"), getConsumptionBudgetItemOfProject);

            initFlexGrid();
            initFlexItemList();

            $('.download_icon_set').hide();

        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Item Consumption");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showConsumedItemList");

    }

    // To populate Inventory List
    function updateInventoryList(defaultInventoryTypeId) {
        var inventoryTypeId = dropDownInventoryType.value();

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

    function updateFromDate() {
        var fromDate = dropDownProject.dataItem().createdon;
        $("#fromDate").val(fromDate);

    }

    function executePreConditionToBudgetItem() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            showError("Please select a Project");
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        return true;
    }

    function printInventoryConsumptionReport() {
        showLoadingSpinner(true);

        var confirmMsg = 'Do you want to download the inventory consumption report in PDF now?';

        var inventoryTypeId = dropDownInventoryType.value();
        var inventoryId = $('#hidInventoryId').val();
        var projectId = $('#hidProjectId').val();
        var startDate = $('#hidFromDate').val();
        var endDate = $('#hidToDate').val();

        var params = "?inventoryId=" + inventoryId + "&startDate=" + startDate + "&endDate=" + endDate + "&projectId=" + projectId + "&inventoryTypeId=" + inventoryTypeId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadForConsumedItemList')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);

        return false;
    }


    function getConsumptionBudgetItemOfProject() {
        emptyBudgetItemGrid();
        emptyItemListGrid();
        if (executePreConditionToBudgetItem() == false) {
            return false;
        }
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        var inventoryId = dropDownInventory.value();
        var inventoryTypeId = dropDownInventoryType.value();

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invReport', action: 'listBudgetOfConsumption')}?projectId=" + projectId + "&inventoryId=" + inventoryId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&inventoryTypeId=" + inventoryTypeId,
            success: executePostConditionForBudgetItem,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForBudgetItem(data) {
        populateBudgetPoGrid(data);
        return false;
    }

    function populateBudgetPoGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var inventoryId = data.inventoryId;

        $('#hidFromDate').val(fromDate);
        $('#hidToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        $('#hidInventoryId').val(inventoryId);
        var inventoryTypeId = dropDownInventoryType.value();
        var budgetListUrl = "${createLink(controller:'invReport', action: 'listBudgetOfConsumption')}?projectId=" + projectId + "&inventoryId=" + inventoryId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&inventoryTypeId=" + inventoryTypeId;
        emptyBudgetItemGrid();
        emptyItemListGrid();
        $("#flexForBudgetItemList").flexOptions({url: budgetListUrl});
        onLoadBudgetListOfConsumptionJSON(data);
    }

    function emptyBudgetItemGrid() {
        $('.download_icon_set').hide();
        var emptyModel = getEmptyGridModel();
        $("#flexForBudgetItemList").flexOptions({url: false});
        $("#flexForBudgetItemList").flexAddData(emptyModel);
    }
    function emptyItemListGrid() {
        var emptyModel = getEmptyGridModel();
        $("#flexForItemList").flexOptions({url: false});
        $("#flexForItemList").flexAddData(emptyModel);
    }

    function initFlexGrid() {
        $("#flexForBudgetItemList").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: true},
                        {display: "Budget Id", name: "id", width: 25, sortable: false, align: "right", hide: true},
                        {display: "Line Item", name: "budget_item", width: 200, sortable: false, align: "left"},
                        {display: "No. of Consumed Item", name: "consume_count", width: 150, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadBudgetGrid},
                        {separator: true}
                    ],
                    sortname: "budget.budget_item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Budget List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    width: getGridWidthOfVoucherList(),
                    height: getGridHeight(5) - 15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadBudgetListOfConsumptionJSON
                }
        );
    }

    function onLoadBudgetListOfConsumptionJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            emptyBudgetItemGrid();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flexForBudgetItemList").flexAddData(data.lstBudgetLineItem);
        $('.download_icon_set').show();
    }

    function checkGrid() {
        var rows = $('table#flexForBudgetItemList > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget Item found');
        }
    }

    function reloadBudgetGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flexForBudgetItemList').flexOptions({query: ''}).flexReload();
        }
    }

    function getGridWidthOfVoucherList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForBudgetItemList").parent().width();
        return gridWidth;
    }


    /********************for Item List****************************/
    function executePreConToShowItemList(ids) {
        if (ids.length == 0) {
            showError("Please select a budget to show its consume item List");
            return false;
        }
        return true;
    }

    function populateItemListGrid() {
        doItemListGridEmpty();
        var ids = $('.trSelected', $('#flexForBudgetItemList'));
        if (ids.length == 0) {
            return false;
        }


        //check consumption count
        var consumptionCount = $(ids[ids.length - 1]).find('td').eq(3).find('div').text();
        consumptionCount = parseFloat(consumptionCount);
        if (consumptionCount <= 0) {
            return false;
        }

        var inventoryTypeId = dropDownInventoryType.value();
        var inventoryId = $('#hidInventoryId').val();
        var fromDate = $('#hidFromDate').val();
        var toDate = $('#hidToDate').val();

        showLoadingSpinner(true);
        var budgetId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var itemListUrl = "${createLink(controller:'invReport', action: 'getConsumedItemList')}?budgetId=" + budgetId + "&inventoryId=" + inventoryId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&inventoryTypeId=" + inventoryTypeId;
        showLoadingSpinner(true);
        $.ajax({
            url: itemListUrl,
            success: function (data) {
                $("#flexForItemList").flexOptions({url: itemListUrl});
                onLoadItemListJSON(data)
            },
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function initFlexItemList() {
        $("#flexForItemList").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "SL", name: "serial", width: 20, sortable: false, align: "right", hide: true},
                        {display: "Item", name: "item_name", width: 140, sortable: false, align: "left"},
                        {display: "Consumed Quantity", name: "quantity", width: 110, sortable: false, align: "right"},
                        {display: "Consumed Amount", name: "amount", width: 110, sortable: false, align: "right"},
                        {display: "Unapproved Quantity", name: "unapproved_quantity", width: 120, sortable: false, align: "right"},
                        {display: "Budget Quantity", name: "budget_quantity", width: 90, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGridDetails},
                        {separator: true}
                    ],
                    sortname: "item.name",
                    sortorder: "asc",
                    usepager: false,
                    singleSelect: true,
                    title: 'Item List',
                    useRp: false,
                    showTableToggleBtn: false,
                    width: getGridWidthOfItemList(),
                    height: getGridHeight() + 16,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkItemListGrid();
                    },
                    customPopulate: onLoadItemListJSON
                }
        );
    }

    function reloadGridDetails(com, grid) {
        if (com == 'Refresh') {
            $('#flexForItemList').flexOptions({query: ''}).flexReload();
        }
    }

    function getGridWidthOfItemList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForItemList").parent().width();
        return gridWidth;
    }

    function onLoadItemListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            emptyItemListGrid();
        }

        $("#flexForItemList").flexAddData(data.lstItem);
    }

    function checkItemListGrid() {
        var rows = $('table#flexForItemList > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Item List found');
        }
    }

    function doItemListGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForItemList").flexAddData(emptyGridModel);
    }

</script>