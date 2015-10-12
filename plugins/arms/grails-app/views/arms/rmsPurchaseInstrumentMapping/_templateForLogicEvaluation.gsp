<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Evaluate Logic
        </div>
    </div>

    <form id='logicEvaluationForm' name='logicEvaluationForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="amount">Amount:</label>

                <div class="col-md-6">
                    <input type="text" class="k-textbox" id="amount" name="amount" tabindex="8"
                           placeholder="Amount"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label">Commission:</label>

                <div class="col-md-3">
                    <span id="lblCommission" name="lblCommission"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label">PNT:</label>

                <div class="col-md-3">
                    <span id="lblPnt" name="lblPnt"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label">Postage:</label>

                <div class="col-md-3">
                    <span id="lblPostage" name="lblPostage"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label">Service Charge:</label>

                <div class="col-md-3">
                    <span id="lblServiceCharge" name="lblServiceCharge"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label">Vat:</label>

                <div class="col-md-3">
                    <span id="lblVat" name="lblVat"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label">Vat on PNT:</label>

                <div class="col-md-3">
                    <span id="lblVatOnPnt" name="lblVatOnPnt"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label"><b>Total:</b></label>

                <div class="col-md-3">
                    <span id="lblTotal" name="lblTotal" style="font-weight: bold"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="evaluate" name="evaluate" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="9"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Evaluate
            </button>
            <button id="reset" name="reset" type="button" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="10"
                    aria-disabled="false" onclick='resetEvaluateForm();'><span class="k-icon k-i-close"></span>Reset
            </button>

        </div>
    </form>
</div>

