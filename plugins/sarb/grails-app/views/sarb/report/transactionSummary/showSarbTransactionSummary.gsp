<g:render template="/sarb/report/transactionSummary/scriptSarbTransactionSummary" />

<div id="application_top_panel" class="panel panel-primary">

    <div class="panel-heading">
        <div class="panel-title">
            SARB Transaction Summary
        </div>
    </div>
    <form id='sarbTransactionSummary' name='sarbTransactionSummary' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="fromDate">From Date:</label>
                <div class="col-md-2">
                    <app:dateControl name="fromDate" tabindex="1" diffWithCurrent="-30">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="toDate">To Date:</label>
                <div class="col-md-2">
                    <app:dateControl name="toDate" tabindex="2">
                    </app:dateControl>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="search" id="search" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <span class="download_icon_set">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                </ul>
            </span>
        </div>
    </form>
</div>

<table id="flex1" style="display:none"></table>