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
 *  Create new IOU slip object and show in grid
 *  For details go through Use-Case doc named 'CreateAccIouSlipActionService'
 */
class CreateAccIouSlipActionService extends BaseService implements ActionIntf {

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

    private static final String ACC_IOU_SLIP_CREATE_FAILURE_MSG = "IOU slip has not been saved"
    private static final String ACC_IOU_SLIP_CREATE_SUCCESS_MSG = "IOU slip has been successfully saved"
    private static final String DEFAULT_ERROR_MESSAGE = "Can not create IOU slip"
    private static final String ACC_IOU_SLIP = "accIouSlip"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build IouSlip object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            AccIouSlip accIouSlip = buildAccIouSlip(parameterMap)  // build accIouSlip object

            result.put(ACC_IOU_SLIP, accIouSlip)
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
     * Save accIouSlip object in DB
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj // cast map returned from executePreCondition method
            AccIouSlip accIouSlip = (AccIouSlip) receivedResult.get(ACC_IOU_SLIP)

            AccIouSlip newAccIouSlip = accIouSlipService.create(accIouSlip)   // save new AccIouSlip object in DB
            if (!newAccIouSlip) {
                result.put(Tools.MESSAGE, ACC_IOU_SLIP_CREATE_FAILURE_MSG)
                return result
            }
            result.put(ACC_IOU_SLIP, newAccIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_CREATE_FAILURE_MSG)
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
     * Show newly created AccIouSlip object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj   // cast map returned from execute method
            AccIouSlip accIouSlip = (AccIouSlip) receivedResult.get(ACC_IOU_SLIP)
            GridEntity object = new GridEntity() //build grid object
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
                    Tools.NO,
                    Tools.EMPTY_SPACE,
                    accIouSlip.purposeCount,
                    project.code,
                    createdBy.username
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, accIouSlip.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_CREATE_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_IOU_SLIP_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build AccIouSlip object
     * @param params -serialized parameters from UI
     * @return -new accIouSlip object
     */
    private AccIouSlip buildAccIouSlip(GrailsParameterMap params) {
        AccIouSlip accIouSlip = new AccIouSlip()

        AppUser user = accSessionUtil.appSessionUtil.getAppUser()

        accIouSlip.id = 0
        accIouSlip.version = 0
        accIouSlip.employeeId = Long.parseLong(params.employeeId.toString())
        accIouSlip.projectId = Long.parseLong(params.projectId.toString())
        long indentId = Long.parseLong(params.indentId.toString())
        accIouSlip.indentId = (indentId < 0) ? 0L : indentId

        accIouSlip.approvedBy = 0L
        accIouSlip.sentForApproval = false

        accIouSlip.createdBy = user.id
        accIouSlip.createdOn = new Date()
        accIouSlip.updatedBy = 0L
        accIouSlip.updatedOn = null

        accIouSlip.companyId = user.companyId
        accIouSlip.totalPurposeAmount = 0D
        accIouSlip.purposeCount = 0

        return accIouSlip
    }
}