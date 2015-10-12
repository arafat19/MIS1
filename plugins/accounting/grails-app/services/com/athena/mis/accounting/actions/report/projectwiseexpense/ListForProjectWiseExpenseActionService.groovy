package com.athena.mis.accounting.actions.report.projectwiseexpense

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

//import SysConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * Retrieve list of project(s) with total credit of cash/bank group within given date range
 * For details go through Use-Case doc named 'ListForProjectWiseExpenseActionService'
 */
class ListForProjectWiseExpenseActionService extends BaseService implements ActionIntf {

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private static final String HEAD_NOT_FOUND = "Account Code not found."
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate expense report."
    private static final String COA_OBJ = "coa"
    private static final String EXPENSE_LIST_WRAP = "expenseListWrap"
    private static final String EXPENSE_GRID_OBJ = "expenseGridObj"
    private static final String COUNT = "count"
    private static final String EXPENSE_LIST_MAP = "expenseListMap"
    private static final String LABEL_ALL = "ALL"
    private static final String USER_HAS_NO_PROJECT = "User is not mapped with any project"
    private static final String NO_EXPENSE_FOUND = "No expense found"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check pre-conditions for input data
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing chart of account object & isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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

            AccChartOfAccount accChartOfAccount = null
            if (!params.coaId.equals(Tools.EMPTY_SPACE)) {
                long coaId = Long.parseLong(params.coaId.toString())
                accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(coaId)
                if (!accChartOfAccount) {
                    result.put(Tools.MESSAGE, HEAD_NOT_FOUND)
                    return result
                }
            }
            result.put(COA_OBJ, accChartOfAccount)
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
     * Get expense list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing wrapped expense list for grid and error msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            List<Long> projectIds = []
            List<Long> groupIds = []

