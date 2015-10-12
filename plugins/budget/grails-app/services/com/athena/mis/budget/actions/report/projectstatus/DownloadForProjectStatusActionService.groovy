package com.athena.mis.budget.actions.report.projectstatus

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.budget.utility.BudgSessionUtil
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.qsmeasurement.QsMeasurementPluginConnector
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
 * Download PDF of Project Status
 * For details go through Use-Case doc named 'DownloadForConsumptionDeviationActionService'
 */
class DownloadForProjectStatusActionService extends BaseService implements ActionIntf {


    InventoryPluginConnector inventoryImplService
    QsMeasurementPluginConnector qsMeasurementImplService
    JasperService jasperService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    BudgSessionUtil budgSessionUtil

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'projectStatus.jasper'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT = "report"
    private static final String PROJECT_NOT_FOUND = "Project not found"
    private static final String REPORT_FOLDER = 'projectStatus'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String OUTPUT_FILE_NAME = 'projectStatus'
    private static final String REPORT_TITLE = 'Project-Status'
    private static final String FAILURE_MSG = "Fail to generate Project Status."
    private static final String USER_HAS_NOT_ACCESS = "User is not associated with this project"
    private static final String PROJECT_STATUS_MAP = "projectStatusMap"
    private static final String PROJECT_OBJ = "project"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get Project
     * 1. Checking the mapping between user and project by isAccessible method of projectCacheUtility
     * 2. Get project ids which is mapped with login user
     * 3. Checking the existence of project
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            if (!params.projectId) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            List<Long> userProjectIds = (List<Long>) budgSessionUtil.appSessionUtil.getUserProjectIds()
            // checking the login user has the access to this project weather yes or not
            if (userProjectIds.size() <= 0) {
                result.put(Tools.MESSAGE, USER_HAS_NOT_ACCESS)
                return result
            }

            long projectId = Long.parseLong(params.projectId.toString())
            Project project = (Project) projectCacheUtility.read(projectId)
            // Checking the existence of project
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            boolean isAccessible = projectCacheUtility.isAccessible(project.id)
            if (!isAccessible) {
                result.put(Tools.MESSAGE, USER_HAS_NOT_ACCESS)
                return result
            }

            result.put(PROJECT_OBJ, project)
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
     * 1. Get Project object from executePreCondition
     * 2. Generate report map by getProjectStatusReport method
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            Project project = (Project) preResult.get(PROJECT_OBJ)

            Map report = getProjectStatusReport(project)  // get project status report
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
     * Build project map for pdf report
     * 1. Get gross receivable QS from getGrossReceivableQs method of qsMeasurementImplService using project id
     * 2. Get total billable budget amount by getBillableBudgetAmountByProject method using project id
     * @param project - object of project from getProjectStatusReport method
     * @return - a new map of project
     */
    private LinkedHashMap buildProjectMap(Project project) {
        Map grossReceivableQs = qsMeasurementImplService.getGrossReceivableQs(project.id)

        double totalBillableBudgetAmount = (double) getBillableBudgetAmountByProject(project.id)
        double totalGrossReceivable = grossReceivableQs.grossReceivableInternal
        double totalProjectCompletedInPercentage = 0d
        if (totalBillableBudgetAmount > 0) {
            totalProjectCompletedInPercentage = (totalGrossReceivable / totalBillableBudgetAmount) * 100
        }

        LinkedHashMap projectMap = [
                projectId: project.id,
                projectName: project.name,
                projectCode: project.code,
                projectDescription: project.description,
                printDate: DateUtility.getDateFormatAsString(new Date()),
                projectCompleted: Tools.formatAmountWithoutCurrency(totalProjectCompletedInPercentage) + Tools.PERCENTAGE,
                invTotalMaterial: inventoryImplService.getMaterialStockBalanceByProjectId(project.id),
                invTotalFixedAsset: inventoryImplService.getFixedAssetStockBalanceByProjectId(project.id),
                invTotalApprovedConsumption: inventoryImplService.getTotalApprovedConsumptionByProjectId(project.id),
                qsTotalReceivableInternal: grossReceivableQs.grossReceivableInternalStr,
                qsTotalReceivableApprovedByGov: grossReceivableQs.grossReceivableGovStr
        ]
        return projectMap
    }

    /**
     * Generate consumption deviation CSV report
     * @param project - object of project from execute
     * @return - generated PDF report with required params
     */
    private Map getProjectStatusReport(Project project) {

        String reportDir = Tools.getBudgetReportDirectory() + File.separator + REPORT_FOLDER
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        LinkedHashMap projectStatusMap = buildProjectMap(project)
        reportParams.put(PROJECT_STATUS_MAP, projectStatusMap)

        String outputFileName = OUTPUT_FILE_NAME + project.id.toString() + PDF_EXTENSION
        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    private static final String QUERY_BUDGET_AMOUNT_CAL = """
        SELECT COALESCE(SUM(budget_quantity * contract_rate),0) AS budget_amount
        FROM budg_budget WHERE project_id=:projectId AND billable = true
    """

    /**
     * Get billable budget amount by project
     * @param projectId - get project id from buildProjectMap method
     * @return - budget amount
     */
    private double getBillableBudgetAmountByProject(long projectId) {
        Map queryParams = [projectId: projectId]
        List result = executeSelectSql(QUERY_BUDGET_AMOUNT_CAL, queryParams)
        double amount = result[0].budget_amount
        return amount
    }
}