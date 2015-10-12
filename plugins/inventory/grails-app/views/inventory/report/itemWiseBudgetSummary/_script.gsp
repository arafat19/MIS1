<script type="text/javascript">
    var dropDownProject;

    jQuery(function ($) {
        $('#printItemWiseSummary').click(function () {
            printItemWiseSummary();
            return false;
        });
        $('#printItemWiseSummaryCSV').click(function () {
            printItemWiseSummaryCsv();
            return false;
        });
        onLoadItemWiseSummary();
    });

    function onLoadItemWiseSummary() {
        initializeForm($("#searchForm"),getItemWiseSummary);

        initFlexGrid();
        // update page title
        $('.download_icon_set').hide();
        $(document).attr('title', "MIS - Item Wise Budget Summary");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showItemWiseBudgetSummary");
    }

    function printItemWiseSummary() {
        var confirmMsg = 'Do you want to download the Item Wise Budget Summary in pdf format?';

        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First select a project then click print');
            return false;
        }
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadItemWiseBudgetSummary')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function printItemWiseSummaryCsv() {
        var confirmMsg = 'Do you want to download the Item Wise Budget Summary in csv format?';

        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First select a project then click print');
            return false;
        }
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm(confirmMsg)) {
            var url = "${createLink(controller:'invReport', action: 'downloadItemWiseBudgetSummaryCsv')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
        return true;
    }

    function executePreConditionToGetSummary() {
        if (!validateForm($("#searchForm"))) {
            doGridEmpty();
            return false;
        }
        return true;
    }

    function getItemWiseSummary() {
        if (executePreConditionToGetSummary() == false) {
            return false;
        }

        var projectId = dropDownProject.value();
        showLoadingSpinner(true);
        $.ajax({
            url:"${createLink(controller:'invReport', action: 'listItemWiseBudgetSummary')}?projectId=" + projectId,
            success:executePostConditionForSummary,
            complete:onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType:'json',
            type:'post'
        });
        return false;
    }

    function executePostConditionForSummary(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return;
        }
        populateItemWiseGridSummary(data);
        return false;
    }

    function populateItemWiseGridSummary(data) {
        $('.download_icon_set').show();
        var projectId = data.projectId;
        $('#hidProjectId').val(projectId);
        var itemWiseSummaryUrl = "${createLink(controller:'invReport', action: 'listItemWiseBudgetSummary')}?projectId=" + projectId;
        $("#flex1").flexOptions({url:itemWiseSummaryUrl});
       onLoadBudgetPoListJSON(data);
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
            (
                {
                    url:false,
                    dataType:'json',
                    colModel:[
                        {display:"Serial", name:"serial", width:35, sortable:false, align:"right", hide:false},
                        {display:"Item", name:"item", width:250, sortable:false, align:"left"},
                        {display:"Total Budget", name:"budget_price", width:120, sortable:false, align:"right"},
                        {display:"PO Issued", name:"po_price", width:120, sortable:false, align:"right"},
                        {display:"PO Remaining", name:"remaining", width:120, sortable:false, align:"right"},
                        {display:"Total Inventory In", name:"remaining", width:120, sortable:false, align:"right"},
                        {display:"Total Consumption", name:"remaining", width:120, sortable:false, align:"right"}
                    ],
                    buttons:[
                        {name:'Clear Results', bclass:'clear-results', onpress:reloadGrid},
                        {separator:true}
                    ],
                    sortname:"item",
                    sortorder:"asc",
                    usepager:true,
                    singleSelect:true,
                    title:'Item Wise Amount Total',
                    useRp:true,
                    rp:20,
                    rpOptions:[10, 20, 25, 30],
                    showTableToggleBtn:false,
                    height:getGridHeight() - 10,
                    afterAjax:function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate:onLoadBudgetPoListJSON
                }
            );
    }

    function onLoadBudgetPoListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            return false;
        }
        if (data.itemWiseSummaryList.total==0) {
            showInfo('Budget Not Found in This Project');
            doGridEmpty();

            return false;
        }
        $("#flex1").flexAddData(data.itemWiseSummaryList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Budget Wise Qs found');
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query:''}).flexReload();
        }
    }
    function doGridEmpty() {
        $('.download_icon_set').hide();
        $("#flex1").flexOptions({url:false, newp:1, rp:20}).flexReload();
        $('select[name=rp]').val(20);
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
    }

</script>