<script type="text/javascript">
    var dropDownProject, dropDownSourceType, dropDownSourceCategory;

    jQuery(function ($) {
        $('#printSourceWiseBalancePdf').click(function () {
            printSourceWiseBalance();
            return false;
        });
        $('#printSourceWiseBalanceCsv').click(function () {
            printSourceWiseBalanceCsv();
            return false;
        });
        onLoadSourceBalance();
    });

    function printSourceWiseBalance() {
        var confirmMsg = 'Do you want to download the source wise balance in pdf format?';

        if (!customValidateDate($('#hidFromDate'), 'From Date', $('#hidToDate'), 'To Date')) {
            return false;
        }
        var sourceLedgerTypeId = $('#hidSourceTypeId').val();
        if (sourceLedgerTypeId.length <= 0) {
            showError('First populate source wise balance then click print');
            return false;
        }

        var toDate = $('#hidToDate').attr('value');
        var fromDate = $('#hidFromDate').attr('value');
        var sourceTypeId = $('#hidSourceTypeId').attr('value');
        var sourceCategoryId = $('#hidSourceCategoryId').attr('value');
        var coaId = $('#hidCoaId').attr('value');
        var projectId = $('#hidProjectId').val();

        showLoadingSpinner(true);
        var params = "?sourceTypeId=" + sourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&toDate=" + toDate + "&coaId=" + coaId + "&projectId=" + projectId + "&fromDate=" + fromDate;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadSourceWiseBalance')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function printSourceWiseBalanceCsv() {
        var confirmMsg = 'Do you want to download the source wise balance in csv format?';

        if (!customValidateDate($('#hidFromDate'), 'From Date', $('#hidToDate'), 'To Date')) {
            return false;
        }
        var sourceLedgerTypeId = $('#hidSourceTypeId').val();
        if (sourceLedgerTypeId.length <= 0) {
            showError('First populate source wise balance then click print');
            return false;
        }

        var toDate = $('#hidToDate').attr('value');
        var fromDate = $('#hidFromDate').attr('value');
        var sourceTypeId = $('#hidSourceTypeId').attr('value');
        var coaId = $('#hidCoaId').attr('value');
        var projectId = $('#hidProjectId').val();
        var sourceCategoryId = $('#hidSourceCategoryId').attr('value');

        showLoadingSpinner(true);
        var params = "?sourceTypeId=" + sourceTypeId + "&sourceCategoryId=" + sourceCategoryId + "&toDate=" + toDate + "&coaId=" + coaId + "&projectId=" + projectId + "&fromDate=" + fromDate;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadSourceWiseBalanceCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function downloadSourceWiseVoucherList(com, grid) {
        var ids = $('.trSelected', grid);
        if (ids.length == 0) {
            showError("Please select a source to download source wise balance details list");
            return false;
        }

        var confirmMsg = 'Do you want to download the source wise balance details report in PDF format?';

        if (!customValidateDate($('#hidFromDate'), 'From Date', $('#hidToDate'), 'To Date')) {
            return false;
        }
        var sourceLedgerTypeId = $('#hidSourceTypeId').val();
        if (sourceLedgerTypeId.length <= 0) {
            showError('First populate source wise balance then click print');
            return false;
        }

        var toDate = $('#hidToDate').attr('value');
        var fromDate = $('#hidFromDate').attr('value');
        var sourceTypeId = $('#hidSourceTypeId').attr('value');
        var sourceCategoryId = $('#hidSourceCategoryId').attr('value');
        var coaId = $('#hidCoaId').attr('value');
        var sourceId = $(ids[0]).find('td').eq(1).find('div').text();
        var projectId = $('#hidProjectId').val();

        showLoadingSpinner(true);
        var params = "?sourceTypeId=" + sourceTypeId + "&toDate=" + toDate + "&coaId=" + coaId + "&sourceId=" + sourceId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&sourceCategoryId=" + sourceCategoryId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'accReport', action: 'downloadVoucherListBySourceId')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function executePreCondition() {
        var sourceTypeId = dropDownSourceType.value();
        if (sourceTypeId == '') {
            showError('Please select a source type');
            return false;
        }
        if (!checkCustomDate($('#fromDate'), 'From')) {
            return false;
        }
        if (!checkCustomDate($('#toDate'), 'To')) {
            return false;
        }
        return true;
    }

    function getSourceWiseLedger() {
        if (executePreCondition() == false) {
            doGridEmpty();
            $("#hidSourceTypeId").val('');
            $("#hidToDate").val('');
            $("#hidFromDate").val('');
            $("#hidProjectId").val('');
            $("#hidSourceCategoryId").val('');
            $('.download_icon_set').hide();
            return false;
        }

        var toDate = $('#toDate').attr('value');
        var fromDate = $('#fromDate').attr('value');
        var sourceTypeId = dropDownSourceType.value();
        var coaId = $('#hidCoaId').attr('value');
        var projectId = dropDownProject.value();
        var sourceCategoryId = dropDownSourceCategory.value();

        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSourceWiseBalance')}?sourceTypeId=" + sourceTypeId + "&toDate=" + toDate + "&coaId=" + coaId + "&projectId=" + projectId + "&fromDate=" + fromDate + "&sourceCategoryId=" + sourceCategoryId,
            success: executePostCondition,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostCondition(data) {
        if (data.isError == true) {
            showError(data.message);
            doGridEmpty();
            $("#hidSourceTypeId").val('');
            $("#hidToDate").val('');
            $("#hidFromDate").val('');
            $("#hidProjectId").val('');
            $("#hidSourceCategoryId").val('');
            $('.download_icon_set').hide();
        } else {
            $('.download_icon_set').show();
            $("#hidSourceTypeId").val(data.sourceTypeId);
            $("#hidToDate").val(data.toDate);
            $("#hidFromDate").val(data.fromDate);
            var coaId = $('#hidCoaId').attr('value');
            var projectId = dropDownProject.value();
            var sourceCategoryId = dropDownSourceCategory.value();
            $("#hidProjectId").val(projectId);
            $("#hidSourceCategoryId").val(sourceCategoryId);
            var params = "?sourceTypeId=" + data.sourceTypeId + "&toDate=" + data.toDate + "&coaId=" + coaId + "&projectId=" + projectId + "&fromDate=" + data.fromDate + "&sourceCategoryId=" + sourceCategoryId;
            var url = "${createLink(controller: 'accReport', action: 'listSourceWiseBalance')}" + params;
            $("#flex1").flexOptions({url: url});
            $("#flex1").flexAddData(data.sourceWiseBalanceList);
        }
        return false
    }

    function onLoadSourceBalance() {
        try {
            initializeForm($("#searchForm"), getSourceWiseLedger);
            $('.download_icon_set').hide();

            initFlexGrid();
            dropDownSourceCategory = initKendoDropdown($('#sourceCategoryId'), null, null, getKendoEmptyDataSource(dropDownSourceCategory, "ALL"));
            initFlexSearchCOA();
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Source wise Balance");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showSourceWiseBalance");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "ID", name: "sourceId", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Source", name: "source_name", width: 170, sortable: false, align: "left"},
                        {display: "Previous Balance", name: "prev_balance", width: 100, sortable: false, align: "right"},
                        {display: "Debit", name: "dr_balance", width: 100, sortable: false, align: "right"},
                        {display: "Credit", name: "dr_balance", width: 100, sortable: false, align: "right"},
                        {display: "Debit Balance", name: "total_balance", width: 100, sortable: false, align: "right"},
                        {display: "Credit Balance", name: "total_balance", width: 100, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/accReport/downloadVoucherListBySourceId">
                        {name: 'Details', bclass: 'debit', onpress: downloadSourceWiseVoucherList},
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    sortname: "source_name",
                    sortorder: "ASC",
                    usepager: true,
                    singleSelect: true,
                    title: 'Source wise Balance',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30, 35],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 40,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadSourceBalanceListJSON
                }
        );
    }

    function initFlexSearchCOA() {
        $("#flexSearchCOA").flexigrid
        (
                {
                    url: "${createLink(controller: 'accChartOfAccount', action: 'listForVoucher')}",
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "coaId", width: 10, sortable: false, align: "right", hide: true},
                        {display: "Code", name: "code", width: 60, sortable: true, align: "left"},
                        {display: "Head Name", name: "description", width: 250, sortable: true, align: "left"},
                        {display: "Source Id", name: "accSourceTypeId", width: 60, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Add', bclass: 'debit', onpress: addCoa},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadCoaGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "ALL", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Search Chart Of Account',
                    useRp: true,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getFullGridHeight() - 40,
                    resizable: false,
                    isSearchOpen: true,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function addCoa(com, grid) {
        var ids = $('.trSelected', grid);
       
        if (executeCommonPreConditionForSelect($('#flexSearchCOA'), ' chart of account') == false) {
            return;
        }

        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();

        $('#hidCoaId').val(coaId);
        $('#coaCode').html(coaCode);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function reloadCoaGrid(com, grid) {
        $('#flexSearchCOA').flexOptions({query: ''}).flexReload();
    }

    function onLoadSourceBalanceListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flex1").flexAddData(data.sourceWiseBalanceList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Source Balance found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function onChangeSourceType() {
        dropDownSourceCategory.setDataSource(getKendoEmptyDataSource(dropDownSourceCategory, "ALL"));
        dropDownSourceCategory.value('');
        var accSourceTypeId = dropDownSourceType.value();
        if (accSourceTypeId == '') {
            dropDownSourceCategory.refresh();
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listSourceCategoryForSourceWiseBalance')}?sourceTypeId=" + accSourceTypeId,
            success: function (data) {
                executePostForOnchangeSourceType(data);
            },
            complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostForOnchangeSourceType(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        dropDownSourceCategory.setDataSource(data.lstSourceCategory);
    }

</script>