package com.athena.mis.budget.actions.budget

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.budget.model.BudgetStatusModel
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get Budget Status For Budget DashBoard
 * For details go through Use-Case doc named 'GetBudgetStatusForDashBoardActionService'
 */
class GetBudgetStatusForDashBoardActionService extends BaseService implements ActionIntf {

    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to get budget status"
    private static final String BUDGET_STATUS_LIST = "budgetStatusList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Budget Status list for dashboard
     * 1. Get list of Project id from getEntityIdsByType method of budgSessionUtil
     * 2. Get budget status list from BudgetStatusModel by list of project ids
     * @param parameters -  serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget status list, count & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            List<Long> lstProjectId = budgSessionUtil.appSessionUtil.getUserProjectIds()
            if (!parameterMap.rp) {
                parameterMap.rp = 10  // default result per page =10
                parameterMap.page = 1
            }
            initPager(parameterMap)
            List<BudgetStatusModel> budgetStatusList = []
            int total = 0

            if (lstProjectId.size() > 0) {
                budgetStatusList = BudgetStatusModel.findAllByProjectIdInList(lstProjectId, [max: resultPerPage, offset: start, readOnly: true])
                total = BudgetStatusModel.countByProjectIdInList(lstProjectId)
            }

            result.put(BUDGET_STATUS_LIST, budgetStatusList)
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
     * Wrap budget status list for the dash board
     * @param budgetStatusList - list of budget status list(budgetList) from buildSuccessResultForUI method
     * @return - list of wrapped budgets
     */

    private List wrapGridEntityList(List<BudgetStatusModel> budgetStatusList) {
        List budgets = [] as List
        GridEntity obj
        int count = 1
        for (int i = 0; i < budgetStatusList.size(); i++) {
            obj = new GridEntity()
            obj.id = budgetStatusList[i].projectId
            obj.cell = [
                    count++,
                    budgetStatusList[i].projectId,
                    budgetStatusList[i].projectCode,
                    budgetStatusList[i].totalBudget,
                    budgetStatusList[i].contractValue,
                    budgetStatusList[i].revenueMargin
            ]
            budgets << obj
        }
        return budgets
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap Budget list in grid entity
     * 1. Get Budget list & count from executePreCondition
     * @param obj - map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<BudgetStatusModel> budgetList = (List<BudgetStatusModel>) receiveResult.get(BUDGET_STATUS_LIST)
            List budgetStatusListWrap = wrapGridEntityList(budgetList)
            Map output = [page: pageNumber, total: count, rows: budgetStatusListWrap]
            result.put(BUDGET_STATUS_LIST, output)
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
}
