<div id="application_top_panel" class="panel panel-danger">
    <div class="panel-heading">
        <div class="panel-title">
            Make Payment
        </div>
    </div>
    <form id="makePaymentForm" name="makePaymentForm" class="form-horizontal form-widgets" role="form"
            action='https://secure.metacharge.com/mcpe/purser' method='post'>
        <input type='hidden' name='intTestMode' value='${testMode}'>
        <input type='hidden' name='intInstID' value='${installationId}'>
        <input type='hidden' name='strCartID' value='${task.id}'>
        <input type='hidden' name='fltAmount' value='${totalAmount}'>
        <input type='hidden' name='strCurrency' value='${strCurrency}'>
        <input type='hidden' name='strDesc' value='Payment for remittance ${task.refNo}'>
        <input type='hidden' name='PT_key' value='${key}'>
        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-12"><H4>You are about to make payment against the following remittance:</H4>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-2">Task Reference:</div>

                <div class="col-md-2"><strong>${task.refNo}</strong></div>
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
                <div class="col-md-2">Total Payable :</div>

                <div class="col-md-3"><strong>${totalAmount} ${strCurrency}</strong>
                </div>
            </div>
            <div class="form-group">
                <div class="col-md-12"><H6>Note: When confirmed, you will be redirected to payment gateway.</H6>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-tick"></span>Proceed to Pay
            </button>
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" onclick="gotoTaskGrid();"
                    aria-disabled="false"><span class="k-icon k-i-close"></span>Cancel
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
    $('span.headingText').html('Make Payment');
    $('#icon_box').attr('class', 'pre-icon-header manage-task-list');
    $(document).attr('title', "ARMS - Make Payment");
    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showExhTaskForCashier");
</script>



