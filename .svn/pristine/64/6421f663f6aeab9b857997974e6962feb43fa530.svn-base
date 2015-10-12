package com.athena.mis.procurement.actions.report.purchaserequest

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.budget.BudgetPluginConnector
import com.athena.mis.procurement.entity.ProcPurchaseRequest
import com.athena.mis.procurement.service.PurchaseRequestService
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download Purchase Request PDF report
 * For details go through Use-Case doc named 'DownloadForPurchaseRequestActionService'
 */
class DownloadForPurchaseRequestActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    PurchaseRequestService purchaseRequestService
    BudgetPluginConnector budgetImplService
    EntityContentService entityContentService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    UnitCacheUtility unitCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ProcSessionUtil procSessionUtil

    private final String PURCHASE_REQUEST_NOT_FOUND = "Purchase Request not found."
    private final String FAILURE_MSG = "Fail to generate Purchase Request."
    private final String PURCHASE_REQUEST_MAP = "purchaseRequestMap"
    private final String PURCHASE_REQUEST_OBJ = "purchaseRequest"
    private final String PURCHASE_REQUEST_ID = "purchaseRequestId"

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'purchaseRequest'
    private static final String OUTPUT_FILE_NAME = 'purchaseRequest'
    private static final String REPORT_TITLE = 'Purchase Request'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String JASPER_FILE = 'purchaseRequest.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String DIRECTOR_IMAGE_STREAM = "directorImageStream"
    private static final String PD_IMAGE_STREAM = "pdImageStream"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. pull purchase request object against purchase request id
     * @param parameters -serialized parameters from UI.
     * @param obj - N/A
     * @return - purchase request object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long purchaseRequestId = Long.parseLong(params.purchaseRequestId.toString())
            ProcPurchaseRequest purchaseRequest = purchaseRequestService.read(purchaseRequestId)

            if (!purchaseRequest) {
                result.put(Tools.MESSAGE, PURCHASE_REQUEST_NOT_FOUND)
                return result
            }

            result.put(PURCHASE_REQUEST_OBJ, purchaseRequest)
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
     * 1. receive purchase request from pre execute method
     * 2. build purchase request object
     * 2. generate report by assigned data
     * @param parameters -N/A
     * @param obj - object receive from previous method
     * @return- a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ProcPurchaseRequest purchaseRequest = (ProcPurchaseRequest) preResult.get(PURCHASE_REQUEST_OBJ)

            LinkedHashMap purchaseRequestMap = buildPurchaseRequestMap(purchaseRequest)
            Map report = getInvoiceReport(purchaseRequestMap)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object result) {
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
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
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
     * 1. pull budget & budget type object
     * 2. pull unit from system entity
     * 3. pull createdBy, approvedByDirector, approvedByProjectDirector from appUser
     * 4. pull supplier object
     * 5. pull entity content for Director & Project Director
     * The method is in Transactional block as it can be roll back in case of any exception.
     * @param purchaseRequest - purchase request object
     * @return - newly built purchase request map
     */
    @Transactional(readOnly = true)
    private LinkedHashMap buildPurchaseRequestMap(ProcPurchaseRequest purchaseRequest) {

        Project project = (Project) projectCacheUtility.read(purchaseRequest.projectId)
        AppUser createdBy = (AppUser) appUserCacheUtility.read(purchaseRequest.createdBy)
        AppUser approvedByDirector = (AppUser) appUserCacheUtility.read(purchaseRequest.approvedByDirectorId)
        AppUser approvedByProjectDirector = (AppUser) appUserCacheUtility.read(purchaseRequest.approvedByProjectDirectorId)
        long companyId = procSessionUtil.appSessionUtil.getCompanyId()
        // pull system entity type(AppUser) object
        SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, companyId)

        InputStream directorImageStream = null
        InputStream pdImageStream = null
        ContentCategory imageTypeSignature = (ContentCategory) contentCategoryCacheUtility.readBySystemContentCategory(contentCategoryCacheUtility.IMAGE_TYPE_SIGNATURE)
        if (approvedByDirector && approvedByDirector.hasSignature) {
            EntityContent dirImage = entityContentService.findByEntityIdAndEntityTypeIdAndContentCategoryId(approvedByDirector.id, contentEntityTypeAppUser.id, imageTypeSignature.id)
            directorImageStream = new ByteArrayInputStream(dirImage.content)
        }
        if (approvedByProjectDirector && approvedByProjectDirector.hasSignature) {
            EntityContent pdImage = entityContentService.findByEntityIdAndEntityTypeIdAndContentCategoryId(approvedByProjectDirector.id, contentEntityTypeAppUser.id, imageTypeSignature.id)
            pdImageStream = new ByteArrayInputStream(pdImage.content)
        }

        LinkedHashMap purchaseRequestMap = [
                purchaseRequestId: purchaseRequest.id,
                numberOfItems: purchaseRequest.itemCount,
                createdOn: DateUtility.getDateFormatAsString(purchaseRequest.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                createdBy: createdBy.username,
                projectName: project.name,
                projectDescription: project.description,
                comments: purchaseRequest.comments,
                directorName: approvedByDirector ? approvedByDirector.username : Tools.EMPTY_SPACE,
                directorId: approvedByDirector ? approvedByDirector.id : 0,
                pdName: approvedByProjectDirector ? approvedByProjectDirector.username : Tools.EMPTY_SPACE,
                pdId: approvedByProjectDirector ? approvedByProjectDirector.id : 0,
                directorImageStream: directorImageStream,
                pdImageStream: pdImageStream
        ]
        return purchaseRequestMap
    }
    /**
     * Generate report by given data
     * 1. set report directory
     * 2. assign all required params
     * @param purchaseRequestMap - purchase request object
     * @return - generated report with required params
     */
    private Map getInvoiceReport(Map purchaseRequestMap) {

        String reportDir = Tools.getProcurementReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        String subReportDir = Tools.getProcurementReportDirectory()
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PURCHASE_REQUEST_MAP, purchaseRequestMap)
        reportParams.put(PURCHASE_REQUEST_ID, purchaseRequestMap.purchaseRequestId)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(DIRECTOR_IMAGE_STREAM, purchaseRequestMap.directorImageStream)
        reportParams.put(PD_IMAGE_STREAM, purchaseRequestMap.pdImageStream)
        ByteArrayOutputStream report

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        report = jasperService.generateReport(reportDef)

        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}