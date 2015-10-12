<script type="text/javascript">
    var modelJsonForFinancialStatementList, dropDownProject, dropDownDivision;
    jQuery(function ($) {
        modelJsonForFinancialStatementList = ${modelJson};
        $('#printFinancialStatementPdf').click(function () {
            printFinancialStatementListOfLevel3('pdf');
            return false;
        });

        $('#printFinancialStatementCsv').click(function () {
            printFinancialStatementCsvOfLevel3();
            return false;
        });
        onLoadFinancialStatementOfLevel3();
    });

    function printFinancialStatementListOfLevel3(formatType) {
        var confirmMsg;

        if (formatType == 'pdf') {
            confirmMsg = 'Do you want to download the financial statement in pdf format?';
        }

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').attr('value');
        var divisionId = $('#hideDivisionId').attr('value');

        if (!customValidateDate($('#hideFromDate'), 'From Date', $('#hideToDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId + "&formatType=" + formatType;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadFinancialStatementOfLevel3')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function printFinancialStatementCsvOfLevel3() {
        var confirmMsg = 'Do you want to download the financial statement in csv format?';

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').attr('value');
        var divisionId = $('#hideDivisionId').attr('value');

        if (!customValidateDate($('#hideFromDate'), 'From Date', $('#hideToDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadFinancialStatementCsvOfLevel3')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function onLoadFinancialStatementOfLevel3() {
        try {   // Model will be supplied to grid on load _list.gsp
            $('.download_icon_set').hide();
            initializeForm($("#searchForm"), getFinancialStatementListOfLevel3);
            dropDownDivision = initKendoDropdown($('#divisionId'), null, null, getKendoEmptyDataSource(dropDownDivision, "ALL"));

            $("#fromDate").val(modelJsonForFinancialStatementList.fromDate);
            $("#toDate").val(modelJsonForFinancialStatementList.toDate);
        } catch (e) {
            showError(e.message);
        }
        initFlexGrid();
        // update page title
        $(document).attr('title', "CIMS - Financial Statement List");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showFinancialStatementOfLevel3");
    }

    function getFinancialStatementListOfLevel3() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        $('.download_icon_set').hide();
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        var divisionId = dropDownDivision.value();

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listFinancialStatementOfLevel3')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId,
            success: executePostConditionForFinancialStatementList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForFinancialStatementList(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }

        populateFinancialStatementListGrid(data);
        return false;
    }

    function populateFinancialStatementListGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var divisionId = data.divisionId;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hideProjectId').val(projectId);
        $('#hideDivisionId').val(divisionId);
        var financialStatementListUrl = "${createLink(controller:'accReport', action: 'listFinancialStatementOfLevel3')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId;
        $("#flex1").flexOptions({url: financialStatementListUrl});
        $("#flex1").flexAddData(data.financialStatementList);
        if (data.financialStatementList.total > 0) {
            $('.download_icon_set').show();
        }
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "COA ID/Tier2 ID", name: "coaId", width: 30, sortable: false, align: "right", hide: "true"},
                        {display: "Description", name: "description", width: 400, sortable: false, align: "left"},
                        {display: "Asset", name: "dr_balance", width: 140, sortable: false, align: "right"},
                        {display: "Liabilities", name: "cr_balance", width: 140, sortable: false, align: "right"}
                    ],
                    sortname: "coaId",
                    sortorder: "asc",
                    usepager: false,
                    singleSelect: true,
                    title: 'Financial Statement (Hierarchy-3)',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(3) + 25,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    preProcess: onLoadFinancialStatementJSON
                }
        );
    }

    function onLoadFinancialStatementJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        return data;
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No financial statement list found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function populateDivisionList() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            dropDownDivision.setDataSource(getKendoEmptyDataSource(dropDownDivision, "ALL"));
            dropDownDivision.value('');
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accDivision', action: 'getDivisionListByProjectId')}?projectId=" + projectId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    dropDownDivision.setDataSource(data.accDivisionList);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

</script>