<script type="text/javascript">

    $(document).ready(function() {
        onLoadDailyRemittanceSummery();
    });

    function onLoadDailyRemittanceSummery() {
        initializeForm($("#searchForm"),showRemittanceSummary);
    }

    function showRemittanceSummary() {
        executePreCondition();

        if (checkDates($('#startDate'), $('#endDate')) == false) {
	        return false;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'exhReport', action: 'getRemittanceSummaryReport')}?startDate=" + $('#startDate').val() + "&endDate=" + $('#endDate').val(),
            success: executePostCondition,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePreCondition(data) {
        clearTable();
        $('#remittanceSummaryDiv').hide();

    }
    function executePostCondition(data) {
        var newTaskList = $(data.newTaskList);
        var sentTaskList = $(data.sentTaskList);
        var sentToOtherTaskList = $(data.sentToOtherTaskList);

        var totGBPCashier = newTaskList[0].total_local_amount ? newTaskList[0].total_local_amount.toFixed(2) : '0';
        var totBDTCashier = newTaskList[0].total_foreign_amount ? newTaskList[0].total_foreign_amount.toFixed(2) : '0';
        var totGBPBank = sentTaskList[0].total_local_amount ? sentTaskList[0].total_local_amount.toFixed(2) : '0';
        var totBDTBank = sentTaskList[0].total_foreign_amount ? sentTaskList[0].total_foreign_amount.toFixed(2) : '0';
        var totGBPOtherBank = sentToOtherTaskList[0].total_local_amount ? sentToOtherTaskList[0].total_local_amount.toFixed(2) : '0';
        var totBDTOtherBank = sentToOtherTaskList[0].total_foreign_amount ? sentToOtherTaskList[0].total_foreign_amount.toFixed(2) : '0';

        $('#lblLocalCurrencyName').html('<b>' + data.localCurrencyName + ' Total:' + '</b>');
        $('#totalCount').html(newTaskList[0].count ? newTaskList[0].count : '0');
        $('#totalForeignAmount').html(totBDTCashier);
        $('#totalLocalAmount').html(totGBPCashier);

        $('#sentCount').html(sentTaskList[0].count ? sentTaskList[0].count : '0');
        $('#sentForeignAmount').html(totBDTBank);
        $('#sentLocalAmount').html(totGBPBank);

        $('#sentToOtherCount').html(sentToOtherTaskList[0].count ? sentToOtherTaskList[0].count : '0');
        $('#sentToOtherForeignAmount').html(totBDTOtherBank);
        $('#sentToOtherAmount').html(totGBPOtherBank);

        $('#remittanceSummaryDiv').show();
    }

    function clearTable() {
        $('#totalCount').html('');
        $('#totalForeignAmount').html('');
        $('#totalLocalAmount').html('');

        $('#sentCount').html('');
        $('#sentForeignAmount').html('');
        $('#sentLocalAmount').html('');

        $('#sentToOtherCount').html('');
        $('#sentToOtherForeignAmount').html('');
        $('#sentToOtherAmount').html('');
    }

    // update page title
    $('span.headingText').html('Daily Remittance Summary');
    $('#icon_box').attr('class', 'pre-icon-header exchange-house-wise-disbursement');
    $(document).attr('title', "ARMS - Daily Remittance Summary");
    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showRemittanceSummary");


</script>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search By Date
        </div>
    </div>

    <form id='searchForm' name='searchForm' class="form-horizontal form-widgets" role="form" method="post">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="startDate">Start Date:</label>

                <div class="col-md-2">
                    <app:dateControl name="startDate" tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="endDate">End Date:</label>
                <div class="col-md-2">
                    <app:dateControl name="endDate" tabindex="2">
                    </app:dateControl>
                </div>
            </div>

        </div>
        <div class="panel-footer">

            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

        </div>

    </form>

</div>


<div class="table-responsive" id="remittanceSummaryDiv" style="display:none;width: 60%">
    <table class="table" style="border:1px solid #ccc">
        <thead>
            <tr>
                <td>&nbsp</td>
                <td><b>Task Received</b></td>
                <td><b>Task Sent To Bank</b></td>
                <td><b>Task Sent To Other Bank</b></td>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><b>Total Task:</b></td>
                <td id='totalCount'></td>
                <td id='sentCount'></td>
                <td id='sentToOtherCount'></td>
            </tr>
            <tr>
                <td id="lblLocalCurrencyName"></td>
                <td id='totalLocalAmount'></td>
                <td id='sentLocalAmount'></td>
                <td id='sentToOtherAmount'></td>
            </tr>
            <tr>
                <td><b>BDT Total:</b></td>
                <td id='totalForeignAmount'></td>
                <td id='sentForeignAmount'></td>
                <td id='sentToOtherForeignAmount'></td>
            </tr>
        </tbody>
    </table>
</div>