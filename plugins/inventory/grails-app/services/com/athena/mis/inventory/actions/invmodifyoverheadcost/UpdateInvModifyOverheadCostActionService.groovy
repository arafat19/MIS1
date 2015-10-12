package com.athena.mis.inventory.actions.invmodifyoverheadcost

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Update overhead Cost
class UpdateInvModifyOverheadCostActionService extends BaseService implements ActionIntf {
    protected final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility

    private static final String SUCCESS_MESSAGE = " transaction(s) updated by the given Overhead Cost."
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to load Modification page of Overhead Cost."
    private static final String PROD_LINE_ITEM_NOT_FOUND = "Production Line Item not found."
    private static final String FINISH_PRODUCT_NOT_FOUND = "Finish Product not found."
    private static final String INVALID_INPUT = "Error occurred due to invalid input."
    private static final String OVERHEAD_COST = "overheadCost"
    private static final String PROD_LINE_ITEM_OBJ = "prodLineItemObj"
    private static final String FINISHED_MATERIAL_OBJ = "finishProductObj"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String TRANSACTION_DETAILS_LIST = "transactionDetailsList"
    private static final String SORT_COLUMN = "viitd.transaction_date"
    private static final String PROD_LINE_ITEM_ID = "prodLineItemId"
    private static final String FINISHED_MATERIAL_ID = "finishedMaterialId"
    private static final String INVENTORY_NOT_FOUND = "No Associated Inventory found."

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.hidProdLineItemId || !parameterMap.hidFinishedMaterialId
                    || !parameterMap.hidFromDate || !parameterMap.hidToDate || !parameterMap.overheadCost) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            long prodLineItemId = 0
            long finishedMaterialId = 0
            Date fromDate
            Date toDate
            double overheadCost
            try {
                prodLineItemId = Long.parseLong(parameterMap.hidProdLineItemId.toString())
                finishedMaterialId = Long.parseLong(parameterMap.hidFinishedMaterialId.toString())
                fromDate = DateUtility.parseMaskedFromDate(parameterMap.hidFromDate.toString())
                toDate = DateUtility.parseMaskedToDate(parameterMap.hidToDate.toString())
                overheadCost = Double.parseDouble(parameterMap.overheadCost.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            List<Long> inventoryIdsList = invSessionUtil.getUserInventoryIds()
            if (inventoryIdsList.size() <= 0) {
                result.put(Tools.MESSAGE, INVENTORY_NOT_FOUND)
                return result
            }

            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(prodLineItemId)
            if (!invProductionLineItem) {
                result.put(Tools.MESSAGE, PROD_LINE_ITEM_NOT_FOUND)
                return result
            }

            InvProductionDetails invProductionDetails = (InvProductionDetails) invProductionDetailsCacheUtility.read(finishedMaterialId)
            if (!invProductionDetails) {
                result.put(Tools.MESSAGE, FINISH_PRODUCT_NOT_FOUND)
                return result
            }

            result.put(PROD_LINE_ITEM_OBJ, invProductionLineItem)
            result.put(FINISHED_MATERIAL_OBJ, invProductionDetails)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(OVERHEAD_COST, overheadCost)
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

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) receivedResult.get(PROD_LINE_ITEM_OBJ)
            InvProductionDetails invProductionDetails = (InvProductionDetails) receivedResult.get(FINISHED_MATERIAL_OBJ)
            Date fromDate = (Date) receivedResult.get(FROM_DATE)
            Date toDate = (Date) receivedResult.get(TO_DATE)
            double overheadCost = (double) receivedResult.get(OVERHEAD_COST)

            int updateCount = updateOverheadCost(invProductionLineItem.id, invProductionDetails.materialId, fromDate, toDate, overheadCost)
            String successMsg = updateCount.toString() + SUCCESS_MESSAGE
            initPager(parameterMap)
            if (!parameterMap.rp) {
                resultPerPage = 20
                start = 0
            }
            if (!parameterMap.sortname) {
                sortColumn = SORT_COLUMN
            }
            LinkedHashMap serviceReturn = searchTransactionDetailsList(invProductionLineItem.id, invProductionDetails.materialId, fromDate, toDate)
            int transactionDetailsCount = serviceReturn.count
            result.put(TRANSACTION_DETAILS_LIST, serviceReturn.transactionDetailsList)
            result.put(Tools.COUNT, transactionDetailsCount)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROD_LINE_ITEM_ID, invProductionLineItem.id)
            result.put(FINISHED_MATERIAL_ID, invProductionDetails.id)
            result.put(Tools.MESSAGE, successMsg)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            List<GroovyRowResult> transactionDetailsList = (List<GroovyRowResult>) receivedResult.get(TRANSACTION_DETAILS_LIST)
            List transactionDetailsListWrap = wrapTransactionDetailsGridEntityList(transactionDetailsList, start)
            int count = Integer.parseInt(receivedResult.get(Tools.COUNT).toString())
            Map gridOutput = [page: pageNumber, total: count, rows: transactionDetailsListWrap]
            result.put(TRANSACTION_DETAILS_LIST, gridOutput)
            result.put(FROM_DATE, receivedResult.get(FROM_DATE))
            result.put(TO_DATE, receivedResult.get(TO_DATE))
            result.put(PROD_LINE_ITEM_ID, receivedResult.get(PROD_LINE_ITEM_ID))
            result.put(FINISHED_MATERIAL_ID, receivedResult.get(FINISHED_MATERIAL_ID))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, receivedResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.get(Tools.MESSAGE)) {
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

    //update inv_inventory_transaction_details    by overhead cost
    public int updateOverheadCost(long prodLineItemId, long finishedMaterialId, Date fromDate, Date toDate, double overheadCost) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        String inventoryIdsStr = Tools.buildCommaSeparatedStringOfIds(invSessionUtil.getUserInventoryIds())
        String queryStr = """UPDATE inv_inventory_transaction_details
                        SET  version = inv_inventory_transaction_details.version+1,
                             overhead_cost =:overheadCost
                        FROM inv_inventory_transaction
                        WHERE inv_inventory_transaction.transaction_type_id =:transactionTypeId
                        AND inv_inventory_transaction_details.inventory_transaction_id = inv_inventory_transaction.id
                        AND inv_inventory_transaction.inv_production_line_item_id =:prodLineItemId
                        AND inv_inventory_transaction_details.inventory_id IN (${inventoryIdsStr})
                        AND inv_inventory_transaction_details.transaction_date BETWEEN :fromDate AND :toDate
                        AND inv_inventory_transaction_details.item_id =:finishedMaterialId
                        AND inv_inventory_transaction.company_id =:companyId
                        """
        Map queryParams = [
                transactionTypeId: transactionTypePro.id,
                prodLineItemId: prodLineItemId,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                finishedMaterialId: finishedMaterialId,
                companyId: companyId,
                overheadCost: overheadCost
        ]
        int updateCount = executeUpdateSql(queryStr, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update transaction details")
        }
        return updateCount
    }

    private List wrapTransactionDetailsGridEntityList(List<GroovyRowResult> transactionDetailsList, int start) {
        List lstTransactionDetails = [] as List
        int counter = start + 1
        GridEntity obj
        GroovyRowResult transactionDetails
        for (int i = 0; i < transactionDetailsList.size(); i++) {
            transactionDetails = transactionDetailsList[i]
            obj = new GridEntity()
            obj.id = transactionDetails.id
            obj.cell = [counter, transactionDetails.id, transactionDetails.inventory_name, transactionDetails.str_transaction_date,
                    transactionDetails.str_rate, transactionDetails.str_overhead_cost]
            lstTransactionDetails << obj
            counter++
        }
        return lstTransactionDetails

    }

    //query to search all transaction details in Production
    private Map searchTransactionDetailsList(long prodLineItemId, long finishedMaterialId, Date fromDate, Date toDate) {
        String inventoryIdsStr = Tools.buildCommaSeparatedStringOfIds(invSessionUtil.getUserInventoryIds())
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        String queryStr = """
          SELECT viitd.id, (inventory_type.key || ': ' || inventory.name) AS inventory_name,
            TO_CHAR(viitd.transaction_date,'dd-Mon-yyyy') AS str_transaction_date,
            TO_CHAR(viitd.rate,'${Tools.DB_CURRENCY_FORMAT}') AS str_rate,
            TO_CHAR(viitd.overhead_cost,'${Tools.DB_CURRENCY_FORMAT}') AS str_overhead_cost
            FROM vw_inv_inventory_transaction_with_details viitd
            LEFT JOIN inv_inventory inventory ON inventory.id = viitd.inventory_id
            LEFT JOIN system_entity inventory_type on inventory_type.id = inventory.type_id
            WHERE viitd.transaction_type_id =:transactionTypeId
            AND viitd.inv_production_line_item_id =:prodLineItemId
            AND viitd.inventory_id IN (${inventoryIdsStr})
            AND viitd.transaction_date BETWEEN :fromDate AND :toDate
            AND viitd.item_id =:finishedMaterialId
            AND viitd.company_id =:companyId
             ORDER BY ${sortColumn} ${sortOrder}
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
            SELECT COUNT(viitd.id)
            FROM vw_inv_inventory_transaction_with_details viitd
            WHERE viitd.transaction_type_id =:transactionTypeId
            AND viitd.inv_production_line_item_id =:prodLineItemId
            AND viitd.inventory_id IN (${inventoryIdsStr})
            AND viitd.transaction_date BETWEEN :fromDate AND :toDate
            AND viitd.item_id =:finishedMaterialId
            AND viitd.company_id =:companyId
        """

        Map queryParams = [
                transactionTypeId: transactionTypePro.id,
                prodLineItemId: prodLineItemId,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                finishedMaterialId: finishedMaterialId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> transactionDetailsList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [transactionDetailsList: transactionDetailsList, count: count]
    }
}