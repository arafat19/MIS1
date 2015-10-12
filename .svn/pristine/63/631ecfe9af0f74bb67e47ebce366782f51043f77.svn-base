<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Budget Sprint
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="hidSprintId" id="hidSprintId"/>
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="sprintId">Sprint ID:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="sprintId" name="sprintId"
                           required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="sprintId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showSprintRpt" name="showSprintRpt" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/budgReport/downloadSprintReport">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printSprintReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </form>
</div>

<table id="flex1" style="display:none"></table>

<g:render template="/budget/report/budgetSprint/script"/>