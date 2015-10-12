<script type="text/javascript">
    var modelJsonForLedger, accTypePaymentBankId, accTypePaymentCashId, accTypeReceivedBankId, accTypeReceivedCashId,
            accTypeJournalId, dropDownGroupId;

    jQuery(function ($) {
        modelJsonForLedger = ${modelJson};
        $('#printLedger').click(function () {
            printLedger();
            return false;
        });
        $('#printLedgerCsv').click(function () {
            printLedgerCsv();
            return false;
        });
        onLoadGroupLedger();
    });

    function printLedger() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var groupId = $('#hideGroupId').attr('value');

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&groupId=" + groupId;
        if (confirm('Do you want to download the Group ledger now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadForGroupLedgerRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printLedgerCsv() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var groupId = $('#hideGroupId').attr('value');

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&groupId=" + groupId;
        if (confirm('Do you want to download the group ledger now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadForGroupLedgerCsvRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadGroupLedger() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getGroupLedger);
            $('.download_icon_set').hide();
            initFlexGrid();

            accTypePaymentBankId = parseInt($('#hidPaymentBankId').val());
            accTypePaymentCashId = parseInt($('#hidPaymentCashId').val());
            accTypeReceivedBankId = parseInt($('#hidReceivedBankId').val());
            accTypeReceivedCashId = parseInt($('#hidReceivedCashId').val());
            accTypeJournalId = parseInt($('#hidJournalId').val());

            if (modelJsonForLedger.ledgerMap) {
                populateLedger(modelJsonForLedger.ledgerMap);
                populateLedgerGrid(modelJsonForLedger.ledgerMap);
            }
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS -Group Ledger");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showForGroupLedgerRpt");
    }

    function executePreConditionToGetLedger() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        var groupId = dropDownGroupId.value();
        if (groupId == '') {
            showError('Please select a group');
            $('.download_icon_set').hide();
            return false;
        }
        return true;
    }

    function getGroupLedger() {
        clearDataField();
        doGridEmpty();
        if (executePreConditionToGetLedger() == false) {
            return false;
        }

        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var groupId = dropDownGroupId.value();

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listForGroupLedgerRpt')}?fromDate=" + fromDate + "&toDate=" + toDate + "&groupId=" + groupId,
            success: executePostConditionForLedger,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForLedger(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateLedger(data.ledgerMap);
        populateLedgerGrid(data);
        return false;
    }

    function clearDataField() {
        $('#hideFromDate').val('');
        $('#hideToDate').val('');
        $('#lblPrevBalance').text('');
        $('#printLedger').hide();
    }

    function populateLedger(result) {
        var groupId = dropDownGroupId.value();
        $('#hideFromDate').val(result.fromDate);
        $('#hideToDate').val(result.toDate);
        $('#hideGroupId').val(groupId);
        $("#fromDate").val(result.fromDate);
        $("#toDate").val(result.toDate);
        $('#printLedger').show();
    }

    function populateLedgerGrid(data) {
        $('.download_icon_set').show();
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var groupId = dropDownGroupId.value();
        var ledgerListUrl = "${createLink(controller:'accReport', action: 'listForGroupLedgerRpt')}?fromDate=" + fromDate + "&toDate=" + toDate + "&groupId=" + groupId;
        $("#flex1").flexOptions({url: ledgerListUrl});
        onLoadLedgerListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: true},
                        {display: "Voucher Date", name: "voucher_date", width: 100, sortable: false, align: "left"},
                        {display: "Trace No", name: "trace_no", width: 100, sortable: false, align: "left"},
                        {display: "Account Code", name: "code", width: 100, sortable: false, align: "right"},
                        {display: "Cheque No", name: "cheque_no", width: 100, sortable: false, align: "left"},
                        {display: "Particulars", name: "particulars", width: 200, sortable: false, align: "left"},
                        {display: "Dr", name: "amount_dr", width: 100, sortable: false, align: "right"},
                        {display: "Cr", name: "amount_cr", width: 100, sortable: false, align: "right"},
                        {display: "Voucher Type Id", name: "voucherTypeId", width: 100, sortable: false, align: "right", hide: true}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/show, /accVoucher/showPayCash,
                            /accVoucher/showPayBank, /accVoucher/showReceiveCash, /accVoucher/showReceiveBank">
                        {name: 'Details', bclass: 'details', onpress: getVoucherDetails},
                        </app:ifAllUrl>
                    ],
                    sortname: "av.voucher_date, av.trace_no",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Group Ledger Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(3) - $("#divLedger").height() - 60,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadLedgerListJSON
                }
        );
    }

    function getVoucherDetails(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return;
        }
        showLoadingSpinner(true);
        var traceNo = $(ids[ids.length - 1]).find('td').eq(2).find('div').text();
        var voucherTypeId = parseInt($(ids[ids.length - 1]).find('td').eq(8).find('div').text());
        var url;
        switch (voucherTypeId) {
            case accTypePaymentBankId:
                url = "${createLink(controller:'accVoucher', action: 'showPayBank')}?traceNo=" + traceNo;
                break;
            case accTypePaymentCashId:
                url = "${createLink(controller:'accVoucher', action: 'showPayCash')}?traceNo=" + traceNo;
                break;
            case accTypeReceivedBankId:
                url = "${createLink(controller:'accVoucher', action: 'showReceiveBank')}?traceNo=" + traceNo;
                break;
            case accTypeReceivedCashId:
                url = "${createLink(controller:'accVoucher', action: 'showReceiveCash')}?traceNo=" + traceNo;
                break;
            case accTypeJournalId:
                url = "${createLink(controller:'accVoucher', action: 'show')}?traceNo=" + traceNo;
                break;
            default:
                break
        }
        $.history.load(formatLink(url));
        return false;
    }

    function onLoadLedgerListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $('#lblPrevBalance').text(data.previousBalance);
        $("#flex1").flexAddData(data.ledgerListWrap);
        checkGrid();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Ledger found');
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