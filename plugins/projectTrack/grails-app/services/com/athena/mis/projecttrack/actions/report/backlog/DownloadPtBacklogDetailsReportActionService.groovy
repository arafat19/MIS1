package com.athena.mis.projecttrack.actions.report.backlog

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.projecttrack.entity.PtBacklog
import com.athena.mis.projecttrack.service.PtBacklogService
import com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DownloadPtBacklogDetailsReportActionService extends BaseService implements ActionIntf {

    PtBacklogService ptBacklogService
    JasperService jasperService
    @Autowired
    PtSessionUtil ptSessionUtil
    @Autowired
    PtAcceptanceCriteriaTypeCacheUtility ptAcceptanceCriteriaTypeCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'useCase'
    private static final String JASPER_FILE = 'useCase.jasper'
    private static final String REPORT_TITLE = 'Use Case'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String NOT_FOUND_MSG = "Use case not found"
    private static final String ERROR_MSG = "Failed to generate use case report"
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = "backlogDetails"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String BACKLOG_ID = "backlogId"
    private static final String POST_CONDITION_ID = "postConditionId"
    private static final String PRE_CONDITION_ID = "preConditionId"
    private static final String BUSINESS_LOGIC_ID = "businessLogicId"
    private static final String OTHERS_ID = "othersId"
    private static final String COMPANY_ID = "companyId"
    private static final String ENTITY_ID = "entityId"
    private static final String ENTITY_TYPE_ID = "entityTypeId"

    private Logger log = Logger.getLogger(getClass())

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
            if ((!params.backlogId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long backlogId = Long.parseLong(params.backlogId)
            PtBacklog backlog = ptBacklogService.read(backlogId)
            if(!backlog) {
                result.put(Tools.MESSAGE, NOT_FOUND_MSG)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MSG)
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
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long backlogId = Long.parseLong(parameterMap.backlogId)
            Map report = getUseCaseReport(backlogId)
            result.put(REPORT, report)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error ex.getMessage()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from execute method
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ERROR_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_MSG)
            return result
        }
    }

    /**
     * Generate report
     * @param backlogId -id of backlog
     * @return -a map containing the generated report
     */
    private Map getUseCaseReport(long backlogId) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity postCondition = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.readByReservedAndCompany(ptAcceptanceCriteriaTypeCacheUtility.POST_CONDITION_RESERVED_ID, companyId)
        SystemEntity preCondition = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.readByReservedAndCompany(ptAcceptanceCriteriaTypeCacheUtility.PRE_CONDITION_RESERVED_ID, companyId)
        SystemEntity businessLogic = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.readByReservedAndCompany(ptAcceptanceCriteriaTypeCacheUtility.BUSINESS_LOGIC_RESERVED_ID, companyId)
        SystemEntity others = (SystemEntity) ptAcceptanceCriteriaTypeCacheUtility.readByReservedAndCompany(ptAcceptanceCriteriaTypeCacheUtility.OTHERS_RESERVED_ID, companyId)

        PtBacklog backlog = ptBacklogService.read(backlogId)
        SystemEntity noteEntityType = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_PT_TASK, backlog.companyId)

        String reportDir = Tools.getProjectTrackReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        String subReportDir = reportDir + File.separator
        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(BACKLOG_ID, backlogId)
        reportParams.put(POST_CONDITION_ID, postCondition.id)
        reportParams.put(PRE_CONDITION_ID, preCondition.id)
        reportParams.put(BUSINESS_LOGIC_ID, businessLogic.id)
        reportParams.put(OTHERS_ID, others.id)
        reportParams.put(COMPANY_ID, backlog.companyId)
        reportParams.put(ENTITY_ID, backlog.id)
        reportParams.put(ENTITY_TYPE_ID, noteEntityType.id)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)           // generate pdf report
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
