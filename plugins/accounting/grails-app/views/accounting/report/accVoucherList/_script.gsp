<script type="text/javascript">
    var modelJsonForVoucherList, voucherListGrid, accTypePaymentBankId, accTypePaymentCashId, accTypeReceivedBankId,
            accTypeReceivedCashId, accTypeJournalId, dropDownVoucherType, dropDownIsPosted;

    jQuery(function ($) {
        modelJsonForVoucherList = ${modelJson};

        $('#printVoucherList').click(function () {
            printVoucherList();
            return false;
        });

        $("#flexForVoucherList").click(function () {
            populateVoucherDetailsListGrid();
        });
        initFlexForVoucherList();
        initFlexForVoucherDetailsList();
        onLoadVoucherList();
    });

    function printVoucherList() {
        var voucherTypeId = $('#hidVoucherTypeId').val();
        if (voucherTypeId.length <= 0) {
            showError('First populate voucher list then click print');
            return false;
        }
        if (!customValidateDate($('#hideFromDate'), 'From Date', $('#hideToDate'), 'To Date')) {
            return false;
        }
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var isPosted = $('#hideIsPosted').attr('value');

        showLoadingSpinner(true);
        var params = "?voucherTypeId=" + voucherTypeId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isPosted=" + isPosted;
        if (confirm('Do you want to download the voucher list now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadVoucherList')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function onLoadVoucherList() {
        try {   // Model will be supplied to grid on load _list.gsp
            initializeForm($("#searchForm"), getVoucherList);
            $('.download_icon_set').hide();
            $('span.headingText').html('Voucher List');
            dropDownIsPosted = initKendoDropdown($('#isPosted'), null, null, modelJsonForVoucherList.postList);

            accTypePaymentBankId = parseInt($('#hidPaymentBankId').val());
            accTypePaymentCashId = parseInt($('#hidPaymentCashId').val());
            accTypeReceivedBankId = parseInt($('#hidReceivedBankId').val());
            accTypeReceivedCashId = parseInt($('#hidReceivedCashId').val());
            accTypeJournalId = parseInt($('#hidJournalId').val());
        } catch (e) {
            showError(e.message);
        }

        // update page title
        $(document).attr('title', "MIS - VoucherList");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showVoucherList");
    }

    function executePreConditionToGetVoucherList(voucherTypeId) {
        if (voucherTypeId == '') {
            showError("Please enter a voucher type");
            $('.download_icon_set').hide();
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }


    function getVoucherList() {
        var voucherTypeId = dropDownVoucherType.value();
        if (executePreConditionToGetVoucherList(voucherTypeId) == false) {
            return false;
        }
        var fromDate = $('#fromDate').attr('value');
        var toDate = $('#toDate').attr('value');
        var isPosted = dropDownIsPosted.value();
        doGridEmpty();
        doVoucherDetailsGridEmpty();
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accReport', action: 'searchVoucherList')}?voucherTypeId=" + voucherTypeId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isPosted=" + isPosted,
            success: executePostConditionForVoucherList,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostConditionForVoucherList(data) {
        if (data.isError) {
            showError(data.message);
            $('.download_icon_set').hide();
            return false;
        }
        populateVoucherList(data);
        populateVoucherListGrid(data);
        doVoucherDetailsGridEmpty();
        return false;
    }

    function populateVoucherList(result) {
        $('#hidVoucherTypeId').val(result.voucherTypeId);
        $('#hideFromDate').val(result.fromDate);
        $('#hideToDate').val(result.toDate);
        $('#hideIsPosted').val(result.isPosted);
    }

    function populateVoucherListGrid(data) {
        $('.download_icon_set').show();
        var voucherTypeId = $('#hidVoucherTypeId').attr('value');
        var fromDate = $('#hideFromDate').attr('value');
        var toDate = $('#hideToDate').attr('value');
        var isPosted = dropDownIsPosted.value();
        var voucherListUrl = "${createLink(controller:'accReport', action: 'searchVoucherList')}?voucherTypeId=" + voucherTypeId + "&fromDate=" + fromDate + "&toDate=" + toDate + "&isPosted=" + isPosted;
        $("#flexForVoucherList").flexOptions({url: voucherListUrl});
        $("#flexForVoucherList").flexAddData(data.voucherListWrap);
    }

    function initFlexForVoucherList() {
        $("#flexForVoucherList").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "voucherDate", width: 100, sortable: false, align: "left"},
                        {display: "Id", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "name", width: 80, sortable: false, align: "right"},
                        {display: "Posted", name: "isPosted", width: 40, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucher},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accVoucher/unPostedVoucher">
                        {name: 'Unpost', bclass: 'unPost', onpress: unPostVoucher},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accVoucher/show, /accVoucher/showPayCash,
                            /accVoucher/showPayBank, /accVoucher/showReceiveCash, /accVoucher/showReceiveBank">
                        {name: 'Details', bclass: 'details', onpress: getVoucherDetails},
                        </app:ifAllUrl>
                    ],
                    sortname: "av.voucher_date, av.trace_no",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Voucher List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    width: getGridWidthOfVoucherList(),
                    height: getGridHeight(3) - 33,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadVoucherListListJSON
                }
        );
    }

    function getGridWidthOfVoucherList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForVoucherList").parent().width();
        return gridWidth;
    }

    function onLoadVoucherListListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flexForVoucherList").flexAddData(data.voucherListWrap);
    }

    function checkGrid() {
        var rows = $('table#flexForVoucherList > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Voucher found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForVoucherList").flexAddData(emptyGridModel);
        $("#flexForVoucherList").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function getVoucherDetails(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flexForVoucherList'), 'voucher') == false) {
            return;
        }
        showLoadingSpinner(true);
        var traceNo = $(ids[ids.length - 1]).find('td').eq(3).find('div').text();
        var voucherTypeId = parseInt($('#hidVoucherTypeId').val());
        var url;
        switch (voucherTypeId) {
            case accTypePaymentBankId:
                url = "${createLink(controller:'accVoucher', action: 'showPayBank')}?traceNo=" + traceNo;
                break;
            case accTypePaymentCashId:
                url = "${createLink(controller:'accVoucher', action: 'showPayCash')}?traceNo=" + traceNo;
                break;
            case accTypeReceivedBankId:
                url = "${createLink(controller:'accVoucher', action: 'showReceiveBank')}?traceNo=" + traceNo;
                break;
            case accTypeReceivedCashId:
                url = "${createLink(controller:'accVoucher', action: 'showReceiveCash')}?traceNo=" + traceNo;
                break;
            case accTypeJournalId:
                url = "${createLink(controller:'accVoucher', action: 'show')}?traceNo=" + traceNo;
                break;
            default:
                break
        }
        $.history.load(formatLink(url));
        return false;
    }

    function executePreConForPostVoucher() {
        if (executeCommonPreConditionForSelect($('#flexForVoucherList'), 'voucher') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return false;
        }
        return true;
    }

    function postVoucher(com, grid) {
        if (executePreConForPostVoucher() == false) {
            return;
        }
        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexForVoucherList'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucher,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucher(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            var selectedRows = $('table#flexForVoucherList > tbody > tr.trSelected');
            var postedHtml = "<div style='text-align: left;'>" + 'YES' + "</div>";
            $(selectedRows).find("td").eq(5).html(postedHtml);
            showSuccess(data.message);
        }
    }

    function unPostVoucher(com, grid) {
        if (executePreConForUnPostVoucher() == false) {
            return;
        }

        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexForVoucherList'));
        var url = "${createLink(controller:'accVoucher', action: 'unPostedVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForUnPostVoucher,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConForUnPostVoucher() {
        if (executeCommonPreConditionForSelect($('#flexForVoucherList'), 'item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to Un-post the selected voucher?')) {
            return false;
        }
        return true;
    }

    function executePostConForUnPostVoucher(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            var selectedRows = $('table#flexForVoucherList > tbody > tr.trSelected');
            var postedHtml = "<div style='text-align: left;'>" + 'NO' + "</div>";
            $(selectedRows).find("td").eq(5).html(postedHtml);
            showSuccess(data.message);
        }
    }

    /********************for voucher Grid List****************************/
    function executePreConToShowVoucherDetails(ids) {
        if (ids.length == 0) {
            showError("Please select a voucher to show its details");
            return false;
        }
        return true;
    }

    function populateVoucherDetailsListGrid() {
        var ids = $('.trSelected', $('#flexForVoucherList'));
        if (ids.length == 0) {
            return false;
        }
        showLoadingSpinner(true);
        doVoucherDetailsGridEmpty();
        var voucherId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var voucherDetailsListUrl = "${createLink(controller:'accReport', action: 'listForVoucherDetails')}?voucherId=" + voucherId;
        showLoadingSpinner(true);
        $.ajax({
            url: voucherDetailsListUrl,
            success: function (data) {
                $("#flexForVoucherDetailsList").flexOptions({url: voucherDetailsListUrl});
                $("#flexForVoucherDetailsList").flexAddData(data.voucherDetailsListWrap);
            },
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function initFlexForVoucherDetailsList() {
        $("#flexForVoucherDetailsList").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "SL", name: "serial", width: 20, sortable: false, align: "right", hide: true},
                        {display: "Code", name: "code", width: 40, sortable: false, align: "left"},
                        {display: "Head Name", name: "headNameDr", width: 190, sortable: false, align: "left"},
                        {display: "Debit", name: "amountDr", width: 80, sortable: false, align: "right"},
                        {display: "Credit", name: "amountCr", width: 80, sortable: false, align: "right"},
                        {display: "Particulars", name: "particulars", width: 130, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGridDetails},
                        {separator: true}
                    ],
                    sortname: "avd.id",
                    sortorder: "asc",
                    usepager: false,
                    singleSelect: true,
                    title: 'Voucher Details List',
                    useRp: false,
                    showTableToggleBtn: false,
                    width: getGridWidthOfVoucherDetailsList(),
                    height: getGridHeight(3) - 5,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkVoucherDetailsGrid();
                    },
                    customPopulate: onLoadVoucherDetailsListJSON
                }
        );
    }

    function reloadGridDetails(com, grid) {
        $('#flexForVoucherDetailsList').flexOptions({query: ''}).flexReload();
    }

    function getGridWidthOfVoucherDetailsList() {
        var gridWidth = '100%';
        gridWidth = $("#flexForVoucherDetailsList").parent().width();
        return gridWidth;
    }

    function onLoadVoucherDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            return null;
        }
        $("#flexForVoucherDetailsList").flexAddData(data.voucherDetailsListWrap);
    }

    function checkVoucherDetailsGrid() {
        var rows = $('table#flexForVoucherDetailsList > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No Voucher Details found');
            $('.download_icon_set').hide();
        }
    }

    function doVoucherDetailsGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flexForVoucherDetailsList").flexAddData(emptyGridModel);
        $("#flexForVoucherDetailsList").flexOptions({url: false, newp: 1}).flexReload();
    }

</script>