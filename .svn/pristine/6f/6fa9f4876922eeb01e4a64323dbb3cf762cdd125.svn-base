<script type="text/javascript">
    var modelJsonForProjectWiseExpenses, dropDownProject, dropDownAccGroup, dropDownCOA;
    var gridModelCoa, fromDateOnChange;

    jQuery(function ($) {
        modelJsonForProjectWiseExpenses = ${modelJson};
        $('#printProjectWiseExpensePdf').click(function () {
            printProjectWiseExpense('pdf');
            return false;
        });
        $('#printProjectWiseExpenseXls').click(function () {
            printProjectWiseExpense('xls');
            return false;
        });
        $('#printProjectWiseExpenseCsv').click(function () {
            printProjectWiseExpenseCsv();
            return false;
        });
        $("#flex1").click(function () {
            populateProjectWiseExpenseDetails();
        });
        onLoadProjectGetExpense();
    });

    function printProjectWiseExpense(formatType) {
        var confirmMsg;
        if (formatType == 'pdf') {
            confirmMsg = 'Do you want to download the project wise expense in pdf format?';
        }
        var projectId = $('#projectId').attr('value');
        var fromDate = $('#hidFromDate').val();
        var toDate = $('#hidToDate').val();
        var coaId = $('#hidCoaId').val();
        var accGroupId = $('#hidAccGroupId').val();
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&coaId=" + coaId + "&accGroupId=" + accGroupId + "&formatType=" + formatType;

        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadProjectWiseExpense')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printProjectWiseExpenseCsv() {
        var confirmMsg = 'Do you want to download the project wise expense in csv format?';

        var projectId = $('#projectId').attr('value');
        var fromDate = $('#hidFromDate').val();
        var toDate = $('#hidToDate').val();
        var coaId = $('#hidCoaId').val();
        var accGroupId = $('#hidAccGroupId').val();
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&coaId=" + coaId + "&accGroupId=" + accGroupId;

        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadProjectWiseExpenseCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadProjectGetExpense() {
        initFlexGrid();
        initializeForm($("#searchForm"), getProjectWiseExpense);
        dropDownCOA = initKendoDropdown($('#coaId'), null, null, getKendoEmptyDataSource(dropDownCOA, "ALL"));
        $('.download_icon_set').hide();

        fromDateOnChange = $('#fromDate').attr('value');
        // update page title
        $(document).attr('title', "MIS - Project Wise Expenses");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showProjectWiseExpense");
    }

    function updateFromDate() {
        if (dropDownProject.value() == '') {
            $("#fromDate").val(fromDateOnChange);
        } else {
            var fromDate = dropDownProject.dataItem().createdon;
            $("#fromDate").val(fromDate);
        }
    }

    function executePreConditionToGetProjectWiseExpense() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function getProjectWiseExpense() {
        resetSearchForm();
        if (executePreConditionToGetProjectWiseExpense() == false) {
            return false;
        }
        $('.download_icon_set').hide();
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        var coaId = dropDownCOA.value();
        var accGroupId = dropDownAccGroup.value();
        var actionListUrl = "${createLink(controller:'accReport', action: 'listProjectWiseExpense')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&coaId=" + coaId + "&accGroupId=" + accGroupId;
        showLoadingSpinner(true);
        $.ajax({
            url: actionListUrl,
            success: function (data) {
                executePostConditionForProjectWiseExpense(data, actionListUrl);
            }, complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForProjectWiseExpense(data, projectListUrl) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        populateProjectWiseExpenseGrid(data, projectListUrl);
        return false;
    }

    function resetSearchForm() {
        emptyProjectGrid();
        emptyExpenseDetailsGrid();
        doGridEmpty();
        $('#hidFromDate').val('');
        $('#hidToDate').val('');
        $('#hidProjectId').val('');
        $('#hidCoaId').val('');
        $('#hidAccGroupId').val('');
    }

    function populateProjectWiseExpenseGrid(data, actionListUrl) {
        var expenseListMap = data.expenseListMap;
        var fromDate = expenseListMap.fromDate;
        var toDate = expenseListMap.toDate;
        var projectId = expenseListMap.projectId;
        var coaId = expenseListMap.coaId;
        var accGroupId = expenseListMap.accGroupId;
        $('#hidFromDate').val(fromDate);
        $('#hidToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        $('#hidCoaId').val(coaId);
        $('#hidAccGroupId').val(accGroupId);
        $("#flex1").flexOptions({url: actionListUrl});
        onLoadProjectGetExpenseListJSON(data);
        $('.download_icon_set').show();
    }

    function emptyProjectGrid() {
        var emptyModel = getEmptyGridModel();
        $("#flex1").flexOptions({url: false});
        $("#flex1").flexAddData(emptyModel);
    }

    function emptyExpenseDetailsGrid() {
        var emptyModel = getEmptyGridModel();
        $("#flexForExpenseList").flexOptions({url: false});
        $("#flexForExpenseList").flexAddData(emptyModel);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right"},
                        {display: "Project Name", name: "project_name", width: 250, sortable: false, align: "left"},
                        {display: "Total Credit", name: "cr_total", width: 160, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadProjectGrid}

                    ],
                    sortname: "project.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Project Wise Total',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    width: getGridWidthOfProjectList(),
                    height: getGridHeight(3) - 50,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: onLoadProjectGetExpenseListJSON
                }
        );
    }

    function reloadCoaGrid(com, grid) {
        $('#flexCOA').flexOptions({query: ''}).flexReload();
    }

    function onLoadProjectGetExpenseListJSON(data) {
        emptyExpenseDetailsGrid();
        if (data.isError) {
            showInfo(data.message);
            emptyProjectGrid();
        }
        $("#flex1").flexAddData(data.expenseGridObj);
        checkGrid();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No record found within given dates');
        }
    }

    function reloadProjectGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1}).flexReload();
        $('select[name=rp]').val(20);
    }

    function getGridWidthOfProjectList() {
        var gridWidth = '100%';
        gridWidth = $("#flex1").parent().width();
        return gridWidth;
    }

    /********************for Purchase Order Grid List****************************/
    function executePreConToShowPurchaseOrderList(ids) {
        if (ids.length == 0) {
            showError("Please select a project to show its expense  List");
            return false;
        }
        return true;
    }

    function populateProjectWiseExpenseDetails() {
        doExpenseGridEmpty();
        var ids = $('.trSelected', $('#flex1'));
        if (ids.length == 0) {
            return false;
        }

        showLoadingSpinner(true);
        var projectId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var coaId = $('#hidCoaId').val();
        var accGroupId = $('#hidAccGroupId').val();
        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        var projectUrl = "${createLink(controller:'accReport', action: 'listProjectWiseExpenseDetails')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&coaId=" + coaId + "&accGroupId=" + accGroupId;
        showLoadingSpinner(true);
        $.ajax({
            url: projectUrl,
            success: function (data) {
                onLoadVoucherDetailsListJSON(data, projectUrl);
            },
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    $("#flexForExpenseList").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Code", name: "code", width: 60, sortable: false, align: "left"},
                    {display: "Head Name", name: "description", width: 160, sortable: false, align: "left"},
                    {display: "Total Debit", name: "amount_dr", width: 150, sortable: false, align: "right"}
                ],
                buttons: [
                    {name: 'Refresh', bclass: 'clear-results', onpress: reloadGridDetails},
                    {name: 'Ledger', bclass: 'view', onpress: viewLedgerReport}
                ],
                sortname: "code",
                sortorder: "asc",
                usepager: false,
                singleSelect: true,
                title: 'Expense Details List',
                useRp: false,
                showTableToggleBtn: false,
                width: getGridWidthOfPurchaseOrderList(),
                height: getGridHeight(3)-20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                },
                customPopulate: onLoadVoucherDetailsListJSON
            }
    );

    function reloadGridDetails(com, grid) {
        $('#flexForExpenseList').flexOptions({query: ''}).flexReload();
    }

    function getGridWidthOfPurchaseOrderList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForExpenseList").parent().width();
        return gridWidth;
    }

    function onLoadVoucherDetailsListJSON(data, projectUrl) {
        if (data.isError) {
            showError(data.message);
            emptyExpenseDetailsGrid();
        }
        $("#flexForExpenseList").flexOptions({url: projectUrl});
        $("#flexForExpenseList").flexAddData(data.expenseGridObj);
    }

    function doExpenseGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForExpenseList").flexAddData(emptyGridModel);
    }

    function viewLedgerReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexForExpenseList'), 'COA') == false) {
            return false;
        }
        var coaId = getSelectedIdFromGrid($('#flexForExpenseList'));

        if ((coaId.length == 0) || isNaN(coaId)) {
            if (executeCommonPreConditionForSelect($('#flexForExpenseList'), 'COA') == false) {
                return false;
            }
        }
        showLoadingSpinner(true);
        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        // now get projectId from left grid(which is selected)

        var ids = $('.trSelected', $('#flex1'));
        if (ids.length == 0) {
            showError("Please select a project from the grid");
            return false;
        }
        var projectId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var loc = "${createLink(controller:'accReport', action: 'showLedger')}?coaId=" + coaId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        $.history.load(formatLink(loc));

        return false;
    }

    // To populate Inventory List
    function updateCOAList() {
        var accGroupId = dropDownAccGroup.value();
        dropDownCOA.setDataSource(getKendoEmptyDataSource(dropDownCOA, "ALL"));
        dropDownCOA.refresh();
        if (accGroupId == '') {
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'accChartOfAccount', action: 'listAccChartOfAccountByAccGroupId')}?accGroupId=" + accGroupId,
            success: function (data) {
                updateCOAListDropDown(data);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateCOAListDropDown(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownCOA.setDataSource(data.coaList);
    }

</script>