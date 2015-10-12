package com.athena.mis.accounting.actions.report.accFinancialStatement

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
 * Download  financial statement in PDF or Excel format for level 2
 * Report will contain  coa of tier1(if any) and sum of tier2
 * For details go through Use-Case doc named 'DownloadForFinancialStatementOfLevel2ActionService'
 */
class DownloadForFinancialStatementOfLevel2ActionService extends BaseService implements ActionIntf {

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
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Could not download financial statement"
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any projects"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accFinancialStatement' + File.separator + 'level2'
    private static final String OUTPUT_FILE_NAME = 'accFinancialStatementOfLevel2'
    private static final String REPORT_TITLE = 'accFinancialStatementOfLevel2'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String ACC_FINANCIAL_STATEMENT_REPORT = 'accFinancialStatementOfLevel2.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INCOME_STATEMENT_SUM_DR = "incomeStatementSumDr"
    private static final String INCOME_STATEMENT_SUM_CR = "incomeStatementSumCr"
    private static final String FINANCIAL_STATEMENT_TOTAL_DR = "financialStatementTotalDr"
    private static final String FINANCIAL_STATEMENT_GRAND_TOTAL_DR = "financialStatementGrandTotalDr"
    private static final String FINANCIAL_STATEMENT_TOTAL_CR = "financialStatementTotalCr"
    private static final String FINANCIAL_STATEMENT_GRAND_TOTAL_CR = "financialStatementGrandTotalCr"
    private static final String PROFIT = " (Profit)"
    private static final String LOSS = " (Loss)"
    private static final String STR_PROFIT_LOSS = "strProfitLoss"
    private static final String ALL_PROJECT = "All Projects"
    private static final String PROJECT_NAME = "projectName"
    private static final String DIVISION_NAME = "divisionName"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String ALL_DIVISION = "All Divisions"

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

            // check required fields
            if (!params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
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
     * Get report format type (e.g: .pdf)
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
            String divisionName = projectAndDivisionIdsMap.divisionName

            String formatType = params.formatType
            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            Map report = getFinancialStatementReport(fromDate, toDate, lstProjectIds, formatType, projectName, postedByParam, divisionId, divisionName)// get financial statement report

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
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Generate PDF or EXCEL report by given data
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIdList - list of  all project ids
     * @param formatType - format type comes from params, the format type may be .pdf
     * @param projectName - project name from execute method for the report
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param divisionId - id of division
     * @param divisionName - name of division
     * @return - generated report with required params
     */
    @Transactional(readOnly = true)
    private Map getFinancialStatementReport(Date fromDate, Date toDate, List<Long> projectIdList, String formatType, String projectName, long postedByParam, long divisionId, String divisionName) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.getFileExtension(formatType)
        JasperExportFormat jasperExportFormat = Tools.getFileType(formatType)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(DIVISION_NAME, divisionName)

        List<GroovyRowResult> lstFinancialStatement = getFinancialStatementList(fromDate, toDate, projectIdList, postedByParam, divisionId)

        // Now retrieve the dr_sum & cr_sum
        Map incomeStatementSumList = getIncomeStatementSum(fromDate, toDate, projectIdList, postedByParam, divisionId)  // get sum of income statement
        Map financialStatementSumList = getFinancialStatementSum(fromDate, toDate, projectIdList, postedByParam, divisionId)

        BigDecimal debitSum = incomeStatementSumList.income_statement_sum_dr
        BigDecimal creditSum = incomeStatementSumList.income_statement_sum_cr
        BigDecimal totalSum = creditSum - debitSum

        BigDecimal financialStatementSumDr = financialStatementSumList.financial_statement_sum_dr
        BigDecimal financialStatementSumCr = financialStatementSumList.financial_statement_sum_cr
        reportParams.put(FINANCIAL_STATEMENT_TOTAL_DR, Tools.makeAmountWithThousandSeparator(financialStatementSumDr))
        reportParams.put(FINANCIAL_STATEMENT_TOTAL_CR, Tools.makeAmountWithThousandSeparator(financialStatementSumCr))

        if (totalSum > 0) {
            financialStatementSumCr = financialStatementSumCr + totalSum.abs()
        } else {
            financialStatementSumDr = financialStatementSumDr + totalSum.abs()
        }
        String strProfitLoss = totalSum > 0 ? PROFIT : LOSS
        reportParams.put(STR_PROFIT_LOSS, strProfitLoss)

        reportParams.put(INCOME_STATEMENT_SUM_DR, (totalSum < 0 ? Tools.makeAmountWithThousandSeparator(totalSum.abs()) : Tools.EMPTY_SPACE))
        reportParams.put(INCOME_STATEMENT_SUM_CR, (totalSum > 0 ? Tools.makeAmountWithThousandSeparator(totalSum.abs()) : Tools.EMPTY_SPACE))
        reportParams.put(FINANCIAL_STATEMENT_GRAND_TOTAL_DR, Tools.makeAmountWithThousandSeparator(financialStatementSumDr))
        reportParams.put(FINANCIAL_STATEMENT_GRAND_TOTAL_CR, Tools.makeAmountWithThousandSeparator(financialStatementSumCr))
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)

