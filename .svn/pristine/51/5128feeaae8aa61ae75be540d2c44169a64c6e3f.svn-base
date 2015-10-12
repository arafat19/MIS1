package com.athena.mis.procurement.actions.report.purchaseorder

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.*
import com.athena.mis.procurement.entity.ProcCancelledPO
import com.athena.mis.procurement.entity.ProcPurchaseOrder
import com.athena.mis.procurement.service.ProcCancelledPOService
import com.athena.mis.procurement.service.PurchaseOrderService
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
 * Download Purchase Order PDF report
 * For details go through Use-Case doc named 'DownloadForPurchaseOrderActionService'
 */
class DownloadForPurchaseOrderActionService extends BaseService implements ActionIntf {

    PurchaseOrderService purchaseOrderService
    ProcCancelledPOService procCancelledPOService
    JasperService jasperService
    EntityContentService entityContentService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    PaymentMethodCacheUtility paymentMethodCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility
    @Autowired
    ContentCategoryCacheUtility contentCategoryCacheUtility
    @Autowired
    ProcSessionUtil procSessionUtil

    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase Order not found."
    private static final String FAILURE_MSG = "Fail to generate Purchase Order."
    private static final String PURCHASE_ORDER_MAP = "purchaseOrderMap"
    private static final String PURCHASE_ORDER_OBJ = "purchaseOrder"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_FOLDER = 'purchaseOrder'
    private static final String OUTPUT_FILE_NAME = 'purchaseOrder'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'purchaseOrder.jasper'
    private static final String JASPER_FILE_CANCEL_PO = 'cancelledPurchaseOrder.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String PURCHASE_ORDER_ID = "purchaseOrderId"
    private static final String PURCHASE_ORDER_NOT_APPROVED = "Purchase Order is not approved."
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * 1. pull purchase order object against purchase order id
     * 2. check the purchase order approval
     * @param parameters -serialized parameters from UI.
     * @param obj - N/A
     * @return - purchase order object
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            if(params.cancelledPo == 'true'){
                ProcCancelledPO procCancelledPO = procCancelledPOService.read(purchaseOrderId)
                if (!procCancelledPO) {
                    result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                    return result
                }

                result.put(PURCHASE_ORDER_OBJ, procCancelledPO)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                return result
            }

