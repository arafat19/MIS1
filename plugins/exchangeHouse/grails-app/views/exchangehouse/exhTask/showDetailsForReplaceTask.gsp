<%@ page import="com.athena.mis.exchangehouse.utility.ExhPaidByCacheUtility; com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<script type="text/javascript">
    var dropDownBank,dropDownDistrict,dropDownBankBranch;
    var dropDownRemittancePurpose, dropDownPaymentMethod, dropDownPaidBy;
</script>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Replace Task
        </div>
    </div>

    <app:systemEntityByReserved name="hiddenBankDeposit" reservedId="${ExhPaymentMethodCacheUtility.PAYMENT_METHOD_BANK_DEPOSIT}" typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD}" />
    <app:systemEntityByReserved name="hiddenCashCollection" reservedId="${ExhPaymentMethodCacheUtility.PAYMENT_METHOD_CASH_COLLECTION}" typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD}" />
    <app:systemEntityByReserved name="hiddenOnlinePaidBy" reservedId="${ExhPaidByCacheUtility.PAID_BY_ID_ONLINE}" typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAID_BY}" />
    <input type="hidden" id="hiddenSystemCurrencyId" value="<app:localCurrency property="id" />" />

    <form name="replaceTaskForm" id="replaceTaskForm" method="post" class="form-horizontal form-widgets" role="form">
        <g:hiddenField name="id" value=""/>
        <g:hiddenField name="hidConversionRate" value=""/>
        <g:hiddenField name="hidFromAmount" value=""/>

        <div class="panel-body" style="min-height: 450px">
            <div class="col-md-6">

                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblCustomerName">Customer Name:</label>
                    <span id="lblCustomerName" class="col-md-5"></span>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblBeneficiaryName">Beneficiary Name:</label>
                    <span id="lblBeneficiaryName" class="col-md-5"></span>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="remittancePurpose">Remittance Purpose:</label>

                    <div class="col-md-5">
                        <exh:dropDownRemittancePurpose dataModelName="dropDownRemittancePurpose" tabindex="1" name="remittancePurpose" required="true">
                        </exh:dropDownRemittancePurpose>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="remittancePurpose"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label" for="isOtherBank">Other Bank:</label>
                    <div class="col-md-5">
                        <input type="checkbox" name="isOtherBank" id="isOtherBank"
                               tabindex="4"
                               onchange="displayOtherBankPanel();"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="outletBankId" id="lblBank">Bank:</label>
                    <div class="col-md-5">
                        <app:dropDownBank data_model_name="dropDownBank" name="outletBankId"
                                          validationmessage="Required" id="outletBankId" tabindex="5"
                                          onchange="onChangeBank();">
                        </app:dropDownBank>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="outletBankId"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="outletDistrictId" id="lblDistrict">District:</label>
                    <div class="col-md-5">
                        <app:dropDownDistrict data_model_name="dropDownDistrict" name="outletDistrictId" id="outletDistrictId"
                                              onchange="onChangeDistrict();" bank_id="" validationmessage="Required" tabindex="6"
                                              url="${createLink(controller: 'district',action: 'reloadDistrictDropDown')}">
                        </app:dropDownDistrict>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="outletDistrictId"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="outletBranchId" id="lblBankBranch">Bank Branch:</label>
                    <div class="col-md-5">
                        <app:dropDownBranchesByBankAndDistrict data_model_name="dropDownBankBranch" name="outletBranchId" id="outletBranchId"
                                                               validationmessage="Required" tabindex="7"
                                                               url="${createLink(controller: 'bankBranch', action: 'reloadBranchesDropDownByBankAndDistrict')}">
                        </app:dropDownBranchesByBankAndDistrict>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="outletBranchId"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="paymentMethod">Payment Method:</label>
                    <div class="col-md-5">
                        <app:dropDownSystemEntity
                                tabindex="8"
                                name="paymentMethod"
                                dataModelName="dropDownPaymentMethod"
                                required="true"
                                showHints="false"
                                validationMessage="Required"
                                onchange="return updatePaymentMethod();"
                                typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAYMENT_METHOD}">
                        </app:dropDownSystemEntity>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="paymentMethod"></span>
                    </div>
                </div>
                <div id="accountInfo" style="display:none">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="accountNumber">Account Number:</label>
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" name="accountNumber" id="accountNumber" validationMessage="Required"
                                   tabindex="9"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="accountNumber"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="bankName">Bank:</label>
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" name="bankName" id="bankName" validationMessage="Required"
                                   tabindex="10"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="bankName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="bankBranchName">Bank Branch:</label>
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" name="bankBranchName" id="bankBranchName" validationMessage="Required"
                                   tabindex="11"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="bankBranchName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="districtName">District:</label>
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" name="districtName" id="districtName" validationMessage="Required"
                                   tabindex="12"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="districtName"></span>
                        </div>
                    </div>
                </div>

                <div id="pinInfo" style="display:none">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="identityType">Identity Type:</label>
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" name="identityType" id="identityType" validationMessage="Required"
                                   tabindex="13"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="identityType"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-4 control-label" for="identityNo">Identity No:</label>
                        <div class="col-md-5">
                            <input type="text" class="k-textbox" name="identityNo" id="identityNo"
                                   tabindex="14"/>
                        </div>
                    </div>
                </div>

            </div>
            <div class="col-md-6">
                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblAmountInForeignCurrency">BDT Amount:</label>
                    <span id="lblAmountInForeignCurrency" class="col-md-5">0.00</span>
                    <g:hiddenField name="hidAmountInForeignCurrency" class="required" value=""/>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblAmountInLocalCurrency"
                           id="localCurrencyLabel"><app:localCurrency property="symbol"/> Amount:</label>
                    <span id="lblAmountInLocalCurrency" class="col-md-5">0.00</span>
                    <g:hiddenField name="hidAmountInLocalCurrency" class="required" value=""/>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblRegularFee">Regular Fee:</label>
                    <span id="lblRegularFee" class="col-md-5">0.00</span>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblTotal">Total:</label>
                    <span id="lblTotal" class="col-md-5">0.00</span>
                </div>


                <div class="form-group">
                    <label class="col-md-4 control-label" for="lblGrandTotal">Grand Total:</label>
                    <span id="lblGrandTotal" class="col-md-5">0.00</span>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="fromCurrencyId">From Amount:</label>

                    <div class="col-md-8">
                        <div class="row">
                            <div class="col-md-4">
                                <exh:dropDownCurrencyConversion dataModelName="dropDownCurrency" name="fromCurrencyId"
                                                                tabindex="15" showHints="false"
                                                                onchange="return setConversionRate()">
                                </exh:dropDownCurrencyConversion>
                            </div>

                            <div class="col-md-6" style="padding: 0">
                                <input type="text" class="k-textbox" tabindex="16"
                                       id="fromAmount" name="fromAmount"/>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="conversionRate">Rate:</label>

                    <div class="col-md-8">
                        <div class="row">
                            <div class="col-md-4">
                                <input type="text" class="k-textbox" name="conversionRate" id="conversionRate" required
                                       validationMessage="Required"
                                       value="" tabindex="17"/>
                            </div>

                            <div class="col-md-4" style="padding: 0">
                                <input type='button' tabindex="18" name="grandTotalLink" id="grandTotalLink"
                                       class="k-button"
                                       value="Calculate"
                                       title="Calculate Grand Total"/>
                            </div>

                            <div class="col-md-4 pull-left">
                                <span class="k-invalid-msg" data-for="conversionRate"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="conversionRate">Paid By:</label>

                    <div class="col-md-5">
                        <app:dropDownSystemEntity class="required valueSelection" tabindex="19"
                                                  name="paidBy"
                                                  onchange="showPaidByNo();"
                                                  required="true" validationMessage="Required"
                                                  dataModelName="dropDownPaidBy"
                                                  typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_PAID_BY}">
                        </app:dropDownSystemEntity>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="paidBy"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="paidByNo"
                           id="lblPaidByNo">Reference No:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" name="paidByNo" id="paidByNo" required="true"
                               validationMessage="Required"
                               value="" tabindex="20"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="paidByNo"></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="21"
                    aria-disabled="false" ><span class="k-icon k-i-tick"></span>Update
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="22"
                    aria-disabled="false" onclick='goBack();'><span class="k-icon k-i-arrow-w"></span>Go Back
            </button>
        </div>
    </form>

</div>

<g:render template="/exchangehouse/exhTask/scriptDetailsForReplaceTask"/>