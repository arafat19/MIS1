<table id="flex" style="display:none"></table>

<div class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            All Unresolved Task List
        </div>
    </div>

    <div>
        <table id="flex2" style="display:none"></table>
    </div>

</div>

<script type="text/javascript">
    var transactionDayListModel = false;
    var taskListModel = false;

    $(document).ready(function () {
        onLoadTransactionDay();
    });

    function onLoadTransactionDay() {
        initFlex();
        initFlex2();
        populateFlex();
        populateFlex2();
        $(document).attr('title', "ARMS - Transaction Day");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTransactionDay/show");
    }

    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "left"},
                        {display: "Transaction Date", name: "id", width: 120, sortable: false, align: "left"},
                        {display: "Opened By", name: "name", width: 120, sortable: false, align: "left"},
                        {display: "Opened On", name: "code", width: 180, sortable: false, align: "left"},
                        {display: "Closed By", name: "countryId", width: 120, sortable: false, align: "left"},
                        {display: "Closed On", name: "countryId", width: 180, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Open Tr. Day', bclass: 'open', onpress: openTransactionDay},
                        {name: 'Close', bclass: 'delete', onpress: closeTransactionDay},
                        {name: 'Re-Open', bclass: 'reOpen', onpress: reOpenTransactionDay},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    sortname: "transactionDate",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Transaction Days',
                    useRp: true,
                    rpOptions: [15, 25, 30, 50],
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() / 2,
                    afterAjax: function () {
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSONForTransactionDay
                }
        );
    }

    function initFlex2() {
        $("#flex2").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "left"},
                        {display: "Task List Name", name: "taskListName", width: 140, sortable: false, align: "left"},
                        {display: "Exchange House", name: "exchangeHouseName", width: 300, sortable: false, align: "left"},
                        {display: "Total Task", name: "totalTask", width: 80, sortable: false, align: "right"},
                        {display: "Included In List", name: "includedInListCount", width: 100, sortable: false, align: "right"},
                        {display: "Decision Taken", name: "decisionTakenCount", width: 100, sortable: false, align: "right"},
                        {display: "Decision Approved", name: "decisionApprovedCount", width: 120, sortable: false, align: "right"},
                        {display: "Created On", name: "createdOn", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGridForTaskList}
                    ],
                    sortname: "taskListId",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    useRp: true,
                    rpOptions: [10, 15, 25],
                    rp: 10,
                    showTableToggleBtn: false,
                    height: getGridHeight() / 2 - 100,
                    afterAjax: function () {
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSONForTaskList
                }
        );
    }

    function onLoadListJSONForTransactionDay(data) {
        if (data.isError) {
            showError(data.message);
            transactionDayListModel = getEmptyGridModel();
        } else {
            transactionDayListModel = data;
        }
        $("#flex").flexAddData(transactionDayListModel);
    }

    function onLoadListJSONForTaskList(data) {
        if (data.isError) {
            showError(data.message);
            taskListModel = getEmptyGridModel();
        } else {
            taskListModel = data;
        }
        $("#flex2").flexAddData(taskListModel);
    }

    function populateFlex() {
        var strUrl = "${createLink(controller:'rmsTransactionDay',action:  'list')}";
        $("#flex").flexOptions({url: strUrl});
        $('#flex').flexOptions({query: ''}).flexReload();
    }

    function populateFlex2() {
        var strUrl = "${createLink(controller:'rmsTaskListSummaryModel',action:  'listUnResolvedTaskList')}";
        $("#flex2").flexOptions({url: strUrl});
        $('#flex2').flexOptions({query: ''}).flexReload();
    }

    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    function reloadGridForTaskList(com, grid) {
        $('#flex2').flexOptions({query: ''}).flexReload();
    }

    function getMonth(month) {
        var ar = new Array("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        return ar[month];
    }
    function executePreConditionForOpenTransactionDay() {
        var date = new Date();
        var currDate = date.getDate();
        var monthName = getMonth(date.getMonth());
        var year = date.getFullYear();
        var todayDate = currDate + ' ' + monthName + ',' + year;
        if (!confirm('Are you sure you want to open transaction day for ' + todayDate + '?')) {
            return false;
        }
    }
    function openTransactionDay() {
        if (executePreConditionForOpenTransactionDay() == false) {
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'rmsTransactionDay', action: 'openTransactionDay')}",
            success: executePostConditionForOpen,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForOpen(result) {
        if (result.isError) {
            showError(result.message);              // show error message in case of error
        }
        else {
            try {
                var newEntry = result;
                if (newEntry.entity != null) {      // show newly created object in a grid row
                    var previousTotal = parseInt(transactionDayListModel.total);
                    var firstSerial = 1;

                    if (transactionDayListModel.rows.length > 0) {
                        firstSerial = transactionDayListModel.rows[0].cell[0];
                        regenerateSerial($(transactionDayListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    transactionDayListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        transactionDayListModel.rows.pop();
                    }

                    transactionDayListModel.total = ++previousTotal;
                    $("#flex").flexAddData(transactionDayListModel);

                }
                showSuccess(result.message);        // show success message

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function closeTransactionDay() {
        if (executeCommonPreConditionForSelect($('#flex'), 'transaction day') == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'rmsTransactionDay', action: 'closeTransactionDay')}?id=" + id,
            success: executePostConditionForClosingDay,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForClosingDay(result) {
        if (result.isError) {
            showError(result.message);
        }
        else {
            try {
                if (result.entity != null) {
                    updateListModel(transactionDayListModel, result.entity, 0);
                    $("#flex").flexAddData(transactionDayListModel);
                }
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function reOpenTransactionDay() {
        if (executeCommonPreConditionForSelect($('#flex'), 'transaction day') == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'rmsTransactionDay', action: 'reOpenTransactionDay')}?id=" + id,
            success: executePostConditionForReOpen,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForReOpen(result) {
        if (result.isError) {
            showError(result.message);
        }
        else {
            try {
                if (result.entity != null) {
                    updateListModel(transactionDayListModel, result.entity, 0);
                    $("#flex").flexAddData(transactionDayListModel);
                }
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }
</script>