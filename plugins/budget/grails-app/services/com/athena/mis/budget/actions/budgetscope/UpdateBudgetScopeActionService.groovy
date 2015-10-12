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
 *  Update Budget scope object and grid data
 *  For details go through Use-Case doc named 'UpdateBudgetScopeActionService'
 */
class UpdateBudgetScopeActionService extends BaseService implements ActionIntf {

    BudgetScopeService budgetScopeService
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String UPDATE_FAILURE_MESSAGE = "Budget scope could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Budget scope has been updated successfully"
    private static final String BUDGET_TYPE_NAME_ALREADY_EXISTS = "Same budget scope name already exists"
    private static final String BUDGET_SCOPE = "budgetScope"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * 1. Check the access of director, project director and CFO
     * 2. Get budgetScope object from obj
     * 3. Validate budget scope object
     * 4. Duplicate checking by budget scope name and company id
     * @param params - N/A
     * @param obj - serialized parameters from UI
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)

            BudgBudgetScope budgetScope = (BudgBudgetScope) obj

            boolean dir = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR)
            boolean pd = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)
            boolean cfo = budgSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_CFO)
            if ((!dir) && (!pd) && (!cfo)) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            budgetScope.validate()
            if (budgetScope.hasErrors()) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }
            int duplicateCount = BudgBudgetScope.countByCompanyIdAndNameIlikeAndIdNotEqual(budgetScope.companyId, budgetScope.name, budgetScope.id)
            if (duplicateCount > 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, BUDGET_TYPE_NAME_ALREADY_EXISTS)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
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
     * update budget scope object in DB, update and sort in budgetScopeCacheUtility
     * 1. This method is in transactional block and will roll back in case of any exception
     * 2. Get budget scope object from executePreCondition
     * 3. Budget scope updates by update method of budgetScopeService
     * @param parameters -N/A
     * @param obj - map returned from executePreCondition method
     * @return -an integer of update count
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            BudgBudgetScope budgetScopeInstance = (BudgBudgetScope) obj
            int updateCount = budgetScopeService.update(budgetScopeInstance)
            budgetScopeInstance.version = budgetScopeInstance.version + 1
            budgetScopeCacheUtility.update(budgetScopeInstance, budgetScopeCacheUtility.SORT_BY_NAME, budgetScopeCacheUtility.SORT_ORDER_ASCENDING)
            return new Integer(updateCount)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            return new Integer(0)
        }
    }

    /**
     * Give total object and update success message
     * 1. Get budget scope object from execute
     * @param obj - a map from execute
     * @return - a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            BudgBudgetScope budgetScopeServiceReturn = (BudgBudgetScope) obj
            GridEntity object = new GridEntity()
            object.id = budgetScopeServiceReturn.id
            object.cell = [Tools.LABEL_NEW, budgetScopeServiceReturn.id, budgetScopeServiceReturn.name]
            Map resultMap = [entity: object, version: budgetScopeServiceReturn.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(BUDGET_SCOPE, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            if (obj) {
                LinkedHashMap receiveResult = (LinkedHashMap) obj
                if (receiveResult.message) {
                    result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
                }
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
}
