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
 * Create Indent Details and show in the grid
 * For details go through Use-Case doc named 'CreateIndentDetailsActionService'
 */
class CreateIndentDetailsActionService extends BaseService implements ActionIntf {

    private static final String SAVE_SUCCESS_MESSAGE = "Indent details has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Failed to save Indent details"
    private static final String INPUT_VALIDATION_ERROR = "Error occurred for invalid input"
    private static final String APPROVED_INDENT_MSG = "Can't add new item in approved indent"
    private static final String INDENT_NOT_FOUND = "Indent not found"
    private static final String INDENT_DETAILS_OBJ = "indentDetailsObj"
    private static final String INDENT_OBJ = "indentObj"

    private final Logger log = Logger.getLogger(getClass())

    IndentService indentService
    IndentDetailsService indentDetailsService
    @Autowired
    ProcSessionUtil procSessionUtil

    /**
     * 1. pull indent details object by indent id
     * 2. check indent existence
     * 3. check indent approval
     * 4. build indent details object
     * @param params - serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing indent object & indent details object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            if ((!parameterMap.indentId)) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR)
                return result
            }

            long indentId = Long.parseLong(parameterMap.indentId.toString())
            ProcIndent indent = indentService.read(indentId)
            if (!indent) {
                result.put(Tools.MESSAGE, INDENT_NOT_FOUND)
                return result
            }

            if (indent.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_INDENT_MSG)
                return result
            }

            ProcIndentDetails indentDetails = buildIndentDetails(parameterMap, indent)

            result.put(INDENT_OBJ, indent)
            result.put(INDENT_DETAILS_OBJ, indentDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Create indent details
     * 1. receive indent details from pre execute method
     * 2. create new indent details
     * 3. update item count
     * @param parameters
     * @param obj
     * @return - a map containing indent details object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcIndentDetails indentDetails = (ProcIndentDetails) preResult.get(INDENT_DETAILS_OBJ)
            ProcIndentDetails newIndentDetails = indentDetailsService.create(indentDetails)
            int updateCount = increaseItemCount(newIndentDetails)

            result.put(INDENT_DETAILS_OBJ, newIndentDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException("Fail to create Indent Details")
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
     * 1. receive indent details instance
     * 2. Show newly created indent details object in grid
     * 3. Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
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
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Build indent details object
     * @param parameterMap - serialize parameters from UI
     * @param indent - indent object
     * @return - indent details object
     */
    private ProcIndentDetails buildIndentDetails(GrailsParameterMap parameterMap, ProcIndent indent) {
        ProcIndentDetails indentDetails = new ProcIndentDetails(parameterMap)
        AppUser systemUser = procSessionUtil.appSessionUtil.getAppUser()

        indentDetails.itemDescription = parameterMap.itemDescription.toString()
        indentDetails.unit = parameterMap.unit.toString()
        indentDetails.projectId = indent.projectId
        indentDetails.indentId = indent.id
        indentDetails.createdOn = new Date()
        indentDetails.createdBy = systemUser.id
        indentDetails.updatedOn = null
        indentDetails.updatedBy = 0
        indentDetails.companyId = procSessionUtil.appSessionUtil.getCompanyId()
        indentDetails.rate = 0.0d
        if (parameterMap.rate.toString().length() > 0) {
            try {
                indentDetails.rate = Double.parseDouble(parameterMap.rate)
            } catch (Exception e) {
                indentDetails.rate = 0.0d
            }
        } else {
            indentDetails.rate = 0.0d
        }

        return indentDetails
    }

    // On create ProcIndent-Details increase the count of Item/Item
    private static final String QUERY_INCREASE = """
                      UPDATE proc_indent SET
                          item_count = item_count + 1,
                          total_price = total_price + :indentDetailsPrice,
                          version = version + 1
                      WHERE
                          id=:id
                   """
    /**
     * @param indentDetails - indent details object
     * @return - int value(e.g- 1 for success, 2 for failure update)
     */
    private int increaseItemCount(ProcIndentDetails indentDetails) {
        Map queryParams = [
                id: indentDetails.indentId,
                indentDetailsPrice: indentDetails.quantity * indentDetails.rate
        ]

        int updateCount = executeUpdateSql(QUERY_INCREASE, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update indent')
        }
        return updateCount
    }
}