            long projectId
            long groupId

            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            } else {
                projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
            }

            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                return result
            }

            if (parameterMap.accGroupId.equals(Tools.EMPTY_SPACE)) {
                groupIds = accGroupCacheUtility.listOfAccGroupBankCashId()
            } else {
                groupId = Long.parseLong(parameterMap.accGroupId.toString())
                groupIds << new Long(groupId)
            }
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) preResult.get(COA_OBJ)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Un-posted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            LinkedHashMap serviceReturn = listProjectWiseExpense(groupIds, accChartOfAccount, projectIds, fromDate, toDate, postedByParam)

            List<GroovyRowResult> expenseListSummary = (List<GroovyRowResult>) serviceReturn.expenseList
            int count = (int) serviceReturn.count

            if (count <= 0) {
                result.put(Tools.MESSAGE, NO_EXPENSE_FOUND)
                return result
            }

            List expenseListWrap = wrapExpenseList(expenseListSummary, start)
            result.put(EXPENSE_LIST_WRAP, expenseListWrap)
            result.put(COUNT, count)

            // create the map to display label values
            LinkedHashMap expenseListMap = [
                    coaDescription: accChartOfAccount ? accChartOfAccount.description : LABEL_ALL,
                    coaId: accChartOfAccount ? accChartOfAccount.id : -1,
                    coaCode: accChartOfAccount ? accChartOfAccount.code : Tools.NOT_APPLICABLE,
                    fromDate: DateUtility.getDateForUI(fromDate),
                    toDate: DateUtility.getDateForUI(toDate),
                    projectId: projectId,
                    accGroupId: parameterMap.accGroupId
            ]
            result.put(EXPENSE_LIST_MAP, expenseListMap)
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
     * Wrap project wise expense list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            List expenseListWrap = (List) executeResult.get(EXPENSE_LIST_WRAP)
            Map expenseListMap = (Map) executeResult.get(EXPENSE_LIST_MAP)

            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: expenseListWrap]

            result.put(EXPENSE_GRID_OBJ, gridOutput)
            result.put(EXPENSE_LIST_MAP, expenseListMap)
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
            if (executeResult.message) {
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
     * Get organized grid object to display data in the grid
     * @param expenseListDetails - expense details list received from execute method
     * @return - grid object
     */
    private List wrapExpenseList(List<GroovyRowResult> expenseList, int start) {
        List lstExpenses = [] as List

        int counter = start + 1
        GroovyRowResult singleRow
        GridEntity obj
        for (int i = 0; i < expenseList.size(); i++) {
            singleRow = expenseList[i]
            obj = new GridEntity()
            obj.id = singleRow.project_id
            obj.cell = [
                    counter,
                    singleRow.project_name,
                    Tools.makeAmountWithThousandSeparator(singleRow.amount_cr)
            ]
            lstExpenses << obj
            counter++
        }
        return lstExpenses
    }

    //@todo-model  adjust using AccVoucherModel.listProjectWiseExpense()
    /**
     * Get total expense of a project
     * @param accGroupId - account group id
     * @param accChartOfAccount - chart of account object
     * @param projectIds - project ids
     * @param fromVoucherDate - voucher start date
     * @param toVoucherDate - voucher end date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - list of total expense of specific project
     */
    private LinkedHashMap listProjectWiseExpense(List accGroupIds, AccChartOfAccount accChartOfAccount, List projectIds, Date fromVoucherDate, Date toVoucherDate, long postedByParam) {
        String lstProject = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String lstGroupIds = Tools.buildCommaSeparatedStringOfIds(accGroupIds)
        AccGroup accGroupBank = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)
        AccGroup accGroupCash = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_CASH)

        String queryStr = """
                SELECT credit.project_id, credit.project_name, (sum(credit.cr)-sum(debit.dr)) amount_cr
                FROM
                    (select avd.project_id, project.name AS project_name,
                            avd.voucher_id, sum(avd.amount_cr) cr
                    FROM acc_voucher_details avd
                            LEFT JOIN acc_voucher av ON av.id =voucher_id
                            LEFT JOIN project ON project.id = avd.project_id
                            LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                    WHERE (av.voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate) AND
                          coa.acc_group_id IN (${lstGroupIds}) AND
                          av.project_id IN (${lstProject}) AND
                          av.posted_by > :postedByParam  AND
                          avd.amount_cr > 0
                    GROUP by avd.project_id, project.name, avd.voucher_id
                    ) credit
                LEFT JOIN
                (select avd.voucher_id, sum(avd.amount_dr) dr
                    FROM acc_voucher_details avd
                            LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                            LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                            WHERE avd.group_id IN(:accGroupBank, :accGroupCash)
                            GROUP by avd.voucher_id
                    ) debit ON credit.voucher_id = debit.voucher_id
                GROUP BY credit.project_id, credit.project_name
                HAVING (sum(credit.cr)-sum(debit.dr)) > 0
                ORDER BY credit.project_name
                LIMIT :resultPerPage  OFFSET :start
            """

        String queryCount = """
        SELECT COALESCE(COUNT(count_total.count),0) count
        FROM(
            SELECT COALESCE(COUNT(credit.project_id),0) count
            FROM
                (select avd.project_id, project.name AS project_name,
                        avd.voucher_id, sum(avd.amount_cr) cr
                FROM acc_voucher_details avd
                        LEFT JOIN acc_voucher av ON av.id =voucher_id
                        LEFT JOIN project ON project.id = avd.project_id
                LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                WHERE (av.voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate) AND
                coa.acc_group_id IN (${lstGroupIds}) AND
                av.project_id IN (${lstProject}) AND
                av.posted_by > :postedByParam  AND
                avd.amount_cr > 0
                        GROUP by avd.project_id, project.name, avd.voucher_id
                ) credit
                LEFT JOIN
                (select avd.voucher_id, sum(avd.amount_dr) dr
                FROM acc_voucher_details avd
                        LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                        LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                        WHERE avd.group_id IN(:accGroupBank, :accGroupCash)
                        GROUP by avd.voucher_id
                ) debit ON credit.voucher_id = debit.voucher_id
                GROUP BY credit.project_id, credit.project_name
                HAVING (sum(credit.cr)-sum(debit.dr)) > 0
                ORDER BY credit.project_name) count_total
        """

        if (accChartOfAccount) {
            queryStr = """
                SELECT credit.project_id, credit.project_name, (sum(credit.cr)-sum(debit.dr)) amount_cr
                FROM
                (select avd.project_id, project.name AS project_name,
                        avd.voucher_id, sum(avd.amount_cr) cr
                FROM acc_voucher_details avd
                        LEFT JOIN acc_voucher av ON av.id =voucher_id
                        LEFT JOIN project ON project.id = avd.project_id
                LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                WHERE (av.voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate) AND
                        coa.acc_group_id IN (${lstGroupIds}) AND
                        av.project_id IN (${lstProject}) AND
                        av.posted_by > :postedByParam  AND
                        avd.coa_id = :coaId AND
                        avd.amount_cr > 0
                GROUP by avd.project_id, project.name, avd.voucher_id
                ) credit
                LEFT JOIN
                (select avd.voucher_id, sum(avd.amount_dr) dr
                FROM acc_voucher_details avd
                        LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                        LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                        WHERE avd.group_id IN( :accGroupBank, :accGroupCash)
                        GROUP by avd.voucher_id
                ) debit ON credit.voucher_id = debit.voucher_id
                GROUP BY credit.project_id, credit.project_name
                HAVING (sum(credit.cr)-sum(debit.dr)) > 0
                ORDER BY credit.project_name
                LIMIT :resultPerPage  OFFSET :start
            """

            queryCount = """
            SELECT COALESCE(COUNT(count_total.count),0) count
            FROM(
                SELECT COALESCE(COUNT(credit.project_id),0) count
                FROM
                (select avd.project_id, project.name AS project_name,
                        avd.voucher_id, sum(avd.amount_cr) cr
                FROM acc_voucher_details avd
                        LEFT JOIN acc_voucher av ON av.id =voucher_id
                        LEFT JOIN project ON project.id = avd.project_id
                LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                WHERE (av.voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate) AND
                coa.acc_group_id IN (${lstGroupIds}) AND
                av.project_id IN (${lstProject}) AND
                av.posted_by > :postedByParam  AND
                avd.coa_id = :coaId AND
                avd.amount_cr > 0
                        GROUP by avd.project_id, project.name, avd.voucher_id
                ) credit
                LEFT JOIN
                (select avd.voucher_id, sum(avd.amount_dr) dr
                FROM acc_voucher_details avd
                        LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                        LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                        WHERE avd.group_id IN(:accGroupBank, :accGroupCash)
                        GROUP by avd.voucher_id
                ) debit ON credit.voucher_id = debit.voucher_id
                GROUP BY credit.project_id, credit.project_name
                HAVING (sum(credit.cr)-sum(debit.dr)) > 0
                ORDER BY credit.project_name) count_total
                """
        }

        Map queryParams = [
                fromVoucherDate: DateUtility.getSqlDate(fromVoucherDate),
                toVoucherDate: DateUtility.getSqlDate(toVoucherDate),
                postedByParam: postedByParam,
                coaId: accChartOfAccount ? accChartOfAccount.id : 0,
                accGroupBank: accGroupBank.id,
                accGroupCash: accGroupCash.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> expenseList = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(queryCount, queryParams)

        int count = 0
        if (resultCount.size() > 0) {
            count = (int) resultCount[0][0]
        }
        return [expenseList: expenseList, count: count]
    }
}
