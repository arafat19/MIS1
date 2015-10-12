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
 * Get list of budget schema for grid
 * For details go through Use-Case doc named 'ListBudgSchemaActionService'
 */
class ListBudgSchemaActionService extends BaseService implements ActionIntf {

    BudgSchemaService budgSchemaService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Fail to list budget schema"
    private static final String LST_BUDGET_SCHEMA = "lstBudgetSchema"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of budget schema
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing list & count of budget schema & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)
            // check required parameters
            if (!parameterMap.budgetId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long budgetId = Long.parseLong(parameterMap.budgetId.toString())
            long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
            List<BudgSchema> lstBudgetSchema = budgSchemaService.findAllByBudgetIdAndCompanyId(budgetId, companyId, this)
            int total = budgSchemaService.countByBudgetIdAndCompanyId(budgetId, companyId)
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
     * @param obj -a map returned from execute method
     * @return - a wrapped map with budget schema list & isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List<BudgSchema> lstBudgetSchema = (List<BudgSchema>) receiveResult.get(LST_BUDGET_SCHEMA)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
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
     * Wrap Budget schema list for showing in the grid
     * @param lstBudgetSchema -list of budget schema
     * @param start -starting index of the page
     * @return -list of wrapped budget schema
     */
    private List wrapBudgetSchema(List<BudgSchema> lstBudgetSchema, int start) {
        List lstWrappedBudgetSchema = []
        int counter = start + 1
        BudgSchema budgSchema
        Item item
        ItemType itemType
        for (int i = 0; i < lstBudgetSchema.size(); i++) {
            budgSchema = lstBudgetSchema[i]
            item = (Item) itemCacheUtility.read(budgSchema.itemId)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            GridEntity obj = new GridEntity()
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
}
