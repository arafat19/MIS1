<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility; com.athena.mis.utility.DateUtility" %>
<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Search Ledger
                </div>
            </div>

            <form name='searchForm' id='searchForm' class="form-horizontal form-widgets"
                  role="form">
                <div class="panel-body">
                    <input type="hidden" name="hidCoaCode" id="hidCoaCode"/>
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

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="fromDate">From Date:</label>

                        <div class="col-md-4">
                            <app:dateControl name="fromDate"
                                             diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}"
                                             tabindex="1">
                            </app:dateControl>
                        </div>

                        <label class="col-md-2 control-label label-required" for="toDate">To Date:</label>

                        <div class="col-md-4">
                            <app:dateControl name="toDate"
                                             tabindex="2">
                            </app:dateControl>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="projectId">Project:</label>

                        <div class="col-md-4">
                            <app:dropDownProject name="projectId"
                                                 tabindex="3"
                                                 hintsText="ALL"
                                                 dataModelName="dropDownProject">
                            </app:dropDownProject>
                        </div>

                        <label class="col-md-2 control-label label-required">Account Code:</label>

                        <div class="col-md-4">
                            <span id='htmlCoaCode'></span>
                            <input type="hidden" id="coaCode" name="coaCode"/>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">
                    <button id="showReport" name="showReport" type="submit" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false"><span class="k-icon k-i-search"></span>Search
                    </button>
                    <app:ifAnyUrl urls="/accReport/downloadLedger,/accReport/downloadLedgerCsv">
                        <span class="download_icon_set">
                            <ul>
                                <li>Save as :</li>
                                <app:ifAllUrl urls="/accReport/downloadLedger">
                                    <li><a href="javascript:void(0)" id="printLedger" class="pdf_icon"></a></li>
                                </app:ifAllUrl>
                                <app:ifAllUrl urls="/accReport/downloadLedgerCsv">
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
                    <td colspan="6">Ledger Information</td>
                </tr>
                <tr>
                    <td class="active" style="width: 15%">Account Code:</td>
                    <td><span id='lblCoaCode'></span></td>
                    <td class="active" style="width: 15%">Account Name:</td>
                    <td><span id='lblCoaDescription'></span></td>
                    <td class="active" style="width: 15%">Prev. Balance:</td>
                    <td><span id='lblPrevBalance'></span></td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="form-group">
            <table id="flex1" style="display:none"></table>
        </div>
    </div>

    <div class="form-group col-md-3">
        <table id="flexSearchCOA" style="display:none"></table>
    </div>
</div>