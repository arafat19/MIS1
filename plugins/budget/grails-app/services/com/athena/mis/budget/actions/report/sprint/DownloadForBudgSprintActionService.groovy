package com.athena.mis.budget.actions.report.sprint

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.budget.entity.BudgSprint
import com.athena.mis.budget.service.BudgSprintService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DownloadForBudgSprintActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    BudgSprintService budgSprintService
    @Autowired
    ProjectCacheUtility projectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String SPRINT_OBJ = "sprintObj"
    private static final String SPRINT_ID = "sprintId"
    private static final String SPRINT_NAME = "sprintName"
    private static final String PROJECT_NAME = "projectName"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String FAILURE_MSG = "Fail to download budget sprint report"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'budgSprint'
    private static final String OUTPUT_FILE_NAME = 'budgSprint'
    private static final String REPORT_TITLE = 'Sprint Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_PDF = 'budgSprint.jasper'
    private static final String REPORT = "report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String DB_QUANTITY_FORMAT = "dbQuantityFormat"
    private static final String START_DATE = "startDate"
    private static final String END_DATE = "endDate"

    /**
     * 1. check required parameters
     * 2. check existence of sprint object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            // check required parameters
            if (!params.sprintId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.sprintId.toString())
            BudgSprint sprint = budgSprintService.read(sprintId)
            // check existence of sprint object
            if (!sprint) {
                result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                return result
            }
            result.put(SPRINT_OBJ, sprint)
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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Generate report
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing generated report and isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            BudgSprint sprint = (BudgSprint) preResult.get(SPRINT_OBJ)
            Map report = getBudgSprintReport(sprint)
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
     * Do nothing for build success operation
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
     * Generate report
     * @param sprint -object of BudgSprint
     * @return -a map containing generated report
     */
    private Map getBudgSprintReport(BudgSprint sprint) {
        Project project = (Project) projectCacheUtility.read(sprint.projectId)
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getBudgetReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(SPRINT_ID, sprint.id)
        reportParams.put(SPRINT_NAME, sprint.name)
        reportParams.put(PROJECT_NAME, project.name)
        reportParams.put(DB_QUANTITY_FORMAT, Tools.DB_QUANTITY_FORMAT)
        reportParams.put(START_DATE, sprint.startDate)
        reportParams.put(END_DATE, sprint.endDate)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_PDF, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
