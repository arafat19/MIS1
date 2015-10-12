package com.athena.mis.accounting.actions.report.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.model.AccIouDetailsModel
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
import grails.converters.JSON
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of Iou Slip which is shown the grid
 * For details go through Use-Case doc named 'ListForAccIouSlipActionService'
 */
class ListForAccIouSlipActionService extends BaseService implements ActionIntf {

    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private static final String IOU_SLIP_NOT_FOUND = "IOU Slip not found"
    private static final String FAILURE_MSG = "Fail to generate Iou Slip"
    private static final String ACC_IOU_SLIP_MAP = "iouSlipMap"
    private static final String PURPOSE_LIST = "purposeList"
    private static final String ACC_IOU_SLIP_OBJ = "accIouSlip"
    private static final String INDENT_DATE = "indentDate"
    private Logger log = Logger.getLogger(getClass())

    /**
     * Get accIouSlip object from AccIouSlip Domain by using dynamic finder
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            // Check the existence
            if (!params.accIouSlipId) {
                result.put(Tools.MESSAGE, IOU_SLIP_NOT_FOUND)
                return result
            }
            long accIouSlipId = Long.parseLong(params.accIouSlipId.toString())
            AccIouSlip accIouSlip = AccIouSlip.findByIdAndCompanyId(accIouSlipId, accSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])
            // check whether the accIouSlip object exists or not
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, IOU_SLIP_NOT_FOUND)
                return result
            }

            result.put(ACC_IOU_SLIP_OBJ, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Get desired report providing all required parameters
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccIouSlip accIouSlip = (AccIouSlip) preResult.get(ACC_IOU_SLIP_OBJ)
            List<AccIouDetailsModel> purposeList = AccIouDetailsModel.listPurposeByIou(accIouSlip.id).list()
            LinkedHashMap accIouSlipMap = buildAccIouSlipMap(accIouSlip)  // build accIouSlip map

            String indentDate = ''
            // check if  accIouSlip.indentId > 0 then it give indent object and make indentDate
            if (accIouSlip.indentId > 0) {
                Object indent = procurementImplService.readIndent(accIouSlip.indentId)
                indentDate = DateUtility.getLongDateForUI(indent.fromDate) + Tools.TO + DateUtility.getLongDateForUI(indent.toDate)
            }

            List lstPurpose = []
            for (int i = 0; i < purposeList.size(); i++) {
                AccIouDetailsModel slipPurpose = purposeList[i]
                Map purposeMap = [
                        sl: i + 1,
                        purpose: slipPurpose.purposeDescription,
                        amount: slipPurpose.purposeAmount
                ]
                lstPurpose << purposeMap
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(ACC_IOU_SLIP_MAP, accIouSlipMap)
            result.put(PURPOSE_LIST, lstPurpose as JSON)
            result.put(INDENT_DATE, indentDate)
            return result
        }

        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj - map returned from previous methods, can be null
     * @return - a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build acc Iou Slip map
     * @param accIouSlip - object returned from previous methods
     * @return -a map named accIouSlipMap
     */
    private LinkedHashMap buildAccIouSlipMap(AccIouSlip accIouSlip) {
        Project project = (Project) projectCacheUtility.read(accIouSlip.projectId)
        Employee employee = (Employee) employeeCacheUtility.read(accIouSlip.employeeId)
        Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(accIouSlip.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(accIouSlip.approvedBy)
        LinkedHashMap accIouSlipMap = [
                iouSlipId: accIouSlip.id,
                createdOn: DateUtility.getDateFormatAsString(accIouSlip.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                employeeName: employee.fullName,
                designation: designation.name,
                projectName: project.name,
                indentId: accIouSlip.indentId,
                totalAmount: Tools.makeAmountWithThousandSeparator(accIouSlip.totalPurposeAmount)
        ]
        return accIouSlipMap
    }
}

