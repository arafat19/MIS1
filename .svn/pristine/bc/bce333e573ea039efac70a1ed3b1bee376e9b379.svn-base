package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of inventory-consumption(parent) for grid
 *  For details go through Use-Case doc named 'ListForInventoryConsumptionActionService'
 */
class ListForInventoryConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String FAILURE_MSG = "Fail to load Inventory-Consumption List"
    private static final String COUNT = "count"
    private static final String INVENTORY_CONSUMPTION_LIST = "inventoryConsumptionList"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  wrap list of inventory-consumption(parent) for grid
     *
     * @Params params -Receives from UI
     * @Params obj -N/A
     *
     * @return -a map containing all objects necessary for grid data
     *          map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)

            List inventoryConsumptionListWrap = []
            int count = 0

            //Get id list of userMappedInventoryIds
            List<Long> inventoryIdList = invSessionUtil.getUserInventoryIds()
            if (inventoryIdList.size() > 0) {//if userMappedInventoryIds > 0 then get list of inventory-consumption(parent) of that Inventories
                LinkedHashMap serviceReturn = (LinkedHashMap) listForInventoryConsumption(inventoryIdList)
                List<GroovyRowResult> inventoryConsumptionList = (List<GroovyRowResult>) serviceReturn.inventoryConsumptionList
                count = (int) serviceReturn.count
                inventoryConsumptionListWrap = (List) wrapInventoryTransactionList(inventoryConsumptionList, start)
            }

            result.put(INVENTORY_CONSUMPTION_LIST, inventoryConsumptionListWrap)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @param obj -map returned from execute method
     * @return -a map containing WrappedConsumptionList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryConsumptionList = (List) receiveResult.get(INVENTORY_CONSUMPTION_LIST)
            result = [page: pageNumber, total: count, rows: inventoryConsumptionList]
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
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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

    /**
     * wrapInventoryConsumptionList for grid
     * @param inventoryConsumptionList -InventoryConsumptionList
     * @param start -start index
     * @return wrappedInventoryConsumptionList
     */
    private List wrapInventoryTransactionList(List<GroovyRowResult> inventoryConsumptionList, int start) {
        List inventoryTransactions = [] as List
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
            inventoryTransactions << obj
            counter++
        }
        return inventoryTransactions
    }

    /**
     * Get ConsumptionList(parent)
     * @param lstInventories -list of inventoryIds(inventory.id)
     * @return - a map containing list of GroovyRowResult(inventoryConsumptionList) &
     *              count(Total number of-inventoryConsumptionList)
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
           ORDER BY iit.id desc
           LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
            SELECT count(iit.id)
            FROM inv_inventory_transaction iit
            WHERE iit.inventory_id IN  (${inventoryIds}) AND
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

