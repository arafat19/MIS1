package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Get specific list of inventory out transaction for grid through search
 *  For details go through Use-Case doc named 'SearchForInventoryOutActionService'
 */
class SearchForInventoryOutActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_ERROR_MSG = "Fail to search Inventory Out page"
    private static final String LST_INVENTORY_OUT = "lstInventoryOut"
    private static final String SORT_BY_ID = "it.id"
    private static final String SORT_ORDER_DESCENDING = 'desc'

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get inventory transaction out list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(parameterMap)    // initialize parameters for flexGrid
            sortColumn = SORT_BY_ID
            sortOrder = SORT_ORDER_DESCENDING
            LinkedHashMap serviceReturn
            List<GroovyRowResult> lstInventoryOut = []
            int total = 0
            // get list of ids of inventory associated with user
            List<Long> lstInventoryIds = invSessionUtil.getUserInventoryIds()
            // get list and count of inventory out transaction
            if (lstInventoryIds.size() > 0) {
                serviceReturn = searchForInventoryOut(lstInventoryIds)
                lstInventoryOut = (List<GroovyRowResult>) serviceReturn.inventoryOutList
                total = (int) serviceReturn.count
            }

            result.put(LST_INVENTORY_OUT, lstInventoryOut)
            result.put(Tools.COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MSG)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap inventory out transaction list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) executeResult.get(Tools.COUNT)
            List lstInventoryOut = (List) executeResult.get(LST_INVENTORY_OUT)
            List lstWrappedInventoryOut = (List) wrapInventoryOutList(lstInventoryOut, start)
            result = [page: pageNumber, total: count, rows: lstWrappedInventoryOut]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MSG)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MSG)
            return result
        }
    }

    /**
     * Wrap list of inventory out transactions in grid entity
     * @param lstInventoryOut -list of inventory out transaction
     * @param start -starting index of the page
     * @return -list of wrapped inventory out transactions
     */
    private List wrapInventoryOutList(List<GroovyRowResult> lstInventoryOut, int start) {
        List lstWrappedInventoryOut = []
        int counter = start + 1
        GroovyRowResult eachRow
        for (int i = 0; i < lstInventoryOut.size(); i++) {
            eachRow = lstInventoryOut[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.from_inventory,
                    eachRow.to_inventory,
                    eachRow.budget_item,
                    eachRow.item_count,
                    eachRow.approved_item_count,
                    eachRow.transaction_date,
                    eachRow.created_by,
                    eachRow.updated_by ? eachRow.updated_by : Tools.EMPTY_SPACE
            ]
            lstWrappedInventoryOut << obj
            counter++
        }
        return lstWrappedInventoryOut
    }

    /**
     * Get list and count of inventory out transactions by search keyword
     * @param lstInventoryIds -list of inventory ids
     * @return -a map containing list and count of inventory out transactions
     */
    private LinkedHashMap searchForInventoryOut(List<Long> lstInventoryIds) {
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)

        // get comma separated string of ids from list of ids
        String strInventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryIds)
        // query for list
        String queryStr = """
        SELECT it.id,(f_se.key || ':' || frm_inv.name) from_inventory, (t_se.key || ':' || to_inv.name) to_inventory,
        budget.budget_item,it.item_count,
        (SELECT count(id) FROM inv_inventory_transaction_details
        WHERE inventory_transaction_id = it.id
        AND approved_by>0
        AND is_current=true) AS approved_item_count,
        to_char(it.transaction_date,'dd-Mon-yyyy') as transaction_date,
        user_created_by.username as created_by,user_updated_by.username as updated_by
        FROM inv_inventory_transaction it
        LEFT JOIN budg_budget budget ON budget.id = it.budget_id
        LEFT JOIN inv_inventory frm_inv ON frm_inv.id= it.inventory_id
        LEFT JOIN inv_inventory to_inv ON to_inv.id= it.transaction_entity_id
        LEFT JOIN system_entity f_se ON f_se.id= frm_inv.type_id
        LEFT JOIN system_entity t_se ON t_se.id= to_inv.type_id
        LEFT JOIN app_user user_created_by ON user_created_by.id = it.created_by
        LEFT JOIN app_user user_updated_by ON user_updated_by.id = it.updated_by
        WHERE it.transaction_type_id = :transactionTypeOut
        AND it.inventory_id IN (${strInventoryIds})
        AND ${queryType} ILIKE :query
        ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """
        // query for count
        String queryCount = """
        SELECT COUNT(it.id) count
        FROM inv_inventory_transaction it
        LEFT JOIN  inv_inventory frm_inv on frm_inv.id=it.inventory_id
        LEFT JOIN inv_inventory to_inv ON to_inv.id= it.transaction_entity_id
        WHERE it.transaction_type_id = :transactionTypeOut
        AND it.inventory_id IN (${strInventoryIds})
        AND ${queryType} ILIKE :query
        """
        Map queryParams = [
                transactionTypeOut: transactionTypeOut.id,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)

        int total = (int) countResult[0].count
        return [inventoryOutList: result, count: total]
    }
}
