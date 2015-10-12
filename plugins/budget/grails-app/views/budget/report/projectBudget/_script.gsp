<script type="text/javascript">
    var dropDownProject;

    jQuery(function ($) {
        $('#printProjectBudget').click(function () {
            printProjectBudget();
            return false;
        });
        onLoadProjectBudget();
    });

    function onLoadProjectBudget() {
        initializeForm($("#projectBudgetForm"), getProjectBudget);
        try {   // Model will be supplied to grid on load _list.gsp
            $('.download_icon_set').hide();
            initFlexGrid();
        } catch (e) {
            showError(e.message);
        }
        // update page title
        $(document).attr('title', "MIS - Budget");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgReport/showProjectBudget");
    }

    function printProjectBudget() {
        var projectId = $('#hidProjectId').val();
        if (projectId.length <= 0) {
            showError('First populate project item then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm('Do you want to download the pdf report now?')) {
            var url = "${createLink(controller:'budgReport', action: 'downloadProjectBudget')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function getProjectBudget() {
        if (!validateForm($("#projectBudgetForm"))) {
            resetForm();
            return false;
        }
        var projectId = dropDownProject.value();
        doGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'budgReport', action: 'listProjectBudget')}?projectId=" + projectId,
            success: executePostCondition,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function resetForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hidProjectId').val('');
    }

    function executePostCondition(data) {
        $('.download_icon_set').show();
        var projectId = data.projectId;
        $('#hidProjectId').val(projectId);
        var projectCostingListUrl = "${createLink(controller:'budgReport', action: 'listProjectBudget')}?projectId=" + projectId;
        $("#flex1").flexOptions({url: projectCostingListUrl});
        onLoadProjectCostingListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "Item", name: "item_name", width: 300, sortable: false, align: "left"},
                        {display: "Total Budget Quantity", name: "total_budget_quantity", width: 200, sortable: false, align: "right"},
                        {display: "Total PR Quantity", name: "total_pr_quantity", width: 200, sortable: false, align: "right"},
                        {display: "Remaining Quantity", name: "remaining_quantity", width: 200, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Item", name: "itemName", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "item_name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Items',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadProjectCostingListJSON
                }
        );
    }

    function onLoadProjectCostingListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flex1").flexAddData(data.lstProjectItem);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Item found');
            $('.download_icon_set').hide();
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

</script>