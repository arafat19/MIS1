<script type="text/javascript">
    var output = false;
    var cancelledPOListModel = false;

    $(document).ready(function () {
        onLoadPurchaseOrder();
    });
    function onLoadPurchaseOrder() {
        initGrid();
        output =${output ? output : ''};
        if (output.isError) {
            showError(data.message);
        } else {
            cancelledPOListModel = output.purchasedOrderList;
        }
        populateFlex1();
        // update page title
        $(document).attr('title', "MIS - Cancelled Purchase Order");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procCancelledPO/show");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right",hide: true},
                        {display: "PO No", name: "id", width: 50, sortable: true, align: "right"},
                        {display: "Date", name: "createdOn", width: 80, sortable: true, align: "center"},
                        {display: "Cancelled Date", name: "cancelledOn", width: 90, sortable: true, align: "center"},
                        {display: "PR No", name: "purchaseRequestId", width: 50, sortable: true, align: "right"},
                        {display: "Supplier", name: "supplierId", width: 180, sortable: false, align: "left"},
                        {display: "Item(s)", name: "materialCount", width: 45, sortable: false, align: "right"},
                        {display: "Discount", name: "discount", width: 80, sortable: false, align: "right"},
                        {display: "Net Price", name: "netPrice", width: 110, sortable: false, align: "right"},
                        {display: "Tr. Cost", name: "total_transport_cost", width: 80, sortable: false, align: "right"},
                        {display: "VAT/Tax", name: "total_vat_tax", width: 70, sortable: false, align: "right"},
                        {display: "Approved(Dir.)", name: "approvedByDirector", width: 85, sortable: false, align: "center"},
                        {display: "Approved(PD)", name: "approvedByProjectDirector", width: 85, sortable: false, align: "center"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/procReport/showPurchaseOrderRpt">
                        {name: 'Report', bclass: 'report', onpress: viewPurchaseOrderReport},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Cancelled Purchase Order',
                    useRp: true,
                    rp: 25,
                    rpOptions: [20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: customPopulateGrid
                }
        );
    }

    function viewPurchaseOrderReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'purchase order') == false) {
            return;
        }

        showLoadingSpinner(true);

        var purchaseOrderId = getSelectedIdFromGrid($('#flex1'));
        var params = "?purchaseOrderId=" + purchaseOrderId + "&cancelledPo=" + true;
        var loc = "${createLink(controller: 'procReport', action: 'showPurchaseOrderRpt')}" + params;

        $.history.load(formatLink(loc));
        return false;
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            cancelledPOListModel = getEmptyGridModel();
        } else {
            cancelledPOListModel = data.purchasedOrderList;
        }
        $("#flex1").flexAddData(cancelledPOListModel);
        return false;
    }

    function populateFlex1() {
        var strUrl = "${createLink(controller:'procCancelledPO', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if(cancelledPOListModel){
            $("#flex1").flexAddData(cancelledPOListModel);
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

</script>
