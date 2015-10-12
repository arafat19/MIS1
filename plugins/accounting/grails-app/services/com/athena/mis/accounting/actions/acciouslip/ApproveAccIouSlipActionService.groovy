package com.athena.mis.accounting.actions.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.*
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Approve IOU slip by director or project director and show in the grid with its result
 *  For details go through Use-Case doc named 'ApproveAccIouSlipActionService'
 */
class ApproveAccIouSlipActionService extends BaseService implements ActionIntf {

    private static final String ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE = "Fail to approve IOU Slip"
    private static final String ACC_IOU_SLIP_APPROVE_SUCCESS_MESSAGE = "IOU slip has been approved successfully"
    private static final String NOT_FOUND_MESSAGE = "IOU slip not found"
    private static final String ACC_IOU_SLIP = "accIouSlip"
    private static final String APPROVED_ERROR = "IOU slip is already approved"
    private static final String INDENT_NOT_APPROVED = "Indent of this IOU slip is not approved"
    private static final String IOU_AMOUNT_EXCEEDS = "Sum of IOU amount exceeds indent amount"
    private static final String NOT_READY_ERROR = "IOU slip is not prepared to approve"
    private static final String NO_PURPOSE_ERROR = "Purpose not found in the IOU slip"
    private static final String ACCESS_ERROR = "You are not allowed to approve this IOU slip"

    private Logger log = Logger.getLogger(getClass())

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
    AccSessionUtil accSessionUtil
    @Autowired
    DesignationCacheUtility designationCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

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
            result.put(Tools.IS_ERROR, Boolean.TRUE)   // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            long accIouSlipId = Long.parseLong(parameterMap.id.toString())
            AccIouSlip accIouSlip = accIouSlipService.read(accIouSlipId)  // get accIouSlip object

            // check whether selected accIouSlip object exists or not
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            boolean isUserRoleDirOrPD = false   // flag for checking is user role Director/Project Director or not
            if (accSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_DIRECTOR) ||
                    accSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR)) {
                isUserRoleDirOrPD = true
            }

            // Access check for approval. Only Director/Project Director can approve the IOU slip
            if (!isUserRoleDirOrPD) {
                result.put(Tools.MESSAGE, ACCESS_ERROR)
                return result
            }

            // if the selected IOU slip is not sent for approval then it can't be approved
            if (!accIouSlip.sentForApproval) {
                result.put(Tools.MESSAGE, NOT_READY_ERROR)
                return result
            }

            // if the selected IOU slip is already approved then it can't be approved
            if (accIouSlip.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_ERROR)
                return result
            }

            // if the selected IOU slip has no purpose then it can't be approved
            if (accIouSlip.totalPurposeAmount <= 0) {
                result.put(Tools.MESSAGE, NO_PURPOSE_ERROR)
                return result
            }

            // Check if Indent is approved or Not
            if (accIouSlip.indentId > 0) {
                Object indent = (Object) procurementImplService.readIndent(accIouSlip.indentId)
                if (indent.approvedBy <= 0) {
                    result.put(Tools.MESSAGE, INDENT_NOT_APPROVED)
                    return result
                }
                def approvedTotal = AccIouSlip.getApprovedIouTotalByIndent((long) indent.id).list(readOnly: true)
                double sumIOU = 0.0d
                if (approvedTotal[0]) {
                    sumIOU = (double) approvedTotal[0]
                }
                if (sumIOU + accIouSlip.totalPurposeAmount > indent.totalPrice) {
                    result.put(Tools.MESSAGE, IOU_AMOUNT_EXCEEDS)
                    return result
                }
            }

            AppUser user = accSessionUtil.appSessionUtil.getAppUser()
            accIouSlip.approvedBy = user.id
            result.put(ACC_IOU_SLIP, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * update  accIouSlip object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccIouSlip accIouSlip = (AccIouSlip) receivedResult.get(ACC_IOU_SLIP)
            approveAccIouSlip(accIouSlip) // accIouSlip object is sent for approval
            result.put(ACC_IOU_SLIP, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
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
     * Show status(Approved By - username) in the grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj // cast map returned form execute method
            AccIouSlip accIouSlip = (AccIouSlip) executeResult.get(ACC_IOU_SLIP)
            GridEntity object = new GridEntity()
            object.id = accIouSlip.id
            String strCreatedOn = DateUtility.getLongDateForUI(accIouSlip.createdOn)
            Employee employee = (Employee) employeeCacheUtility.read(accIouSlip.employeeId)
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            Project project = (Project) projectCacheUtility.read(accIouSlip.projectId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(accIouSlip.createdBy)
            AppUser approvedBy = (AppUser) appUserCacheUtility.read(accIouSlip.approvedBy)
            String strTotalAmount = Tools.makeAmountWithThousandSeparator(accIouSlip.totalPurposeAmount)
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
                    accIouSlip.sentForApproval ? Tools.YES : Tools.NO,
                    approvedBy.username,
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
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj    // cast map returned form previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_iou_slip
                    SET
                        version=:newVersion,
                        approved_by=:approvedBy
                    WHERE
                        id=:id AND
                        version=:version
                    """

    /**
     * Update accIouSlip object
     * @param accIouSlip - object of AccIouSlip
     * @return - updated accIouSlip object
     */
    private AccIouSlip approveAccIouSlip(AccIouSlip accIouSlip) {
        Map queryParams = [
                id: accIouSlip.id,
                newVersion: accIouSlip.version + 1,
                approvedBy: accIouSlip.approvedBy,
                version: accIouSlip.version
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(ACC_IOU_SLIP_APPROVE_FAILURE_MESSAGE)
        }
        accIouSlip.version = accIouSlip.version + 1
        return accIouSlip
    }
}