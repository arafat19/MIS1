package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for inventory transaction(Inventory In From Supplier) CRUD and list of inventory transaction for grid
 *  For details go through Use-Case doc named 'ShowForInventoryInFromSupplierActionService'
 */
class ShowForInventoryInFromSupplierActionService extends BaseService implements ActionIntf {

    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load Inventory In From Supplier page"
    private static final String LST_INVENTORY_IN = "lstInventoryIn"
    private static final String GRID_OBJ = "gridObj"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get inventory type and supplier list for dropDown and inventory transaction(Inventory In From Inventory) list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid

            List<GroovyRowResult> lstInventoryIn = []
            Map serviceReturn
            int total = 0
            // get ids of inventory associated with user
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            // get list and count of inventory transaction (Inventory In From Supplier)
            if (inventoryIds.size() > 0) {
                serviceReturn = listForInventoryInFromSupplier(inventoryIds)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Wrap inventory transaction list (Inventory In From Supplier) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryIn = (List) executeResult.get(LST_INVENTORY_IN)
            // wrap inventory transaction list (Inventory In From Supplier) in grid entity
            List lstWrappedInventoryIns = wrapInventoryInList(lstInventoryIn, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedInventoryIns]
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
     * Wrap list of inventory transactions (Inventory In From Supplier) in grid entity
     * @param lstInventoryIn -list of inventory transaction object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions (Inventory In From Supplier)
     */
    private wrapInventoryInList(List<GroovyRowResult> lstInventoryIn, int start) {
        List lstWrappedInventoryIn = [] as List
        int counter = start + 1
        GroovyRowResult invInventoryIn
        GridEntity invTransaction
        for (int i = 0; i < lstInventoryIn.size(); i++) {
            invInventoryIn = lstInventoryIn[i]
            invTransaction = new GridEntity()
            invTransaction.id = invInventoryIn.id
            invTransaction.cell = [counter,
                    invInventoryIn.id,
                    invInventoryIn.inventory_type + Tools.COLON + invInventoryIn.inventory_name,
                    invInventoryIn.supplier_name,
                    invInventoryIn.purchase_order_id,
                    invInventoryIn.item_count,
                    invInventoryIn.total_approve,
                    invInventoryIn.created_by,
                    invInventoryIn.updated_by ? invInventoryIn.updated_by : Tools.EMPTY_SPACE
            ]
            lstWrappedInventoryIn << invTransaction
            counter++
        }
        return lstWrappedInventoryIn
    }

    /**
     * Get list and count of inventory transactions (Inventory In From Supplier)
     * @param inventoryIdList -list of inventory ids
     * @return -a map containing list and count of inventory transactions (Inventory In From Supplier)
     */
    private Map listForInventoryInFromSupplier(List<Long> inventoryIdList) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        // get comma separated string of ids from list of ids
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(inventoryIdList)
        // query for list
        String queryStr = """
            SELECT iit.id, to_char(iit.transaction_date, 'dd-Mon-YYYY') AS transaction_date_str,
                   se.key AS inventory_type, inventory.name AS inventory_name,
                   supplier.name AS supplier_name, iit.transaction_id AS purchase_order_id,
                   iit.item_count, COUNT(iitd.id) AS total_approve,
                   user_created_by.username as created_by,user_updated_by.username as updated_by
            FROM inv_inventory_transaction iit
            LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
                                                            AND iitd.approved_by > 0
                                                            AND iitd.is_current = TRUE
            LEFT JOIN system_entity se ON se.id = iit.inventory_type_id
            LEFT JOIN inv_inventory inventory ON inventory.id = iit.inventory_id
            LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
            LEFT JOIN app_user user_created_by ON user_created_by.id = iit.created_by
            LEFT JOIN app_user user_updated_by ON user_updated_by.id = iit.updated_by
            WHERE iit.inventory_id IN (${inventoryIds})
                        AND iit.transaction_type_id=:transactionTypeId
                        AND iit.transaction_entity_type_id=:transactionEntityTypeId
            GROUP BY  iit.id,transaction_date_str, inventory_type, inventory_name, supplier_name,purchase_order_id,
                      iit.item_count, user_created_by.username,user_updated_by.username
            ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage  OFFSET :start
        """
        // query for cont
        String queryCount = """
        SELECT COUNT(iit.id) count
        FROM inv_inventory_transaction iit
        WHERE iit.inventory_id IN (${inventoryIds})
            AND iit.transaction_type_id=:transactionTypeId
            AND iit.transaction_entity_type_id=:transactionEntityTypeId
        """

        Map queryParams = [
                transactionTypeId: transactionTypeIn.id,
                transactionEntityTypeId: transactionEntitySupplier.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)
        int total = countResult[0].count
        return [inventoryInList: result, count: total]
    }
}

