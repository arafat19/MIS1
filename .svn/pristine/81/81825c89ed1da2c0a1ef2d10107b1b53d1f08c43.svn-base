<script type="text/javascript">
    var cashierWiseTaskReportGridUrl;
    var modelJsonForCashierWiseTask = ${modelJson};

    $(document).ready(function () {
        onLoadCashierWiseReportAdmin();
    });

    function onLoadCashierWiseReportAdmin() {

        $('#printPdfBtn').click(function() {
            downloadCashierTaskReport();
        });

        initializeForm($('#cashierWiseReportForAdmin'), loadGridForCashierWiseTaskReport);

        var isError = modelJsonForCashierWiseTask.isError;
        if (isError=='true') {
            var message = modelJsonForCashierWiseTask.message;
            showError(message);
            return;
        }

        $('#userCashierId').val(modelJsonForCashierWiseTask.userCashierId);

        initFlex();

        $(document).attr('title', "ARMS - Cashier Wise Task Report");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showCashierWiseReportForCashier");

    }

    function loadGridForCashierWiseTaskReport() {

        if (checkDates($('#createdDateFrom'), $('#createdDateTo')) == false) return false;
        var params =  "?formDate=" + $('#createdDateFrom').val() + "&toDate=" + $('#createdDateTo').val();


        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        resetGrid();
        cashierWiseTaskReportGridUrl = "${createLink(controller: 'exhReport', action: 'listCashierWiseReportForCashier')}" + params;
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
            var gridTitle = "Cashier wise task report : [ Total Due (" + $('#hidLocalCurrency').val() + ") : " + data.summaryDue +" ]";
            //$('#divFundTransferInfo').html(data.transferInfoOutput);
            $('div.mDiv > div.ftitle').text(gridTitle);
        }
    }

    function downloadCashierTaskReport() {
        if (checkDates($('#createdDateFrom'), $('#createdDateTo')) == false) return false;
        var params = "?cashierIds=" + $('#userCashierId').val()+ "&formDate=" + $('#createdDateFrom').val() + "&toDate=" + $('#createdDateTo').val();
        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadCashierWiseTaskReport')}" +params;
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
                       {display: "Date", name : "date", width : 75, sortable : false, align: "left"},
                       {display: "Ref No", name : "ref_no", width : 80, sortable : false, align: "left"},
                       {display: "Tr. Type", name : "transaction_type", width : 80, sortable : false, align: "left"},
                       {display: "Customer Name", name : "customer_name", width : 120, sortable : false, align: "left"},
                       {display: "Beneficiary Name", name : "beneficiary", width : 120, sortable : false, align: "left"},
                       {display: "Taka Equivalent", name : "taka_equivalent", width : 100, sortable : false, align: "right"},
                       {display: "Rate", name : "takarate", width : 60, sortable : false, align: "right"},
                       {display: "Remittance " + $('#hidLocalCurrency').val(), name : "remittance_stg", width : 100, sortable : false, align: "right"},
                       {display: "Commission", name : "commission", width : 70, sortable : false, align: "right"},
                       {display: "Discount", name : "discount", width : 60, sortable : false, align: "right"},
                       {display: "Received in Till", name : "received_in_till", width : 100, sortable : false, align: "right"},
                       {display: "Received by Card", name : "received_by_card", width : 100, sortable : false, align: "right"},
                       {display: "Received Online", name : "received_online", width : 100, sortable : false, align: "right"}
                   ],
                   sortname: "cashier",
                   sortorder: "asc",
                   usepager: true,
                   singleSelect: true,
                   title: 'Cashier Wise Task Report',
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