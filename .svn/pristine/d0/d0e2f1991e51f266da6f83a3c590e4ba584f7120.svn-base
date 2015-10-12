<script type="text/javascript">
    $(document).ready(function () {
        initFlexCancelledVoucher();
        // update page title
        $(document).attr('title', "MIS - Cancelled Voucher");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accCancelledVoucher/showCancelledVoucher");

    });
    function initFlexCancelledVoucher() {
        $("#flex1").flexigrid
        (
                {
                    url: "${createLink(controller: 'accCancelledVoucher', action: 'listCancelledVoucher')}",
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 35, sortable: false, align: "right", hide: "true"},
                        {display: "Trace No", name: "id", width: 120, sortable: false, align: "left"},
                        {display: "Voucher Date", name: "voucher_date", width: 100, sortable: true, align: "left"},
                        {display: "Amount", name: "name", width: 120, sortable: false, align: "right"},
                        {display: "Dr Count", name: "drCount", width: 60, sortable: false, align: "right"},
                        {display: "Cr Count", name: "crCount", width: 60, sortable: false, align: "right"},
                        {display: "Posted", name: "isPosted", width: 50, sortable: false, align: "left"},
                        {display: "Cancelled By", name: "cancelledBy", width: 120, sortable: false, align: "left"},
                        {display: "Type", name: "accVoucherType", width: 150, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <sec:access url="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </sec:access>
                        {name: 'Refresh', bclass: 'refresh', onpress: refreshGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All  Cancelled Voucher',
                    useRp: true,
                    rp: 25,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateCancelledVoucherGrid
                }
        );
    }

    function viewVoucherReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return;
        }
        showLoadingSpinner(true);

        var voucherId = getSelectedIdFromGrid($('#flex1'));

        var cancelledVoucher = true;
        var loc = "${createLink(controller:'accReport', action: 'showVoucher')}?voucherId=" + voucherId + "&cancelledVoucher=" + cancelledVoucher;
        $.history.load(formatLink(loc));
        return false;
    }


    function populateCancelledVoucherGrid(data) {
        var gridData;
        if (data.isError) {
            showError(data.message);
            gridData = getEmptyGridModel();
        } else {
            gridData = data.gridObj;
        }
        $('#flex1').flexAddData(gridData);
        return false;
    }

    function refreshGrid(com, grid) {
        $('#flex1').flexReload();
    }
</script>

