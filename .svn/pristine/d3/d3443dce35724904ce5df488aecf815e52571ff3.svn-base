package com.athena.mis.integration.budget

import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.model.BudgetProjectItemModel
import com.athena.mis.budget.utility.BudgetTaskStatusCacheUtility
import com.athena.mis.integration.budget.actions.*
import org.springframework.beans.factory.annotation.Autowired

class BudgetImplService extends BudgetPluginConnector {

    static transactional = false
    static lazyInit = false

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(BUDGET, this);
        return true
    }

    @Override
    public String getName() {
        return BUDGET;
    }

    @Override
    public int getId() {
        return BUDGET_ID;
    }

    SearchBudgetByLineItemImplActionService searchBudgetByLineItemImplActionService
    ReadBudgetImplActionService readBudgetImplActionService
    ReadBudgSprintImplActionService readBudgSprintImplActionService
    ReadBudgetByLineItemImplActionService readBudgetByLineItemImplActionService
    ListBudgetByProjectIdsImplActionService listBudgetByProjectIdsImplActionService
    ReadBudgetDetailsImplActionService readBudgetDetailsImplActionService
    ReadBudgetTypeImplActionService readBudgetTypeImplActionService
    ReadBudgetDetailsByBudgetAndItemImplActionService readBudgetDetailsByBudgetAndItemImplActionService
    UpdateContentCountForBudgetImplActionService updateContentCountForBudgetImplActionService

    BudgDefaultDataBootStrapService budgDefaultDataBootStrapService
    BudgetBootStrapService budgetBootStrapService
    BudgSchemaUpdateBootStrapService budgSchemaUpdateBootStrapService
    @Autowired
    BudgetTaskStatusCacheUtility budgetTaskStatusCacheUtility

    // Return the budget sprint Object by id
    Object readBudgSprint(long id) {
        return readBudgSprintImplActionService.execute(id, null)
    }

    // Return the budget Object by id
    Object readBudget(long id) {
        return readBudgetImplActionService.execute(id, null)
    }

    // Return the budgetDetails Object by id
    Object readBudgetDetails(long projectId, long itemId) {
        BudgetProjectItemModel budgetDetails = (BudgetProjectItemModel) readBudgetDetailsImplActionService.execute(projectId, itemId)
        return budgetDetails
    }

    // Return budget scope Object by id
    Object readBudgetScope(long id) {
        Object budgetScope = readBudgetTypeImplActionService.execute(id, null)
        return budgetScope
    }

    // Search budget by budgetLineItem
    Object searchByBudgetItem(String budgetLineItem) {
        Object lstBudget = searchBudgetByLineItemImplActionService.execute(budgetLineItem, null)
        return lstBudget
    }

    // Read budget by budgetLineItem
    Object readByBudgetItem(String budgetLineItem) {
        BudgBudget budget = (BudgBudget) readBudgetByLineItemImplActionService.execute(budgetLineItem, null)
        return budget
    }

    // Read budgetDetails by budgetId and itemId
    Object readBudgetDetailsByBudgetAndItem(long budgetId, long itemId) {
        BudgBudgetDetails budgetDetails = (BudgBudgetDetails) readBudgetDetailsByBudgetAndItemImplActionService.execute(budgetId, itemId)
        return budgetDetails
    }

    // Get budget list by list of project ids to build budget list for right panel
    Object listBudgetByProjectIdList(BaseService baseService, List<Long> lstProjects) {
        Map result = (Map) listBudgetByProjectIdsImplActionService.execute(baseService, lstProjects)
        return result
    }

    // update content count for budget during create, update and delete content for budget
    Object updateContentCountForBudget(long budgetId, int count){
        Integer updateCount = (Integer) updateContentCountForBudgetImplActionService.execute(budgetId, count)
        return updateCount
    }

    public void bootStrap(boolean isSchema, boolean isData) {
        if (isData) budgDefaultDataBootStrapService.init()
        if (isSchema) budgSchemaUpdateBootStrapService.init()
        budgetBootStrapService.init()
    }

    // Re-initialize the whole cacheUtility of construction type (used in create,update,delete)
    public void initBudgetTaskStatusCacheUtility() {
        budgetTaskStatusCacheUtility.init()
    }


    // get list of budget task status system entity
    public List<Object> listBudgetTaskStatus() {
        return budgetTaskStatusCacheUtility.listByIsActive()
    }

    // get reserved system entity of budget task status type
    public Object readByReservedBudgetTaskStatus(long reservedId, long companyId) {
        return budgetTaskStatusCacheUtility.readByReservedAndCompany(reservedId, companyId)
    }
}
