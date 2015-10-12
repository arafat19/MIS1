<%@ page import="com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility; com.athena.mis.utility.DateUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Group Ledger
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>
        <input type="hidden" name="hideGroupId" id="hideGroupId"/>
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
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     tabindex="1"
                                     diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="groupId">Group:</label>

                <div class="col-md-3">
                    <acc:dropDownAccGroup dataModelName="dropDownGroupId"
                                          name="groupId">
                    </acc:dropDownAccGroup>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchLedger" name="searchLedger" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/accReport/downloadForGroupLedgerRpt,/accReport/downloadForGroupLedgerCsvRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadForGroupLedgerRpt">
                            <li><a href="javascript:void(0)" id="printLedger" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadForGroupLedgerCsvRpt">
                            <li><a href="javascript:void(0)" id="printLedgerCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>

<div id="divLedger" class="table-responsive">
    <table class="table table-bordered">
        <tbody>
        <tr class="active">
            <td colspan="6">Group Ledger Information</td>
        </tr>
        <tr>
            <td class="active" style="width: 15%">Prev. Balance:</td>
            <td><span id='lblPrevBalance'></span></td>
        </tr>
        </tbody>
    </table>
</div>

<table id="flex1" style="display:none"></table>