<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Item Reconciliation
        </div>
    </div>

    <form id="itemReconciliationForm" name="itemReconciliationForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

            <div class="col-md-4">
                <app:dropDownProject
                        addAllAttributes="true"
                        tabindex="1"
                        name="projectId"
                        hintsText="ALL"
                        dataModelName="dropDownProject">
                </app:dropDownProject>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadForItemReconciliation,/invReport/downloadForItemReconciliationCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadForItemReconciliation">
                            <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadForItemReconciliationCsv">
                            <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
