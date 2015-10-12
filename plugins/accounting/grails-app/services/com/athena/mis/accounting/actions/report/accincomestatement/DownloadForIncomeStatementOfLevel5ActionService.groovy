package com.athena.mis.accounting.actions.report.accincomestatement

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccDivisionCacheUtility
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download income statement in pdf format-
 * for level 5(show coa of all(tier1, tier2,tier3,tier4,tier5(if any)).
 * For details go through Use-Case doc named 'DownloadForIncomeStatementOfLevel5ActionService'
 */
class DownloadForIncomeStatementOfLevel5ActionService extends BaseService implements ActionIntf {

    JasperService jasperService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Could not download income statement"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accIncomeStatement'
    private static final String LEVEL5 = 'level5'
    private static final String OUTPUT_FILE_NAME = 'accIncomeStatementOfHierarchy5'
    private static final String REPORT_TITLE = 'accIncomeStatementOfHierarchy5'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String REPORT = "report"
    private static final String ACC_INCOME_STATEMENT_REPORT = 'accIncomeStatementOfHierarchy5.jasper'
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any projects"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String INCOME_STATEMENT_TOTAL_DR = "incomeStatementTotalDr"
    private static final String INCOME_STATEMENT_TOTAL_CR = "incomeStatementTotalCr"
    private static final String PROJECT_ID_LIST = "projectIdList"
    private static final String DIVISION_ID_LIST = "divisionIdList"
    private static final String ALL_PROJECT = "All Projects"
    private static final String ALL_DIVISION = "All Divisions"
    private static final String PROJECT_NAME = "projectName"
    private static final String DIVISION_NAME = "divisionName"
    private static final String PROFIT = " (Profit)"
    private static final String LOSS = " (Loss)"
    private static final String STR_PROFIT_LOSS = "strProfitLoss"
    private static final String TOTAL_SUM = "totalSum"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String ACC_TYPE_EXPENSES_ID = "accTypeExpensesId"
    private static final String ACC_TYPE_INCOME_ID = "accTypeIncomeId"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check all pre conditions for input data
     * @param parameters - serialized parameters received from UI.
     * @param obj - N/A
     * @return - a map containing income statement object and isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate)   // convert String to Date & format(dd/MM/yyyy)
            Date endDate = DateUtility.parseMaskedDate(params.toDate)      // convert String to Date & format(dd/MM/yyyy)
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()   // get current financial year
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
     * Get desired report providing all required parameters
     * @param parameters -serialized parameters from UI
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

            long projectId = Long.parseLong(params.projectId.toString())
            long divisionId = Long.parseLong(params.divisionId.toString())

            Map projectAndDivisionIdsMap = getProjectAndDivisionInfoMap(projectId, divisionId)
            String errorMessage = projectAndDivisionIdsMap.errorMsg
            if (errorMessage) {
                result.put(Tools.MESSAGE, errorMessage)
                return result
            }
            List<Long> lstProjectIds = projectAndDivisionIdsMap.lstProjectIds
            String projectName = projectAndDivisionIdsMap.projectName

            List<Long> lstDivisionIds = projectAndDivisionIdsMap.lstDivisionIds
            String divisionName = projectAndDivisionIdsMap.divisionName

            String formatType = params.formatType
            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            Map report = getIncomeStatementReport(fromDate, toDate, lstProjectIds, lstDivisionIds, formatType, projectName, divisionName, postedByParam)

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
     * do nothing for success operation
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
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Generate report by given data
     * @param fromDate - start date for report
     * @param toDate - current date
     * @param projectIdList - project ids
     * @param formatType - pdf/csv
     * @param projectName - project name
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - generated report with required params
     */
    @Transactional(readOnly = true)
    private Map getIncomeStatementReport(Date fromDate, Date toDate, List<Long> projectIdList, List<Long> divisionIdList,
                                         String formatType, String projectName, String divisionName, long postedByParam) {
        Map reportParams = new LinkedHashMap()
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object EXPENSE and INCOME
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER + File.separator + LEVEL5
        String subReportDir = reportDir + File.separator
        String outputFileName = OUTPUT_FILE_NAME + Tools.getFileExtension(formatType)
        JasperExportFormat jasperExportFormat = Tools.getFileType(formatType)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_ID_LIST, projectIdList)
        reportParams.put(DIVISION_ID_LIST, divisionIdList)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(DIVISION_NAME, divisionName)
        reportParams.put(COMPANY_ID, companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)

        // Now retrieve the dr_sum & cr_sum
        Map incomeStatementSumList = getIncomeStatementSum(fromDate, toDate, projectIdList, divisionIdList, postedByParam)

        BigDecimal incomeStatementTotalDr = incomeStatementSumList.income_statement_sum_dr
        BigDecimal incomeStatementTotalCr = incomeStatementSumList.income_statement_sum_cr
        BigDecimal totalSum = incomeStatementTotalCr - incomeStatementTotalDr

        reportParams.put(INCOME_STATEMENT_TOTAL_DR, Tools.makeAmountWithThousandSeparator(incomeStatementTotalDr))
        reportParams.put(INCOME_STATEMENT_TOTAL_CR, Tools.makeAmountWithThousandSeparator(incomeStatementTotalCr))

        String strProfitLoss = totalSum > 0 ? PROFIT : LOSS
        reportParams.put(STR_PROFIT_LOSS, strProfitLoss)
        reportParams.put(TOTAL_SUM, Tools.makeAmountWithThousandSeparator(Math.abs(totalSum)))

        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        reportParams.put(ACC_TYPE_EXPENSES_ID, accTypeExpense.id)
        reportParams.put(ACC_TYPE_INCOME_ID, accTypeIncome.id)

        JasperReportDef reportDef = new JasperReportDef(name: ACC_INCOME_STATEMENT_REPORT, fileFormat: jasperExportFormat,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: formatType]
    }
    /**
     * Get sum of income statement
     * @param fromDate - start date
     * @param toDate - end date
     * @param projectIds - project ids
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - a map containing sum of debit & credit of income statement
     */
    private Map getIncomeStatementSum(Date fromDate, Date toDate, List<Long> projectIds, List<Long> divisionIdList, long postedByParam) {
        String lstProjectId = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String divisionIds = Tools.buildCommaSeparatedStringOfIds(divisionIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object EXPENSE and INCOME
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        String queryDr = """
            SELECT SUM(dr.dr_balance) AS income_statement_sum_dr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id= :accTypeExpenseId) THEN SUM(amount_dr-amount_cr)
                    ELSE 0
                    END dr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND coa.company_id = :companyId
                    AND details.project_id IN (${lstProjectId})
                    AND details.division_id IN (${divisionIds})
                    GROUP BY coa.acc_type_id,details.coa_id
            ) dr
        """

        String queryCr = """
            SELECT SUM(cr.cr_balance) AS income_statement_sum_cr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id= :accTypeIncomeId) THEN SUM(amount_cr-amount_dr)
                    ELSE 0
                    END cr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND coa.company_id = :companyId
                    AND details.project_id IN (${lstProjectId})
                    AND details.division_id IN (${divisionIds})
                    GROUP BY coa.acc_type_id,details.coa_id
            ) cr
        """

        Map queryParams = [
                accTypeExpenseId: accTypeExpense.id,
                accTypeIncomeId: accTypeIncome.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                postedByParam: postedByParam,
                companyId: companyId

        ]
        List<GroovyRowResult> tempDr = executeSelectSql(queryDr, queryParams)
        List<GroovyRowResult> tempCr = executeSelectSql(queryCr, queryParams)

        Map incomeStatementSum = [
                income_statement_sum_dr: tempDr[0].income_statement_sum_dr,
                income_statement_sum_cr: tempCr[0].income_statement_sum_cr
        ]
        return incomeStatementSum
    }

    private Map getProjectAndDivisionInfoMap(long projectId, long divisionId) {
        String errorMsg = Tools.EMPTY_SPACE //default value
        String projectName = ALL_PROJECT  //default value
        String divisionName = ALL_DIVISION  //default value

        List<Long> lstProjectIds = []
        List<Long> lstDivisionIds = []

        if ((projectId > 0) && (divisionId > 0)) { //specific project and specific division
            lstProjectIds << projectId
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name

            lstDivisionIds << divisionId
            AccDivision division = (AccDivision) accDivisionCacheUtility.read(divisionId)
            divisionName = division.name
        } else if ((projectId > 0) && (divisionId <= 0)) { //specific project and all divisions
            lstProjectIds << projectId
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name

            lstDivisionIds = accDivisionCacheUtility.listActiveDivisionIdsByProjectIds(lstProjectIds)
        } else if (projectId <= 0) { //for all projects
            lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            if (lstProjectIds.size() <= 0) { //if user has no mapped project then return with message
                errorMsg = USER_HAS_NO_PROJECT
            } else {//if any user-mapped project found
                lstDivisionIds = accDivisionCacheUtility.listActiveDivisionIdsByProjectIds(lstProjectIds)
            }
        }
        return [errorMsg: errorMsg, lstProjectIds: lstProjectIds, projectName: projectName, lstDivisionIds: lstDivisionIds, divisionName: divisionName]
    }
}
