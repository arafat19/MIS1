package com.athena.mis.procurement.actions.indentdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.procurement.service.IndentDetailsService
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Delete indent details from DB as well as from grid.
 * For details go through Use-Case doc named 'DeleteIndentDetailsActionService'
 */
class DeleteIndentDetailsActionService extends BaseService implements ActionIntf {

    private static final String DELETE_SUCCESS_MSG = "Indent details has been successfully deleted!"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Item"
    private static final String APPROVED_MESSAGE = "Approved indent can not be deleted."
    private static final String DELETED = "deleted"
    private static final String INDENT_DETAILS_OBJ = "indentDetailsObj"
    private static final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " Purchase request already created with this indent details"
    private static final String HAS_ASSOCIATION_MESSAGE_IOU_PURPOSE = " IOU purpose(s) is associated with this indent details"

    IndentService indentService
    IndentDetailsService indentDetailsService

    private final Logger log = Logger.getLogger(getClass())

    /**
     * 1. pull indent details object by indent id
     * 2. pull indent object
     * 3. check indent approval
     * 4. check association with pr & iou slip
     * @param params - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing indent details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long indentDetailsId = Long.parseLong(params.id.toString())

            ProcIndentDetails indentDetails = (ProcIndentDetails) indentDetailsService.read(indentDetailsId)
            if (!indentDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            ProcIndent indent = indentService.read(indentDetails.indentId)
            if (indent.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_MESSAGE)
                return result
            }

            Map associationResult = (Map) hasAssociation(indentDetails)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

            result.put(INDENT_DETAILS_OBJ, indentDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * 1. receive indent details object
     * 2. delete indent details from DB
     * 3. update item count
     * @param params - N/A
     * @param obj - receive from pre execute method
     * @return - isError(True/False)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            ProcIndentDetails indentDetails = (ProcIndentDetails) preResult.get(INDENT_DETAILS_OBJ)

            indentDetailsService.delete(indentDetails.id)
            decreaseItemCount(indentDetails)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Fail to delete Indent Details")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Set delete operation True
     * @param obj- N/A
     * @return - a map containing deleted=True and success msg
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Check association with PR & IOU Slip
     * @param indent - indent object
     * @return - has-association message
     */
    private LinkedHashMap hasAssociation(ProcIndentDetails indentDetails) {
        LinkedHashMap result = new LinkedHashMap()
        long indentId = indentDetails.indentId;

        int count = 0;
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE);

        count = countPurchaseRequest(indentId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            count = countIOUPurpose(indentDetails.id)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_IOU_PURPOSE)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result;
    }

    private static final String QUERY_COUNT_PR = """
                                        SELECT COUNT(id) AS count
                                             FROM proc_purchase_request
                                        WHERE indent_id = :indentId
                                        """
    /**
     * @param indentId - indent id
     * @return - total purchase request number
     */
    private int countPurchaseRequest(long indentId) {
        List results = executeSelectSql(QUERY_COUNT_PR, [indentId: indentId]);
        int count = results[0].count;
        return count;
    }

    private static final String QUERY_COUNT_IOU_PURPOSE = """
                                        SELECT COUNT(id) AS count
                                             FROM acc_iou_purpose
                                        WHERE indent_details_id = :indentDetailsId
                                """
    /**
     * @param indentDetailsId - indent details id
     * @return - total iou purpose number
     */
    private int countIOUPurpose(long indentDetailsId) {
        List results = executeSelectSql(QUERY_COUNT_IOU_PURPOSE, [indentDetailsId: indentDetailsId]);
        int count = results[0].count;
        return count;
    }

    // On create ProcIndent-Details decrease the count of Item/Item
    private static final String QUERY_DECREASE = """
                      UPDATE proc_indent SET
                          item_count = item_count - 1,
                          total_price = total_price - :indentDetailsPrice,
                          version= version + 1
                      WHERE
                          id=:id
                     """
    /**
     * Decrease item count
     * @param indentDetails - indent details object
     * @return -int value(e.g- 1 for success, 2 for failure update)
     */
    private int decreaseItemCount(ProcIndentDetails indentDetails) {

        Map queryParams = [
                id: indentDetails.indentId,
                indentDetailsPrice: indentDetails.quantity * indentDetails.rate
        ]
        int updateCount = executeUpdateSql(QUERY_DECREASE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update indent')
        }
        return updateCount
    }
}