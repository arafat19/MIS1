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
 *  Class to get list of approved inventory-production-with-consumption-details
 *  For details go through Use-Case doc named 'ListForApprovedProductionWithConsumptionActionService'
 */
class ListForApprovedProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String ERROR_MESSAGE = "Failed to load Production List"
    private static final String SORT_ORDER_DESCENDING = 'desc'
    private static final String INVENTORY_PRODUCTION_LIST_WRAP = "inventoryProductionListWrap"
    private static final String COUNT = "count"

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
     *  wrap ApprovedInventoryProductionList for grid
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

            sortOrder = SORT_ORDER_DESCENDING

            LinkedHashMap serviceReturn
            List<GroovyRowResult> inventoryProductionList = []
            int total = 0

            //Get id list of userMappedInventoryIds
            List<Long> inventoryId = invSessionUtil.getUserInventoryIds()
            if (inventoryId.size() > 0) { //if userMappedInventoryIds > 0 then get list of ApprovedProductionDetailsInfo of that Inventories
                serviceReturn = listForApprovedProdWithConsump(inventoryId)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MESSAGE)
            return result
        }
    }

    /**
     * wrapApprovedInventoryProductionList for grid
     * @param inventoryProductionList -approvedInventoryProductionList
     * @param start -start index
     * @return wrappedApprovedInventoryProductionList
    */
    private List wrapInventoryProductionList(List<GroovyRowResult> inventoryProductionList, int start) {
        List lstInventoryProductions = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < inventoryProductionList.size(); i++) {
            obj = new GridEntity()
            singleRow = inventoryProductionList[i]
            obj.id = singleRow.consump_id
            String trDate = DateUtility.getLongDateForUI(singleRow.transaction_date)
            obj.cell = [counter,
                    singleRow.consump_id,
                    singleRow.type_name + Tools.COLON + singleRow.inventory_name,
                    singleRow.line_item_name,
                    singleRow.consump_material_count,
                    singleRow.prod_material_count,
                    trDate,
                    singleRow.created_by
            ]
            lstInventoryProductions << obj
            counter++
        }
        return lstInventoryProductions
    }

    /**
     * Get List of Approved-Production-With-Consumption
     * @param lstInventories -list of inventoryIds(inventory.id)
     * @return - a map containing list of GroovyRowResult(UnapprovedProductionWithConsumptionDetailsInfo) &
     *              count(Total number of-UnapprovedProductionWithConsumption)
     */
    private Map listForApprovedProdWithConsump(List<Long> lstInventories) {
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventories)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        String queryStr = """
            SELECT consump.id AS consump_id, prod.id AS prod_id,line_item.name AS line_item_name,
                consump.transaction_date,consump.item_count AS consump_material_count, prod.item_count AS prod_material_count,
                inv.name inventory_name,type.key type_name,
                user_created_by.username as created_by
                FROM inv_inventory_transaction consump
                LEFT JOIN inv_inventory_transaction prod ON consump.id = prod.transaction_id
                LEFT JOIN inv_production_line_item line_item ON consump.inv_production_line_item_id= line_item.id
                LEFT JOIN inv_inventory inv ON inv.id=consump.inventory_id
                LEFT JOIN system_entity type ON type.id = consump.inventory_type_id
                LEFT JOIN app_user user_created_by ON user_created_by.id = consump.created_by
                WHERE consump.inventory_id IN (${inventoryIds})
                AND consump.transaction_type_id = :transactionTypeId
                AND consump.inv_production_line_item_id > 0
                AND consump.item_count > 0
                AND consump.is_approved = TRUE
            ORDER BY consump_id ${sortOrder}  LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
            SELECT COUNT(consump.id) count
                FROM inv_inventory_transaction consump
                LEFT JOIN inv_inventory_transaction prod ON consump.id = prod.transaction_id
                WHERE consump.inventory_id IN (${inventoryIds})
                AND consump.inv_production_line_item_id >0
                AND consump.item_count > 0
                AND consump.transaction_type_id = :transactionTypeId
                AND consump.is_approved = TRUE
        """

        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)
        int total = countResult[0].count
        return [lstProduction: result, count: total]
    }
}