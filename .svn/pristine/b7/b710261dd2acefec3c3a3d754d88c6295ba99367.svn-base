<script type="text/javascript">
    var dropDownExchangeHouse, dropDownTaskList;
    var currentStatus;

    $(document).ready(function () {
        onLoadTaskListPlanPage();
    });

    function onLoadTaskListPlanPage() {
        dropDownTaskList = initKendoDropdown($('#taskListId'), null, null, null);
        currentStatus = $('#currentStatus').val();
        $(document).attr('title', "ARMS - Task List Plan");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsReport/showTaskListPlan");
    }

    function executePreConditionForSubmit() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        if(isEmpty(dropDownExchangeHouse.value())) {
            showError("Please select Exchange House");
            return false;
        }
        if(isEmpty(dropDownTaskList.value())) {
            showError("Please select Task List");
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }

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

    function populateTaskList() {
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

</script>