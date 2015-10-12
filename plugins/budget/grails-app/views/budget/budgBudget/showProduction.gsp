<app:ifAllUrl urls="/budgBudget/create,/budgBudget/update,/budgBudget/select">
    <g:render template='/budget/budgBudget/create'/>
</app:ifAllUrl>
<g:render template='/budget/budgBudget/scriptProduction'/>
<app:ifAllUrl urls="/budgBudget/list">
    <table id="flex1" style="display:none"></table>
</app:ifAllUrl>