package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.service.BudgetDetailsService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Create Budget Details with Item(s)
 *  For details go through Use-Case doc named 'CreateBudgetDetailsActionService'
 */
class CreateBudgetDetailsActionService extends BaseService implements ActionIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Budget details has been saved successfully"
    private static final String MATERIAL_ALREADY_EXISTS = "Budget details with same material already exists"
    private static final String SAVE_FAILURE_MESSAGE = "Can not save budget details"
    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String BUDGET_DETAILS_OBJ = "budgetDetails"

    private Logger log = Logger.getLogger(getClass())

    BudgetService budgetService
    BudgetDetailsService budgetDetailsService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    /**
     * Get budget details
     * 1. Check required params are present yes or not
     * 2. Pull budget object by budget id from params
     * 3. Checking the existence of budget object
     * 4. Build budget details by buildBudgetDetails method
     * 5. Budget Details duplicate count by budget id and item id
     * 6. If budgetDetails.hasErrors() becomes then it will show INPUT_VALIDATION_ERROR in the UI
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

            // check here for required params are present
            if ((!parameterMap.budgetId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            BudgBudget budget = budgetService.read(budgetId)
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }

            BudgBudgetDetails budgetDetails = buildBudgetDetails(parameterMap, budget) // build budget details object

            int duplicateCount = BudgBudgetDetails.countByBudgetIdAndItemId(budget.id, budgetDetails.itemId)
            if (duplicateCount > 0) {
                result.put(Tools.MESSAGE, MATERIAL_ALREADY_EXISTS)
                return result
            }

            // checks input validation
            budgetDetails.validate()
            if (budgetDetails.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            result.put(BUDGET_DETAILS_OBJ, budgetDetails)
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
     * Save budget object in DB
     * 1. This method is in transactional boundary and will roll back in case of any exception
     * 2. Get budgetDetails object from executePreCondition
     * 3. Budget details creates by create method of budgetDetailsService
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
            BudgBudgetDetails budgetDetails = (BudgBudgetDetails) preResult.get(BUDGET_DETAILS_OBJ)
            //create budget details
            BudgBudgetDetails budgetDetailsInstance = budgetDetailsService.create(budgetDetails)
            // Checking is the budget details created or not
            if (!budgetDetailsInstance) {
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
                return result
            }
            //increase itemCount of budget(parent)
            increaseCountForBudgetDetails(budgetDetails.budgetId)

            result.put(BUDGET_DETAILS_OBJ, budgetDetailsInstance)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created budget details object in grid
     * Show success message
     * @param obj - map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgBudgetDetails budgetDetails = (BudgBudgetDetails) receiveResult.get(BUDGET_DETAILS_OBJ)
            GridEntity object = new GridEntity()
            object.id = budgetDetails.id
            Item item = (Item) itemCacheUtility.read(budgetDetails.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            String quantityWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            String totalConsumptionWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.totalConsumption) + Tools.SINGLE_SPACE + item.unit
            Double balance = budgetDetails.quantity - budgetDetails.totalConsumption
            String balanceWithUnit = Tools.formatAmountWithoutCurrency(balance) + Tools.SINGLE_SPACE + item.unit

            if (balance < 0) {
                balanceWithUnit = "<span style='color:red'>${balanceWithUnit}</span>"
            }
            object.cell = [
                    Tools.LABEL_NEW,
                    itemType.name,
                    item.name,
                    quantityWithUnit,
                    totalConsumptionWithUnit,
                    balanceWithUnit,
                    Tools.makeAmountWithThousandSeparator(budgetDetails.rate),
                    Tools.makeAmountWithThousandSeparator(budgetDetails.quantity * budgetDetails.rate)
            ]
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
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

    /**
     * Build Budget details
     * @param parameterMap - serialized parameters from UI
     * @param budget - object of budget from executePreCondition method
     * @return - a object of budget details
     */
    private BudgBudgetDetails buildBudgetDetails(GrailsParameterMap parameterMap, BudgBudget budget) {

        BudgBudgetDetails budgetDetails = new BudgBudgetDetails(parameterMap)
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()

        budgetDetails.budgetId = budget.id
        budgetDetails.projectId = budget.projectId
        budgetDetails.createdOn = new Date()
        budgetDetails.createdBy = systemUser.id

        // ensure null on updateBy and additional fields
        budgetDetails.totalConsumption = 0.00
        budgetDetails.updatedOn = null
        budgetDetails.updatedBy = 0
        budgetDetails.companyId = systemUser.companyId
        budgetDetails.isUpToDate = true
        return budgetDetails
    }

    private static final String UPDATE_QUERY = """
            UPDATE budg_budget SET
                item_count = item_count + 1,
                version= version + 1
            WHERE
                id=:budgetId
        """

    /**
     * Item number increase by sql query
     * @param budgetId - budget id comes from execute method
     * @return - an integer updateCount
     */
    private int increaseCountForBudgetDetails(long budgetId) {
        Map queryParams = [budgetId: budgetId]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update item count')
        }
        return updateCount
    }
}
