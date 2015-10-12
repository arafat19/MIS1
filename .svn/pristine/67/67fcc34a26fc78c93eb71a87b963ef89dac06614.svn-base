package com.athena.mis.fixedasset.actions.fixedassetdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.fixedasset.entity.FxdFixedAssetDetails
import com.athena.mis.fixedasset.service.FixedAssetDetailsService
import com.athena.mis.fixedasset.service.FixedAssetTraceService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Fixed Asset Details and Fixed Asset Trace.
 * For details go through Use-Case doc named 'DeleteFixedAssetDetailsActionService'
 */
class DeleteFixedAssetDetailsActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    FixedAssetDetailsService fixedAssetDetailsService
    FixedAssetTraceService fixedAssetTraceService

    private static final String DELETE_SUCCESS_MESSAGE = "Fixed Asset Details has been deleted successfully"
    private static final String DELETE_FAILURE_MESSAGE = "Fixed Asset Details could not be deleted"
    private static final String FIXED_ASSET_DETAILS_NOT_FOUND = "Fixed Asset Details not found"
    private static final String FA_DETAILS_OBJ = "fixedAssetDetails"
    private static final String DELETED = "deleted"
    private static final String ASSOCIATION_MSG_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected fixed asset details"
    private static final String ASSOCIATION_MSG_FIXED_ASSET_TRACE = " fixed asset trace(s) associated with selected fixed asset details"
    private static final String ASSOCIATION_MSG_FIXED_ASSET_MAINTENANCE = " fixed asset maintenance(s) associated with selected fixed asset details"

    /**
     * 1. check fixed asset details existence
     * 2. association check with inventory transaction, fixed asset trace & fixed asset maintenance
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing fixed asset details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long id = Long.parseLong(params.id.toString())
            FxdFixedAssetDetails fixedAssetDetails = FxdFixedAssetDetails.read(id)
            if (!fixedAssetDetails) {
                result.put(Tools.MESSAGE, FIXED_ASSET_DETAILS_NOT_FOUND)
                return result
            }

            Map preResult = (Map) hasAssociation(fixedAssetDetails)

            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(FA_DETAILS_OBJ, fixedAssetDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. receive fixed asset details from pre method
     * 2. delete fixed asset details
     * 3. update proc_purchase_order_details for item count
     * @param params - N/A
     * @param obj - object receive from pre execute method
     * @return - a map containing isError(True/False)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            FxdFixedAssetDetails fixedAssetDetails = (FxdFixedAssetDetails) receiveResult.get(FA_DETAILS_OBJ)
            fixedAssetDetailsService.delete(fixedAssetDetails.id)
            decreaseFixedAssetDetailsCount(fixedAssetDetails.poId, fixedAssetDetails.itemId)
            fixedAssetTraceService.delete(fixedAssetDetails.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete Fixed Asset Details')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Set delete operation True
     * @param obj- N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            result.put(DELETED, Boolean.TRUE.booleanValue())
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. association check for inventory transaction
     * 2. association check for fixed asset trace
     * 3. association check for fixed asset maintenance
     * @param fixedAssetDetails - fixed asset details object
     * @return - relevant message
     */
    private LinkedHashMap hasAssociation(FxdFixedAssetDetails fixedAssetDetails) {
        LinkedHashMap result = new LinkedHashMap()
        long fixedAssetDetailsId = fixedAssetDetails.id
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        int count = 0

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            count = countInvTransactionDetails(fixedAssetDetailsId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + ASSOCIATION_MSG_INVENTORY_TRANSACTION)
                return result
            }
        }

        count = countFixedAssetTrace(fixedAssetDetailsId)
        if (count > 1) {
            result.put(Tools.MESSAGE, count.toString() + ASSOCIATION_MSG_FIXED_ASSET_TRACE)
            return result
        }

        count = countFixedAssetMaintenance(fixedAssetDetailsId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + ASSOCIATION_MSG_FIXED_ASSET_MAINTENANCE)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_COUNT_ITD = """
                        SELECT COUNT(id) AS count
                            FROM inv_inventory_transaction_details
                            WHERE fixed_asset_details_id = :fixedAssetDetailsId
                      """
    /**
     * @param fixedAssetDetailsId - fixed asset details id
     * @return - int value for fixed asset details-inventory transaction association
     */
    private int countInvTransactionDetails(long fixedAssetDetailsId) {
        List results = executeSelectSql(QUERY_COUNT_ITD, [fixedAssetDetailsId: fixedAssetDetailsId])
        int count = results[0].count
        return count
    }

    private static final String QUERY_COUNT_FAT = """
                        SELECT COUNT(id) AS count
                            FROM fxd_fixed_asset_trace
                            WHERE fixed_asset_details_id = :fixedAssetDetailsId
                      """
    /**
     * @param fixedAssetDetailsId - fixed asset details id
     * @return - int value for fixed asset details-fixed asset trace association
     */
    private int countFixedAssetTrace(long fixedAssetDetailsId) {
        List results = executeSelectSql(QUERY_COUNT_FAT, [fixedAssetDetailsId: fixedAssetDetailsId])
        int count = results[0].count
        return count
    }

    private static final String QUERY_COUNT_MAIN = """
                        SELECT COUNT(id) AS count
                            FROM fxd_maintenance
                            WHERE fixed_asset_details_id = :fixedAssetDetailsId
                      """
    /**
     * @param fixedAssetDetailsId - fixed asset details id
     * @return - int value for fixed asset details-fixed asset maintenance association
     */
    private int countFixedAssetMaintenance(long fixedAssetDetailsId) {
        List results = executeSelectSql(QUERY_COUNT_MAIN, [fixedAssetDetailsId: fixedAssetDetailsId])
        int count = results[0].count
        return count
    }

    //decrease fixed asset details count of purchase order details
    private static final String QUERY_DECREASE = """
                        UPDATE proc_purchase_order_details
                            SET
                                fixed_asset_details_count = fixed_asset_details_count - 1
                            WHERE purchase_order_id = :poId AND
                                  item_id = :itemId
                            """

    private int decreaseFixedAssetDetailsCount(long poId, long itemId) {
        Map queryParams = [
                poId: poId,
                itemId: itemId
        ]
        int updateCountPOD = executeUpdateSql(QUERY_DECREASE, queryParams)
        if (updateCountPOD <= 0) {
            throw new RuntimeException("Error occurred to update purchase order details")
        }
        return updateCountPOD
    }
}