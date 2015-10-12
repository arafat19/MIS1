<script type="text/javascript">
    var dropDownProjectId;

    jQuery(function ($) {
        $('#printPendingFixedAsset').click(function () {
            printPendingFixedAsset();
            return false;
        });
        onLoadPendingFixedAsset();
    });

    function printPendingFixedAsset() {
        var projectId = $('#hidProjectId').attr('value');
        if (projectId.length <= 0) {
            showError('First populate Pending Fixed Asset then click print');
            return false;
        }
        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm('Do you want to download the Pending Fixed Asset Report now?')) {
            var url = "${createLink(controller:'fixedAssetReport', action: 'downloadPendingFixedAsset')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadPendingFixedAsset() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#searchForm"), getPendingFixedAsset);

        try {   // Model will be supplied to grid on load _list.gsp
            $('.download_icon_set').hide();
            initFlexGrid();
        } catch (e) {
            showError(e.message);
            $('.download_icon_set').hide();
        }
        // update page title
        $('span.headingText').html('PendingFixedAsset');
        $('#icon_box').attr('class', 'pre-icon-header Pending Fixed Asset');
        $(document).attr('title', "MIS - Pending Fixed Asset");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fixedAssetReport/showPendingFixedAsset");

    }

    function executePreConditionToGetPendingFixedAsset() {
        var projectId = dropDownProjectId.value();
        if (projectId == '') {
            showError("Please select a Project");
            return false;
        }
        return true;
    }

    function getPendingFixedAsset() {
        //    resetBudgetForm();
        if (executePreConditionToGetPendingFixedAsset() == false) {
            return false;
        }

        if (!validateForm($("#searchForm"))) {
            return false;
        }

        var projectId = dropDownProjectId.value();

        showLoadingSpinner(true);
        $.ajax({
            url:"${createLink(controller:'fixedAssetReport', action: 'listPendingFixedAsset')}?projectId=" + projectId ,
            success:executePostConditionForPendingFixedAsset,
            complete:onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType:'json',
            type:'post'
        });
        return false;
    }

    function executePostConditionForPendingFixedAsset(data) {
        populatePendingFixedAssetGrid(data)
        return false;

    }

    function populatePendingFixedAsset(result) {
        $('#hidProjectId').val(result.projectId);
    }

    function resetPendingFixedAssetForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hidProjectId').val('');
    }

    function populatePendingFixedAssetGrid(data) {
        $('.download_icon_set').show();
        var projectId = data.projectId;
        $('#hidProjectId').val(projectId);
        var PendingFixedAssetUrl = "${createLink(controller:'fixedAssetReport', action: 'listPendingFixedAsset')}?projectId=" + projectId;
        emptyGrid();
        $("#flex1").flexOptions({url:PendingFixedAssetUrl});
        onLoadPendingFixedAssetListJSON(data);
    }

    function emptyGrid() {
        var emptyModel = getEmptyGridModel();
        $("#flex1").flexOptions({url:false});
        $("#flex1").flexAddData(emptyModel);
    }


    function initFlexGrid() {
        $("#flex1").flexigrid
            (
                {
                    url:false,
                    dataType:'json',
                    colModel:[
                        {display:"Serial", name:"serial", width:25, sortable:false, align:"right", hide:false},
                        {display:"PO Trace No", name:"po_trace_no", width:70, sortable:false, align:"right"},
                        {display:"Category", name:"item_name", width:200, sortable:false, align:"left"},
                        {display:"PO Quantity", name:"received", width:130, sortable:false, align:"right"},
                        {display:"Asset Found", name:"asset_found", width:130, sortable:false, align:"right"},
                        {display:"Asset Remaining", name:"asset_remaining", width:130, sortable:false, align:"right"}
                    ],
                    buttons:[
                        {name:'Clear Results', bclass:'clear-results', onpress:reloadPendingFixedAssetGrid},
                        {separator:true}
                    ],
                    sortname:"item.name",
                    sortorder:"asc",
                    usepager:true,
                    singleSelect:true,
                    title:'Pending Fixed Asset',
                    useRp:true,
                    rp:20,
                    rpOptions:[10, 20, 25, 30],
                    showTableToggleBtn:false,
                    height:getGridHeight()-15,
                    afterAjax:function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate:onLoadPendingFixedAssetListJSON
                }
            );
    }

    function onLoadPendingFixedAssetListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            emptyGrid();
            $('.download_icon_set').hide();
            return false;
        }
        $("#flex1").flexAddData(data.lstPendingFixedAsset);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Pending Fixed Asset found');
            $('.download_icon_set').hide();
        }
    }

    function reloadPendingFixedAssetGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query:''}).flexReload();
        }
    }
    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url:false, newp:1}).flexReload();
        $('select[name=rp]').val(20);
    }


</script>