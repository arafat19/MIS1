<script type="text/javascript">
    var modelJsonForLedger, gridModelCoa, accTypePaymentBankId, accTypePaymentCashId,
            accTypeReceivedBankId, accTypeReceivedCashId, accTypeJournalId, dropDownProject;

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
        onLoadLedger();
    });

    function initFlexSearchCOA() {
        $("#flexSearchCOA").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "coaId", width: 10, sortable: false, align: "right", hide: true},
                        {display: "Code", name: "code", width: 60, sortable: true, align: "left"},
                        {display: "Head Name", name: "description", width: 200, sortable: true, align: "left"},
                        {display: "Source Id", name: "accSourceTypeId", width: 60, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Add', bclass: 'add', onpress: addIntoReport},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadCoaGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "ALL", name: "name", sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Search Chart Of Account',
                    showTableToggleBtn: false,
                    height: getFullGridHeight() - 40,
                    resizable: false,
                    customPopulate: customPopulateCoaGrid,
                    isSearchOpen: true,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function customPopulateCoaGrid(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            gridModelCoa = data;
        }
        $("#flexSearchCOA").flexAddData(gridModelCoa);
    }

    function addIntoReport(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flexSearchCOA'), ' chart of account') == false) {
            return;
        }

        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();

        $('#htmlCoaCode').html(coaCode);
        $('#coaCode').val(coaCode);
        return false;
    }

    function reloadCoaGrid(com, grid) {
        $('#flexSearchCOA').flexOptions({query: ''}).flexReload();
    }

    function setUrlCOAGrid() {
        var strUrl = "${createLink(controller:'accChartOfAccount', action: 'listForVoucher')}";
        $("#flexSearchCOA").flexOptions({url: strUrl});

        if (gridModelCoa) {
            $("#flexSearchCOA").flexAddData(gridModelCoa);
        }
    }

    function printLedger() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        var ledgerId = $('#hidCoaCode').val();
        if (ledgerId.length <= 0) {
            showError('First populate ledger then click print');
            return false;
        }

        var coaCode = $('#hidCoaCode').attr('value');
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = dropDownProject.value();

        showLoadingSpinner(true);
        var params = "?coaCode=" + coaCode + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the ledger now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadLedger')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printLedgerCsv() {
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        var ledgerId = $('#hidCoaCode').val();
        if (ledgerId.length <= 0) {
            showError('First populate ledger then click print');
            return false;
        }

        var coaCode = $('#hidCoaCode').attr('value');
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = dropDownProject.value();

        showLoadingSpinner(true);
        var params = "?coaCode=" + coaCode + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the ledger now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadLedgerCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadLedger() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getLedger);
            $('.download_icon_set').hide();
            // initLedgerReportLayout();
            initFlexGrid();
            initFlexSearchCOA();

            $('#projectId').val(modelJsonForLedger.projectId);
            gridModelCoa = modelJsonForLedger.gridObjCoa;

            accTypePaymentBankId = parseInt($('#hidPaymentBankId').val());
            accTypePaymentCashId = parseInt($('#hidPaymentCashId').val());
            accTypeReceivedBankId = parseInt($('#hidReceivedBankId').val());
            accTypeReceivedCashId = parseInt($('#hidReceivedCashId').val());
            accTypeJournalId = parseInt($('#hidJournalId').val());

            setUrlCOAGrid();
            if (modelJsonForLedger.ledgerMap) {
                dropDownProject.value(modelJsonForLedger.projectId);
                populateLedger(modelJsonForLedger.ledgerMap);
                populateLedgerGrid(modelJsonForLedger.ledgerMap);
            }
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Ledger");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showLedger");
    }

    function executePreConditionToGetLedger(code) {
        if (code.length == 0) {
            showError("Please enter an account code");
            $('.download_icon_set').hide();
            return false;
        }
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
    }

    function getLedger() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        var coaCode = $.trim($('#coaCode').val());
        if (executePreConditionToGetLedger(coaCode) == false) {
            return false;
        }
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();

        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listLedger')}?coaCode=" + coaCode + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId,
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

    function populateLedger(result) {
        $('#lblLedgerTypeName').text(result.ledgerTypeName);
        $('#htmlCoaCode').html(result.coaCode);
        $('#coaCode').val(result.coaCode);
        $('#lblCoaCode').text(result.coaCode);
        $('#lblCoaDescription').text(result.coaDescription);
        $('#hidCoaCode').val(result.coaCode);
        $('#hideFromDate').val(result.fromDate);
        $('#hideToDate').val(result.toDate);
        $("#fromDate").val(result.fromDate);
        $("#toDate").val(result.toDate);
    }

    function populateLedgerGrid(data) {
        var coaCode = $('#hidCoaCode').attr('value');
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = dropDownProject.value();
        var ledgerListUrl = "${createLink(controller:'accReport', action: 'listLedger')}?coaCode=" + coaCode + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
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
                        {display: "Cheque No", name: "cheque_no", width: 100, sortable: false, align: "left"},
                        {display: "Particulars", name: "particulars", width: 200, sortable: false, align: "left"},
                        {display: "Dr", name: "amount_dr", width: 100, sortable: false, align: "right"},
                        {display: "Cr", name: "amount_cr", width: 100, sortable: false, align: "right"},
                        {display: "voucherTypeId", name: "voucherTypeId", width: 100, sortable: false, align: "right", hide: true}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/show, /accVoucher/showPayCash,
                            /accVoucher/showPayBank, /accVoucher/showReceiveCash, /accVoucher/showReceiveBank">
                        {name: 'Details', bclass: 'details', onpress: getVoucherDetails},
                        </app:ifAllUrl>
                    ],
                    sortname: "traceNo",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Ledger Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 130,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadLedgerListJSON
                }
        );
    }

    function executePreConForVoucherDetails(ids) {
        if (ids.length == 0) {
            showError("Please select a voucher to get details");
            return false;
        }
        return true;
    }

    function getVoucherDetails(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConForVoucherDetails(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var traceNo = $(ids[ids.length - 1]).find('td').eq(2).find('div').text();
        var voucherTypeId = parseInt($(ids[ids.length - 1]).find('td').eq(7).find('div').text());
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
            showInfo('No Ledger found within given dates');
            $('.download_icon_set').hide();
        } else {
            $('.download_icon_set').show();
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>