<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Budget Contract Details
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidProjectId" value=""/>
            <g:hiddenField name="hideFromDate" value=""/>
            <g:hiddenField name="hideToDate" value=""/>
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-5">
                    <app:dropDownProject addAllAttributes="true"
                                         name="projectId"
                                         tabindex="1"
                                         dataModelName="dropDownProject"
                                         onchange="updateFromDate();">
                    </app:dropDownProject>
                </div>

                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-2">
                    <app:dateControl
                            name="fromDate"
                            tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-2">
                    <app:dateControl
                            name="toDate"
                            tabindex="3">
                    </app:dateControl>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchBudgetContractDetails" name="searchBudgetContractDetails" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/qsReport/downloadBudgetContractDetails,/qsReport/downloadBudgetContractCsvDetails">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/qsReport/downloadBudgetContractDetails">
                            <li><a href="javascript:void(0)" id="printBudgetContractDetails" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/qsReport/downloadBudgetContractCsvDetails">
                            <li><a href="javascript:void(0)" id="printBudgetContractCsvDetails" class="csv_icon"></a>
                            </li>
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