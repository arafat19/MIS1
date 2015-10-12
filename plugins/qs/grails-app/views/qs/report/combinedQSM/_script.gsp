<script type="text/javascript">
    var validatorSearch, dropDownProject;

    jQuery(function ($) {
        $('#printBudgetQs').click(function () {
            printCombinedQSM();
            return false;
        });
        $('#printBudgetQsCsv').click(function () {
            printCombinedQSMCsv();
            return false;
        });
        $("#flex1").click(function () {
            populateCombinedQSMListGrid();
        });

        onLoadCombinedQSM();
    });
    function printCombinedQSM() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate combined QSM then click print');
            return false;
        }

        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the Combined Qs Measurement report now?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadCombinedQSM')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printCombinedQSMCsv() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate combined QSM CSV then click print');
            return false;
        }

        var fromDate = $('#hidFromDate').attr('value');
        var toDate = $('#hidToDate').attr('value');

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        if (confirm('Do you want to download the Combined CSV Qs Measurement report now?')) {
            var url = "${createLink(controller:'qsReport', action: 'downloadCombinedQSMCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadCombinedQSM() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getCombinedQSM);

        initFlexGrid();

        $('.download_icon_set').hide();

        // update page title
        $(document).attr('title', "MIS - Combined QS Measurement");
        loadNumberedMenu(MENU_ID_QS, "#qsReport/showCombinedQSM");
    }

    function updateFromDate() {
        var projectFromDate = dropDownProject.dataItem().createdon;
        $("#fromDate").val(projectFromDate);
    }

    function executePreConditionToGetCombinedQSM() {
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

    function getCombinedQSM() {
        if (executePreConditionToGetCombinedQSM() == false) {
            return false;
        }

        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var projectId = dropDownProject.value();
        showLoadingSpinner(true);
        $.ajax({
            url:"${createLink(controller:'qsReport', action: 'listCombinedQSM')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate,
            success:executePostConditionForCombinedQSM,
            complete:onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType:'json',
            type:'post'
        });
        return false;
    }

    function executePostConditionForCombinedQSM(data) {
        // populateBudgetPo(data)
        populateCombinedQSM(data);

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

    function populateCombinedQSM(data) {
        $('.download_icon_set').show();
        $("#lblBudgetDetails").html('');
        var fromDate = data.fromDate;
        var toDate = data.toDate;
        var projectId = data.projectId;
        var isGovtQs = data.isGovtQs;
        $('#hidFromDate').val(fromDate);
        $('#hidToDate').val(toDate);
        $('#hidProjectId').val(projectId);
        var combinedQSMUrl = "${createLink(controller:'qsReport', action: 'listCombinedQSM')}?projectId=" + projectId + "&fromDate=" + fromDate + "&toDate=" + toDate;
        doGridEmpty();
        $("#flex1").flexOptions({url:combinedQSMUrl});
        onLoadCombinedQSMListJSON(data);
    }

    function populateCombinedQSMListGrid() {
        var ids = $('.trSelected', $('#flex1'));
        if (ids.length == 0) {
            return false;
        }
        //show budget details
        var budgetDetails = $(ids[ids.length - 1]).find('td').eq(11).find('div').text();
        $("#lblBudgetDetails").html(budgetDetails);

    }

    function initFlexGrid() {
        $("#flex1").flexigrid
            (
                {
                    url:false,
                    dataType:'json',
                    colModel:[
                        {display:"Serial", name:"serial", width:25, sortable:false, align:"right", hide:false},
                        {display:"Line Item", name:"budget_item", width:100, sortable:false, align:"left"},
                        {display:"Quantity", name:"budget_quantity_unit", width:100, sortable:false, align:"right"},
                        {display:"Certified(Intern)", name:"work_completed_intern", width:100, sortable:false, align:"right"},
                        {display:"Certified(Govt)", name:"work_completed_gov", width:100, sortable:false, align:"right"},
                        {display:"Achieved%(Intern)", name:"work_achieved_in_percent_intern", width:100, sortable:false, align:"right"},
                        {display:"Achieved%(Govt)", name:"work_achieved_in_percent_gov", width:100, sortable:false, align:"right"},
                        {display:"Contract Rate", name:"contract_rate", width:70, sortable:false, align:"right"},
                        {display:"Cost per Unit(Intern)", name:"cost_per_unit_intern", width:100, sortable:false, align:"right"},
                        {display:"Certified Amount(Intern)", name:"work_certified_amount_intern", width:135, sortable:false, align:"right"},
                        {display:"Certified Amount(Govt)", name:"work_certified_amount_gov", width:135, sortable:false, align:"right"},
                        {display:"Budget Details", name:"budget_details", width:120, sortable:false, align:"left", hide:true}

                    ],
                    buttons:[
                        {name:'Clear Results', bclass:'clear-results', onpress:reloadQsGrid},
                        {separator:true}
                    ],
                    sortname:"b.budget_item",
                    sortorder:"asc",
                    usepager:true,
                    singleSelect:true,
                    title:'Combined QS Measurement List',
                    useRp:true,
                    rp:20,
                    rpOptions:[10, 20, 25, 30],
                    showTableToggleBtn:false,
                    height:getGridHeight(9) - 100,
                    afterAjax:function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate:onLoadCombinedQSMListJSON
                }
            );
    }

    function onLoadCombinedQSMListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flex1").flexAddData(data.combinedQsMeasurementList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Combined QS Measurement found');
        }
    }

    function reloadQsGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query:''}).flexReload();
        }
    }
    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url:false, newp:1, rp:20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>