package com.athena.mis.inventory.actions.report.inventoryvaluation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
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
 * Download inventory valuation report in csv format
 * For details go through Use-Case doc named 'DownloadForInventoryValuationCsvActionService'
 */
class DownloadForInventoryValuationCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String INVALID_INPUT = "Invalid input"
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to generate Inventory Valuation CSV Report"
    private static final String INVENTORY_ID = "inventoryId"
    private static final String INVENTORY_OBJ = "inventoryObj"
    private static final String INVENTORY_LOCATION = "inventoryLocation"
    private static final String PROJECT_NAME = "projectName"
    private static final String PRINT_DATE = "printDate"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'inventoryValuation'
    private static final String OUTPUT_FILE_NAME = 'inventoryValuation'
    private static final String REPORT_TITLE = 'Inventory Details'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'inventoryValuationCsv.jasper'
    private static final String CSV_EXTENSION = ".csv"
    private static final String REPORT = "report"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Check and get required parameters from UI
     * Get inventory object from cache utility by inventoryId
     * Check if inventory exists or not
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // check required parameter
            if (!parameterMap.inventoryId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            // get inventory object form invInventoryCacheUtility by inventoryId
            long inventoryId = Long.parseLong(parameterMap.inventoryId.toString())
            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)
            // check if inventory exists or not
            if (!invInventory) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            result.put(INVENTORY_OBJ, invInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get desired report providing all required parameters
     * 1. get all required objects
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
            InvInventory invInventory = (InvInventory) preResult.get(INVENTORY_OBJ)
            Map report = getInventoryValuationReport(invInventory)  // generate report
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Generate inventory valuation report
     * @param invInventory -object of InvInventory
     * @return -generated report
     */
    private Map getInventoryValuationReport(InvInventory invInventory) {
        String reportDir = Tools.getInventoryReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION

        Map reportParams = new LinkedHashMap()
        // put required parameters to generate report
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(INVENTORY_ID, invInventory.id)
        reportParams.put(INVENTORY_LOCATION, invInventory.name)
        Project project = (Project) projectCacheUtility.read(invInventory.projectId)
        reportParams.put(PROJECT_NAME, project.name)
        reportParams.put(PRINT_DATE, new Date().format(DateUtility.dd_MMM_yyyy_DASH).toString())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}