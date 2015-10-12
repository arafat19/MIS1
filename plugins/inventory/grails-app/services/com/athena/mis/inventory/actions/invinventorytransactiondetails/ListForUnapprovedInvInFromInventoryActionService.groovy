package com.athena.mis.inventory.actions.invinventorytransactiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Show unapproved list of inventory transaction details(Inventory In From Inventory) for grid
 *  For details go through Use-Case doc named 'ListForUnapprovedInvInFromInventoryActionService'
 */
class ListForUnapprovedInvInFromInventoryActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load inventory-in details"
    private static final String LST_INVENTORY_IN_DETAILS = "lstInventoryInDetails"

    /**
     * Do nothing fro pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get inventory transaction details list (Inventory In From Inventory) for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid

            List<GroovyRowResult> lstInventoryInDetails = []
            int total = 0
            // get list and count of unapproved inventory transaction details(Inventory In From Inventory)
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryTransactionId.toString())
            if (inventoryTransactionId > 0) {
                LinkedHashMap serviceReturn = listForUnapprovedInvInDetailsByInvTransId(inventoryTransactionId)
                lstInventoryInDetails = (List<GroovyRowResult>) serviceReturn.invTransDetailsList;
                total = (int) serviceReturn.count;
            }

            result.put(LST_INVENTORY_IN_DETAILS, lstInventoryInDetails)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventory transaction details list (Inventory In From Inventory) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryInDetails = (List) executeResult.get(LST_INVENTORY_IN_DETAILS)
            // wrap inventory transaction details list (Inventory In From Inventory) in grid entity
            List lstWrappedInventoryInDetails = wrapInventoryInDetailsList(lstInventoryInDetails, start)
            result = [page: pageNumber, total: count, rows: lstWrappedInventoryInDetails]
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
     * Wrap list of inventory transactions details(Inventory In From Inventory) in grid entity
     * @param lstInventoryInDetails -list of inventory transaction details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions details(Inventory In From Inventory)
     */
    private List wrapInventoryInDetailsList(List<GroovyRowResult> lstInventoryInDetails, int start) {
        List lstWrappedInventoryInDetails = []
        int counter = start + 1
        GroovyRowResult invTransactionDetails
        GridEntity obj
        for (int i = 0; i < lstInventoryInDetails.size(); i++) {
            invTransactionDetails = lstInventoryInDetails[i]
            obj = new GridEntity()
            obj.id = invTransactionDetails.id;
            obj.cell = [counter,
                    invTransactionDetails.id,
                    invTransactionDetails.item_name,
                    invTransactionDetails.supplied_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.shrinkage + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.created_by,
                    invTransactionDetails.updated_by ? invTransactionDetails.updated_by : Tools.EMPTY_SPACE
            ]
            lstWrappedInventoryInDetails << obj
            counter++
        }
        return lstWrappedInventoryInDetails;
    }

    private static final String COUNT_QUERY = """
        SELECT count(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        WHERE inventory_transaction_id= :inventoryTransactionId
          AND iitd.approved_by <= 0
          AND iitd.is_current = true
    """

    /**
     * Get list and count of unapproved inventory transactions details(Inventory In From Inventory)
     * @param inventoryTransactionId -id of inventory transaction object(parent object)
     * @return -a map containing list and count of unapproved inventory transactions details(Inventory In From Inventory)
     */
    private Map listForUnapprovedInvInDetailsByInvTransId(long inventoryTransactionId) {
        // query for list
        String queryStr = """
        SELECT iitd.id, item.name AS item_name, item.unit, iitd.rate,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
        to_char(iitd.supplied_quantity,'${Tools.DB_QUANTITY_FORMAT}') supplied_quantity,
        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity,
        to_char(iitd.shrinkage,'${Tools.DB_QUANTITY_FORMAT}') shrinkage, iitd.supplier_chalan,
        user_created_by.username as created_by,user_updated_by.username as updated_by,
        vehicle.name AS vehicle_name
        FROM inv_inventory_transaction_details iitd
        LEFT JOIN item ON item.id = iitd.item_id
        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
        LEFT JOIN app_user user_updated_by ON user_updated_by.id = iitd.updated_by
        LEFT JOIN vehicle ON vehicle.id = iitd.vehicle_id
        WHERE iitd.inventory_transaction_id = :inventoryTransactionId
        AND iitd.approved_by <= 0
        AND iitd.is_current = true
        ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)
        int total = countResult[0].count
        return [invTransDetailsList: result, count: total]
    }
}