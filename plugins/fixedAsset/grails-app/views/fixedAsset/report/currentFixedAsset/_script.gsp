<script type="text/javascript">
    var validatorSearch, dropDownItemId;
    var modelJsonForPendingFixedAsset;
    jQuery(function ($) {
        modelJsonForPendingFixedAsset = ${modelJson};

        $('#printCurrentFixedAsset').click(function () {
            printCurrentFixedAsset();
            return false;
        });
        $('#printCurrentFixedAssetCsv').click(function () {
            printCurrentFixedAssetCsv();
            return false;
        });
        onLoadPendingFixedAsset();
    });

    function printCurrentFixedAsset() {
        var itemId = dropDownItemId.value()?dropDownItemId.value():-1;

        showLoadingSpinner(true);
        var params = "?itemId=" + itemId;
        if (confirm('Do you want to download the Current Fixed Asset Report now?')) {
            var url = "${createLink(controller:'fixedAssetReport', action: 'downloadCurrentFixedAsset')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function printCurrentFixedAssetCsv() {
        var itemId = dropDownItemId.value()?dropDownItemId.value():-1;

        showLoadingSpinner(true);
        var params = "?itemId=" + itemId;
        if (confirm('Do you want to download the Current Fixed Asset Csv Report now?')) {
            var url = "${createLink(controller:'fixedAssetReport', action: 'downloadCurrentFixedAssetCsv')}" + params;
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
        $(document).attr('title', "MIS - Current Fixed Asset");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#fixedAssetReport/showCurrentFixedAsset");

    }


    function getPendingFixedAsset() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        var itemId = dropDownItemId.value()?dropDownItemId.value():-1;
        showLoadingSpinner(true);
        $.ajax({
            url:"${createLink(controller:'fixedAssetReport', action: 'listCurrentFixedAsset')}?itemId=" + itemId ,
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
        $('#hidItemId').val(result.itemId);
    }

    function resetPendingFixedAssetForm() {
        doGridEmpty();
        $('.download_icon_set').hide();
        $('#hidItemId').val('');
    }

    function populatePendingFixedAssetGrid(data) {
        $('.download_icon_set').show();
        var itemId = dropDownItemId.value()?dropDownItemId.value():-1;
        $('#hidItemId').val(itemId);
        var PendingFixedAssetUrl = "${createLink(controller:'fixedAssetReport', action: 'listCurrentFixedAsset')}?itemId=" + itemId;
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
                        {display:"Current Location", name:"inventory", width:220, sortable:false, align:"left"},
                        {display:"Category", name:"category", width:250, sortable:false, align:"left"},
                        {display:"Model/Serial", name:"model", width:220, sortable:false, align:"left"},
                        {display:"Purchase Date", name:"purchase_date", width:100, sortable:false, align:"left"},
                        {display:"Price", name:"price", width:110, sortable:false, align:"right"}
                    ],
                    buttons:[
                        {name:'Clear Results', bclass:'clear-results', onpress:reloadPendingFixedAssetGrid},
                        {separator:true}
                    ],
                    searchitems:[
                        {display:"Model/Serial", name:"fad.name", width:180, sortable:true, align:"left"}
                    ],
                    sortname:"fad.name",
                    sortorder:"asc",
                    usepager:true,
                    singleSelect:true,
                    title:'Current Fixed Asset',
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
        $("#flex1").flexAddData(data.currentFixedAssetList);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Current Fixed Asset found');
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