<script type="text/javascript">
    var modelJsonForTrialBalanceList, dropDownProject, dropDownDivision;

    $(document).ready(function () {
        modelJsonForTrialBalanceList = ${modelJson};
        $('#printTrialBalancePdf').click(function () {
            printTrialBalanceListOfLevel2('pdf');
            return false;
        });
        $('#printTrialBalanceCsv').click(function () {
            printTrialBalanceListCsvOfLevel2();
            return false;
        });
        onLoadTrialBalanceListOfLevel2();
    });

    function printTrialBalanceListOfLevel2(formatType) {
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
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId + "&formatType=" + formatType;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadTrialBalanceOfLevel2')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function printTrialBalanceListCsvOfLevel2() {
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
            var url = "${createLink(controller:'accReport', action: 'downloadTrialBalanceCsvOfLevel2')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function onLoadTrialBalanceListOfLevel2() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getTrialBalanceListOfLevel2);
            $('.download_icon_set').hide();
            dropDownDivision = initKendoDropdown($('#divisionId'), null, null, getKendoEmptyDataSource(dropDownDivision, "ALL"));

            $("#fromDate").val(modelJsonForTrialBalanceList.fromDate);
            $("#toDate").val(modelJsonForTrialBalanceList.toDate);
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Trial Balance List");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showTrialBalanceOfLevel2");
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

    function getTrialBalanceListOfLevel2() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        $('.download_icon_set').hide();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var projectId = dropDownProject.value();
        var divisionId = dropDownDivision.value();

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        doGridEmpty();
        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId;
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listTrialBalanceOfLevel2')}" + params,
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
        var trialBalanceListUrl = "${createLink(controller:'accReport', action: 'listTrialBalanceOfLevel2')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&divisionId=" + divisionId;
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
                    {display: "Description", name: "description", width: 500, sortable: false, align: "left"},
                    {display: "Debit", name: "dr_balance", width: 150, sortable: false, align: "right"},
                    {display: "Credit", name: "cr_balance", width: 150, sortable: false, align: "right"}
                ],
                sortname: "description",
                sortorder: "asc",
                usepager: false,
                singleSelect: true,
                title: 'Trial Balance (Hierarchy 2)',
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