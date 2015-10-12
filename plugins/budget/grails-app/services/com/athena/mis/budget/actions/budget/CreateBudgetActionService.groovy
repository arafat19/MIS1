package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.service.BudgetService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new budget object and show in grid
 *  For details go through Use-Case doc named 'CreateBudgetActionService'
 */
class CreateBudgetActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SAVE_SUCCESS_MESSAGE = "Budget has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can not save budget"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String LINE_ITEM_ALREADY_EXISTS = "Budget line item already exists"
    private static final String DATE_RANGE_EXCEEDS = "Budget date range should be within Project date range"
    private static final String BUDGET_OBJ = "budget"

    BudgetService budgetService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

    /**
     * Get parameters from UI and build budget object
     * 1. Check existence of required params and show input validation message
     * 2. Check budget date range is within project data range or not
     * 3. Build Budget object by parameterMap
     * 4. Check duplicate budget by budget item and company id
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
            if ((!parameterMap.budgetItem) || (!parameterMap.details)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
            Date endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())

            String exceedsDateRange = checkProjectDateRange(startDate, endDate, project)
            if (exceedsDateRange) {
                result.put(Tools.MESSAGE, exceedsDateRange)
                return result
            }

            BudgBudget newBudget = buildBudget(parameterMap)    // build budget object

            // check unique budget
            int countBudget = BudgBudget.countByBudgetItemAndCompanyId(newBudget.budgetItem, newBudget.companyId)
            if (countBudget > 0) {
                result.put(Tools.MESSAGE, LINE_ITEM_ALREADY_EXISTS)
                return result
            }

            result.put(BUDGET_OBJ, newBudget)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save budget object in DB
     * 1. This method is in transactional boundary and will roll back in case of any exception
     * 2. Get budget object from executePreCondition
     * 3. Create budget by budgetService.create
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
            LinkedHashMap preResult = (LinkedHashMap) obj    // cast map returned from executePreCondition method
            BudgBudget budget = (BudgBudget) preResult.get(BUDGET_OBJ)
            BudgBudget budgetInstance = budgetService.create(budget)  // save new budget in DB
            result.put(BUDGET_OBJ, budgetInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
     * Show newly created budget object in grid
     * 1. Show success message
     * 2. Pull project, unit, budget type from corresponding cacheUtility
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj     // cast map returned from execute method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            BudgBudget budgetInstance = (BudgBudget) receiveResult.get(BUDGET_OBJ)
            GridEntity object = new GridEntity()    // build new grid object
            object.id = budgetInstance.id
            Project project = (Project) projectCacheUtility.read(budgetInstance.projectId)
            SystemEntity unit = (SystemEntity) unitCacheUtility.read(budgetInstance.unitId)
            String targetQuantityWithUnit = Tools.formatAmountWithoutCurrency(budgetInstance.budgetQuantity) + Tools.SINGLE_SPACE + unit.key
            BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budgetInstance.budgetScopeId)
            // if details is too long - make it short to display in grid
            String details = Tools.makeDetailsShort(budgetInstance.details, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)
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

            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj      // cast map returned from previous method
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
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build budget object
     * @param parameterMap -serialized parameters from UI
     * @return -new budget object
     */
    private BudgBudget buildBudget(GrailsParameterMap parameterMap) {
        BudgBudget budget = new BudgBudget(parameterMap)
        AppUser systemUser = budgSessionUtil.appSessionUtil.getAppUser()
        budget.createdOn = new Date()
        budget.createdBy = systemUser.id
        budget.companyId = systemUser.companyId
        budget.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        budget.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())

        // ensure null on updateBy field
        budget.updatedOn = null
        budget.updatedBy = 0

        //add prefix at line item
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        budget.budgetItem = project.code.toString() + Tools.HYPHEN + budget.budgetItem.toString()
        boolean isProduction = Boolean.parseBoolean(parameterMap.isProduction.toString())
        budget.isProduction = isProduction

        budget.itemCount = 0
        budget.contractRate = 0.0d
        if (parameterMap.contractRate.toString().length() > 0) {
            try {
                budget.contractRate = Double.parseDouble(parameterMap.contractRate.toString())
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
}
