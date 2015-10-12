package com.athena.mis.accounting.actions.report.acccustomgroupbalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTypeCacheUtility
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
 * Download  custom group balance in CSV format
 * For details go through Use-Case doc named 'AccDownloadForCustomGroupBalanceCsvActionService'
 */
class AccDownloadForCustomGroupBalanceCsvActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download custom group balance"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accCustomGroupBalance'
    private static final String OUTPUT_FILE_NAME = 'customGroupBalanceCsv'
    private static final String REPORT_TITLE = 'Custom Group Balance CSV'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String CUSTOM_GROUP_BALANCE_JASPER = 'accCustomGroupBalanceCsv.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String PROJECT_ID_LIST = "projectIdList"
    private static final String ALL_PROJECT = "All Projects"
    private static final String PROJECT_NAME = "projectName"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String REPORT_FILE_FORMAT = 'csv'
    private static final String CSV_EXTENSION = ".csv"
    private static final String ACC_TYPE_ASSET = "accTypeAssetId"
    private static final String ACC_TYPE_EXPENSES = "accTypeExpensesId"
    private static final String ACC_TYPE_LIABILITIES = "accTypeLiabilitiesId"
    private static final String ACC_TYPE_INCOME = "accTypeIncomeId"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI.
     * Check existence of account financial year.
     * Check account financial year date range.
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            // check required params
            if (!params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate)
            Date endDate = DateUtility.parseMaskedDate(params.toDate)
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
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
     * Get desired report providing all required parameters.
     * Get all project Id(s).
     * Get project name.
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing report
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())

            List<Long> projectIdList = [] //main list of projectIds
            String projectName
            if (params.projectId.equals(Tools.EMPTY_SPACE)) {
                projectName = ALL_PROJECT
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIdList << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIdList = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(params.projectId.toString())
                projectIdList << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            }

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher*
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            Map report = getLedgerReport(fromDate, toDate, projectIdList, projectName, postedByParam)// get ledger report
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
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    /**
     * Generate CSV report by given data
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIdList - all project ids
     * @param projectName - project name from execute method for the report
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - generated report with required params
     */
    private Map getLedgerReport(Date fromDate, Date toDate, List<Long> projectIdList, String projectName, long postedByParam) {
        // get AccType object
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + CSV_EXTENSION
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_ID_LIST, projectIdList)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)
        reportParams.put(ACC_TYPE_ASSET, accTypeAsset.id)
        reportParams.put(ACC_TYPE_EXPENSES, accTypeExpense.id)
        reportParams.put(ACC_TYPE_LIABILITIES, accTypeLiabilities.id)
        reportParams.put(ACC_TYPE_INCOME, accTypeIncome.id)

        String jasperName = CUSTOM_GROUP_BALANCE_JASPER
        JasperReportDef reportDef = new JasperReportDef(name: jasperName, fileFormat: JasperExportFormat.CSV_FORMAT,
                parameters: reportParams, folder: reportDir)

        //Generate a report based on jasper file
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}