<script type="text/javascript">

    jQuery(function ($) {

        $('#printPdfReport').click(function () {
            printReport();
            return false;
        });
        $('#printCsvReport').click(function () {
            printReportCsv();
            return false;
        });
        onLoadPage();
    });

    function onLoadPage() {
        initializeForm($("#searchForm"), getPoItemReceived);

        $('.download_icon_set').hide();
        initFlexGrid();
        // update page title
        $(document).attr('title', "MIS - PO Item Received Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showPoItemReceived");
        $('#poId').focus();
    }

    function executePreConditionToGetPoItemReceived() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        return true;
    }

    function getPoItemReceived() {
        $('.download_icon_set').hide();

        if (executePreConditionToGetPoItemReceived() == false) {
            $('#poId').focus();
            return false;
        }

        var poId = $('#poId').val();
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invReport', action: 'listPoItemReceived')}?poId=" + poId,
            success: populatePoItemReceivedGrid,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function populatePoItemReceivedGrid(data) {
        var poId = $('#poId').val();
        var poItemReceivedListUrl = "${createLink(controller:'invReport', action: 'listPoItemReceived')}?poId=" + poId;

        $("#flex1").flexOptions({url: poItemReceivedListUrl});
        onLoadPoItemReceivedListJSON(data);
        $('#hidPoId').val(poId);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "chalan", width: 25, sortable: false, align: "right", hide: true},
                        {display: "Chalan", name: "chalan", width: 50, sortable: false, align: "right"},
                        {display: "Supplier Chalan", name: "supplier_chalan", width: 80, sortable: false, align: "left"},
                        {display: "Item", name: "item-name", width: 200, sortable: false, align: "left"},
                        {display: "Transaction Date", name: "transactionDate", width: 90, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 103, sortable: false, align: "right"},
                        {display: "Rate", name: "rate", width: 80, sortable: false, align: "right"},
                        {display: "Amount", name: "amount", width: 120, sortable: false, align: "right"},
                        {display: "Approved", name: "approved", width: 50, sortable: false, align: "center"},
                        {display: "Acknowledge By", name: "received", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadPoItemReceivedGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'PO Item Received List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - $("#divSupplier").height() - 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadPoItemReceivedListJSON
                }
        );
    }

    function onLoadPoItemReceivedListJSON(data) {
        $('#lblSupplierName').text('');
        $('.download_icon_set').hide();
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            return false;
        }
        $("#flex1").flexAddData(data.lstPoItemReceived);
        $('#lblSupplierName').text(data.supplierName);
        $('.download_icon_set').show();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('PO Item Received not found');
        }
    }

    function reloadPoItemReceivedGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function doGridEmpty() {
        $('.download_icon_set').hide();
        $("#flex1").flexOptions({url:false, newp:1, rp:20}).flexReload();
        $('select[name=rp]').val(20);
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
    }

    function executePreConditionToPrintReport(ids) {
        if (ids.length == 0) {
            showError("Please enter a PO number");
            return false;
        }
        return true;
    }

    function printReport() {
        var poId = $('#hidPoId').val();
        if (executePreConditionToPrintReport(poId) == false) {
            return false;
        }

        var confirmMsg = 'Do you want to download the report in pdf format?';

        showLoadingSpinner(true);
        var params = "?poId=" + poId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadPoItemReceived')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function printReportCsv() {
        var poId = $('#hidPoId').val();
        if (executePreConditionToPrintReport(poId) == false) {
            return false;
        }

        var confirmMsg = 'Do you want to download the report in csv format?';

        showLoadingSpinner(true);
        var params = "?poId=" + poId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadPoItemReceivedCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

</script>