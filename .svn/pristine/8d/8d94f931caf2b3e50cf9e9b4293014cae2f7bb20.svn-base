<script type="text/javascript">
    var validatorSearch, dropDownProject;

    jQuery(function ($) {
        $('#printBudgetQs').click(function () {
            printBudgetQs();
            return false;
        });
        $('#printBudgetWiseQsCsv').click(function () {
            printBudgetQsCsv();
            return false;
        });
        $("#flex1").click(function () {
            populateBudgetWiseQsListGrid();
        });

        onLoadBudgetPo();
    });

    function printBudgetQs() {
        var confirmMsg = 'Do you want to download the Budget wise Qs report in pdf format?';

        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate Budget Qs then click print');
            return false;
        }

        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        var isGovtQs = $('#hidIsGovtQs').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'qsReport', action: 'downloadBudgetWiseQs')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function printBudgetQsCsv() {
        var confirmMsg = 'Do you want to download the Budget wise Qs report in csv format?';

        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate Budget Qs then click print');
            return false;
        }

        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        var isGovtQs = $('#hidIsGovtQs').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'qsReport', action: 'downloadBudgetWiseQsCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function onLoadBudgetPo() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getBudgetWiseQs);

        initFlexGrid();
        $('.download_icon_set').hide();

        // update page title
        $(document).attr('title', "MIS - Budget Wise QS");
        loadNumberedMenu(MENU_ID_QS, "#qsReport/showBudgetWiseQs");
    }

    function updateFromDate() {
        var projectFromDate = dropDownProject.dataItem().createdon;
        $("#fromDate").val(projectFromDate);
    }

    function executePreConditionToGetBudgetWiseQs() {
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

    function getBudgetWiseQs() {
        if (executePreConditionToGetBudgetWiseQs() == false) {
            return false;
        }

        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        var isGovtQs = $('#isGovtQs').attr('checked');
        isGovtQs = isGovtQs ? true : false;
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'qsReport', action: 'listBudgetWiseQs')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs,
            success: executePostConditionBudgetWiseQs,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionBudgetWiseQs(data) {
        // populateBudgetPo(data)
        populateBudgetWiseQsGrid(data);

        return false;
    }

    function populateBudgetPo(result) {
        $('#hidProjectId').val(result.projectId);
        $('#hidFromDate').val(result.fromDate);
        $('#hidToDate').val(result.toDate);
        $("#fromDate").val(result.fromDate);
        $("#toDate").val(result.toDate);
    }

    function resetBudgetForm() {
        doGridEmpty();
        $('#hidFromDate').val('');
        $('#hidToDate').val('');
        $('#hidProjectId').val('');
    }

    function populateBudgetWiseQsGrid(data) {
        $('.download_icon_set').show();
        $("#lblBudgetDetails").html('');
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var isGovtQs = data.isGovtQs;
        $('#hidFromDate').val(fromDate);
        $('#hidToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        $('#hidIsGovtQs').val(isGovtQs);
        var budgetWiseQsUrl = "${createLink(controller:'qsReport', action: 'listBudgetWiseQs')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        doGridEmpty();
        $("#flex1").flexOptions({url: budgetWiseQsUrl});
        onLoadBudgetPoListJSON(data);
    }

    function populateBudgetWiseQsListGrid() {
        var ids = $('.trSelected', $('#flex1'));
        if (ids.length == 0) {
            return false;
        }
        //show budget details
        var budgetDetails = $(ids[ids.length - 1]).find('td').eq(9).find('div').text();
        $("#lblBudgetDetails").html(budgetDetails);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: false},
                    {display: "Line Item", name: "budget_item", width: 100, sortable: false, align: "left"},
                    {display: "Quantity", name: "budget_quantity_unit", width: 100, sortable: false, align: "right"},
                    {display: "Certified", name: "work_completed", width: 100, sortable: false, align: "right"},
                    {display: "Remaining", name: "work_remaining", width: 100, sortable: false, align: "right"},
                    {display: "Achieved(%)", name: "work_achieved_in_percent", width: 100, sortable: false, align: "right"},
                    {display: "Remaining(%)", name: "work_remaining_in_percent", width: 110, sortable: false, align: "right"},
                    {display: "Gross Receivables", name: "gross_receivables", width: 130, sortable: false, align: "right"},
                    {display: "Net Receivables", name: "net_receivables", width: 130, sortable: false, align: "right"},
                    {display: "Budget Details", name: "budget_details", width: 120, sortable: false, align: "left", hide: true}
                ],
                sortname: "b.budget_item",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'Budget Wise QS List',
                useRp: true,
                rp: 20,
                rpOptions: [10, 20, 25, 30],
                showTableToggleBtn: false,
                height: getGridHeight() - $("#divBudgetWiseQs").height() - 70,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                    checkGrid();
                },
                customPopulate: onLoadBudgetPoListJSON
            }
        );
    }

    function onLoadBudgetPoListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flex1").flexAddData(data.budgetWiseQsList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget Wise Qs found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>