<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Bank Reconciliation Cheque
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hideToDate" id="hideToDate"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="toDate">To Date:</label>

                <div class="col-md-3">
                    <app:dateControl
                            name="toDate"
                            required="true"
                            validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="toDate"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchBankReconciliationCheque" name="searchBankReconciliationCheque" type="submit"
                    data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl
                    urls="/accReport/downloadBankReconciliationCheque,/accReport/downloadBankReconciliationChequeCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadBankReconciliationCheque">
                            <li><a href="javascript:void(0)" id="printBankReconciliationChequeList" class="pdf_icon"></a>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadBankReconciliationChequeCsv">
                            <li><a href="javascript:void(0)" id="printBankReconciliationChequeListCsv"
                                   class="csv_icon"></a>
                            </li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>