package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.budget.model.BudgetProjectModel
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Search Budget
/**
 * Search Budget and show in grid of UI
 * For details go through Use-Case doc named 'SearchBudgetActionService'
 */
class SearchBudgetActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to search budget"
    private static final String NO_ASSOCIATION_MESSAGE = "User is not associated with any project"
    private static final String BUDGET_LIST = "budgetList"
    private static final String MATERIAL = "material"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Budget List by several searching criteria
     * 1. Get project ids by getEntityIdsByType method of budgSessionUtil.appSessionUtil
     * 2. Association check with user by projectIds.size
     * 3. If queryType == MATERIAL becomes true then budget list comes from searchByProjectIdsAndMaterial method of BudgetProjectModel
     * 4. If queryType == MATERIAL becomes false then budget list comes from searchByProjectIdsAndQuery method of BudgetProjectModel
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map of budget list , total & isError(TRUE/FALSE)
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initSearch(parameterMap)

            List<Long> projectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()
            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, NO_ASSOCIATION_MESSAGE)
                return result
            }

            List<BudgetProjectModel> budgetList = []
            int total = 0
            boolean isProduction = Boolean.parseBoolean(parameterMap.isProduction.toString())

            if (queryType == MATERIAL) {
                budgetList = BudgetProjectModel.searchByProjectIdsAndIsProductionAndMaterial(projectIds, isProduction, query).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                total = BudgetProjectModel.searchByProjectIdsAndIsProductionAndMaterial(projectIds, isProduction, query).count()
            } else {
                budgetList = BudgetProjectModel.searchByProjectIdsAndIsProductionAndQuery(projectIds, isProduction,  queryType, query).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                total = BudgetProjectModel.searchByProjectIdsAndIsProductionAndQuery(projectIds, isProduction, queryType, query).count()
            }

            result.put(BUDGET_LIST, budgetList)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<BudgetProjectModel> budgetList = (List<BudgetProjectModel>) receiveResult.get(BUDGET_LIST)
            List budgetListWrap = wrapBudgetInGridEntityList(budgetList, start)
            result = [page: pageNumber, total: count, rows: budgetListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of budget in grid entity
     * @param budgetList - budgetList comes from buildSuccessResultForUI
     * @param start - starting index of the page
     * @return - list of wrapped budget
     */
    private List wrapBudgetInGridEntityList(List<BudgetProjectModel> budgetList, int start) {
        List budgets = []
        int counter = start + 1
        BudgetProjectModel budgetProjectModel
        GridEntity obj
        String targetQuantityWithUnit
        for (int i = 0; i < budgetList.size(); i++) {
            budgetProjectModel = budgetList[i]
            obj = new GridEntity()
            targetQuantityWithUnit = Tools.formatAmountWithoutCurrency(budgetProjectModel.budgetQuantity) + Tools.SINGLE_SPACE + budgetProjectModel.unitName
            String details = Tools.makeDetailsShort(budgetProjectModel.budgetDetails, Tools.DEFAULT_LENGTH_DETAILS_OF_BUDGET)
            obj.id = budgetList[i].budgetId
            obj.cell = [
                    counter,
                    budgetProjectModel.budgetItem,
                    budgetProjectModel.budgetScopeName,
                    budgetProjectModel.projectCode,
                    targetQuantityWithUnit,
                    budgetProjectModel.itemCount,
                    budgetProjectModel.billable ? Tools.YES : Tools.NO,
                    details,
                    budgetProjectModel.taskCount,
                    budgetProjectModel.contentCount

            ]
            budgets << obj
            counter++
        }
        return budgets
    }
}
