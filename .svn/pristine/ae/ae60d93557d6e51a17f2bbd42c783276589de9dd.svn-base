package com.athena.mis.inventory.actions.report.invsupplierchalan

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Acknowledge suppler chalan
 * For details go through Use-Case doc named 'AcknowledgeInvoiceFromSupplierActionService'
 */
class AcknowledgeInvoiceFromSupplierActionService extends BaseService implements ActionIntf {

    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String NOT_APPROVED = "Chalan is not approved"
    private static final String ALREADY_ACKNOWLEDGED = "Chalan is already acknowledged"
    private static final String FAILURE_MSG = "Failed to acknowledge chalan"
    private static final String SUCCESS_MSG = "Chalan has been received successfully"
    private static final String INV_DETAILS_OBJ = "invDetailsObj"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get inventory transaction details object by id
     * Check if transaction details object is approved or not
     * Check if transaction details object is already acknowledged or not
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long transactionDetailsId = Long.parseLong(params.id)
            // get inventory transaction details object
            InvInventoryTransactionDetails invTransactionDetailsObj = invInventoryTransactionDetailsService.read(transactionDetailsId)
            // check if transaction details object is approved or not
            if (invTransactionDetailsObj.approvedBy <= 0) {
                result.put(Tools.MESSAGE, NOT_APPROVED)
                return result
            }
            // check if transaction details object is already acknowledged or not
            if (invTransactionDetailsObj.invoiceAcknowledgedBy > 0) {
                result.put(Tools.MESSAGE, ALREADY_ACKNOWLEDGED)
                return result
            }
            result.put(INV_DETAILS_OBJ, invTransactionDetailsObj)
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
     * Get inventory transaction details object from executePreCondition
     * Acknowledge supplier chalan and update related properties
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            InvInventoryTransactionDetails invTransactionDetailsObj = (InvInventoryTransactionDetails) preResult.get(INV_DETAILS_OBJ)
            // acknowledge supplier chalan
            int updateCount = acknowledgeSupplierChalan(invTransactionDetailsObj)
            if (updateCount <= 0) {
                result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with success message and isError false
     * @param obj -N/A
     * @return -a map containing success message for show
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        result.put(Tools.MESSAGE, SUCCESS_MSG)
        return result
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

    private static final String UPDATE_QUERY = """
            UPDATE inv_inventory_transaction_details
                SET
                    version=version+1,
                    invoice_acknowledged_by =:acknowledgedBy,
                    updated_by =:updatedBy,
                    updated_on =:updatedOn
            WHERE
                id =:id AND
                version =:version
    """

    /**
     * Acknowledge supplier chalan and update related properties
     * @param invTransactionDetailsObj -object of InvInventoryTransactionDetails
     * @return -an integer containing the value of update count
     */
    private int acknowledgeSupplierChalan(InvInventoryTransactionDetails invTransactionDetailsObj) {
        Map queryParams = [
                acknowledgedBy: invSessionUtil.appSessionUtil.getAppUser().id,
                updatedBy: invSessionUtil.appSessionUtil.getAppUser().id,
                updatedOn: DateUtility.getSqlDateWithSeconds(new Date()),
                id: invTransactionDetailsObj.id,
                version: invTransactionDetailsObj.version
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        return updateCount
    }
}



