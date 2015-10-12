<script type="text/javascript">
    jQuery(function ($) {
        onLoadData();

    });
    //Global variables
    var taskGridModel;
    var paramForSearch;
    var createdDateFrom, createdDateTo;

    function onLoadData() {
        try {

            initializeForm($('#filterAgentForm'),populateAgentWiseCommission);

            $('#downloadBtn').click(function () {
                downloadAgentWiseCommission();
            });

            initFlexAgentCommission();

            // update page title
            $(document).attr('title', "ARMS - Show Agent Wise Commission");
            loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showAgentWiseCommissionForAgent");
        } catch (e) {
            showError('Error occurred on page load');
        }
    }

    function populateAgentWiseCommission() {
        if (!checkDates($('#createdDateFrom'), $('#createdDateTo'))) return false;

        createdDateFrom = $('#createdDateFrom').val();
        createdDateTo = $('#createdDateTo').val();

        paramForSearch = "?createdDateFrom=" + $('#createdDateFrom').val() + "&createdDateTo=" + $('#createdDateTo').val();
        // First reset the grid
        var obj = getEmptyGridModel();
        $('#flex1').flexOptions({url: false}).flexAddData(obj);
        $('#flex1').flexOptions({url: false, query: ''}).flexReload();
        var strUrl = "${createLink(controller: 'exhReport', action: 'listAgentWiseCommissionForAgent')}" + paramForSearch;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex1').flexOptions({url: strUrl, query: ''}).flexReload();
        return false;
    }

    function initFlexAgentCommission(localCurrency) {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
//                            {display:"ID", name:"id", width:30, sortable:false, align:"right", hide:true},
                        {display: "Date", name: "created_on", width: 125, sortable: true, align: "left"},
                        {display: "Total Task", name: "task_count", width: 100, sortable: true, align: "right"},
                        {display: "Total Amount(" + $('#hidLocalCurrency').val() + ")", name: "total_amount", width: 150, sortable: false, align: "right"},
                        {display: "Regular Fee", name: "total_regular_fee", width: 130, sortable: true, align: "right"},
                        {display: "Commission", name: "total_commission", width: 110, sortable: true, align: "left"},
                        {display: "Discount", name: "total_discount", width: 110, sortable: true, align: "right"},
                        {display: "Net Commission", name: "net_commission", width: 130, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "count",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Commissions',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    //width: 725,
                    height: getGridHeight(),
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );
    }


    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Commission found');
            return false;
        }
    }

    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'exhReport', action: 'listAgentWiseCommissionForAgent')}" + paramForSearch;
        $("#flex1").flexOptions({url: strUrl});
        if (taskGridModel) {
            $("#flex1").flexAddData(taskGridModel);
        }
    }

    function downloadAgentWiseCommission() {

        if (!checkDates($('#createdDateFrom'), $('#createdDateTo'))) return false;

        paramForSearch = "?createdDateFrom=" + $('#createdDateFrom').val() + "&createdDateTo=" + $('#createdDateTo').val();

        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadAgentWiseCommissionForAgent')}" + paramForSearch;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

</script>