<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Trial Balance
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>
        <input type="hidden" name="hideProjectId" id="hideProjectId"/>
        <input type="hidden" name="hideDivisionId" id="hideDivisionId"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-2">
                    <app:dateControl name="fromDate"
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-2">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-2">
                    <app:dropDownProject name="projectId"
                                         tabindex="3"
                                         hintsText="ALL"
                                         dataModelName="dropDownProject"
                                         onchange="populateDivision();">
                    </app:dropDownProject>
                </div>

                <label class="col-md-1 control-label label-optional" for="divisionId">Division:</label>

                <div class="col-md-2">
                    <select id="divisionId" name="divisionId" tabindex="4"></select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/accReport/downloadTrialBalanceOfLevel2,/accReport/downloadTrialBalanceCsvOfLevel2">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadTrialBalanceOfLevel2">
                            <li><a href="javascript:void(0)" id="printTrialBalancePdf" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadTrialBalanceCsvOfLevel2">
                            <li><a href="javascript:void(0)" id="printTrialBalanceCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>