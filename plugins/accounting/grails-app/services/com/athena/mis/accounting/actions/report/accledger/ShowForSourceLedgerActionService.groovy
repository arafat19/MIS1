package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccDivisionCacheUtility
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of source ledger in UI
 *  For details go through Use-Case doc named 'ShowForLedgerActionService'
 */
class ShowForSourceLedgerActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility

    private static final String FAILURE_MSG = "Fail to load source ledger"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String SORT_COLUMN = "voucherDate"
    private static final String PREVIOUS_BALANCE = "previousBalance"
    private static final String ACC_SOURCE_TYPE_ID = "accSourceTypeId"
    private static final String SOURCE_CATEGORY_ID = "sourceCategoryId"
    private static final String LST_SOURCE_CATEGORY = "lstSourceCategory"
    private static final String SOURCE_ID = "sourceId"
    private static final String LST_SUPPLIER = "lstSupplier"
    private static final String PROJECT_ID = "projectId"
    private static final String LEDGER_LIST_WRAP = "ledgerListWrap"
    private static final String KEY = "key"

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
     * Make a map with required information to show on UI
     * If navigated form another report then-
     * 1. Get all required information to show on UI
     * 2. Get source ledger list for grid
     * @param parameters - N/A
     * @param obj - N/A
     * @return - a map containing all necessary objects
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            Date fromDate
            Date toDate

            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (params.projectId && params.supplierId && params.fromDate && params.toDate) {
                if (!params.rp) {
                    params.rp = 20
                    params.page = 1
                }
                sortOrder = ASCENDING_SORT_ORDER
                sortColumn = SORT_COLUMN
                initPager(params)

                long supplierId = Long.parseLong(params.supplierId.toString())
                long projectId = Long.parseLong(params.projectId.toString())
                fromDate = DateUtility.parseMaskedDate(params.fromDate.toString())
                toDate = DateUtility.parseMaskedDate(params.toDate.toString())

                AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
                if (accFinancialYear) {
                    String exceedsFinancialYear = DateUtility.checkFinancialDateRange(fromDate, toDate, accFinancialYear)
                    if (exceedsFinancialYear) {
                        result.put(Tools.MESSAGE, exceedsFinancialYear)
                        return result
                    }
                }
                SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
                Supplier supplier = (Supplier) supplierCacheUtility.read(supplierId)
                List lstSourceCategory = supplierTypeCacheUtility.list()
                lstSourceCategory = Tools.buildSourceDropDown(lstSourceCategory, KEY)
                List lstSupplier = supplierCacheUtility.listBySupplierTypeId(supplier.supplierTypeId)

                //if postedByParam  = 0 the show Only Posted Voucher
                //if postedByParam  = -1 the show both Posted & Un-posted Voucher
                long postedByParam = new Long(-1)
                SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
                if (sysConfiguration) {
                    postedByParam = Long.parseLong(sysConfiguration.value)
                }

                List lstSource = []
                lstSource << supplierId
                List<Long> lstProjectIds = []
                if (projectId <= 0) {
                    lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                } else {
                    lstProjectIds << new Long(projectId)
                }

                List<AccVoucherModel> ledgerList = AccVoucherModel.ledgerBySourceTypeAndSourceWithOrder(accSourceTypeSupplier.id, lstSource, fromDate, toDate, lstProjectIds, postedByParam, companyId).list(offset: start, max: resultPerPage)
                int count = (int) AccVoucherModel.ledgerBySourceTypeAndSource(accSourceTypeSupplier.id, lstSource, fromDate, toDate, lstProjectIds, postedByParam, companyId).count()
                List prevBalanceList = AccVoucherModel.previousBalanceBySourceTypeAndSource(accSourceTypeSupplier.id, lstSource, fromDate, lstProjectIds, postedByParam, companyId).list()
                double prevBalance = prevBalanceList[0] ? prevBalanceList[0] : 0d
                String prevBalanceStr = Tools.makeAmountWithThousandSeparator(prevBalance)

                Map gridOutput
                if (ledgerList.size() <= 0) {
                    gridOutput = [page: 1, total: 0, rows: []]
                    prevBalanceStr = Tools.EMPTY_SPACE
                } else {
                    List ledgerListWrap = wrapLedgerListInGridEntityList(ledgerList, start)
                    gridOutput = [page: pageNumber, total: count, rows: ledgerListWrap]
                }

                result.put(LEDGER_LIST_WRAP, gridOutput)
                result.put(PREVIOUS_BALANCE, prevBalanceStr)
                result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
                result.put(TO_DATE, DateUtility.getDateForUI(toDate))
                result.put(ACC_SOURCE_TYPE_ID, accSourceTypeSupplier.id)
                result.put(SOURCE_CATEGORY_ID, supplier.supplierTypeId)
                result.put(SOURCE_ID, supplierId)
                result.put(PROJECT_ID, projectId)
                result.put(LST_SOURCE_CATEGORY, lstSourceCategory)
                result.put(LST_SUPPLIER, lstSupplier)
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
     * Wrap list of source ledger in grid entity
     * @param ledgerList -list of ledger object(s)
     * @param start -starting index of the page
     * @return -list of wrapped ledger
     */
    private List wrapLedgerListInGridEntityList(List<AccVoucherModel> ledgerList, int start) {
        List lstLedger = []
        int counter = start + 1
        AccVoucherModel ledger
        AccDivision division
        GridEntity obj
        for (int i = 0; i < ledgerList.size(); i++) {
            ledger = ledgerList[i]
            division = (AccDivision) accDivisionCacheUtility.read(ledger.divisionId)
            obj = new GridEntity()
            obj.id = ledger.voucherId
            obj.cell = [
                    counter,
                    ledger.strVoucherDate,
                    ledger.traceNo,
                    ledger.coaCode,
                    ledger.coaDescription,
                    division ? division.name : Tools.EMPTY_SPACE,
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
}
