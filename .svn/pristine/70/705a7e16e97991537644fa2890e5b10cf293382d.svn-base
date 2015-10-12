package com.athena.mis.accounting.actions.report.projectwiseexpense

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools

//import SysConfiguration
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Fetch list of chart of account with debit amount related to specific project.
 * For details go through Use-Case doc named 'ListForProjectWiseExpenseDetailsActionService'
 */
class ListForProjectWiseExpenseDetailsActionService extends BaseService implements ActionIntf {

    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to show expense details."
    private static final String EXPENSE_LIST_WRAP = "expenseListWrap"
    private static final String EXPENSE_GRID_OBJ = "expenseGridObj"
    private static final String TOTAL_IN_BOLD = "<b>Total:</b>"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check validation for input date in UI level.
     * @param parameters - serialized parameters received from UI
     * @param obj - N/A
     * @return - a map containing isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.coaId || !params.fromDate || !params.toDate || !params.projectId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
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
     * Get balance list against specific project id
     * @param parameters -serialized parameters UI
     * @param obj -N/A
     * @return - a map containing project wise balance list
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            this.initPager(parameterMap)      // initialized parameter for flexi grid
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            long coaId = Long.parseLong(parameterMap.coaId.toString())

            List<Long> groupIds = []
            long groupId
            if (parameterMap.accGroupId.equals(Tools.EMPTY_SPACE)) {
                groupIds = accGroupCacheUtility.listOfAccGroupBankCashId()
            } else {
                groupId = Long.parseLong(parameterMap.accGroupId.toString())
                groupIds << new Long(groupId)
            }
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            /* if postedByParam  = 0 the show Only Posted Voucher
               if postedByParam  = -1 the show both Posted & Un-posted Voucher */
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }
            List<GroovyRowResult> expenseListDetails = listProjectWiseExpenseDetails(groupIds, coaId, projectId, fromDate, toDate, postedByParam)

            List expenseListWrap = wrapExpenseListDetails(expenseListDetails)
            result.put(EXPENSE_LIST_WRAP, expenseListWrap)
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
     * Wrap project wise balance list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List expenseListWrap = (List) executeResult.get(EXPENSE_LIST_WRAP)
            int count = expenseListWrap.size()
            Map gridOutput = [page: this.pageNumber, total: count, rows: expenseListWrap]
            result.put(EXPENSE_GRID_OBJ, gridOutput)
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
    private List wrapExpenseListDetails(List<GroovyRowResult> expenseListDetails) {
        List lstExpenseDetails = [] as List
        GroovyRowResult singleRow
        GridEntity obj
        double totDr = 0.0D

        for (int i = 0; i < expenseListDetails.size(); i++) {
            singleRow = expenseListDetails[i]
            obj = new GridEntity()
            obj.id = singleRow.id
            totDr = totDr + singleRow.amount_dr
            obj.cell = [
                    singleRow.code,
                    singleRow.description,
                    singleRow.amount_dr_str
            ]
            lstExpenseDetails << obj
        }
        obj = new GridEntity()
        obj.id = 0
        String total_dr = Tools.makeAmountWithThousandSeparator(totDr)
        obj.cell = [TOTAL_IN_BOLD, Tools.EMPTY_SPACE, Tools.makeBold(total_dr), Tools.EMPTY_SPACE]
        lstExpenseDetails << obj
        return lstExpenseDetails
    }

    //@todo: use above view + AccVoucherModel
    /**
     * Get expense list of given project id
     * @param accGroupId - group id
     * @param coaId - chart of account id
     * @param projectId - project id
     * @param fromVoucherDate - starting date of voucher
     * @param toVoucherDate - ending date of voucher
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - list of project wise expense details
     */
    private List<GroovyRowResult> listProjectWiseExpenseDetails(List accGroupIds, long coaId, long projectId, Date fromVoucherDate, Date toVoucherDate, long postedByParam) {
        String lstGroupIds = Tools.buildCommaSeparatedStringOfIds(accGroupIds)
        AccGroup accGroupBank = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_BANK)
        AccGroup accGroupCash = accGroupCacheUtility.readBySystemAccGroup(accGroupCacheUtility.ACC_GROUP_CASH)

        String queryForSpecificCOA = """
                SELECT  coa.id, coa.code, coa.description,
                        (to_char(SUM(avd.amount_dr), '${Tools.DB_CURRENCY_FORMAT}')) AS amount_dr_str,
                        SUM(avd.amount_dr) AS amount_dr
                FROM acc_voucher_details avd
                LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                WHERE avd.voucher_id IN (
                    SELECT avd.voucher_id
                    FROM acc_voucher_details avd
                    LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                    WHERE avd.project_id = :projectId
                    AND avd.coa_id = :coaId
                    AND avd.amount_cr >0
                    AND (av.voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate)
                    AND av.posted_by > :postedByParam
                    )
                AND avd.amount_dr > 0
                AND avd.group_id NOT IN(:accGroupBank, :accGroupCash)
                GROUP BY coa.id, coa.code, coa.description
                ORDER BY coa.code ASC
            """

        String queryForAllCOA = """
                SELECT  coa.id, coa.code, coa.description,
                        (to_char(SUM(avd.amount_dr), '${Tools.DB_CURRENCY_FORMAT}')) AS amount_dr_str,
                        SUM(avd.amount_dr) AS amount_dr
                FROM acc_voucher_details avd
                LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                WHERE avd.voucher_id IN (
                    SELECT avd.voucher_id
                    FROM acc_voucher_details avd
                    LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                    LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                    WHERE avd.project_id = :projectId
                    AND coa.acc_group_id IN (${lstGroupIds})
                    AND avd.amount_cr > 0
                    AND (av.voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate)
                    AND av.posted_by > :postedByParam
                    )
                AND avd.amount_dr > 0
                AND avd.group_id NOT IN(:accGroupBank, :accGroupCash)
                GROUP BY coa.id, coa.code, coa.description
                ORDER BY coa.code ASC
            """
        Map queryParams = [
                projectId: projectId,
                coaId: coaId,
                fromVoucherDate: DateUtility.getSqlDate(fromVoucherDate),
                toVoucherDate: DateUtility.getSqlDate(toVoucherDate),
                accGroupBank: accGroupBank.id,
                accGroupCash: accGroupCash.id,
                postedByParam: postedByParam
        ]
        List<GroovyRowResult> expenseDetailsList
        if (coaId > 0) {
            expenseDetailsList = executeSelectSql(queryForSpecificCOA, queryParams)
        } else {
            expenseDetailsList = executeSelectSql(queryForAllCOA, queryParams)
        }
        return expenseDetailsList
    }
}
