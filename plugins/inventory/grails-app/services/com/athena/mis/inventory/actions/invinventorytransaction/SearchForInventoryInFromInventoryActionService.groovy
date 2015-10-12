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
 *  Get specific list of inventory transaction(Inventory In From Inventory) for grid through search
 *  For details go through Use-Case doc named 'SearchForInventoryInFromInventoryActionService'
 */
class SearchForInventoryInFromInventoryActionService extends BaseService implements ActionIntf {

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to search Inventory-In transaction List"
    private static final String LST_INVENTORY_IN = "lstInventoryIn"
    private static final String QUERY_TYPE_TRANSACTION_ID = "transactionId"
    private static final String MSG_INVALID_TRANSACTION_ID = "Invalid transfer id"
    private static final String SORT_NAME = "iit.id"
    private static final String SORT_ORDER = "desc"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get inventory transaction(Inventory In From Inventory) list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    //default value
            LinkedHashMap serviceReturn
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.rp) {
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = SORT_ORDER
            }
            initSearch(parameterMap)    // initialize parameters for flexGrid

            int total = 0
            List<GroovyRowResult> lstInventoryIn = []
            // get ids of inventory associated with user
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            // check input validation for transaction id
            long transactionId = 0
            if (queryType == QUERY_TYPE_TRANSACTION_ID) {
                try {
                    transactionId = Long.parseLong(query)
                } catch (Exception ex) {
                    result.put(Tools.MESSAGE, MSG_INVALID_TRANSACTION_ID)
                    return result
                }
            }
            // get list and count of inventory transaction (Inventory In From Inventory)
            if (inventoryIds.size() > 0) {
                serviceReturn = searchForInventoryInFromInventory(inventoryIds)
                lstInventoryIn = serviceReturn.inventoryInList
                total = (int) serviceReturn.count
            }

            result.put(LST_INVENTORY_IN, lstInventoryIn)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventory transaction list (Inventory In From Inventory) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryIn = (List) executeResult.get(LST_INVENTORY_IN)
            // wrap inventory transaction list (Inventory In From Inventory) in grid entity
            List lstWrappedInventoryIns = wrapInventoryInList(lstInventoryIn, start)
            result = [page: pageNumber, total: count, rows: lstWrappedInventoryIns]
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * Wrap list of inventory transactions (Inventory In From Inventory) in grid entity
     * @param lstInventoryIn -list of inventory transaction object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions (Inventory In From Inventory)
     */
    private List wrapInventoryInList(List<GroovyRowResult> lstInventoryIn, int start) {
        List lstWrappedInventoryIn = []
        GroovyRowResult invInventoryIn
        GridEntity obj
        int counter = start + 1
        for (int i = 0; i < lstInventoryIn.size(); i++) {
            invInventoryIn = lstInventoryIn[i]
            obj = new GridEntity()
            obj.id = invInventoryIn.id
            obj.cell = [
                    counter,
                    invInventoryIn.id,
                    invInventoryIn.transfer_date_str,
                    invInventoryIn.transaction_date_str,
                    invInventoryIn.inventory_type_name + Tools.COLON + invInventoryIn.to_inventory_name,
                    invInventoryIn.from_inventory_name,
                    invInventoryIn.item_count,
                    invInventoryIn.total_approve,
                    invInventoryIn.created_by,
                    invInventoryIn.updated_by ? invInventoryIn.updated_by : Tools.EMPTY_SPACE,
                    invInventoryIn.transfer_id,
            ]
            lstWrappedInventoryIn << obj
            counter++
        }
        return lstWrappedInventoryIn
    }

    /**
     * Get list and count of inventory transactions (Inventory In From Inventory)
     * @param inventoryIdList -list of inventory ids
     * @return -a map containing list and count of inventory transactions (Inventory In From Inventory)
     */
    private LinkedHashMap searchForInventoryInFromInventory(List<Long> inventoryIdList) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        // get comma separated string of ids from list of ids
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(inventoryIdList)
        // query for list
        String queryStr = """
        SELECT iit.id, to_char(iit_out.transaction_date, 'dd-Mon-YYYY') AS transfer_date_str,
              to_char(iit.transaction_date, 'dd-Mon-YYYY') AS transaction_date_str,
              inventory_type.key AS inventory_type_name, to_inventory.name AS to_inventory_name,
	          from_inv_type.key || ': ' ||  from_inventory.name AS from_inventory_name,
              iit.item_count, COUNT(iitd.id) AS total_approve, user_created_by.username as created_by,
              user_updated_by.username as updated_by, iit_out.id AS transfer_id
        FROM inv_inventory_transaction iit
                LEFT JOIN inv_inventory_transaction iit_out ON iit_out.id = iit.transaction_id
                LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
                                                        AND iitd.approved_by > 0
                                                        AND iitd.is_current = TRUE
                LEFT JOIN system_entity inventory_type ON inventory_type.id = iit.inventory_type_id
                LEFT JOIN inv_inventory to_inventory ON to_inventory.id = iit.inventory_id
                LEFT JOIN inv_inventory from_inventory ON from_inventory.id = iit.transaction_entity_id
                LEFT JOIN system_entity from_inv_type ON from_inv_type.id = from_inventory.type_id
                LEFT JOIN app_user user_created_by ON user_created_by.id = iit.created_by
                LEFT JOIN app_user user_updated_by ON user_updated_by.id = iit.updated_by
        WHERE iit.inventory_id IN (${inventoryIds})
                AND iit.transaction_type_id = :transactionTypeId
                AND iit.transaction_entity_type_id = ${transactionEntityInventory.id}
        """
        if (queryType == QUERY_TYPE_TRANSACTION_ID) {
            queryStr = queryStr + " AND iit.transaction_id = ${query}"
        } else {
            queryStr = queryStr + " AND ${queryType} ILIKE :query"
        }

        queryStr = queryStr + """
        GROUP BY iit.id, transfer_date_str, transaction_date_str, inventory_type_name, to_inventory_name,
                from_inventory_name, iit.item_count,  user_created_by.username, user_updated_by.username, transfer_id
        ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                transactionTypeId: transactionTypeIn.id,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]
        // query for count
        String queryCount = """
        SELECT COUNT(iit.id) count
        FROM inv_inventory_transaction iit
                LEFT JOIN inv_inventory to_inventory ON to_inventory.id = iit.inventory_id
                LEFT JOIN inv_inventory from_inventory ON from_inventory.id = iit.transaction_entity_id
        WHERE iit.inventory_id IN (${inventoryIds}) AND
              iit.transaction_type_id = ${transactionTypeIn.id}
              AND iit.transaction_entity_type_id = ${transactionEntityInventory.id}
        """
        if (queryType == QUERY_TYPE_TRANSACTION_ID) {
            queryCount = queryCount + "AND iit.transaction_id = ${query}"
        } else {
            queryCount = queryCount + "AND ${queryType} ILIKE :query"
        }

        List<GroovyRowResult> result
        List<GroovyRowResult> countResult
        if (queryType == QUERY_TYPE_TRANSACTION_ID) {
            result = executeSelectSql(queryStr, queryParams)
            countResult = executeSelectSql(queryCount)
        } else {
            result = executeSelectSql(queryStr, queryParams)
            countResult = executeSelectSql(queryCount, queryParams)
        }

        int total = countResult[0].count
        return [inventoryInList: result, count: total]
    }
}