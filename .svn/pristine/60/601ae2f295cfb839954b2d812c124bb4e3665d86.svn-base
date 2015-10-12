package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Parent class to show UI for inventory-consumption based on budget
 *  For details go through Use-Case doc named 'ShowForInventoryConsumptionActionService'
 */
class ShowForInventoryConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    BudgetPluginConnector budgetImplService
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String FAILURE_MSG = "Fail to load Inventory-Consumption page."
    private static final String COUNT_INVENTORY_CONSUMPTION = "countInventoryConsumption"
    private static final String COUNT_BUDGET = "countBudget"
    private static final String INVENTORY_CONSUMPTION_LIST = "inventoryConsumptionList"
    private static final String WRAPPED_BUDGET_LIST = "wrappedBudgetList"
    private static final String GRID_OBJ_BUDGET = "gridObjBudget"

    private static final String GRID_OBJ_CONSUMPTION = "gridObjConsumption"

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Get budget list for right panel grid and transactionTypeConsumption list for grid
     * @param params -N/A
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            List inventoryConsumptionListWrap = []
            List wrappedBudgetList = []
            List<GroovyRowResult> budgetList = []
            int countInvConsumption = 0
            int budgetCount = 0

            // get user associated inventory(s) id(s)s
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()

            // get user associated project(s) id(s)s
            List<Long> projectIds = getUserProjectIdsByInventoryIds(inventoryIds)

            if (inventoryIds.size() > 0) {
                initPager(parameters)
                resultPerPage = DEFAULT_RESULT_PER_PAGE

                //Get consumptionList for grid
                Map serviceReturn = (Map) listForInventoryConsumption(inventoryIds)
                List<GroovyRowResult> inventoryConsumptionList = (List<GroovyRowResult>) serviceReturn.inventoryConsumptionList
                countInvConsumption = (int) serviceReturn.count
                inventoryConsumptionListWrap = (List) wrapInventoryConsumptionList(inventoryConsumptionList, start)

                //Get consumptionList for budget grid at right panel
                resultPerPage = 20
                Map budgetServiceReturn = (Map) budgetImplService.listBudgetByProjectIdList(this, projectIds)
                budgetList = (List<GroovyRowResult>) budgetServiceReturn.budgetList
                budgetCount = (int) budgetServiceReturn.count
                wrappedBudgetList = (List) wrapBudgetList(budgetList, start)
            }

            result.put(INVENTORY_CONSUMPTION_LIST, inventoryConsumptionListWrap)
            result.put(WRAPPED_BUDGET_LIST, wrappedBudgetList)
            result.put(COUNT_INVENTORY_CONSUMPTION, countInvConsumption)
            result.put(COUNT_BUDGET, budgetCount)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventoryTransactionConsumption list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid, budget grid at right pan, inventoryType drop-down
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            int countConsumption = (int) receiveResult.get(COUNT_INVENTORY_CONSUMPTION)
            int countBudget = (int) receiveResult.get(COUNT_BUDGET)

            List wrappedConsumptionList = (List) receiveResult.get(INVENTORY_CONSUMPTION_LIST)
            List wrappedBudgetList = (List) receiveResult.get(WRAPPED_BUDGET_LIST)

            Map gridObjConsumption = [page: pageNumber, total: countConsumption, rows: wrappedConsumptionList]
            Map gridObjBudget = [page: pageNumber, total: countBudget, rows: wrappedBudgetList]

            result.put(GRID_OBJ_CONSUMPTION, gridObjConsumption)
            result.put(GRID_OBJ_BUDGET, gridObjBudget)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    // Return list of project IDs
    /**
     * Method to get list of projectIds associated with user by inventoryIds
     *
     * @param budgetList -list of budget object(s)
     * @param start -starting index of the page
     *
     * @return -list of wrapped budget
     */
    private List getUserProjectIdsByInventoryIds(List<Long> inventoryList) {
        List<Long> projectIds = []
        for (int i = 0; i < inventoryList.size(); i++) {
            InvInventory inventory = (InvInventory) invInventoryCacheUtility.read(inventoryList[i])
            projectIds << (Long) inventory.projectId
        }
        return projectIds.unique()
    }

    /**
     * Wrap list of budget for right pan grid
     *
     * @param budgetList -list of budget object(s)
     * @param start -starting index of the page
     *
     * @return -list of wrapped budget
     */
    private List wrapBudgetList(List<GroovyRowResult> budgetList, int start) {
        List budgets = [] as List
        GridEntity obj
        GroovyRowResult budget
        for (int i = 0; i < budgetList.size(); i++) {
            budget = budgetList[i]
            obj = new GridEntity()
            obj.id = budget.id
            obj.cell = [
                    budget.id,
                    budget.budget_item,
                    budget.details,
                    budget.project_id,
                    budget.project_name
            ]
            budgets << obj
        }
        return budgets
    }

    /**
     * Wrap list of inventoryConsumption for grid
     *
     * @param inventoryConsumptionList -list of inventoryTransactionConsumption object(s)
     * @param start -starting index of the page
     *
     * @return -list of wrapped inventoryTransactionConsumption
     */
    private List wrapInventoryConsumptionList(List<GroovyRowResult> inventoryConsumptionList, int start) {
        List inventoryConsumption = [] as List
        int counter = start + 1
        GridEntity obj
        for (int i = 0; i < inventoryConsumptionList.size(); i++) {
            obj = new GridEntity()
            obj.id = inventoryConsumptionList[i].id
            obj.cell = [counter,
                    inventoryConsumptionList[i].id,
                    inventoryConsumptionList[i].inventory_type + Tools.COLON + inventoryConsumptionList[i].inventory_name,
                    inventoryConsumptionList[i].budget_item,
                    inventoryConsumptionList[i].item_count,
                    inventoryConsumptionList[i].total_approve,
                    inventoryConsumptionList[i].created_by,
                    inventoryConsumptionList[i].updated_by ? inventoryConsumptionList[i].updated_by : Tools.EMPTY_SPACE
            ]
            inventoryConsumption << obj
            counter++
        }
        return inventoryConsumption
    }

    /**
     * Get list and count of inventoryTransactionsTypeConsumption
     * @param inventoryIdList -list of login user associated inventory ids
     * @return -a map containing list and count of inventoryConsumption for grid
     */
    private Map listForInventoryConsumption(List<Long> inventoryIdList) {
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(inventoryIdList)
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)

        String queryStr = """
             SELECT iit.id, to_char(iit.transaction_date, 'dd-Mon-YYYY') AS transaction_date_str,
                    se.key AS inventory_type, inventory.name AS inventory_name, budget.budget_item, iit.item_count,
                    (SELECT COUNT(iitd.id) FROM inv_inventory_transaction_details iitd
                    WHERE iitd.inventory_transaction_id = iit.id
                                                                AND iitd.approved_by > 0
                                                                AND iitd.is_current = TRUE) AS total_approve,
                    user_created_by.username as created_by,user_updated_by.username as updated_by
             FROM inv_inventory_transaction iit
                LEFT JOIN inv_inventory inventory ON inventory.id = iit.inventory_id
                LEFT JOIN budg_budget budget ON budget.id = iit.budget_id
                LEFT JOIN system_entity se ON se.id = inventory.type_id
                LEFT JOIN app_user user_created_by ON user_created_by.id = iit.created_by
                LEFT JOIN app_user user_updated_by ON user_updated_by.id = iit.updated_by
            WHERE iit.inventory_id IN (${inventoryIds}) AND
                  iit.transaction_type_id=:transactionTypeId AND
                  iit.transaction_entity_type_id=:transactionEntityTypeId AND
                  iit.budget_id > 0
           ORDER BY iit.id desc LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
            SELECT count(iit.id)
            FROM inv_inventory_transaction iit
          WHERE iit.inventory_id IN (${inventoryIds}) AND
                  iit.transaction_type_id=:transactionTypeId AND
                  iit.transaction_entity_type_id=:transactionEntityTypeId AND
                  iit.budget_id > 0
        """

        Map queryParams = [
                transactionTypeId: transactionTypeConsumption.id,
                transactionEntityTypeId: transactionEntityNone.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].count

        return [inventoryConsumptionList: result, count: count]
    }
}
