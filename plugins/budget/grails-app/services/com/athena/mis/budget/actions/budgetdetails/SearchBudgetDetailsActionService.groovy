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
 *  Search budget details and show specific list of budget details
 *  For details go through Use-Case doc named 'SearchBudgetDetailsActionService'
 */
class SearchBudgetDetailsActionService extends BaseService implements ActionIntf {
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private static final String SERVER_ERROR_MESSAGE = "Fail to search budget details"
    private static final String FAILURE_MESSAGE = "Fail to search budget details"
    private static final String BUDGET_DETAILS_LIST = "budgetDetailsList"
    private static final String ITEM_ID = "itemId"
    private static final String ITEM_TYPE_ID = "itemTypeId"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Budget Details list by item id, item type id and budget id
     * @param params - serialized params from UI
     * @param obj - N/A
     * @return - a map of budget details list, count & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())

            LinkedHashMap serviceReturn = new LinkedHashMap()
            serviceReturn.put(BUDGET_DETAILS_LIST, [])    // default value
            serviceReturn.put(Tools.COUNT, 0)                   // default value

            switch (queryType) {
                case ITEM_ID:
                    serviceReturn = searchByItemAndBudgetId(budgetId)
                    break
                case ITEM_TYPE_ID:
                    serviceReturn = searchByItemTypeAndBudgetId(budgetId)
                    break
                default:
                    serviceReturn = listWithBudgetId(budgetId)
                    break
            }

            List<BudgBudgetDetails> budgetDetailsList = (List<BudgBudgetDetails>) serviceReturn.budgetDetailsList
            int total = (int) serviceReturn.count

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
            item = (Item) itemCacheUtility.read(budgetDetails.itemId)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            quantityWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            String totalConsumptionWithUnit = Tools.formatAmountWithoutCurrency(budgetDetails.totalConsumption) + Tools.SINGLE_SPACE + item.unit
            Double balance = budgetDetails.quantity - budgetDetails.totalConsumption
            String balanceWithUnit = Tools.formatAmountWithoutCurrency(balance) + Tools.SINGLE_SPACE + item.unit

            if (balance < 0) {
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
     * Give map of budget details list and it's total by using item id and budget id
     * @param budgetId - budget id comes from params
     * @return - a map with budget details list
     */
    private Map searchByItemAndBudgetId(long budgetId) {
        List<Item> lstMatchedItems = (List<Item>) itemCacheUtility.search(itemCacheUtility.NAME, query)
        List<Long> lstMatchedItemIds = []
        for (int i = 0; i < lstMatchedItems.size(); i++) {
            lstMatchedItemIds << lstMatchedItems[i].id
        }
        List<BudgBudgetDetails> budgetDetailsList = []
        int total = 0
        long companyId = budgSessionUtil.appSessionUtil.getAppUser().companyId
        if (lstMatchedItemIds.size() > 0) {
            budgetDetailsList = BudgBudgetDetails.findAllByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            total = BudgBudgetDetails.countByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId)
        }
        return [budgetDetailsList: budgetDetailsList as List, count: total]
    }

    /**
     * Give map of budget details list and it's total by using item type id and budget id
     * @param budgetId - budget id comes from params
     * @return - a map with budget details list
     */
    private Map searchByItemTypeAndBudgetId(long budgetId) {
        List<ItemType> lstMatchedItemTypes = (List<ItemType>) itemTypeCacheUtility.search(itemTypeCacheUtility.NAME, query)
        List<Item> lstMatchedItems = []
        List<Long> lstMatchedItemIds = []
        //get item
        for (int i = 0; i < lstMatchedItemTypes.size(); i++) {
            lstMatchedItems = itemCacheUtility.listByItemTypeId(Long.parseLong(lstMatchedItemTypes[i].id.toString()))
            //get item id
            for (int j = 0; j < lstMatchedItems.size(); j++) {
                lstMatchedItemIds << lstMatchedItems[j].id
            }
        }

        List<BudgBudgetDetails> budgetDetailsList = []
        int total = 0
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        if (lstMatchedItemIds.size() > 0) {
            budgetDetailsList = BudgBudgetDetails.findAllByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            total = BudgBudgetDetails.countByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId)
        }
        return [budgetDetailsList: budgetDetailsList as List, count: total]
    }

    //followed method used to get BudgBudget Details List by budget id
    /**
     * Get Budget Details list by budget id and company id
     * @param budgetId - budget id comes from params
     * @return - a map of budget details list and it's total
     */
    private LinkedHashMap listWithBudgetId(long budgetId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        List<BudgBudgetDetails> budgetDetailsList = BudgBudgetDetails.findAllByBudgetIdAndCompanyId(budgetId, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
        int total = BudgBudgetDetails.countByBudgetIdAndCompanyId(budgetId, companyId)
        return [budgetDetailsList: budgetDetailsList, count: total]
    }
}
