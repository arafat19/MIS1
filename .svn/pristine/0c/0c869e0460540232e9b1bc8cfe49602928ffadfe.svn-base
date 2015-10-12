<script type="text/javascript">
    var dropDownExchangeHouse, dropDownTaskList;
    var lstTaskModel = false;

    $(document).ready(function() {
        onLoadListWiseStatusPage();
    });
    // method called on page load
    function onLoadListWiseStatusPage() {
        initializeForm($("#filterPanelListWiseStatusForm"), onSubmitListWiseStatusFilterPanel);

        $('#printPdfBtn').click(function () {
            downloadListWiseStatusReport();
        });

        initFlex();

        // update page title
        $(document).attr('title', "ARMS - List Wise Status Report");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsReport/showForListWiseStatusReport");
    }
    // Populate exchange house list onClick of exchange house refresh button
    function populateExchangeHouse() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        $('#exhHouseId').attr('from_date', fromDate);
        $('#exhHouseId').attr('to_date', toDate);
        $('#exhHouseId').reloadMe();
        dropDownTaskList.setDataSource(getKendoEmptyDataSource());
        dropDownTaskList.refresh();
    }
    // Populate task list onchange of exchange house dropdown
    function populateTaskList(){
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        $('#taskListId').attr('from_date', fromDate);
        $('#taskListId').attr('to_date', toDate);
        $('#taskListId').attr('exchange_house_id', exhHouseId);
        $('#taskListId').reloadMe();
    }

    // check pre condition before populate task list
    function executePreConditionForPopulateDropdown() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        return true;
    }

    // method called  on submit of the filter panel form
    function onSubmitListWiseStatusFilterPanel() {
        if (executePreConditionForSubmitFilterPanel() == false) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var taskListId = dropDownTaskList.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsReport', action: 'listForListWiseStatusReport')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex').flexOptions({url:strUrl, query:''}).flexReload();
        return false;
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSubmitFilterPanel() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        if (dropDownExchangeHouse.value() == '') {
            showError('Please select exchange house.');
            return false;
        }
        if (dropDownTaskList.value() == '') {
            showError('Please select task list.');
            return false;
        }
        return true;
    }
    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Task Status", name: "taskStatus", width: 150, sortable: false, align: "left"},
                    {display: "Total Task", name: "totalTask", width: 100, sortable: false, align: "right"},
                    {display: "Total Amount", name: "totalAmount", width: 100, sortable: false, align: "right"}
                ],
                sortname: "refNo",
                sortorder: "asc",
                usepager: false,
                singleSelect: false,
                title: 'List wise status',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() + 30,
                afterAjax: function () {
                    afterAjaxError();
                    showLoadingSpinner(false);
                },
                customPopulate: onLoadListJSON
            }
        );
    }
    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            lstTaskModel = getEmptyGridModel();
        } else {
            lstTaskModel = data.gridObj;
        }
        $("#flex").flexAddData(lstTaskModel);
    }

    // method call for download
    function downloadListWiseStatusReport() {
        if (executePreConditionForSubmitFilterPanel() == false) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the pdf now?')) {
            var exhHouseId = dropDownExchangeHouse.value();
            var taskListId = dropDownTaskList.value();
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate;
            var url = "${createLink(controller: 'rmsReport', action: 'downloadListWiseStatusReport')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }


</script>