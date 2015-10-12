<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility" %>
<g:render template='/arms/rmsTask/script'/>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Task
        </div>

    </div>
    <app:systemEntityByReserved name="hidBankDepositId" reservedId="${RmsPaymentMethodCacheUtility.BANK_DEPOSIT_ID}" typeId="${SystemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD}">
    </app:systemEntityByReserved>
    <app:systemEntityByReserved name="hidCashCollectionId" reservedId="${RmsPaymentMethodCacheUtility.CASH_COLLECTION_ID}" typeId="${SystemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD}">
    </app:systemEntityByReserved>

    <div id="taskTabs"  class="panel-body" style="min-height: 310px;">

        <ul class="nav nav-tabs">
            <li class="active"><a href="#fragmentBasicInfo" data-toggle="tab">Basic Info</a></li>
            <li><a href="#fragmentDisbursementInfo" data-toggle="tab">Disbursement Info</a></li>
            <li><a href="#fragmentAdditionalInfo" data-toggle="tab">Additional Info</a></li>
        </ul>


        <div class="tab-content">
            <div class="tab-pane active" id="fragmentBasicInfo">
                <g:render template='/arms/rmsTask/createBasicInfo'/>
            </div>

            <div class="tab-pane" id="fragmentDisbursementInfo">
                <g:render template='/arms/rmsTask/createDisbursementInfo'/>
            </div>

            <div class="tab-pane" id="fragmentAdditionalInfo">
                <g:render template='/arms/rmsTask/createAdditionalInfo'/>
            </div>
        </div>

    </div>

    <div class="panel-footer">
        <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                role="button" tabindex="20"
                aria-disabled="false" onclick='return onSubmitTask();'><span class="k-icon k-i-plus"></span>Create
        </button>

        <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                class="k-button k-button-icontext" role="button" tabindex="21"
                aria-disabled="false" onclick='return clearTaskForm();'><span class="k-icon k-i-close"></span>Cancel
        </button>
    </div>

</div>

<div style="margin-top: 3px">
    <table id="flex" style="display:none"></table>
</div>
