<script type="text/javascript">
    var dropDownProject, fromDateOnChange;

    jQuery(function ($) {
        $('#printProjectFundFlow').click(function () {
            printProjectFundFlow();
            return false;
        });
        onLoadProjectFundFlow();
    });

    function printProjectFundFlow() {
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var projectId = $('#hidProjectId').val();

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        if (confirm('Do you want to download the project fund flow now?')) {
            var url = "${createLink(controller: 'accReport', action: 'downloadProjectFundFlowReport')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadProjectFundFlow() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getProjectFundFlow);
            $('.download_icon_set').hide();
            initFlexGrid();
            fromDateOnChange = $('#fromDate').attr('value');

        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Project Fund Flow");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showProjectFundFlowReport");
    }

    function executePreConditionToGetProjectFund() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function getProjectFundFlow() {
        if (executePreConditionToGetProjectFund() == false) {
            resetProjectFundFlowForm();
            return false;
        }

        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();


        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'listProjectFundFlowReport')}" + params,
            success: executePostConditionForProjectFund,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForProjectFund(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateProjectFundGrid(data);
        $('.download_icon_set').show();
        return false;
    }

    function resetProjectFundFlowForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hideFromDate').val('');
        $('#hideToDate').val('');
        $('#hidProjectId').val('');
    }

    function populateProjectFundGrid(data) {
        var fromDate = $('#fromDate').attr('value');
        var toDate =  $('#toDate').attr('value');
        var projectId = dropDownProject.value();

        $('#hideFromDate').val(fromDate);
        $('#hideToDate').val(toDate);
        $('#hidProjectId').val(projectId);

        var ledgerListUrl = "${createLink(controller:'accReport', action: 'listProjectFundFlowReport')}?fromDate=" + fromDate + "&toDate=" + toDate + "&projectId=" + projectId;
        $("#flex1").flexOptions({url: ledgerListUrl});
        $("#flex1").flexAddData(data.gridObj);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right", hide: false},
                        {display: "Group", name: "group_name", width: 150, sortable: false, align: "left"},
                        {display: "COA Code", name: "coa_code", width: 90, sortable: false, align: "left"},
                        {display: "Opening Balance", name: "opening_balance", width: 170, sortable: false, align: "right"},
                        {display: "Total Debit", name: "dr_balance", width: 160, sortable: false, align: "right"},
                        {display: "Total Credit", name: "cr_balance", width: 160, sortable: false, align: "right"},
                        {display: "Closing Balance", name: "closing_balance", width: 170, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    sortname: "ag.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Project Fund Flow Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(3) - 60,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadProjectFundFlowListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadProjectFundFlowListJSON(data) {

        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flex1").flexAddData(data.gridObj);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Project Fund flow found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function updateFromDate() {
        if (dropDownProject.value() == '') {
            $("#fromDate").val(fromDateOnChange);
        } else {
            var fromDate = dropDownProject.dataItem().createdon;
            $("#fromDate").val(fromDate);
        }
    }

</script>