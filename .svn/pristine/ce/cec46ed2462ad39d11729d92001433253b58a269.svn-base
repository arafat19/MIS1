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
 *  Class to get search-result-list of unapprovedOutTransaction item list to show on grid
 *  For details go through Use-Case doc named 'SearchForUnApprovedInventoryOutDetailsActionService'
 */
class SearchForUnApprovedInventoryOutDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load inventory out details page"
    private static final String INVENTORY_DETAILS_OUT_LIST_WRAP = "inventoryDetailsOutListWrap"
    private static final String COUNT = "count"
    private static final String INV_DETAILS_ID = "iitd.id"
    private static final String ACTUAL_QUANTITY = "iitd.actual_quantity"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * method to search unapprovedInvOutDetails list
     * @param params -Receives from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for grid data(WrappedUnapprovedInvOutDetailList)
     *          map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)
            List<GroovyRowResult> inventoryOutList = []
            int total = 0
            LinkedHashMap serviceReturn
            long inventoryTransactionId = Long.parseLong(parameterMap.inventoryOutId.toString())
            if (inventoryTransactionId > 0) {
                if (queryType == INV_DETAILS_ID || queryType == ACTUAL_QUANTITY) {
                    try {
                        //get search(by InvTranDetailsId OR ActualQuantity) list of unapproved out item list
                        serviceReturn = searchForUnapprovedInvOutDetailsByInvTransIdAndNumber(inventoryTransactionId)
                        inventoryOutList = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                        total = (int) serviceReturn.count
                    } catch (Exception ex) {
                        result.put(Tools.MESSAGE, INVALID_INPUT)
                        return result
                    }
                } else { //searching by other than InvTranDetailsId OR ActualQuantity
                    serviceReturn = searchForUnapprovedInvOutDetailsByInvTransId(inventoryTransactionId)
                    inventoryOutList = (List<GroovyRowResult>) serviceReturn.inventoryOutDetailsList
                    total = (int) serviceReturn.count
                }
            }

            //wrap unapproved-out-item-list for grid
            List inventoryOutListWrap = wrapInventoryOutListInGridEntity(inventoryOutList, start)

            result.put(INVENTORY_DETAILS_OUT_LIST_WRAP, inventoryOutListWrap)
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

    /**
     * WrappedUnapprovedOutItemList for grid
     * @param inventoryOutList -unapprovedOutItemList
     * @param start -start index
     * @return WrappedUnapprovedOutItemList
     */
    private List wrapInventoryOutListInGridEntity(List<GroovyRowResult> inventoryOutList, int start) {
        List invTransactionDetailsList = [] as List
        int counter = start + 1
        for (int i = 0; i < inventoryOutList.size(); i++) {
            GroovyRowResult invTranDetails = inventoryOutList[i]
            GridEntity obj = new GridEntity()
            obj.id = invTranDetails.id
            obj.cell = [counter,
                    invTranDetails.id,
                    invTranDetails.item_name,
                    invTranDetails.actual_quantity + Tools.SINGLE_SPACE + invTranDetails.unit,
                    invTranDetails.transaction_date,
                    invTranDetails.mrf,
                    invTranDetails.vehicle,
                    invTranDetails.vehicle_number,
                    invTranDetails.created_by,
                    invTranDetails.updated_by ? invTranDetails.updated_by : Tools.EMPTY_SPACE
            ]
            invTransactionDetailsList << obj
            counter++
        }
        return invTransactionDetailsList
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrapInventoryOutItemList for grid
     * @param obj -map returned from execute method
     * @return -a map containing WrappedUnapprovedOutItemList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryOutDetails = (List) receiveResult.get(INVENTORY_DETAILS_OUT_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: inventoryOutDetails]
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
     * Get search(by InvTranDetailsId OR ActualQuantity) list of unapprovedInventoryOutDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(UnapprovedOutDetailsInfo based on search) &
     *              count(Total number of-UnapprovedOutDetailsInfo based on search)
     */
    private LinkedHashMap searchForUnapprovedInvOutDetailsByInvTransIdAndNumber(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit,
                        to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity,user_created_by.username as created_by,
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
                        AND iitd.is_current = true
                        AND ${queryType} = ${query}
                        ORDER BY iitd.transaction_date, iitd.id DESC LIMIT :resultPerPage  OFFSET :start
                     """
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item item ON item.id=iitd.item_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                        AND iitd.approved_by <= 0
                        AND is_current = true
                        AND ${queryType} = ${query}
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

    /**
     * Get search(other than InvTranDetailsId OR ActualQuantity) list of unapprovedInventoryOutDetails for grid
     * @param inventoryTransactionId -InvInventoryTransaction.id (parentId)
     * @return - a map containing list of GroovyRowResult(UnapprovedOutDetailsInfo based on search) &
     *              count(Total number of-UnapprovedOutDetailsInfo based on search)
     */
    private LinkedHashMap searchForUnapprovedInvOutDetailsByInvTransId(long inventoryTransactionId) {
        String queryStr = """
                        SELECT
                        iitd.id, item.name AS item_name, item.unit,
                        to_char(iitd.transaction_date,'dd-Mon-yyyy') as transaction_date,
                        to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') actual_quantity,user_created_by.username as created_by,
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
                          AND ${queryType} ILIKE :query
                        ORDER BY iitd.transaction_date DESC LIMIT :resultPerPage  OFFSET :start
                     """
        String queryCount = """
                        SELECT count(iitd.id) AS count
                        FROM inv_inventory_transaction_details iitd
                        LEFT JOIN item item ON item.id=iitd.item_id
                        WHERE inventory_transaction_id= :inventoryTransactionId
                          AND iitd.approved_by <= 0
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
