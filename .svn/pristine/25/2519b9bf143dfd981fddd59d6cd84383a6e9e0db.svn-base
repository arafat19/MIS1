package com.athena.mis.qs.actions.report.budgetcontractdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Download CSV report of Budget Contract Details
 * For details go through Use-Case doc named 'DownloadForBudgetContractCsvDetailsActionService'
 */
class DownloadForBudgetContractCsvDetailsActionService extends BaseService implements ActionIntf {

    protected final Logger log = Logger.getLogger(getClass())
    JasperService jasperService

    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download Budget Contract Details Report."
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'budgetContractDetails'
    private static final String OUTPUT_FILE_NAME = 'budgetContractCsvDetails'
    private static final String REPORT_TITLE = 'Budget Contract Details Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'budgetContractCsvDetails.jasper'
    private static final String CSV_EXTENSION = '.csv'
    private static final String REPORT = "report"
    private static final String PROJECT_ID = "projectId"
    private static final String PROJECT_NAME = "projectName"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_NOT_FOUND = "Project not found."
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"

    /**
     * Get project id, from date & to date from UI.
     * 1. Get project id as long from params
     * 2. Get project object from projectCacheUtility
     * 3. Check existence of project object
     * 4. Get from date from parseMaskedFromDate method of DateUtility
     * 5. Get to date from parseMaskedToDate method of DateUtility
     * 6. Get project name from project object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all necessary things & isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long projectId = Long.parseLong(params.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            // Checking existence of project object
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            String projectName = project.name
            result.put(PROJECT_ID, projectId)
            result.put(PROJECT_NAME, projectName)
            result.put(FROM_DATE, fromDate.toTimestamp())
            result.put(TO_DATE, toDate.toTimestamp())
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
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object result) {
        return null
    }

    /**
     * Generate report and put it to the map
     * 1. Get project id, project name, from date, to date from executePreCondition
     * 2. Get map of report by getBudgetContractDetailsCsvReport method
     * @param parameters - N/A
     * @param obj - receive map from executePreCondition
     * @return -  a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long projectId = (long) preResult.get(PROJECT_ID)
            String projectName = (String) preResult.get(PROJECT_NAME)
            Date fromDate = (Date) preResult.get(FROM_DATE)
            Date toDate = (Date) preResult.get(TO_DATE)
            Map report = getBudgetContractDetailsCsvReport(projectId, fromDate, toDate, projectName)  // get budget Contract Details CSV report
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
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Generate Budget Contract Details CSV report
     * @param projectId - Get projectId from UI
     * @param fromDate -  Get fromDate from UI
     * @param toDate - Get toDate from UI
     * @param projectName - Get project name from project object
     * @return - generated report with required params
     */
    private Map getBudgetContractDetailsCsvReport(long projectId, Date fromDate, Date toDate, String projectName) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getQsReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_ID, projectId)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)

        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT_CSV)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT_CSV)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
