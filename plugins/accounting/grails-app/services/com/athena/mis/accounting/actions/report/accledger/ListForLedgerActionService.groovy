package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of Ledger in the grid
 *  For details go through Use-Case doc named 'ListForLedgerActionService'
 */
class ListForLedgerActionService extends BaseService implements ActionIntf {

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String HEAD_NOT_FOUND = "Head not found"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate ledger"
    private static final String COA_OBJ = "coa"
    private static final String LEDGER_LIST = "ledgerList"
    private static final String LEDGER_LIST_WRAP = "ledgerListWrap"
    private static final String COUNT = "count"
    private static final String LEDGER_MAP = "ledgerMap"
    private static final String PREVIOUS_BALANCE = "previousBalance"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not mapped with any project"
    private static final String SORT_COLUMN = "voucherDate"

    protected final Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // Check required parameters
            if (!params.coaCode || !params.fromDate || !params.toDate) {
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
            String coaCode = params.coaCode.toString()
            AccChartOfAccount accChartOfAccount = accChartOfAccountCacheUtility.readByCode(coaCode)
            if (!accChartOfAccount) {
                result.put(Tools.MESSAGE, HEAD_NOT_FOUND)
                return result
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
     * Wrap ledger list for grid
     * @param parameters - serialized parameters UI
     * @param obj - map from executePreCondition method
     * @return - a map containing all objects necessary for buildSuccessResultForUI method
     */
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
            sortColumn = SORT_COLUMN
            sortOrder = ASCENDING_SORT_ORDER

            List<Long> projectIds = []

            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {   // for ALL Projects
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            } else {
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
            }

            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                return result
            }

            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) preResult.get(COA_OBJ)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            LinkedHashMap serviceReturn = getLedgerListByCoaAndDateRange(accChartOfAccount.id, fromDate, toDate, projectIds, postedByParam)  // get ledger list

            List<AccVoucherModel> ledgerList = (List<AccVoucherModel>) serviceReturn.ledgerList
            int count = (int) serviceReturn.count
            List prevBalanceList = AccVoucherModel.previousBalance(accChartOfAccount.id, fromDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
            double prevBalance = prevBalanceList[0] ? prevBalanceList[0] : 0d
            String prevBalanceStr = Tools.makeAmountWithThousandSeparator(prevBalance)
            List ledgerListWrap = wrapLedgerListInGridEntityList(ledgerList, start)
            result.put(LEDGER_LIST, ledgerListWrap)
            result.put(COUNT, count)
            result.put(PREVIOUS_BALANCE, getBalanceString(prevBalanceStr))

            // create the map to display label values
            LinkedHashMap ledgerMap = [
                    coaDescription: accChartOfAccount.description,
                    coaId: accChartOfAccount.id,
                    coaCode: accChartOfAccount.code,
                    printDate: DateUtility.getDateFormatAsString(new Date()),
                    fromDate: DateUtility.getDateForUI(fromDate),
                    toDate: DateUtility.getDateForUI(toDate)
            ]
            result.put(LEDGER_MAP, ledgerMap)
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
     * Receive ledger map, wrapped ledger list from execute method
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List ledgerListWrap = (List) executeResult.get(LEDGER_LIST)
            Map ledgerMap = (Map) executeResult.get(LEDGER_MAP)
            String previousBalance = (String) executeResult.get(PREVIOUS_BALANCE)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: ledgerListWrap]
            result.put(LEDGER_LIST_WRAP, gridOutput)
            result.put(LEDGER_MAP, ledgerMap)
            result.put(PREVIOUS_BALANCE, previousBalance)
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
     * Wrap list of ledger in grid entity
     * @param ledgerList -list of ledger object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ledger
     */
    private List wrapLedgerListInGridEntityList(List<AccVoucherModel> ledgerList, int start) {
        List lstLedger = []

        int counter = start + 1
        AccVoucherModel ledgerModel
        GridEntity obj

        for (int i = 0; i < ledgerList.size(); i++) {
            ledgerModel = ledgerList[i]
            obj = new GridEntity()
            obj.id = ledgerModel.voucherId
            obj.cell = [
                    counter,
                    ledgerModel.strVoucherDate,
                    ledgerModel.traceNo,
                    ledgerModel.chequeNo,
                    ledgerModel.particulars,
                    ledgerModel.strAmountDr,
                    ledgerModel.strAmountCr,
                    ledgerModel.voucherTypeId
            ]
            lstLedger << obj
            counter++
        }
        return lstLedger
    }

    private static final NEGATIVE_SIGN = "-"
    private static final LABEL_DR = " (Dr)"
    private static final LABEL_CR = " (Cr)"

    /**
     * Give Balance string
     * @param balance - balance from execute method
     * @return balance string for execute method
     */
    private String getBalanceString(String balance) {
        if (balance.indexOf(NEGATIVE_SIGN) != -1) {     // negative sign found
            return balance + LABEL_CR
        } else {                                        // positive balance
            return balance + LABEL_DR
        }
    }

    /**
     * Give Ledger List
     * @param coaId - chart of account id from execute method
     * @param fromVoucherDate - from voucher date comes from parameter
     * @param toVoucherDate - to voucher date comes from parameter
     * @param projectIds - all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - generated map of ledger list and it's count
     */
    //getLedger in date range for All projects(which can be accessed by the user & which has no project)
    private LinkedHashMap getLedgerListByCoaAndDateRange(long coaId, Date fromVoucherDate, Date toVoucherDate, List projectIds, long postedByParam) {
        List<AccVoucherModel> ledgerList = AccVoucherModel.listLedgerByCoaAndProject(coaId, fromVoucherDate, toVoucherDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
        int count = AccVoucherModel.listLedgerByCoaAndProject(coaId, fromVoucherDate, toVoucherDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).count()
        return [ledgerList: ledgerList, count: count]
    }
}