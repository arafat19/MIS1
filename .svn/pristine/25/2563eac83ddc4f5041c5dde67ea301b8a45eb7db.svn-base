package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Parent class to show UI for approved-inventory-production-with-consumption
 *  For details go through Use-Case doc named 'ShowForApprovedProductionWithConsumptionActionService'
 */
class ShowForApprovedProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load inventory production page"
    private static final String LST_PRODUCTION_LINE_ITEM = "lstProductionLineItem"
    private static final String COUNT = "count"
    private static final String OBJ_GRID_INV_PRODUCTION = "objGridInvProduction"
    private static final String PRODUCTION_DETAILS_LIST = "productionDetailsList"

    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    InvInventoryTypeCacheUtility invInventoryTypeCacheUtility
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
     * list of approved-produced-items-information for grid
     * InventoryTypeList & ProductionLineItemList for drop-down
     *
     * @Params parameters -N/A
     * @Params obj -N/A
     *
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            initPager(params)

            LinkedHashMap serviceReturn
            List<GroovyRowResult> lstProduction = []
            int total = 0

            //Get id list of userMappedInventory
            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            if (inventoryIds.size() > 0) {
                serviceReturn = listForApprovedProdWithConsump(inventoryIds)
                lstProduction = serviceReturn.lstProduction
                total = (int) serviceReturn.count
            }
            result.put(PRODUCTION_DETAILS_LIST, lstProduction)
            result.put(COUNT, total)

            //Get list of InvProductionLineItemList fo login user company
            List lstProductionLineItem = invProductionLineItemCacheUtility.list()


            result.put(LST_PRODUCTION_LINE_ITEM, lstProductionLineItem)
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
     * Wrap ApprovedProductionDetailsList for grid
     *
     * @param obj -map returned from execute method
     *
     * @return -a map containing all objects necessary for show page (WrappedProductionDetailsList, InventoryTypeList, ProductionLineItemList)
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List lstProductionLineItem = (List) executeResult.get(LST_PRODUCTION_LINE_ITEM)

            // Wrap ApprovedProductionDetailsList for grid
            int count = (int) executeResult.get(COUNT)
            List productionDetailsList = (List) executeResult.get(PRODUCTION_DETAILS_LIST)
            List productionDetailsListWrap = wrapInventoryProductionList(productionDetailsList, start)
            Map gridOutput = [page: pageNumber, total: count, rows: productionDetailsListWrap]

            result.put(OBJ_GRID_INV_PRODUCTION, gridOutput)
            result.put(LST_PRODUCTION_LINE_ITEM, Tools.listForKendoDropdown(lstProductionLineItem,null,null))
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
     * Build failure result in case of any error
     * @param obj -map returned from previous method, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of unapprovedProductionDetailsList for gird
     *
     * @param inventoryProductionList -list of inventoryProduction object(s)
     * @param start -starting index of the page
     *
     * @return -list of wrapped InvProductionDetailsInfo
     */
    private List wrapInventoryProductionList(List<GroovyRowResult> inventoryProductionList, int start) {
        List lstInvProductions = []
        int counter = start + 1
        GridEntity obj
        GroovyRowResult singleRow
        for (int i = 0; i < inventoryProductionList.size(); i++) {
            obj = new GridEntity()
            singleRow = inventoryProductionList[i]
            obj.id = singleRow.consump_id
            String createdOn = DateUtility.getLongDateForUI(singleRow.transaction_date)
            obj.cell = [counter,
                    singleRow.consump_id,
                    singleRow.type_name + Tools.COLON + singleRow.inventory_name,
                    singleRow.line_item_name,
                    singleRow.consump_material_count,
                    singleRow.prod_material_count,
                    createdOn,
                    singleRow.created_by
            ]

            lstInvProductions << obj
            counter++
        }
        return lstInvProductions
    }

    /**
     * Get List of approved-Production-With-Consumption
     *
     * @param lstInventories -list of inventoryIds(inventory.id)
     *
     * @return - a map containing list of GroovyRowResult(approvedProductionWithConsumptionDetailsInfo) &
     *              count(Total number of approvedProductionWithConsumption)
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

