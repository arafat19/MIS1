<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Remittance Transaction
        </div>
    </div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/>">
    <form id='remittanceTransactionForm' name='remittanceTransactionForm' class="form-horizontal form-widgets"
          role="form" method="post">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="createdDateFrom">From Date:</label>

                <div class="col-md-2">
                    <app:dateControl name="createdDateFrom" value="${startDate}" tabindex="1">
                    </app:dateControl>

                </div>

                <label class="col-md-2 control-label label-required" for="createdDateTo">To Date:</label>

                <div class="col-md-2">
                    <app:dateControl name="createdDateTo"  tabindex="2">
                    </app:dateControl>
                </div>
                <label class="col-md-2 control-label label-required" for="amount">Amount Higher or Equal:</label>

                <div class="col-md-2">
                    <input type="text" id="amount" name="amount" tabindex="3" maxlength="20"
                           class="required k-textbox"/>

                </div>

            </div>


        </div>
       <div class="panel-footer">
        <button id="searchBatch" name="searchBatch" type="submit" data-role="button"
                class="k-button k-button-icontext"
                role="button" tabindex="4"
                aria-disabled="false"><span class="k-icon k-i-search"></span>Search
        </button>
           <span class="download_icon_set">
               <ul>
                   <li>Save as :</li>
                   <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                   <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
               </ul>
           </span>
       </div>



    </form>
</div>

<table id="flex1" style="display:none"></table>

<g:render template='/exchangehouse/report/remittanceTransaction/script'/>