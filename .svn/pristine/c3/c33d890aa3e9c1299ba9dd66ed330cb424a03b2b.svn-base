<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search PO Item Received
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidPoId" name="hidPoId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="poId">PO No:</label>

                <div class="col-md-3">
                    <input class="k-textbox"
                           type="text"
                           pattern="^(0|[1-9][0-9]*)$"
                           id="poId"
                           name="poId"
                           required="required"
                           placeholder="PO No."
                           tabindex="1"
                           validationMessage="Required" autofocus/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="poId"></span>
                </div>
            </div>

        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="2"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadPoItemReceived,/invReport/downloadPoItemReceivedCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadPoItemReceived">
                            <li><a href="javascript:void(0)" id="printPdfReport" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadPoItemReceivedCsv">
                            <li><a href="javascript:void(0)" id="printCsvReport" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
