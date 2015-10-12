package com.athena.mis.procurement.actions.indent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.AppMailService
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
 * Create Indent and show in the grid
 * For details go through Use-Case doc named 'CreateIndentActionService'
 */
class CreateIndentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    IndentService indentService
    AppMailService appMailService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String TRANSACTION_CODE = "CreateIndentActionService"
    private static final String MAIL_SUBJECT_POSTFIX = "Indent No: "
    private static final String ERROR_SENDING_MAIL = "Indent has been created but notification mail not send"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Indent has been created but notification mail not send due to absence of mail template"
    private static final String MAIL_RECIPIENT_NOT_FOUND = "Indent has been created but notification mail not send due to absence of recipient"
    private static final String INDENT_CREATE_SUCCESS_MSG = "Indent has been created successfully."
    private static final String INDENT_CREATE_FAILURE_MSG = "Indent has not been created."
    private static final String ADD_PROJECT = "Add a project to create indent"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create indent"
    private static final String INDENT = "indent"

    /**
     * Get newly created indent object by parameters from UI from controller.
     * @param parameters- N/A
     * @param obj - received indent object from controller
     * @return - Map containing isError(true/false) and relevant msg & indent object
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            ProcIndent indent = (ProcIndent) obj
            if (indent.projectId <= 0) {
                result.put(Tools.MESSAGE, ADD_PROJECT)
                return result
            }
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
     * Save indent object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * -map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ProcIndent indent = (ProcIndent) preResult.get(INDENT)
            indent = indentService.create(indent)
            if (!indent) {
                result.put(Tools.MESSAGE, INDENT_CREATE_FAILURE_MSG)
                return result
            }
            result.put(INDENT, indent)
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
     * Send mail to proper authority for approval
     * 1. Get appMail object by transaction code
     * 2. Send mail for indent approval
     * @param parameters - N/A
     * @param obj- map returned from execute method
     * @return -map contains isError(true/false)& relative msg depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePostCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            ProcIndent indent = (ProcIndent) executeResult.get(INDENT)
            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, indent.companyId, true)
            if (!appMail) {      // check 'CreateIndentActionService' mail template
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }
            // build mail subject(e.g- 'MIS Notification for Indent:(Indent No: 520)' )
            appMail.subject = appMail.subject + Tools.PARENTHESIS_START + MAIL_SUBJECT_POSTFIX + indent.id + Tools.PARENTHESIS_END
            String createdOn = DateUtility.getDateTimeFormatAsString(indent.createdOn)      // format - "dd MMMM,yyyy [hh:mm a]"
            AppUser user = (AppUser) appUserCacheUtility.read(indent.createdBy)
            String createdBy = user.username
            Project project = (Project) projectCacheUtility.read(indent.projectId)
            Boolean mailSend = appMailService.sendMailForIndentApproval(project, appMail, indent.id, createdOn, createdBy)
            if (!mailSend) {
                result.put(Tools.MESSAGE, MAIL_RECIPIENT_NOT_FOUND)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            ProcIndent indent = (ProcIndent) executeResult.get(INDENT)
            Project project = (Project) projectCacheUtility.read(indent.projectId)
            GridEntity object = new GridEntity()
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
                    Tools.STR_ZERO_DECIMAL,
                    createdBy.username
            ]
            Map resultMap = [entity: object, version: indent.version]
            result.put(INDENT, resultMap)
            result.put(Tools.MESSAGE, INDENT_CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INDENT_CREATE_FAILURE_MSG)
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
}