<script type="text/javascript">
    var modelJsonForSupplierPayable, dropDownProject;

    jQuery(function ($) {
        modelJsonForSupplierPayable = ${modelJson};
        $('#printProjectPayable').click(function () {
            printSupplierPayable();
            return false;
        });
        $('#printProjectPayableCsv').click(function () {
            printSupplierPayableCsv();
            return false;
        });
        onLoadSupplierPayable();
    });

    function printSupplierPayable() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate Supplier payable list then click print');
            return false;
        }

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the supplier wise payable report in PDF format?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSupplierWisePayable')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printSupplierPayableCsv() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate Supplier payable list then click print');
            return false;
        }

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the supplier wise payable report in CSV format?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSupplierWisePayableCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadSupplierPayable() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getSupplierPayable);
            $('.download_icon_set').hide();
            initFlexGrid();

            $("#fromDate").val(modelJsonForSupplierPayable.fromDate);
            $("#toDate").val(modelJsonForSupplierPayable.toDate);
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Supplier Wise Payable Report");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showSupplierWisePayable");
    }

    function executePreConditionToGetSupplierPo() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function getSupplierPayable() {
        resetSupplierPayableForm();
        if (executePreConditionToGetSupplierPo() == false) {
            return false;
        }

        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSupplierWisePayable')}" + params,
            success: executePostConditionForSupplierPayable,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForSupplierPayable(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateSupplierPayableGrid(data);
        $('.download_icon_set').show();
        return false;
    }

    function populateSupplierPayable(result) {
        $('#hidProjectId').val(result.projectId);
        $('#hideFromDate').val(result.fromDate);
        $('#hideToDate').val(result.toDate);
        $("#fromDate").val(result.fromDate);
        $("#toDate").val(result.toDate);
        $('.download_icon_set').show();
    }

    function resetSupplierPayableForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hideFromDate').val('');
        $('#hideToDate').val('');
        $('#hidProjectId').val('');
    }

    function populateSupplierPayableGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        if(projectId == 0) projectId = ''
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var supplierPayableListUrl = "${createLink(controller:'accReport', action: 'listSupplierWisePayable')}" + params;
        $("#flex1").flexOptions({url: supplierPayableListUrl});
        onLoadSupplierPayableListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: true},
                        {display: "Supplier", name: "supplier_id", width: 165, sortable: false, align: "left"},
                        {display: "PO Amount", name: "po_amount", width: 130, sortable: false, align: "right"},
                        {display: "Received(Inventory)", name: "received_in_inventory", width: 130, sortable: false, align: "right"},
                        {display: "Received(Fixed Asset)", name: "voucher_total", width: 130, sortable: false, align: "right"},
                        {display: "Payable", name: "payable", width: 130, sortable: false, align: "right"},
                        {display: "Paid", name: "paid", width: 130, sortable: false, align: "right"},
                        {display: "Balance", name: "balance", width: 130, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/accReport/showSupplierWisePayment">
                        {name: 'Paid Details', bclass: 'paid_details', onpress: getPaidDetails},
                        </sec:access>
                        <sec:access url="/procReport/showSupplierWisePO">
                        {name: 'PO Details', bclass: 'poDetails', onpress: getSupplierPODetails},
                        </sec:access>
                        <sec:access url="/accReport/showSourceLedger">
                        {name: 'Source Ledger', bclass: 'view', onpress: viewSourceLedger}
                        </sec:access>
                    ],
                    sortname: "supplier.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Supplier Wise Payable Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadSupplierPayableListJSON
                }
        );
    }

    function executePreConForPaidDetails() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'supplier') == false) {
            return false;
        }
        return true;
    }

    function getPaidDetails(com, grid) {
        if (executePreConForPaidDetails() == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGrid($('#flex1'));
        var projectId = $('#hidProjectId').attr('value');
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var url = "${createLink(controller:'accReport', action: 'showSupplierWisePayment')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        $.history.load(formatLink(url));
        return false;
    }

    function executePreConForGetPODetails() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return false;
        }
        return true;
    }

    function getSupplierPODetails(com, grid) {
        if (executePreConForGetPODetails() == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGrid($('#flex1'));

        var projectId = $('#hidProjectId').val();
        var fromDate = $('#hideFromDate').val();
        var toDate = $('#hideToDate').val();
        var url = "${createLink(controller:'procReport', action: 'showSupplierWisePO')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        $.history.load(formatLink(url));
        return false;
    }

    function onLoadSupplierPayableListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flex1").flexAddData(data.supplierWisePayableList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Supplier Po found');
            $('.download_icon_set').hide();
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function executePreConForViewSourceLedger() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return false;
        }
        return true;
    }

    function viewSourceLedger(com, grid) {
        if (executePreConForViewSourceLedger() == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGrid($('#flex1'));

        var projectId = $('#hidProjectId').val();
        var fromDate = $('#hideFromDate').val();
        var toDate = $('#hideToDate').val();
        var url = "${createLink(controller:'accReport', action: 'showSourceLedger')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        $.history.load(formatLink(url));
        return false;
    }

</script>