<script type="text/javascript">
    var modelJsonForCustomGroupBalanceList, dropDownProject;
    jQuery(function ($) {
        modelJsonForCustomGroupBalanceList = ${modelJson};
        $('#printCustomGroupBalancePdf').click(function () {
            printCustomGroupBalanceList('pdf');
            return false;
        });
        $('#printCustomGroupBalanceXls').click(function () {
            printCustomGroupBalanceList('xls');
            return false;
        });
        $('#printCustomGroupBalanceCsv').click(function () {
            printCustomGroupBalanceListCsv();
            return false;
        });
        onLoadCustomGroupBalanceList();
    });

    function printCustomGroupBalanceList(formatType) {
        var confirmMsg;
        if (formatType == 'pdf') {
            confirmMsg = 'Do you want to download the custom group balance in pdf format?';
        }
        else if (formatType == 'xls') {
            confirmMsg = 'Do you want to download the custom group balance in xls format?';
        }
        else if (formatType == 'csv') {
            confirmMsg = 'Do you want to download the custom group balance in csv format?';
        }
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').attr('value');

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId + "&formatType=" + formatType;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadCustomGroupBalance')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function printCustomGroupBalanceListCsv() {

        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').attr('value');

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the custom group balance in csv format?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadCustomGroupBalanceCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true
    }

    function onLoadCustomGroupBalanceList() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getCustomGroupBalanceList);
            $('.download_icon_set').hide();
            $("#fromDate").val(modelJsonForCustomGroupBalanceList.fromDate);
            $("#toDate").val(modelJsonForCustomGroupBalanceList.toDate);
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Custom Group Balance List");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showCustomGroupBalance");
    }

    function getCustomGroupBalanceList() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        $('.download_icon_set').hide();
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listCustomGroupBalance')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId,
            success: executePostConditionForCustomGroupBalanceList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForCustomGroupBalanceList(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }

        populateCustomGroupBalanceListGrid(data);
        return false;
    }

    function populateCustomGroupBalanceListGrid(data) {
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hideProjectId').val(projectId);
        var customGroupBalanceListUrl = "${createLink(controller:'accReport', action: 'listCustomGroupBalance')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        $("#flex1").flexOptions({url: customGroupBalanceListUrl});
        $("#flex1").flexAddData(data.customGroupBalanceList);
        if (data.customGroupBalanceList.total > 0) {
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
                    {display: "Custom Group", name: "coaId", width: 275, sortable: false, align: "left"},
                    {display: "COA ID", name: "coaId", width: 30, sortable: false, align: "right", hide: "true"},
                    {display: "Description", name: "description", width: 400, sortable: false, align: "left"},
                    {display: "Code", name: "code", width: 50, sortable: false, align: "left"},
                    {display: "Debit", name: "dr_balance", width: 100, sortable: false, align: "right"},
                    {display: "Credit", name: "cr_balance", width: 100, sortable: false, align: "right"}
                ],
                buttons: [
                    <sec:access url="/accReport/showLedger">
                    {name: 'Ledger', bclass: 'view', onpress: viewLedgerReport}
                    </sec:access>
                ],
                sortorder: "asc",
                usepager: false,
                singleSelect: true,
                title: 'Custom Group Balance List',
                useRp: true,
                rp: 20,
                rpOptions: [10, 20, 25, 30],
                showTableToggleBtn: false,
                height: getGridHeight(3),
                afterAjax: function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                    checkGrid();
                },
                preProcess: onLoadCustomGroupBalanceListJSON
            }
    );

    function onLoadCustomGroupBalanceListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        return data;
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Custom Group Balance List found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function viewLedgerReport(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'head') == false) {
            return false;
        }
        var coaId = $(ids[0]).find('td').eq(2).find('div').text();
        if ($.trim(coaId).isEmpty()) {
            if (executeCommonPreConditionForSelect($('#flex1'), 'head') == false) {
                return false;
            }
        }
        showLoadingSpinner(true);
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hideProjectId').val();

        var loc = "${createLink(controller:'accReport', action: 'showLedger')}?coaId=" + coaId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>