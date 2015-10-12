package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.service.BudgetDetailsService
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.integration.qsmeasurement.QsMeasurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update Budget object and grid data
 *  For details go through Use-Case doc named 'UpdateBudgetActionService'
 */
class UpdateBudgetActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    BudgetService budgetService
    BudgetDetailsService budgetDetailsService
    @Autowired(required = false)
    QsMeasurementPluginConnector qsMeasurementImplService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private static final String UPDATE_SUCCESS_MESSAGE = "Budget has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Can not update budget"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String BUDGET_OBJ_NOT_FOUND = "Budget not found. Please refresh the grid"
    private static final String BUDGET_VERSION_NOT_MATCH = "Budget may updated by someone. Please refresh the grid"
    private static final String BUDGET_TARGET_LOWER_THAN_INTERNAL_QS = "Budget target can not be lower than Internal QS Measurement"
    private static final String BUDGET_TARGET_LOWER_THAN_GOVT_QS = "Budget target can not be lower than Govt. QS Measurement"
    private static final String LINE_ITEM_ALREADY_EXISTS = "Budget line item already exists"
    private static final String BUDGET_OBJ = "budget"
    private static final String DATE_RANGE_EXCEEDS = "Budget date range should be within Project date range"
    private static final String BILLABLE_BUDGET_HAS_QS_MESSAGE = "This billable budget has association with QS measurement"
    private static final String IS_UP_TO_DATE = "isUpToDate"
    private static final String QUANTITY_MSG = "Budget quantity can not be lower than already added sprint budget quantity"

    /**
     * Get parameters from UI and build budget object for update
     * 1. Checking params for INPUT_VALIDATION_ERROR
     * 2. Get Budget id from params and pull budget object
     * 3. Check budget date range is within project data range or not
     * 4. Build new Budget object for update
     * 5. Check budget unique Line Item
     * 6. Check budget association with any QS
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
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.budgetItem) || (!parameterMap.details)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            int version = Integer.parseInt(parameterMap.version.toString())

            BudgBudget oldBudgetObj = budgetService.read(id)
            // Checking oldBudget object existence
            if (!oldBudgetObj) {
                result.put(Tools.MESSAGE, BUDGET_OBJ_NOT_FOUND)
                return result
            }
            // Checking the version of old budget object and new version from params
            if (oldBudgetObj.version != version) {
                result.put(Tools.MESSAGE, BUDGET_VERSION_NOT_MATCH)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
            Date endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())
            Project project = (Project) projectCacheUtility.read(oldBudgetObj.projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            String exceedsDateRange = checkProjectDateRange(startDate, endDate, project)
            if (exceedsDateRange) {
                result.put(Tools.MESSAGE, exceedsDateRange)
                return result
            }

            BudgBudget newBudget = buildBudget(parameterMap, oldBudgetObj)
            // check unique LineItem
            int totalDuplicateLineItem = BudgBudget.countByBudgetItemAndIdNotEqual(newBudget.budgetItem, newBudget.id)
            if (totalDuplicateLineItem > 0) {
                result.put(Tools.MESSAGE, LINE_ITEM_ALREADY_EXISTS)
                return result
            }
            double sprintBudgetQuantity = getSprintBudgetQuantity(newBudget.id)
            if (newBudget.budgetQuantity < sprintBudgetQuantity) {
                result.put(Tools.MESSAGE, QUANTITY_MSG)
                return result
            }
            result.put(IS_UP_TO_DATE, Boolean.TRUE)
            if (newBudget.budgetQuantity != oldBudgetObj.budgetQuantity) {
                result.put(IS_UP_TO_DATE, Boolean.FALSE)
            }
            // Check if budget is associated with any QS
            if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
                int qsCount = qsMeasurementImplService.countQSMeasurementByBudgetId(newBudget.id)
                if (qsCount > 0) {
                    boolean isGovt = false
                    double internalQsCompletedWork = qsMeasurementImplService.getQsSumOfBudget(newBudget.id, isGovt)
                    if (newBudget.budgetQuantity < internalQsCompletedWork) {
                        result.put(Tools.MESSAGE, BUDGET_TARGET_LOWER_THAN_INTERNAL_QS)
                        return result
                    }
                    isGovt = true
                    double govtQsCompletedWork = qsMeasurementImplService.getQsSumOfBudget(newBudget.id, isGovt)
                    if (newBudget.budgetQuantity < govtQsCompletedWork) {
                        result.put(Tools.MESSAGE, BUDGET_TARGET_LOWER_THAN_GOVT_QS)
                        return result
                    }
                }
                if ((oldBudgetObj.billable) && (!newBudget.billable) && (qsCount > 0)) {
                    result.put(Tools.MESSAGE, BILLABLE_BUDGET_HAS_QS_MESSAGE)
                    return result
                }
            }
            result.put(BUDGET_OBJ, newBudget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update budget object in DB
     * 1. This function is in transactional boundary and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - serialized parameters from UI
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Boolean isUpToDate = (Boolean) preResult.get(IS_UP_TO_DATE)
            BudgBudget budget = (BudgBudget) preResult.get(BUDGET_OBJ)
            budgetService.update(budget)
            if (!isUpToDate.booleanValue()) {
                int budgetItemCount = budgetDetailsService.countByBudgetId(budget.id)
                if (budgetItemCount > 0) {
                    updateBudgetDetails(budget.id)
                }
            }
            result.put(BUDGET_OBJ, budget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Give total object and update success message
     * 1. Get budget object from execute
     * 2. Get project, unit, budget type from corresponding cacheUtility
     * @param obj - a map from execute
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgBudget budgetInstance = (BudgBudget) receiveResult.get(BUDGET_OBJ)
            GridEntity object = new GridEntity()
            Project project = (Project) projectCacheUtility.read(budgetInstance.projectId)
            SystemEntity unit = (SystemEntity) unitCacheUtility.read(budgetInstance.unitId)
            String targetQuantityWithUnit = Tools.formatAmountWithoutCurrency(budgetInstance.budgetQuantity) + Tools.SINGLE_SPACE + unit.key
            BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budgetInstance.budgetScopeId)
            String details = Tools.makeDetailsShort(budgetInstance.details, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)
            object.id = budgetInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    budgetInstance.budgetItem,
                    budgetScope.name,
                    project.code,
                    targetQuantityWithUnit,
                    budgetInstance.itemCount,
                    budgetInstance.billable ? Tools.YES : Tools.NO,
                    details,
                    budgetInstance.taskCount,
                    budgetInstance.contentCount
            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build budget object for update
     * @param parameterMap - serialized parameters from UI
     * @param oldBudgetObj - old budget object from executePreCondition
     * @return - updated budget object
     */
    private BudgBudget buildBudget(GrailsParameterMap parameterMap, BudgBudget oldBudgetObj) {

        BudgBudget budget = new BudgBudget(parameterMap)
        // ensure internal inputs
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()
        budget.updatedOn = new Date()
        budget.updatedBy = systemUser.id
        budget.startDate = DateUtility.parseMaskedDate(parameterMap.startDate)
        budget.endDate = DateUtility.parseMaskedDate(parameterMap.endDate)
        // set old budget's property to new budget for validation
        budget.id = oldBudgetObj.id
        budget.version = oldBudgetObj.version
        budget.createdOn = oldBudgetObj.createdOn
        budget.createdBy = oldBudgetObj.createdBy
        budget.projectId = oldBudgetObj.projectId
        budget.itemCount = oldBudgetObj.itemCount
        budget.companyId = oldBudgetObj.companyId
        budget.contractRate = 0.0d
        budget.isProduction = oldBudgetObj.isProduction
        if (parameterMap.contractRate.toString().length() > 0) {
            try {
                budget.contractRate = Double.parseDouble(parameterMap.contractRate)
            } catch (Exception e) {
                budget.contractRate = 0.0d
            }
        } else {
            budget.contractRate = 0.0d
        }
        return budget
    }

    /**
     * Check project date range with budget date range
     * @param startDate - BudgBudget start date
     * @param endDate - BudgBudget end date
     * @param project - object of Project
     * @return - a static message string or null
     */
    public static String checkProjectDateRange(Date startDate, Date endDate, Project project) {
        if ((startDate < project.startDate) || (endDate > project.endDate)) {
            return DATE_RANGE_EXCEEDS
        }
        return null
    }

    private static final String UPDATE_QUERY = """
        UPDATE budg_budget_details
        SET
            is_up_to_date = false
        WHERE
        budget_id=:budgetId
    """

    private int updateBudgetDetails(long budgetId) {
        Map queryParams = [
                budgetId: budgetId
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Budget item update failed on UpdateBudgetActionService')
        }
        return updateCount
    }

    private static final String SPRINT_BUDGET_QUANTITY = """
        SELECT COALESCE(SUM(quantity),0) AS quantity
        FROM budg_sprint_budget
        WHERE budget_id=:budgetId
    """

    private double getSprintBudgetQuantity(long budgetId) {
        Map queryParams = [
                budgetId: budgetId
        ]
        List<GroovyRowResult> result = executeSelectSql(SPRINT_BUDGET_QUANTITY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].quantity
        }
        return 0.0d
    }
}
