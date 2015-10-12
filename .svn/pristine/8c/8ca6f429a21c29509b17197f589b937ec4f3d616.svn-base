<script type="text/javascript">
    var taskListModel, taskDetailsListModel, branchId;
    $(document).ready(function() {
        onLoadForwardUnpaidTask();
    });

    function onLoadForwardUnpaidTask() {
        initializeForm($("#forwardUnpaidTask"), onSubmitForm);
        initFlex();
        initFlexDetails();

        // update page title
        $(document).attr('title', "ARMS - Forwarded Unpaid Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsReport/showForForwardUnpaidTask");
    }

    function initFlex() {
        $("#flex").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    {display: "Mapping Bank Branch & District", name: "mappingInfo", width: 250, sortable: false, align: "left"},
                    {display: "Total Task(s)", name: "totalTasks", width: 100, sortable: false, align: "right"},
                    {display: "Total Amount", name: "amount", width: 100, sortable: false, align: "left"}
                ],
                buttons:[
                    {name: 'View Details', bclass: 'details', onpress: viewDetails},
                    {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid}
                ],
                usepager: true,
                singleSelect: true,
                title: 'Tasks Summary',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 15,
                afterAjax: function () {
                    afterAjaxError();
                    showLoadingSpinner(false);
                },
                customPopulate: populateFlexGrid
            }
        );
    }
    function populateFlexGrid(data) {
        if (data.isError) {
            showError(data.message);
            taskListModel = getEmptyGridModel();
        } else {
            taskListModel = data.gridObj;
        }
        $("#flex").flexAddData(taskListModel);
    }
    function setUrlFlexGrid() {
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var refNo= $('#refNo').val();

        var params = "?refNo=" + refNo + "&fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsReport', action: 'listTaskForForwardUnpaidTask')}" + params;
        $("#flex").flexOptions({url: strUrl});
    }

    function onSubmitForm() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        setButtonDisabled($('#search'), true);  // disable the search button
        showLoadingSpinner(true);   // show loading spinner
        var strUrl = "${createLink(controller:'rmsReport',action: 'listTaskForForwardUnpaidTask')}";
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            data: jQuery("#forwardUnpaidTask").serialize(),
            success: function (data, textStatus) {
                executePostConditionForSearchTask(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
                setButtonDisabled($('#search'), false);
            },
            dataType: 'json',
            type: 'post'
        });
        return false;
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSearchTask() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        return true;
    }
    // execute post condition for search task
    function executePostConditionForSearchTask(result) {
        populateFlexGrid(result);
        setUrlFlexGrid();
    }
    function reloadGrid(){
        $('#flex').flexOptions({query:''}).flexReload();
    }
    function initFlexDetails() {
        $("#flexDetails").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    {display: "Ref No", name: "refNo", width: 100, sortable: false, align: "left"},
                    {display: "Amount", name: "amount", width: 100, sortable: false, align: "left"},
                    {display: "Beneficiary Name", name: "beneficiaryName", width: 100, sortable: false, align: "left"},
                    {display: "Instrument", name: "instrument", width: 100, sortable: false, align: "left"},
                    {display: "Payment Method", name: "paymentMethod", width: 100, sortable: false, align: "left"}
                ],
                buttons:[
                    {name: 'Refresh', bclass: 'clear-results', onpress: reloadGridDetails}
                ],
                usepager: true,
                singleSelect: false,
                title: 'Task Details',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 15,
                afterAjax: function () {
                    afterAjaxError();
                    showLoadingSpinner(false);
                },
                customPopulate: populateFlexGridForTaskDetails
            }
        );
    }
    function reloadGridDetails(){
        $('#flexDetails').flexOptions({query:''}).flexReload();
    }
    function viewDetails(){
        if(executeCommonPreConditionForSelect($('#flex'),'row',true)==false){
            return false;
        }
        branchId= getSelectedIdFromGrid($('#flex'));
        var strUrl = "${createLink(controller:'rmsReport',action: 'listTaskDetailsForForwardedUnpaidTasks')}?branchId=" + branchId;
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            success: executePostConditionForViewDetails,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostConditionForViewDetails(result){
        populateFlexGridForTaskDetails(result);
        setUrlFlexGridForDetails();
    }
    function populateFlexGridForTaskDetails(data) {
        if (data.isError) {
            showError(data.message);
            taskDetailsListModel = getEmptyGridModel();
        } else {
            taskDetailsListModel = data.gridObj;
        }
        $("#flexDetails").flexAddData(taskDetailsListModel);
    }
    function setUrlFlexGridForDetails() {
        var params = "?branchId=" + branchId;
        var strUrl = "${createLink(controller: 'rmsReport', action: 'listTaskDetailsForForwardedUnpaidTasks')}" + params;
        $("#flexDetails").flexOptions({url: strUrl});
    }
</script>