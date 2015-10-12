<script type="text/javascript">
    var dropDownProject;

    jQuery(function ($) {
        $('#printBudgetContractDetails').click(function () {
            printBudgetContractDetails();
            return false;
        });

        $('#printBudgetContractCsvDetails').click(function () {
            printBudgetContractCsvDetails();
            return false;
        });

        $("#flex1").click(function () {
            populateBudgetContractDetailsListGrid();
        });
        onLoadBudgetContractDetails();
    });

    function onLoadBudgetContractDetails() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getBudgetContractDetails);
        try {   // Model will be supplied to grid on load _list.gsp
            initFlexGrid();
        } catch (e) {
            showError(e.message);
        }
        $('.download_icon_set').hide();

        // update page title
        $(document).attr('title', "MIS - BudgetContractDetails");
        loadNumberedMenu(MENU_ID_QS, "#qsReport/showBudgetContractDetails");
    }

    function printBudgetContractDetails() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the Budget Contract Details now?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadBudgetContractDetails')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printBudgetContractCsvDetails() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the Budget Contract Details in Csv format now?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadBudgetContractCsvDetails')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function updateFromDate() {
        $("#fromDate").val(dropDownProject.dataItem().createdon);
    }

    function executePreCondition() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            showError("Please select a Project");
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        return true;
    }

    function getBudgetContractDetails() {
        if (executePreCondition() == false) {
            return false;
        }
        if (validateForm($("#searchForm"))) {
        }
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'qsReport', action: 'listBudgetContractDetails')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate,
            success: executePostConditionBudgetContractDetails,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionBudgetContractDetails(data) {
        // populateBudgetPo(data)
        populateBudgetContractDetails(data);
        return false;
    }

    function populateBudgetContractDetails(data) {
        $("#lblBudgetDetails").html('');
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        var qsMeasurementUrl = "${createLink(controller:'qsReport', action: 'listBudgetContractDetails')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        doGridEmpty();
        emptyPOGrid();
        $("#flex1").flexOptions({url: qsMeasurementUrl});
        onLoadBudgetContractDetailsListJSON(data);
    }

    function populateBudgetContractDetailsListGrid() {
        var ids = $('.trSelected', $('#flex1'));
        if (ids.length == 0) {
            return false;
        }
        //show budget details
        var budgetDetails = $(ids[ids.length - 1]).find('td').eq(2).find('div').text();
        $("#lblBudgetDetails").html(budgetDetails);

    }

    function emptyPOGrid() {
        var emptyModel = getEmptyGridModel();
        $("#flexForPurchaseOrderList").flexOptions({url: false});
        $("#flexForPurchaseOrderList").flexAddData(emptyModel);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: false},
                        {display: "BOQ Item No", name: "budget_item", width: 100, sortable: false, align: "left"},
                        {display: "Budget Details", name: "budget_details", width: 120, sortable: false, align: "left", hide: true},
                        {display: "Budget Quantity", name: "budget_quantity", width: 120, sortable: false, align: "left", hide: true},
                        {display: "Unit", name: "unit", width: 40, sortable: false, align: "left"},
                        {display: "Contract Rate", name: "contract_rate", width: 100, sortable: false, align: "right"},
                        {display: "Net Rate Of Item", name: "net_rate_of_item", width: 100, sortable: false, align: "right"},
                        {display: "Gross Value Of Item", name: "gross_value_of_item", width: 120, sortable: false, align: "right"},
                        {display: "Net Value Of Item", name: "net_value_of_item", width: 100, sortable: false, align: "right"},
                        {display: "WT(%) Of Item", name: "wt_of_item", width: 100, sortable: false, align: "right"}
                    ],
                    sortname: "budget_item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Budget Contract Details List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - $("#divBudgetContractDetails").height() - 70,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadBudgetContractDetailsListJSON
                }
        );
    }

    function onLoadBudgetContractDetailsListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flex1").flexAddData(data.contractDetailsList);
        $('.download_icon_set').show();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget Contract Details found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>