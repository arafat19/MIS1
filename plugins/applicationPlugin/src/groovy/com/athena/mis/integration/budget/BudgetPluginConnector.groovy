package com.athena.mis.integration.budget

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector

public abstract class BudgetPluginConnector extends PluginConnector{

    // Return the budget sprint Object by id
    public abstract Object readBudgSprint(long id)

    // Return the budget Object by id
    public abstract Object readBudget(long id)

    // Return the budgetDetails Object by id
    public abstract Object readBudgetDetails(long projectId, long itemId)

    // Return budget scope Object by id
    public abstract Object readBudgetScope(long id)

    // Search budget by budgetLineItem
    public abstract Object searchByBudgetItem(String budgetLineItem)

    // Read budget by budgetLineItem
    public abstract Object readByBudgetItem(String budgetLineItem)

    // Read budget by budgetId and itemId
    public abstract Object readBudgetDetailsByBudgetAndItem(long budgetId, long itemId)

    // Get budget list by list of project ids to build budget list for right panel
    public abstract Object listBudgetByProjectIdList(BaseService baseService, List<Long> lstProjects)

    // update content count for budget during create, update and delete content for budget
    public abstract Object updateContentCountForBudget(long budgetId, int count)

    public abstract void bootStrap(boolean isSchema, boolean isData)

    //initialize budget task status cache utility
    public abstract void initBudgetTaskStatusCacheUtility()

    // get list of budget task status system entity
    public abstract List<Object> listBudgetTaskStatus()

    // get reserved system entity of acceptance criteria type
    public abstract Object readByReservedBudgetTaskStatus(long reservedId, long companyId)
}