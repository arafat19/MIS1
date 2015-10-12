package com.athena.mis.accounting.actions.report.acctrialbalance

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
 * download trial balance in pdf format -
 * for level 2(show coa of tier1(if any) and sum of tier2)
 * For details go through Use-Case doc named 'DownloadForTrialBalanceOfLevel2ActionService'
 */
class DownloadForTrialBalanceOfLevel2ActionService extends BaseService implements ActionIntf {

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

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to download trail balance"
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any projects"
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'accTrialBalance'
    private static final String LEVEL2 = 'level2'
    private static final String OUTPUT_FILE_NAME = 'accTrialBalanceOfHierarchy2'
    private static final String REPORT_TITLE = 'accTrialBalance'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String TRIAL_BALANCE_JASPER = 'trialBalanceOfLevel2.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String ALL_PROJECT = "All Projects"
    private static final String ALL_DIVISION = "All Divisions"
    private static final String PROJECT_NAME = "projectName"
    private static final String DIVISION_NAME = "divisionName"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check all pre conditions for input data
     * @param parameters - serialized parameters received from UI.
     * @param obj - N/A
     * @return - a map containing trial balance object and isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.toDate.toString())
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
     * Get desired report providing all required parameters
     * @param parameters -serialized parameters from UI
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

            List<Long> lstProjectIds = []
            String projectName = ALL_PROJECT  //default value
            String divisionName = ALL_DIVISION  //default value

            long projectId = Long.parseLong(params.projectId.toString())
            long divisionId = Long.parseLong(params.divisionId.toString())

            if (divisionId > 0) { //if specific division is given
                AccDivision division = (AccDivision) accDivisionCacheUtility.read(divisionId)
                divisionName = division.name

                lstProjectIds << new Long(projectId)
                Project project = (Project) projectCacheUtility.read(projectId)
                projectName = project.name
            } else { //if no specific division is given(for all division)
                if (projectId < 0) { //if no specific project is given
                    lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                    if (lstProjectIds.size() <= 0) {
                        result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                        return result
                    }
                } else {  //for specific project
                    lstProjectIds << new Long(projectId)
                    Project project = (Project) projectCacheUtility.read(projectId)
                    projectName = project.name
                }
            }

            String formatType = params.formatType

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Un-posted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            List<GroovyRowResult> trailBalanceList = getTrialBalanceListOfLevel2(fromDate, toDate, lstProjectIds, postedByParam, divisionId)

            Map report = getTrialBalanceReport(fromDate, toDate, formatType, projectName, divisionName, trailBalanceList)
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
    private Map getTrialBalanceReport(Date fromDate, Date toDate, String formatType, String projectName, String divisionName, List<GroovyRowResult> trailBalanceList) {
        Map reportParams = new LinkedHashMap()
        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER + File.separator + LEVEL2  // set directory
        String outputFileName = OUTPUT_FILE_NAME + Tools.getFileExtension(formatType)     // generated report name(filename+extension)
        JasperExportFormat jasperExportFormat = Tools.getFileType(formatType)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(DIVISION_NAME, divisionName)

        JasperReportDef reportDef = new JasperReportDef(name: TRIAL_BALANCE_JASPER, fileFormat: jasperExportFormat,
                reportData: trailBalanceList, parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: formatType]
    }

    /**
     * Get trial balance level 2
     * @param fromDate - start date
     * @param toDate - current date
     * @param projectIdList - list of project ids
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @param divisionId -AccDivision.Id
     * @return - list for trial balance level 2
     */
    private List<GroovyRowResult> getTrialBalanceListOfLevel2(Date fromDate, Date toDate, List<Long> projectIdList,
                                                              long postedByParam, long divisionId) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object for ASSET and LIABILITIES
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        // check if specific division given
        String sqlDivisionWhereClause = Tools.EMPTY_SPACE // default value
        String sqlProjectWhereClause = Tools.EMPTY_SPACE // default value
        if (divisionId > 0) {
            sqlDivisionWhereClause = " AND vw_voucher_details.division_id = ${divisionId} "
        } else {
            sqlProjectWhereClause = " AND vw_voucher_details.project_id IN (${projectIds}) "
        }
        String strQuery = """
            SELECT t1.name AS tier1_name, type.name AS acc_type_name,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId OR coa.acc_type_id=:accTypeExpenseId) THEN (to_char(SUM(vw_voucher_details.dr_balance),'${Tools.DB_CURRENCY_FORMAT}' ))
            ELSE '৳0.00'
            END str_dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId OR coa.acc_type_id=:accTypeExpenseId) THEN SUM(vw_voucher_details.dr_balance)
            ELSE 0
            END dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN (to_char(SUM(vw_voucher_details.cr_balance),'${Tools.DB_CURRENCY_FORMAT}' ))
            ELSE '৳0.00'
            END str_cr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN SUM(vw_voucher_details.cr_balance)
            ELSE 0
            END cr_balance
            FROM vw_acc_voucher_with_details vw_voucher_details
            LEFT JOIN acc_chart_of_account coa ON coa.id = vw_voucher_details.coa_id
            LEFT JOIN acc_type type ON type.id = coa.acc_type_id
            LEFT JOIN acc_tier1 t1 ON t1.id = coa.tier1
            WHERE vw_voucher_details.voucher_date >= :fromDate
            AND vw_voucher_details.voucher_date <= :toDate
            AND vw_voucher_details.posted_by > :postedByParam
            ${sqlProjectWhereClause} ${sqlDivisionWhereClause}
            AND vw_voucher_details.company_id = :companyId
            AND coa.tier1 > 0
            GROUP BY coa.acc_type_id,t1.name,type.name,type.order_id
            ORDER BY type.order_id,t1.name
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
        List<GroovyRowResult> trialBalanceList = executeSelectSql(strQuery, queryParams)
        return trialBalanceList
    }
}
