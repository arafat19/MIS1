<script type="text/javascript">
    var output,localCurrencyName,taskGridModel;

    $(document).ready(function () {
        onLoadInProgressTaskForCustomer();
    });

    function onLoadInProgressTaskForCustomer() {
        output =${output ? output : ''};

        if (output.isError) {
            showError(data.message);
        } else {
            taskGridModel = output.taskObj;
            localCurrencyName = output.localCurrencyName;
        }
        initFlexigrid();
        populateFlexGrid();

        // update page title
        $('span.headingText').html('Show Approved Task');
        $('#icon_box').attr('class', 'pre-icon-header manage-task-list');
        $(document).attr('title', "ARMS(Agent) - Show Approved Task");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showApprovedTaskForCustomer");
    }

    function initFlexigrid() {

        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 45, sortable: false, align: "right"},
                        {display: "Ref No", name: "refNo", width: 100, sortable: true, align: "left"},
                        {display: "Date", name: "createdOn", width: 170, sortable: false, align: "left"},
                        {display: "Amount(" + localCurrencyName + ")", name: "amountInLocalCurrency", width: 100, sortable: true, align: "right"},
                        {display: "Amount(BDT)", name: "amountInForeignCurrency", width: 100, sortable: true, align: "right"},
                        {display: "Total Due", name: "total_due", width: 75, sortable: false, align: "right"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 150, sortable: true, align: "left"},
                        {display: "Payment Method", name: "paymentMethod", width: 105, sortable: false, align: "left"},
                        {display: "Status", name: "taskStatus", width: 170, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Show Invoice', bclass: 'rename', onpress: viewInvoice},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],

                    sortname: "createdOn",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Approved Remittance',
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
        if(executeCommonPreConditionForSelect($('#flex1'))==false){
            return;
        }
        var taskId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhReport', action: 'showInvoiceFromGridForCustomer')}?taskId=" + taskId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller: 'exhTask', action: 'listApprovedTaskForCustomer')}";
        $("#flex1").flexOptions({url: strUrl});
        if (taskGridModel) {
            $("#flex1").flexAddData(taskGridModel);
            $('div.mDiv > div.ftitle').text('All Approved Remittance of ' + output.customerName);
        }
    }

</script>
<table id="flex1" style="display:none"></table>