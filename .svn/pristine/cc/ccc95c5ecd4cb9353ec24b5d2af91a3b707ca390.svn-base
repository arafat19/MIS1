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
 *  Show unapproved list of inventory transaction(Inventory In From Inventory) for dashBoard
 *  For details go through Use-Case doc named 'ListUnApprovedInFromInventoryActionService'
 */
class ListUnApprovedInFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to populate un-approved inventory in from inventory list"
    private static final String LST_UNAPPROVED_INVENTORY_IN = "unApprovedList"
    private static final String GRID_OBJ = "gridObj"
    private static final String ID = "iit.id"

    /**
     * do nothing fro pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get unapproved inventory transaction list (Inventory In From Inventory) for dashBoard
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            Map serviceReturn = new LinkedHashMap()
            List lstUnApprovedInventoryIn = []
            int count = 0
            // check required parameters
            parameterMap.sortname = ID
            if (!parameterMap.rp) {
                parameterMap.rp = 10
                parameterMap.page = 1
            }
            initPager(parameterMap) // initialize parameters for flexGrid
            // get ids of inventory associated with user
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            // get list and count of unapproved inventory transaction (Inventory In From Inventory)
            if (inventoryIds.size() > 0) {
                serviceReturn = listUnApprovedInFromInventory(inventoryIds)
                lstUnApprovedInventoryIn = serviceReturn.unApprovedList
                count = serviceReturn.count
            }

            result.put(LST_UNAPPROVED_INVENTORY_IN, lstUnApprovedInventoryIn)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap unapproved inventory transaction list (Inventory In From Inventory) for dashBoard
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for dashBoard
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj   // cast map returned from execute method

            List<GroovyRowResult> lstUnApprovedInventoryIn = (List<GroovyRowResult>) executeResult.get(LST_UNAPPROVED_INVENTORY_IN)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedInventoryIn = wrapInvInFormInventoryList(lstUnApprovedInventoryIn, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedInventoryIn]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap unapproved list of inventory transactions (Inventory In From Inventory) in grid entity
     * @param unApprovedList -list of unapproved inventory transaction object(s)
     * @param start -starting index of the page
     * @return -list of wrapped unapproved inventory transactions (Inventory In From Inventory)
     */
    private List wrapInvInFormInventoryList(List<GroovyRowResult> unApprovedList, int start) {
        List lstUnapprovedInvIn = []
        int counter = start + 1
        for (int i = 0; i < unApprovedList.size(); i++) {
            GroovyRowResult eachRow = unApprovedList[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            String inventoryName = eachRow.inventory_type + Tools.COLON + eachRow.inventory_name
            obj.cell = [
                    counter,
                    eachRow.id,
                    inventoryName,
                    eachRow.item_count,
                    eachRow.total_pending
            ]
            counter++
            lstUnapprovedInvIn << obj
        }
        return lstUnapprovedInvIn
    }

    /**
     * Get list and count of unapproved inventory transactions (Inventory In From Inventory)
     * @param lstInventoryIds -list of inventory ids
     * @return -a map containing list and count of unapproved inventory transactions (Inventory In From Inventory)
     */
    private LinkedHashMap listUnApprovedInFromInventory(List<Long> lstInventoryIds) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        // get comma separated string of ids from list of ids
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryIds)
        // query for list
        String queryStr = """
       SELECT iit.id, se.key AS inventory_type, inventory.name AS inventory_name,
          iit.item_count, COALESCE(COUNT(iitd.id), 0) AS total_pending
       FROM inv_inventory_transaction iit
          LEFT JOIN inv_inventory inventory ON inventory.id = iit.inventory_id
          LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
          LEFT JOIN system_entity se ON se.id = inventory.type_id
       WHERE iit.inventory_id IN (${inventoryIds}) AND
         iit.transaction_type_id=:transactionTypeIdIn AND
         iit.transaction_entity_type_id=:transactionEntityTypeId AND
         iitd.approved_by = 0
       GROUP BY  iit.id, inventory_type, inventory_name, iit.item_count
       ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
       """
       // query for count
        String queryCount = """
       SELECT COALESCE(COUNT(DISTINCT(iit.id)),0) AS total FROM inv_inventory_transaction iit
             LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
       WHERE iit.inventory_id IN (${inventoryIds}) AND
             iit.transaction_type_id=:transactionTypeIdIn AND
             iit.transaction_entity_type_id=:transactionEntityTypeId AND
             iitd.approved_by = 0
       """

        Map queryParams = [
                transactionTypeIdIn: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntityInventory.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].total
        return [unApprovedList: result, count: count]
    }
}