        JasperReportDef reportDef = new JasperReportDef(name: ACC_FINANCIAL_STATEMENT_REPORT, fileFormat: jasperExportFormat,
                parameters: reportParams, folder: reportDir, reportData: lstFinancialStatement)

        //Generate a report based on jasper file
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: formatType]
    }

    /**
     * Calculate financial statement balance of level 2 - sum of Debit(Asset) and sum of Credit(Liability)
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIds - list of  all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param divisionId - id of division
     * @return - Map of financial statement list
     */
    private List<GroovyRowResult> getFinancialStatementList(Date fromDate, Date toDate, List<Long> projectIds, long postedByParam, long divisionId) {
        String lstProjectId = Tools.buildCommaSeparatedStringOfIds(projectIds)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object for ASSET and LIABILITIES
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)

        // check if specific division is given
        String subQueryDivision = Tools.EMPTY_SPACE // default value
        String subQueryProject = Tools.EMPTY_SPACE // default value
        if (divisionId > 0) {
            subQueryDivision = " AND details.division_id = ${divisionId} "
        } else {
            subQueryProject = " AND details.project_id IN (${lstProjectId}) "
        }
        String queryStr = """
            SELECT t1.name AS tier1_name, type.name AS acc_type_name,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId) THEN  (to_char(SUM(amount_dr-amount_cr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END str_dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId) THEN  (to_char(SUM(amount_cr-amount_dr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END str_cr_balance
            FROM acc_voucher_details details
            LEFT JOIN acc_chart_of_account coa ON coa.id = details.coa_id
            LEFT JOIN acc_voucher v ON v.id=details.voucher_id
            LEFT JOIN acc_tier1 t1 ON t1.id = coa.tier1
            LEFT JOIN acc_type type ON type.id = coa.acc_type_id
            WHERE v.voucher_date >= :fromDate
            AND v.voucher_date <= :toDate
            AND v.posted_by > :postedByParam
            ${subQueryProject} ${subQueryDivision}
            AND coa.tier1 > 0
            AND (coa.acc_type_id =:accTypeAssetId OR coa.acc_type_id =:accTypeLiabilitiesId)
            AND coa.company_id =:companyId
            GROUP BY coa.acc_type_id, type.name, t1.name, t1.id
            ORDER BY coa.acc_type_id, t1.name
        """
        Map queryParams = [
                accTypeAssetId: accTypeAsset.id,
                accTypeLiabilitiesId: accTypeLiabilities.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                postedByParam: postedByParam,
                companyId: companyId
        ]
        List<GroovyRowResult> financialStatementList = executeSelectSql(queryStr, queryParams)
        return financialStatementList
    }

    /**
     * Give sum of income statement by executing raw sql
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIdList - list of  all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param divisionId - id of division
     * @return - Map of income statement sum
     */
    private Map getIncomeStatementSum(Date fromDate, Date toDate, List<Long> projectIdList, long postedByParam, long divisionId) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object for EXPENSE and INCOME
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        // check if specific division is given
        String subQueryDivision = Tools.EMPTY_SPACE // default value
        String subQueryProject = Tools.EMPTY_SPACE // default value
        if (divisionId > 0) {
            subQueryDivision = " AND details.division_id = ${divisionId} "
        } else {
            subQueryProject = " AND details.project_id IN (${projectIds}) "
        }
        String queryDr = """
            SELECT  (to_char(SUM(dr.dr_balance),'${Tools.DB_CURRENCY_FORMAT}')) AS income_statement_sum_dr_str, SUM(dr.dr_balance) AS income_statement_sum_dr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeExpenseId) THEN SUM(amount_dr-amount_cr)
                    ELSE 0
                    END dr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND coa.company_id =:companyId
                    ${subQueryProject} ${subQueryDivision}
                    GROUP BY coa.acc_type_id,details.coa_id
            ) dr
        """

        String queryCr = """
            SELECT  (to_char(SUM(cr.cr_balance),'${Tools.DB_CURRENCY_FORMAT}')) AS income_statement_sum_cr_str, SUM(cr.cr_balance) AS income_statement_sum_cr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeIncomeId) THEN SUM(amount_cr-amount_dr)
                    ELSE 0
                    END cr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND coa.company_id =:companyId
                    ${subQueryProject} ${subQueryDivision}
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
                income_statement_sum_dr_str: tempDr[0].income_statement_sum_dr_str,
                income_statement_sum_dr: tempDr[0].income_statement_sum_dr,
                income_statement_sum_cr_str: tempCr[0].income_statement_sum_cr_str,
                income_statement_sum_cr: tempCr[0].income_statement_sum_cr
        ]
        return incomeStatementSum
    }

    /**
     * Calculate financial statement balance - sum of Debit(Asset) and sum of Credit(Liability)
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIdList - list of  all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @param divisionId - id of division
     * @return - Map of financial statement sum
     */
    private Map getFinancialStatementSum(Date fromDate, Date toDate, List<Long> projectIds, long postedByParam, long divisionId) {
        String lstProjectId = Tools.buildCommaSeparatedStringOfIds(projectIds)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object for ASSET and LIABILITIES
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)

        // check if specific division is given
        String subQueryDivision = Tools.EMPTY_SPACE // default value
        String subQueryProject = Tools.EMPTY_SPACE // default value
        if (divisionId > 0) {
            subQueryDivision = " AND details.division_id = ${divisionId} "
        } else {
            subQueryProject = " AND details.project_id IN (${lstProjectId}) "
        }
        String queryDr = """
            SELECT SUM(dr.dr_balance) AS financial_statement_sum_dr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeAssetId) THEN SUM(amount_dr-amount_cr)
                    ELSE 0
                    END dr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND coa.company_id =:companyId
                    ${subQueryProject} ${subQueryDivision}
                    GROUP BY coa.acc_type_id,details.coa_id
            ) dr
        """

        String queryCr = """
            SELECT SUM(cr.cr_balance) AS financial_statement_sum_cr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeLiabilitiesId) THEN SUM(amount_cr-amount_dr)
                    ELSE 0
                    END cr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND coa.company_id = :companyId
                    ${subQueryProject} ${subQueryDivision}
                    GROUP BY coa.acc_type_id,details.coa_id
            ) cr
        """

        Map queryParams = [
                accTypeAssetId: accTypeAsset.id,
                accTypeLiabilitiesId: accTypeLiabilities.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                postedByParam: postedByParam,
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> tempDr = executeSelectSql(queryDr, queryParams)
        List<GroovyRowResult> tempCr = executeSelectSql(queryCr, queryParams)

        Map financialStatementSum = [
                financial_statement_sum_dr: tempDr[0].financial_statement_sum_dr,
                financial_statement_sum_cr: tempCr[0].financial_statement_sum_cr
        ]
        return financialStatementSum
    }

    /**
     * Get list of project ids, list of division ids, project name and division name
     * @param projectId -id of project
     * @param divisionId -id of division
     * @return -a map containing list of project ids, list of division ids, project name and division name
     */
    private Map getProjectAndDivisionInfoMap(long projectId, long divisionId) {
        String errorMsg = Tools.EMPTY_SPACE //default value
        String projectName = ALL_PROJECT  //default value
        String divisionName = ALL_DIVISION  //default value

        List<Long> lstProjectIds = []

        if ((projectId > 0) && (divisionId > 0)) { //specific project and specific division
            lstProjectIds << projectId
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name

            AccDivision division = (AccDivision) accDivisionCacheUtility.read(divisionId)
            divisionName = division.name
        } else if ((projectId > 0) && (divisionId <= 0)) { //specific project and all divisions
            lstProjectIds << projectId
            Project project = (Project) projectCacheUtility.read(projectId)
            projectName = project.name

        } else if (projectId <= 0) { //for all projects
            lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            if (lstProjectIds.size() <= 0) { //if user has no mapped project then return with message
                errorMsg = USER_HAS_NO_PROJECT
            }
        }
        return [errorMsg: errorMsg, lstProjectIds: lstProjectIds, projectName: projectName, divisionName: divisionName]
    }
}
