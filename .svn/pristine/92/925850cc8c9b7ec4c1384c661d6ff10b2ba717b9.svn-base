package com.athena.mis.accounting.actions.report.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.N2W
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download IOU Slip in pdf format
 * For details go through Use-Case doc named 'DownloadForAccIouSlipActionService'
 */
class DownloadForAccIouSlipActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    EntityContentService entityContentService
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
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accIouSlip'
    private static final String OUTPUT_FILE_NAME = 'accIouSlip'
    private static final String REPORT_TITLE = 'AccIouSlip'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'accIouSlip.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String IOU_SLIP_IS_NOT_APPROVE = "IOU Slip is not approve"
    private static final String IOU_SLIP_NOT_FOUND = "IOU Slip not found"
    private static final String FAILURE_MSG = "Fail to generate Iou Slip"
    private static final String ACC_IOU_SLIP_MAP = "accIouSlipMap"
    private static final String ACC_IOU_SLIP_OBJ = "accIouSlip"
    private static final String ACC_IOU_SLIP_ID = "accIouSlipId"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String INDENT_DATE = "indentDate"
    private static final String APPROVED_BY_IMAGE_STREAM = "approvedByImageStream"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Get IOU slip object
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            long accIouSlipId = Long.parseLong(params.accIouSlipId.toString())
            AccIouSlip accIouSlip = AccIouSlip.findByIdAndCompanyId(accIouSlipId, accSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])
            // check whether the accIouSlip object exists or not
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, IOU_SLIP_NOT_FOUND)
                return result
            }

            // check the selected iou slip is approved or not
            if (accIouSlip.approvedBy <= 0) {
                result.put(Tools.MESSAGE, IOU_SLIP_IS_NOT_APPROVE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj      // cast object received from executePreCondition method
            AccIouSlip accIouSlip = (AccIouSlip) preResult.get(ACC_IOU_SLIP_OBJ)

            LinkedHashMap accIouSlipMap = buildAccIouSlipMap(accIouSlip) // build accIouSlip object

            String indentDate = Tools.EMPTY_SPACE
            // check if  accIouSlip.indentId > 0 then it give indent object and make indentDate
            if (accIouSlip.indentId > 0) {
                Object indent = procurementImplService.readIndent(accIouSlip.indentId)
                indentDate = DateUtility.getLongDateForUI(indent.fromDate) + Tools.TO + DateUtility.getLongDateForUI(indent.toDate)
            }

            Map report = getIouSlipReport(accIouSlipMap, indentDate) // get Iou Slip report
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj // cast object received from previous method

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
     * Build Acc Iou Slip object
     * @param accIouSlip - object of AccIouSlip domain
     * @return - new accIouSlipMap object
     */
    private LinkedHashMap buildAccIouSlipMap(AccIouSlip accIouSlip) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        Project project = (Project) projectCacheUtility.read(accIouSlip.projectId)
        Employee employee = (Employee) employeeCacheUtility.read(accIouSlip.employeeId)
        Designation designation = (Designation) designationCacheUtility.read(employee.designationId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(accIouSlip.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(accIouSlip.approvedBy)
        // pull system entity type(AppUser) object
        SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, companyId)

        InputStream approvedByImageStream = null
        ContentCategory imageTypeSignature = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE)
        if (approvedBy && approvedBy.hasSignature) {
            EntityContent approvedByImage = entityContentService.findByEntityIdAndEntityTypeIdAndContentCategoryId(approvedBy.id, contentEntityTypeAppUser.id, imageTypeSignature.id)
            approvedByImageStream = new ByteArrayInputStream(approvedByImage.content)
        }
        LinkedHashMap accIouSlipMap = [
                iouSlipId: accIouSlip.id,
                createdOn: DateUtility.getDateFormatAsString(accIouSlip.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdByName: createdBy.username,
                approvedByName: approvedBy.username,
                employeeName: employee.fullName,
                designation: designation.name,
                projectName: project.name,
                indentId: accIouSlip.indentId,
                totalAmount: Tools.makeAmountWithThousandSeparator(accIouSlip.totalPurposeAmount),
                totalAmountInWord: N2W.convert(accIouSlip.totalPurposeAmount),
                createdById: accIouSlip.createdBy,
                approvedById: accIouSlip.approvedBy,
                approvedByImageStream: approvedByImageStream
        ]
        return accIouSlipMap
    }

    /**
     * Generate PDF report using given data
     * @param accIouSlipMap - map of accIouSlip
     * @param indentDate - indent Date from execute method
     * @return - generated map with required params
     */
    private Map getIouSlipReport(Map accIouSlipMap, String indentDate) {
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(ACC_IOU_SLIP_MAP, accIouSlipMap)
        reportParams.put(INDENT_DATE, indentDate)
        reportParams.put(ACC_IOU_SLIP_ID, accIouSlipMap.iouSlipId)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(APPROVED_BY_IMAGE_STREAM, accIouSlipMap.approvedByImageStream)
        ByteArrayOutputStream report

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        report = jasperService.generateReport(reportDef)

        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]

    }
}
