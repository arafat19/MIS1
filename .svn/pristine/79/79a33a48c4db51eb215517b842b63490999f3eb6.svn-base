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
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Download  custom group balance in pdf format
 * For details go through Use-Case doc named 'AccDownloadForCustomGroupBalanceActionService'
 */
class AccDownloadForCustomGroupBalanceActionService extends BaseService implements ActionIntf {

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

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download trail balance"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accCustomGroupBalance'
    private static final String OUTPUT_FILE_NAME = 'customGroupBalance'
    private static final String REPORT_TITLE = 'Custom Group Balance'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String CUSTOM_GROUP_BALANCE_JASPER = 'accCustomGroupBalance.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String CUSTOM_GROUP_BALANCE_SUM = "customGroupBalanceSum"
    private static final String PROJECT_ID_LIST = "projectIdList"
    private static final String ALL_PROJECT = "All Projects"
    private static final String PROJECT_NAME = "projectName"
    private static final String DB_CURRENCY_FORMAT = "dbCurrencyFormat"
    private static final String COMPANY_ID = "companyId"
    private static final String POSTED_BY_PARAM = "postedByParam"
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
     * Get report format type (e.g: .pdf or .xls)
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing report
     */
    @Transactional(readOnly = true)
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
            String formatType = params.formatType

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            Map report = getLedgerReport(fromDate, toDate, projectIdList, formatType, projectName, postedByParam)// get ledger report
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
     * Generate PDF or EXCEL report by given data
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIdList - list of  all project ids
     * @param formatType - format type comes from params, the format type may be .pdf or .xls
     * @param projectName - project name from execute method for the report
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - generated report with required params
     */
    private Map getLedgerReport(Date fromDate, Date toDate, List<Long> projectIdList, String formatType, String projectName, long postedByParam) {
        // get AccType object
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + Tools.getFileExtension(formatType)
        JasperExportFormat jasperExportFormat = Tools.getFileType(formatType)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_ID_LIST, projectIdList)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getCompanyId())
        reportParams.put(POSTED_BY_PARAM, postedByParam)
        reportParams.put(ACC_TYPE_ASSET, accTypeAsset.id)
        reportParams.put(ACC_TYPE_EXPENSES, accTypeExpense.id)
        reportParams.put(ACC_TYPE_LIABILITIES, accTypeLiabilities.id)
        reportParams.put(ACC_TYPE_INCOME, accTypeIncome.id)

        // Retrieve the debit sum & credit sum
        Map customGroupBalanceSum = getCustomGroupBalanceSum(fromDate, toDate, projectIdList, postedByParam)
        reportParams.put(CUSTOM_GROUP_BALANCE_SUM, customGroupBalanceSum)
        // Determine the report template
        String jasperName = CUSTOM_GROUP_BALANCE_JASPER
        reportParams.put(DB_CURRENCY_FORMAT, Tools.DB_CURRENCY_FORMAT)
        JasperReportDef reportDef = new JasperReportDef(name: jasperName, fileFormat: jasperExportFormat,
                parameters: reportParams, folder: reportDir)

        //Generate a report based on jasper file
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: formatType]
    }

    /**
     * Give sum of custom group balance by executing raw sql
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIdList - list of  all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - Map of custom group balance sum
     */
    private Map getCustomGroupBalanceSum(Date fromDate, Date toDate, List<Long> projectIdList, long postedByParam) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        String queryDr = """
            SELECT  (to_char(SUM(dr.dr_balance),'${Tools.DB_CURRENCY_FORMAT}')) AS custom_group_balance_sum_dr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeAssetId OR coa.acc_type_id=:accTypeExpenseId) THEN SUM(amount_dr-amount_cr)
                    ELSE 0
                    END dr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND details.project_id IN (${projectIds})
                    AND coa.company_id =:companyId
                    AND coa.acc_custom_group_id > 0
                    GROUP BY coa.acc_type_id,details.coa_id
            ) dr """

        String queryCr = """
            SELECT  (to_char(SUM(cr.cr_balance),'${Tools.DB_CURRENCY_FORMAT}')) AS custom_group_balance_sum_cr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN SUM(amount_cr-amount_dr)
                    ELSE 0
                    END cr_balance
                    FROM acc_voucher_details details
                    LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
                    LEFT JOIN acc_voucher v ON v.id=details.voucher_id
                    WHERE v.voucher_date >= :fromDate
                    AND v.voucher_date <= :toDate
                    AND v.posted_by > :postedByParam
                    AND details.project_id IN (${projectIds})
                    AND coa.company_id =:companyId
                    AND coa.acc_custom_group_id > 0
                    GROUP BY coa.acc_type_id,details.coa_id
            ) cr
        """

        Map queryParams = [
                accTypeAssetId: accTypeAsset.id,
                accTypeLiabilitiesId: accTypeLiabilities.id,
                accTypeExpenseId: accTypeExpense.id,
                accTypeIncomeId: accTypeIncome.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                postedByParam: postedByParam,
                companyId: companyId
        ]
        List<GroovyRowResult> tempDr = executeSelectSql(queryDr, queryParams)
        List<GroovyRowResult> tempCr = executeSelectSql(queryCr, queryParams)

        Map customGroupBalanceSum = [
                custom_group_balance_sum_dr: tempDr[0].custom_group_balance_sum_dr,
                custom_group_balance_sum_cr: tempCr[0].custom_group_balance_sum_cr
        ]
        return customGroupBalanceSum
    }
}
