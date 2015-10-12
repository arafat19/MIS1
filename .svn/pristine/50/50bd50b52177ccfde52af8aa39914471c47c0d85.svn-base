<script type="text/javascript">
    var output = ${modelJson ? modelJson : ''};
    var supplierWisePOList = false;
    var dropDownProject, dropDownItemType;

    $(document).ready(function () {
        onLoadSupplierWisePOPage();
        $('#printSupplierWisePOReportPdf').click(function () {
            printSupplierWisePOReport();
            return false;
        });
        $('#printSupplierWisePOReportCsv').click(function () {
            printSupplierWisePOReportCsv();
            return false;
        });
    });

    function onLoadSupplierWisePOPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getPOList);
        initFlexPOList();
        initFlexSupplier();

        if (output.isError) {
            showError(output.message);
            return false;
        }
        $('.download_icon_set').hide();

        if (output.supplierId) {
            $("#fromDate").val(output.fromDate);
            $("#toDate").val(output.toDate);

            populateGrid(output);
            $('.download_icon_set').show();
        }
        populateSupplierGrid();
        // update page title
        $(document).attr('title', "MIS - Supplier Wise Purchase Order ");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procReport/showSupplierWisePO");
        $('#codeSpan').text('(Auto Generated)');
    }

    function printSupplierWisePOReport() {
        var confirmMsg = 'Do you want to download supplier wise PO report in PDF format?';

        var projectId = $('#hidProjectId').val();
        if (!customValidateDate($('#hidFromDate'), 'From Date', $('#hidToDate'), 'To Date')) {
            return false;
        }
        var supplierId = $('#supplierId').val();
        var itemTypeId = $('#hidItemTypeId').val();
        var fromDate = $('#hidFromDate').val();
        var toDate = $('#hidToDate').val();
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&supplierId=" + supplierId + "&itemTypeId=" + itemTypeId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'procReport', action: 'downloadSupplierWisePO')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function printSupplierWisePOReportCsv() {
        var confirmMsg = 'Do you want to download supplier wise PO report in CSV format?';

        var projectId = $('#hidProjectId').val();
        if (!customValidateDate($('#hidFromDate'), 'From Date', $('#hidToDate'), 'To Date')) {
            return false;
        }
        var supplierId = $('#supplierId').val();
        var itemTypeId = $('#hidItemTypeId').val();
        var fromDate = $('#hidFromDate').val();
        var toDate = $('#hidToDate').val();
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&supplierId=" + supplierId + "&itemTypeId=" + itemTypeId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'procReport', action: 'downloadSupplierWisePOCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function executePreConditionToGetPOList() {
        var supplierId = $('#supplierId').val();
        if (supplierId == '') {
            showError("Please select a supplier");
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function getPOList() {
        if (executePreConditionToGetPOList() == false) {
            return false;
        }
        var supplierId = $('#supplierId').val();
        var projectId = dropDownProject.value();
        var itemTypeId = $('#itemTypeId').val();
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');

        var url = "${createLink(controller:'procReport', action: 'listSupplierWisePO')}?projectId=" + projectId + "&supplierId=" + supplierId + "&itemTypeId=" + itemTypeId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        showLoadingSpinner(true);
        doGridEmpty();
        $.ajax({
            url: url,
            success: executePostConditionForPOList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });

        $("#flex1").flexOptions({url: url});
        return false;
    }

    function executePostConditionForPOList(data) {
        if (data.isError) {
            showInfo(data.message);
            $('.download_icon_set').hide();
            return;
        }
        $("#flex1").flexAddData(data.supplierWisePoList);
        $('.download_icon_set').show();

        $("#hidProjectId").val(dropDownProject.value());
        $("#hidItemTypeId").val($("#itemTypeId").val());
        $("#hidFromDate").val($("#fromDate").val());
        $("#hidToDate").val($("#toDate").val());
        return false;
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function initFlexSupplier() {
        $("#flexSupplier").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 310, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select', bclass: 'searchPO', onpress: searchSupplierWisePO},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadSupplierGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Supplier',
                    useRp: false,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getFullGridHeight() - 35,
                    resizable: false,
                    isSearchOpen: true,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function initFlexPOList() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right", hide: true},
                        {display: "PO No", name: "po.id", width: 40, sortable: false, align: "right"},
                        {display: "Item Name", name: "item.name", width: 250, sortable: false, align: "left"},
                        {display: "Rate", name: "pod.rate", width: 120, sortable: false, align: "right"},
                        {display: "Quantity", name: "pod.quantity", width: 120, sortable: false, align: "right"},
                        {display: "Received(Inventory)", name: "pod.store_in_quantity", width: 150, sortable: false, align: "right"},
                        {display: "Received(Fixed Asset)", name: "pod.store_in_quantity", width: 150, sortable: false, align: "right"},
                        {display: "Remaining", name: "remaining_quantity", width: 120, sortable: false, align: "right"},
                        {display: "PO Amount", name: "po_amount", width: 120, sortable: false, align: "right"},
                        {display: "Payable Amount", name: "payable_amount", width: 120, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/procReport/showPurchaseOrderRpt">
                        {name: 'Report', bclass: 'report', onpress: viewPurchaseOrderReport},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "PO No.", name: "poId", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "poId",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Supplier Wise PO List',
                    useRp: true,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getGridHeight() - $('#supplierInfo').height() - 50,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }

    function searchSupplierWisePO(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForPOList(ids) == false) {
            return;
        }

        var supplierId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var supplierName = $(ids[ids.length - 1]).find('td').eq(1).find('div').text();

        $('#lblSupplierName').text(supplierName);
        $('#supplierId').val(supplierId);
        return false;
    }

    function executePreConditionForPOList(ids) {
        if (ids.length == 0) {
            showError("Please select a supplier");
            return false;
        }
        return true;
    }

    function viewPurchaseOrderReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }

        showLoadingSpinner(true);
        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));

        var loc = "${createLink(controller: 'procReport', action: 'showPurchaseOrderRpt')}?purchaseOrderId=" + purchaseOrderId;
        $.post(loc, function (data) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        });
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function reloadSupplierGrid(com, grid) {
        $('#flexSupplier').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            var emptyModel = getEmptyGridModel();
            supplierWisePOList = emptyModel;
        } else {
            supplierWisePOList = data.supplierWisePoList;
        }
        $("#flex1").flexAddData(supplierWisePOList);
    }

    function populateSupplierGrid() {
        var strBudgetUrl = "${createLink(controller: 'supplier', action: 'getAllSupplierList')}";
        $("#flexSupplier").flexOptions({url: strBudgetUrl});
        $("#flexSupplier").flexReload();
    }

    function populateGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var supplierId = data.supplierId;
        var projectId = data.projectId;
        dropDownProject.value(projectId);
        $('#hidProjectId').val(projectId);
        $('#supplierId').val(supplierId);
        $('#hidFromDate').val(fromDate);
        $('#hidToDate').val(toDate);
        $('#hidItemTypeId').val('');
        $('#lblSupplierName').val(data.supplierName);
        var params = "?projectId=" + projectId + "&supplierId=" + supplierId + "&itemTypeId=" + '' + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var supplierWisePoListUrl = "${createLink(controller:'procReport', action: 'listSupplierWisePO')}" + params;
        $("#flex1").flexOptions({url: supplierWisePoListUrl});
        onLoadSupplierPoListJSON(data);
        if (data.supplierWisePoList.total <= 0) {
            $('.download_icon_set').hide();
            showInfo('PO list not found');
        }
    }

    function onLoadSupplierPoListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $('#lblSupplierName').text(data.supplierName);
        $("#flex1").flexAddData(data.supplierWisePoList);
    }
</script>
