package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to search unapproved inventory-production-with-consumption-details
 *  For details go through Use-Case doc named 'SearchForInvProductionWithConsumptionActionService'
 */
class SearchForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load Production List"
    private static final String INVENTORY_PRODUCTION_LIST_WRAP = "inventoryProductionListWrap"
    private static final String COUNT = "count"
    private static final String SORT_ORDER_DESCENDING = 'desc'
    private static final String SORT_BY_CREATED_ON = "transaction_date"

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     *  wrap unApprovedInventoryProductionList for grid
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

            initSearch(parameterMap)
            sortColumn = SORT_BY_CREATED_ON
            sortOrder = SORT_ORDER_DESCENDING

            LinkedHashMap serviceReturn
            List<GroovyRowResult> inventoryProductionList = []
            int total = 0

            //Get id list of userMappedInventory
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            if (inventoryIds.size() > 0) { //if userMappedInventoryIds > 0 then get list of UnapprovedProductionDetailsInfo of that Inventories
                serviceReturn = searchForUnapprovedProdWithConsump(inventoryIds)
                inventoryProductionList = serviceReturn.lstProduction
                total = (int) serviceReturn.count
            }

            List inventoryProductionListWrap = wrapInventoryProductionList(inventoryProductionList, start)

            result.put(INVENTORY_PRODUCTION_LIST_WRAP, inventoryProductionListWrap)
            result.put(COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @param obj -map returned from execute method
     * @return -a map containing WrappedProductionDetailsList for grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List inventoryIn = (List) receiveResult.get(INVENTORY_PRODUCTION_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: inventoryIn]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    //wrapUnapprovedInventoryProductionList for grid
    private List wrapInventoryProductionList(List<GroovyRowResult> InventoryProductionList, int start) {
        List lstInventoryProductions = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < InventoryProductionList.size(); i++) {
            obj = new GridEntity()
            singleRow = InventoryProductionList[i]
            obj.id = singleRow.consump_id
            String transactionDate = DateUtility.getLongDateForUI(singleRow.transaction_date)
            obj.cell = [counter,
                    singleRow.consump_id,
                    singleRow.type_name + Tools.COLON + singleRow.inventory_name,
                    singleRow.line_item_name,
                    singleRow.consump_material_count,
                    singleRow.prod_material_count,
                    transactionDate, singleRow.created_by,
                    singleRow.updated_by ? singleRow.updated_by : Tools.EMPTY_SPACE]
            lstInventoryProductions << obj
            counter++
        }
        return lstInventoryProductions
    }

    /**
     * Get SearchList of Unapproved-Production-With-Consumption
     *
     * @param lstInventories -list of inventoryIds(inventory.id)
     *
     * @return - a map containing list of GroovyRowResult(UnapprovedProductionWithConsumptionDetailsInfo) &
     *              count(Total number of-UnapprovedProductionWithConsumption)
     */
    private Map searchForUnapprovedProdWithConsump(List<Long> lstInventories) {
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventories)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        String queryStr = """
                SELECT consump.id AS consump_id, prod.id AS prod_id,line_item.name AS line_item_name,
                consump.transaction_date,consump.item_count AS consump_material_count, prod.item_count AS prod_material_count,
                inv.name inventory_name,type.key type_name,
                user_created_by.username as created_by,user_updated_by.username as updated_by
                FROM inv_inventory_transaction consump
                LEFT JOIN inv_inventory_transaction prod ON consump.id = prod.transaction_id
                LEFT JOIN inv_production_line_item line_item ON consump.inv_production_line_item_id= line_item.id
                LEFT JOIN inv_inventory inv ON inv.id=consump.inventory_id
                LEFT JOIN system_entity type ON type.id = consump.inventory_type_id
                LEFT JOIN app_user user_created_by ON user_created_by.id = consump.created_by
                LEFT JOIN app_user user_updated_by ON user_updated_by.id = consump.updated_by
                WHERE consump.inventory_id IN (${inventoryIds})
                AND consump.transaction_type_id = :transactionTypeId
                AND consump.inv_production_line_item_id > 0
                AND consump.is_approved = FALSE
                AND ${queryType} ILIKE :query
            ORDER BY ${sortColumn} ${sortOrder}
            LIMIT ${resultPerPage}  OFFSET ${start}
        """
        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                query: Tools.PERCENTAGE + query + Tools.PERCENTAGE
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)

        String queryCount = """
              SELECT COUNT(consump.id) count
                FROM inv_inventory_transaction consump
                LEFT JOIN inv_inventory inv ON inv.id=consump.inventory_id
                LEFT JOIN inv_production_line_item line_item ON consump.inv_production_line_item_id= line_item.id
                WHERE consump.inventory_id IN (${inventoryIds})
                AND consump.inv_production_line_item_id >0
                AND consump.transaction_type_id = :transactionTypeId
                AND consump.is_approved = FALSE
              AND ${queryType} ILIKE :query
        """

        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)
        int total = countResult[0].count
        return [lstProduction: result, count: total]
    }
}