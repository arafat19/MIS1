<script type="text/javascript">
    var dropDownProject;

    jQuery(function ($) {
        $('#printConsumptionDeviation').click(function () {
            printConsumptionDeviation();
            return false;
        });
        $('#printConsumptionDeviationCsv').click(function () {
            printConsumptionDeviationCsv();
            return false;
        });
        onLoadConsumptionDeviation();
    });

    function printConsumptionDeviation() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate Consumption Deviation List then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm('Do you want to download the consumption deviation report?')) {
            var url = "${createLink(controller:'budgReport', action: 'downloadConsumptionDeviation')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printConsumptionDeviationCsv() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('Select a Project to see the Report');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm('Do you want to download the consumption deviation report in csv format?')) {
            var url = "${createLink(controller:'budgReport', action: 'downloadConsumptionDeviationCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function onLoadConsumptionDeviation() {
        initializeForm($("#searchForm"), getConsumptionDeviation);
        try {   // Model will be supplied to grid on load _list.gsp
            $('.download_icon_set').hide();
            initFlexGrid();
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Consumption Deviation Report");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgReport/showConsumptionDeviation");
    }

    function getConsumptionDeviation() {

        if (!validateForm($("#searchForm"))) {
            resetConsumptionDeviationForm();
            return false;
        }

        var projectId = dropDownProject.value();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'budgReport', action: 'listConsumptionDeviation')}?projectId=" + projectId,
            success: executePostConditionForConsumptionDeviation,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForConsumptionDeviation(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateConsumptionDeviationGrid(data);
        $('.download_icon_set').show();
        return false;
    }

    function populateConsumptionDeviation(result) {
        $('#hidProjectId').val(result.projectId);
        $('.download_icon_set').show();
    }

    function resetConsumptionDeviationForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
    }

    function populateConsumptionDeviationGrid(data) {
        var projectId = data.projectId;
        $('#hidProjectId').val(projectId);

        var consumptionDeviationListUrl = "${createLink(controller:'budgReport', action: 'listConsumptionDeviation')}?projectId=" + projectId;
        $("#flex1").flexOptions({url: consumptionDeviationListUrl});
        onLoadConsumptionDeviationListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: true},
                        {display: "Item", name: "item_name", width: 250, sortable: false, align: "left"},
                        {display: "Budget Quantity", name: "budget_quantity", width: 130, sortable: false, align: "right"},
                        {display: "Budget Amount", name: "budget_amount", width: 130, sortable: false, align: "right"},
                        {display: "Consumed Quantity", name: "consume_quantity", width: 130, sortable: false, align: "right"},
                        {display: "Consumed Amount", name: "consume_amount", width: 130, sortable: false, align: "right"},
                        {display: "Deviation Amount", name: "deviation_amount", width: 130, sortable: false, align: "right"}
                    ],
                    sortname: "item_name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Consumption Deviation Report',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(3) - 10,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadConsumptionDeviationListJSON
                }
        );
    }

    function onLoadConsumptionDeviationListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flex1").flexAddData(data.consumptionDeviationList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget found');
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