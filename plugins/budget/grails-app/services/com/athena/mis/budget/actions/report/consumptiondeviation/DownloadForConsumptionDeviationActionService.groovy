package com.athena.mis.budget.actions.report.consumptiondeviation

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download PDF of Consumption Deviation
 * For details go through Use-Case doc named 'DownloadForConsumptionDeviationActionService'
 */
class DownloadForConsumptionDeviationActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    JasperService jasperService
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    BudgSessionUtil budgSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download consumption deviation report"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'consumptionDeviation'
    private static final String OUTPUT_FILE_NAME = 'ConsumptionDeviation'
    private static final String REPORT_TITLE = 'Item Consumption Deviation'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String PROJECT_NAME = "projectName"
    private static final String PROJECT_ID = "projectId"
    private static final String REPORT_NAME = 'consumptionDeviation.jasper'
    private static final String COMPANY_ID = "companyId"
    private static final String TRANSACTION_TYPE_CONSUMPTION = "transactionTypeConsumption"
    private static final String ENTITY_TYPE_NONE = "entityTypeNone"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"

    /**
     * Checking the mapping between user and project by isAccessible method of projectCacheUtility
     * 1. Checking the existence of project by params.projectId
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

            long projectId = Long.parseLong(params.projectId.toString())
            boolean isAccessible = projectCacheUtility.isAccessible(projectId)
            if (!isAccessible) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }

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
     * 1. Pull project object by params project id
     * 2. Get map of report by getConsumptionDeviationReport method
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            Long projectId = Long.parseLong(params.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            Map report = getConsumptionDeviationReport(project)  // get consumption deviation report

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
     * do nothing for build success operation
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
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Generate consumption deviation report
     * @param project - object of project from execute
     * @return - generated report with required params
     */
    private Map getConsumptionDeviationReport(Project project) {
        Map reportParams = new LinkedHashMap()
        long companyId = budgSessionUtil.appSessionUtil.getCompanyId()
        String reportDir = Tools.getBudgetReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_ID, project.id)
        reportParams.put(PROJECT_NAME, project.name)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(TRANSACTION_TYPE_CONSUMPTION, inventoryImplService.getInvTransactionTypeIdConsumption())
        reportParams.put(ENTITY_TYPE_NONE, inventoryImplService.transactionEntityTypeIdNone)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: REPORT_NAME, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
