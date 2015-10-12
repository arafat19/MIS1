<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Custom Group Balance
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>
        <input type="hidden" name="hideProjectId" id="hideProjectId"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-optional">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         hintsText="ALL"
                                         tabindex="3"
                                         name="projectId">
                    </app:dropDownProject>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchCustomGroupBalance" name="searchCustomGroupBalance" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/accReport/downloadCustomGroupBalance,/accReport/downloadCustomGroupBalanceCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadCustomGroupBalance">
                            <li><a href="javascript:void(0)" id="printCustomGroupBalancePdf" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadCustomGroupBalanceCsv">
                            <li><a href="javascript:void(0)" id="printCustomGroupBalanceCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>