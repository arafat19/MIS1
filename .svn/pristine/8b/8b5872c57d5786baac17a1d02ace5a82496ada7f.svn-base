
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Budget
        </div>
    </div>

    <g:formRemote name="searchForm" url="[controller: 'budgReport', action: 'searchBudgetRpt']" method="POST"
                  update="updateBudgetDetailsDiv"
                  before="if (!executePreConditionForSearchBudget()) return false"
                  onComplete="executePostCondition()">
        <div class="panel-body">
            <input type="hidden" name="hideBudgetId" id="hideBudgetId"/>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="budgetLineItem">Budget Line Item:</label>

                <div class="col-md-4">
                    <input type="text" class="k-textbox" id="budgetLineItem" name="budgetLineItem"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="budgetLineItem"></span>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="showBudgetRpt" name="showBudgetRpt" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAllUrl urls="/budgReport/downloadBudgetRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printBudgetReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </g:formRemote>
</div>


<div id="updateBudgetDetailsDiv" style="display: none">
    <g:render template='/budget/report/budget/tmpBudget'/>
</div>
<g:render template="/budget/report/budget/script"/>