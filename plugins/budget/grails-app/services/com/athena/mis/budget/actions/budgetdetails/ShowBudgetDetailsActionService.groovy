package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.budget.entity.BudgBudget
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.entity.BudgBudgetScope
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.budget.utility.BudgetScopeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for budget details CRUD and list of budget details for grid
 *  For details go through Use-Case doc named 'ShowBudgetDetailsActionService'
 */
class ShowBudgetDetailsActionService extends BaseService implements ActionIntf {

    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    BudgetScopeCacheUtility budgetScopeCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to load budget details"
    private static final String DEFAULT_ERROR_MESSAGE = "Can't load budget details due to internal error"
    private static final String BUDGET_NOT_FOUND = "Budget not found"
    private static final String BUDGET_ID = "budgetId"
    private static final String BUDGET_INFO = "budgetInfo"
    private static final String BUDGET_DETAILS_LIST = "budgetDetailsList"
    private static final String TOTAL = "total"
    private static final String IS_PRODUCTION = "isProduction"

    /**
     * Get budget id from params
     * 1. Check budget is exist or not by budgetId
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long budgetId = Long.parseLong(params.budgetId.toString())

            //Checking budget is exist or not by budgetId
            if (budgetId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            result.put(BUDGET_ID, budgetId)
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
     * Get budget details list and budget map
     * 1. Get budget id from executePreCondition
     * 2. Get budget object by budget id
     * 3. Check the existence of budget object
     * 4. Build a budget map
     * 5. Get budget details list by dynamic finder by budget id and company id
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
            LinkedHashMap preResult = (LinkedHashMap) obj
            long budgetId = Long.parseLong(preResult.get(BUDGET_ID).toString())

            BudgBudget budget = BudgBudget.read(budgetId)
            //Checking the existence of budget object
            if (!budget) {
                result.put(Tools.MESSAGE, BUDGET_NOT_FOUND)
                return result
            }
            Map budgetInfo = buildBudgetMap(budget) // build budget information


            long companyId = budgSessionUtil.appSessionUtil.getAppUser().companyId
            List<BudgBudgetDetails> budgetDetailsList = BudgBudgetDetails.findAllByBudgetIdAndCompanyId(budgetId, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int total = BudgBudgetDetails.countByBudgetIdAndCompanyId(budgetId, companyId)

            result.put(BUDGET_INFO, budgetInfo)
            result.put(BUDGET_DETAILS_LIST, budgetDetailsList)
            result.put(TOTAL, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(IS_PRODUCTION, budget.isProduction)
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
     * Wrap budget details list for grid
     * 1. Get Budget details list from execute method
     * 2. Get Budget info from execute method
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receiveResult = (LinkedHashMap) obj
            List<BudgBudgetDetails> budgetDetailsList = (List<BudgBudgetDetails>) receiveResult.get(BUDGET_DETAILS_LIST)
            LinkedHashMap budgetInfo = (LinkedHashMap) receiveResult.get(BUDGET_INFO)
            int total = (int) receiveResult.get(TOTAL)
            List budgetDetailsListWrap = wrapBudgetDetailsInGridEntity(budgetDetailsList, this.start)
            Map gridObject = [page: pageNumber, total: total, rows: budgetDetailsListWrap]

            result.put(BUDGET_INFO, budgetInfo)
            result.put(BUDGET_DETAILS_LIST, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
     * Wrap list of budget details in grid entity
     * 1. Get item, item type from corresponding cacheUtility
     * @param budgetList -list of budget details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped budget details
     */
    private List wrapBudgetDetailsInGridEntity(List<BudgBudgetDetails> budgetDetailsList, int start) {
        List budgetDetailList = [] as List
        int counter = start + 1
        for (int i = 0; i < budgetDetailsList.size(); i++) {
            BudgBudgetDetails budgetDetails = budgetDetailsList[i]
            GridEntity obj = new GridEntity()
            Item item = (Item) itemCacheUtility.read(budgetDetails.itemId)
            ItemType itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            String quantityWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            String totalConsumptionWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.totalConsumption) + Tools.SINGLE_SPACE + item.unit
            Double balance = budgetDetails.quantity - budgetDetails.totalConsumption
            String balanceWithUnit = Tools.formatAmountWithoutCurrency(balance)+ Tools.SINGLE_SPACE + item.unit

            if(balance < 0){
                balanceWithUnit = "<span style='color:red'>${balanceWithUnit}</span>"
            }
            obj.id = budgetDetails.id
            obj.cell = [
                    counter,
                    itemType.name,
                    item.name,
                    quantityWithUnit,
                    totalConsumptionWithUnit,
                    balanceWithUnit,
                    Tools.makeAmountWithThousandSeparator(budgetDetails.rate),
                    Tools.makeAmountWithThousandSeparator(budgetDetails.quantity * budgetDetails.rate),
                    budgetDetails.isUpToDate? Tools.YES : "<span style='color:red'>NO</span>"
            ]
            budgetDetailList << obj
            counter++
        }
        return budgetDetailList
    }

    /**
     * Give a budget map
     * 1. Get project, budget type object from corresponding cacheUtility
     * @param budget - budget object from execute
     * @return - a map of budget
     */
    Map buildBudgetMap(BudgBudget budget) {
        Project project = (Project) projectCacheUtility.read(budget.projectId)
        BudgBudgetScope budgetScope = (BudgBudgetScope) budgetScopeCacheUtility.read(budget.budgetScopeId)
        String leftMenu = '#budgBudget/show'
        if (budget.isProduction) {
            leftMenu = '#budgBudget/show?isProduction=true'
        }
        Map budgetMap = [
                projectId: project.id,
                projectName: project.name,
                budgetScope: budgetScope.name,
                budgetId: budget.id,
                budgetItem: budget.budgetItem,
                details: Tools.makeDetailsShort(budget.details, 100),
                leftMenu: leftMenu
        ]
        return budgetMap
    }
}