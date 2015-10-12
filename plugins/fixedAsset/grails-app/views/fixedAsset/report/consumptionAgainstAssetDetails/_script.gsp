<script type="text/javascript">
    var dropDownProject, dropDownItemId, dropDownFixedAssetDetailsId,modelJsonForConsumptionList,
        fixedAssetDetailsId, fromDate, toDate, itemId,projectId;

    jQuery(function ($) {
        modelJsonForConsumptionList = ${modelJson};

        $('#showReportDetails').click(function () {
            getConsumptionList();
            return false;
        });
        $('#printReport').click(function () {
            printConsumptionReport();
            return false;
        });

        $('#printReportCSV').click(function () {
            printConsumptionReportCsv();
            return false;
        });

        onLoadPage();
        initGridData();
    });

    function onLoadPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getConsumptionList);
        dropDownFixedAssetDetailsId = initKendoDropdown($('#fixedAssetDetailsId'), null, null, getKendoEmptyDataSource(dropDownFixedAssetDetailsId, "ALL"));

        try {

            $("#fromDate").val(modelJsonForConsumptionList.date);
            $('.download_icon_set').hide();
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Consumption Against Fixed Asset Details Report");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fixedAssetReport/showConsumptionAgainstAssetDetails");
    }

    function printConsumptionReport() {
        showLoadingSpinner(true);
        var hidProjectId = $('#hidProjectId').val();
        var hidFixedAssetDetailsId = $('#hidFixedAssetDetailsId').val();
        var hidFromDate = $('#hidFromDate').val();
        var hidToDate = $('#hidToDate').val();
        var hidItemId = $('#hidItemId').val();
        var params = "?fixedAssetDetailsId=" + hidFixedAssetDetailsId + "&itemId=" + hidItemId + "&fromDate=" + hidFromDate + "&toDate=" + hidToDate + "&projectId=" + hidProjectId;
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller:'fixedAssetReport', action: 'downloadConsumptionAgainstAssetDetails')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);

        return false;
    }

    function printConsumptionReportCsv() {
        showLoadingSpinner(true);
        var hidProjectId = $('#hidProjectId').val();
        var hidFixedAssetDetailsId = $('#hidFixedAssetDetailsId').val();
        var hidFromDate = $('#hidFromDate').val();
        var hidToDate = $('#hidToDate').val();
        var hidItemId = $('#hidItemId').val();
        var params = "?fixedAssetDetailsId=" + hidFixedAssetDetailsId + "&itemId=" + hidItemId + "&fromDate=" + hidFromDate + "&toDate=" + hidToDate + "&projectId=" + hidProjectId;

        if (confirm('Do you want to download the CSV now?')) {
            var url = "${createLink(controller:'fixedAssetReport', action: 'downloadConsumptionAgainstAssetDetailsCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);

        return false;
    }

    function executePreConToGetFixedAssetList() {

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        if (dropDownItemId.value() <= 0) {
            showError('Please select an item');
            return false;
        }
        return true;
    }

    // To populate FixedAssetName List
    function updateFixedAssetList() {
        dropDownFixedAssetDetailsId.setDataSource(getKendoEmptyDataSource());
        dropDownFixedAssetDetailsId.value('');
        if (dropDownItemId.value() <= 0) {
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        projectId = dropDownProject.value();
        fromDate = $('#fromDate').val();
        toDate = $('#toDate').val();
        itemId = dropDownItemId.value();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransaction', action: 'listFixedAssetByItemAndProject')}?fromDate=" + fromDate + "&toDate=" + toDate + "&itemId=" + itemId + "&projectId=" + projectId,
            success: updateFixedAssetListDropDown,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateFixedAssetListDropDown(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownFixedAssetDetailsId.setDataSource(data.fixedAssetList);
    }

    function executePreConToGetConsumptionList() {

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        if (dropDownItemId.value() <= 0) {
            showError('Please select an item');
            return false;
        }
        return true;
    }

    function getConsumptionList() {
        if (executePreConToGetConsumptionList() == false) {
            return false;
        }
        projectId = dropDownProject.value() ? dropDownProject.value() : '-1';
        fixedAssetDetailsId = dropDownFixedAssetDetailsId.value() ? dropDownFixedAssetDetailsId.value() : '-1';
        fromDate = $('#fromDate').val();
        toDate = $('#toDate').val();
        itemId = dropDownItemId.value();
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'fixedAssetReport', action: 'listConsumptionAgainstAssetDetails')}?fixedAssetDetailsId=" + fixedAssetDetailsId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemId=" + itemId + "&projectId=" + projectId,
            success: executePostConToGetConsumptionList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConToGetConsumptionList(data) {
        if (data.isError) {
            doGridEmpty();
            showError(data.message);
            $('.download_icon_set').hide();
            return false;
        }
        $('.download_icon_set').show();
        populateConsumptionList(data);
        setHiddenFieldDate();

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
                        {display: "Fixed Asset Name", name: "fixed_asset_name", width: 250, sortable: false, align: "left"},
                        {display: "Transaction Date", name: "transaction_date", width: 100, sortable: true, align: "left"},
                        {display: "Quantity", name: "quantity", width: 150, sortable: false, align: "right"},
                        {display: "Inventory", name: "inventory_name", width: 250, sortable: false, align: "left"}
                    ],
                    sortname: "fad.name, iitd.transaction_date",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Consumption Against Fixed Asset(Details) List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(3) - 15
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function populateConsumptionList(data) {
        var strUrl = "${createLink(controller: 'fixedAssetReport', action: 'listConsumptionAgainstAssetDetails')}?fixedAssetDetailsId=" + fixedAssetDetailsId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemId=" + itemId + "&projectId=" + projectId;
        $("#flex1").flexOptions({url: strUrl});
        $("#flex1").flexAddData(data);
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 15}).flexReload();
        $('select[name=rp]').val(20);
    }

    function setHiddenFieldDate() {
        $('#hidFromDate').val($('#fromDate').val());
        $('#hidToDate').val($('#toDate').val());
        $('#hidProjectId').val(dropDownProject.value());
        $('#hidItemId').val(dropDownItemId.value());
        $('#hidFixedAssetDetailsId').val(dropDownFixedAssetDetailsId.value());
    }

</script>