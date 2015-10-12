<script type="text/javascript">

    $(document).ready(function() {
        onLoadDecisionSummary();
    });
    function onLoadDecisionSummary() {
        initializeForm($("#decisionSummaryForm"), onSubmitForm);
        $('#printPdfBtn').click(function() {
            downloadDecisionSummaryPdf();
        });
        initFlex();

        $(document).attr('title', "ARMS - Decision Summary");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsReport/showDecisionSummary");
    }

    function onSubmitForm() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        setButtonDisabled($('#search'), true);
        showLoadingSpinner(true);
        var strUrl = "${createLink(controller:'rmsReport',action: 'listDecisionSummary')}";
        $.ajax({
            url: strUrl,
            data: jQuery("#decisionSummaryForm").serialize(),
            success: function (data, textStatus) {
                executePostConditionForSearch(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
                setButtonDisabled($('#search'), false);
            },
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function populateFlexGrid(data) {
        if (data.isError) {
            showError(data.message);
            $("#flex").flexAddData(getEmptyGridModel());
        } else {
            $("#flex").flexAddData(data.gridObj);
        }
    }

    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Process", name: "process", width: 100, sortable: false, align: "left"},
                        {display: "Instrument", name: "instrument", width: 100, sortable: false, align: "left"},
                        {display: "Bank", name: "Bank", width: 250, sortable: false, align: "left"},
                        {display: "Branch", name: "Branch", width: 120, sortable: false, align: "left"},
                        {display: "District", name: "district", width: 120, sortable: false, align: "left"},
                        {display: "Total Task", name: "total_task", width: 120, sortable: false, align: "right"},
                        {display: "Total Amount", name: "total_amount", width: 120, sortable: false, align: "right"}
                    ],
                    sortname: "process",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All Task',
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateFlexGrid
                }
        );
    }

    function executePostConditionForSearch(result) {
        populateFlexGrid(result);
        setUrlFlexGrid();
    }

    function reloadGrid() {
        $('#flex').flexOptions({query:''}).flexReload();
    }

    function setUrlFlexGrid() {
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate;
        var strUrl = "${createLink(controller: 'rmsReport', action: 'listDecisionSummary')}" + params;
        $("#flex").flexOptions({url: strUrl});
    }

    function downloadDecisionSummaryPdf() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the pdf now?')) {
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();
            var params = "?fromDate=" + fromDate + "&toDate=" + toDate;
            var url = "${createLink(controller: 'rmsReport', action: 'downloadDecisionSummaryReport')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }


</script>