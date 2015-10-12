package com.athena.mis.inventory.actions.report.inventorysummary

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download inventory summary report in pdf format
 * For details go through Use-Case doc named 'DownloadForInventorySummaryActionService'
 */
class DownloadForInventorySummaryActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility

    private static final String INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String INVENTORY_ID = "inventoryId"
    private static final String INVENTORY_NAME = "inventoryName"
    private static final String INVENTORY_DESCRIPTION = "description"
    private static final String FAILURE_MSG = "Fail to generate inventory summary report"
    private static final String REPORT_FOLDER = 'inventorySummary'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'summary'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_TITLE = 'Inventory Summary'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'inventorySummary.jasper'

    protected final Logger log = Logger.getLogger(getClass())

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
            if ((!params.inventoryId) || (!params.startDate) || (!params.endDate)) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            // get required parameters from UI
            long inventoryId = Long.parseLong(params.inventoryId.toString())
            Date startDate = DateUtility.parseMaskedFromDate(params.startDate)
            Date endDate = DateUtility.parseMaskedToDate(params.endDate)

            result.put(INVENTORY_ID, inventoryId)
            result.put(START_DATE, startDate)
            result.put(END_DATE, endDate)
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
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from executePreCondition method
            Map report = getInventoryStockReport(preResult) // generate report
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
     * Do nothing for post operation
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
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from previous method
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
     * Generate inventory summary report in pdf format
     * @param preResult -map from executePreCondition method
     * @return -generated report
     */
    private Map getInventoryStockReport(Map preResult) {
        long inventoryId = Long.parseLong(preResult.get(INVENTORY_ID).toString())
        InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
        Date startDate = (Date) preResult.get(START_DATE)
        Date endDate = (Date) preResult.get(END_DATE)
        // put required parameters to generate report
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)

        reportParams.put(INVENTORY_ID, invInventory.id)
        reportParams.put(START_DATE, startDate.toTimestamp())
        reportParams.put(END_DATE, endDate.toTimestamp())
        reportParams.put(INVENTORY_NAME, invInventory.name)
        reportParams.put(INVENTORY_DESCRIPTION, invInventory.description)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}