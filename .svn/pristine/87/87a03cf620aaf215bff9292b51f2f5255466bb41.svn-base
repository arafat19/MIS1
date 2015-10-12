<script type="text/javascript">
    var remittanceTransactionGridUrl, modelJson, foreignCurrency;
    var $createdDateFrom = $('#createdDateFrom');
    var $createdDateTo = $('#createdDateTo');
    var $flex1 = $("#flex1");
    $(document).ready(function()  {

        $('#printPdfBtn').click(function () {
            downloadRemittanceTransaction();
        });

        $('#printCsvBtn').click(function () {
            downloadRemittanceTransactionCsv();
        });

        onLoadTransactionSummaryReport();

    });

    function onLoadTransactionSummaryReport() {
        initializeForm($("#remittanceTransactionForm"),onSubmitRemittanceTransaction);
        modelJson = ${modelJson};
        var isError = modelJson.isError;
        if (isError == 'true') {
            showError(modelJson.message);
            return false;
        }
        foreignCurrency = modelJson.foreignCurrency;
        initFlex();
        setDocumentUI();
        return false;
    }
    function setDocumentUI() {
        $(document).attr('title', "ARMS - Remittance Transaction Summary Report");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showRemittanceTransaction");
        return false;
    }

    function onSubmitRemittanceTransaction() {

        if (!$.isNumeric($('#amount').val())) {
            showError('Enter a valid amount');
            return false;
        }

        if (customValidateDate($('#createdDateFrom'), 'Start Date', $('#createdDateTo'), 'End Date') == false) return false;

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        resetGrid();
        var params = "?formDate=" + $createdDateFrom.val() + "&toDate=" + $createdDateTo.val() + "&amount=" + $('#amount').val();
        remittanceTransactionGridUrl = "${createLink(controller: 'exhReport', action: 'listRemittanceTransaction')}" + params;
        $flex1.flexOptions({url: remittanceTransactionGridUrl}).flexReload();
        return false;
    }
    function resetGrid() {
        var obj = getEmptyGridModel();
        $flex1.flexOptions({url: false}).flexAddData(obj);
        return false;
    }
    function executePostConditionForLoadGrid(data) {
        if (data.isError == true) {
            showError(data.message);
            resetGrid();
        } else {
            $flex1.flexOptions({url: remittanceTransactionGridUrl});
            $flex1.flexAddData(data.gridOutput);
        }
        return false;
    }

    function getCurrency(currency) {
        return "(" + currency + ")";
    }

//    function getLocalCurrency() {
//        return getCurrency(localCurrency);
//    }

    function getForeignCurrency() {
        return getCurrency(foreignCurrency);
    }

    function downloadRemittanceTransaction() {
        if (!$.isNumeric($('#amount').val())) {
            showError('Enter a valid amount');
            return false;
        }
        if (checkDates($createdDateFrom, $createdDateTo) == false) return false;

        showLoadingSpinner(true);
        var params = "?formDate=" + $createdDateFrom.val() + "&toDate=" + $createdDateTo.val() + "&amount=" + $('#amount').val();

        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadRemittanceTransaction')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    function downloadRemittanceTransactionCsv() {
        if (!$.isNumeric($('#amount').val())) {
            showError('Enter a valid amount');
            return false;
        }
        if (checkDates($createdDateFrom, $createdDateTo) == false) return false;

        var params = "?formDate=" + $createdDateFrom.val() + "&toDate=" + $createdDateTo.val() + "&amount=" + $('#amount').val();
        var confirmMsg = 'Do you want to download the report in csv format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadRemittanceTransactionCsv')}"+ params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function initFlex() {
        $flex1.flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Customer code", name: "customer_code", width: 120, sortable: false, align: "left"},
                        {display: "Customer Name", name: "customer_name", width: 200, sortable: false, align: "left"},
                        {display: "Customer DOB", name: "customer_dob", width: 120, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiary_name", width: 200, sortable: false, align: "left"},
                        {display: "Total Task", name: "amount_in_local_currency", width: 65, sortable: false, align: "right"},
                        {display: "Total (" + $('#hidLocalCurrency').val()+")", name: "amount_in_local_currency", width: 100, sortable: false, align: "right"},
                        {display: "Total" + getForeignCurrency(), name: " amount_in_foreign_currency", width: 100, sortable: false, align: "right"}
                    ],
                    sortname: "exh_customer.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Remittance Transaction',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    customPopulate: executePostConditionForLoadGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No transaction found');
        }
        return false;
    }

</script>