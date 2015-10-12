<script type="text/javascript">
    var voucherIdForNavigation = "${result.voucherMap?.voucherId}";
    var voucherTraceNo = "${result.voucherMap?.traceNo}";
    var isChequePrintableForNavigation = "${result.voucherMap?.isChequePrintable}";
    var isCancelledForNavigation = "${result.voucherMap?.isCancelledVoucher}";
    var lstTransForNavigation = ${result.voucherMap?.voucherList ? result.voucherMap?.voucherList:[]};

    $(document).ready(function () {
        onLoadVoucher();

        $('#printVoucher').click(function () {
            printVoucher();
            return false;
        });

        $('#printVoucherBankCheque').click(function () {
            printVoucherBankCheque();
            return false;
        });

        $('#printVoucherChequePreview').click(function () {
            printVoucherChequePreview();
            return false;
        });
    });

    function onLoadVoucher() {
        if (voucherIdForNavigation > 0) {
            $('#hideVoucherId').val(voucherIdForNavigation);
            $('#traceNo').val(voucherTraceNo);
            $('.download_icon_set').show();
        }
        if (isChequePrintableForNavigation == 'true') {
            $('#accountNameDiv').show();
        } else {
            $('#accountNameDiv').hide();
        }

        if (isCancelledForNavigation == 'true') {
            $('#lblCancelled').show();
            $('#lblCancelled').text(' ( Cancelled )');
            $('#cancelledVoucher').attr('checked', true);
            $('#lblCancelledByOn').show();
            $('#lblCancelledReason').show();
            $('#accountNameDiv').hide();
        }

        $("#tblTransactionDetails").kendoListView({
            dataSource: lstTransForNavigation,
            template: "<tr>" +
                    "<td>#:code#</td>" +
                    "<td>#:headName#<br>#:particulars#</td>" +
                    "<td>#:source#</td>" +
                    "<td>#:debit#</td>" +
                    "<td>#:credit#</td>" +
                    "</tr>"
        });
        // update page title
        $(document).attr('title', "MIS - Voucher");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showVoucher");
    }
    function executePreCondition() {
        showLoadingSpinner(true);
        if ($('#traceNo').val() == '') {
            showError('Please insert a trace number')
            showLoadingSpinner(false);
            return;
        }
        return true;
    }
    function executePostCondition() {
        if (isError == 'true') {
            $('#lblVoucherTypeName').text('Voucher Type Title');
            $('.download_icon_set').hide();
            $('#accountNameDiv').hide();
            showLoadingSpinner(false);
            return;
        }
        showLoadingSpinner(false);
       // only for template rendering
        $('#printVoucherBankCheque').click(function () {
            printVoucherBankCheque();
            return false;
        });

        $('#printVoucherChequePreview').click(function () {
            printVoucherChequePreview();
            return false;
        });
    }

    function printVoucher() {
        var voucherId = $('#hideVoucherId').val();
        if (voucherId.length <= 0) {
            showError('First populate voucher then click print');
            return false;
        }

        showLoadingSpinner(true);
        var cancelledVoucher = $('#cancelledVoucher').is(":checked");
        var params = "?voucherId=" + voucherId + "&cancelledVoucher=" + cancelledVoucher;
        if (confirm('Do you want to download the voucher now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadVoucher')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printVoucherBankCheque() {
        var voucherId = $('#hideVoucherId').val();
        if (voucherId.length <= 0) {
            showError('First populate voucher then click print');
            return false;
        }

        var accountName = $('#accountName').val();
        var isAccountPayable = $('#isAccountPayable').attr('checked');
        isAccountPayable = isAccountPayable ? true : false;

        showLoadingSpinner(true);
        var params = "?voucherId=" + voucherId + "&accountName=" + encodeURIComponent(accountName) + "&isAccountPayable=" + isAccountPayable;
        if (confirm('Do you want to download the cheque now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadVoucherBankCheque')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printVoucherChequePreview() {
        var voucherId = $('#hideVoucherId').val();
        if (voucherId.length <= 0) {
            showError('First populate voucher then click print');
            return false;
        }

        var accountName = $('#accountName').val();
        var isAccountPayable = $('#isAccountPayable').attr('checked');
        isAccountPayable = isAccountPayable ? true : false;

        showLoadingSpinner(true);
        var params = "?voucherId=" + voucherId + "&accountName=" + encodeURIComponent(accountName) + "&isAccountPayable=" + isAccountPayable;
        if (confirm('Do you want to download the bank cheque preview now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadVoucherBankChequePreview')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>