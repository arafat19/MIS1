<%@ page import="com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Voucher List
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hidVoucherTypeId" id="hidVoucherTypeId"/>
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>
        <input type="hidden" name="hideIsPosted" id="hideIsPosted"/>
        <app:systemEntityByReserved
                name="hidPaymentBankId"
                reservedId="${AccVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID}"
                typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
        </app:systemEntityByReserved>
        <app:systemEntityByReserved
                name="hidPaymentCashId"
                reservedId="${AccVoucherTypeCacheUtility.PAYMENT_VOUCHER_CASH_ID}"
                typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
        </app:systemEntityByReserved>
        <app:systemEntityByReserved
                name="hidReceivedBankId"
                reservedId="${AccVoucherTypeCacheUtility.RECEIVED_VOUCHER_BANK_ID}"
                typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
        </app:systemEntityByReserved>
        <app:systemEntityByReserved
                name="hidReceivedCashId"
                reservedId="${AccVoucherTypeCacheUtility.RECEIVED_VOUCHER_CASH_ID}"
                typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
        </app:systemEntityByReserved>
        <app:systemEntityByReserved
                name="hidJournalId"
                reservedId="${AccVoucherTypeCacheUtility.JOURNAL_ID}"
                typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
        </app:systemEntityByReserved>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From Date:</label>

                <div class="col-md-2">
                    <app:dateControl name="fromDate"
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To Date:</label>

                <div class="col-md-2">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="voucherTypeId">Voucher Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity dataModelName="dropDownVoucherType"
                                                 tabindex="3"
                                                 name="voucherTypeId"
                                                 typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
                    </app:dropDownSystemEntity>
                </div>

                <label class="col-md-1 control-label label-optional" for="isPosted">Status Posted:</label>

                <div class="col-md-1">
                    <select id='isPosted'
                            name="isPosted"
                            tabindex="4">
                    </select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchVoucherList" name="searchVoucherList" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAllUrl urls="/accReport/downloadVoucherList">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printVoucherList" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </form>
</div>
