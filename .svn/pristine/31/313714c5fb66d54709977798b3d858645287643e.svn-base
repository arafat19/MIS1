<script type="text/javascript">
    var taskListModel = false;

    $(document).ready(function() {
        onLoadSearchTaskListPage();
    });
    // method called on page load
    function onLoadSearchTaskListPage() {
        initializeForm($("#searchTaskListForm"), onSubmitSearchTaskList);

        taskListModel = false;

        initFlex();

        // update page title
        $(document).attr('title', "ARMS - Search Task List");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTaskList/showSearchTaskList");
    }
    // method called  on submit of the filter panel form
    function onSubmitSearchTaskList() {
        if (executePreConditionForSearchTaskList() == false) {
            return false;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsTaskList', action: 'listSearchTaskList')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex').flexOptions({url:strUrl, query:''}).flexReload();
        return false;
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSearchTaskList() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
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
                    {display: "Name", name: "name", width: 200, sortable: false, align: "left"},
                    {display: "Created Date", name: "createdOn", width: 100, sortable: false, align: "left"},
                    {display: "Exchange House", name: "exchangeHouse", width: 250, sortable: false, align: "left"},
                    {display: "Total Task", name: "totalTask", width: 80, sortable: false, align: "right"},
                    {display: "Total Amount", name: "totalAmount", width: 120, sortable: false, align: "right"}
                ],
                searchitems: [
                    {display: "Name", name: "task_list.name", width: 180, sortable: true, align: "left"},
                    {display: "Exchange House", name: "exchange_house.name", width: 180, sortable: true, align: "left"}
                ],
                sortname: "name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'Task List',
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



</script>