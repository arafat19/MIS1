<script type="text/javascript">
    var transactionSummaryUrl;
    $(document).ready(function () {
        onLoadSarbTransactionSummary();
    });

    function onLoadSarbTransactionSummary() {
        $('#printPdfBtn').click(function() {
            downloadSarbTransactionSummary();
        });
        initializeForm($('#sarbTransactionSummary'), loadSarbTransactionSummary);
        initFlex();
        $(document).attr('title', "ARMS - SARB Transaction Summary");
        loadNumberedMenu(MENU_ID_SARB, "#sarbReport/showSarbTransactionSummary");
    }

    function loadSarbTransactionSummary() {
        if (checkDates($('#fromDate'), $('#toDate')) == false) return false;
        var params = "?fromDate=" + $('#fromDate').val() + "&toDate=" + $('#toDate').val();
        showLoadingSpinner(true);
        resetGrid();
        transactionSummaryUrl = "${createLink(controller: 'sarbReport', action: 'listSarbTransactionSummary')}" + params;
        $('#flex1').flexOptions({url: transactionSummaryUrl}).flexReload();
        return false;
    }

    function downloadSarbTransactionSummary() {
        if (checkDates($('#fromDate'), $('#toDate')) == false) return false;
        showLoadingSpinner(true);
        var params = "?fromDate=" + $('#fromDate').val() + "&toDate=" + $('#toDate').val();
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'sarbReport', action: 'downloadSarbTransactionSummary')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function initFlex(){
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel : [
                        {display: "Serial", name : "serial", width : 38, sortable : false, align: "left"},
                        {display: "Date", name : "date", width : 100, sortable : false, align: "left"},
                        {display: "SECL Count", name : "secl_count", width : 108, sortable : false, align: "right"},
                        {display: "SECL Amount", name : "secl_total", width : 106, sortable : false, align: "right"},
                        {display: "Accepted Count", name : "acc_count", width : 170, sortable : false, align: "right"},
                        {display: "Accepted Total", name : "acc_total", width : 165, sortable : false, align: "right"},
                        {display: "Rejected Count", name : "rej_count", width : 165, sortable : false, align: "right"},
                        {display: "Rejected Total", name : "rej_total", width : 160, sortable : false, align: "right"}
                    ],
                    sortname: "serial",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'SARB Transaction Summary',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15,20,25],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    customPopulate: populateGrid,
                    afterAjax: function(XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No task found');
        }
    }

    function populateGrid(data) {
        if (data.isError == true) {
            showError(data.message);
            resetGrid();
        } else {
            $("#flex1").flexOptions({url: transactionSummaryUrl});
            $("#flex1").flexAddData(data.gridObj);
            checkGrid();
        }

    }

    function resetGrid() {
        var obj = getEmptyGridModel();
        $('#flex1').flexOptions({url: false}).flexAddData(obj);
    }
</script>