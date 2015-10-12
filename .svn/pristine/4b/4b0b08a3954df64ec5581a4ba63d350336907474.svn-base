package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show Group ledger and list of group ledger in UI
 *  For details go through Use-Case doc named 'ShowEmployeeActionService'
 */
class ShowForGroupLedgerActionService extends BaseService implements ActionIntf {

    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String FAILURE_MSG = "Fail to load Group ledger"
    private static final String LEDGER_MAP = "ledgerMap"
    private static final String SORT_COLUMN = "voucherDate, voucherDetailsId"

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
            Date fromDate
            Date toDate
            initPager(params)
            this.sortOrder = ASCENDING_SORT_ORDER
            this.sortColumn = SORT_COLUMN
            if (params.groupId) {
                Long groupId = Long.parseLong(params.groupId.toString())
                if (params.fromDate && params.toDate) {
                    fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
                    toDate = DateUtility.parseMaskedToDate(params.toDate.toString())
                }
                AccGroup accGroup = (AccGroup) accGroupCacheUtility.readByCompany(groupId)
                if (accGroup) {
                    //if postedByParam  = 0 the show Only Posted Voucher
                    //if postedByParam  = -1 the show both Posted & Unposted Voucher
                    long postedByParam = new Long(-1)
                    SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
                    if (sysConfiguration) {
                        postedByParam = Long.parseLong(sysConfiguration.value)
                    }
                    List<Long> projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                    LinkedHashMap ledgerMap = buildLedgerMap(accGroup, fromDate, toDate, projectIds, postedByParam)
                    result.put(LEDGER_MAP, ledgerMap)
                }
            }
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
     * @param accGroup - AccGroup object
     * @param fromDate - from Date from param
     * @param toDate - to Date from param
     * @param projectIds - all project ids
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - built ledger map
     */
    private LinkedHashMap buildLedgerMap(AccGroup accGroup, Date fromDate, Date toDate, List<Long> projectIds, long postedByParam) {
        List<AccVoucherModel> ledgerList = AccVoucherModel.ledgerByGroup(accGroup.id, fromDate, toDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list(offset: start, max: resultPerPage)

        List ledgerListWrap = wrapLedgerListInGridEntityList(ledgerList, start)
        int count = AccVoucherModel.ledgerByGroup(accGroup.id, fromDate, toDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).count()
        Map gridOutput = [page: pageNumber, total: count, rows: ledgerListWrap]

        List prevBalanceList = AccVoucherModel.previousBalanceByGroup(accGroup.id, fromDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
        double prevBalance = prevBalanceList[0] ? prevBalanceList[0] : 0d
        String previousBalanceStr = Tools.makeAmountWithThousandSeparator(prevBalance)
        LinkedHashMap ledgerMap = [
                printDate: DateUtility.getDateFormatAsString(new Date()),
                ledgerListWrap: gridOutput,
                fromDate: DateUtility.getDateForUI(fromDate),
                toDate: DateUtility.getDateForUI(toDate),
                previousBalance: getBalanceString(previousBalanceStr)
        ]
        return ledgerMap
    }

    /**
     * Wrap list of group ledger in grid entity
     * @param ledgerList -list of ledger object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ledger
     */
    private List wrapLedgerListInGridEntityList(List<AccVoucherModel> ledgerList, int start) {
        List lstLedger = [] as List

        int counter = start + 1
        AccVoucherModel ledger
        GridEntity obj
        for (int i = 0; i < ledgerList.size(); i++) {
            ledger = ledgerList[i]
            obj = new GridEntity()
            obj.id = ledger.voucherId
            obj.cell = [
                    counter,
                    ledger.strVoucherDate,
                    ledger.traceNo,
                    ledger.coaCode,
                    ledger.chequeNo,
                    ledger.particulars,
                    ledger.strAmountDr,
                    ledger.strAmountCr,
                    ledger.voucherTypeId
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
}
