<%@ page import="com.athena.mis.application.utility.RoleUtility; org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var payCashListModel = false;
    var payBankListModel = false;
    var receiveCashListModel = false;
    var receiveBankListModel = false;
    var journalListModel = false;

    var outputPayCash =${outputPayCash ? outputPayCash : ''};
    var outputPayBank =${outputPayBank ? outputPayBank : ''};
    var outputReceiveCash =${outputReceiveCash ? outputReceiveCash : ''};
    var outputReceiveBank =${outputReceiveBank ? outputReceiveBank : ''};
    var outputJournal =${outputJournal ? outputJournal : ''};

    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );
        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        <sec:access url="/accVoucher/listOfUnApprovedPayCash">
        initPayCashGrid();
        setUrlPayCashGrid();
        populatePayCashGrid(outputPayCash);
        payCashListModel = outputPayCash.gridObj;
        </sec:access>

        <sec:access url="/accVoucher/listOfUnApprovedPayBank">
        initPayBankGrid();
        setUrlPayBankGrid();
        populatePayBankGrid(outputPayBank);
        payBankListModel = outputPayBank.gridObj;
        </sec:access>

        <sec:access url="/accVoucher/listOfUnApprovedReceiveCash">
        initReceiveCashGrid();
        setUrlReceiveCashGrid();
        populateReceiveCashGrid(outputReceiveCash);
        receiveCashListModel = outputReceiveCash.gridObj;
        </sec:access>

        <sec:access url="/accVoucher/listOfUnApprovedReceiveBank">
        initReceiveBankGrid();
        setUrlReceiveBankGrid();
        populateReceiveBankGrid(outputReceiveBank);
        receiveBankListModel = outputReceiveBank.gridObj;
        </sec:access>

        <sec:access url="/accVoucher/listOfUnApprovedJournal">
        initJournalGrid();
        setUrlJournalGrid();
        populateJournalGrid(outputJournal);
        journalListModel = outputJournal.gridObj;
        </sec:access>
    }

    // -------- Pay-Cash -------- \\
    function initPayCashGrid() {
        $("#flexUnapprovedPayCash").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "id", width: 150, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "id", width: 85, sortable: false, align: "left"},
                        {display: "Created By", name: "id", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucherPayCash},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadPayCashGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unposted Cash Payment',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populatePayCashGrid
                }
        );
    }

    function setUrlPayCashGrid() {
        var strUrl = "${createLink(controller: 'accVoucher', action: 'listOfUnApprovedPayCash')}";
        $("#flexUnapprovedPayCash").flexOptions({url: strUrl});
    }

    function reloadPayCashGrid() {
        $('#flexUnapprovedPayCash').flexOptions({query: ''}).flexReload();
    }

    function populatePayCashGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedPayCash").flexAddData(gridObj);
    }

    // -------- Pay-Bank -------- \\
    function initPayBankGrid() {
        $("#flexUnapprovedPayBank").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "id", width: 150, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "id", width: 85, sortable: false, align: "left"},
                        {display: "Created By", name: "id", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucherPayBank},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadPayBankGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unposted Cheque Payment',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populatePayBankGrid
                }
        );
    }

    function setUrlPayBankGrid() {
        var strUrl = "${createLink(controller: 'accVoucher', action: 'listOfUnApprovedPayBank')}";
        $("#flexUnapprovedPayBank").flexOptions({url: strUrl});
    }

    function reloadPayBankGrid() {
        $('#flexUnapprovedPayBank').flexOptions({query: ''}).flexReload();
    }

    function populatePayBankGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedPayBank").flexAddData(gridObj);
    }

    // -------- Receive-Cash -------- \\
    function initReceiveCashGrid() {
        $("#flexUnapprovedReceiveCash").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "id", width: 150, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "id", width: 85, sortable: false, align: "left"},
                        {display: "Created By", name: "id", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucherReceiveCash},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadReceiveCashGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unposted Cash Receive',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateReceiveCashGrid
                }
        );
    }

    function setUrlReceiveCashGrid() {
        var strUrl = "${createLink(controller: 'accVoucher', action: 'listOfUnApprovedReceiveCash')}";
        $("#flexUnapprovedReceiveCash").flexOptions({url: strUrl});
    }

    function reloadReceiveCashGrid() {
        $('#flexUnapprovedReceiveCash').flexOptions({query: ''}).flexReload();
    }

    function populateReceiveCashGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedReceiveCash").flexAddData(gridObj);
    }

    // -------- Receive-Bank -------- \\
    function initReceiveBankGrid() {
        $("#flexUnapprovedReceiveBank").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "id", width: 150, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "id", width: 85, sortable: false, align: "left"},
                        {display: "Created By", name: "id", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucherReceiveBank},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadReceiveBankGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unposted Cheque Receive',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateReceiveBankGrid
                }
        );
    }

    function setUrlReceiveBankGrid() {
        var strUrl = "${createLink(controller: 'accVoucher', action: 'listOfUnApprovedReceiveBank')}";
        $("#flexUnapprovedReceiveBank").flexOptions({url: strUrl});
    }

    function reloadReceiveBankGrid() {
        $('#flexUnapprovedReceiveBank').flexOptions({query: ''}).flexReload();
    }

    function populateReceiveBankGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedReceiveBank").flexAddData(gridObj);
    }

    // -------- Journal -------- \\
    function initJournalGrid() {
        $("#flexUnapprovedJournal").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "id", width: 150, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "id", width: 85, sortable: false, align: "left"},
                        {display: "Created By", name: "id", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucherJournal},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadJournalGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Unposted Journal',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateJournalGrid
                }
        );
    }

    function setUrlJournalGrid() {
        var strUrl = "${createLink(controller: 'accVoucher', action: 'listOfUnApprovedJournal')}";
        $("#flexUnapprovedJournal").flexOptions({url: strUrl});
    }

    function reloadJournalGrid() {
        $('#flexUnapprovedJournal').flexOptions({query: ''}).flexReload();
    }

    function populateJournalGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedJournal").flexAddData(gridObj);
    }

    // -------- To view voucher report -------- \\
    function executePreConditionToViewReport(ids) {
        if (ids.length == 0) {
            showError("Please select a voucher");
            return false;
        }
        return true;
    }

    function viewVoucherReport(com, grid) {
        var ids = $('.trSelected', grid);
        if (ids.length == 0) {
            showError("Please select a voucher to view report.");
            return false;
        }
        showLoadingSpinner(true);

        var voucherId = $(ids[ids.length - 1]).attr('id').replace('row', '');

        var loc = "${createLink(controller:'accReport', action: 'showVoucher')}?voucherId=" + voucherId;
        $.history.load(formatLink(loc));
        return false;
    }


    // -------- For Pay cash Voucher Posting -------- \\
    function postVoucherPayCash(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPayCash'), 'voucher') == false) {
            return;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return;
        }

        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexUnapprovedPayCash'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucherPayCash,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucherPayCash(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                var selectedRow = null;
                $('.trSelected', $('#flexUnapprovedPayCash')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                payCashListModel.total = parseInt(payCashListModel.total) - 1;
                removeEntityFromGridRows(payCashListModel, selectedRow);
                $('#flexUnapprovedPayCash').flexAddData(payCashListModel);
                reloadPayCashGrid();
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // -------- For Pay Bank Voucher Posting -------- \\
    function postVoucherPayBank(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPayBank'), 'voucher') == false) {
            return;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return;
        }


        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexUnapprovedPayBank'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucherPayBank,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucherPayBank(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                var selectedRow = null;
                $('.trSelected', $('#flexUnapprovedPayBank')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                payBankListModel.total = parseInt(payBankListModel.total) - 1;
                removeEntityFromGridRows(payBankListModel, selectedRow);
                $('#flexUnapprovedPayBank').flexAddData(payBankListModel);
                reloadPayBankGrid();
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // -------- For Receive Cash Voucher Posting -------- \\
    function postVoucherReceiveCash(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedReceiveCash'), 'voucher') == false) {
            return;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return;
        }
        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexUnapprovedReceiveCash'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucherReceiveCash,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucherReceiveCash(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                var selectedRow = null;
                $('.trSelected', $('#flexUnapprovedReceiveCash')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                receiveCashListModel.total = parseInt(receiveCashListModel.total) - 1;
                removeEntityFromGridRows(receiveCashListModel, selectedRow);
                $('#flexUnapprovedReceiveCash').flexAddData(receiveCashListModel);
                reloadReceiveCashGrid();
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // -------- For Receive Bank Voucher Posting -------- \\
    function postVoucherReceiveBank(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedReceiveBank'), 'voucher') == false) {
            return;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return;
        }
        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexUnapprovedReceiveBank'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucherReceiveBank,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucherReceiveBank(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                var selectedRow = null;
                $('.trSelected', $('#flexUnapprovedReceiveBank')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                receiveBankListModel.total = parseInt(receiveBankListModel.total) - 1;
                removeEntityFromGridRows(receiveBankListModel, selectedRow);
                $('#flexUnapprovedReceiveBank').flexAddData(receiveBankListModel);
                reloadReceiveBankGrid();
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // -------- For Journal Posting -------- \\
    function postVoucherJournal(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedJournal'), 'voucher') == false) {
            return;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return;
        }
        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flexUnapprovedJournal'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucherJournal,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucherJournal(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                var selectedRow = null;
                $('.trSelected', $('#flexUnapprovedJournal')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                payCashListModel.total = parseInt(journalListModel.total) - 1;
                removeEntityFromGridRows(journalListModel, selectedRow);
                $('#flexUnapprovedJournal').flexAddData(journalListModel);
                reloadJournalGrid();
            } catch (e) {
                // Do Nothing
            }
        }
    }



</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedPayCash">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unposted_cash_payment"></span>
                            <h5 class="feature">Unposted Cash Payment</h5>
                            <span>Summary of unposted cash payment</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedPayBank">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unposted_check_payment"></span>
                            <h5 class="feature">Unposted Cheque Payment</h5>
                            <span>Summary of unposted cheque payment</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedReceiveCash">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unposted_cash_receive"></span>
                            <h5 class="feature">Unposted Cash Receive</h5>
                            <span>Summary of unposted cash receive</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedReceiveBank">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unposted_check_receive"></span>
                            <h5 class="feature">Unposted Cheque Receive</h5>
                            <span>Summary of unposted cheque receive</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedJournal">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unposted_journal"></span>
                            <h5 class="feature">Unposted Journal</h5>
                            <span>Summary of unposted journal</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <li><a href="javascript:;">
                    <span class="dashboard_icon my_favourites"></span>
                    <h5 class="feature">MY FAVOURITES</h5>
                    <span>Your favourite content here</span></a>
                </li>

            </ul>
            <ul id="output" style="height: 100%">
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedPayCash">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedPayCash" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedPayBank">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedPayBank" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedReceiveCash">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedReceiveCash" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedReceiveBank">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedReceiveBank" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/accVoucher/listOfUnApprovedJournal">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedJournal" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/>
                </li>
            </ul>
        </div>
    </div>
</div>