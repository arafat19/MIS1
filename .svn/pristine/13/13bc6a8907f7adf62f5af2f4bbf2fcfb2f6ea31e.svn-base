package com.athena.mis.fixedasset.actions.report.consumptionagainstasset

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download Consumption Against Asset in pdf format.
 * For details go through Use-Case doc named 'DownloadForConsumptionAgainstAssetActionService'
 */
class DownloadForConsumptionAgainstAssetActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download Fixed Asset Consumption Report."
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'consumptionAgainstAsset'
    private static final String OUTPUT_FILE_NAME = 'consumptionAgainstAsset'
    private static final String REPORT_TITLE = 'Consumption Against Fixed Asset Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'consumptionAgainstAsset.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String PROJECT_NOT_FOUND = "Project not found."
    private static final String PROJECT_ID = "projectId"
    private static final String PROJECT_NAME = "projectName"
    private static final String TRANSACTION_TYPE_CONSUMPTION = "transactionTypeConsumption"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"

    /**
     * 1. pull project object
     * 2. check project existence
     * @param parameters - serialized parameters from UI.
     * @param obj - N/A
     * @return- a map containing project id & name and isError msg(True/False) and relevant msg(if any)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long projectId = Long.parseLong(params.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            String projectName = project.name

            result.put(PROJECT_ID, projectId)
            result.put(PROJECT_NAME, projectName)
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
     * Get all object for generating consumption against asset report.
     * 1. receive project id & name from pre method
     * 2. set transaction type = consumption
     * 3. get consumption against fixed asset for generating report
     * @param parameters -N/A
     * @param obj -object receive from pre execute method
     * @return - a map containing consumption against fixed asset object & isError msg(True/False)
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long transactionTypeConsumption = inventoryImplService.getInvTransactionTypeIdConsumption()
            long projectId = (long) preResult.get(PROJECT_ID)
            String projectName = preResult.get(PROJECT_NAME)
            Map report = getConsumptionFixedAssetReport(projectId, projectName, transactionTypeConsumption)
            result.put(REPORT, report)
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
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Generate report by given data
     * @param projectId - project id
     * @param projectName - project name
     * @param transactionTypeConsumption - transaction Type = Consumption
     * @return - generated report with required params
     */
    private Map getConsumptionFixedAssetReport(long projectId, String projectName, long transactionTypeConsumption) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getFixedAssetReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_ID, projectId)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(TRANSACTION_TYPE_CONSUMPTION, transactionTypeConsumption)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
