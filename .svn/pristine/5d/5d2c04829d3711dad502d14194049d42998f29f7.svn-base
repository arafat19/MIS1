<script type="text/javascript">
    var modelJsonForLedger, accTypePaymentBankId, accTypePaymentCashId, accTypeReceivedBankId,
            accTypeReceivedCashId, accTypeJournalId, dropDownProject, dropDownSourceType,
            dropDownSourceCategory, dropDownSource;

    jQuery(function ($) {
        modelJsonForLedger = ${modelJson};
        $('#printSourceLedgerPdf').click(function () {
            printSourceLedgerReportGroupBySource();
            return false;
        });
        onLoadLedger();
    });

    function printSourceLedgerReportGroupBySource() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var accSourceTypeId = $('#hidAccSourceId').attr('value');
        var sourceCategoryId = $('#hidSourceCategoryId').attr('value');
        var sourceId = $('#hidSourceId').attr('value');
        var sourceName = $('#hidSourceName').attr('value');
        var projectId = $('#hidProjectId').val();

        if (accSourceTypeId.length <= 0) {
            showError('First populate source ledger then click print');
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?accSourceTypeId=" + accSourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&sourceId=" + sourceId + "&sourceName=" + sourceName + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the source ledger report now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSourceLedgeReportGroupBySource')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printSourceLedger() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var accSourceTypeId = $('#hidAccSourceId').attr('value');
        var sourceCategoryId = $('#hidSourceCategoryId').attr('value');
        var sourceId = $('#hidSourceId').attr('value');
        var sourceName = $('#hidSourceName').attr('value');
        var projectId = $('#hidProjectId').val();

        if (accSourceTypeId.length <= 0) {
            showError('No source ledger found');
            return false;
        }
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?accSourceTypeId=" + accSourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&sourceId=" + sourceId + "&sourceName=" + sourceName + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the source ledger report in pdf format now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSourceLedger')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printSourceLedgerCsv() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var accSourceTypeId = $('#hidAccSourceId').attr('value');
        var sourceCategoryId = $('#hidSourceCategoryId').attr('value');
        var sourceId = $('#hidSourceId').attr('value');
        var sourceName = $('#hidSourceName').attr('value');
        var projectId = $('#projectId').val();

        if (accSourceTypeId.length <= 0) {
            showError('No source ledger found');
            return false;
        }
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?accSourceTypeId=" + accSourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&sourceId=" + sourceId + "&sourceName=" + sourceName + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the source ledger report in csv format now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadSourceLedgerCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadLedger() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getLedger);
            if (modelJsonForLedger.isError) {
                showError(modelJsonForLedger.message);
                return false;
            }
            $('.download_icon_set').hide();
            initFlexGrid();

            dropDownSourceCategory = initKendoDropdown($('#sourceCategoryId'), null, null, getKendoEmptyDataSource(dropDownSourceCategory, "ALL"));
            dropDownSource = initKendoDropdown($('#sourceId'), null, null, getKendoEmptyDataSource(dropDownSource, "ALL"));

            accTypePaymentBankId = parseInt($('#hidPaymentBankId').val());
            accTypePaymentCashId = parseInt($('#hidPaymentCashId').val());
            accTypeReceivedBankId = parseInt($('#hidReceivedBankId').val());
            accTypeReceivedCashId = parseInt($('#hidReceivedCashId').val());
            accTypeJournalId = parseInt($('#hidJournalId').val());

            if (modelJsonForLedger.ledgerListWrap) {
                dropDownProject.value(modelJsonForLedger.projectId);
                dropDownSourceType.value(modelJsonForLedger.accSourceTypeId);

                dropDownSourceCategory.setDataSource(modelJsonForLedger.lstSourceCategory);
                dropDownSourceCategory.value(modelJsonForLedger.sourceCategoryId);

                dropDownSource.setDataSource(modelJsonForLedger.lstSupplier);
                dropDownSource.value(modelJsonForLedger.sourceId);

                $("#fromDate").val(modelJsonForLedger.fromDate);
                $("#toDate").val(modelJsonForLedger.toDate);
                $('#hideFromDate').val(modelJsonForLedger.fromDate);
                $('#hideToDate').val(modelJsonForLedger.toDate);
                $('#hidProjectId').val(modelJsonForLedger.projectId);
                $('#hidAccSourceId').val(modelJsonForLedger.accSourceTypeId);
                $('#hidSourceId').val(modelJsonForLedger.sourceId);
                $('#hidSourceCategoryId').val(modelJsonForLedger.sourceCategoryId);

                var sourceName = $('#sourceId > option:selected').text();
                $('#hidSourceName').val(sourceName);

                $('#lblPrevBalance').text(modelJsonForLedger.previousBalance);
                var params = "?accSourceTypeId=" + modelJsonForLedger.accSourceTypeId + "&sourceCategoryId=" + modelJsonForLedger.sourceCategoryId
                        + "&sourceId=" + modelJsonForLedger.sourceId + "&fromDate=" + modelJsonForLedger.fromDate + "&toDate=" + modelJsonForLedger.toDate
                        + "&projectId=" + modelJsonForLedger.projectId;
                var url = "${createLink(controller:'accReport', action: 'listSourceLedger')}" + params;
                $("#flex1").flexAddData(modelJsonForLedger.ledgerListWrap);
                $("#flex1").flexOptions({url: url});
                if (modelJsonForLedger.ledgerListWrap.total <= 0) {
                    showInfo('No Source Ledger found');
                    $('.download_icon_set').hide();
                } else {
                    $('.download_icon_set').show();
                }
            }
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Source Ledger");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showSourceLedger");
    }

    function executePreConditionToGetLedger() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function getLedger() {
        if (executePreConditionToGetLedger() == false) {
            return false;
        }
        resetSourceLedgerForm();
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var accSourceTypeId = dropDownSourceType.value();
        var sourceCategoryId = dropDownSourceCategory.value();
        var sourceId = dropDownSource.value();
        var projectId = dropDownProject.value();

        var params = "?accSourceTypeId=" + accSourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&sourceId=" + sourceId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSourceLedger')}" + params,
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
        populateLedgerGrid(data);
        $('.download_icon_set').show();
        return false;
    }

    function resetSourceLedgerForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hideFromDate').val('');
        $('#hideToDate').val('');
        $('#hidAccSourceId').val('');
        $('#hidSourceCategoryId').val('');
        $('#hidSourceId').val('');
        $('#hidSourceName').val('');
        $('#hidProjectId').val('');
        $('#lblPrevBalance').text('');
    }

    function populateLedgerGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var accSourceTypeId = data.accSourceTypeId;
        var sourceCategoryId = data.sourceCategoryId;
        var sourceId = data.sourceId;
        var projectId = dropDownProject.value();

        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        $('#hidAccSourceId').val(accSourceTypeId);
        $('#hidSourceId').val(sourceId);
        $('#hidSourceCategoryId').val(sourceCategoryId);

        var sourceName = dropDownSource.text();
        $('#hidSourceName').val(sourceName);

        var ledgerListUrl = "${createLink(controller:'accReport', action: 'listSourceLedger')}?accSourceTypeId=" + accSourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&sourceId=" + sourceId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
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
                        {display: "Account Code", name: "coa_code", width: 100, sortable: false, align: "left"},
                        {display: "Head Name", name: "coa_head", width: 150, sortable: false, align: "left"},
                        {display: "Division", name: "division", width: 100, sortable: false, align: "left"},
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
                        <app:ifAllUrl urls="/accReport/downloadSourceLedger">
                        {name: 'PDF Report', bclass: 'report', onpress: printSourceLedger},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadSourceLedgerCsv">
                        {name: 'CSV Report', bclass: 'note', onpress: printSourceLedgerCsv},
                        </app:ifAllUrl>
                    ],
                    sortname: "av.voucher_date, av.trace_no",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Source Ledger Report',
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
        var voucherTypeId = parseInt($(ids[ids.length - 1]).find('td').eq(9).find('div').text());
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
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Source Ledger found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function onChangeSourceType() {
        dropDownSourceCategory.setDataSource(getKendoEmptyDataSource(dropDownSourceCategory, "ALL"));
        dropDownSourceCategory.value('');
        dropDownSource.setDataSource(getKendoEmptyDataSource(dropDownSource, "ALL"));
        dropDownSource.value('');
        var accSourceTypeId = dropDownSourceType.value();
        if (accSourceTypeId == '') {
            dropDownSourceCategory.refresh();
            dropDownSource.refresh();
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSourceCategoryForSourceLedger')}?sourceTypeId=" + accSourceTypeId,
            success: function (data) {
                executePostForOnchangeSourceType(data);
            },
            complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostForOnchangeSourceType(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownSourceCategory.setDataSource(data.lstSourceCategory);
        dropDownSource.setDataSource(data.lstSource);
    }

    function onChangeSourceCategory() {
        dropDownSource.setDataSource(getKendoEmptyDataSource(dropDownSource, "ALL"));  // reset source
        dropDownSource.value('');
        var sourceCategoryId = dropDownSourceCategory.value();
        if (sourceCategoryId == '') {
            return false;
        }
        var accSourceTypeId = dropDownSourceType.value();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSourceByCategoryAndType')}?accSourceTypeId=" + accSourceTypeId + "&sourceCategoryId=" + sourceCategoryId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    dropDownSource.setDataSource(data.sourceList);
                    dropDownSource.refresh();
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

</script>