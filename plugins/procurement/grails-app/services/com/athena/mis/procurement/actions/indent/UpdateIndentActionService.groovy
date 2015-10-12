package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update indent object and grid data
 *  For details go through Use-Case doc named 'UpdateIndentActionService'
 */
class UpdateIndentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    IndentService indentService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String UPDATE_FAILURE_MSG = "Indent has not been updated."
    private static final String UPDATE_SUCCESS_MSG = "Indent has been successfully updated!"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to update indent"
    private static final String APPROVED_MESSAGE = "Indent is approved"
    private static final String INDENT = "indent"
    private static final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " Purchase request already created using this indent"
    private static final String HAS_ASSOCIATION_MESSAGE_IOU_SLIP = " IOU Slip already created using this indent"

    /**
     * Get parameters from UI and build indent object for update
     * 1. pull previous old object
     * 2. check different association
     * @param parameters -N/A
     * @param obj -get indent object from controller
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            ProcIndent indent = (ProcIndent) obj
            ProcIndent oldIndent = (ProcIndent) indentService.read(indent.id)

            if (oldIndent.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_MESSAGE)
                return result
            }

            Map associationResult = (Map) hasAssociation(oldIndent)
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }

            //For showing on Grid
            indent.itemCount = oldIndent.itemCount
            indent.totalPrice = oldIndent.totalPrice
            indent.createdBy = oldIndent.createdBy
            indent.createdOn = oldIndent.createdOn

            result.put(INDENT, indent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Update indent object in DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -object receive from pre execute method
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            ProcIndent indent = (ProcIndent) preResult.get(INDENT)
            int updateIndent = indentService.update(indent)
            result.put(INDENT, indent)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Show updated indent object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            String projectName
            Map executeResult = (Map) obj
            GridEntity object = new GridEntity()

            ProcIndent indent = (ProcIndent) executeResult.get(INDENT)
            Project project = (Project) projectCacheUtility.read(indent.projectId)

            object.id = indent.id
            String fromDate = DateUtility.getLongDateForUI(indent.fromDate)
            String toDate = DateUtility.getLongDateForUI(indent.toDate)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(indent.createdBy)
            object.cell = [
                    Tools.LABEL_NEW,
                    indent.id,
                    project.name,
                    fromDate,
                    toDate,
                    indent.itemCount,
                    Tools.NO,
                    Tools.formatAmountWithoutCurrency(indent.totalPrice),
                    createdBy.username
            ]
            Map resultMap = [entity: object, version: indent.version]
            result.put(INDENT, resultMap)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MSG)
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
     * Check association of indent with relevant domains
     * @param indent -indent object
     * @return -a map containing isError(true/false) depending on association and relevant message
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
     * Count purchase request-indent association
     * @param indentId - indent id
     * @return - total associated number
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
     * Count iou slip-indent association
     * @param indentId - indent id
     * @return - total associated number
     */
    private int countIOUSlip(long indentId) {
        List results = executeSelectSql(QUERY_COUNT_IOU_SLIP, [indentId: indentId]);
        int count = results[0].count;
        return count;
    }
}