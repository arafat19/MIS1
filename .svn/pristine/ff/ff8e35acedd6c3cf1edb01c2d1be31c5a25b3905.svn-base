<script type="text/javascript">
    var modelJsonForItemStockList;
    
    jQuery(function ($) {
        modelJsonForItemStockList = ${modelJson};

        $("#flexForItemStockList").click(function () {
            populateItemStockDetailsListGrid();
        });

        onLoadItemStockList();
        initItemStockListGrid();
        initItemStockDetailsGrid();
    });

    function onLoadItemStockList() {

        // update page title
        $('span.headingText').html('Item Stock List');
        $('#icon_box').attr('class', 'pre-icon-header item-stock');
        $(document).attr('title', "MIS - Item Stock List");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showItemStock");
    }

    function populateItemStockListGrid(data) {
        var itemStockListUrl = "${createLink(controller:'invReport', action: 'listItemStock')}";
        $("#flexForItemStockList").flexOptions({url:itemStockListUrl});
        onLoadItemStockListJSON(data);
    }

    function initItemStockListGrid() {
        $("#flexForItemStockList").flexigrid
                (
                    {
                        url:false,
                        dataType:'json',
                        colModel:[
                            {display:"Serial", name:"serial", width:50, sortable:false, align:"right"},
                            {display:"Item Name", name:"name", width:250, sortable:false, align:"left"},
                            {display:"Total Quantity", name:"item_quantity", width:180, sortable:true, align:"right"}
                        ],
                        buttons:[
                            {name:'Clear Results', bclass:'clear-results', onpress:reloadItemStockGrid},
                            {separator:true}
                        ],
                        searchitems:[
                            {display:"Item", name:"name", width:180, sortable:true, align:"left"}
                        ],
                        sortname:"name",
                        sortorder:"asc",
                        usepager:true,
                        singleSelect:true,
                        title:'Item Stock List',
                        useRp:true,
                        rp:25,
                        rpOptions:[15, 20, 25, 30],
                        showTableToggleBtn:false,
                        width:getGridWidthOfVoucherList(),
                        height:getGridHeight(5),
                        afterAjax:function (XMLHttpRequest, textStatus) {
                            showLoadingSpinner(false);// Spinner hide after AJAX Call
                            checkGrid();
                        },
                        customPopulate:onLoadItemStockListJSON
                    }
                );

    }

    function initItemStockDetailsGrid() {
        $("#flexForItemStockDetailsList").flexigrid
                (
                        {
                            url:false,
                            dataType:'json',
                            colModel:[
                                {display:"SL", name:"serial", width:20, sortable:false, align:"right", hide:true},
                                {display:"Type", name:"name", width:30, sortable:false, align:"left" , hide:true},
                                {display:"Inventory Name", name:"name", width:120, sortable:false, align:"left"},
                                {display:"Stock Quantity", name:"quantity", width:100, sortable:false, align:"right"},
                                {display:"Unapproved(+)", name:"unapproved_quantity_plus", width:100, sortable:false, align:"right"},
                                {display:"Unapproved(-)", name:"unapproved_quantity_minus", width:100, sortable:false, align:"right"}
                            ],
                            buttons:[
                                {separator:true}
                            ],
                            sortname:"name",
                            sortorder:"asc",
                            usepager:false,
                            singleSelect:true,
                            title:'Stock Details List',
                            useRp:false,
                            showTableToggleBtn:false,
                            width:getGridWidthOfStockDetailsList(),
                            height:getGridHeight(3),
                            afterAjax:function (XMLHttpRequest, textStatus) {
                                showLoadingSpinner(false);// Spinner hide after AJAX Call
                                checkStockDetailsGrid();
                            },
                            customPopulate:onLoadStockDetailsListJSON
                        }
                );

    }

    function reloadItemStockGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flexForItemStockList').flexOptions({query:''}).flexReload();
        }
    }
    function getGridWidthOfVoucherList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForItemStockList").parent().width();
        return gridWidth;
    }

    function onLoadItemStockListJSON(data) {
        doStockDetailsListGridEmpty();
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flexForItemStockList").flexAddData(data.itemStockList);
    }

    function checkGrid() {
        var rows = $('table#flexForItemStockList > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Item Stock found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForItemStockList").flexAddData(emptyGridModel);
        $("#flexForItemStockList").flexOptions({url:false, newp:1,rp:20}).flexReload();
        $('select[name=rp]').val(20);
    }

    /********************for Item Stock Details Grid List****************************/

    function populateItemStockDetailsListGrid() {
        doStockDetailsListGridEmpty();
        var ids = $('.trSelected', $('#flexForItemStockList'));
        if (ids.length == 0) {
            return false;
        }

        var quantityWithUnit = $(ids[ids.length - 1]).find('td').eq(2).find('div').text();
        var arr = quantityWithUnit.split(' ');
        var quantity = parseFloat(arr[0]);
        if (quantity <= 0) {
            return false;
        }
        showLoadingSpinner(true);
        var itemId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var stockDetailsListUrl = "${createLink(controller:'invReport', action: 'getStockDetailsListByItemId')}?itemId=" + itemId;
        showLoadingSpinner(true);
        $.ajax({
            url:stockDetailsListUrl,
            success:function (data) {
                $("#flexForItemStockDetailsList").flexOptions({url:stockDetailsListUrl});
                onLoadStockDetailsListJSON(data);
            },
            complete:onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType:'json',
            type:'post'
        });
        return false;
    }


    function reloadGridDetails(com, grid) {
        if (com == 'Refresh') {
            $('#flexForItemStockDetailsList').flexOptions({query:''}).flexReload();
        }
    }

    function getGridWidthOfStockDetailsList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForItemStockDetailsList").parent().width();
        return gridWidth;
    }

    function onLoadStockDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flexForItemStockDetailsList").flexAddData(data.itemStockDetailsList);
    }

    function checkStockDetailsGrid() {
        var rows = $('table#flexForItemStockDetailsList > tbody > tr');
        if (rows && rows.length < 1) {
            showError('No Purchase Order List found');
        }
    }

    function doStockDetailsListGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForItemStockDetailsList").flexAddData(emptyGridModel);
        $("#flexForItemStockDetailsList").flexOptions({url:false, newp:1}).flexReload();
    }

    window.onload = populateItemStockListGrid(modelJsonForItemStockList);

</script>