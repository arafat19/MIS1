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
 *  Get specific list of approved inventory out transaction details for grid through search
 *  For details go through Use-Case doc named 'SearchForApprovedInventoryOutDetailsActionService'
 */
class SearchForApprovedInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load inventory out details page"
    private static final String LST_INVENTORY_OUT_DETAILS = "lstInventoryOutDetails"
    private static final String INV_DETAILS_ID = "iitd.id"
    private static final String ACTUAL_QUANTITY = "iitd.actual_quantity"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"

    /**
     * Do nothing fro pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get approved inventory transaction out details list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)    // initialize parameters for flexGrid
            List<GroovyRowResult> lstInventoryOutDetails = []
            int total = 0
            // get list and count of approved inventory transaction out details
            LinkedHashMap serviceReturn
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryOutId.toString())
            if (inventoryTransactionId > 0) {
                if (queryType == INV_DETAILS_ID || queryType == ACTUAL_QUANTITY) {
                    try {
                        serviceReturn = searchForApprovedInvOutDetailsByInvTransIdAndNumber(inventoryTransactionId)
                        lstInventoryOutDetails = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                        total = (int) serviceReturn.count
                    } catch (Exception ex) {
                        result.put(Tools.MESSAGE, INVALID_INPUT)
                        return result
                    }
                } else {
                    serviceReturn = searchForApprovedInvOutDetailsByInvTransId(inventoryTransactionId)
                    lstInventoryOutDetails = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                    total = (int) serviceReturn.count
                }
            }

            result.put(LST_INVENTORY_OUT_DETAILS, lstInventoryOutDetails)
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
     * Wrap inventory transaction details list (Inventory Out) for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryOutDetails = (List) executeResult.get(LST_INVENTORY_OUT_DETAILS)
            // wrap inventory transaction details list (Inventory Out) in grid entity
            List lstWrappedInvOutDetails = wrapInventoryOutDetailsList(lstInventoryOutDetails, start)
            result = [page: pageNumber, total: count, rows: lstWrappedInvOutDetails]
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
     * Wrap list of inventory transactions details(Inventory Out) in grid entity
     * @param lstInventoryOutDetails -list of inventory transaction details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions details(Inventory Out)
     */
    private List wrapInventoryOutDetailsList(List<GroovyRowResult> lstInventoryOutDetails, int start) {
        List lstWrappedInventoryOutDetails = [] as List
        int counter = start + 1
        GroovyRowResult invTransactionDetails
        GridEntity obj
        for (int i = 0; i < lstInventoryOutDetails.size(); i++) {
            invTransactionDetails = lstInventoryOutDetails[i]
            obj = new GridEntity()
            obj.id = invTransactionDetails.id
            obj.cell = [
                    counter,
                    invTransactionDetails.id,
                    invTransactionDetails.item_name,
                    invTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.rate,
                    invTransactionDetails.transaction_date,
                    invTransactionDetails.mrf,
                    invTransactionDetails.vehicle,
                    invTransactionDetails.vehicle_number,
                    invTransactionDetails.created_by,
                    invTransactionDetails.approved_on,
                    invTransactionDetails.approved_by
            ]
            lstWrappedInventoryOutDetails << obj
            counter++
        }
        return lstWrappedInventoryOutDetails
    }

    /**
     * Get list and count of approved inventory transactions details(Inventory Out) by details id/actual quantity
     * @param inventoryTransactionId -id of inventory transaction object(parent object)
     * @return -a map containing list and count of approved inventory transactions details(Inventory Out)
     */
    private LinkedHashMap searchForApprovedInvOutDetailsByInvTransIdAndNumber(long inventoryTransactionId) {
        // query for list
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name,iitd.rate, item.unit,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity,to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
                        user_created_by.username as created_by, user_approved_by.username AS approved_by,
                        iitd.is_increase,iitd.transaction_type_id,iitd.mrf_no as mrf,v.name as vehicle,iitd.vehicle_number
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN vehicle v ON v.id = iitd.vehicle_id
                        LEFT JOIN app_user user_approved_by ON user_approved_by.id = iitd.approved_by
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND is_current = true
                          AND ${queryType} = ${query}
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage OFFSET :start
        """
        // query for count
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item item ON item.id=iitd.item_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND is_current = true
                          AND ${queryType} = ${query}
        """

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].count
        return [inventoryOutDetailsList: result, count: count]
    }

    /**
     * Get list and count of approved inventory transactions details(Inventory Out) by specific search keyword
     * @param inventoryTransactionId -id of inventory transaction object(parent object)
     * @return -a map containing list and count of approved inventory transactions details(Inventory Out)
     */
    private LinkedHashMap searchForApprovedInvOutDetailsByInvTransId(long inventoryTransactionId) {
        // query for list
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name,iitd.rate, item.unit,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity,to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
                        user_created_by.username as created_by, user_approved_by.username AS approved_by,
                        iitd.is_increase,iitd.transaction_type_id,iitd.mrf_no as mrf,v.name as vehicle,iitd.vehicle_number
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN vehicle v ON v.id = iitd.vehicle_id
                        LEFT JOIN app_user user_approved_by ON user_approved_by.id = iitd.approved_by
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND is_current = true
                          AND ${queryType} ILIKE :query
                        ORDER BY iitd.transaction_date DESC LIMIT :resultPerPage  OFFSET :start
        """
        // query for count
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item item ON item.id=iitd.item_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by > 0
                          AND is_current = true
                          AND ${queryType} ILIKE :query
        """

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].count
        return [inventoryOutDetailsList: result, count: count]
    }
}
