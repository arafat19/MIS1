<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Create Task
		</div>
	</div>

	<div class="panel-body" style="min-height: 260px">
		<div class="row">
			<div class="col-md-9">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#fragmentBasicInfo" data-toggle="tab">Basic Info</a></li>
					<li><a href="#fragmentDisbursementInfo" data-toggle="tab">Disbursement Info</a></li>
					<li><a href="#fragmentAmountInfo" data-toggle="tab">Fees & Charges</a></li>
				</ul>


				<div class="tab-content">
					<div class="tab-pane active" id="fragmentBasicInfo">
						<div class="panel-body">
							<form name="basicInfoForm" id="basicInfoForm" class="form-horizontal form-widgets"
							      role="form">
								<g:hiddenField name="id" value=""/>
								<g:hiddenField name="version" value=""/>
								<g:hiddenField name="refNo" value=""/>
								<g:hiddenField name="agentId" value="0"/>

								<g:render template='/exchangehouse/exhTask/basicInfo'/>

							</form>
						</div>
					</div>

					<div class="tab-pane" id="fragmentDisbursementInfo">
						<div class="panel-body">
							<form name="disbursementInfoForm" id="disbursementInfoForm"
							      class="form-horizontal form-widgets" role="form">
								<div class="col-md-6">
									<div class="form-group">
										<label class="col-md-5 control-label label-required"
										       for="paymentMethod">Payment Method:</label>

										<div class="col-md-5">
											<app:dropDownSystemEntity
													tabindex="2"
													name="paymentMethod"
													dataModelName="dropDownPaymentMethod"
													required="true"
													validationMessage=" "
													onchange="return updatePaymentMethod();"
													typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD}">
											</app:dropDownSystemEntity>
										</div>

										<div class="col-md-2 pull-left">
											<span class="k-invalid-msg" data-for="paymentMethod"></span>
										</div>
									</div>
									<g:render template='/exchangehouse/exhTask/outletInfo'/>
									<g:render template='/exchangehouse/exhTask/mobileInfo'/>
									<g:render template='/exchangehouse/exhTask/cashWithdrawalInfo'/>
								</div>

								<div class="col-md-6">
									<div class="form-group">
										<label class="col-md-5 control-label"
										       for="isOtherBank">Select Other Bank:</label>

										<div class="col-md-5">
											<input type="checkbox" name="isOtherBank" id="isOtherBank"
											       tabindex="15"
											       onchange="displayOtherBankPanel();"/>
										</div>
									</div>
									<g:render template='/exchangehouse/exhTask/otherBankInfo'/>
								</div>
							</form>
						</div>
					</div>

					<div class="tab-pane" id="fragmentAmountInfo">
						<div class="panel-body">
							<form name="amountInfoForm" id="amountInfoForm" class="form-horizontal form-widgets"
							      role="form">
								<g:render template='/exchangehouse/exhTask/feesForAgent'/>
							</form>
						</div>
					</div>
				</div>
			</div>

			<div class="col-md-3">
				<exh:customerSummaryForTask customerId="${customerId}"/>
			</div>
		</div>
	</div>

	<div class="panel-footer">
		<button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
		        role="button" onclick="return onSubmitTask();" tabindex="28"
		        aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
		</button>

		<button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
		        class="k-button k-button-icontext" role="button" tabindex="29"
		        aria-disabled="false" onclick='clearFormTask();'><span class="k-icon k-i-close"></span>Cancel
		</button>
	</div>

</div>



<table id="flex1" style="display:none"></table>

