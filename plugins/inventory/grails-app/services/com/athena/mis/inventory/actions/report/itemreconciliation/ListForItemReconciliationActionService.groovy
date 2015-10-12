package com.athena.mis.inventory.actions.report.itemreconciliation

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
 *  get list of Item-Reconciliation for grid
 *  For details go through Use-Case doc named 'ListForItemReconciliationActionService'
 */
class ListForItemReconciliationActionService extends BaseService implements ActionIntf {

    final Logger log = Logger.getLogger(getClass())

    private static final String ITEM_RECONCILIATION_LIST = "itemReconciliationList"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to get item reconciliation list"
    private static final String DATA_NOT_FOUND_MSG = "Item reconciliation not found of selected project"
    private static final String LIST_GRID = "gridOutput"
    private static final String COUNT = "count"

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    /**
     * Checking required parameters send from UI
     * @param parameters -N/A
     * @param obj -N/A
     * @return -map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * do nothing for executePostCondition operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * method to get list of item-reconciliation by project
     *      -if specific project is given then get item-reconciliation-list by given project and date range
     *      -else select all projects then : pull all user mapped projectIds & then get item-reconciliation-list
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing item-reconciliation-list and count and isError(True/False) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            List<Long> projectIds = []
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                List<Long> tempProjectIdList = invSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
            }

            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)
            LinkedHashMap itemReconciliationMap = getItemReconciliationList(projectIds)
            if (itemReconciliationMap.count.toInteger() <= 0) {
                result.put(Tools.MESSAGE, DATA_NOT_FOUND_MSG)
                return result
            }
            result.put(ITEM_RECONCILIATION_LIST, itemReconciliationMap.itemReconciliationList)
            result.put(COUNT, itemReconciliationMap.count)
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
     * Build a map with all necessary objects for grid view
     * @param obj -map returned from execute method
     * @return -a map containing Wrapped item-reconciliation-list for grid &
     *              isError(True/False) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            List<GroovyRowResult> itemReconciliationList = (List<GroovyRowResult>) returnResult.get(ITEM_RECONCILIATION_LIST)
            int count = Integer.parseInt(returnResult.get(COUNT).toString())

            List itemReconciliationListWrap = wrapItemReconciliationListGrid(itemReconciliationList, this.start)
            Map gridOutput = [page: pageNumber, total: count, rows: itemReconciliationListWrap]
            result.put(LIST_GRID, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrapped-item-reconciliation-list for grid
     * @param itemReconciliationList -List of GroovyRowResult
     * @param start -start index
     * @return -WrappedItemReconciliation-list
     */
    private List wrapItemReconciliationListGrid(List<GroovyRowResult> itemReconciliationList, int start) {
        List gridObjectList = [] as List
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < itemReconciliationList.size(); i++) {
            singleRow = itemReconciliationList[i]
            obj = new GridEntity()
            obj.id = singleRow.item_id
            obj.cell = [
                    counter,
                    singleRow.item_name,
                    singleRow.total_increase_quantity,
                    singleRow.total_decrease_quantity,
                    singleRow.total_shrinkage,
                    singleRow.total_pending,
                    singleRow.total_stock_quantity
            ]
            gridObjectList << obj
            counter++
        }
        return gridObjectList
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                result.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
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
     * get itemReconciliationList to show on grid
     * @param projectIds -list of projectIds(Project.id)
     * @return -map contains list of itemReconciliationList and count
     */
    private LinkedHashMap getItemReconciliationList(List<Long> projectIds) {
        String projectIdStr = Tools.buildCommaSeparatedStringOfIds(projectIds)
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)

