<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Combined QS Measurement
        </div>

    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidProjectId" value=""/>
            <g:hiddenField name="hidFromDate" value=""/>
            <g:hiddenField name="hidToDate" value=""/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-9">
                            <app:dropDownProject addAllAttributes="true"
                                                 name="projectId"
                                                 tabindex="1"
                                                 dataModelName="dropDownProject"
                                                 onchange="updateFromDate();">
                            </app:dropDownProject>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="fromDate">From:</label>

                        <div class="col-md-8">
                            <app:dateControl
                                    name="fromDate"
                                    tabindex="2">
                            </app:dateControl>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="toDate">To:</label>

                        <div class="col-md-8">
                            <app:dateControl
                                    name="toDate"
                                    tabindex="3">
                            </app:dateControl>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchQsMeasurement" name="searchQsMeasurement" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/qsReport/downloadCombinedQSM,/qsReport/downloadCombinedQSMCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/qsReport/downloadCombinedQSM">
                            <li><a href="javascript:void(0)" id="printBudgetQs" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/qsReport/downloadCombinedQSMCsv">
                            <li><a href="javascript:void(0)" id="printBudgetQsCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>


<div class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Budget Information
        </div>
    </div>

    <div class="panel-body">
        <div class="form-group">
            <label class="col-md-3 control-label label-optional" for="lblBudgetDetails">Budget Details:</label>

            <div class="col-md-9">
                <span id='lblBudgetDetails'></span>
            </div>
        </div>
    </div>
</div>
<table id="flex1" style="display:none"></table>