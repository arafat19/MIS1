package com.athena.mis.accounting.actions.report.projectfundflow

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SysConfiguration
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
 * Download project fund flow report in pdf format
 * For details go through Use-Case doc named 'DownloadForProjectFundFlowActionService'
 */
class DownloadForProjectFundFlowActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private static final String FAILURE_MSG = "Failed to generate project fund flow report"
    private static final String REPORT_FOLDER = 'projectFundFlow'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String REPORT = "report"
    private static final String OUTPUT_FILE_NAME = 'projectFundFlow'
    private static final String TO_DATE = "toDate"
    private static final String FROM_DATE = "fromDate"
    private static final String REPORT_FILE_FORMAT_PDF = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String PDF_EXTENSION = ".pdf"
    private static final String REPORT_TITLE = 'Project Fund Flow'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE = 'projectFundFlow.jasper'
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String PROJECT_IDS = "projectIds"
    private static final String GROUP_IDS = "groupIds"
    private static final String NO_GROUP_MSG = "No group found"
    private static final String PROJECT_NAME = "projectName"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check required input fields from UI
     * Check date range with financial year
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.toDate) || (!params.fromDate)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) { // check given date range with current financial year
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(fromDate, toDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }
            result.put(TO_DATE, toDate)
            result.put(FROM_DATE, fromDate)
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
     * @param parameters -serialized parameters from UI
     * @param obj -object received from executePreCondition
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            GrailsParameterMap params = (GrailsParameterMap) parameters

            Date toDate = (Date) preResult.get(TO_DATE)
            Date fromDate = (Date) preResult.get(FROM_DATE)

            List<Long> projectIds = []  //main list of projectIds
            String projectName
            if (params.projectId.equals(Tools.EMPTY_SPACE)) {
                projectName = Tools.ALL
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) {
                    //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(params.projectId.toString())
                projectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Un-posted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            List<Long> groupIds = accGroupCacheUtility.listOfActiveGroupIds()
            if (groupIds.size() <= 0) {
                result.put(Tools.MESSAGE, NO_GROUP_MSG)
                return result
            }
            Map report = getProjectFundFlowReport(toDate, fromDate, postedByParam, projectIds, projectName, groupIds)

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
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for success operation
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
     * Generate report by given data
     * @param toDate -current date(today)
     * @param fromDate -start date
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @param projectIds -all project ids
     * @param projectName -project name
     * @param groupIds -list of group ids
     * @return -generated report
     */
    private Map getProjectFundFlowReport(Date toDate, Date fromDate, long postedByParam, List<Long> projectIds, String projectName, List<Long> groupIds) {
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(PROJECT_IDS, projectIds)
        reportParams.put(GROUP_IDS, groupIds)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT_PDF]
    }
}
