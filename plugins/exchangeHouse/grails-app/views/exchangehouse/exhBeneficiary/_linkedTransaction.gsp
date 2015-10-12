<form name="linkedTransactionForm" id="linkedTransactionForm" class="form-horizontal form-widgets" role="form"
      method="post">
    <div class="panel-body">
        <div class="form-group">
            <label class="col-md-3 control-label label-required"
                   for="isLinkedTransactionVerified">Linked Beneficiary Verified:</label>

            <div class="col-md-2">
                <input type="checkbox" id="isLinkedTransactionVerified" name="isLinkedTransactionVerified" tabindex="1"
                       required validationMessage="Required"/>
                <span id="linkedTransactionInfo"></span>
            </div>

            <div class="col-md-2 pull-left">
                <span class="k-invalid-msg" data-for="isLinkedTransactionVerified"></span>
            </div>
        </div>

        <div class="form-group">
            <table id="flexGridForLinkedBeneficiary" style="display:none;"></table>
        </div>

    </div>
</form>
