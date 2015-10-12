package com.athena.mis.accounting.actions.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Send notification to Director or Project Director for approval of accIouSlip
 *  For details go through Use-Case doc named 'SelectAccIouSlipActionService'
 */
class SentNotificationAccIouSlipActionService extends BaseService implements ActionIntf {

    private static final String ACC_IOU_SLIP_APPROVE_SUCCESS_MESSAGE = "Notification of IOU slip has been sent successfully"
    private static final String NOT_FOUND_MESSAGE = "IOU slip not found"
    private static final String ACC_IOU_SLIP = "accIouSlip"
    private static final String NO_PURPOSE_ERROR = "Purpose not found in the IOU slip"
    private static final String ALREADY_SENT_ERROR = "IOU slip already sent for approval"
    private static final String TRANSACTION_CODE = "SentNotificationAccIouSlipActionService"
    private static final String ERROR_SENDING_MAIL = "IOU slip mail notification not send"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "IOU slip mail notification not send due to absence of mail template"
    private static final String MAIL_RECIPIENT_NOT_FOUND = "IOU slip mail notification not send due to absence of recipient"
    private static final String EMPLOYEE_OBJ = "employee"
    private static final String PROJECT_OBJ = "project"
    private static final String STR_TOTAL_AMOUNT = "strTotalAmount"

    private Logger log = Logger.getLogger(getClass())

    AppMailService appMailService
    AccIouSlipService accIouSlipService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    /**
     * Get parameters from UI
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long accIouSlipId = Long.parseLong(parameterMap.id.toString())
            AccIouSlip accIouSlip = accIouSlipService.read(accIouSlipId)
            // check whether the accIouSlip object exists or not
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            // check the selected iou slip is already approved or not
            if (accIouSlip.sentForApproval) {
                result.put(Tools.MESSAGE, ALREADY_SENT_ERROR)
                return result
            }

            // check association of accIouSlip with Iou Purpose
            if (accIouSlip.totalPurposeAmount <= 0) {
                result.put(Tools.MESSAGE, NO_PURPOSE_ERROR)
                return result
            }

            accIouSlip.sentForApproval = true

            result.put(ACC_IOU_SLIP, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
    }

    /**
     * Send mail to desired employee (e.g: Director/Project Director) about the accIouSlip
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccIouSlip accIouSlip = (AccIouSlip) receivedResult.get(ACC_IOU_SLIP)

            AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, accIouSlip.companyId, true)
            if (!appMail) {
                result.put(Tools.MESSAGE, MAIL_TEMPLATE_NOT_FOUND)
                return result
            }

            appMail.subject = appMail.subject + Tools.SINGLE_SPACE + accIouSlip.id
            Employee employee = (Employee) employeeCacheUtility.read(accIouSlip.employeeId)
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            Project project = (Project) projectCacheUtility.read(accIouSlip.projectId)
            String strCreatedOn = DateUtility.getDateTimeFormatAsString(accIouSlip.createdOn)
            String strTotalAmount = Tools.makeAmountWithThousandSeparator(accIouSlip.totalPurposeAmount)
            sentNotificationOfAccIouSlip(accIouSlip)  // DB update of table named acc_iou_slip
            Boolean mailSend = appMailService.sendMailForAccIouSlip(accIouSlip.projectId, appMail, accIouSlip.id, project.name, employee.fullName, designation.name, strTotalAmount, strCreatedOn)
            // check the mail is sentL or not
            if (!mailSend) {
                result.put(Tools.MESSAGE, MAIL_RECIPIENT_NOT_FOUND)
                return result
            }
            result.put(EMPLOYEE_OBJ, employee)
            result.put(PROJECT_OBJ, project)
            result.put(STR_TOTAL_AMOUNT, strTotalAmount)
            result.put(ACC_IOU_SLIP, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ERROR_SENDING_MAIL)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly updated AccIouSlip object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj  // cast map returned from execute method
            AccIouSlip accIouSlip = (AccIouSlip) executeResult.get(ACC_IOU_SLIP)
            GridEntity object = new GridEntity()
            object.id = accIouSlip.id
            String strCreatedOn = DateUtility.getLongDateForUI(accIouSlip.createdOn)
            String strTotalAmount = executeResult.get(STR_TOTAL_AMOUNT)
            Employee employee = (Employee) executeResult.get(EMPLOYEE_OBJ)
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            Project project = (Project) executeResult.get(PROJECT_OBJ)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(accIouSlip.createdBy)
            String employeeNameAndDesignation = employee.fullName + Tools.PARENTHESIS_START + designation.name + Tools.PARENTHESIS_END
            Object indent = procurementImplService.readIndent(accIouSlip.indentId)
            String indentDetails = Tools.EMPTY_SPACE
            if (indent) {
                indentDetails = indent.id + Tools.PARENTHESIS_START + DateUtility.getLongDateForUI(indent.fromDate) + ' To ' + DateUtility.getLongDateForUI(indent.toDate) + Tools.PARENTHESIS_END
            }
            object.cell = [
                    Tools.LABEL_NEW,
                    accIouSlip.id,
                    strCreatedOn,
                    employeeNameAndDesignation,
                    indentDetails,
                    strTotalAmount,
                    Tools.YES,
                    Tools.EMPTY_SPACE,
                    accIouSlip.purposeCount,
                    project.code,
                    createdBy.username
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, accIouSlip.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_APPROVE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SENDING_MAIL)
            return result
        }
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_iou_slip SET
                          version=:newVersion,
                          sent_for_approval=:sentForApproval
                      WHERE
                          id=:id AND
                          version=:version
                          """

    /**
     * Update acc_iou_slip by setting sent_for_approval true
     * @param accIouSlip - accIouSlip object from execute
     * @return -new updated accIouSlip object
     */
    private AccIouSlip sentNotificationOfAccIouSlip(AccIouSlip accIouSlip) {
        Map queryParams = [
                id: accIouSlip.id,
                newVersion: accIouSlip.version + 1,
                sentForApproval: accIouSlip.sentForApproval,
                version: accIouSlip.version
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(ERROR_SENDING_MAIL)
        }
        accIouSlip.version = accIouSlip.version + 1
        return accIouSlip
    }
}