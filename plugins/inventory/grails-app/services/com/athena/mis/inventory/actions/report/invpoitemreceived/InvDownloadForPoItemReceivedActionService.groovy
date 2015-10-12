package com.athena.mis.inventory.actions.report.invpoitemreceived

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download received items of PO report in pdf format
 * For details go through Use-Case doc named 'InvDownloadForPoItemReceivedActionService'
 */
class InvDownloadForPoItemReceivedActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired(required = false)
    ProcurementPluginConnector procurementImplService
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_FOLDER = 'poItemReceived'
    private static final String JASPER_FILE = 'poItemReceived.jasper'
    private static final String PO_NOT_FOUND = "PO not found"
    private static final String FAILURE_MSG = "Fail to download PO item received report"
    private static final String OUTPUT_FILE_NAME = 'poItemReceived'
    private static final String REPORT_TITLE = 'PO Item Received'
    private static final String PO_ID = "poId"
    private static final String TRANSACTION_ENTITY_TYPE_ID = "transactionEntityTypeId"
    private static final String SUPPLIER_NAME = "supplierName"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check required parameters from UI
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!params.poId) {
                result.put(Tools.MESSAGE, PO_NOT_FOUND)
                return result
            }
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
     * Get desired report providing all required parameters
     * 1. get all required parameters
     * 2. generate report
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long poId = Long.parseLong(params.poId.toString())
            // get purchase order object by poId
            Object purchaseOrder = procurementImplService.readPO(poId)
            // get supplier object by purchaseOrder.supplierId
            Supplier supplier = (Supplier) supplierCacheUtility.read(purchaseOrder.supplierId)
            String supplierName = supplier.name
            Map report = getPoItemReceivedReport(poId, supplierName)  // generate report
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for build successful result
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
            LinkedHashMap previousResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, previousResult.get(Tools.IS_ERROR))
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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
     * Generate PO item received report
     * @param poId -id of purchase order object
     * @param supplierName -name of supplier
     * @return -generated report
     */
    private Map getPoItemReceivedReport(long poId, String supplierName) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)

        Map reportParams = new LinkedHashMap()
        // put required parameters to generate report
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PO_ID, poId)
        reportParams.put(SUPPLIER_NAME, supplierName)
        reportParams.put(TRANSACTION_ENTITY_TYPE_ID, transactionEntitySupplier.id)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}