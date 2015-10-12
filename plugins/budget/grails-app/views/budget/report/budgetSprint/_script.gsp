<script type="text/javascript">
    jQuery(function ($) {
        $('#printSprintReport').click(function () {
            printSprintReport();
            return false;
        });
        onLoadSprintReport();
    });

    function printSprintReport() {
        var sprintId = $('#hidSprintId').attr('value');
        if (sprintId.length <= 0) {
            showError('First populate sprint report then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?sprintId=" + sprintId;
        if (confirm('Do you want to download the sprint report now?')) {
            var url = "${createLink(controller:'budgReport', action: 'downloadSprintReport')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadSprintReport() {
        initializeForm($("#searchForm"), getSprintReport);
        try {
            var output = ${output};
            $('.download_icon_set').hide();
            initFlexGrid();
            if (output.sprintId) {
                $('.download_icon_set').show();
                $("#sprintId").val(output.sprintId);
                populateSprintGrid(output);
                checkGrid();
            }
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Budget Sprint Report");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgReport/showBudgetSprint");
    }

    function getSprintReport() {
        resetBudgetSprintForm();
        if (!validateForm($("#searchForm"))) {
            doGridEmpty();
            $('.download_icon_set').hide();
            return false;
        }

        var sprintId = $('#sprintId').attr('value');
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'budgReport', action: 'searchBudgetSprint')}?sprintId=" + sprintId,
            success: executePostConditionForSprintReport,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForSprintReport(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateSprintGrid(data);
        $('.download_icon_set').show();
        return false;
    }

    function resetBudgetSprintForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
    }

    function populateSprintGrid(data) {
        var sprintId = data.sprintId;
        $('#hidSprintId').val(sprintId);

        var url = "${createLink(controller:'budgReport', action: 'searchBudgetSprint')}?sprintId=" + sprintId;
        $("#flex1").flexOptions({url: url});
        onLoadBudgetSprintListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "BOQ Line Item", name: "budget_line_item", width: 200, sortable: false, align: "left"},
                        {display: "Task", name: "task", width: 400, sortable: false, align: "left"},
                        {display: "Start Date", name: "start_date", width: 120, sortable: false, align: "left"},
                        {display: "End Date", name: "end_date", width: 120, sortable: false, align: "left"},
                        {display: "Status", name: "status", width: 120, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "budget_line_item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Task List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadBudgetSprintListJSON
                }
        );
    }

    function onLoadBudgetSprintListJSON(data) {
        if (data.isError) {
            showError(data.message);
            doGridEmpty();
            return null;
        }
        $("#flex1").flexAddData(data.lstBudgetSprint);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            $('.download_icon_set').hide();
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

</script>