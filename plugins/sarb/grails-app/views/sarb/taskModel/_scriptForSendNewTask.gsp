<script type="text/javascript">
    var localCurrencyName;
    $(document).ready(function () {
        onLoadSendTask();
    });

    function onLoadSendTask() {
        localCurrencyName = $('#localCurrencySymbol').val();
        initFlex();
        $(document).attr('title', 'ARMS-Send Task');
        loadNumberedMenu(MENU_ID_SARB, "#sarbTaskModel/showForSendTaskToSarb");
    }

    function initFlex() {
        $("#flex1").flexigrid
        (
                {
                    url: "${createLink(controller: 'sarbTaskModel', action: 'listForSendTaskToSarb')}",
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 60, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 50, sortable: false, align: "right", hide: true},
                        {display: "Ref No", name: "refNo", width: 120, sortable: true, align: "left"},
                        {display: "Amount(BDT)", name: "amountInForeignCurrency", width: 120, sortable: true, align: "right"},
                        {display: "Amount(" + localCurrencyName + ")", name: "amountInLocalCurrency", width: 120, sortable: true, align: "right"},
                        {display: "Customer Name", name: "customerName", width: 180, sortable: true, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 180, sortable: true, align: "left"},
                        {display: "Created On", name: "createdOn", width: 130, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <sec:access url="/sarbTaskModel/sendTaskToSarb">
                        {name: 'Send To SARB', bclass: 'send', onpress: sendTaskToSarb},
                        </sec:access>
                        {separator: true},
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    sortname: "refNo",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Tasks',
                    useRp: true,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    }
                }
        );
    }

    function doSelectAll(com, grid) {
        try {
            var rows = $('table#flex1 > tbody > tr');
            if (rows && rows.length > 0) {
                rows.addClass('trSelected');
            }
        } catch (e) {
        }
    }
    function doDeselectAll(com, grid) {
        try {
            var rows = $('table#flex1 > tbody > tr');
            if (rows && rows.length > 0) {
                rows.removeClass('trSelected');
            }
        } catch (e) {
        }
    }

    function sendTaskToSarb(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'task', false) == false) {
            return;
        }
        var ids = getSelectedIdFromGrid($('#flex1'));
        var selectedIds = $('.trSelected', grid);

        if (!confirm('Are you sure you want to send ' + selectedIds.length + ' task(s) to SARB?')) {
            return false;
        }

        $('span.send').hide();
        $('span.delete').hide();

        showLoadingSpinner(true);
        $.ajax({
            type: 'post',
            dataType: 'json',
            url: "${createLink(controller: 'sarbTaskModel',action: 'sendTaskToSarb')}?id=" + ids,
            success: function (data) {
                if (data.isError == false) {
                    showSuccess(data.message);
                    executePostConditionForSentToSarb(selectedIds);
                } else {
                    showError(data.message);
                }
            },
            complete: function (XMLHttpRequest, textStatus) {
                $('span.send').show();
                $('span.delete').show();
                onCompleteAjaxCall();
            }
        });
    }

    function executePostConditionForSentToSarb(ids) {
        $(ids).each(function (e) {
            $(this).remove();
        });
        $('#flex1').decreaseCount(ids.length);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }


</script>