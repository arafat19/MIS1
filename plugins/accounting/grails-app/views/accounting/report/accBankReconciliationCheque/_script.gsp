<script type="text/javascript">
    jQuery(function ($) {
        $('#printBankReconciliationChequeList').click(function () {
            printBankReconciliationChequeList();
            return false;
        });
        $('#printBankReconciliationChequeListCsv').click(function () {
            printBankReconciliationChequeListCsv();
            return false;
        });
        initFlexForBankReconciliationChequeList();
        onLoadBankReconciliationChequeList();
    });

    function getBankReconciliationChequeList() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        $('.download_icon_set').hide();
        var toDate = $('#toDate').attr('value');

        if (!checkCustomDate($('#toDate'), 'To Date')) {
            return false;
        }
        doGridEmpty();
        $("#hideToDate").val('');
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listBankReconciliationCheque')}?toDate=" + toDate,
            success: executePostConditionForBankReconciliationChequeList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForBankReconciliationChequeList(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return false;
        }

        populateBankReconciliationChequeListGrid(data);
        return false;
    }

    function printBankReconciliationChequeList() {
        var toDate = $('#hideToDate').attr('value');

        if (!checkCustomDate($('#hideToDate'), 'To Date')) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the Bank Reconciliation Cheque list now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadBankReconciliationCheque')}?toDate=" + toDate;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printBankReconciliationChequeListCsv() {
        var toDate = $('#hideToDate').attr('value');

        if (!checkCustomDate($('#hideToDate'), 'To Date')) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the Bank Reconciliation Cheque list in CSV Format now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadBankReconciliationChequeCsv')}?toDate=" + toDate;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadBankReconciliationChequeList() {
        initializeForm($("#searchForm"), getBankReconciliationChequeList);
        $('.download_icon_set').hide();
        $('span.headingText').html('Bank Reconciliation Cheque List');

        // update page title
        $(document).attr('title', "MIS - Bank Reconciliation Cheque List");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showBankReconciliationCheque");
    }

    function populateBankReconciliationChequeListGrid(data) {
        $('.download_icon_set').show();
        var accBankReconciliationChequeListUrl = "${createLink(controller:'accReport', action: 'listBankReconciliationCheque')}?toDate=" + data.toDate;
        $("#flex1").flexOptions({url: accBankReconciliationChequeListUrl});
        $("#flex1").flexAddData(data.bankReconciliationChequeList);
        $("#hideToDate").val(data.toDate);
    }

    function initFlexForBankReconciliationChequeList() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Source", name: "voucherDate", width: 400, sortable: false, align: "left"},
                        {display: "Cheque No", name: "chequeNo", width: 100, sortable: false, align: "left"},
                        {display: "Cheque Date", name: "chequeDate", width: 80, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 150, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "cheque_no",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Bank Reconciliation Cheque List',
                    useRp: true,
                    rp: 20,
                    resizable: false,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadBankReconciliationChequeListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadBankReconciliationChequeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flex1").flexAddData(data.bankReconciliationChequeList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Cheque found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>