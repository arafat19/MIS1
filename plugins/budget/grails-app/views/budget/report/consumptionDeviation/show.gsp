<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Consumption Deviation Report
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="hidProjectId" id="hidProjectId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-5">
                    <app:dropDownProject
                            name="projectId"
                            required="true"
                            validationMessage="Select a project"
                            dataModelName="dropDownProject">
                    </app:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchConsumptionDeviation" name="searchConsumptionDeviation" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/budgReport/downloadConsumptionDeviation,/budgReport/downloadConsumptionDeviationCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/budgReport/downloadConsumptionDeviation">
                            <li><a href="javascript:void(0)" id="printConsumptionDeviation" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/budgReport/downloadConsumptionDeviationCsv">
                            <li><a href="javascript:void(0)" id="printConsumptionDeviationCsv" class="csv_icon"></a>
                            </li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>

<table id="flex1" style="display:none"></table>

<g:render template="/budget/report/consumptionDeviation/script"/>