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
 * Download trial balance in pdf format -
 * for level 3(show coa of tier1 & tier2(if any) and sum of tier3)
 * For details go through Use-Case doc named 'DownloadForTrialBalanceOfLevel3ActionService'
 */
class DownloadForTrialBalanceOfLevel3ActionService extends BaseService implements ActionIntf {

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
    private static final String LEVEL3 = 'level3'
    private static final String OUTPUT_FILE_NAME = 'accTrialBalanceOfHierarchy3'
    private static final String REPORT_TITLE = 'accTrialBalance'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String REPORT = "report"
    private static final String TRIAL_BALANCE_JASPER = 'trialBalanceOfLevel3.jasper'
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String PROJECT_ID_LIST = "projectIdList"
    private static final String DIVISION_ID_LIST = "divisionIdList"
    private static final String ALL_PROJECT = "All Projects"
    private static final String ALL_DIVISION = "All Divisions"
    private static final String PROJECT_NAME = "projectName"
    private static final String DIVISION_NAME = "divisionName"
    private static final String COMPANY_ID = "companyId"
    private static final String TRIAL_BALANCE_SUM_DR = "trialBalanceSumDr"
    private static final String TRIAL_BALANCE_SUM_CR = "trialBalanceSumCr"
    private static final String POSTED_BY_PARAM = "postedByParam"
    private static final String ACC_TYPE_ASSET = "accTypeAssetId"
    private static final String ACC_TYPE_EXPENSES = "accTypeExpensesId"
    private static final String ACC_TYPE_LIABILITIES = "accTypeLiabilitiesId"
    private static final String ACC_TYPE_INCOME = "accTypeIncomeId"

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
                result.put(Tools.MESSAGE, INVALID_INPUT)
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
            Map trialBalanceSum = getTrialBalanceSum(fromDate, toDate, lstProjectIds, lstDivisionIds, postedByParam) // get trial balance sum(debit & credit)
            Map report = getTrialBalanceReport(fromDate, toDate, lstProjectIds, lstDivisionIds, formatType, projectName, divisionName, postedByParam, trialBalanceSum)
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
            if (executeResult.message) {
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
     * @param fromDate -start date
     * @param toDate - current date
     * @param projectIdList - list of project ids
     * @param formatType - pdf/csv
     * @param projectName - name of project
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @param trialBalanceSumList - trial balance sum list
     * @return - generated report with required params
     */
    private Map getTrialBalanceReport(Date fromDate, Date toDate, List<Long> projectIdList, List<Long> divisionIdList, String formatType, String projectName,
                                      String divisionName, long postedByParam, Map trialBalanceSumList) {
        Map reportParams = new LinkedHashMap()
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        String reportDir = Tools.getAccountingReportDirectory() + File.separator + REPORT_FOLDER + File.separator + LEVEL3
        String outputFileName = OUTPUT_FILE_NAME + Tools.getFileExtension(formatType)
        JasperExportFormat jasperExportFormat = Tools.getFileType(formatType)
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(Tools.COMMON_REPORT_DIR, Tools.getCommonReportDirectory())
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(FROM_DATE, fromDate)
        reportParams.put(TO_DATE, toDate)
        reportParams.put(PROJECT_ID_LIST, projectIdList)
        reportParams.put(DIVISION_ID_LIST, divisionIdList)
        reportParams.put(PROJECT_NAME, projectName)
        reportParams.put(DIVISION_NAME, divisionName)
        reportParams.put(COMPANY_ID, accSessionUtil.appSessionUtil.getAppUser().companyId)
        reportParams.put(POSTED_BY_PARAM, postedByParam)
        reportParams.put(TRIAL_BALANCE_SUM_CR, trialBalanceSumList.trial_balance_sum_cr)
        reportParams.put(TRIAL_BALANCE_SUM_DR, trialBalanceSumList.trial_balance_sum_dr)
        reportParams.put(ACC_TYPE_ASSET, accTypeAsset.id)
        reportParams.put(ACC_TYPE_EXPENSES, accTypeExpense.id)
        reportParams.put(ACC_TYPE_LIABILITIES, accTypeLiabilities.id)
        reportParams.put(ACC_TYPE_INCOME, accTypeIncome.id)

        JasperReportDef reportDef = new JasperReportDef(name: TRIAL_BALANCE_JASPER, fileFormat: jasperExportFormat,
                parameters: reportParams, folder: reportDir)
        /*  Generate a report based on jasper file. */
        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: formatType]
    }
    /**
     * Retrieve trial balance sum
     * @param fromDate - starting point of date range
     * @param toDate - ending point of date range
     * @param projectIdList -list of project ids
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - sum of trial balance
     */
    private Map getTrialBalanceSum(Date fromDate, Date toDate, List<Long> projectIdList, List<Long> divisionIdList, long postedByParam) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        String divisionIds = Tools.buildCommaSeparatedStringOfIds(divisionIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        String queryDr = """
            SELECT  SUM(dr.dr_balance) AS trial_balance_sum_dr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id= :accTypeAssetId OR coa.acc_type_id= :accTypeExpenseId) THEN SUM(vd.dr_balance)
                    ELSE 0
                    END dr_balance
                    FROM vw_acc_voucher_with_details vd
                    LEFT JOIN acc_chart_of_account coa ON coa.id = vd.coa_id
                    WHERE vd.voucher_date >= :fromDate
                    AND vd.voucher_date <= :toDate
                    AND vd.posted_by > :postedByParam
                    AND vd.project_id IN (${projectIds})
                    AND vd.division_id IN (${divisionIds})
                    AND vd.company_id = :companyId
                    GROUP BY coa.acc_type_id
            ) dr
        """

        String queryCr = """
            SELECT  SUM(cr.cr_balance) AS trial_balance_sum_cr
            FROM (
                    SELECT
                    CASE
                    WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN SUM(vd.cr_balance)
                    ELSE 0
                    END cr_balance
                    FROM vw_acc_voucher_with_details vd
                    LEFT JOIN acc_chart_of_account coa ON coa.id = vd.coa_id
                    WHERE vd.voucher_date >= :fromDate
                    AND vd.voucher_date <= :toDate
                    AND vd.posted_by > :postedByParam
                    AND vd.project_id IN (${projectIds})
                    AND vd.division_id IN (${divisionIds})
                    AND vd.company_id = :companyId
                    GROUP BY coa.acc_type_id
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

        Map trialBalanceSum = [
                trial_balance_sum_cr: tempCr[0].trial_balance_sum_cr,
                trial_balance_sum_dr: tempDr[0].trial_balance_sum_dr,
        ]
        return trialBalanceSum
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