            ProcPurchaseOrder purchaseOrder = purchaseOrderService.read(purchaseOrderId)
            if (!purchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result
            }
            //check the purchase order approval
            ProcPurchaseOrder approvePurchaseOrder = purchaseOrderService.readApprovedPurchaseOrder(purchaseOrder.id)
            if (!approvePurchaseOrder) {
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_APPROVED)
                return result
            }

            result.put(PURCHASE_ORDER_OBJ, purchaseOrder)
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
     * 1. receive purchase order from pre execute method
     * 2. build purchase order object
     * 2. generate report by assigned data
     * @param parameters -N/A
     * @param obj - object receive from previous method
     * @return- a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            LinkedHashMap purchaseOrderMap
            Map report
            boolean isCancelled = Boolean.parseBoolean(params.cancelledPo)
            if(isCancelled) {
                ProcCancelledPO procCancelledPO = (ProcCancelledPO) preResult.get(PURCHASE_ORDER_OBJ)
                 purchaseOrderMap = buildCancelledPOMap(procCancelledPO)
                 report = getCancelledPOReport(purchaseOrderMap)
            }else{
                ProcPurchaseOrder purchaseOrder = (ProcPurchaseOrder) preResult.get(PURCHASE_ORDER_OBJ)
                purchaseOrderMap = buildPurchaseOrderMap(purchaseOrder)
                 report = getPOReport(purchaseOrderMap)
            }


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
     * 1. pull project related to specific purchase order
     * 2. pull lastUpdatedBy, approvedByDirector, approvedByProjectDirector from appUser
     * 3. pull supplier object
     * 4. pull entity content for Director & Project Director
     * @param purchaseOrder - purchase order object
     * @return - newly built purchase order map
     */
    private LinkedHashMap buildPurchaseOrderMap(ProcPurchaseOrder purchaseOrder) {
        Project project = (Project) projectCacheUtility.read(purchaseOrder.projectId)
        AppUser lastUpdatedBy = (AppUser) (purchaseOrder.updatedBy > 0 ? appUserCacheUtility.read(purchaseOrder.updatedBy) : appUserCacheUtility.read(purchaseOrder.createdBy))
        Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrder.supplierId)
        SystemEntity paymentMethod = (SystemEntity) paymentMethodCacheUtility.read(purchaseOrder.paymentMethodId)
        AppUser approvedByDirector = (AppUser) appUserCacheUtility.read(purchaseOrder.approvedByDirectorId)
        AppUser approvedByProjectDirector = (AppUser) appUserCacheUtility.read(purchaseOrder.approvedByProjectDirectorId)
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

        LinkedHashMap purchaseOrderMap = [
                purchaseOrderId: purchaseOrder.id,
                lastUpdatedOn: purchaseOrder.updatedOn ? DateUtility.getDateFormatAsString(purchaseOrder.updatedOn) : DateUtility.getDateFormatAsString(purchaseOrder.createdOn), // po date
                printDate: DateUtility.getDateFormatAsString(new Date()),
                lastUpdatedBy: lastUpdatedBy.username,
                projectName: project.name,
                totalTransportCost: purchaseOrder.trCostTotal,
                netPrice: purchaseOrder.totalPrice,
                discount: purchaseOrder.discount,
                totalVatTax: purchaseOrder.totalVatTax,
                supplierName: supplier.name,
                supplierAddress: supplier.address,
                paymentMethod: paymentMethod.key, //payment method such as cash, cheque
                paymentTerms: purchaseOrder.modeOfPayment, //modeOfPayment
                directorName: approvedByDirector ? approvedByDirector.username : Tools.EMPTY_SPACE,
                pdName: approvedByProjectDirector ? approvedByProjectDirector.username : Tools.EMPTY_SPACE,
                directorImageStream: directorImageStream,
                pdImageStream: pdImageStream,
                lblLastUpdatedOn: 'Last Updated On',
                lblLastUpdatedBy: 'Our References'
        ]
        return purchaseOrderMap
    }

    /**
     * 1. pull project related to specific purchase order
     * 2. pull cancelledBy, approvedByDirector, approvedByProjectDirector from appUser
     * 3. pull supplier object
     * 4. pull entity content for Director & Project Director
     * @param purchaseOrder - ProcCancelledPO object
     * @return - newly built ProcCancelledPO map
     */
    private LinkedHashMap buildCancelledPOMap(ProcCancelledPO purchaseOrder) {
        Project project = (Project) projectCacheUtility.read(purchaseOrder.projectId)
        AppUser cancelledBy = (AppUser)appUserCacheUtility.read(purchaseOrder.cancelledBy)
        Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrder.supplierId)
        SystemEntity paymentMethod = (SystemEntity) paymentMethodCacheUtility.read(purchaseOrder.paymentMethodId)
        AppUser approvedByDirector = (AppUser) appUserCacheUtility.read(purchaseOrder.approvedByDirectorId)
        AppUser approvedByProjectDirector = (AppUser) appUserCacheUtility.read(purchaseOrder.approvedByProjectDirectorId)
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

        LinkedHashMap purchaseOrderMap = [
                purchaseOrderId: purchaseOrder.id,
                lastUpdatedOn: DateUtility.getDateFormatAsString(purchaseOrder.cancelledOn),
                printDate: DateUtility.getDateFormatAsString(new Date()),
                lastUpdatedBy: cancelledBy.username,
                projectName: project.name,
                totalTransportCost: purchaseOrder.trCostTotal,
                netPrice: purchaseOrder.totalPrice,
                discount: purchaseOrder.discount,
                totalVatTax: purchaseOrder.totalVatTax,
                supplierName: supplier.name,
                supplierAddress: supplier.address,
                paymentMethod: paymentMethod.key, //payment method such as cash, cheque
                paymentTerms: purchaseOrder.modeOfPayment, //modeOfPayment
                directorName: approvedByDirector ? approvedByDirector.username : Tools.EMPTY_SPACE,
                pdName: approvedByProjectDirector ? approvedByProjectDirector.username : Tools.EMPTY_SPACE,
                directorImageStream: directorImageStream,
                pdImageStream: pdImageStream,
                lblLastUpdatedOn: 'Cancelled On',
                lblLastUpdatedBy: 'Cancelled By',
                cancelReason: purchaseOrder.cancelReason
        ]
        return purchaseOrderMap
    }
    /**
     * Generate report by given data
     * 1. set report directory
     * 2. assign all required params
     * @param purchaseOrderMap - purchase order object
     * @return - generated report with required params
     */
    private Map getPOReport(Map purchaseOrderMap) {
        String reportDir = Tools.getProcurementReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
        reportParams.put(PURCHASE_ORDER_ID, purchaseOrderMap.purchaseOrderId)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    /**
     * Generate report by given data
     * 1. set report directory
     * 2. assign all required params
     * @param purchaseOrderMap - purchase order object
     * @return - generated report with required params
     */
    private Map getCancelledPOReport(Map purchaseOrderMap) {
        String reportDir = Tools.getProcurementReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(PURCHASE_ORDER_MAP, purchaseOrderMap)
        reportParams.put(PURCHASE_ORDER_ID, purchaseOrderMap.purchaseOrderId)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_CANCEL_PO, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}