package com.athena.mis.procurement.actions.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.UnitCacheUtility
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select specific object of selected pr at grid row and show for UI
 *  For details go through Use-Case doc named 'SelectPurchaseRequestActionService'
 */
class SelectPurchaseRequestActionService extends BaseService implements ActionIntf {

    private static final String PURCHASE_REQUEST_NOT_FOUND_MESSAGE = "Purchase request not found on server"
    private static final String SERVER_ERROR_MESSAGE = "Can't get purchase request"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred for invalid input"
    private static final String PURCHASE_REQUEST_OBJ = "purchaseRequest"
    private static final String INDENT_LIST = "indentList"

    private final Logger log = Logger.getLogger(getClass())

    PurchaseRequestService purchaseRequestService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object params, Object obj) {
        return null
    }
    /**
     * Get purchase request object by id
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            // check here for required params are present
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(id)
            if (purchaseRequest == null) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND_MESSAGE)
                return result
            }
            List<GroovyRowResult> lstIndent = listByProjectAndIndentId(purchaseRequest.projectId, purchaseRequest.indentId)

            result.put(INDENT_LIST, Tools.listForKendoDropdown(lstIndent,'indent_date', null))
            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * Show selected object on the form
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) receiveResult.get(PURCHASE_REQUEST_OBJ)
            List<GroovyRowResult> lstIndent = (List<GroovyRowResult>) receiveResult.get(INDENT_LIST)
            result.put(Tools.ENTITY, purchaseRequest)
            result.put(INDENT_LIST, lstIndent)
            result.put(Tools.VERSION, purchaseRequest.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    private static final String PROC_INDENT_SELECT_QUERY = """
                SELECT indent.id, indent.id || ' (From ' || to_char(from_date,'dd-Mon-yyyy') || ' To ' || to_char(to_date,'dd-Mon-yyyy')||')' AS indent_date
                    FROM proc_indent indent
                WHERE project_id =:projectId
                    AND indent.id IN(
                            SELECT id FROM proc_indent indt
                            WHERE project_id = :projectId
                            AND to_date >= :newDate
                            AND id <> :indentId
                            AND item_count > 0
                            AND approved_by > 0
                    )
                    OR indent.id = :indentId
                ORDER BY indent.id ASC
            """

    private List<GroovyRowResult> listByProjectAndIndentId(long projectId, long indentId) {
        Date newDate = DateUtility.getSqlDate(new Date())
        Map queryParams = [
                projectId: projectId,
                newDate: newDate,
                indentId: indentId
        ]
        List<GroovyRowResult> lstIntend = executeSelectSql(PROC_INDENT_SELECT_QUERY, queryParams)
        return lstIntend
    }
}
