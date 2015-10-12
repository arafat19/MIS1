package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Delete indent from DB as well as from grid.
 * For details go through Use-Case doc named 'DeleteIndentActionService'
 */
class DeleteIndentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    IndentService indentService

    private static final String DELETE_SUCCESS_MSG = "Indent has been successfully deleted!"
    private static final String DELETE_FAILURE_MSG = "Sorry! Indent has not been deleted."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete Indent"
    private static final String ITEM_ASSOCIATION_MESSAGE = " Item associated with this Indent"
    private static final String APPROVED_MESSAGE = "Approved indent can not be deleted."
    private static final String DELETED = "deleted"
    private static final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " Purchase request already created using this indent"
    private static final String HAS_ASSOCIATION_MESSAGE_IOU_SLIP = " IOU Slip already created using this indent"

    /**
     * Check pre-conditions
     * 1. pull indent object
     * 2. check indent existence
     * 3. check approve status
     * 4. check indent-item association
     * 5. check association with PR & IOU Slip
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(True/False) and relative msg
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long indentId = Long.parseLong(params.id.toString())
            ProcIndent indent = (ProcIndent) indentService.read(indentId)

            if (!indent) {                  // check indent existence
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            if (indent.approvedBy > 0) {         // check indent approval
                result.put(Tools.MESSAGE, APPROVED_MESSAGE)
                return result
            }

            if (indent.itemCount > 0) {        // check indent-item association
                result.put(Tools.MESSAGE, indent.itemCount + ITEM_ASSOCIATION_MESSAGE)
                return result
            }

            Map associationResult = (Map) hasAssociation(indent)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

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
     * Delete indent from DB
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - success or failure message depending on execution
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long indentId = Long.parseLong(parameterMap.id.toString())
            int deleteCount = indentService.delete(indentId)
            if (deleteCount < 1) {
                result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
                return result
            }
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
        Map result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
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
    private LinkedHashMap hasAssociation(ProcIndent indent) {
        LinkedHashMap result = new LinkedHashMap()
        long indentId = indent.id;
        int count = 0;
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE);

        count = countPurchaseRequest(indentId);
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            count = countIOUSlip(indentId);
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_IOU_SLIP)
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
     * Count indent-purchase request association
     * @param indentId - indent id
     * @return - total association number
     */
    private int countPurchaseRequest(long indentId) {
        List results = executeSelectSql(QUERY_COUNT_PR, [indentId: indentId]);
        int count = results[0].count;
        return count;
    }

    private static final String QUERY_COUNT_IOU_SLIP = """
                                        SELECT COUNT(id) AS count
                                              FROM acc_iou_slip
                                        WHERE indent_id = :indentId
                                        """
    /**
     * Count indent-iou slip association
     * @param indentId - indent id
     * @return - total association number
     */
    private int countIOUSlip(long indentId) {
        List results = executeSelectSql(QUERY_COUNT_IOU_SLIP, [indentId: indentId]);
        int count = results[0].count;
        return count;
    }
}
