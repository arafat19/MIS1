package com.athena.mis.budget.actions.budgschema

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.budget.entity.BudgSchema
import com.athena.mis.budget.service.BudgSchemaService
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search budget schema by specific key word & show result on grid
 *  For details go through Use-Case doc named 'SearchBudgSchemaActionService'
 */
class SearchBudgSchemaActionService extends BaseService implements ActionIntf {

    BudgSchemaService budgSchemaService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private static final String FAILURE_MESSAGE = "Failed to search budget schema"
    private static final String LST_BUDGET_SCHEMA = "lstBudgetSchema"
    private static final String ITEM_ID = "itemId"
    private static final String ITEM_TYPE_ID = "itemTypeId"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget schema list by item id, item type id and budget id
     * @param params -serialized params from UI
     * @param obj -N/A
     * @return -a map of budget schema list, count & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)
            // check required parameters
            if (!parameterMap.budgetId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            LinkedHashMap serviceReturn = new LinkedHashMap()
            serviceReturn.put(LST_BUDGET_SCHEMA, [])    // default value
            serviceReturn.put(Tools.COUNT, 0)           // default value
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
            List<BudgSchema> lstBudgetSchema = (List<BudgSchema>) serviceReturn.lstBudgetSchema
            int total = (int) serviceReturn.count
            result.put(LST_BUDGET_SCHEMA, lstBudgetSchema)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap budget schema list in grid entity
     * @param obj -a map from execute method
     * @return -a wrapped map with budget schema list & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List<BudgSchema> lstBudgetSchema = (List<BudgSchema>) executeResult.get(LST_BUDGET_SCHEMA)
            List lstWrappedBudgetSchema = wrapBudgetSchema(lstBudgetSchema, start)
            result = [page: pageNumber, total: count, rows: lstWrappedBudgetSchema]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap budget schema list for showing in the grid
     * @param lstBudgetSchema -list of budget schema from buildSuccessResultForUI method
     * @param start -starting index of the page
     * @return -list of wrapped budget schema
     */
    private List wrapBudgetSchema(List<BudgSchema> lstBudgetSchema, int start) {
        List lstWrappedBudgetSchema = []
        int counter = start + 1
        BudgSchema budgSchema
        Item item
        GridEntity obj
        ItemType itemType
        for (int i = 0; i < lstBudgetSchema.size(); i++) {
            budgSchema = lstBudgetSchema[i]
            item = (Item) itemCacheUtility.read(budgSchema.itemId)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            obj = new GridEntity()
            obj.id = budgSchema.id
            obj.cell = [
                    counter,
                    itemType.name,
                    item.name,
                    Tools.formatAmountWithoutCurrency(budgSchema.quantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.makeAmountWithThousandSeparator(budgSchema.rate)
            ]
            lstWrappedBudgetSchema << obj
            counter++
        }
        return lstWrappedBudgetSchema
    }

    /**
     * Get list of budget schema by item & budget id
     * @param budgetId -id of budget
     * @return -a map with budget schema list
     */
    private Map searchByItemAndBudgetId(long budgetId) {
        List<Item> lstMatchedItems = (List<Item>) itemCacheUtility.search(itemCacheUtility.NAME, query)
        List<Long> lstMatchedItemIds = []
        for (int i = 0; i < lstMatchedItems.size(); i++) {
            lstMatchedItemIds << lstMatchedItems[i].id
        }
        List<BudgSchema> lstBudgetSchema = []
        int total = 0
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        if (lstMatchedItemIds.size() > 0) {
            lstBudgetSchema = budgSchemaService.findAllByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId, this)
            total = budgSchemaService.countByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId)
        }
        return [lstBudgetSchema: lstBudgetSchema, count: total]
    }

    /**
     * Get list of budget schema by item type & budget id
     * @param budgetId -id of budget
     * @return -a map with budget schema list
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
        List<BudgSchema> lstBudgetSchema = []
        int total = 0
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        if (lstMatchedItemIds.size() > 0) {
            lstBudgetSchema = budgSchemaService.findAllByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId, this)
            total = budgSchemaService.countByBudgetIdAndItemIdInListAndCompanyId(budgetId, lstMatchedItemIds, companyId)
        }
        return [lstBudgetSchema: lstBudgetSchema, count: total]
    }

    /**
     * Get budget schema list by budget id and company id
     * @param budgetId -id of budget
     * @return -a map with budget schema list
     */
    private LinkedHashMap listWithBudgetId(long budgetId) {
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        List<BudgSchema> lstBudgetSchema = budgSchemaService.findAllByBudgetIdAndCompanyId(budgetId, companyId, this)
        int total = budgSchemaService.countByBudgetIdAndCompanyId(budgetId, companyId)
        return [lstBudgetSchema: lstBudgetSchema, count: total]
    }
}
