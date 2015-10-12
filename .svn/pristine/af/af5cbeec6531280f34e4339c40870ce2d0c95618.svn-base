<script type="text/javascript">
    var modelJsonForTrialBalanceList, dropDownProject, dropDownDivision;
    jQuery(function ($) {
        modelJsonForTrialBalanceList = ${modelJson};
        $('#printTrialBalancePdf').click(function () {
            printTrialBalanceListOfLevel5('pdf');
            return false;
        });
        $('#printTrialBalanceXls').click(function () {
            printTrialBalanceListOfLevel5('xls');
            return false;
        });
        $('#printTrialBalanceCsv').click(function () {
            printTrialBalanceListCsvOfLevel5();
            return false;
        });
        onLoadTrialBalanceListOfLevel5();
    });

    function printTrialBalanceListOfLevel5(formatType) {
        var confirmMsg;
        if (formatType == 'pdf') {
            confirmMsg = 'Do you want to download the trial balance in pdf format?';
        }

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').attr('value');
        var divisionId = $('#hideDivisionId').attr('value');

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&formatType=" + formatType + "&divisionId=" + divisionId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadTrialBalanceOfLevel5')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function printTrialBalanceListCsvOfLevel5() {
        var confirmMsg = 'Do you want to download the trial balance in csv format?';

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').attr('value');
        var divisionId = $('#hideDivisionId').attr('value');

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadTrialBalanceCsvOfLevel5')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function populateDivision() {
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

    function onLoadTrialBalanceListOfLevel5() {
        try {   // Model will be supplied to grid on load _list.gsp
            $('.download_icon_set').hide();
            initializeForm($("#searchForm"), getTrialBalanceListOfLevel5);
            dropDownDivision = initKendoDropdown($('#divisionId'), null, null, getKendoEmptyDataSource(dropDownDivision, "ALL"));

            $("#fromDate").val(modelJsonForTrialBalanceList.fromDate);
            $("#toDate").val(modelJsonForTrialBalanceList.toDate);
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "CIMS - Trial Balance List");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showTrialBalanceOfLevel5");
    }

    function getTrialBalanceListOfLevel5() {
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
            url: "${createLink(controller:'accReport', action: 'listTrialBalanceOfLevel5')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId,
            success: executePostConditionForTrialBalanceList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForTrialBalanceList(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }

        populateTrialBalanceListGrid(data);
        return false;
    }

    function populateTrialBalanceListGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var divisionId = data.divisionId;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hideProjectId').val(projectId);
        $('#hideDivisionId').val(divisionId);
        var trialBalanceListUrl = "${createLink(controller:'accReport', action: 'listTrialBalanceOfLevel5')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId;
        $("#flex1").flexOptions({url: trialBalanceListUrl});
        $("#flex1").flexAddData(data.trailBalanceList);
        if (data.trailBalanceList.total > 0) {
            $('.download_icon_set').show();
        }
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "Coa ID", name: "coa.id", width: 30, sortable: false, align: "left", hide: true},
                    {display: "Description", name: "description", width: 500, sortable: false, align: "left"},
                    {display: "Debit", name: "dr_balance", width: 150, sortable: false, align: "right"},
                    {display: "Credit", name: "cr_balance", width: 150, sortable: false, align: "right"}
                ],
                sortorder: "asc",
                usepager: false,
                singleSelect: true,
                title: 'Trial Balance (Hierarchy 5)',
                useRp: true,
                rp: 20,
                rpOptions: [10, 20, 25, 30],
                showTableToggleBtn: false,
                height: getGridHeight(3) + 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                    checkGrid();
                },
                preProcess: onLoadTrialBalanceListJSON
            }
    );

    function onLoadTrialBalanceListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        return data;
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Trial Balance List found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>