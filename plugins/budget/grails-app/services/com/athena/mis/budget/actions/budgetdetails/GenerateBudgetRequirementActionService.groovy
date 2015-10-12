package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.service.BudgetDetailsService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Generate budget requirements according to budget schema
 * For details go through Use-Case doc named 'GenerateBudgetRequirementActionService'
 */
class GenerateBudgetRequirementActionService extends BaseService implements ActionIntf {

    private static final String SAVE_FAILURE_MESSAGE = "Could not generate budget requirements"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String BUDGET_OBJ = "budgetObj"
    private static final String BUDGET_DETAILS_LIST = "budgetDetailsList"

    private Logger log = Logger.getLogger(getClass())

    BudgetService budgetService
    BudgetDetailsService budgetDetailsService
    @Autowired
    BudgSessionUtil budgSessionUtil

    /**
     * 1. check required parameters
     * 2. pull budget object by budget id from params
     * 3. check the existence of budget object
     * 4. build budget details object by buildBudgetDetails method
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if ((!parameterMap.budgetId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            BudgBudget budget = budgetService.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            List<BudgBudgetDetails> lstBudgetDetails = buildBudgetDetails(budget) // build budget details object
            result.put(BUDGET_DETAILS_LIST, lstBudgetDetails)
            result.put(BUDGET_OBJ, budget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * 1. delete budget details objects if exists already
     * 2. save budget details objects in DB
     * 3. increase item count for budget
     * 4. build budget info map with necessary information of budget to show on UI
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgBudget budget = (BudgBudget) preResult.get(BUDGET_OBJ)
            int count = BudgBudgetDetails.countByBudgetId(budget.id)
            if (count > 0) {
                deleteBudgetDetails(budget.id)
            }
            List<BudgBudgetDetails> lstBudgetDetails = (List<BudgBudgetDetails>) preResult.get(BUDGET_DETAILS_LIST)
            List<BudgBudgetDetails> lstSavedBudgetDetails = []
            for (int i = 0; i < lstBudgetDetails.size(); i++) {
                BudgBudgetDetails budgetDetails = budgetDetailsService.create(lstBudgetDetails[i])
                lstSavedBudgetDetails << budgetDetails
            }
            int itemCount = lstSavedBudgetDetails.size()
            increaseItemCountForBudget(budget.id, itemCount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ex.message)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    private static final String DELETE_QUERY = """
        DELETE FROM budg_budget_details
        WHERE
        budget_id=:budgetId
    """

    /**
     * Delete budget details objects
     * @param budgetId -id of BudgBudget object
     * @return -an integer containing the value of delete count
     */
    private int deleteBudgetDetails(long budgetId) {
        Map queryParams = [
                budgetId: budgetId
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Generate budget requirement failed at delete budget details method')
        }
        return deleteCount
    }

    /**
     * Build Budget details objects
     * @param budget -object of budget from executePreCondition method
     * @return -list of budget details objects
     */
    private List<BudgBudgetDetails> buildBudgetDetails(BudgBudget budget) {
        List<BudgBudgetDetails> lstBudgetDetails = []
        List<BudgSchema> lstSchema = BudgSchema.findAllByBudgetId(budget.id)
        for (int i = 0; i < lstSchema.size(); i++) {
            BudgSchema schema = lstSchema[i]
            BudgBudgetDetails budgetDetails = new BudgBudgetDetails()
            budgetDetails.budgetId = budget.id
            budgetDetails.projectId = budget.projectId
            budgetDetails.itemId = schema.itemId
            budgetDetails.quantity = schema.quantity * budget.budgetQuantity
            budgetDetails.createdOn = new Date()
            budgetDetails.createdBy = budgSessionUtil.appSessionUtil.getAppUser().id
            budgetDetails.comments = schema.comments
            budgetDetails.rate = schema.rate
            budgetDetails.companyId = budget.companyId
            budgetDetails.isUpToDate = true

            lstBudgetDetails << budgetDetails
        }
        return lstBudgetDetails
    }

    private static final String UPDATE_QUERY = """
        UPDATE budg_budget SET
            item_count=:itemCount,
            version=version + 1
        WHERE id=:budgetId
    """

    /**
     * Increase item count of budget
     * @param budgetId -id of BudgBudget object
     * @param itemCount -count of items
     * @return -an integer containing the value of update count
     */
    private int increaseItemCountForBudget(long budgetId, int itemCount) {
        Map queryParams = [
                budgetId: budgetId,
                itemCount: itemCount
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update item count')
        }
        return updateCount
    }
}
