package com.athena.mis.inventory.actions.report.invpoitemreceived

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show received items of Purchase Order
 *  For details go through Use-Case doc named 'InvListForPoItemReceivedActionService'
 */
class InvListForPoItemReceivedActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate PO item received report"
    private static final String LST_PO_ITEM_RECEIVED = "lstPoItemReceived"
    private static final String INVALID_PO = "Please enter digits in PO no"
    private static final String PO_ITEM_LIST_NOT_FOUND = "PO item received information not found with PO no : "
    private static final String PO_NOT_FOUND = "Inventory Item not received with given PO"
    private static final String SUPPLIER_NAME = "supplierName"

    /**
     * Check required parameters from UI
     * Check input validation
     * Get purchase order object by poId
     * Check if purchase order exists or not
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!params.poId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            long poId = 0L
            // check input validation
            try {
                poId = Long.parseLong(params.poId.toString())
            } catch (Exception e) {
                result.put(Tools.MESSAGE, INVALID_PO)
                return result
            }
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            // get purchase order object by poId
            Object purchaseOrder = procurementImplService.readPO(poId)
            // check if purchase order exists or not
            if ((!purchaseOrder) || (purchaseOrder.companyId != companyId)) {
                result.put(Tools.MESSAGE, PO_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Check required parameters from UI
     * Get purchase order object by poId
     * Get supplier object by purchaseOrder.supplierId
     * Get list of items of purchase order by poId
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameter
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap) // initialize parameters for flexGrid
            long poId = Long.parseLong(parameterMap.poId.toString())
            // get purchase order object by poId
            Object purchaseOrder = procurementImplService.readPO(poId)
            // get supplier object by purchaseOrder.supplierId
            Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrder.supplierId)
            // get list and count of items of purchase order by poId
            Map serviceMap = getPoItemReceivedList(poId)
            List<GroovyRowResult> lstPoItemReceived = serviceMap.lstPoItemReceived
            int count = serviceMap.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, PO_ITEM_LIST_NOT_FOUND + poId)
                return result
            }

            result.put(LST_PO_ITEM_RECEIVED, lstPoItemReceived)
            result.put(SUPPLIER_NAME, supplier.name)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Wrap list of received items of PO for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List lstPoItemReceived = (List) executeResult.get(LST_PO_ITEM_RECEIVED)
            int count = (int) executeResult.get(Tools.COUNT)
            // wrap received item list of PO
            List lstWrappedPoItemReceived = wrapPoItemReceivedList(lstPoItemReceived, start)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedPoItemReceived]
            result.put(LST_PO_ITEM_RECEIVED, gridOutput)
            result.put(SUPPLIER_NAME, executeResult.get(SUPPLIER_NAME))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of received items in grid entity
     * @param lstPoItemReceived -list of received items
     * @param start -starting index of the page
     * @return -list of wrapped received items
     */
    private List wrapPoItemReceivedList(List<GroovyRowResult> lstPoItemReceived, int start) {
        List lstWrappedPoItemReceived = []
        int counter = start + 1
        GroovyRowResult singleRow
        GridEntity obj
        for (int i = 0; i < lstPoItemReceived.size(); i++) {
            singleRow = lstPoItemReceived[i]
            obj = new GridEntity()
            obj.id = singleRow.chalan_no
            obj.cell = [
                    counter,
                    singleRow.chalan_no,
                    singleRow.supplier_chalan,
                    singleRow.item_name,
                    DateUtility.getLongDateForUI(singleRow.transaction_date),
                    singleRow.actual_quantity + Tools.SINGLE_SPACE + singleRow.unit,
                    singleRow.rate,
                    singleRow.amount,
                    singleRow.approved_by > 0 ? Tools.YES : Tools.NO,
                    singleRow.acknowledged_by
            ]
            lstWrappedPoItemReceived << obj
            counter++
        }
        return lstWrappedPoItemReceived
    }

    private static final String COUNT_QUERY = """
                SELECT COUNT(iitd.id) AS count
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                WHERE iit.transaction_entity_type_id =:transactionEntityTypeId AND
                      iit.transaction_id =:poId AND
                      iitd.is_current = TRUE
    """

    /**
     * Get received items list by specific purchase order id
     * @param poId -id of purchase order
     * @return -a map containing list and count of received items
     */
    private Map getPoItemReceivedList(long poId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        String queryStr = """
               SELECT iitd.id AS chalan_no, iitd.supplier_chalan AS supplier_chalan, item.name AS item_name, item.unit, iitd.transaction_date,
                      to_char(iitd.actual_quantity,'${Tools.DB_QUANTITY_FORMAT}') AS actual_quantity,
                      to_char(iitd.rate,'${Tools.DB_CURRENCY_FORMAT}') AS rate,
                      to_char((iitd.actual_quantity*iitd.rate),'${Tools.DB_CURRENCY_FORMAT}') AS amount,
                      iitd.approved_by, au.username AS acknowledged_by
               FROM inv_inventory_transaction_details iitd
                  LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                  LEFT JOIN item  ON item.id = iitd.item_id
                  LEFT JOIN app_user au ON au.id = iitd.invoice_acknowledged_by
               WHERE iit.transaction_entity_type_id =:transactionEntityTypeId AND
                     iit.transaction_id =:poId AND
                     iitd.is_current = TRUE
               ORDER BY iitd.id
               LIMIT :resultPerPage OFFSET :start
        """

        Map queryParams = [
                transactionEntityTypeId: transactionEntitySupplier.id,
                poId: poId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstPoItemReceived = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> poItemReceivedCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) poItemReceivedCount[0].count

        return [lstPoItemReceived: lstPoItemReceived, count: count]
    }
}

