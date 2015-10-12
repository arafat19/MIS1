<script type="text/javascript">
    var taskListModel;
    $(document).ready(function() {
        onLoadCanceledTask();
    });

    function onLoadCanceledTask() {
        initializeForm($("#canceledTask"), onSubmitForm);
        initFlex();

        // update page title
        $(document).attr('title', "ARMS - Canceled Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsReport/showForViewCancelTask");
    }
    function initFlex() {
        $("#flex").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "left"},
                    {display: "Ref No", name: "refNo", width: 100, sortable: false, align: "left"},
                    {display: "Mapping Bank, Branch & District Info.", name: "mapping", width: 250, sortable: false, align: "left"},
                    {display: "Process Type", name: "process", width: 86, sortable: false, align: "left"},
                    {display: "Instrument Type", name: "instrument", width: 100, sortable: false, align: "left"},
                    {display: "Payment Method", name: "paymentMethod", width: 100, sortable: false, align: "left"},
                    {display: "Amount(BDT)", name: "amount", width: 82, sortable: false, align: "left"},
                    {display: "Beneficiary Name", name: "beneficiaryName", width: 100, sortable: false, align: "left"},
                    {display: "Revision Note", name: "revisionNote", width: 100, sortable: false, align: "left"}
                ],
                buttons:[
                    {name: 'View Details', bclass: 'detail', onpress: viewDetails},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                ],
                usepager: true,
                singleSelect: false,
                title: 'All Task',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight()-15,
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
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsReport', action: 'listForViewCancelTask')}" + params;
        $("#flex").flexOptions({url: strUrl});
    }

    function onSubmitForm() {
        if (executePreConditionForSearchTask() == false) {
            return false;
        }
        setButtonDisabled($('#search'), true);  // disable the search button
        showLoadingSpinner(true);   // show loading spinner
        var strUrl = "${createLink(controller:'rmsReport',action: 'listForViewCancelTask')}";
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            data: jQuery("#canceledTask").serialize(),
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
    function viewDetails(){
        if (executeCommonPreConditionForSelect($('#flex'), 'task',true) == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        var propertyValue=id;
        var propertyName="id";
        var params="?property_value="+propertyValue+"&property_name="+propertyName;
        var loc = "${createLink(controller: 'rmsTask', action: 'showTaskDetailsWithNote')}" + params;
        $.history.load(formatLink(loc));
        return false;
    }
</script>