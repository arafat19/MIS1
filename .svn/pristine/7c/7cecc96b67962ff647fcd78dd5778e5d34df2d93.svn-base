package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of ledger in UI
 *  For details go through Use-Case doc named 'ShowForLedgerActionService'
 */
class ShowForLedgerActionService extends BaseService implements ActionIntf {

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String FAILURE_MSG = "Fail to load ledger"
    private static final String LEDGER_MAP = "ledgerMap"
    private static final String SORT_COLUMN = "traceNo"
    private static final String PROJECT_ID = "projectId"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not mapped with any project"
    private static final String GRID_OBJ_COA = "gridObjCoa"

    private Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get group list for dropDown and group ledger list for grid
     * Build Ledger Map
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            Date fromDate = new Date()
            Date toDate = new Date()
            initPager(params)
            List<Long> projectIds = []
            long projectId = -1L

            if (params.projectId) {      // request from T.Balance
                projectId = Long.parseLong(params.projectId.toString())
                if (projectId == -1) {     //  search by ALL project
                    projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                } else {
                    projectIds << new Long(projectId)
                }
            } else {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            }
            // check is the user mapped with any project or not
            if (projectIds.size() == 0) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                return result
            }

            if (params.coaId) {
                this.sortColumn = SORT_COLUMN
                this.sortOrder = ASCENDING_SORT_ORDER
                Long coaId = Long.parseLong(params.coaId.toString())
                if (params.fromDate && params.toDate) {
                    fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
                    toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
                }

                AccChartOfAccount accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(coaId)
                if (accChartOfAccount) {
                    //if postedByParam  = 0 the show Only Posted Voucher
                    //if postedByParam  = -1 the show both Posted & Unposted Voucher
                    long postedByParam = new Long(-1)
                    SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
                    if (sysConfiguration) {
                        postedByParam = Long.parseLong(sysConfiguration.value)
                    }
                    LinkedHashMap ledgerMap = buildLedgerMap(accChartOfAccount, fromDate, toDate, projectIds, postedByParam)
                    result.put(LEDGER_MAP, ledgerMap)
                }
            }

            //Get list of COA from utility
            int countCoa = accChartOfAccountCacheUtility.count()
            List lstChartOfAccount = accChartOfAccountCacheUtility.list(this)
            List gridListCoa = wrapChartOfAccount(lstChartOfAccount, start)
            Map gridObjChartOfAcc = [page: pageNumber, total: countCoa, rows: gridListCoa]
            result.put(GRID_OBJ_COA, gridObjChartOfAcc)

            result.put(PROJECT_ID, projectId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * do nothing for build failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Give Ledger Map
     * Wrap Ledger List
     * @param accChartOfAccount - accChartOfAccount object
     * @param fromDate - from Date from param
     * @param toDate - to Date from param
     * @param projectIds - all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - built ledger map
     */
    private LinkedHashMap buildLedgerMap(AccChartOfAccount accChartOfAccount, Date fromDate, Date toDate, List<Long> projectIds, long postedByParam) {
        LinkedHashMap serviceReturn = getLedgerListByCoaAndDateRange(accChartOfAccount.id, fromDate, toDate, projectIds, postedByParam)
        List<AccVoucherModel> ledgerList = (List<AccVoucherModel>) serviceReturn.ledgerList

        List ledgerListWrap = wrapLedgerListInGridEntityList(ledgerList, start)
        int count = serviceReturn.count
        Map gridOutput = [page: pageNumber, total: count, rows: ledgerListWrap]

        List<Long> lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()

        List prevBalanceList = AccVoucherModel.previousBalance(accChartOfAccount.id, fromDate, lstProjectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
        double prevBalance = prevBalanceList[0] ? prevBalanceList[0] : 0d
        String previousBalanceStr = Tools.makeAmountWithThousandSeparator(prevBalance)
        LinkedHashMap ledgerMap = [
                coaDescription: accChartOfAccount.description,
                coaId: accChartOfAccount.id,
                coaCode: accChartOfAccount.code,
                printDate: DateUtility.getDateFormatAsString(new Date()),
                ledgerListWrap: gridOutput,
                fromDate: DateUtility.getDateForUI(fromDate),
                toDate: DateUtility.getDateForUI(toDate),
                previousBalance: getBalanceString(previousBalanceStr)
        ]
        return ledgerMap
    }

    /**
     * Wrap list of ledger in grid entity
     * @param ledgerList -list of ledger object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ledger
     */
    private List wrapLedgerListInGridEntityList(List<AccVoucherModel> ledgerList, int start) {
        List lstLedger = [] as List

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

    private String getBalanceString(String balance) {
        if (balance.indexOf(NEGATIVE_SIGN) != -1) {     // negative sign found
            return balance + LABEL_CR
        } else {                                        // positive balance
            return balance + LABEL_DR
        }
    }

    private List wrapChartOfAccount(List<AccChartOfAccount> accChartOfAccountList, int start) {
        List accChartOfAccounts = [] as List
        int counter = start + 1
        SystemEntity accSource

        for (int i = 0; i < accChartOfAccountList.size(); i++) {
            AccChartOfAccount accChartOfAccount = accChartOfAccountList[i]
            accSource = (SystemEntity) accSourceCacheUtility.read(accChartOfAccount.accSourceId)
            GridEntity obj = new GridEntity()
            obj.id = accChartOfAccount.id
            obj.cell = [
                    accChartOfAccount.id,
                    accChartOfAccount.code,
                    accChartOfAccount.description,
                    accSource.id,
                    accSource.key
            ]
            accChartOfAccounts << obj
            counter++
        }
        return accChartOfAccounts
    }

    //getLedger in date range for All projects(which can be accessed by the user & which has no project)
    public LinkedHashMap getLedgerListByCoaAndDateRange(long coaId, Date fromVoucherDate, Date toVoucherDate, List projectIds, long postedByParam) {
        List<AccVoucherModel> ledgerList = AccVoucherModel.listLedgerByCoaAndProject(coaId, fromVoucherDate, toVoucherDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
        int count = AccVoucherModel.listLedgerByCoaAndProject(coaId, fromVoucherDate, toVoucherDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).count()
        return [ledgerList: ledgerList, count: count]
    }
}
