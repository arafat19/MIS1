<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility" %>


<form>
    <app:systemEntityByReserved
            name="inProgressStatusId"
            reservedId="${BudgetTaskStatusCacheUtility.IN_PROGRESS_RESERVED_ID}"
            typeId="${SystemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS}">
    </app:systemEntityByReserved>
    <app:systemEntityByReserved
            name="completeStatusId"
            reservedId="${BudgetTaskStatusCacheUtility.COMPLETED_RESERVED_ID}"
            typeId="${SystemEntityTypeCacheUtility.TYPE_BUDG_TASK_STATUS}">
    </app:systemEntityByReserved>
</form>

<g:render template="/budget/budgTask/scriptForSprint"/>

<table id="flex1" style="display:none"></table>