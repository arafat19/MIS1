<g:render template='/budget/budgBudgetDetails/script'/>
<app:ifAllUrl urls="/budgBudgetDetails/create,/budgBudgetDetails/update, /budgBudgetDetails/select">
    <g:render template='/budget/budgBudgetDetails/create'/>
</app:ifAllUrl>

<table id="flex1" style="display:none"></table>