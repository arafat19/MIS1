package com.athena.mis.inventory.actions.report.invsupplierchalan

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Search and show supplier chalan list of a supplier
 *  For details go through Use-Case doc named 'InvListForSupplierChalanActionService'
 */
class InvListForSupplierChalanActionService extends BaseService implements ActionIntf {

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate supplier chalan report"
    private static final String LST_SUPPLIER_CHALAN = "lstSupplierChalan"
    private static final String CHALAN_NOT_FOUND = "Supplier chalan not found with chalan no : "

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check required parameters from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!params.chalanNo) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
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
     * Check and get required parameters from UI
     * Get list and count of supplier chalan by supplierId
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            // check required parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap) // initialize parameters for flexGrid
            List<GroovyRowResult> searchReturnList = []
            List<GroovyRowResult> searchReturnCount
            String chalanNo = parameterMap.chalanNo.toString()
            long supplierId = Long.parseLong(parameterMap.supplierId)
            int stat = Integer.parseInt(parameterMap.status)
            int count = 0
            // get list and count of supplier chalan by specific supplierId
            if (stat == -1 || stat == 0) {
                searchReturnList = searchListForAcknowledgedOrAll(chalanNo, supplierId, stat)
                searchReturnCount = searchCountForAcknowledgedOrAll(chalanNo, supplierId, stat)
                count = (int) searchReturnCount[0][0]
            } else if (stat == 1) {
                searchReturnList = searchListForPending(chalanNo, supplierId)
                searchReturnCount = searchCountForPending(chalanNo, supplierId)
                count = (int) searchReturnCount[0][0]
            }
            if (count <= 0) {
                result.put(Tools.MESSAGE, CHALAN_NOT_FOUND + chalanNo)
                return result
            }
            result.put(LST_SUPPLIER_CHALAN, searchReturnList)
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
     * Wrap list of supplier chalan for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List lstSupplierChalan = (List) executeResult.get(LST_SUPPLIER_CHALAN)
            int count = (int) executeResult.get(Tools.COUNT)
            // wrap supplier chalan list for grid view
            List lstWrappedSupplierChalan = wrapSupplierChalanList(lstSupplierChalan, start)
            Map gridOutput = [page: pageNumber, total: count, rows: lstWrappedSupplierChalan]
            result.put(LST_SUPPLIER_CHALAN, gridOutput)
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
     * Wrap list of supplier chalan in grid entity
     * @param lstSupplierChalan -list of supplier chalan
     * @param start -starting index of the page
     * @return -list of wrapped supplier chalan
     */
    private List wrapSupplierChalanList(List<GroovyRowResult> lstSupplierChalan, int start) {
        List lstWrappedSupplierChalan = []
        int counter = start + 1
        GroovyRowResult supplierChalan
        GridEntity obj
        for (int i = 0; i < lstSupplierChalan.size(); i++) {
            supplierChalan = lstSupplierChalan[i]
            obj = new GridEntity()
            obj.id = supplierChalan.id
            obj.cell = [
                    counter,
                    supplierChalan.po_id,
                    supplierChalan.transaction_date,
                    supplierChalan.inventory_name,
                    supplierChalan.item_name,
                    supplierChalan.str_quantity,
                    supplierChalan.str_rate,
                    supplierChalan.str_amount,
                    supplierChalan.approved_by > 0 ? Tools.YES : Tools.NO,
                    supplierChalan.invoice_acknowledged_by > 0 ? Tools.YES : Tools.NO
            ]
            lstWrappedSupplierChalan << obj
            counter++
        }
        return lstWrappedSupplierChalan
    }

    /**
     * Get list of acknowledged or all supplier chalan by supplierId and chalan no
     * @param chalanNo -supplier chalan no
     * @param supplierId -id of supplier
     * @param status -status of supplier chalan (pending/acknowledged/all)
     * @return -a list of supplier chalan
     */
    private List searchListForAcknowledgedOrAll(String chalanNo, long supplierId, int status) {
        String queryStr = """
            SELECT iitd.id, se.key ||' : '|| inv.name AS inventory_name, item.name AS item_name,
                   iitd.approved_by AS approved_by, iitd.invoice_acknowledged_by, iit.transaction_id AS po_id,
                   to_char(iitd.transaction_date,'dd-Mon-yyyy') AS transaction_date,
                   to_char(COALESCE(iitd.actual_quantity,0),'${Tools.DB_QUANTITY_FORMAT}') ||' ' || item.unit AS str_quantity,
                   to_char(COALESCE(iitd.rate,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_rate,
                   to_char((COALESCE(iitd.actual_quantity,0)*COALESCE(iitd.rate,0)),'${Tools.DB_CURRENCY_FORMAT}') AS str_amount
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
            LEFT JOIN inv_inventory inv ON inv.id = iitd.inventory_id
            LEFT JOIN system_entity se ON se.id = iitd.inventory_type_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.supplier_chalan = '${chalanNo}' AND
                  supplier.id =:supplierId AND
                  iitd.invoice_acknowledged_by > :status AND
                  iitd.is_current = TRUE
            ORDER BY iitd.transaction_date, item.name
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                supplierId: supplierId,
                status: status,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstSupplierChalan = executeSelectSql(queryStr, queryParams)
        return lstSupplierChalan
    }

    /**
     * Get list of pending supplier chalan by supplierId and chalan no
     * @param chalanNo -supplier chalan no
     * @param supplierId -id of supplier
     * @return -a list of supplier chalan
     */
    private List searchListForPending(String chalanNo, long supplierId) {
        String queryStr = """
            SELECT iitd.id, se.key ||' : '|| inv.name AS inventory_name, item.name AS item_name,
                   iitd.approved_by AS approved_by, iitd.invoice_acknowledged_by, iit.transaction_id AS po_id,
                   to_char(iitd.transaction_date,'dd-Mon-yyyy') AS transaction_date,
                   to_char(COALESCE(iitd.actual_quantity,0),'${Tools.DB_QUANTITY_FORMAT}') ||' ' || item.unit AS str_quantity,
                   to_char(COALESCE(iitd.rate,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_rate,
                   to_char((COALESCE(iitd.actual_quantity,0)*COALESCE(iitd.rate,0)),'${Tools.DB_CURRENCY_FORMAT}') AS str_amount
            FROM inv_inventory_transaction_details iitd
            LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
            LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
            LEFT JOIN inv_inventory inv ON inv.id = iitd.inventory_id
            LEFT JOIN system_entity se ON se.id = iitd.inventory_type_id
            LEFT JOIN item ON item.id = iitd.item_id
            WHERE iitd.supplier_chalan = '${chalanNo}' AND
                  supplier.id =:supplierId AND
                  iitd.invoice_acknowledged_by = 0 AND
                  iitd.is_current = TRUE
            ORDER BY iitd.transaction_date, item.name
            LIMIT :resultPerPage OFFSET :start
        """
        Map queryParams = [
                supplierId: supplierId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> lstSupplierChalan = executeSelectSql(queryStr, queryParams)
        return lstSupplierChalan
    }

    /**
     * Get count of acknowledged or all supplier chalan by supplierId and chalan no
     * @param chalanNo -supplier chalan no
     * @param supplierId -id of supplier
     * @param status -status of supplier chalan (pending/acknowledged/all)
     * @return -a list containing the count of supplier chalan
     */
    private List searchCountForAcknowledgedOrAll(String chalanNo, long supplierId, int status) {
        String queryCount = """
                SELECT COUNT(item_id)
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
                WHERE iitd.supplier_chalan = '${chalanNo}' AND
                      supplier.id =:supplierId AND
                      iitd.invoice_acknowledged_by > :status AND
                      iitd.is_current = TRUE
        """

        Map queryParams = [
                supplierId: supplierId,
                status: status
        ]
        List<GroovyRowResult> supplierChalanCount = executeSelectSql(queryCount, queryParams)
        return supplierChalanCount
    }

    /**
     * Get count of pending supplier chalan by supplierId and chalan no
     * @param chalanNo -supplier chalan no
     * @param supplierId -id of supplier
     * @return -a list containing the count of supplier chalan
     */
    private List searchCountForPending(String chalanNo, long supplierId) {
        String queryCount = """
                SELECT COUNT(item_id)
                FROM inv_inventory_transaction_details iitd
                LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
                LEFT JOIN supplier ON supplier.id = iit.transaction_entity_id
                WHERE iitd.supplier_chalan = '${chalanNo}' AND
                      supplier.id =:supplierId AND
                      iitd.invoice_acknowledged_by = 0 AND
                      iitd.is_current = TRUE
        """

        Map queryParams = [
                supplierId: supplierId
        ]
        List<GroovyRowResult> supplierChalanCount = executeSelectSql(queryCount, queryParams)
        return supplierChalanCount
    }
}
