<%@ page import="com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div class="col-md-6">
	<g:hiddenField name="hidConversionRate" value=""/>
	<g:hiddenField name="hidDiscount" value=""/>
	<g:hiddenField name="hidFromAmount" value=""/>

	<div class="form-group">
		<label class="col-md-5 control-label" for="lblAmountInForeignCurrency">BDT Amount:</label>
		<span id="lblAmountInForeignCurrency" class="col-md-5">0.00</span>
		<g:hiddenField name="hidAmountInForeignCurrency" class="required" value=""/>
	</div>

	<div class="form-group">
		<label class="col-md-5 control-label" for="lblAmountInLocalCurrency"
		       id="localCurrencyLabel"></label>
		<span id="lblAmountInLocalCurrency" class="col-md-5">0.00</span>
		<g:hiddenField name="hidAmountInLocalCurrency" class="required" value=""/>
	</div>

	<div class="form-group">
		<label class="col-md-5 control-label" for="lblRegularFee">Regular Fee:</label>
		<span id="lblRegularFee" class="col-md-5">0.00</span>
	</div>

	<div class="form-group">
		<label class="col-md-5 control-label" for="lblTotal">Total:</label>
		<span id="lblTotal" class="col-md-5">0.00</span>
	</div>

    <div class="form-group">
        <label class="col-md-5 control-label" for="lblCommission">Commission:</label>
        <span id="lblCommission" class="col-md-5">0.00</span>
    </div>

	<div class="form-group">
		<label class="col-md-5 control-label" for="lblGrandTotal">Grand Total:</label>
		<span id="lblGrandTotal" class="col-md-5">0.00</span>
	</div>
</div>

<div class="col-md-6">
	<div class="form-group">
		<label class="col-md-5 control-label label-required" for="fromCurrencyId">From Amount:</label>

		<div class="col-md-7">
			<div class="row">
				<div class="col-md-6">
					<exh:dropDownCurrencyConversion dataModelName="dropDownCurrency" name="fromCurrencyId"
					                                tabindex="19" showHints="false"
					                                onchange="return setConversionRate()">
					</exh:dropDownCurrencyConversion>
				</div>

				<div class="col-md-6" style="padding: 0">
					<input type="text" class="k-textbox" tabindex="20"
					       id="fromAmount" name="fromAmount"/>
				</div>
			</div>
		</div>
	</div>

	<div class="form-group">
		<label class="col-md-5 control-label label-required" for="conversionRate">Rate:</label>

		<div class="col-md-7">
			<div class="row">
				<div class="col-md-6">
					<input type="text" class="k-textbox" name="conversionRate" id="conversionRate" required
					       validationMessage=" "
					       value="" tabindex="21"/>
				</div>

				<div class="col-md-2 pull-left">
					<span class="k-invalid-msg" data-for="conversionRate"></span>
				</div>
			</div>
		</div>
	</div>

    <div class="form-group">
        <label class="col-md-5 control-label label-required" for="discount">Discount:</label>
        <div class="col-md-7">
            <div class="row">
                <div class="col-md-6">
                    <input type="text" class="k-textbox" name="discount" id="discount" required="true"
                           validationMessage=" "
                           value="0.00" tabindex="22"/>
                </div>
                <div class="col-md-4" style="padding: 0">
                    <input type='button' tabindex="23" name="grandTotalLink" id="grandTotalLink"
                           class="k-button"
                           value="Calculate"
                           title="Calculate Grand Total"/>
                </div>
                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="discount"></span>
                </div>
            </div>
        </div>
    </div>

	<div class="form-group">
		<label class="col-md-5 control-label label-required" for="conversionRate">Paid By:</label>

		<div class="col-md-5">
			<app:dropDownSystemEntity class="required valueSelection" tabindex="24"
			                             name="paidBy"
			                             onchange="showPaidByNo();"
			                             required="true" validationMessage=" "
			                             dataModelName="dropDownPaidBy"
			                             elements="${[
					                             ExhPaidByCacheUtility.PAID_BY_ID_CASH,
					                             ExhPaidByCacheUtility.PAID_BY_ID_ONLINE
			                             ]}"
			                             typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAID_BY}">
			</app:dropDownSystemEntity>
		</div>
		<div class="col-md-2 pull-left">
			<span class="k-invalid-msg" data-for="paidBy"></span>
		</div>
	</div>
	<div  id="paidByNoSpan" style="display: none">
		<div class="form-group">
			<label class="col-md-5 control-label label-required" for="paidByNo" id="lblPaidByNo">Transaction No:</label>

			<div class="col-md-5">
				<input type="text" class="k-textbox" name="paidByNo" id="paidByNo" required="true" validationMessage=" "
				       value="" tabindex="25"/>
			</div>
			<div class="col-md-2 pull-left">
				<span class="k-invalid-msg" data-for="paidByNo"></span>
			</div>
		</div>
	</div>
</div>

