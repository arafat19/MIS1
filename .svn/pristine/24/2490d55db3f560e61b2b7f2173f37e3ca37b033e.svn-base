<script type="text/javascript">
    var cashierWiseTaskReportGridUrl;
    $(document).ready(function () {
        onLoadCashierWiseReportAdmin();
    });

    function onLoadCashierWiseReportAdmin() {

        $('#printPdfBtn').click(function() {
            downloadRemittanceSummary();
        });

        initializeForm($('#cashierWiseReportSummaryForAdmin'), loadGridForCashierWiseTaskReport);
        initFlex();

        $(document).attr('title', "ARMS - Remittance Summary");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showSummaryReportForAdmin");
    }

    function loadGridForCashierWiseTaskReport() {


        if (checkDates($('#createdDateFrom'), $('#createdDateTo')) == false) return false;
        var cashierId = $('#cashierListId').attr('value');
        var params = "?formDate=" + $('#createdDateFrom').val() + "&toDate=" + $('#createdDateTo').val();

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        resetGrid();
        cashierWiseTaskReportGridUrl = "${createLink(controller: 'exhReport', action: 'listReportSummaryForAdmin')}" + params;
        $('#flex1').flexOptions({url: cashierWiseTaskReportGridUrl}).flexReload();
        return false;
    }

    function resetGrid() {
        var obj = getEmptyGridModel();
        $('#flex1').flexOptions({url: false}).flexAddData(obj);
    }
    function executePostConditionForLoadGrid(data, cashierWiseTaskReportGridUrl) {
        if (data.isError == true) {
            showError(data.message);
            resetGrid();
        } else {
            $("#flex1").flexOptions({url: cashierWiseTaskReportGridUrl});
            $("#flex1").flexAddData(data.gridOutput);
        }

    }

    function downloadRemittanceSummary() {
        if (checkDates($('#createdDateFrom'), $('#createdDateTo')) == false) return false;



        showLoadingSpinner(true);
        var params = "?formDate=" + $('#createdDateFrom').val() + "&toDate=" + $('#createdDateTo').val();
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadRemittanceSummaryReport')}" + params;
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
                        {display: "Serial", name : "serial", width : 38, sortable : false, align: "right"},
                        {display: "Date", name : "date", width : 100, sortable : false, align: "left"},
                        {display: "Bank Transfer", name : "receive_in_online", width : 100, sortable : false, align: "left"},
                        {display: "Net Collection", name : "total_net_collection", width : 90, sortable : false, align: "left"},
                        {display: "Commission", name : "total_commission", width : 100, sortable : false, align: "left"},
                        {display: "Discount", name : "total_discount", width : 100, sortable : false, align: "left"},
                        {display: "Equivalent", name : "total_net_collection_taka", width : 100, sortable : false, align: "left"},
                        {display: "Total Transactions", name : "total_transactions", width : 100, sortable : false, align: "right"}
                    ],
                    sortname: "cashier",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Remittance Summary',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20,25,30],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    customPopulate:populateGridForListWiseTaskReport,
                    afterAjax: function(XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );

    }
    function populateGridForListWiseTaskReport(data) {
        executePostConditionForLoadGrid(data, cashierWiseTaskReportGridUrl);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No task found');
        }
    }

</script>