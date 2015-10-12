<script type="text/javascript">
    var validatorSearch, dropDownProject;

    jQuery(function ($) {
        $('#printBudgetFinancialSummary').click(function () {
            printBudgetFinancialSummary();
            return false;
        });

        $('#printBudgetFinancialCsvSummary').click(function () {
            printBudgetFinancialCsvSummary();
            return false;
        });

        $("#flex1").click(function () {
            populateBudgetFinancialSummaryListGrid();
        });
        onLoadBudgetFinancialSummary();
    });

    function onLoadBudgetFinancialSummary() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getBudgetFinancialSummary);
        initFlexGrid();
        $('.download_icon_set').hide();

        // update page title
        $(document).attr('title', "MIS - BudgetFinancialSummary");
        loadNumberedMenu(MENU_ID_QS, "#qsReport/showBudgetFinancialSummary");
    }

    function printBudgetFinancialSummary() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        var isGovtQs = $('#hidIsGovtQs').attr('value');
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        if (confirm('Do you want to download the Budget Financial Summary now?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadBudgetFinancialSummary')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printBudgetFinancialCsvSummary() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hidProjectId').attr('value');
        var isGovtQs = $('#hidIsGovtQs').attr('value');
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        if (confirm('Do you want to download the Budget Financial CSV Summary now?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadBudgetFinancialCsvSummary')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function updateFromDate() {
        var fromDate = dropDownProject.dataItem().createdon;
        $("#fromDate").val(fromDate);
    }

    function executePreConditionToGetBudgetFinancialSummary() {
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

    function getBudgetFinancialSummary() {
        if (executePreConditionToGetBudgetFinancialSummary() == false) {
            return false;
        }
        if (validateForm($("#searchForm"))) {
        }
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        var isGovtQs = $('#isGovtQs').attr('checked');
        isGovtQs = isGovtQs ? true : false;
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'qsReport', action: 'listBudgetFinancialSummary')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs,
            success: executePostConditionBudgetFinancialSummary,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionBudgetFinancialSummary(data) {
        // populateBudgetPo(data)
        populateBudgetFinancialSummary(data);
        return false;
    }

    function populateBudgetFinancialSummary(data) {
        $("#lblBudgetDetails").html('');
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var isGovtQs = data.isGovtQs;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        $('#hidIsGovtQs').val(isGovtQs);
        var qsMeasurementUrl = "${createLink(controller:'qsReport', action: 'listBudgetFinancialSummary')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        doGridEmpty();
        emptyPOGrid();
        $("#flex1").flexOptions({url: qsMeasurementUrl});
        onLoadBudgetFinancialSummaryListJSON(data);
    }

    function populateBudgetFinancialSummaryListGrid() {
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
                        {display: "Details", name: "budget_details", width: 120, sortable: false, align: "left", hide: true},
                        {display: "Budget Quantity", name: "budget_quantity", width: 120, sortable: false, align: "right", hide: false},
                        {display: "Unit", name: "unit", width: 60, sortable: false, align: "left"},
                        {display: "Gross Receivables", name: "gross_receivables", width: 200, sortable: false, align: "right"},
                        {display: "Net Receivables", name: "net_receivables", width: 200, sortable: false, align: "right"},
                        {display: "WT% of Contract Completed", name: "wt_of_contract_completed", width: 200, sortable: false, align: "right"}
                    ],
                    sortname: "budget_item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Financial Summary List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - $("#divBudgetFinancialSummary").height() - 70,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadBudgetFinancialSummaryListJSON
                }
        );
    }

    function onLoadBudgetFinancialSummaryListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flex1").flexAddData(data.financialSummaryList);
        $('.download_icon_set').show();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget Financial Summary found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>