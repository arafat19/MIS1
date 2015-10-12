package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtSprint
import com.athena.mis.projecttrack.service.PtSprintService
import com.athena.mis.projecttrack.utility.PtBugStatusCacheUtility
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class DownloadPtBugDetailsReportActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    SystemEntityService systemEntityService
    PtSprintService ptSprintService
    @Autowired
    PtBugStatusCacheUtility ptBugStatusCacheUtility
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'bugDetails'
    private static final String JASPER_FILE = 'bugDetails.jasper'
    private static final String REPORT_TITLE = 'Bug Details'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String ERROR_INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String ERROR_EXCEPTION = "Failed to generate bug details report"
    private static final String BUG_STATUS_NOT_FOUND = "Bug status not found"
    private static final String SPRINT_NOT_FOUND = "Sprint not found"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "bugDetails"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String PROJECT_NAME = "projectName"
    private static final String STATUS_NAME = "statusName"
    private static final String SPRINT_NAME = "sprintName"
    private static final String STATUS_ID = "statusId"
    private static final String SPRINT_ID = "sprintId"

    /**
     * Get parameter from UI
     * @param parameters -serialized parameter from UI
     * @param obj -N/A
     * @return-a map containing necessary information for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long projectId = Long.parseLong(parameterMap.projectId)
            PtProject projectObj = (PtProject) ptProjectCacheUtility.read(projectId)

            List<Long> sprintIds = []
            if (parameterMap.sprintId.equals(Tools.EMPTY_SPACE)) {
                // get Sprint list by projectId
                List<PtSprint> lstSprint = ptSprintService.findAllByProjectIdAndCompanyId(projectId)

                if (lstSprint.size() == 0) {
                    result.put(Tools.MESSAGE, SPRINT_NOT_FOUND)
                    return result
                }
                for (int i = 0; i < lstSprint.size(); i++) {
                    sprintIds << lstSprint[i].id
                }
                result.put(SPRINT_NAME, Tools.ALL)
            } else {
                long sprintId = Long.parseLong(parameterMap.sprintId)
                sprintIds << new Long(sprintId)
                PtSprint sprint = ptSprintService.read(sprintId)
                result.put(SPRINT_NAME, sprint.name)
            }
            List<Long> bugStatusIds = []
            if (parameterMap.statusId.equals(Tools.EMPTY_SPACE)) {
                List lstBugStatus = ptBugStatusCacheUtility.list()

                if (lstBugStatus.size() == 0) {
                    result.put(Tools.MESSAGE, BUG_STATUS_NOT_FOUND)
                    return result
                }
                for (int i = 0; i < lstBugStatus.size(); i++) {
                    bugStatusIds << lstBugStatus[i].id
                }
                result.put(STATUS_NAME, Tools.ALL)
            } else {
                long bugStatusId = Long.parseLong(parameterMap.statusId)
                bugStatusIds << new Long(bugStatusId)
                SystemEntity statusObj = systemEntityService.read(bugStatusId)
                result.put(STATUS_NAME, statusObj.key)
            }
            result.put(SPRINT_ID, sprintIds)
            result.put(STATUS_ID, bugStatusIds)
            result.put(PROJECT_NAME, projectObj.name)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error ex.getMessage()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_EXCEPTION)
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
     * Generates report
     * @param params -N/A
     * @param obj -a map returned from previous method
     * @return-a map containing all necessary information for downloading report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            List<Long> sprintIds = (List<Long>) preResult.get(SPRINT_ID)
            List<Long> statusIds = (List<Long>) preResult.get(STATUS_ID)
            String projectName = preResult.get(PROJECT_NAME)
            String statusName = preResult.get(STATUS_NAME)
            String sprintName = preResult.get(SPRINT_NAME)

            Map report = getBugDetailsReport(projectName, sprintIds, sprintName, statusIds, statusName)


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

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    private Map getBugDetailsReport(String projectName, List<Long> sprintIds, String sprintName, List<Long> statusIds, String statusName) {
        String reportDir = Tools.getProjectTrackReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(SPRINT_ID, sprintIds)
        reportParams.put(STATUS_ID, statusIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(SPRINT_NAME, sprintName)
        reportParams.put(STATUS_NAME, statusName)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)           // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
