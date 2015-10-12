package com.athena.mis.procurement.actions.indentdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.procurement.entity.ProcIndent
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.procurement.service.IndentDetailsService
import com.athena.mis.procurement.service.IndentService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update indent details object and grid data
 *  For details go through Use-Case doc named 'UpdateIndentDetailsActionService'
 */
class UpdateIndentDetailsActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_SUCCESS_MESSAGE = "Indent details has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Failed to update indent details"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String INDENT_DETAILS_OBJ = "indentDetailsObj"
    private static final String INDENT_OBJ = "indentObj"
    private static final String INDENT_DETAILS_NOT_FOUND = "Indent details not found. Please refresh and try again"
    private static final String APPROVED_INDENT_MSG = "Approved indent can't be updated"

    IndentDetailsService indentDetailsService
    IndentService indentService
    @Autowired
    ProcSessionUtil procSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build indent details object for update
     * 1. pull previous old object
     * 2. pull indent object
     * 3. check for approval
     * 4. build indent details object
     * 5. change totalPrice of indent
     * @param parameters -N/A
     * @param obj -get indent object from controller
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if ((!parameterMap.id) || (!parameterMap.version) || (!parameterMap.quantity) || (!parameterMap.itemDescription)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long id = Long.parseLong(parameterMap.id.toString())
            ProcIndentDetails oldIndentDetails = indentDetailsService.read(id)
            if (!oldIndentDetails) {
                result.put(Tools.MESSAGE, INDENT_DETAILS_NOT_FOUND)
                return result
            }

            long version = Long.parseLong(parameterMap.version.toString())
            if (oldIndentDetails.version != version) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            ProcIndent indent = indentService.read(oldIndentDetails.indentId)
            if (indent.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_INDENT_MSG)
                return result
            }
            ProcIndentDetails newIndentDetails = buildIndentDetails(parameterMap, oldIndentDetails)

            // now change totalPrice of indent
            indent.totalPrice = indent.totalPrice - (oldIndentDetails.quantity * oldIndentDetails.rate) + (newIndentDetails.quantity * newIndentDetails.rate)

            result.put(INDENT_DETAILS_OBJ, newIndentDetails)
            result.put(INDENT_OBJ, indent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * 1. Update indent details object in DB
     * 2. update total price
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -object receive from pre execute method
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcIndentDetails indentDetails = (ProcIndentDetails) preResult.get(INDENT_DETAILS_OBJ)
            ProcIndent indent = (ProcIndent) preResult.get(INDENT_OBJ)
            int updateStatus = indentDetailsService.update(indentDetails)
            updateTotalPrice(indent)
            result.put(INDENT_DETAILS_OBJ, indentDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Fail to update ProcIndent Details")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Show updated indent details object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GridEntity object = new GridEntity()
            ProcIndentDetails indentDetails = (ProcIndentDetails) receiveResult.get(INDENT_DETAILS_OBJ)
            object.id = indentDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    indentDetails.itemDescription,
                    Tools.formatAmountWithoutCurrency(indentDetails.quantity) + Tools.SINGLE_SPACE + indentDetails.unit,
                    Tools.makeAmountWithThousandSeparator(indentDetails.rate),
                    Tools.makeAmountWithThousandSeparator(indentDetails.quantity * indentDetails.rate)
            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * @param parameterMap - serialize parameters from UI
     * @param oldIndentDetails - previous state of indent details
     * @return - new indent datails object
     */
    private ProcIndentDetails buildIndentDetails(GrailsParameterMap parameterMap, ProcIndentDetails oldIndentDetails) {
        ProcIndentDetails indentDetails = new ProcIndentDetails(parameterMap)
        AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()

        indentDetails.id = oldIndentDetails.id
        indentDetails.version = oldIndentDetails.version
        indentDetails.itemDescription = parameterMap.itemDescription.toString()
        indentDetails.unit = parameterMap.unit.toString()
        indentDetails.updatedOn = new Date()
        indentDetails.updatedBy = systemUser.id
        indentDetails.createdOn = oldIndentDetails.createdOn
        indentDetails.createdBy = oldIndentDetails.createdBy

        indentDetails.rate = 0.0d
        if (parameterMap.rate.toString().length() > 0) {
            try {
                indentDetails.rate = Double.parseDouble(parameterMap.rate.toString())
            } catch (Exception e) {
                indentDetails.rate = 0.0d
            }
        } else {
            indentDetails.rate = 0.0d
        }
        return indentDetails
    }

    // On update indent details update indent
    private static final String UPDATE_QUERY = """
                      UPDATE proc_indent SET
                          total_price = :totalPrice,
                          version = version + 1
                      WHERE
                          id=:id
                       """
    /**
     * Update total price of indent
     * @param indent - indent object
     * @return - 1 for success, 0 for failure
     */
    private int updateTotalPrice(ProcIndent indent) {
        Map queryParams = [id: indent.id, totalPrice: indent.totalPrice]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update indent')
        }
        return updateCount
    }
}
