<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<form id='rmsTaskDisbursementInfoForm' name='rmsTaskDisbursementInfoForm' class="form-horizontal form-widgets"
      role="form">
    <div class="panel-body">
        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="beneficiaryName">Beneficiary Name:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="beneficiaryName" name="beneficiaryName" tabindex="4"
                           placeholder="Beneficiary Name" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="beneficiaryName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="amount">Amount:</label>

                <div class="col-md-6">
                    <input type="text" id="amount" name="amount" tabindex="5"
                           placeholder="Amount" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="amount"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="outletBank">Bank:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="outletBank" name="outletBank" tabindex="6"
                           placeholder="Bank" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="outletBank"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="outletBranch">Bank Branch:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="outletBranch" name="outletBranch" tabindex="7"
                           placeholder="Branch" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="outletBranch"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="outletDistrict">District:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="outletDistrict" name="outletDistrict" tabindex="8"
                           placeholder="District" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="outletDistrict"></span>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-3 control-label label-optional" for="localCurrencyId">Local Currency:</label>

                <div class="col-md-6">
                    <app:dropDownCurrency dataModelName="dropDownCurrency" id="localCurrencyId" name="localCurrencyId"
                                          tabindex="9">
                    </app:dropDownCurrency>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-optional" for="amountInLocalCurrency">Amount(Local):</label>

                <div class="col-md-6">
                    <input type="text" id="amountInLocalCurrency" name="amountInLocalCurrency" tabindex="10"
                           placeholder="Amount"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="paymentMethod">Payment Method:</label>

                <div class="col-md-6">
                    <app:dropDownSystemEntity dataModelName="dropDownPaymentMethod" id="paymentMethod"
                                              name="paymentMethod"
                                              typeId="${SystemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD}"
                                              tabindex="11" required="true" validationMessage="Required"
                                              onchange="onChangePayment();">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="paymentMethod"></span>
                </div>
            </div>

            <div class="form-group for-bank">
                <label class="col-md-3 control-label label-required" for="accountNo">Account Number:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="accountNo" name="accountNo" tabindex="12"
                           placeholder="Account No" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="accountNo"></span>
                </div>
            </div>

            <div class="form-group for-cash">
                <label class="col-md-3 control-label label-required" for="pinNo">Pin No.:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="pinNo" name="pinNo" tabindex="13"
                           placeholder="Pin No." required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="pinNo"></span>
                </div>
            </div>

            <div class="form-group for-cash">
                <label class="col-md-3 control-label label-required" for="identityType">Identity Type:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="identityType" name="identityType" tabindex="14"
                           placeholder="Identity Type" required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="identityType"></span>
                </div>
            </div>

            <div class="form-group for-cash">
                <label class="col-md-3 control-label label-optional" for="identityNo">Identity No:</label>

                <div class="col-md-6">
                    <input class="k-textbox" id="identityNo" name="identityNo" tabindex="15"
                           placeholder="Identity No"/>
                </div>
            </div>
        </div>
    </div>

</form>
