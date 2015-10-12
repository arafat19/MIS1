<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Budget Wise QS
        </div>

    </div>
    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidProjectId" value=""/>
            <g:hiddenField name="hidFromDate" value=""/>
            <g:hiddenField name="hidToDate" value=""/>
            <g:hiddenField name="hidIsGovtQs" value=""/>
            <div class="form-group">
                <div class="col-md-4">
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

                <div class="col-md-2">
                    <div class="form-group">
                        <label class="col-md-6 control-label label-optional" for="isGovtQs">Is Govt.:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="4" name="isGovtQs"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="searchQsMeasurement" name="searchQsMeasurement" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/qsReport/downloadBudgetWiseQs,/qsReport/downloadBudgetWiseQsCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/qsReport/downloadBudgetWiseQs">
                            <li><a href="javascript:void(0)" id="printBudgetQs" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/qsReport/downloadBudgetWiseQsCsv">
                            <li><a href="javascript:void(0)" id="printBudgetWiseQsCsv" class="csv_icon"></a></li>
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
            <label class="col-md-2 control-label label-optional" for="lblBudgetDetails">Budget Details:</label>

            <div class="col-md-10">
                <span id='lblBudgetDetails'></span>
            </div>
        </div>
    </div>
</div>
<table id="flex1" style="display:none"></table>