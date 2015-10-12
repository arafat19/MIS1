package com.athena.mis.budget.actions.budgetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.budget.entity.BudgBudgetDetails
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Budget Details
 * For details go through Use-Case doc named 'ListBudgetDetailsActionService'
 */
class ListBudgetDetailsActionService extends BaseService implements ActionIntf {

    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Internal Server Error"
    private static final String FAILURE_MESSAGE = "Fail to list budget details"
    private static final String BUDGET_DETAILS_LIST = "budgetDetailsList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Give Budget Details List
     * 1. Get budget id from params
     * 2. Get budget details list by dynamic finder using budget Id and company id
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing budget details list, total of budget details list & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initSearch(parameterMap)
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())

            long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
            List<BudgBudgetDetails> budgetDetailsList = BudgBudgetDetails.findAllByBudgetIdAndCompanyId(budgetId, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int total = BudgBudgetDetails.countByBudgetIdAndCompanyId(budgetId, companyId)

            result.put(BUDGET_DETAILS_LIST, budgetDetailsList)
            result.put(Tools.COUNT, total)
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
     * Wrap Budget Details List in Grid Entity
     * @param obj - get a map from execute method with budget details list
     * @return - a wrapped map with budget details list & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<BudgBudgetDetails> budgetDetailsList = (List<BudgBudgetDetails>) receiveResult.get(BUDGET_DETAILS_LIST)
            List budgetDetailsListWrap = wrapInGridEntityList(budgetDetailsList, start)
            result = [page: pageNumber, total: count, rows: budgetDetailsListWrap]
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
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * Wrap Budget details list for showing in the grid
     * @param budgetDetailsList - list of budget details from buildSuccessResultForUI method
     * @param start - starting index of the page
     * @return - list of wrapped budget details
     */
    private List wrapInGridEntityList(List<BudgBudgetDetails> budgetDetailsList, int start) {
        List budgetDetailList = []
        int counter = start + 1
        BudgBudgetDetails budgetDetails
        Item item
        GridEntity obj
        ItemType itemType
        String quantityWithUnit
        for (int i = 0; i < budgetDetailsList.size(); i++) {
            budgetDetails = budgetDetailsList[i]
            obj = new GridEntity()
            obj.id = budgetDetails.id
            item = (Item) itemCacheUtility.read(budgetDetails.itemId)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            quantityWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            String totalConsumptionWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.totalConsumption) + Tools.SINGLE_SPACE + item.unit
            Double balance = budgetDetails.quantity - budgetDetails.totalConsumption
            String balanceWithUnit = Tools.formatAmountWithoutCurrency(balance) + Tools.SINGLE_SPACE + item.unit

            if (balance < 0) {
                balanceWithUnit = "<span style='color:red'>${balanceWithUnit}</span>"
            }
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
}
