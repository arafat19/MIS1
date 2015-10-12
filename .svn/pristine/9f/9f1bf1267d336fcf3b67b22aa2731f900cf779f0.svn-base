package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBacklogStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  download sprint details report
 *  For details go through Use-Case doc named 'PtDownloadSprintDetailsActionService'
 */
class PtDownloadSprintDetailsActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    PtSprintService ptSprintService
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtBacklogStatusCacheUtility ptBacklogStatusCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'sprintDetails'
    private static final String JASPER_FILE = 'sprintDetails.jasper'
    private static final String JASPER_FILE_WITH_OWNER = 'sprintDetailsWithOwner.jasper'
    private static final String JASPER_FILE_WITHOUT_OWNER = 'sprintDetailsWithoutOwner.jasper'
    private static final String REPORT_TITLE = 'Sprint Details'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String ERROR_INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String ERROR_EXCEPTION = "Failed to generate sprint details report"
    private static final String REPORT = "report"
    private static final String SPRINT_NAME = "sprintName"
    private static final String PROJECT_NAME = "projectName"
    private static final String OUTPUT_FILE_NAME = "sprintDetails"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String SPRINT_ID = "sprintId"
    private static final String HAS_OWNER = "hasOwner"
    private static final String FOR_ALL = "all"
    private static final String COMPANY_ID = "companyId"

    /**
     * Get parameters from UI
     * @param parameters -serialized parameter from UI
     * @param obj -N/A
     * @return-a map containing necessary information for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if ((!params.sprintId)) {
                result.put(Tools.MESSAGE, ERROR_INVALID_INPUT)
                return result
            }
            long sprintId = Long.parseLong(params.sprintId.toString())
            boolean hasOwner = Boolean.parseBoolean(params.hasOwner.toString()) // defines tasks for only owner or without owner
            boolean all = Boolean.parseBoolean(params.forAll.toString())  // defines for all tasks(both owner/without owner)
            PtSprint sprint = ptSprintService.read(sprintId)
            long projectId = sprint.projectId
            PtProject project = (PtProject) ptProjectCacheUtility.read(projectId)
            result.put(SPRINT_ID, sprintId.toLong())
            result.put(PROJECT_NAME, project.name)
            result.put(SPRINT_NAME, sprint.name)
            result.put(HAS_OWNER, hasOwner)
            result.put(FOR_ALL, all)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            return result
        }
    }

    /**
     * Generates report
     * @param params -N/A
     * @param obj -a map returned from previous method
     * @return-a map containing all necessary information for downloading report
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map report = new LinkedHashMap()
            Map preResult = (Map) obj
            String sprintName = (String) preResult.get(SPRINT_NAME)
            String projectName = (String) preResult.get(PROJECT_NAME)
            long sprintId = (long) preResult.get(SPRINT_ID)
            boolean hasOwner = (boolean) preResult.get(HAS_OWNER)
            boolean all = (boolean) preResult.get(FOR_ALL)
            if(hasOwner){
                 report = getSprintDetailsWithOwnerReport(sprintName, projectName, sprintId)
            }else if(all){
                report = getSprintDetailsReport(sprintName, projectName, sprintId)
            }else{
                 report = getSprintDetailsWithOutOwnerReport(sprintName, projectName, sprintId)
            }

            result.put(REPORT, report)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error ex.getMessage()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }
    // both for owner & without owner
    private Map getSprintDetailsReport(String sprintName, String projectName, long sprintId) {

        String reportDir = Tools.getProjectTrackReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(SPRINT_NAME, sprintName)
        reportParams.put(SPRINT_ID, sprintId.toLong())
        reportParams.put(COMPANY_ID, ptSessionUtil.appSessionUtil.getCompanyId())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)           // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    // only for owner
    private Map getSprintDetailsWithOwnerReport(String sprintName, String projectName, long sprintId) {

        String reportDir = Tools.getProjectTrackReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(SPRINT_NAME, sprintName)
        reportParams.put(SPRINT_ID, sprintId.toLong())
        reportParams.put(COMPANY_ID, ptSessionUtil.appSessionUtil.getCompanyId())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_WITH_OWNER, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)           // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
    // only for without owner
    private Map getSprintDetailsWithOutOwnerReport(String sprintName, String projectName, long sprintId) {

        String reportDir = Tools.getProjectTrackReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(SPRINT_NAME, sprintName)
        reportParams.put(SPRINT_ID, sprintId.toLong())
        reportParams.put(COMPANY_ID, ptSessionUtil.appSessionUtil.getCompanyId())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_WITHOUT_OWNER, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)           // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

}
