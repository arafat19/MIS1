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
 *  Class to get list of unapprovedOutTransaction item list to show on grid
 *  For details go through Use-Case doc named 'ListForUnApprovedInventoryOutDetailsActionService'
 */
class ListForUnApprovedInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load inventory out details page"
    private static final String INVENTORY_OUT_DETAILS_LIST_WRAP = "inventoryOutDetailsListWrap"
    private static final String COUNT = "count"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  wrap unapprovedOutTransactionItemList for grid
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
            List<GroovyRowResult> inventoryOutList = []
            int total = 0

            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryOutId.toString())
            if (inventoryTransactionId > 0) {//get list of unapproved-out-transaction-item-list
                LinkedHashMap serviceReturn = listForUnapprovedInvOutDetailsByInvTransId(inventoryTransactionId)
                inventoryOutList = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                total = (int) serviceReturn.count
            }
            //wrap unapproved-out-transaction-item-list for grid
            List inventoryOutListWrap = wrapOutDetailsListForGridEntity(inventoryOutList, start)

            result.put(INVENTORY_OUT_DETAILS_LIST_WRAP, inventoryOutListWrap)
            result.put(COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @param obj -map returned from execute method
     * @return -a map containing WrappedUnapprovedOutItemList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryDetailsOut = (List) receiveResult.get(INVENTORY_OUT_DETAILS_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: inventoryDetailsOut]
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
     * unapprovedOutTransactionItemList for grid
     * @param inventoryOutList -unapprovedOutTransactionItemList
     * @param start -start index
     * @return WrappedUnapprovedOutTransactionItemList
     */
    private List wrapOutDetailsListForGridEntity(List<GroovyRowResult> inventoryOutList, int start) {
        List invTransactionDetailsList = [] as List
        int counter = start + 1
        for (int i = 0; i < inventoryOutList.size(); i++) {
            GroovyRowResult invTransactionDetails = inventoryOutList[i]
            GridEntity obj = new GridEntity()
            obj.id = invTransactionDetails.id
            obj.cell = [
                    counter,
                    invTransactionDetails.id,
                    invTransactionDetails.item_name,
                    invTransactionDetails.actual_quantity + Tools.SINGLE_SPACE + invTransactionDetails.unit,
                    invTransactionDetails.transaction_date,
                    invTransactionDetails.mrf,
                    invTransactionDetails.vehicle,
                    invTransactionDetails.vehicle_number,
                    invTransactionDetails.created_by,
                    invTransactionDetails.updated_by ? invTransactionDetails.updated_by : Tools.EMPTY_SPACE
            ]
            invTransactionDetailsList << obj
            counter++
        }
        return invTransactionDetailsList
    }

    private static final String COUNT_QUERY = """
        SELECT count(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        WHERE inventory_transaction_id= :inventoryTransactionId
          AND iitd.approved_by <= 0
          AND iitd.is_current = true
    """
    /**
     * Get list of unapprovedInventoryOutDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(UnapprovedInvOutDetailsInfo) &
     *              count(Total number of-UnapprovedInvOutDetailsInfo)
     */
    private LinkedHashMap listForUnapprovedInvOutDetailsByInvTransId(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit,
                        to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity, user_created_by.username as created_by,
                        user_updated_by.username as updated_by,iitd.is_increase,
                        iitd.transaction_type_id,iitd.mrf_no as mrf,v.name as vehicle,iitd.vehicle_number
                        FROM
                        inv_inventory_transaction_details iitd
                        LEFT JOIN item ON item.id = iitd.item_id
                        LEFT JOIN vehicle v ON v.id = iitd.vehicle_id
                        LEFT JOIN app_user user_created_by ON user_created_by.id = iitd.created_by
                        LEFT JOIN app_user user_updated_by ON user_updated_by.id = iitd.updated_by
                        LEFT JOIN system_entity transaction_type ON transaction_type.id = iitd.transaction_type_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by <= 0
                          AND is_current = true
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
