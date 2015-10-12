package com.athena.mis.inventory.actions.report.inventorytransactionlist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search and show list for grid view of inventory transaction(IN, OUT, CONSUMPTION, PRODUCTION)
 *  For details go through Use-Case doc named 'SearchForInventoryTransactionListActionService'
 */
class SearchForInventoryTransactionListActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String SERVER_ERROR_MESSAGE = "Fail to load inventory transaction list"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to get inventory transaction list"
    private static final String INVALID_INPUT_MESSAGE = "Error occurred due to invalid input"
    private static final String NO_INVENTORY_FOUND_MSG = "User is not associated with any inventory"
    private static final String NOT_FOUND_MESSAGE = "Inventory transaction not found within given date"
    private static final String LST_INVENTORY_TRANSACTION = "lstInventoryTransaction"
    private static final String INVENTORY_OBJ = "invInventory"
    private static final String DEFAULT_SORT_NAME = "item.name, iitd.approved_on"
    private static final String SORT_ORDER_ASCENDING = "ASC"
    private static final String QUERY_TYPE_CHALAN_NO = "chalanNo"
    private static final String INCREASE = " (increase)"
    private static final String DECREASE = " (decrease)"

    /**
     * Check and get required parameters from UI
     * Get inventory object from cache utility by inventoryId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameters
            if (!parameterMap.startDate || !parameterMap.endDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }
            // assign default value '-1' for empty string that are used to indicate 'ALL' inventories
            long inventoryId = parameterMap.inventoryId.equals(Tools.EMPTY_SPACE) ? -1 : Long.parseLong(parameterMap.inventoryId.toString())
            // get inventory object from cache utility by inventoryId
            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

            result.put(INVENTORY_OBJ, invInventory)
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
     * Get specific inventory id if exists or get all inventory ids by type mapped with user
     * Get list and count of inventory transaction
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            GrailsParameterMap params = (GrailsParameterMap) parameters

            InvInventory invInventory = (InvInventory) preResult.get(INVENTORY_OBJ)
            Date startDate = DateUtility.parseMaskedDate(params.startDate)
            Date endDate = DateUtility.parseMaskedDate(params.endDate)

            generatePagination(params)  // initialize parameters for flexGrid
            // assign default value '-1' for empty string that are used to indicate 'ALL' projects/inventory types/ transTypes
            long projectId = params.projectId.equals(Tools.EMPTY_SPACE) ? -1 : Long.parseLong(params.projectId.toString())
            long transactionTypeId = params.transactionTypeId.equals(Tools.EMPTY_SPACE) ? -1 : Long.parseLong(params.transactionTypeId.toString())
            long inventoryTypeId = params.inventoryTypeId.equals(Tools.EMPTY_SPACE) ? -1 : Long.parseLong(params.inventoryTypeId.toString())
            long itemTypeId = params.itemTypeId.equals(Tools.EMPTY_SPACE) ? -1 : Long.parseLong(params.itemTypeId.toString())
            List<Long> lstItemTypeIds = []
            if (itemTypeId < 0) {
                lstItemTypeIds = itemTypeCacheUtility.getAllItemTypeIds()
            }else{
                lstItemTypeIds << itemTypeId
            }
            try {
                if (query && (query != Tools.EMPTY_SPACE)) {
                    if (queryType == QUERY_TYPE_CHALAN_NO) {
                        long chalanNo = Long.parseLong(query.toString())// check input validation for search by chalanNo
                    }
                }
            } catch (Exception ex) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MESSAGE)
                return result
            }

            // get specific inventory id if exists or get all inventory ids by type mapped with user
            List<Long> lstInventoryIds = getUserInventoryIdList(projectId, inventoryTypeId, invInventory)
            if (lstInventoryIds.size() <= 0) {
                result.put(Tools.MESSAGE, NO_INVENTORY_FOUND_MSG)
                return result
            }
            // get list and count of inventory transaction
            Map serviceReturn
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            if (query && (query != Tools.EMPTY_SPACE)) {
                if (transactionTypeId > 0) {
                    serviceReturn = searchSpecificInventoryTransactionList(lstInventoryIds, transactionTypeId, startDate, endDate, companyId, lstItemTypeIds)
                } else {
                    serviceReturn = searchAllInventoryTransactionList(lstInventoryIds, startDate, endDate, companyId, lstItemTypeIds)
                }
            } else {
                if (transactionTypeId > 0) {
                    serviceReturn = getSpecificInventoryTransactionList(lstInventoryIds, transactionTypeId, startDate, endDate, companyId, lstItemTypeIds)
                } else {
                    serviceReturn = getAllInventoryTransactionList(lstInventoryIds, startDate, endDate, companyId, lstItemTypeIds)
                }
            }
            List<GroovyRowResult> inventoryTransactionList = (List<GroovyRowResult>) serviceReturn.inventoryTransactionList
            if (inventoryTransactionList.size() <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            result.put(LST_INVENTORY_TRANSACTION, inventoryTransactionList)
            result.put(Tools.COUNT, serviceReturn.count)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventory transaction list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<GroovyRowResult> lstInventoryTransaction = (List<GroovyRowResult>) executeResult.get(LST_INVENTORY_TRANSACTION)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            // wrap inventory transaction list
            List lstWrappedInventoryTransaction = (List) wrapAllInventoryTransactionList(lstInventoryTransaction, start)
            result = [page: pageNumber, total: count, rows: lstWrappedInventoryTransaction]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: []]
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
            result = [page: pageNumber, total: 0, rows: []]
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                String msg = previousResult.get(Tools.MESSAGE)
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: []]
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of inventory transaction in grid entity
     * @param lstInventoryTransaction -list of inventory transaction
     * @param start -starting index of the page
     * @return -list of wrapped inventory transaction
     */
    private List wrapAllInventoryTransactionList(List<GroovyRowResult> lstInventoryTransaction, int start) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_ADJUSTMENT, companyId)
        SystemEntity transactionTypeReAdj = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_REVERSE_ADJUSTMENT, companyId)

        List lstWrappedInventoryTransaction = []
        int counter = start + 1
        GroovyRowResult invInventoryTransaction
        GridEntity obj
        for (int i = 0; i < lstInventoryTransaction.size(); i++) {
            invInventoryTransaction = lstInventoryTransaction[i]
            obj = new GridEntity()
            obj.id = invInventoryTransaction.id
            obj.cell = [counter,
                    invInventoryTransaction.id,
                    invInventoryTransaction.transaction_date_str,
                    invInventoryTransaction.transaction_entity_name,
                    invInventoryTransaction.item_name,
                    invInventoryTransaction.quantity + Tools.SINGLE_SPACE + invInventoryTransaction.unit,
                    invInventoryTransaction.rate,
                    invInventoryTransaction.total,
                    (invInventoryTransaction.transaction_type_id == transactionTypeAdj.id ||
                            invInventoryTransaction.transaction_type_id == transactionTypeReAdj.id) ?
                            ((invInventoryTransaction.is_increase) ?
                                    invInventoryTransaction.transaction_type_name + INCREASE
                                    : invInventoryTransaction.transaction_type_name + DECREASE)
                            : invInventoryTransaction.transaction_type_name,
                    invInventoryTransaction.inventory_name
            ]
            lstWrappedInventoryTransaction << obj
            counter++
        }
        return lstWrappedInventoryTransaction
    }

    /**
     * Check and get required parameters
     * Initialize parameters for flexGrid
     * @param params -serialized parameters from UI
     */
    private void generatePagination(GrailsParameterMap params) {
        if (!params.page || !params.rp) {
            params.page = 1
            params.rp = 20
            params.currentCount = 0
            params.sortname = DEFAULT_SORT_NAME
            params.sortorder = SORT_ORDER_ASCENDING
        }
        initSearch(params)  // initialize parameters for flexGrid
    }

    /**
     * Search inventory transaction list by specific transaction type id
     * @param userInventoryList -list of inventory ids
     * @param transactionTypeId -id of inventory transaction type(IN, OUT, CONSUMPTION etc.)
     * @param startDate -starting date
     * @param endDate -end date
     * @return -a map containing list and count of inventory transaction
     */
    public Map searchSpecificInventoryTransactionList(List<Long> userInventoryList, long transactionTypeId, Date startDate, Date endDate, long companyId, List<Long> itemTypeIds) {
        String lstUserInventories = Tools.buildCommaSeparatedStringOfIds(userInventoryList)
        String lstItemTypeIds = Tools.buildCommaSeparatedStringOfIds(itemTypeIds)

        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        SystemEntity transactionEntityCustomer = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_CUSTOMER, companyId)

        String queryStr = Tools.EMPTY_SPACE
        String queryCount = Tools.EMPTY_SPACE

        if (queryType == 'chalanNo') {
            queryStr = """
            SELECT iitd.id,to_char(iitd.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,iitd.transaction_type_id,iitd.is_increase,
            CASE
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeInventoryId)
                THEN  transaction_entity_type.key ||  ': '|| transaction_entity.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeSupplierId)
                THEN  transaction_entity_type.key ||  ': '|| supplier.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeCustomerId)
                THEN  transaction_entity_type.key ||  ': '|| customer.full_name
                ELSE ''
            END AS transaction_entity_name,

            item.name AS item_name,
            to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit AS unit,
            to_char(ROUND(iitd.rate,4),'${
                Tools.DB_CURRENCY_FORMAT
            }') AS rate, to_char(ROUND((iitd.rate*iitd.actual_quantity),4),'${Tools.DB_CURRENCY_FORMAT}') AS total,
            transaction_type.key AS transaction_type_name,
            (inventory_type.key || ': ' || inventory.name) AS inventory_name
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            LEFT JOIN inv_inventory transaction_entity ON iit.transaction_entity_id = transaction_entity.id
            LEFT JOIN supplier ON iit.transaction_entity_id = supplier.id
            LEFT JOIN customer ON iit.transaction_entity_id = customer.id
            LEFT JOIN system_entity transaction_entity_type ON iit.transaction_entity_type_id = transaction_entity_type.id
            LEFT JOIN system_entity transaction_type ON iitd.transaction_type_id = transaction_type.id
            LEFT JOIN inv_inventory inventory ON iit.inventory_id = inventory.id
            LEFT JOIN system_entity inventory_type ON inventory_type.id = inventory.type_id
            WHERE iitd.id = ${query}
              AND iitd.transaction_type_id =:transactionTypeId
              AND iitd.inventory_id IN(${lstUserInventories})
              AND iitd.approved_by > 0
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
            """

            queryCount = """
            SELECT count(iitd.id)
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.id = ${query}
              AND iitd.transaction_type_id =:transactionTypeId
              AND iitd.inventory_id IN(${lstUserInventories})
              AND iitd.approved_by > 0
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            """
        }

        if (queryType == 'item_name') {
            queryStr = """
            SELECT iitd.id,to_char(iitd.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,iitd.transaction_type_id,iitd.is_increase,
            CASE
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeInventoryId)
                THEN  transaction_entity_type.key ||  ': '|| transaction_entity.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeSupplierId)
                THEN  transaction_entity_type.key ||  ': '|| supplier.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeCustomerId)
                THEN  transaction_entity_type.key ||  ': '|| customer.full_name
                ELSE ''
            END AS transaction_entity_name,

            item.name AS item_name,
            to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit AS unit,
            to_char(ROUND(iitd.rate,4),'${
                Tools.DB_CURRENCY_FORMAT
            }') AS rate, to_char(ROUND((iitd.rate*iitd.actual_quantity),4),'${Tools.DB_CURRENCY_FORMAT}') AS total,
            transaction_type.key AS transaction_type_name,
            (inventory_type.key || ': ' || inventory.name) AS inventory_name
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            LEFT JOIN inv_inventory transaction_entity ON iit.transaction_entity_id = transaction_entity.id
            LEFT JOIN  supplier ON iit.transaction_entity_id = supplier.id
            LEFT JOIN customer ON iit.transaction_entity_id = customer.id
            LEFT JOIN system_entity transaction_entity_type ON iit.transaction_entity_type_id = transaction_entity_type.id
            LEFT JOIN system_entity transaction_type ON iitd.transaction_type_id = transaction_type.id
            LEFT JOIN inv_inventory inventory ON iit.inventory_id = inventory.id
            LEFT JOIN system_entity inventory_type ON inventory_type.id = inventory.type_id
                WHERE iitd.transaction_type_id =:transactionTypeId
                  AND item.name ILIKE '%${query}%'
                  AND iitd.inventory_id IN(${lstUserInventories})
                  AND iitd.approved_by > 0
                  AND item.item_type_id  IN (${lstItemTypeIds})
                  AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
            """

            queryCount = """
                SELECT count(iitd.id)
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN item item ON item.id = iitd.item_id
                WHERE iitd.transaction_type_id =:transactionTypeId
                  AND item.name ILIKE '%${query}%'
                  AND iitd.inventory_id IN(${lstUserInventories})
                  AND iitd.approved_by > 0
                  AND item.item_type_id  IN (${lstItemTypeIds})
                  AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            """
        }

        Map queryParams = [
                transactionTypeId: transactionTypeId,
                transactionEntityTypeInventoryId: transactionEntityInventory.id,
                transactionEntityTypeSupplierId: transactionEntitySupplier.id,
                transactionEntityTypeCustomerId: transactionEntityCustomer.id,
                startDate: DateUtility.getSqlFromDateWithSeconds(startDate),
                endDate: DateUtility.getSqlToDateWithSeconds(endDate),
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> inventoryTransactionList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [inventoryTransactionList: inventoryTransactionList, count: count]
    }

    /**
     * Search all inventory transaction list
     * @param userInventoryList -list of inventory ids
     * @param startDate -starting date
     * @param endDate -end date
     * @param companyId -id of company
     * @return -a map containing list and count of inventory transaction
     */
    private Map searchAllInventoryTransactionList(List<Long> userInventoryList, Date startDate, Date endDate, long companyId, List<Long> itemTypeIds) {
        String lstUserInventories = Tools.buildCommaSeparatedStringOfIds(userInventoryList)
        String lstItemTypeIds = Tools.buildCommaSeparatedStringOfIds(itemTypeIds)

        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        SystemEntity transactionEntityCustomer = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_CUSTOMER, companyId)

        String queryStr = Tools.EMPTY_SPACE
        String queryCount = Tools.EMPTY_SPACE

        if (queryType == 'chalanNo') {
            queryStr = """
            SELECT iitd.id,to_char(iitd.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,iitd.transaction_type_id,iitd.is_increase,
            CASE
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeInventoryId)
                THEN  transaction_entity_type.key ||  ': '|| transaction_entity.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeSupplierId)
                THEN  transaction_entity_type.key ||  ': '|| supplier.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeCustomerId)
                THEN  transaction_entity_type.key ||  ': '|| customer.full_name
                ELSE ''
            END AS transaction_entity_name,

            item.name AS item_name,
            to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit AS unit,
            to_char(ROUND(iitd.rate,4),'${
                Tools.DB_CURRENCY_FORMAT
            }') AS rate, to_char(ROUND((iitd.rate*iitd.actual_quantity),4),'${Tools.DB_CURRENCY_FORMAT}') AS total,
            transaction_type.key AS transaction_type_name,
            (inventory_type.key || ': ' || inventory.name) AS inventory_name
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            LEFT JOIN inv_inventory transaction_entity ON iit.transaction_entity_id = transaction_entity.id
            LEFT JOIN  supplier ON iit.transaction_entity_id = supplier.id
            LEFT JOIN customer ON iit.transaction_entity_id = customer.id
            LEFT JOIN system_entity transaction_entity_type ON iit.transaction_entity_type_id = transaction_entity_type.id
            LEFT JOIN system_entity transaction_type ON iitd.transaction_type_id = transaction_type.id
            LEFT JOIN inv_inventory inventory ON iit.inventory_id = inventory.id
            LEFT JOIN system_entity inventory_type ON inventory_type.id = inventory.type_id
            WHERE iitd.id = ${query}
              AND iitd.approved_by > 0
              AND iitd.inventory_id IN(${lstUserInventories})
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            """

            queryCount = """
            SELECT count(iitd.id)
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.id = ${query}
              AND iitd.approved_by > 0
              AND iitd.inventory_id IN(${lstUserInventories})
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            """
        }

        if (queryType == 'item_name') {
            queryStr = """
            SELECT iitd.id,to_char(iitd.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,iitd.transaction_type_id,iitd.is_increase,
            CASE
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeInventoryId)
                THEN  transaction_entity_type.key ||  ': '|| transaction_entity.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeSupplierId)
                THEN  transaction_entity_type.key ||  ': '|| supplier.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeCustomerId)
                THEN  transaction_entity_type.key ||  ': '|| customer.full_name
                ELSE ''
            END AS transaction_entity_name,

            item.name AS item_name,
            to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit AS unit,
            to_char(ROUND(iitd.rate,4),'${
                Tools.DB_CURRENCY_FORMAT
            }') AS rate, to_char(ROUND((iitd.rate*iitd.actual_quantity),4),'${Tools.DB_CURRENCY_FORMAT}') AS total,
            transaction_type.key AS transaction_type_name,
            (inventory_type.key || ': ' || inventory.name) AS inventory_name
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            LEFT JOIN inv_inventory transaction_entity ON iit.transaction_entity_id = transaction_entity.id
            LEFT JOIN  supplier ON iit.transaction_entity_id = supplier.id
            LEFT JOIN customer ON iit.transaction_entity_id = customer.id
            LEFT JOIN system_entity transaction_entity_type ON iit.transaction_entity_type_id = transaction_entity_type.id
            LEFT JOIN system_entity transaction_type ON iitd.transaction_type_id = transaction_type.id
            LEFT JOIN inv_inventory inventory ON iit.inventory_id = inventory.id
            LEFT JOIN system_entity inventory_type ON inventory_type.id = inventory.type_id
                WHERE item.name ILIKE '%${query}%'
                  AND iitd.inventory_id IN(${lstUserInventories})
                  AND item.item_type_id  IN (${lstItemTypeIds})
                  AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
                  AND iitd.approved_by > 0
            """

            queryCount = """
                SELECT count(iitd.id)
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN item item ON item.id = iitd.item_id
                WHERE item.name ILIKE '%${query}%'
                  AND iitd.inventory_id IN(${lstUserInventories})
                  AND item.item_type_id  IN (${lstItemTypeIds})
                  AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
                  AND iitd.approved_by > 0
            """
        }

        queryStr = queryStr + """
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                transactionEntityTypeInventoryId: transactionEntityInventory.id,
                transactionEntityTypeSupplierId: transactionEntitySupplier.id,
                transactionEntityTypeCustomerId: transactionEntityCustomer.id,
                startDate: DateUtility.getSqlFromDateWithSeconds(startDate),
                endDate: DateUtility.getSqlToDateWithSeconds(endDate),
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> inventoryTransactionList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [inventoryTransactionList: inventoryTransactionList, count: count]
    }

    /**
     * Get inventory transaction list by specific transaction type id
     * @param userInventoryList -list of inventory ids
     * @param transactionTypeId -id of inventory transaction type(IN, OUT, CONSUMPTION etc.)
     * @param startDate -starting date
     * @param endDate -end date
     * @param companyId -id of company
     * @return -a map containing list and count of inventory transaction
     */
    private Map getSpecificInventoryTransactionList(List<Long> userInventoryList, long transactionTypeId, Date startDate, Date endDate, long companyId, List<Long> itemTypeIds) {
        String lstUserInventories = Tools.buildCommaSeparatedStringOfIds(userInventoryList)
        String lstItemTypeIds = Tools.buildCommaSeparatedStringOfIds(itemTypeIds)

        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        SystemEntity transactionEntityCustomer = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_CUSTOMER, companyId)

        String queryStr = """
            SELECT iitd.id,to_char(iitd.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,iitd.transaction_type_id,iitd.is_increase,
            CASE
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeInventoryId)
                THEN  transaction_entity_type.key ||  ': '|| transaction_entity.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeSupplierId)
                THEN  transaction_entity_type.key ||  ': '|| supplier.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeCustomerId)
                THEN  transaction_entity_type.key ||  ': '|| customer.full_name
                ELSE ''
            END AS transaction_entity_name,

            item.name AS item_name,
            to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit AS unit,
            to_char(ROUND(iitd.rate,4),'${
            Tools.DB_CURRENCY_FORMAT
        }') AS rate, to_char(ROUND((iitd.rate*iitd.actual_quantity),4),'${Tools.DB_CURRENCY_FORMAT}') AS total,
            transaction_type.key AS transaction_type_name,
            (inventory_type.key || ': ' || inventory.name) AS inventory_name
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            LEFT JOIN inv_inventory transaction_entity ON iit.transaction_entity_id = transaction_entity.id
            LEFT JOIN  supplier ON iit.transaction_entity_id = supplier.id
            LEFT JOIN customer ON iit.transaction_entity_id = customer.id
            LEFT JOIN system_entity transaction_entity_type ON iit.transaction_entity_type_id = transaction_entity_type.id
            LEFT JOIN system_entity transaction_type ON iitd.transaction_type_id = transaction_type.id
            LEFT JOIN inv_inventory inventory ON iit.inventory_id = inventory.id
            LEFT JOIN system_entity inventory_type ON inventory_type.id = inventory.type_id
            WHERE iitd.transaction_type_id =:transactionTypeId
              AND iitd.approved_by > 0
              AND iitd.inventory_id IN(${lstUserInventories})
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
            SELECT count(iitd.id)
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.transaction_type_id =:transactionTypeId
              AND iitd.approved_by > 0
              AND iitd.inventory_id IN(${lstUserInventories})
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
        """

        Map queryParams = [
                transactionTypeId: transactionTypeId,
                transactionEntityTypeInventoryId: transactionEntityInventory.id,
                transactionEntityTypeSupplierId: transactionEntitySupplier.id,
                transactionEntityTypeCustomerId: transactionEntityCustomer.id,
                startDate: DateUtility.getSqlFromDateWithSeconds(startDate),
                endDate: DateUtility.getSqlToDateWithSeconds(endDate),
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> inventoryTransactionList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [inventoryTransactionList: inventoryTransactionList, count: count]
    }

    /**
     * Get all inventory transaction list
     * @param userInventoryList -list of inventory ids
     * @param startDate -starting date
     * @param endDate -end date
     * @param companyId -id of company
     * @return -a map containing list and count of inventory transaction
     */
    private Map getAllInventoryTransactionList(List<Long> userInventoryList, Date startDate, Date endDate, long companyId, List<Long> itemTypeIds) {
        String lstUserInventories = Tools.buildCommaSeparatedStringOfIds(userInventoryList)
        String lstItemTypeIds = Tools.buildCommaSeparatedStringOfIds(itemTypeIds)

        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        SystemEntity transactionEntityCustomer = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_CUSTOMER, companyId)

        String queryStr = """
            SELECT iitd.id,to_char(iitd.transaction_date, 'dd-MON-YYYY') AS transaction_date_str,iitd.transaction_type_id,iitd.is_increase,
            CASE
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeInventoryId)
                THEN  transaction_entity_type.key ||  ': '|| transaction_entity.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeSupplierId)
                THEN  transaction_entity_type.key ||  ': '|| supplier.name
                WHEN (iit.transaction_entity_type_id =:transactionEntityTypeCustomerId)
                THEN  transaction_entity_type.key ||  ': '|| customer.full_name
                ELSE ''
            END AS transaction_entity_name,

            item.name AS item_name,
            to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS quantity, item.unit AS unit,
            to_char(ROUND(iitd.rate,4),'${
            Tools.DB_CURRENCY_FORMAT
        }') AS rate, to_char(ROUND((iitd.rate*iitd.actual_quantity),4),'${Tools.DB_CURRENCY_FORMAT}') AS total,
            transaction_type.key AS transaction_type_name,
            (inventory_type.key || ': ' || inventory.name) AS inventory_name
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN item ON item.id = iitd.item_id
            LEFT JOIN inv_inventory transaction_entity ON iit.transaction_entity_id = transaction_entity.id
            LEFT JOIN  supplier ON iit.transaction_entity_id = supplier.id
            LEFT JOIN customer ON iit.transaction_entity_id = customer.id
            LEFT JOIN system_entity transaction_entity_type ON iit.transaction_entity_type_id = transaction_entity_type.id
            LEFT JOIN system_entity transaction_type ON iitd.transaction_type_id = transaction_type.id
            LEFT JOIN inv_inventory inventory ON iit.inventory_id = inventory.id
            LEFT JOIN system_entity inventory_type ON inventory_type.id = inventory.type_id
            WHERE iitd.approved_by > 0
              AND iitd.inventory_id IN(${lstUserInventories})
              AND item.item_type_id  IN (${lstItemTypeIds})
              AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
            ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
                SELECT count(iitd.id)
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN item ON item.id = iitd.item_id
                WHERE iitd.approved_by > 0
                  AND iitd.inventory_id IN(${lstUserInventories})
                  AND item.item_type_id  IN (${lstItemTypeIds})
                  AND (iitd.transaction_date BETWEEN :startDate AND :endDate)
        """

        Map queryParams = [
                transactionEntityTypeInventoryId: transactionEntityInventory.id,
                transactionEntityTypeSupplierId: transactionEntitySupplier.id,
                transactionEntityTypeCustomerId: transactionEntityCustomer.id,
                startDate: DateUtility.getSqlFromDateWithSeconds(startDate),
                endDate: DateUtility.getSqlToDateWithSeconds(endDate),
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> inventoryTransactionList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [inventoryTransactionList: inventoryTransactionList, count: count]
    }

    /**
     * Get specific inventory id if exists or get all inventory ids by type mapped with user
     * @param projectId -id of project
     * @param inventoryTypeId -id of inventory type(Store/Site)
     * @param invInventory -object of InvInventory
     * @return -a list of inventory ids
     */
    private List<Long> getUserInventoryIdList(long projectId, long inventoryTypeId, InvInventory invInventory) {
        List<Long> lstUserInventoryId = []
        // Specific inventory
        if (invInventory) {
            lstUserInventoryId << invInventory.id
            return lstUserInventoryId
        }

        // Specific Project & All inventories(Side, Store)
        if ((projectId >= 0) && (inventoryTypeId <= 0)) {
            lstUserInventoryId = invSessionUtil.getUserInventoryIdsByProject(projectId)
            return lstUserInventoryId
        }

        // Specific Project & Specific InventoryType
        if ((projectId >= 0) && (inventoryTypeId >= 0)) {
            lstUserInventoryId = invSessionUtil.getUserInventoryIdsByTypeAndProject(inventoryTypeId, projectId)
            return lstUserInventoryId
        }

        // All projects & Specific InventoryType
        if ((projectId <= 0) && (inventoryTypeId >= 0)) {
            lstUserInventoryId = invSessionUtil.getUserInventoryIdsByType(inventoryTypeId)
            return lstUserInventoryId
        }

        // All Projects & All Inventories(Get userInventoryList)
        if ((projectId <= 0) && (inventoryTypeId <= 0)) {
            lstUserInventoryId = invSessionUtil.getUserInventoryIds()
            return lstUserInventoryId
        }
        return lstUserInventoryId
    }
}

