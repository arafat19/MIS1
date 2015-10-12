<script type="text/javascript">
    var transactionSummaryGridUrl;
    //Global variables
    var paramForSearch;
    var createdDateFrom, createdDateTo;

    $(document).ready(function () {
        onLoadData();
    });

    function onLoadData() {
        try {

            initializeForm($('#searchForm'), onSubmitTransactionSummary);

            $('#printBtn').click(function () {
                downloadCustomerRemittanceSummary();
            });
            initFlexGrid();

            $(document).attr('title', "ARMS - Show Transaction Summary");
            loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showTransactionSummary");

        } catch (e) {
            showError('Error occurred on page load');
        }
    }

    function onSubmitTransactionSummary() {
        if (!$.isNumeric($('#amount').val())) {
            showError('Enter a valid amount');
            return false;
        }
        if (!customValidateDate($('#createdDateFrom'), 'Start Date', $('#createdDateTo'), 'End Date')) return false;

        var createdDateFrom = $('#createdDateFrom').val();
        var createdDateTo = $('#createdDateTo').val();

        var paramForSearch = "?createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo + "&amount=" + $('#amount').val();

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        resetGrid();
        transactionSummaryGridUrl = "${createLink(controller: 'exhReport', action: 'listTransactionSummary')}" + paramForSearch;
        $('#flex1').flexOptions({url: transactionSummaryGridUrl}).flexReload();
        return false;
    }

    function resetGrid() {
        var obj = getEmptyGridModel();
        $('#flex1').flexOptions({url: false}).flexAddData(obj);
        return false;
    }

    function populateGridData(data, transactionSummaryGridUrl) {
        if (data.isError == true) {
            showError(data.message);
            resetGrid();
        } else {
            $("#flex1").flexOptions({url: transactionSummaryGridUrl});
            $("#flex1").flexAddData(data.gridOutput);
            return false;
        }

    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Code", name: "code", width: 100, sortable: true, align: "left"},
                        {display: "Customer Name", name: "name", width: 300, sortable: true, align: "left"},
                        {display: "Total Task", name: "count", width: 100, sortable: false, align: "right"},
                        {display: "Total Amount(" + $('#hidLocalCurrency').val() + ")", name: "total_amount", width: 150, sortable: false, align: "right"},
                        {display: "Total Amount(BDT)", name: "total_amount_bdt", width: 150, sortable: false, align: "right"}
                    ],
                    sortname: "name",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Transaction Summary',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    //width: 725,
                    height: getGridHeight(),
                    customPopulate: populateGridData,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('Transaction Summary not found');
            return false;
        }
    }

    function downloadCustomerRemittanceSummary() {
        if (!$.isNumeric($('#amount').val())) {
            showError('Enter a valid amount');
            return false;
        }
        if (!checkDates($('#createdDateFrom'), $('#createdDateTo'))) return false;

        var createdDateFrom = $('#createdDateFrom').val();
        var createdDateTo = $('#createdDateTo').val();

        var paramForSearch = "?createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo + "&amount=" + $('#amount').val();

        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadTransactionSummary')}" + paramForSearch;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }
</script>