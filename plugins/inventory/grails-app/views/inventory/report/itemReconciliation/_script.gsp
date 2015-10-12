<script language="javascript" type="text/javascript">
    var dropDownProject,hidProjectId;
    var output = false;
    var itemReconciliationListModel = false;

    $(document).ready(function () {
        onLoadItemReconciliationPage();

        $('#printPdfBtn').click(function () {
            downloadItemReconciliationReport();
        });

        $('#printCsvBtn').click(function () {
            downloadItemReconciliationReportCsv();
        });

    });

    function onLoadItemReconciliationPage() {
        initializeForm($("#itemReconciliationForm"),loadGridForItemReconciliation);

        initGrid();

        //update page title
        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Item Reconciliation Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showForItemReconciliation");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Item", name: "name", width: 220, sortable: false, align: "left"},
                        {display: "Increase(+)", name: "total_increase", width: 120, sortable: false, align: "right"},
                        {display: "Decrease(-)", name: "total_decrease", width: 120, sortable: false, align: "right"},
                        {display: "Shrinkage", name: "total_shrinkage", width: 120, sortable: false, align: "right"},
                        {display: "Pending", name: "total_pending", width: 120, sortable: false, align: "right"},
                        {display: "Stock", name: "total_stock_quantity", width: 120, sortable: false, align: "right"}
                    ],
                    sortname: "item.name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Item Reconciliation List [ Increase(+) - Decrease(-) = Stock ]',
                    useRp: true,
                    rp: 20,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() + 10,
                    customPopulate: populateGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );

    }

    function populateGrid(data) {
        executePostConditionForLoadGrid(data);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Item found');
        }
    }

    function loadGridForItemReconciliation() {
        var projectId = dropDownProject.value();
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        doGridEmpty();
        hidProjectId = projectId;
        $.ajax({
            url: "${createLink(controller: 'invReport', action: 'listForItemReconciliation')}?projectId=" + projectId,
            success: executePostConditionForLoadGrid,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForLoadGrid(data) {
        if (data.isError == true) {
            showError(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
        } else {
            var url = "${createLink(controller: 'invReport', action: 'listForItemReconciliation')}?projectId=" + hidProjectId;
            $("#flex1").flexOptions({url: url});
            $("#flex1").flexAddData(data.gridOutput);
            $('.download_icon_set').show();
        }
    }
    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function downloadItemReconciliationReport() {

        var confirmMsg = 'Do you want to download the report in pdf format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller: 'invReport', action: 'downloadForItemReconciliation')}?projectId=" + hidProjectId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadItemReconciliationReportCsv() {

        var confirmMsg = 'Do you want to download the report in csv format?';

        showLoadingSpinner(true);
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller: 'invReport', action: 'downloadForItemReconciliationCsv')}?projectId=" + hidProjectId;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>