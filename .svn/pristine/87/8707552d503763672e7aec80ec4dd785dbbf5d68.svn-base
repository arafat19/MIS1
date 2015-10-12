package com.athena.mis.inventory.actions.report.inventoryconsumption

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show specific list of budgets with number of consumed items
 *  For details go through Use-Case doc named 'ListForBudgetOfConsumptionActionService'
 */
class ListForBudgetOfConsumptionActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String LST_BUDGET_LINE_ITEM = "lstBudgetLineItem"
    private static final String EXCEPTION_MESSAGE = "Could not get the consumption list of project"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load the consumption list of project"
    private static final String NOT_FOUND_MESSAGE = "There are no consumption within given information"
    private static final String SORT_COLUMN = "budget.budget_item"
    private static final String PROJECT_ID = "projectId"
    private static final String INVENTORY_ID = "inventoryId"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get budget line item list for grid through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initPager(parameterMap) // initialize parameters for flexGrid
            // check required parameters
            if (!parameterMap.rp) {
                resultPerPage = 20
                start = 0
            }
            if (!parameterMap.sortname) {
                sortColumn = SORT_COLUMN
            }
            long projectId = Long.parseLong(parameterMap.projectId)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate)
            Date toDate = DateUtility.parseMaskedFromDate(parameterMap.toDate)
            long inventoryTypeId = Long.parseLong(parameterMap.inventoryTypeId.toString())
            List<Long> lstInventoryIds = []
            long inventoryId
            // get list of inventory by typeId(store/site) mapped with user
            if (parameterMap.inventoryId.equals(Tools.EMPTY_SPACE)) {
                lstInventoryIds = invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            } else {
                inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
                lstInventoryIds << new Long(inventoryId)
            }
            // get list and count of budget line item used in consumption
            LinkedHashMap serviceReturn = listBudgetOfConsumptionByProjectAndInventory(projectId, lstInventoryIds, fromDate, toDate)
            int count = serviceReturn.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            result.put(LST_BUDGET_LINE_ITEM, serviceReturn.budgetItemListOfProject)
            result.put(Tools.COUNT, count)
            result.put(PROJECT_ID, projectId)
            result.put(INVENTORY_ID, inventoryId)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
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
     * Wrap budget line item list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj    // cast map returned from execute method
            List<GroovyRowResult> lstBudgetLineItem = (List<GroovyRowResult>) executeResult.get(LST_BUDGET_LINE_ITEM)
            // wrap budget list in grid entity
            List lstWrapBudgetLineItem = wrapConsumedBudgetLineItemList(lstBudgetLineItem, start)
            int count = Integer.parseInt(executeResult.get(Tools.COUNT).toString())
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrapBudgetLineItem]
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(INVENTORY_ID, executeResult.get(INVENTORY_ID))
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(LST_BUDGET_LINE_ITEM, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
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
            LinkedHashMap previousResult = (LinkedHashMap) obj    // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of budget line items in grid entity
     * @param lstBudgetLineItem -list of budget line items
     * @param start -starting index of the page
     * @return -list of wrapped budget line items
     */
    private List wrapConsumedBudgetLineItemList(List<GroovyRowResult> lstBudgetLineItem, int start) {
        List lstWrapBudgetLineItem = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < lstBudgetLineItem.size(); i++) {
            singleRow = lstBudgetLineItem[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [counter, singleRow.id, singleRow.budget_item, singleRow.consump_count]
            lstWrapBudgetLineItem << obj
            counter++
        }
        return lstWrapBudgetLineItem
    }

    /**
     * Get list and count of consumed budget line items with number of consumed items
     * @param projectId -id of project
     * @param lstInventoryId -list of inventory ids
     * @param fromDate -start date
     * @param toDate -end date
     * @return -a map containing list and count of consumed budget line items
     */
    private Map listBudgetOfConsumptionByProjectAndInventory(long projectId, List<Long> lstInventoryId, Date fromDate, Date toDate) {
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryId)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        // query for list
        String queryStr = """SELECT budget.id,  budget.budget_item, COUNT(DISTINCT iitd.item_id) AS consump_count
            FROM budg_budget budget
            LEFT JOIN inv_inventory_transaction iit ON iit.budget_id = budget.id
            LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
            WHERE budget.project_id =:projectId
            AND iitd.inventory_id IN (${inventoryIds})
            AND iit.transaction_type_id =:transactionTypeId
            AND (iitd.transaction_date BETWEEN :fromDate AND :toDate)
            AND iitd.is_current  = true
            GROUP BY budget.id, budget.budget_item
            ORDER BY budget.budget_item ASC
            LIMIT :resultPerPage OFFSET :start
        """
        // query for count
        String queryCount = """
            SELECT COUNT(DISTINCT budget.id) AS count
            FROM budg_budget budget
            LEFT JOIN inv_inventory_transaction iit ON iit.budget_id = budget.id
            LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
            WHERE budget.project_id =:projectId AND
            iitd.inventory_id IN (${inventoryIds})
            AND iit.transaction_type_id =:transactionTypeId
            AND (iitd.transaction_date BETWEEN :fromDate AND :toDate)
            AND iitd.is_current  = true
        """

        Map queryParams = [
                projectId: projectId,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                resultPerPage: resultPerPage,
                start: start,
                transactionTypeId: transactionTypeCons.id
        ]
        List<GroovyRowResult> budgetItemListOfProject = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [budgetItemListOfProject: budgetItemListOfProject, count: count]
    }
}