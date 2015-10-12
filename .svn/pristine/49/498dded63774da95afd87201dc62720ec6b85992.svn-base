<script type="text/javascript">
    var modelJsonForSupplierPayment, dropDownProject, dropDownSupplier;
    jQuery(function ($) {
        modelJsonForSupplierPayment = ${modelJson};
        $('#printSupplierPayment').click(function () {
            printSupplierPayment();
            return false;
        });
        $('#printSupplierPaymentCsv').click(function () {
            printSupplierPaymentCsv();
            return false;
        });
        onLoadSupplierPayment();
    });

    function onLoadSupplierPayment() {
        try {
            initializeForm($("#searchForm"), getSupplierPayment);
            $('.download_icon_set').hide();
            $('#lblTotalPaid').text('');
            initFlexGrid();

            if (modelJsonForSupplierPayment.supplierId) {
                $('.download_icon_set').show();
                $('#projectId').val(modelJsonForSupplierPayment.projectId);
                $('#hidProjectId').val(modelJsonForSupplierPayment.projectId);
                $('#supplierId').val(modelJsonForSupplierPayment.supplierId);
                $('#hidSupplierId').val(modelJsonForSupplierPayment.supplierId);
                $('#hideFromDate').val(modelJsonForSupplierPayment.fromDate);
                $('#hideToDate').val(modelJsonForSupplierPayment.toDate);
                $("#fromDate").val(modelJsonForSupplierPayment.fromDate);
                $("#toDate").val(modelJsonForSupplierPayment.toDate);

                dropDownSupplier.value(modelJsonForSupplierPayment.supplierId);
                dropDownProject.value(modelJsonForSupplierPayment.projectId);

                if (modelJsonForSupplierPayment.isError) {
                    showError(modelJsonForSupplierPayment.message);
                    $('.download_icon_set').hide();
                } else {
                    populateSupplierPoGrid(modelJsonForSupplierPayment);
                }
            }
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Supplier Wise Payment");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showSupplierWisePayment");
    }

    function getSupplierPayment() {
        resetSupplierPaymentForm();
        if (executePreConditionToGetSupplierPayment() == false) {
            return false;
        }

        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var supplierId = dropDownSupplier.value();
        var projectId = dropDownProject.value();

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSupplierWisePayment')}?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate,
            success: executePostConditionForSupplierPayment,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePreConditionToGetSupplierPayment() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function executePostConditionForSupplierPayment(data) {
        $('#lblTotalPaid').text('');
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateSupplierPoGrid(data);

        $('.download_icon_set').show();
        return false;
    }

    function printSupplierPayment() {

        if (!customValidateDate($('#hideFromDate'), 'From Date', $('#hideToDate'), 'To Date')) {
            return false;
        }

        var supplierId = $('#hidSupplierId').val();
        var projectId = $('#hidProjectId').val();
        if ((supplierId.length < 0)) {
            showError('First populate supplier payment then click print');
            return false;
        }

        var fromDate = $('#hideFromDate').val();
        var toDate = $('#hideToDate').val();

        showLoadingSpinner(true);
        var params = "?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the supplier wise payment report in PDF format?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSupplierWisePayment')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printSupplierPaymentCsv() {
        if (!customValidateDate($('#hideFromDate'), 'From Date', $('#hideToDate'), 'To Date')) {
            return false;
        }

        var supplierId = $('#hidSupplierId').val();
        var projectId = $('#hidProjectId').val();
        if ((supplierId.length < 0)) {
            showError('First populate supplier payment then click print');
            return false;
        }

        var fromDate = $('#hideFromDate').val();
        var toDate = $('#hideToDate').val();

        showLoadingSpinner(true);
        var params = "?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the supplier wise payment report in CSV format?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSupplierWisePaymentCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function populateSupplierPayment(result) {
        $('#hidSupplierId').val(result.supplierId);
        $('#hidProjectId').val(result.projectId);
        $('#hideFromDate').val(result.fromDate);
        $('#hideToDate').val(result.toDate);
        $("#fromDate").val(result.fromDate);
        $("#toDate").val(result.toDate);
        $('.download_icon_set').show();
    }

    function resetSupplierPaymentForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hideFromDate').val('');
        $('#hideToDate').val('');
        $('#hidSupplierId').val('');
        $('#hidProjectId').val('');
        $('#lblTotalPaid').text('');
    }

    function populateSupplierPoGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var supplierId = data.supplierId;
        var projectId = data.projectId;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hidSupplierId').val(supplierId);
        $('#hidProjectId').val(projectId);
        if(projectId == 0) projectId = ''
        var params = "?supplierId=" + supplierId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var supplierPoListUrl = "${createLink(controller:'accReport', action: 'listSupplierWisePayment')}" + params;
        $("#flex1").flexOptions({url: supplierPoListUrl});
        onLoadSupplierPoListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: true},
                        {display: "PO No", name: "po_id", width: 50, sortable: false, align: "left"},
                        {display: "PO Project", name: "po_project_id", width: 200, sortable: false, align: "left"},
                        {display: "Total PO Amount", name: "voucher_total", width: 210, sortable: false, align: "right"},
                        {display: "Paid Amount", name: "po_total", width: 210, sortable: false, align: "right"},
                        {display: "Remaining", name: "remaining", width: 210, sortable: false, align: "right"}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Supplier Payment Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 80,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadSupplierPoListJSON
                }
        );
    }

    function onLoadSupplierPoListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $('#lblTotalPaid').text(data.paidTotal);
        $("#flex1").flexAddData(data.supplierPaymentList);
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

</script>