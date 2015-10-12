package com.athena.mis.accounting.actions.report.acctrialbalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Fetch list of trial balance when clear button of grid is fired-
 * for level 5(show coa of all(tier1, tier2,tier3,tier4,tier5(if any)).
 * For details go through Use-Case doc named 'ListForTrialBalanceOfLevel4ActionService'
 */
class ListForTrialBalanceOfLevel5ActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Fail to generate trial balance report"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any projects"
    private static final String NO_TRIAL_BALANCE_FOUND = "No trial balance list found"
    private static final String TRAIL_BALANCE_LIST = "trailBalanceList"
    private static final String TOTAL = "<b>TOTAL</b>"
    private static final String TEXT_BOLD_TAG_START = "<b>"
    private static final String TEXT_BOLD_TAG_END = "</b>"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
    private static final String DIVISION_ID = "divisionId"

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    /**
     * Check pre-conditions for input data
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing isError msg(True/False)
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
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
     * Get trial balance level 5 list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing trial balance level 5 for grid and error msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            List<Long> lstProjectIds = []
            long projectId = parameterMap.projectId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(parameterMap.projectId.toString())
            long divisionId = parameterMap.divisionId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(parameterMap.divisionId.toString())
            if (divisionId <= 0) {     // evaluate project list, only if division not found
                if (projectId < 0) {
                    lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                    if (lstProjectIds.size() <= 0) {
                        result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                        return result
                    }
                } else {
                    lstProjectIds << new Long(projectId)
                }
            }

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            List<GroovyRowResult> returnTrailBalanceList = getTrialBalanceListOfLevel5(fromDate, toDate, lstProjectIds, postedByParam, divisionId)
            if (returnTrailBalanceList.size() <= 0) {
                result.put(Tools.MESSAGE, NO_TRIAL_BALANCE_FOUND)
                return result
            }

            List trialBalanceList = wrapTrialBalanceListInGridEntityList(returnTrailBalanceList)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(DIVISION_ID, divisionId)
            result.put(TRAIL_BALANCE_LIST, trialBalanceList)
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
     * Wrap trial balance level 5 list for grid
     * @param trialBalanceList -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    private List wrapTrialBalanceListInGridEntityList(List<GroovyRowResult> trialBalanceList) {
        List lstTrialBalance = [] as List
        int counter = 1
        BigDecimal totalSumOfDebit = 0
        BigDecimal totalSumOfCredit = 0
        GroovyRowResult trialBalance
        GridEntity object
        for (int i = 0; i < trialBalanceList.size(); i++) {
            trialBalance = trialBalanceList[i]
            object = new GridEntity()
            object.id = counter
            object.cell = [
                    counter,
                    trialBalance.coa_id,
                    trialBalance.coa_description,
                    trialBalance.str_dr_balance,
                    trialBalance.str_cr_balance
            ]
            lstTrialBalance << object
            counter++

            totalSumOfDebit = totalSumOfDebit + trialBalance.dr_balance
            totalSumOfCredit = totalSumOfCredit + trialBalance.cr_balance
        }

        //Add totalSum
        object = new GridEntity()
        object.id = counter
        object.cell = [
                Tools.EMPTY_SPACE,
                Tools.EMPTY_SPACE,
                TEXT_BOLD_TAG_START + TOTAL + TEXT_BOLD_TAG_END,
                TEXT_BOLD_TAG_START + Tools.makeAmountWithThousandSeparator(totalSumOfDebit) + TEXT_BOLD_TAG_END,
                TEXT_BOLD_TAG_START + Tools.makeAmountWithThousandSeparator(totalSumOfCredit) + TEXT_BOLD_TAG_END
        ]
        lstTrialBalance << object

        return lstTrialBalance
    }
    /**
     * Get trial balance level 5 list
     * @param obj -received parameters from execute method
     * @return - a map containing wrapped trial balance level 5 for grid and error msg(True/False)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstTrialBalance = (List<GroovyRowResult>) executeResult.get(TRAIL_BALANCE_LIST)
            Map trialBalanceGrid = [page: 1, total: lstTrialBalance.size(), rows: lstTrialBalance]

            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(DIVISION_ID, executeResult.get(DIVISION_ID))
            result.put(TRAIL_BALANCE_LIST, trialBalanceGrid)
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
     * Get trial balance level 5
     * @param fromDate - start date
     * @param toDate - current date
     * @param projectIdList - list of project ids
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - list for trial balance level 5
     */
    private List<GroovyRowResult> getTrialBalanceListOfLevel5(Date fromDate, Date toDate, List<Long> projectIdList,
                                                              long postedByParam, long divisionId) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object
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
        String queryStr = """
            SELECT coa.id coa_id,coa.description ||' ('||coa.code||')' AS coa_description,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId OR coa.acc_type_id=:accTypeExpenseId) THEN (to_char(SUM(vw_voucher_details.dr_balance),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳0.00'
            END str_dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId OR coa.acc_type_id=:accTypeExpenseId) THEN SUM(vw_voucher_details.dr_balance)
            ELSE 0
            END dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN (to_char(SUM(vw_voucher_details.cr_balance),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳0.00'
            END str_cr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN SUM(vw_voucher_details.cr_balance)
            ELSE 0
            END cr_balance
            FROM vw_acc_voucher_with_details vw_voucher_details
            LEFT JOIN acc_chart_of_account coa ON coa.id = vw_voucher_details.coa_id
            WHERE vw_voucher_details.voucher_date >= :fromDate
            AND vw_voucher_details.voucher_date <= :toDate
            AND vw_voucher_details.posted_by > :postedByParam
            ${sqlProjectWhereClause} ${sqlDivisionWhereClause}
            AND vw_voucher_details.company_id = :companyId
            GROUP BY coa.id,coa.code,coa.description,coa.acc_type_id
            ORDER BY coa.description;
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
        List<GroovyRowResult> trialBalanceList = executeSelectSql(queryStr, queryParams)
        return trialBalanceList
    }
}