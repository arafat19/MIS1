<div id="application_top_panel" class="panel panel-success">
    <div class="panel-heading">
        <div class="panel-title">
            Payment Process Completed
        </div>
    </div>

    <form class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-12">Thanks for completing the payment process.
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-2"><H4>Status:</H4></div>

                <div class="col-md-10"><H4>${message}</H4>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Task Reference:</div>

                <div class="col-md-3"><strong>${task.refNo}</strong></div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Amount :</div>

                <div class="col-md-3">${task.amountInForeignCurrency} BDT</div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Equivalent :</div>

                <div class="col-md-3">${task.amountInLocalCurrency} ${strCurrency}</div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Fee :</div>

                <div class="col-md-3">${task.regularFee} ${strCurrency}</div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Discount :</div>

                <div class="col-md-3">${task.discount} ${strCurrency}</div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Total paid :</div>

                <div class="col-md-3"><strong>${totalAmount} ${strCurrency}</strong>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" onclick="gotoTaskGrid();"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Show Task
            </button>
        </div>
    </form>
</div>

<script type="text/javascript">
    function gotoTaskGrid() {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhTask', action: 'showExhTaskForCashier')}";
        $.history.load(formatLink(loc));
    }
    // update page title
    $('span.headingText').html('Payment Completed');
    $('#icon_box').attr('class', 'pre-icon-header manage-task-list');
    $(document).attr('title', "ARMS - Payment Completed");
    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showExhTaskForCashier");
</script>



