<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Item Wise Budget Summary
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidProjectId" name="hidProjectId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject tabindex="1"
                                         dataModelName="dropDownProject"
                                         name="projectId"
                                         required="true"
                                         validationMessage="Required"
                                         hintsText="Please Select...">
                    </app:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchItemWiseSummary" name="searchItemWiseSummary" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="2"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadItemWiseBudgetSummary,/invReport/downloadItemWiseBudgetSummaryCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadItemWiseBudgetSummary">
                            <li><a href="javascript:void(0)" id="printItemWiseSummary" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadItemWiseBudgetSummaryCsv">
                            <li><a href="javascript:void(0)" id="printItemWiseSummaryCSV" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
