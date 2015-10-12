<script type="text/javascript">
    var validatorSearch, dropDownProject;

    jQuery(function ($) {
        $('#printReportPDF').click(function () {
            printReport();
            return false;
        });

        $("#flexForSummary").click(function () {
            populateDetailsGrid();
        });
        onLoadPage();
    });

    function onLoadPage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getSummary);

        try {   // Model will be supplied to grid on load _list.gsp
            $('.download_icon_set').hide();

            initSummaryGrid();
            initDetailsGrid();
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - Consumption Against Fixed Asset Report");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fixedAssetReport/showConsumptionAgainstAsset");
    }

    function printReport() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('Please select a project');
            return false;
        }
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm('Do you want to download the consumption against fixed asset report now?')) {
            var url = "${createLink(controller:'fixedAssetReport', action: 'downloadConsumptionAgainstAsset')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function doGridEmpty() {
        $("#flexForSummary").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $("#flexForSummary").flexOptions({url: false}).flexReload();

        $("#flexForDetailList").flexOptions({url: false}).flexReload();

        $('select[name=rp]').val(20);
        var emptyGridModel = getEmptyGridModel();

        $("#flexForSummary").flexAddData(emptyGridModel);
        $("#flexForDetailList").flexAddData(emptyGridModel);
    }

    // ********** For Getting Summary List ********\\
    function getSummary() {
        if (executePreConditionToGetSummary() == false) {
            return false;
        }
        var projectId = dropDownProject.value();

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'fixedAssetReport', action: 'listConsumptionAgainstAsset')}?projectId=" + projectId,
            success: executePostConditionForSummary,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePreConditionToGetSummary() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            showError("Please Select a Project");
            return false;
        }
        return true;
    }

    function executePostConditionForSummary(data) {
        populateSummaryGrid(data);
        return false;
    }

    function populateSummaryGrid(data) {
        var projectId = dropDownProject.value();
        $('#hidProjectId').val(projectId);
        var summaryGridUrl = "${createLink(controller:'fixedAssetReport', action: 'listConsumptionAgainstAsset')}?projectId=" + projectId;

        doGridEmpty();
        $("#flexForSummary").flexOptions({url: summaryGridUrl, newp: 1, rp: 20});
        onLoadSummaryListJSON(data);
    }

    function initSummaryGrid() {
        $("#flexForSummary").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Sl.", name: "id", width: 50, sortable: false, hide: true, align: "left"},
                        {display: "Item Name", name: "name", width: 200, sortable: false, align: "left"},
                        {display: "Total Quantity", name: "quantity", width: 150, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadSummaryGrid},
                        {separator: true}
                    ],
                    sortname: "item",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Consumption Summary',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    width: getGridSummaryWidth(),
                    height: getGridHeight(5)-15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadSummaryListJSON
                }
        );
    }

    function onLoadSummaryListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            $(".download_icon_set").hide();
        } else {
            $(".download_icon_set").show();
            $("#flexForSummary").flexAddData(data.projectWiseSummaryList);
        }
    }

    function checkGrid() {
        var rows = $('table#flexForSummary > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget Po found');
        }
    }

    function reloadSummaryGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flexForSummary').flexOptions({query: ''}).flexReload();
        }
    }

    function getGridSummaryWidth() {
        var gridWidth = '100%';
        gridWidth = $("#flexForSummary").parent().width();
        return gridWidth;
    }

    /********************for getting Details Grid List****************************/
    function populateDetailsGrid() {
        var ids = $('.trSelected', $('#flexForSummary'));
        if (ids.length == 0) {
            return false;
        }

        var itemId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var projectId = $('#hidProjectId').val();
        var detailsListUrl = "${createLink(controller:'fixedAssetReport', action: 'getConsumptionAgainstAssetDetails')}?itemId=" + itemId + "&projectId=" + projectId;
        showLoadingSpinner(true);
        $.ajax({
            url: detailsListUrl,
            success: function (data) {
                $("#flexForDetailList").flexOptions({url: detailsListUrl});
                onLoadDetailsListJSON(data)
            },
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function initDetailsGrid() {
        $("#flexForDetailList").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Sl.", name: "inv.id", width: 50, sortable: false, hide: true, align: "left"},
                        {display: "Inventory Name", name: "inv.name", width: 160, sortable: false, align: "left"},
                        {display: "Fixed Asset", name: "fixed_asset", width: 240, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 170, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGridDetails},
                        {separator: true}
                    ],
                    sortname: "inv.name",
                    sortorder: "asc",
                    usepager: false,
                    singleSelect: true,
                    title: 'Consumption Details',
                    useRp: false,
                    showTableToggleBtn: false,
                    width: getGridWidthOfDetailsList(),
                    height: getGridHeight(3)-15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: onLoadDetailsListJSON
                }
        );
    }

    function reloadGridDetails(com, grid) {
        if (com == 'Clear Results') {
            $('#flexForDetailList').flexOptions({query: ''}).flexReload();
        }
    }

    function getGridWidthOfDetailsList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForDetailList").parent().width();
        return gridWidth;
    }

    function onLoadDetailsListJSON(data) {
        doDetailListGridEmpty();
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
        }
        $("#flexForDetailList").flexAddData(data.detailListWrap);
    }

    function doDetailListGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForDetailList").flexAddData(emptyGridModel);
    }

</script>