<app:ifAllUrl urls="/budgBudgetDetails/create,/budgBudgetDetails/update, /budgBudgetDetails/select">
    <g:render template='/budget/budgBudgetDetails/createForProcurement'/>
</app:ifAllUrl>
<g:render template='/budget/budgBudgetDetails/scriptForProcurement'/>
<table id="flex1" style="display:none"></table>