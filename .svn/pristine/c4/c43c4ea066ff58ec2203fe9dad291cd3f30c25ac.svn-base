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
 *  Update iouSlip object and grid data
 *  For details go through Use-Case doc named 'UpdateAccIouSlipActionService'
 */
class UpdateAccIouSlipActionService extends BaseService implements ActionIntf {

    private static final String ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE = "Fail to update IOU slip"
    private static final String ACC_IOU_SLIP_UPDATE_SUCCESS_MESSAGE = "IOU slip has been update successfully"
    private static final String NOT_FOUND_MESSAGE = "IOU slip not found"
    private static final String PURPOSE_ERROR = " purpose(s) has association with the IOU slip"
    private static final String ACC_IOU_SLIP = "accIouSlip"
    private static final String APPROVED_ERROR = "IOU slip already approved"

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

    /**
     * Get parameters from UI and build accIouSlip object for update
     * @param params - serialized parameters from UI
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
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long accIouSlipId = Long.parseLong(parameterMap.id.toString())
            AccIouSlip oldAccIouSlip = accIouSlipService.read(accIouSlipId)  // get accIouSlip object
            // check whether the accIouSlip object exists or not
            if (!oldAccIouSlip) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            AccIouSlip newAccIouSlip = buildAccIouSlip(parameterMap, oldAccIouSlip)  // build accIouSlip object for update

            // check whether the select accIouSlip object version exists or not
            if (oldAccIouSlip.version != newAccIouSlip.version) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }

            // check the selected iou slip is approved or not
            if (newAccIouSlip.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_ERROR)
                return result
            }

            // check association of accIouSlip with Iou Purpose and check newAccIouSlip object and oldAccIouSlip object is contain same indentId or not
            if ((oldAccIouSlip.purposeCount > 0) && (newAccIouSlip.indentId != oldAccIouSlip.indentId)) {
                result.put(Tools.MESSAGE, oldAccIouSlip.purposeCount.toString() + PURPOSE_ERROR)
                return result
            }
            result.put(ACC_IOU_SLIP, newAccIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update accIouSlip object in DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj    // cast map returned from executePreCondition method

            AccIouSlip accIouSlip = (AccIouSlip) receivedResult.get(ACC_IOU_SLIP)

            AccIouSlip newAccIouSlip = accIouSlipService.update(accIouSlip) // update accIouSlip object in DB

            result.put(ACC_IOU_SLIP, newAccIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE)
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
     * Show updated accIouSlip object in grid
     * Show success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj  // cast map returned from execute method
            AccIouSlip accIouSlip = (AccIouSlip) executeResult.get(ACC_IOU_SLIP)
            GridEntity object = new GridEntity()  // build grid entity object
            object.id = accIouSlip.id
            String strCreatedOn = DateUtility.getLongDateForUI(accIouSlip.createdOn)
            Employee employee = (Employee) employeeCacheUtility.read(accIouSlip.employeeId)
            Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
            Project project = (Project) projectCacheUtility.read(accIouSlip.projectId)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(accIouSlip.createdBy)
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
                    Tools.EMPTY_SPACE,
                    accIouSlip.purposeCount,
                    project.code,
                    createdBy.username
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, accIouSlip.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_UPDATE_SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build accIouSlip object for update
     * @param params - serialized parameters from UI
     * @param oldAccIouSlip - old AccIouSlip object
     * @return - updated AccIouSlip object
     */
    private AccIouSlip buildAccIouSlip(GrailsParameterMap params, AccIouSlip oldAccIouSlip) {
        AccIouSlip accIouSlip = new AccIouSlip()

        AppUser user = accSessionUtil.appSessionUtil.getAppUser()

        accIouSlip.id = Long.parseLong(params.id.toString())
        accIouSlip.version = Integer.parseInt(params.version.toString())
        accIouSlip.employeeId = Long.parseLong(params.employeeId.toString())
        accIouSlip.projectId = Long.parseLong(params.projectId.toString())
        long indentId = Long.parseLong(params.indentId.toString())
        accIouSlip.indentId = (indentId <= 0) ? 0L : indentId

        accIouSlip.approvedBy = oldAccIouSlip.approvedBy
        accIouSlip.sentForApproval = oldAccIouSlip.sentForApproval

        accIouSlip.createdBy = oldAccIouSlip.createdBy
        accIouSlip.createdOn = oldAccIouSlip.createdOn
        accIouSlip.updatedBy = user.id
        accIouSlip.updatedOn = new Date()

        accIouSlip.companyId = oldAccIouSlip.companyId
        accIouSlip.totalPurposeAmount = oldAccIouSlip.totalPurposeAmount
        accIouSlip.purposeCount = oldAccIouSlip.purposeCount

        return accIouSlip
    }

}