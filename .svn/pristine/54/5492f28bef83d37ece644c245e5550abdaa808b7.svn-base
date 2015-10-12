<%@ page import="com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Upload Task
        </div>
    </div>
    <app:systemEntityByReserved name="hidCashCollectionId" reservedId="${RmsPaymentMethodCacheUtility.CASH_COLLECTION_ID}" typeId="${RmsPaymentMethodCacheUtility.ENTITY_TYPE}">
    </app:systemEntityByReserved>
    <app:systemEntityByReserved name="hidBankDepositId" reservedId="${RmsPaymentMethodCacheUtility.BANK_DEPOSIT_ID}" typeId="${RmsPaymentMethodCacheUtility.ENTITY_TYPE}">
    </app:systemEntityByReserved>
    <form id='uploadTaskForm' name='uploadTaskForm' class="form-horizontal form-widgets" role="form"
          enctype="multipart/form-data" method="post">
        <div class="panel-body">
            <input type="hidden" name="isExhUser" id="isExhUser" value="true"/>
            <div class="col-md-7">
                <div class="form-group">
                    <label class="col-md-4 control-label label-required" id="lblPaymentMethod" for="paymentMethod">Payment Method:</label>
                    <div class="col-md-5">
                        <app:dropDownSystemEntity dataModelName="dropDownPaymentMethod" name="paymentMethod"
                                                  typeId="${SystemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD}"
                                                  required="true" validationMessage="Required">
                        </app:dropDownSystemEntity>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="paymentMethod"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-4 control-label label-required" for="taskFile">Task File:</label>
                    <div class="col-md-5">
                        <input type="file" id="taskFile" name="taskFile" validationMessage="Required"/>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="taskFile"></span>
                    </div>

                </div>
            </div>
            <div class="col-md-5">
                <div class="panel panel-primary" id="errorDiv" style="display: none">
                    <div class="panel-heading">
                        <div class="panel-title">
                            Task contains following errors
                        </div>
                    </div>
                    <div class="panel-body">
                        <ul id="lstViewTaskErrors" class='list-group' style="border: 0;margin-bottom: 0px">
                        </ul>
                        <div id="pager" class="k-pager-wrap" style="width: 98.5%"></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="upload" name="upload" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Upload
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="5"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>