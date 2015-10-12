package com.athena.mis.projecttrack.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.projecttrack.entity.PtProject
import com.athena.mis.projecttrack.entity.PtProjectModule
import com.athena.mis.projecttrack.service.PtProjectModuleService
import com.athena.mis.projecttrack.utility.PtProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  download open backlog report
 *  For details go through Use-Case doc named 'DownloadOpenBacklogReportActionService'
 */
class DownloadOpenBacklogReportActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    PtProjectModuleService ptProjectModuleService
    @Autowired
    PtProjectCacheUtility ptProjectCacheUtility
    @Autowired
    PtSessionUtil ptSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'openBacklog'
    private static final String JASPER_FILE = 'openBacklog.jasper'
    private static final String REPORT_TITLE = 'Backlog'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String ERROR_INVALID_INPUT = "Error occurred for invalid inputs"
    private static final String ERROR_EXCEPTION = "Failed to generate open backlog details report"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "Backlog"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String MODULE_IDS = "moduleIds"
    private static final String NO_MODULE = "No module found"
    private static final String PROJECT_NAME = "projectName"

    /**
     * Get parameter(PtModule.id) from UI
     * @param parameters -serialized parameter from UI
     * @param obj -N/A
     * @return-a map containing necessary information for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.projectId) {
                result.put(Tools.MESSAGE, ERROR_INVALID_INPUT)
                return result
            }
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // set default value
            GrailsParameterMap params = (GrailsParameterMap) obj

            if (params.moduleId.equals(Tools.EMPTY_SPACE)) {
                params.moduleId = 0L
            }
            long moduleId = Long.parseLong(params.moduleId.toString())
            long projectId = Long.parseLong(params.projectId.toString())

            PtProject ptProject = (PtProject) ptProjectCacheUtility.read(projectId)
            String projectName = ptProject.name

            List<Long> lstModuleIds = []

            long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
            if (moduleId <= 0) {
                List<PtProjectModule> lstModule = ptProjectModuleService.findByProjectIdAndCompanyId(projectId, companyId)
                if (lstModule.size() == 0) {
                    result.put(Tools.MESSAGE, NO_MODULE)
                    return result
                }
                for (int i = 0; i < lstModule.size(); i++) {
                    lstModuleIds << lstModule[i].moduleId
                }
            } else {
                lstModuleIds << new Long(moduleId)
            }
            Map report
            report = getOpenBacklogReport(lstModuleIds, projectName)
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

    /**
     * do nothing for buildSuccess
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * do nothing for buildFailure
     */
    public Object buildFailureResultForUI(Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * generate report by module id
     * @param moduleId - PtModule.id
     * @return
     */
    private Map getOpenBacklogReport(List<Long> moduleIds, String projectName) {
        String reportDir = Tools.getProjectTrackReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(MODULE_IDS, moduleIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)           // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
