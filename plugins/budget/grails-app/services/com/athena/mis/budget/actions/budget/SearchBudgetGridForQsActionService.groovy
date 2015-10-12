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

/**
 * Search Budget For QS
 * For details go through Use-Case doc named 'SearchBudgetGridForQsActionService'
 */
class SearchBudgetGridForQsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String ERROR_MESSAGE = "Failed to load budget list"
    private static final String BUDGET_LIST = "budgetList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Budget List for QS
     * 1. Get list of project ids by getEntityIdsByType method of budgSessionUtil.appSessionUtil
     * 2. Get Budget list from listBudgetByDetailsAndItemAndBillable method of BudgetProjectModel by lstProjectIds
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map of budget list , total & isError(TRUE/FALSE)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            List<Long> lstProjectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()
            initSearch(parameterMap)

            List<BudgetProjectModel> budgetList = []
            int total = 0
            if (lstProjectIds.size() > 0) {
                budgetList = BudgetProjectModel.listBudgetByDetailsAndItemAndBillable(lstProjectIds, query, true).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                total = BudgetProjectModel.listBudgetByDetailsAndItemAndBillable(lstProjectIds, query, true).count()
            }
            result.put(BUDGET_LIST, budgetList)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
     * 1. Get budget list from execute
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<BudgetProjectModel> budgetList = (List<BudgetProjectModel>) receiveResult.get(BUDGET_LIST)
            List budgetListWrap = wrapGridEntityList(budgetList)
            result = [page: pageNumber, total: count, rows: budgetListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of budget in grid entity
     * @param budgetList - budgetList comes from buildSuccessResultForUI
     * @param start - starting index of the page
     * @return - list of wrapped budget(s)
     */
    private List wrapGridEntityList(List<BudgetProjectModel> budgetProjectModel) {
        List budgets = [] as List
        GridEntity obj
        for (int i = 0; i < budgetProjectModel.size(); i++) {
            obj = new GridEntity()
            obj.id = budgetProjectModel[i].budgetId
            obj.cell = [budgetProjectModel[i].budgetId,
                    budgetProjectModel[i].budgetItem,
                    budgetProjectModel[i].budgetDetails,
                    budgetProjectModel[i].projectId,
                    budgetProjectModel[i].projectName,
                    budgetProjectModel[i].budgetQuantity,
                    budgetProjectModel[i].unitName
            ]
            budgets << obj
        }
        return budgets
    }
}