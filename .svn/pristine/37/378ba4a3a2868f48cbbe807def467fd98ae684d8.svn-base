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
 *  Show list of budget for QS grid
 *  For details go through Use-Case doc named 'ListBudgetGridForQsActionService'
 */
class ListBudgetGridForQsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String ERROR_MESSAGE = "Failed to search budget list"
    private static final String BUDGET_LIST = "budgetList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget list for grid
     * 1. Get project ids from getEntityIdsByType method of budgSessionUtil.appSessionUtil
     * 2. Get budget list from BudgetProjectModel by list of project ids
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map of budget list, count & isError(TRUE/FALSE)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            initSearch(parameterMap)

            List<Long> lstProjectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()

            List<BudgetProjectModel> budgetList = []
            int total = 0
            if (lstProjectIds.size() > 0) {
                budgetList = BudgetProjectModel.findAllByProjectIdInListAndBillable(lstProjectIds, true, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                total = (int) BudgetProjectModel.countByProjectIdInListAndBillable(lstProjectIds, true)
            }

            result.put(BUDGET_LIST, budgetList)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
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
     * 1. Get budget list form execute
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
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
     * Wrap list of budget list in grid entity
     * @param budgetList -list of budget(s)
     * @param start -starting index of the page
     * @return -list of wrapped budget(s)
     */
    private List wrapGridEntityList(List<BudgetProjectModel> budgetList) {
        List budgets = []
        GridEntity obj
        for (int i = 0; i < budgetList.size(); i++) {
            obj = new GridEntity()
            obj.id = budgetList[i].budgetId
            obj.cell = [
                    budgetList[i].budgetId,
                    budgetList[i].budgetItem,
                    budgetList[i].budgetDetails,
                    budgetList[i].projectId,
                    budgetList[i].projectName,
                    budgetList[i].budgetQuantity,
                    budgetList[i].unitName
            ]
            budgets << obj
        }
        return budgets
    }
}
