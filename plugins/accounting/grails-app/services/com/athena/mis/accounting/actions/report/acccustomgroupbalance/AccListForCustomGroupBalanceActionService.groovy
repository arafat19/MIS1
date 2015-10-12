package com.athena.mis.accounting.actions.report.acccustomgroupbalance

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
 *  Show list of  custom group balance for grid
 *  For details go through Use-Case doc named 'AccListForCustomGroupBalanceActionService'
 */
class AccListForCustomGroupBalanceActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String TRIAL_BALANCE_NOT_FOUND = "Custom Group Balance not found within given dates."
    private static final String FAILURE_MSG = "Fail to generate Custom Group Balance"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String CUSTOM_GROUP_BALANCE_LIST = "customGroupBalanceList"
    private static final String TOTAL = "<b>TOTAL</b>"
    private static final String TEXT_BOLD_TAG_START = "<b>"
    private static final String TEXT_BOLD_TAG_END = "</b>"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"

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
     * Get desired list providing all required parameters.
     * Get all project Id(s).
     * Get custom group balance list.
     * Get custom group balance sum.
     * Wrap custom group balance list for UI.
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all financial statement list and isError(true/false)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            List<Long> projectIdList = []  //main list of projectIds

            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                result.put(PROJECT_ID, Tools.EMPTY_SPACE)
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIdList << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIdList = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIdList << new Long(projectId)
                result.put(PROJECT_ID, projectId)
            }

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            List<GroovyRowResult> returnCustomGroupBalanceList = getCustomGroupBalanceList(fromDate, toDate, projectIdList, postedByParam) // get custom group balance list
            if (returnCustomGroupBalanceList.size() <= 0) {
                result.put(Tools.MESSAGE, TRIAL_BALANCE_NOT_FOUND)
                return result
            }

            Map customGroupBalanceSumList = getCustomGroupBalanceSum(fromDate, toDate, projectIdList, postedByParam)   // get custom group balance sum
            List customGroupBalanceList = wrapCustomGroupBalanceListInGridEntityList(returnCustomGroupBalanceList, customGroupBalanceSumList) //Wrap custom group balance list for UI
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(CUSTOM_GROUP_BALANCE_LIST, customGroupBalanceList)
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
     * Wrap list of custom group balance in grid entity
     * @param customGroupBalanceList - list of custom group balance
     * @param customGroupBalanceSumList - list of custom group balance sum
     * @return - list wrapped trial balance
     */
    private List wrapCustomGroupBalanceListInGridEntityList(List<GroovyRowResult> customGroupBalanceList, Map customGroupBalanceSumList) {
        List lstTrialBalance = [] as List
        int counter = 1
        GroovyRowResult customGroupBalance
        GridEntity object
        for (int i = 0; i < customGroupBalanceList.size(); i++) {
            customGroupBalance = customGroupBalanceList[i]
            object = new GridEntity()
            object.id = counter
            object.cell = [
                    counter,
                    customGroupBalance.acg_name,
                    customGroupBalance.coa_id,
                    customGroupBalance.description,
                    customGroupBalance.code,
                    customGroupBalance.dr_balance,
                    customGroupBalance.cr_balance
            ]
            lstTrialBalance << object
            counter++
        }
        // add total amount to the last row
        if (lstTrialBalance.size() > 0) {
            object = new GridEntity()
            String debitSum = TEXT_BOLD_TAG_START + customGroupBalanceSumList.custom_group_balance_sum_dr + TEXT_BOLD_TAG_END
            String creditSum = TEXT_BOLD_TAG_START + customGroupBalanceSumList.custom_group_balance_sum_cr + TEXT_BOLD_TAG_END
            object.id = counter
            object.cell = [Tools.EMPTY_SPACE, TOTAL, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, Tools.EMPTY_SPACE, debitSum, creditSum]
            lstTrialBalance << object
        }

        return lstTrialBalance
    }

    /**
     * Get list of custom group balance for showing in the grid
     * @param obj - map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List<GroovyRowResult> lstCustomGroupBalance = (List<GroovyRowResult>) executeResult.get(CUSTOM_GROUP_BALANCE_LIST)
            Map customGroupBalanceGrid = [page: 1, total: lstCustomGroupBalance.size(), rows: lstCustomGroupBalance]

            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(CUSTOM_GROUP_BALANCE_LIST, customGroupBalanceGrid)
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

    private List<GroovyRowResult> getCustomGroupBalanceList(Date fromDate, Date toDate, List<Long> projectIdList, long postedByParam) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()

        // get AccType object
        AccType accTypeAsset = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.ASSET)
        AccType accTypeLiabilities = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.LIABILITIES)
        AccType accTypeExpense = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.EXPENSE)
        AccType accTypeIncome = accTypeCacheUtility.readBySystemAccountType(accTypeCacheUtility.INCOME)

        String queryStr = """
            SELECT acg.id AS acg_id, acg.name AS acg_name, coa.id coa_id,coa.description,coa.code,
            CASE
            WHEN (coa.acc_type_id=:accTypeAssetId OR coa.acc_type_id=:accTypeExpenseId) THEN  (to_char(SUM(amount_dr-amount_cr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE ''
            END dr_balance,
            CASE
            WHEN (coa.acc_type_id=:accTypeLiabilitiesId OR coa.acc_type_id=:accTypeIncomeId) THEN  (to_char(SUM(amount_cr-amount_dr),'${Tools.DB_CURRENCY_FORMAT}'))
            ELSE ''
            END cr_balance
            FROM acc_voucher_details  details
            LEFT JOIN acc_chart_of_account coa ON coa.id= details.coa_id
            LEFT JOIN acc_voucher v ON v.id=details.voucher_id
            LEFT JOIN acc_custom_group acg ON acg.id = coa.acc_custom_group_id
            WHERE v.voucher_date >= :fromDate
            AND v.voucher_date <= :toDate
            AND v.posted_by > :postedByParam
            AND details.project_id IN (${projectIds})
            AND coa.company_id =:companyId
            AND coa.acc_custom_group_id > 0
            GROUP BY acg.id,acg.name,coa.acc_type_id,coa_id,coa.code,coa.description,coa.id
            ORDER BY coa.acc_type_id
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
        List<GroovyRowResult> customGroupBalanceList = executeSelectSql(queryStr, queryParams)
        return customGroupBalanceList
    }

    /**
     * Calculate custom group balance - sum of Debit(Asset) and sum of Credit(Liability)
     * @param fromDate - start date from UI
     * @param toDate - current date(today)
     * @param projectIds - list of  all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - Map of trial balance sum
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
               ) dr
        """

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

        Map trialBalanceSum = [
                custom_group_balance_sum_dr: tempDr[0].custom_group_balance_sum_dr,
                custom_group_balance_sum_cr: tempCr[0].custom_group_balance_sum_cr
        ]
        return trialBalanceSum
    }
}