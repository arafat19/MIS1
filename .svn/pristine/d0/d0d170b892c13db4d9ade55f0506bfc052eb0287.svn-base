package com.athena.mis.inventory.actions.report.invsupplierchalan

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download supplier chalan report in pdf format
 * For details go through Use-Case doc named 'DownloadForInventorySupplierchalanActionService'
 */
class DownloadForInventorySupplierchalanActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String CHALAN_NO = "chalanNo"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String STATUS = "status"
    private static final String STATUS_STR = "statusStr"
    private static final String STATUS_STR_ALL = "All"
    private static final String STATUS_STR_PENDING = "Pending"
    private static final String STATUS_STR_ACKNOWLEDGED = "Acknowledged"
    private static final String SUPPLIER_NAME = "supplierName"
    private static final String FAILURE_MSG = "Fail to generate supplier chalan report"
    private static final String REPORT_FOLDER = 'supplierChalan'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'supplier_chalan'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_TITLE = 'Supplier chalan'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_FOR_ALL_OR_ACKNOWLEDGED = 'supplierChalanForAllOrAcknowledged.jasper'
    private static final String JASPER_FILE_FOR_PENDING = 'supplierChalanForPending.jasper'
    private static final String DB_QUANTITY_FORMAT = 'dbQuantityFormat'
    private static final String DB_CURRENCY_FORMAT = 'dbCurrencyFormat'

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check and get required parameters from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameters
            if (!params.chalanNo || !params.supplierId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            // get required parameters from UI
            String chalanNo = params.chalanNo.toString()
            long supplierId = Long.parseLong(params.supplierId.toString())
            int status = Integer.parseInt(params.status.toString())

            result.put(CHALAN_NO, chalanNo)
            result.put(SUPPLIER_ID, supplierId)
            result.put(STATUS, status)
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
     * 1. get supplier from supplier cache utility by supplierId
     * 2. generate report
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            String statusStr
            String supplierName
            String chalanNo = preResult.get(CHALAN_NO).toString()
            long supplierId = Long.parseLong(preResult.get(SUPPLIER_ID).toString())
            int status = Integer.parseInt(preResult.get(STATUS).toString())
            if (status == -1) {
                statusStr = STATUS_STR_ALL
            } else if (status == 1) {
                statusStr = STATUS_STR_PENDING
            } else {
                statusStr = STATUS_STR_ACKNOWLEDGED
            }
            // get supplier from supplier cache utility by supplierId
            Supplier supplier = (Supplier) supplierCacheUtility.read(supplierId)
            supplierName = supplier.name
            // generate report
            Map report = getInventorySupplierChalanReport(chalanNo, supplierId, status, statusStr, supplierName)
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
     * Generate supplier chalan report in pdf format
     * @param chalanNo -supplier chalan no
     * @param supplierId -id of supplier
     * @param status -status of supplier chalan (pending/acknowledged/all)
     * @param statusStr -status ofd supplier chalan in String
     * @param supplierName -name of supplier
     * @return -generated report
     */
    private Map getInventorySupplierChalanReport(String chalanNo, long supplierId, int status, String statusStr, String supplierName) {

        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        // put required parameters to generate report
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(CHALAN_NO, chalanNo)
        reportParams.put(SUPPLIER_ID, supplierId)
        reportParams.put(STATUS, status)
        reportParams.put(STATUS_STR, statusStr)
        reportParams.put(SUPPLIER_NAME, supplierName)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)

        String fileName
        if (status == -1 || status == 0) {
            fileName = JASPER_FILE_FOR_ALL_OR_ACKNOWLEDGED
        } else {
            fileName = JASPER_FILE_FOR_PENDING
        }

        JasperReportDef reportDef = new JasperReportDef(name: fileName, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
