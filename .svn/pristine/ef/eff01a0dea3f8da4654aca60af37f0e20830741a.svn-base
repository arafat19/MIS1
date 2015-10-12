package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccDivision
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of source Ledger in the grid
 *  For details go through Use-Case doc named 'ListForSourceLedgerActionService'
 */
class ListForSourceLedgerActionService extends BaseService implements ActionIntf {

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate ledger"
    private static final String LEDGER_LIST = "ledgerList"
    private static final String LEDGER_LIST_WRAP = "ledgerListWrap"
    private static final String COUNT = "count"
    private static final String PREVIOUS_BALANCE = "previousBalance"
    private static final String SOURCE_LEDGER_NOT_FOUND = "Source ledger not found within given dates"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String ACC_SOURCE_TYPE_ID = "accSourceTypeId"
    private static final String SOURCE_CATEGORY_ID = "sourceCategoryId"
    private static final String SOURCE_ID = "sourceId"
    private static final String PROJECT_NOT_FOUND_MESSAGE = "User is not mapped with any project"
    private static final String SORT_COLUMN = "voucherDate"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // checking required parameters
            if ((!params.accSourceTypeId) || (!params.fromDate) || (!params.toDate)) {
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
     * Wrap source ledger list for grid
     * @param parameters - serialized parameters UI
     * @param obj - N/A
     * @return - a map containing ledger List Wrap, count, BalanceString & ledgerMap
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }

            initPager(parameterMap)


            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            long accSourceTypeId = Long.parseLong(parameterMap.accSourceTypeId.toString())
            // assign default value -1 for empty string that indicates "ALL"
            long sourceCategoryId =parameterMap.sourceCategoryId.equals(Tools.EMPTY_SPACE)?-1: Long.parseLong(parameterMap.sourceCategoryId.toString())
            long sourceId =parameterMap.sourceId.equals(Tools.EMPTY_SPACE)?-1:Long.parseLong(parameterMap.sourceId.toString())
            long projectId = parameterMap.projectId.equals(Tools.EMPTY_SPACE)?-1:Long.parseLong(parameterMap.projectId.toString())

            List<Long> projectIds = []
            if (projectId <= 0) {
                projectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (projectIds.size() <= 0) {
                    result.put(Tools.MESSAGE, PROJECT_NOT_FOUND_MESSAGE)
                    return result
                }
            } else {
                projectIds << new Long(projectId)
            }

            sortOrder = ASCENDING_SORT_ORDER
            sortColumn = SORT_COLUMN

            //if postedByParam  = 0 the show Only Posted Voucher
            //if postedByParam  = -1 the show both Posted & Unposted Voucher
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            List lstSource = []
            if (sourceId > 0) {
                lstSource << sourceId
            } else {
                lstSource = getSourceList(sourceCategoryId, accSourceTypeId)
            }

            List<AccVoucherModel> ledgerList = AccVoucherModel.ledgerBySourceTypeAndSourceWithOrder(accSourceTypeId, lstSource, fromDate, toDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list(offset: start, max: resultPerPage)
            int count = (int) AccVoucherModel.ledgerBySourceTypeAndSource(accSourceTypeId, lstSource, fromDate, toDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).count()
            List prevBalanceList = AccVoucherModel.previousBalanceBySourceTypeAndSource(accSourceTypeId, lstSource, fromDate, projectIds, postedByParam, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
            double prevBalance = prevBalanceList[0] ? prevBalanceList[0] : 0d
            String prevBalanceStr = Tools.makeAmountWithThousandSeparator(prevBalance)

            if (ledgerList.size() <= 0) {
                result.put(Tools.MESSAGE, SOURCE_LEDGER_NOT_FOUND)
                return result
            }
            List ledgerListWrap = wrapLedgerListInGridEntityList(ledgerList, start)

            result.put(PREVIOUS_BALANCE, prevBalanceStr)
            result.put(LEDGER_LIST, ledgerListWrap)
            result.put(COUNT, count)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(ACC_SOURCE_TYPE_ID, accSourceTypeId)
            result.put(SOURCE_CATEGORY_ID, sourceCategoryId)
            result.put(SOURCE_ID, sourceId)
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
     * Receive map with necessary object from execute method
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List ledgerListWrap = (List) executeResult.get(LEDGER_LIST)
            String previousBalance = (String) executeResult.get(PREVIOUS_BALANCE)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: ledgerListWrap]
            result.put(LEDGER_LIST_WRAP, gridOutput)
            result.put(PREVIOUS_BALANCE, previousBalance)
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(ACC_SOURCE_TYPE_ID, executeResult.get(ACC_SOURCE_TYPE_ID))
            result.put(SOURCE_CATEGORY_ID, executeResult.get(SOURCE_CATEGORY_ID))
            result.put(SOURCE_ID, executeResult.get(SOURCE_ID))
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

    /**
     * Give Source List
     * @param sourceCategoryId - sourceCategoryId from params
     * @param accSourceTypeId - accSourceTypeId from params
     * @return - source list
     */
    private List getSourceList(long sourceCategoryId, long accSourceTypeId) {
        List sourceList = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                sourceList = (sourceCategoryId > 0) ? supplierCacheUtility.listBySupplierTypeId(sourceCategoryId) : supplierCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                sourceList = (sourceCategoryId > 0) ? itemCacheUtility.listByItemTypeId(sourceCategoryId) : itemCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                sourceList = (sourceCategoryId > 0) ? employeeCacheUtility.listByDesignationForDropDown(sourceCategoryId) : employeeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                sourceList = customerCacheUtility.listByCompanyForDropDown()
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                sourceList = (sourceCategoryId > 0) ? accSubAccountCacheUtility.searchByCoaIdAndCompany(sourceCategoryId) : accSubAccountCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                sourceList = accLcCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                sourceList = accIpcCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                sourceList = accLeaseAccountCacheUtility.list()
                break
            default:
                break
        }
        if (sourceList.size() == 0) {
            return [new Long(0)]  // in case unknown type OR cacheUtil returns empty List, fill list with Zero to avoid exception in query
        }
        sourceList = Tools.getIds(sourceList)
        return sourceList
    }
}