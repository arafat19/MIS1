package com.athena.mis.accounting.actions.report.accincomestatement

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
 * Fetch list of income statement when refresh button of grid is fired
 * for level 5(show coa of all(tier1, tier2,tier3,tier4,tier5(if any)).
 * For details go through Use-Case doc named 'ListForIncomeStatementOfLevel5ActionService'
 */
class ListForIncomeStatementOfLevel5ActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String INCOME_STATEMENT_NOT_FOUND = "Income statement not found within given dates"
    private static final String FAILURE_MSG = "Fail to generate income statement report"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any projects"
    private static final String INCOME_STATEMENT_LIST = "incomeStatementList"
    private static final String TOTAL = "<b>TOTAL</b>"
    private static final String TEXT_BOLD_TAG_START = "<b>"
    private static final String TEXT_BOLD_TAG_END = "</b>"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
    private static final String DIVISION_ID = "divisionId"
    private static final String TOTAL_PROFIT = "<b>TOTAL PROFIT : </b>"
    private static final String TOTAL_LOSS = "<b>TOTAL LOSS : </b>"

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
     * Get income statement level 5 list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing income statement level 5 for grid and error msg(True/False)
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

            List<GroovyRowResult> returnIncomeStatementList = getIncomeStatementList(fromDate, toDate, lstProjectIds, postedByParam, divisionId)
            if (returnIncomeStatementList.size() <= 0) {
                result.put(Tools.MESSAGE, INCOME_STATEMENT_NOT_FOUND)
                return result
            }

            Map incomeStatementSumList = getIncomeStatementSum(fromDate, toDate, lstProjectIds, postedByParam, divisionId)
            List incomeStatementList = wrapIncomeStatementListInGridEntityList(returnIncomeStatementList, incomeStatementSumList)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(DIVISION_ID, divisionId)
            result.put(INCOME_STATEMENT_LIST, incomeStatementList)
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
     * Get income statement level 5 list
     * @param obj -received parameters from execute method
     * @return - a map containing wrapped income statement level 5 for grid and error msg(True/False)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstIncomeStatement = (List<GroovyRowResult>) executeResult.get(INCOME_STATEMENT_LIST)
            Map incomeStatementGrid = [page: 1, total: lstIncomeStatement.size(), rows: lstIncomeStatement]

            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(DIVISION_ID, executeResult.get(DIVISION_ID))
            result.put(INCOME_STATEMENT_LIST, incomeStatementGrid)
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
     * Wrap income statement level 5 list for grid
     * @param incomeStatementList -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    private List wrapIncomeStatementListInGridEntityList(List<GroovyRowResult> incomeStatementList, Map incomeStatementSumList) {
        List lstIncomeStatement = [] as List
        int counter = 1
        GroovyRowResult incomeStatement
        GridEntity object
        for (int i = 0; i < incomeStatementList.size(); i++) {
            incomeStatement = incomeStatementList[i]
            object = new GridEntity()
            object.id = counter
            object.cell = [
                    counter,
                    incomeStatement.coa_id,
                    incomeStatement.description,
                    incomeStatement.str_dr_balance,
                    incomeStatement.str_cr_balance
            ]
            lstIncomeStatement << object
            counter++
        }

        // add total amount to the last row
        if (lstIncomeStatement.size() > 0) {
            object = new GridEntity()
            String debitSumStr = TEXT_BOLD_TAG_START + incomeStatementSumList.income_statement_sum_dr_str + TEXT_BOLD_TAG_END
            String creditSumStr = TEXT_BOLD_TAG_START + incomeStatementSumList.income_statement_sum_cr_str + TEXT_BOLD_TAG_END
            object.id = counter
            object.cell = [
                    Tools.EMPTY_SPACE,
                    Tools.EMPTY_SPACE, TOTAL,
                    debitSumStr,
                    creditSumStr
            ]
            lstIncomeStatement << object

            BigDecimal debitSum = incomeStatementSumList.income_statement_sum_dr
            BigDecimal creditSum = incomeStatementSumList.income_statement_sum_cr
            BigDecimal totalSum = creditSum - debitSum

            String totalSumStr = Tools.makeAmountWithThousandSeparator(totalSum.abs())

            counter++
            object = new GridEntity()
            object.id = counter

            if (totalSum > 0) {
                object.cell = [Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, TOTAL_PROFIT + TEXT_BOLD_TAG_START + totalSumStr + TEXT_BOLD_TAG_END, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE]
            } else {
                object.cell = [Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, TOTAL_LOSS + TEXT_BOLD_TAG_START + totalSumStr + TEXT_BOLD_TAG_END, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE]
            }
            lstIncomeStatement << object
        }

        return lstIncomeStatement
    }
    /**
     * Get income statement list
     * @param fromDate
     * @param toDate
     * @param projectIds
     * @param postedByParam
     * @return- a list of income statement
     */
    public List<GroovyRowResult> getIncomeStatementList(Date fromDate, Date toDate, List<Long> projectIdList, long postedByParam, long divisionId) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object EXPENSE and INCOME
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        // check if specific division given
        String sqlDivisionWhereClause = Tools.EMPTY_SPACE // default value
        String sqlProjectWhereClause = Tools.EMPTY_SPACE // default value
        if (divisionId > 0) {
            sqlDivisionWhereClause = " AND details.division_id = ${divisionId} "
        } else {
            sqlProjectWhereClause = " AND details.project_id IN (${projectIds}) "
        }
        String queryStr = """
            SELECT coa.id coa_id, coa.description || ' (' || coa.code || ')' AS description,
            CASE
            WHEN (coa.acc_type_id=:accTypeExpenseId) THEN  (to_char(SUM(amount_dr-amount_cr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END str_dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeIncomeId) THEN  (to_char(SUM(amount_cr-amount_dr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END str_cr_balance
            FROM acc_voucher_details  details
            LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
            LEFT JOIN acc_voucher v ON v.id=details.voucher_id
            WHERE v.voucher_date >= :fromDate
            AND v.voucher_date <= :toDate
            AND v.posted_by > :postedByParam
            ${sqlProjectWhereClause} ${sqlDivisionWhereClause}
            AND (coa.acc_type_id = :accTypeIncomeId OR coa.acc_type_id = :accTypeExpenseId)
            AND coa.company_id = :companyId
            GROUP BY coa.acc_type_id,coa.code,coa.description,coa.id
            ORDER BY coa.description
        """

        Map queryParams = [
                accTypeExpenseId: accTypeExpense.id,
                accTypeIncomeId: accTypeIncome.id,
                fromDate: DateUtility.getSqlDate(fromDate),
                toDate: DateUtility.getSqlDate(toDate),
                postedByParam: postedByParam,
                companyId: companyId
        ]
        List<GroovyRowResult> incomeStatementList = executeSelectSql(queryStr, queryParams)

        return incomeStatementList
    }
    /**
     * Get income statement sum
     * @param fromDate
     * @param toDate
     * @param projectIdList
     * @param postedByParam
     * @return- a map containing income statement sum
     */
    private Map getIncomeStatementSum(Date fromDate, Date toDate, List<Long> projectIdList, long postedByParam, long divisionId) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object EXPENSE and INCOME
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        // check if specific division given
        String sqlDivisionWhereClause = Tools.EMPTY_SPACE // default value
        String sqlProjectWhereClause = Tools.EMPTY_SPACE // default value
        if (divisionId > 0) {
            sqlDivisionWhereClause = " AND details.division_id = ${divisionId} "
        } else {
            sqlProjectWhereClause = " AND details.project_id IN (${projectIds}) "
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
                    ${sqlProjectWhereClause} ${sqlDivisionWhereClause}
                    AND coa.company_id = :companyId
                    GROUP BY coa.acc_type_id,details.coa_id
            ) dr """

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
                    ${sqlProjectWhereClause} ${sqlDivisionWhereClause}
                    AND coa.company_id = :companyId
                    GROUP BY coa.acc_type_id,details.coa_id
            ) cr """

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
}
