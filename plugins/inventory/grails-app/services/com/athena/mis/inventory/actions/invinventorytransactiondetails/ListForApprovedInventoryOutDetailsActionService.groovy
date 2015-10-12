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
 *  Show approved list of inventory transaction out details for grid
 *  For details go through Use-Case doc named 'ListForApprovedInventoryOutDetailsActionService'
 */
class ListForApprovedInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load inventory out details page"
    private static final String LST_INVENTORY_OUT_DETAILS = "lstInventoryOutDetails"

    /**
     * Do nothing fro pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get inventory transaction out details list for grid
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
            List<GroovyRowResult> lstInventoryOutDetails = []
            int total = 0
            // get list and count of approved inventory transaction out details
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryOutId.toString())
            if (inventoryTransactionId > 0) {
                LinkedHashMap serviceReturn = listForApprovedInvOutDetailsByInvTransId(inventoryTransactionId)
                lstInventoryOutDetails = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                total = (int) serviceReturn.count
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
     * Wrap inventory transaction out details list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryOutDetails = (List) executeResult.get(LST_INVENTORY_OUT_DETAILS)
            List lstWrappedInventoryOutDetails = wrapInventoryOutDetailsList(lstInventoryOutDetails, start)
            result = [page: pageNumber, total: count, rows: lstWrappedInventoryOutDetails]
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
     * @param lstInventoryOutDetails -list of inventory transaction details object(s)
     * @param start -starting index of the page
     * @return -list of wrapped inventory transactions details(Inventory In From Inventory)
     */
    private List wrapInventoryOutDetailsList(List<GroovyRowResult> lstInventoryOutDetails, int start) {
        List lstWrappedInventoryOutDetails = [] as List
        int counter = start + 1
        GroovyRowResult invInventoryTransactionDetails
        GridEntity obj
        for (int i = 0; i < lstInventoryOutDetails.size(); i++) {
            invInventoryTransactionDetails = lstInventoryOutDetails[i]
            obj = new GridEntity()
            obj.id = invInventoryTransactionDetails.id
            obj.cell = [
                    counter,
                    invInventoryTransactionDetails.id,
                    invInventoryTransactionDetails.item_name,
                    invInventoryTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invInventoryTransactionDetails.unit,
                    invInventoryTransactionDetails.rate,
                    invInventoryTransactionDetails.transaction_date,
                    invInventoryTransactionDetails.mrf,
                    invInventoryTransactionDetails.vehicle,
                    invInventoryTransactionDetails.vehicle_number,
                    invInventoryTransactionDetails.created_by,
                    invInventoryTransactionDetails.approved_on,
                    invInventoryTransactionDetails.approved_by
            ]
            lstWrappedInventoryOutDetails << obj
            counter++
        }
        return lstWrappedInventoryOutDetails
    }

    private static final String COUNT_QUERY = """
        SELECT count(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        WHERE inventory_transaction_id= :inventoryTransactionId
          AND iitd.approved_by > 0
          AND iitd.is_current = true
    """

    /**
     * Get list and count of approved inventory transactions out details(
     * @param inventoryTransactionId -id of inventory transaction object(parent object)
     * @return -a map containing list and count of approved inventory transactions out details
     */
    private LinkedHashMap listForApprovedInvOutDetailsByInvTransId(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name,iitd.rate, item.unit,to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity, to_char(iitd.approved_on,'dd-Mon-yyyy') as approved_on,
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
                          AND iitd.is_current = true
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage  OFFSET :start
        """

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = resultCount[0].count
        return [inventoryOutDetailsList: result, count: count]
    }
}
