<script type="text/javascript">
    var taskGridModel,localCurrencyName,output;

    $(document).ready(function () {
        onLoadInPaidTaskForCustomer();
    });

    function onLoadInPaidTaskForCustomer() {

        output =${output ? output : ''};

        if (output.isError) {
            showError(data.message);
        } else {
            taskGridModel = output.taskObj;
            localCurrencyName = output.localCurrencyName;
        }
        initFlexGrid();
        loadFlexGrid();

        // update page title
        $('span.headingText').html('Show Disbursed Task');
        $('#icon_box').attr('class', 'pre-icon-header manage-task-list');
        $(document).attr('title', "ARMS(Agent) - Show Disbursed Task");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showDisbursedTaskForCustomer");
    }

    function initFlexGrid() {

        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 45, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Status", name: "currentStatus", width: 10, sortable: false, align: "right", hide: true},
                        {display: "Date", name: "createdOn", width: 170, sortable: false, align: "left"},
                        {display: "Ref No", name: "refNo", width: 100, sortable: true, align: "left"},
                        {display: "Amount(BDT)", name: "amountInForeignCurrency", width: 100, sortable: true, align: "right"},
                        {display: "Amount("+ localCurrencyName + ")", name: "amountInLocalCurrency", width: 100, sortable: true, align: "right"},
                        {display: "Total Due", name: "total_due", width: 95, sortable: false, align: "right"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 150, sortable: true, align: "left"},
                        {display: "Payment Method", name: "paymentMethod", width: 105, sortable: false, align: "left"},
                        {display: "Pin No", name: "pinNo", width: 125, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Show Task Status', bclass: 'do_search', onpress: viewTaskStatus},
                        {name: 'Show Invoice', bclass: 'rename', onpress: viewInvoice},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "createdOn",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Disbursed Remittance',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    //width: 725,
                    height: getGridHeight(),
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: customPopulateGrid
                }
        );
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            taskGridModel = getEmptyGridModel();
        } else {
            taskGridModel = data.gridObj;
        }
        $("#flex1").flexAddData(taskGridModel);
        return false;
    }


    function viewInvoice(com, grid) {
        if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
        var taskId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhReport', action: 'showInvoiceFromGridForCustomer')}?taskId=" + taskId;
        $.history.load(formatLink(loc));
        return false;
    }


    // view task details
    function viewTaskStatus(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
        var ids = $('.trSelected', grid);
        var taskId = $(ids[ids.length - 1]).find("td").eq(1).find("div").html();

        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhTask', action: 'searchTaskWithRefOrPinForCustomer')}?taskId=" + taskId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'exhTask', action: 'listDisbursedTaskForCustomer')}";
        $("#flex1").flexOptions({url: strUrl});
        if (taskGridModel) {
            $("#flex1").flexAddData(taskGridModel);
            $('div.mDiv > div.ftitle').text('All Disbursed Remittance of ' + output.customerName);
        }
    }

</script>
<table id="flex1" style="display:none"></table>