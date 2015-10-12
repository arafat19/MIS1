<script type="text/javascript">
    var dropDownProject;

    jQuery(function ($) {
        $('#printQsMeasurement').click(function () {
            printQsMeasurement();
            return false;
        });

        $('#printQsMeasurementCsv').click(function () {
            printQsMeasurementCsv();
            return false;
        });

        $("#flex1").click(function () {
            populateQsMeasurementListGrid();
        });
        onLoadBudgetPo();
    });

    function onLoadBudgetPo() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getQsMeasurement);

        initFlexGrid();

        $('.download_icon_set').hide();

        // update page title
        $(document).attr('title', "MIS - Quantity Survey Measurement");
        loadNumberedMenu(MENU_ID_QS, "#qsReport/showQsMeasurementRpt");
    }

    function printQsMeasurement() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate QS Measurement then print report');
            return false;
        }

        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        var isGovtQs = $('#hidIsGovtQs').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        if (confirm('Do you want to download the QS Measurement report in PDF format?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadQsMeasurementRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printQsMeasurementCsv() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate QS Measurement then print report');
            return false;
        }

        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');
        var isGovtQs = $('#hidIsGovtQs').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        if (confirm('Do you want to download the QS Measurement report in CSV format?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadQsMeasurementCsvRpt')}" + params;
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

    function getQsMeasurement() {
        if (executePreCondition() == false) {
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
            url: "${createLink(controller:'qsReport', action: 'listQsMeasurementRpt')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs,
            success: executePostConditionQsMeasurement,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionQsMeasurement(data) {
        populateQsMeasurementGrid(data);
        return false;
    }

    function populateBudgetPo(result) {
        $('#hidProjectId').val(result.projectId);
        $('#hidFromDate').val(result.fromDate);
        $('#hidToDate').val(result.toDate);
        $("#fromDate").val(result.fromDate);
        $("#toDate").val(result.toDate);

        $('.download_icon_set').show();
    }

    function resetBudgetForm() {
        doGridEmpty();
        $('#hidFromDate').val('');
        $('#hidToDate').val('');
        $('#hidProjectId').val('');
        $('.download_icon_set').show();
    }

    function populateQsMeasurementGrid(data) {
        $("#lblBudgetDetails").html('');
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var isGovtQs = data.isGovtQs;
        $('#hidFromDate').val(fromDate);
        $('#hidToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        $('#hidIsGovtQs').val(isGovtQs);
        var qsMeasurementUrl = "${createLink(controller:'qsReport', action: 'listQsMeasurementRpt')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isGovtQs=" + isGovtQs;
        doGridEmpty();
        $("#flex1").flexOptions({url: qsMeasurementUrl});
        onLoadBudgetPoListJSON(data);
        $('.download_icon_set').show();
    }

    function populateQsMeasurementListGrid() {
        var ids = $('.trSelected', $('#flex1'));
        if (ids.length == 0) {
            return false;
        }
        //show budget details
        var budgetDetails = $(ids[ids.length - 1]).find('td').eq(7).find('div').text();
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
                        {display: "Budget Quantity", name: "budget_quantity", width: 120, sortable: false, align: "right"},
                        {display: "Work Certified", name: "work_completed", width: 100, sortable: false, align: "right"},
                        {display: "Work Remaining", name: "work_remaining", width: 100, sortable: false, align: "right"},
                        {display: "Work Achieved(%)", name: "work_achieved_in_percent", width: 100, sortable: false, align: "right"},
                        {display: "Work Remaining(%)", name: "work_remaining_in_percent", width: 110, sortable: false, align: "right"},
                        {display: "Budget Details", name: "budget_details", width: 120, sortable: false, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadQsGrid},
                        {separator: true}
                    ],
                    sortname: "budget_item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'QS Measurement List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - $("#divQsMeasurement").height() - 100,
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
            return false;
        }
        $("#flex1").flexAddData(data.qsMeasurementList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No QS Measurement found');
        }
    }

    function reloadQsGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }
    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>