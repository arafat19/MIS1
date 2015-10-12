<script type="text/javascript">
    var process, instrument;
    var dropDownExchangeHouse, dropDownTaskList;
    var taskListModel = false, taskStatusDisbursed;

    $(document).ready(function() {
        onLoadForwardCashCollectionPage();
    });
    // method called on page load
    function onLoadForwardCashCollectionPage() {
        initializeForm($("#filterPanelForwardCashCollectionForm"), onSubmitForwardCashCollectionFilterPanel);
        process = $("#hidProcessType").val();
        instrument = $("#hidInstrumentType").val();
        taskStatusDisbursed = $("#hidTaskStatusDisbursed").val();
        $('#printCsvBtn').click(function () {
            downloadForwardCashCollection();
        });

        initFlex();

        // update page title
        $(document).attr('title', "ARMS - Forward Cash Collection");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsInstrument/showForForwardCashCollection");
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

    // method called  on submit of the filter panel form
    function onSubmitForwardCashCollectionFilterPanel() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var taskListId = dropDownTaskList.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var currentStatus = taskStatusDisbursed;
        var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&currentStatus=" + currentStatus + "&process=" + process + "&instrument=" + instrument;
        var strUrl = "${createLink(controller: 'rmsInstrument', action: 'listTaskForProcessInstrument')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex').flexOptions({url:strUrl, query:''}).flexReload();
        return false;
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSearchTask() {
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
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    {display: "Id", name: "id", width: 40, sortable: false, align: "right", hide: true},
                    {display: "Ref No", name: "refNo", width: 120, sortable: false, align: "left"},
                    {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                    {display: "Created Date", name: "createdOn", width: 100, sortable: false, align: "left"},
                    {display: "Beneficiary Name", name: "beneficiaryName", width: 200, sortable: false, align: "left"},
                    {display: "Mapping Bank,Branch & District", name: "mapping", width: 280, sortable: false, align: "left"}
                ],
                buttons: [
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                ],
                searchitems: [
                    {display: "Ref No", name: "refNo", width: 180, sortable: true, align: "left"},
                    {display: "Beneficiary Name", name: "beneficiaryName", width: 180, sortable: true, align: "left"}
                ],
                sortname: "refNo",
                sortorder: "asc",
                usepager: true,
                singleSelect: false,
                title: 'All Task of Forward Cash Collection',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight(),
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
            taskListModel = getEmptyGridModel();
        } else {
            taskListModel = data.gridObj;
        }
        $("#flex").flexAddData(taskListModel);
    }

    // method call for download
    function downloadForwardCashCollection() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the CSV now?')) {
            var exhHouseId = dropDownExchangeHouse.value();
            var taskListId = dropDownTaskList.value();
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&fromDate=" + fromDate + "&toDate=" + toDate;
            var url = "${createLink(controller: 'rmsInstrument', action: 'downloadTaskReportForForwardCashCollection')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }
    function reloadGrid(){
        $('#flex').flexOptions({query:''}).flexReload();
    }
</script>