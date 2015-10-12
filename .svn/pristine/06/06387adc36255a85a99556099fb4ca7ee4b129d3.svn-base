package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

class DownloadDecisionSummaryReportActionService extends BaseService implements ActionIntf {

    JasperService jasperService

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Failed to generate task report"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String COMPANY_ID = "companyId"
    private static final String DECISION_TAKEN_ID = "decisionTakenId"
    private static final String DECISION_APPROVED_ID = "decisionApprovedId"
    private static final String DISBURSED_ID = "disbursedId"

    // report related variables
    private static final String REPORT_FOLDER = 'decisionSummary'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'decisionSummary'
    private static final String PDF_EXTENSION = ".pdf"

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'decisionSummary.jasper'

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.toDate) || (!params.fromDate)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date endDate = DateUtility.parseMaskedToDate(params.toDate)
            Timestamp fromDate = DateUtility.getSqlFromDateWithSeconds(startDate)
            Timestamp toDate = DateUtility.getSqlToDateWithSeconds(endDate)
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            Timestamp fromDate = (Timestamp) preResult.get(FROM_DATE)
            Timestamp toDate = (Timestamp) preResult.get(TO_DATE)
            Map report = getTaskReport(fromDate, toDate)
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

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!executeResult.message) {
                result.put(Tools.MESSAGE, FAILURE_MSG)
                return result
            }
            result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    private Map getTaskReport(Timestamp fromDate, Timestamp toDate) {
        Map reportParams = new LinkedHashMap()
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        String reportDir = Tools.getArmsReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())

        SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_TAKEN, companyId)
        SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
        SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DISBURSED, companyId)

        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(DECISION_TAKEN_ID, decisionTaken.id)
        reportParams.put(DECISION_APPROVED_ID, decisionApproved.id)
        reportParams.put(DISBURSED_ID, disbursed.id)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
