package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.budget.model.BudgetProjectModel
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for budget CRUD and list of budget for grid
 *  For details go through Use-Case doc named 'ShowBudgetActionService'
 */
class ShowBudgetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility

    private static final String SERVER_ERROR_MESSAGE = "Failed to load budget page"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't load budget list due to internal error"
    private static final String BUDGET_LIST = "budgetList"
    private static final String TOTAL = "total"
    private static final String IS_PRODUCTION = "isProduction"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget list
     * 1. Get project ids from getEntityIdsByType method of budgSessionUtil.appSessionUtil
     * 2. Get budget list from findAllByProjectIdInList method of BudgetProjectModel
     * @param params - N/A
     * @param obj - N/A
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            boolean isProduction = Boolean.FALSE
            if (parameterMap.isProduction) {
                isProduction = Boolean.TRUE
            }
            List<Long> projectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()

            List<BudgetProjectModel> budgetList = []
            int total = 0

            if (projectIds.size() > 0) {
                budgetList = BudgetProjectModel.findAllByProjectIdInListAndIsProduction(projectIds, isProduction, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = (int) BudgetProjectModel.countByProjectIdInListAndIsProduction(projectIds, isProduction)
            }

            result.put(BUDGET_LIST, budgetList)
            result.put(TOTAL, total)
            result.put(IS_PRODUCTION, isProduction)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap budget list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            List<BudgetProjectModel> budgetList = (List<BudgetProjectModel>) receiveResult.get(BUDGET_LIST)
            int total = (int) receiveResult.get(TOTAL)
            boolean isProduction = (boolean) receiveResult.get(IS_PRODUCTION)
            List budgetListWrap = wrapBudgetInGridEntityList(budgetList, start)
            Map gridObject = [page: pageNumber, total: total, rows: budgetListWrap]
            result = [gridObject: gridObject, isProduction: isProduction]
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            result.put(IS_PRODUCTION, receiveResult.get(IS_PRODUCTION))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of budget in grid entity
     * @param budgetList -list of budget object(s)
     * @param start -starting index of the page
     * @return -list of wrapped budget
     */
    private List wrapBudgetInGridEntityList(List<BudgetProjectModel> budgetList, int start) {
        List budgets = []
        int counter = start + 1
        BudgetProjectModel budget
        GridEntity obj
        String targetQuantityWithUnit
        for (int i = 0; i < budgetList.size(); i++) {
            budget = budgetList[i]
            obj = new GridEntity()
            targetQuantityWithUnit = Tools.formatAmountWithoutCurrency(budget.budgetQuantity) + Tools.SINGLE_SPACE + budget.unitName
            String details = Tools.makeDetailsShort(budget.budgetDetails, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)
            obj.id = budgetList[i].budgetId
            obj.cell = [
                    counter,
                    budget.budgetItem,
                    budget.budgetScopeName,
                    budget.projectCode,
                    targetQuantityWithUnit,
                    budget.itemCount,
                    budget.billable ? Tools.YES : Tools.NO,
                    details,
                    budget.taskCount,
                    budget.contentCount
            ]
            budgets << obj
            counter++
        }
        return budgets
    }
}
