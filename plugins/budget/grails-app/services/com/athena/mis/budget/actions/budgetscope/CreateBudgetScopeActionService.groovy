package com.athena.mis.budget.actions.budgetscope

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.service.BudgetScopeService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Budget Scope, save it to DB and update budgetScopeCacheUtility
 *  For details go through Use-Case doc named 'CreateBudgetScopeActionService'
 */
class CreateBudgetScopeActionService extends BaseService implements ActionIntf {

    BudgetScopeService budgetScopeService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String BUDGET_SCOPE = "budgetScope"
    private static final String BUDGET_SCOPE_SAVE_FAILURE_MESSAGE = "Budget scope has not been saved"
    private static final String BUDGET_SCOPE_NAME_ALREADY_EXISTS = "Same budget scope name already exists"
    private static final String BUDGET_SCOPE_SAVE_SUCCESS_MESSAGE = "Budget scope has been saved successfully"

    /**
     * 1. Check the access of director, project director and CFO
     * 2. Get budgetScope object from obj
     * 3. Validate budget Scope object
     * 4. Duplicate checking by budget Scope name and company id
     * @param params - N/A
     * @param obj - serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            BudgBudgetScope budgetScope = (BudgBudgetScope) obj
            budgetScope.companyId = budgSessionUtil.appSessionUtil.getCompanyId()

            boolean dir = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean pd = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)
            boolean cfo = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_CFO)
            // Has access check of director, project director and CFO
            if ((!dir) && (!pd) && (!cfo)) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            budgetScope.validate()
            if (budgetScope.hasErrors()) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
            int duplicateCount = BudgBudgetScope.countByCompanyIdAndNameIlike(budgetScope.companyId, budgetScope.name)
            if (duplicateCount > 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, BUDGET_SCOPE_NAME_ALREADY_EXISTS)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Save budget scope object in DB, add and sort budgetScopeCacheUtility
     * 1. This method is in transactional block and will roll back in case of any exception
     * 2. Get budget scope object from executePreCondition
     * 3. Budget Scope creates by create method of budgetScopeService
     * @param parameters -N/A
     * @param obj - map returned from executePreCondition method
     * @return -an object of BudgBudgetScope
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgBudgetScope budgetScopeInstance = (BudgBudgetScope) obj
            BudgBudgetScope newBudgetScope = budgetScopeService.create(budgetScopeInstance)
            budgetScopeCacheUtility.add(newBudgetScope, budgetScopeCacheUtility.SORT_BY_NAME, budgetScopeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(BUDGET_SCOPE, newBudgetScope)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(BUDGET_SCOPE_SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUDGET_SCOPE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Show newly created budget Scope object in grid
     * Show success message
     * @param obj - map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            BudgBudgetScope budgetScopeInstance = (BudgBudgetScope) receiveResult.get(BUDGET_SCOPE)
            GridEntity object = new GridEntity()
            object.id = budgetScopeInstance.id
            object.cell = [Tools.LABEL_NEW, budgetScopeInstance.id, budgetScopeInstance.name]
            Map resultMap = [entity: object, version: budgetScopeInstance.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, BUDGET_SCOPE_SAVE_SUCCESS_MESSAGE)
            result.put(BUDGET_SCOPE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUDGET_SCOPE_SAVE_SUCCESS_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, BUDGET_SCOPE_SAVE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BUDGET_SCOPE_SAVE_FAILURE_MESSAGE)
            return result
        }
    }
}
