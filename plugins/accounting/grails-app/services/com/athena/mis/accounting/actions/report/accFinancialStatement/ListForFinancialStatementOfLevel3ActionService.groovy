package com.athena.mis.accounting.actions.report.accFinancialStatement

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
 *  Show list of financial statement of level 3 (show coa of tier1 & tier2(if any) and sum of tier3) for grid
 *  For details go through Use-Case doc named 'ListForFinancialStatementOfLevel3ActionService'
 */
class ListForFinancialStatementOfLevel3ActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MSG = "Could not generate financial statement"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FINANCIAL_STATEMENT_LIST = "financialStatementList"
    private static final String TOTAL = "<b>TOTAL</b>"
    private static final String GRAND_TOTAL = "<b>GRAND TOTAL</b>"
    private static final String INCOME_STATEMENT = "INCOME STATEMENT"
    private static final String PROFIT = " (Profit)"
    private static final String LOSS = " (Loss)"
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
            log.error(ex.getMessage());
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
     * Get desired list providing all required parameters.
     * Get all project Id(s).
     * Get financial statement list.
     * Get income statement sum.
     * Wrap income statement list for UI.
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all financial statement list for level 3 and isError(true/false)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())

            List<Long> lstProjectIds = [] //main list of projectIds
            long projectId = params.projectId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(params.projectId.toString())
            long divisionId = params.divisionId.equals(Tools.EMPTY_SPACE) ? -1L : Long.parseLong(params.divisionId.toString())
            if (divisionId <= 0) {     // evaluate project list, only if division is not found
                if (projectId < 0) {
                    List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                    if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                        lstProjectIds << new Long(0)
                    } else {  //if tempList is not null then set tempProjectIdList at main list
                        lstProjectIds = tempProjectIdList
                    }
                } else {
                    lstProjectIds << new Long(projectId)
                }
            }

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            List<GroovyRowResult> returnFinancialStatementList = getFinancialStatementList(fromDate, toDate, lstProjectIds, postedByParam, divisionId)

            Map financialStatementSumList = getFinancialStatementSum(fromDate, toDate, lstProjectIds, postedByParam, divisionId)  // get financial statement sum
            Map incomeStatementSumList = getIncomeStatementSum(fromDate, toDate, lstProjectIds, postedByParam, divisionId)  // get income statement sum
            List financialStatementList = wrapFinancialStatementListInGridEntityList(returnFinancialStatementList, financialStatementSumList, incomeStatementSumList) // wrap income statement list for the grid view
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(PROJECT_ID, projectId)
            result.put(DIVISION_ID, divisionId)
            result.put(FINANCIAL_STATEMENT_LIST, financialStatementList)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Get list of financial statement of level 3 for showing in the grid
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstFinancialStatement = (List<GroovyRowResult>) executeResult.get(FINANCIAL_STATEMENT_LIST)
            Map FinancialStatementGrid = [page: 1, total: lstFinancialStatement.size(), rows: lstFinancialStatement]

            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(DIVISION_ID, executeResult.get(DIVISION_ID))
            result.put(FINANCIAL_STATEMENT_LIST, FinancialStatementGrid)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
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
     * Wrap list of income statement of level 3 in grid entity
     * @param financialStatementList - list of financial statement
     * @param financialStatementSumList - summation of financial statement list
     * @param incomeStatementSumList -  summation of income statement list
     * @return - list wrapped income statement
     */
    private List wrapFinancialStatementListInGridEntityList(List<GroovyRowResult> financialStatementList, Map financialStatementSumList, Map incomeStatementSumList) {
        List lstFinancialStatement = [] as List
        int counter = 1
        GroovyRowResult financialStatement
        GridEntity object

        if (financialStatementList.size() > 0) {
            for (int i = 0; i < financialStatementList.size(); i++) {
                financialStatement = financialStatementList[i];
                object = new GridEntity()
                object.id = counter
                object.cell = [
                        counter,
                        financialStatement.id,
                        financialStatement.description,
                        financialStatement.dr_balance,
                        financialStatement.cr_balance
                ]
                lstFinancialStatement << object
                counter++
            }
        }

        // add income statement
        if (lstFinancialStatement.size() > 0) {
            BigDecimal financialStatementSumDr = financialStatementSumList.financial_statement_sum_dr
            BigDecimal financialStatementSumCr = financialStatementSumList.financial_statement_sum_cr

            // add total amount to the last row
            object = new GridEntity()
            object.id = counter
            object.cell = [
                    Tools.EMPTY_SPACE,
                    Tools.EMPTY_SPACE,
                    TOTAL,
                    TEXT_BOLD_TAG_START + Tools.makeAmountWithThousandSeparator(financialStatementSumDr) + TEXT_BOLD_TAG_END,
                    TEXT_BOLD_TAG_START + Tools.makeAmountWithThousandSeparator(financialStatementSumCr) + TEXT_BOLD_TAG_END
            ]
            lstFinancialStatement << object

            //add income statement
            BigDecimal debitSum = incomeStatementSumList.income_statement_sum_dr
            BigDecimal creditSum = incomeStatementSumList.income_statement_sum_cr
            BigDecimal totalSum = creditSum - debitSum

            String totalSumStr = Tools.makeAmountWithThousandSeparator(totalSum.abs())
            // if totalSum is -ve (i.e. LOSS) then place in Dr
            // if totalSum is +ve (i.e. PROFIT) then place in Cr
            String strProfitLoss = totalSum > 0 ? PROFIT : LOSS

            object = new GridEntity()
            object.id = counter
            object.cell = [
                    Tools.EMPTY_SPACE,
                    Tools.EMPTY_SPACE,
                    Tools.makeBold(INCOME_STATEMENT + strProfitLoss),
                    totalSum < 0 ? TEXT_BOLD_TAG_START + totalSumStr + TEXT_BOLD_TAG_END : Tools.EMPTY_SPACE,
                    totalSum > 0 ? TEXT_BOLD_TAG_START + totalSumStr + TEXT_BOLD_TAG_END : Tools.EMPTY_SPACE
            ]
            lstFinancialStatement << object

            // add grand total amount to the last row
            object = new GridEntity()
            if (totalSum > 0) {
                financialStatementSumCr = financialStatementSumCr + totalSum.abs()
            } else {
                financialStatementSumDr = financialStatementSumDr + totalSum.abs()
            }
            String totalDebitSum = TEXT_BOLD_TAG_START + Tools.makeAmountWithThousandSeparator(financialStatementSumDr) + TEXT_BOLD_TAG_END
            String totalCreditSum = TEXT_BOLD_TAG_START + Tools.makeAmountWithThousandSeparator(financialStatementSumCr) + TEXT_BOLD_TAG_END
            object.id = counter
            object.cell = [Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, GRAND_TOTAL, totalDebitSum, totalCreditSum]
            lstFinancialStatement << object
        }
        return lstFinancialStatement
    }

    /**
     * Calculate financial statement balance of level 3 - sum of Debit(Asset) and sum of Credit(Liability)
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
            (SELECT coa.id AS id, coa.description || ' (' || coa.code || ')' AS description,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId) THEN  (to_char(SUM(amount_dr-amount_cr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId) THEN  (to_char(SUM(amount_cr-amount_dr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END cr_balance
            FROM acc_voucher_details details
            LEFT JOIN acc_chart_of_account coa ON coa.id = details.coa_id
            LEFT JOIN acc_voucher v ON v.id=details.voucher_id
            WHERE v.voucher_date >= :fromDate
            AND v.voucher_date <= :toDate
            AND v.posted_by > :postedByParam
            ${subQueryProject} ${subQueryDivision}
            AND coa.tier2 = 0
            AND (coa.acc_type_id =:accTypeAssetId OR coa.acc_type_id =:accTypeLiabilitiesId)
            AND coa.company_id =:companyId
            GROUP BY coa.acc_type_id,coa_id,coa.code,coa.description,coa.id
            ORDER BY coa.acc_type_id)

            UNION

            (SELECT t2.id AS id, t2.name AS description,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId) THEN  (to_char(SUM(amount_dr-amount_cr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId) THEN  (to_char(SUM(amount_cr-amount_dr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE '৳ 0.00'
            END cr_balance
            FROM acc_voucher_details details
            LEFT JOIN acc_chart_of_account coa ON coa.id = details.coa_id
            LEFT JOIN acc_voucher v ON v.id=details.voucher_id
            LEFT JOIN acc_tier2 t2 ON t2.id = coa.tier2
            WHERE v.voucher_date >= :fromDate
            AND v.voucher_date <= :toDate
            AND v.posted_by > :postedByParam
            ${subQueryProject} ${subQueryDivision}
            AND coa.tier2 > 0
            AND (coa.acc_type_id =:accTypeAssetId OR coa.acc_type_id =:accTypeLiabilitiesId)
            AND coa.company_id =:companyId
            GROUP BY coa.acc_type_id, t2.name, t2.id
            ORDER BY coa.acc_type_id, t2.name)
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
     * @param projectIds - list of  all project ids
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
                    AND coa.company_id =:companyId
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
                companyId: companyId
        ]

        List<GroovyRowResult> tempDr = executeSelectSql(queryDr, queryParams)
        List<GroovyRowResult> tempCr = executeSelectSql(queryCr, queryParams)

        Map financialStatementSum = [
                financial_statement_sum_dr: tempDr[0].financial_statement_sum_dr,
                financial_statement_sum_cr: tempCr[0].financial_statement_sum_cr
        ]
        return financialStatementSum
    }
}
