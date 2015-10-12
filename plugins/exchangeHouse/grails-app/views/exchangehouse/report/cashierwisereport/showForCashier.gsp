<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Cashier Wise Task report
        </div>
    </div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/> ">
    <form id='cashierWiseReportForAdmin' name='cashierWiseReportForAdmin' class="form-horizontal form-widgets" role="form">
       <input type="hidden" id="userCashierId" name="userCashierId" value="">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="createdDateFrom">From Date:</label>
                <div class="col-md-2">
                <app:dateControl name="createdDateFrom" tabindex="1">
                </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="createdDateTo">To Date:</label>
                <div class="col-md-2">
                    <app:dateControl name="createdDateTo" tabindex="2">
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

<div id="divFundTransferInfo" style="padding-bottom: 5px; visibility: visible;"></div>
<table id="flex1" style="display:none"></table>

<g:render template='/exchangehouse/report/cashierwisereport/scriptForCashier'/>