        String strQuery = """
            SELECT iitd.item_id AS item_id,item.name AS item_name,

            to_char(COALESCE(total_increase.total_increase_quantity,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_increase_quantity,
            to_char(COALESCE(total_decrease.total_decrease_quantity,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_decrease_quantity,

            to_char((COALESCE(inventory_in_from_inv.total_supplied_quantity,0))-(COALESCE(inventory_in_from_inv.total_actual_quantity,0)), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_shrinkage,

            to_char((COALESCE(inventory_out.total_inv_out_quantity,0))-(COALESCE(inventory_in_from_inv.total_supplied_quantity,0)), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_pending,

            to_char(COALESCE(stock.total_stock,0), '${Tools.DB_QUANTITY_FORMAT}') ||' '|| item.unit AS total_stock_quantity

        FROM vw_inv_inventory_transaction_with_details iitd

            FULL OUTER JOIN
            (
                SELECT item_id,
                        SUM(actual_quantity) AS total_increase_quantity
                FROM vw_inv_inventory_transaction_with_details
                WHERE project_id IN (${projectIdStr}) AND
                      approved_by > 0 AND
                      is_increase = TRUE AND
                      is_current = TRUE
                GROUP BY item_id
            ) total_increase
            ON total_increase.item_id = iitd.item_id

            FULL OUTER JOIN
            (
                SELECT item_id,
                        SUM(actual_quantity) AS total_decrease_quantity
                FROM vw_inv_inventory_transaction_with_details
                WHERE project_id IN (${projectIdStr}) AND
                      approved_by > 0 AND
                      is_increase = FALSE AND
                      is_current = TRUE
                GROUP BY item_id
            ) total_decrease
            ON total_decrease.item_id = iitd.item_id

            FULL OUTER JOIN
            (
                SELECT item_id,
                        SUM(actual_quantity) AS total_inv_out_quantity
                FROM vw_inv_inventory_transaction_with_details
                WHERE project_id IN (${projectIdStr}) AND
                      transaction_type_id =:transactionTypeIdOut AND
                      transaction_entity_type_id =:transactionEntityTypeId AND
                      approved_by > 0 AND
                      is_current = TRUE
                GROUP BY item_id
            ) inventory_out
            ON inventory_out.item_id = iitd.item_id

            FULL OUTER JOIN
            (
                SELECT item_id,
                        SUM(supplied_quantity) AS total_supplied_quantity,
                        SUM(actual_quantity) AS total_actual_quantity
                FROM vw_inv_inventory_transaction_with_details
                WHERE project_id IN (${projectIdStr}) AND
                      transaction_type_id =:transactionTypeId AND
                      transaction_entity_type_id =:transactionEntityTypeId AND
                      approved_by > 0 AND
                      is_current = TRUE
                GROUP BY item_id
            ) inventory_in_from_inv
            ON inventory_in_from_inv.item_id = iitd.item_id

            FULL OUTER JOIN
            (
                SELECT item_id,
                        SUM(available_stock) AS total_stock
                FROM vw_inv_inventory_valuation
                LEFT JOIN inv_inventory inv on inv.id = vw_inv_inventory_valuation.inventory_id
                WHERE inv.project_id IN (${projectIdStr})
                GROUP BY item_id
            ) stock
            ON stock.item_id = iitd.item_id

            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.approved_by > 0 AND
                  iitd.is_current = TRUE AND
                  iitd.project_id IN (${projectIdStr})
            GROUP BY iitd.item_id, item.name, item.unit,
                     total_increase.total_increase_quantity,
                     total_decrease.total_decrease_quantity,
                     inventory_out.total_inv_out_quantity,
                     inventory_in_from_inv.total_actual_quantity,
                     inventory_in_from_inv.total_supplied_quantity,
                     stock.total_stock
            ORDER BY item.name
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
            SELECT COUNT(DISTINCT(item_id)) AS count
            FROM vw_inv_inventory_transaction_with_details
            WHERE project_id IN (${projectIdStr}) AND
                  approved_by > 0 AND
                  is_current = TRUE
        """

        Map queryParams = [
                transactionTypeId: transactionTypeIn.id,
                transactionTypeIdOut: transactionTypeOut.id,
                transactionEntityTypeId: transactionEntityInventory.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> itemReconciliationList = executeSelectSql(strQuery, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(queryCount)
        int total = resultCount[0].count
        return [itemReconciliationList: itemReconciliationList, count: total]
    }
}
