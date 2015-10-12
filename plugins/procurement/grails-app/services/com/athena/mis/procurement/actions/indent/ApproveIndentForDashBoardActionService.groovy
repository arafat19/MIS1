package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Approval of Indent for showing in the Dash Board
 * For details go through Use-Case doc named 'ApproveIndentForDashBoardActionService'
 */
class ApproveIndentForDashBoardActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String INDENT_SAVE_SUCCESS_MESSAGE = "Indent has been approved successfully"
    private static final String INDENT_SAVE_FAILURE_MESSAGE = "Indent could not be approved"
    private static final String INDENT_NOT_FOUND = "Indent not found."
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Fail to approve due to Invalid Input"
    private static final String INDENT_NO_ITEM = "Indent has no item(s)"
    private static final String ALREADY_APPROVED = "Selected indent is already approved"
    private static final String INDENT_OBJ = "indentInstance"
    private static final String SERVER_ERROR_MESSAGE = "Fail to approve Indent"

    IndentService indentService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * Get parameters from UI and get indent object from DB
     * 1. pull indent object
     * 2. check different validations
     * @Params parameters -serialized parameters from UI
     * @Params obj -N/A
     * @Return -Map containing isError(true/false) & indent object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            ProcIndent indent = ProcIndent.read(id)
            //checking purchaseOrder Instance existence
            if (!indent) {
                result.put(Tools.MESSAGE, INDENT_NOT_FOUND)
                return result
            }

            if (indent.approvedBy > 0) {   // check indent approval
                result.put(Tools.MESSAGE, ALREADY_APPROVED)
                return result
            }
            if (indent.itemCount <= 0) {   // check indent-item association
                result.put(Tools.MESSAGE, INDENT_NO_ITEM)
                return result
            }

            result.put(INDENT_OBJ, indent)
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
     * Update ProcIndent for approval
     * 1. set logging user id as approved-by field value
     * 2. update proc_indent
     * @param parameters- N/A
     * @param obj-map from pre-execute method
     * @return- a map containing indent object and relevant msg(success/failure)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from pre-execute method

            ProcIndent indent = (ProcIndent) preResult.get(INDENT_OBJ)

            indent.approvedBy = procSessionUtil.appSessionUtil.getAppUser().id      // set logging user id as approved-by field value

            int indentUpdateStatus = updateForApproval(indent)         // update indent
            if (indentUpdateStatus <= 0) {
                result.put(Tools.MESSAGE, INDENT_SAVE_FAILURE_MESSAGE)
                return result
            }
            result.put(INDENT_OBJ, indent)
            result.put(Tools.MESSAGE, INDENT_SAVE_SUCCESS_MESSAGE)
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
     * 1. receive indent instance
     * 2. pull project object
     * 3. Show newly created indent object in grid
     * 4. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj

            result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
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
                result.put(Tools.MESSAGE, INDENT_SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * SQL query for update
     */
    private static final String UPDATE_QUERY = """
                      UPDATE proc_indent
                          SET
                            approved_by = :approvedBy,
                            version = :newVersion
                      WHERE
                          id=:id AND
                          version=:version
                    """
    /**
     * Update ProcIndent for approval
     * @param indent- received indent object from execute method
     * @return -int value for update success(e.g- 1 for success) or throw exception for failure
     */
    private int updateForApproval(ProcIndent indent) {

        Map queryParams = [
                id:indent.id,
                version: indent.version,
                newVersion: indent.version + 1,
                approvedBy: indent.approvedBy
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount > 0) {
            return updateCount;
        } else
            throw new RuntimeException('Error occurred while approving ProcIndent')
    }
}
