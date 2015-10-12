<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Budget
        </div>
    </div>

    <form id="projectBudgetForm" name="projectBudgetForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidProjectId" name="hidProjectId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>

                <div class="col-md-5">
                    <app:dropDownProject
                            name="projectId"
                            required="true"
                            validationMessage="Required"
                            dataModelName="dropDownProject">
                    </app:dropDownProject>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="projectId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showProjectBudget" name="showProjectBudget" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/budgReport/downloadProjectBudget">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printProjectBudget" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </form>
</div>