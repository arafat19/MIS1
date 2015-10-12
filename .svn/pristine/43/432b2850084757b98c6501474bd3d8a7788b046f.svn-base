<script type="text/javascript">
    var dropDownProject, dropDownSupplierId, hidSupplierId, dropDownItemType, hidItemTypeId;

    jQuery(function ($) {
        $('#printPdfBtn').click(function () {
            downloadItemReceivedGroupBySupplierReport();
            return false;
        });

        onLoadItemReceivedStockReport();
    });

    function onLoadItemReceivedStockReport() {
        initializeForm($("#stockSearchForm"), loadGridForItemReceivedStockReport);

        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Item Received Stock (From Supplier)");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showItemReceivedStock");
    }

    function loadGridForItemReceivedStockReport() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        var supplierId = dropDownSupplierId.value();
        var projectId = dropDownProject.value();
        var itemTypeId = dropDownItemType.value();

        var fromDate = $('#fromDate').val();
        var toDate = $('#toDate').val();
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        hidSupplierId = supplierId;
        $.ajax({
            url: "${createLink(controller: 'invReport', action: 'listItemReceivedStock')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemTypeId=" + itemTypeId,
            success: executePostConditionForLoadGrid,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForLoadGrid(data) {
        var supplierId = dropDownSupplierId.value();
        var projectId = dropDownProject.value();
        var itemTypeId = dropDownItemType.value();

        var fromDate = $('#fromDate').val();
        var toDate = $('#toDate').val();
        if (data.isError == true) {
            showError(data.message);
            doGridEmpty();
            $('#hidProjectId').val('');
            $('#hidSupplierId').val('');
            hidItemTypeId = '';
            $('#hidFromDate').val('');
            $('#hidToDate').val('');

            $('.download_icon_set').hide();
        } else {
            $('#hidProjectId').val(projectId);
            $('#hidSupplierId').val(supplierId);
            hidItemTypeId = itemTypeId;
            $('#hidFromDate').val(fromDate);
            $('#hidToDate').val(toDate);

            $('.download_icon_set').show();
            $("#flex1").flexAddData(data.gridOutput);
        }
        var url = "${createLink(controller: 'invReport', action: 'listItemReceivedStock')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemTypeId=" + itemTypeId;
        $("#flex1").flexOptions({url: url});
    }

    function downloadItemReceivedGroupBySupplierReport() {
        var confirmMsg = 'Do you want to download item received report in PDF format?';

        var supplierId = $('#hidSupplierId').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        var itemTypeId = hidItemTypeId;
        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadItemReceivedGroupBySupplier')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemTypeId=" + itemTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadPdf() {
        var confirmMsg = 'Do you want to download item received report in PDF format?';

        var supplierId = $('#hidSupplierId').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        var itemTypeId = hidItemTypeId;
        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        if(fromDate==''||toDate==''){
            showError('First Populate item stock to download pdf report');
            return false;
        }
        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadItemReceivedStock')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemTypeId=" + itemTypeId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadCsv() {
        var confirmMsg = 'Do you want to download item received report in CSV format?';

        var supplierId = $('#hidSupplierId').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        var itemTypeId = hidItemTypeId;
        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        if(fromDate==''||toDate==''){
            showError('First Populate item stock to download csv report');
            return false;
        }
        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadItemReceivedStockCsv')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&itemTypeId=" + itemTypeId;
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
                    {display: "Serial", name: "serial", width: 25, sortable: false, align: "right"},
                    {display: "Supplier", name: "supplier_name", width: 190, sortable: false, align: "left"},
                    {display: "Item", name: "item_name", width: 280, sortable: false, align: "left"},
                    {display: "Received Quantity", name: "received_quantity", width: 130, sortable: false, align: "right"},
                    {display: "Total Amount", name: "total_amount", width: 130, sortable: false, align: "right"}
                ],
                buttons: [
                    {name: 'PDF', bclass: 'details', onpress: downloadPdf},
                    {name: 'CSV', bclass: 'reportInstance', onpress: downloadCsv},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Supplier", name: "supplier.name", width: 180, sortable: true, align: "left"},
                    {display: "Item", name: "item.name", width: 180, sortable: true, align: "left"}
                ],
                sortname: "supplier.name, item.name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'Item Received Stock',
                useRp: true,
                rp: 20,
                rpOptions: [20, 25, 30],
                showTableToggleBtn: false,
                height: getGridHeight() - 15,
                customPopulate: populateGridForInventoryStockReport,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
//                            checkGrid();
                }
            }
    );

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateGridForInventoryStockReport(data) {
        executePostConditionForLoadGrid(data);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Received Stock found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false}).flexReload();
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('#flex1').flexOptions({query: ''}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>