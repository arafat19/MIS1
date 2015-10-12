<%@ page import="com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility; com.athena.mis.utility.DateUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Source Ledger
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hidAccSourceId" id="hidAccSourceId"/>
        <input type="hidden" name="hidSourceCategoryId" id="hidSourceCategoryId"/>
        <input type="hidden" name="hidSourceId" id="hidSourceId"/>
        <input type="hidden" name="hidSourceName" id="hidSourceName"/>
        <input type="hidden" name="hidProjectId" id="hidProjectId"/>
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>
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
                                     diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}"
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         hintsText="ALL"
                                         tabindex="3"
                                         name="projectId">
                    </app:dropDownProject>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="accSourceId">Source Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownSourceType"
                            tabindex="4"
                            name="accSourceId"
                            onchange="onChangeSourceType();"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_SOURCE}"
                            required="true"
                            validationMessage="Required">
                    </app:dropDownSystemEntity>
                </div>

                <label class="col-md-1 control-label label-optional"
                       for="sourceCategoryId">Source Category:</label>

                <div class="col-md-3">
                    <select id="sourceCategoryId"
                            name="sourceCategoryId"
                            tabindex="5"
                            onchange="onChangeSourceCategory();">
                    </select>
                </div>

                <label class="col-md-1 control-label label-optional" for="sourceId">Source:</label>

                <div class="col-md-3">
                    <select id="sourceId"
                            name="sourceId"
                            tabindex="6">
                    </select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchLedger" name="searchLedger" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/accReport/downloadSourceLedger,/accReport/downloadSourceLedgerCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadSourceLedgeReportGroupBySource">
                            <li><a href="javascript:void(0)" id="printSourceLedgerPdf" class="pdf_icon"></a></li>
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
            <td colspan="6">Source Ledger Information</td>
        </tr>
        <tr>
            <td class="active" style="width: 15%">Prev. Balance:</td>
            <td><span id='lblPrevBalance'></span></td>
        </tr>
        </tbody>
    </table>
</div>
<table id="flex1" style="display:none"></table>