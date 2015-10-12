package com.athena.mis.accounting.actions.report.sourcewisebalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get list of source wise balance
 * For details go through Use-Case doc named 'ListForSourceWiseBalanceActionService'
 */
class ListForSourceWiseBalanceActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to load source wise balance report"
    private static final String SOURCE_WISE_BALANCE_LIST = "sourceWiseBalanceList"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String NOT_FOUND = "Balance not found for the selected source"
    private static final String COUNT = "count"
    private static final String SORT_NAME = "source_name"
    private static final String SORT_ORDER = "ASC"
    private static final String SOURCE_TYPE_ID = "sourceTypeId"
    private static final String TO_DATE = "toDate"
    private static final String FROM_DATE = "fromDate"

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

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
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.sourceTypeId || !parameterMap.toDate || !parameterMap.fromDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date toDate = DateUtility.parseMaskedDate(parameterMap.toDate)
            Date fromDate = DateUtility.parseMaskedDate(parameterMap.fromDate)
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(fromDate, toDate, accFinancialYear)
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
     * Supplier wise balance list in a specific date range
     * @param parameters -serialized parameters UI
     * @param obj -N/A
     * @return - a map containing supplier wise balance list, source type & date range
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = SORT_ORDER
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)       // initialized parameters for flexi grid
            long sourceTypeId = Long.parseLong(parameterMap.sourceTypeId.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())

            List<Long> projectIds = [] //main list of projectIds
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIds << new Long(projectId)
            }

            List<Long> lstSourceCategoryIds = []
            if (parameterMap.sourceCategoryId.equals(Tools.EMPTY_SPACE)) {
                lstSourceCategoryIds = listSourceCategoryIds(sourceTypeId)
            } else {
                long sourceCategoryId = Long.parseLong(parameterMap.sourceCategoryId)
                lstSourceCategoryIds << sourceCategoryId
            }

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            long coaId = parameterMap.coaId.equals(Tools.EMPTY_SPACE)?-1:Long.parseLong(parameterMap.coaId.toString())
            LinkedHashMap serviceReturn = getSourceWiseBalance(sourceTypeId, toDate, fromDate, postedByParam, coaId, projectIds, lstSourceCategoryIds)
            List<GroovyRowResult> sourceWiseBalanceList = serviceReturn.sourceBalanceList
            if (sourceWiseBalanceList.size() <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            int count = serviceReturn.count
            List wrapSourceBalanceList = wrapSourceWiseBalanceList(sourceWiseBalanceList, start)
            result.put(SOURCE_WISE_BALANCE_LIST, wrapSourceBalanceList)
            result.put(COUNT, count)
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(SOURCE_TYPE_ID, sourceTypeId)
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
     * Wrap supplier wise balance list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List sourceBalanceListWrap = (List) executeResult.get(SOURCE_WISE_BALANCE_LIST)
            int count = (int) executeResult.get(COUNT)
            Map gridOutput = [page: pageNumber, total: count, rows: sourceBalanceListWrap]
            result.put(SOURCE_WISE_BALANCE_LIST, gridOutput)
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(SOURCE_TYPE_ID, executeResult.get(SOURCE_TYPE_ID))
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
     * Wrap supplier wise balance list in grid entity
     * @param sourceWiseBalanceList -list of supplier wise balance object(s)
     * @param start -starting index of the page
     * @return -list of wrapped supplier wise balance
     */
    private List wrapSourceWiseBalanceList(List<GroovyRowResult> sourceWiseBalanceList, int start) {
        List lstSourceBalance = [] as List

        int counter = start + 1
        GroovyRowResult sourceBalance
        GridEntity obj

        for (int i = 0; i < sourceWiseBalanceList.size(); i++) {
            sourceBalance = sourceWiseBalanceList[i]
            obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    sourceBalance.source_id,
                    sourceBalance.source_name,
                    sourceBalance.prev_balance,
                    sourceBalance.dr_balance,
                    sourceBalance.cr_balance,
                    sourceBalance.total_dr_balance,
                    sourceBalance.total_cr_balance
            ]
            lstSourceBalance << obj
            counter++
        }
        return lstSourceBalance
    }

    /**
     * Following method is used to get supplier wise balance report
     * @param sourceTypeId - source type
     * @param toDate - current date(today)
     * @param fromDateDate - start date
     * @param postedByParam - determines whether voucher is post or un-posted
     * @param coaId - chart of account(coa) id
     * @param projectIds - project ids
     * @return source wise balance list and count their total number
     */
    private LinkedHashMap getSourceWiseBalance(long sourceTypeId, Date toDate, Date fromDate, long postedByParam, long coaId, List projectIds, List<Long> lstSourceCategoryIds) {

        String lstProject = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String lstSourceCategory = Tools.buildCommaSeparatedStringOfIds(lstSourceCategoryIds)

        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeCustomer = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_CUSTOMER, companyId)
        SystemEntity accSourceTypeEmployee = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE, companyId)
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        SystemEntity accSourceTypeItem = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_ITEM, companyId)
        SystemEntity accSourceTypeLc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LC, companyId)
        SystemEntity accSourceTypeIpc = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_IPC, companyId)
        SystemEntity accSourceTypeLeaseAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT, companyId)

        String coaIdParams = "avd.coa_id > 0"
        if (coaId > 0) {
            coaIdParams = "avd.coa_id = " + coaId
        }

        String queryStr = """
            SELECT
            source.source_id,
            CASE
                WHEN source.source_type_id = :sourceTypeCustomer
                THEN customer.full_name
                WHEN source.source_type_id = :sourceTypeEmployee
                THEN employee.full_name
                WHEN source.source_type_id = :sourceTypeSubAccount
                THEN acc_sub_account.description
                WHEN source.source_type_id = :sourceTypeSupplier
                THEN supplier.name
                WHEN source.source_type_id = :sourceTypeItem
                THEN item.name
                WHEN source.source_type_id = :sourceTypeLc
                THEN lc.lc_no
                WHEN source.source_type_id = :sourceTypeIpc
                THEN ipc.ipc_no
                WHEN source.source_type_id = :sourceTypeLeaseAccount
                THEN ala.institution
            END AS source_name,

            to_char(coalesce(previous_balance.balance,0),'${Tools.DB_CURRENCY_FORMAT}') AS prev_balance,
            to_char(coalesce(avd.amount_dr,0),'${Tools.DB_CURRENCY_FORMAT}') AS dr_balance,
            to_char(coalesce(avd.amount_cr,0),'${Tools.DB_CURRENCY_FORMAT}') AS cr_balance,

            CASE
                WHEN (coalesce((avd.amount_dr-avd.amount_cr),0)+coalesce(previous_balance.balance,0)) > 0
                THEN to_char(ABS(coalesce((avd.amount_dr-avd.amount_cr),0)+coalesce(previous_balance.balance,0)),'${Tools.DB_CURRENCY_FORMAT}')
                ELSE  '৳ 0.00'
                END AS total_dr_balance,
                CASE
                WHEN (coalesce((avd.amount_dr-avd.amount_cr),0)+coalesce(previous_balance.balance,0)) < 0
                THEN to_char(ABS(coalesce((avd.amount_dr-avd.amount_cr),0)+coalesce(previous_balance.balance,0)),'${Tools.DB_CURRENCY_FORMAT}')
                ELSE  '৳ 0.00'
                END AS total_cr_balance

            FROM (
                SELECT DISTINCT source_type_id, source_id
                FROM acc_voucher_details avd
                LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                WHERE avd.source_type_id = :sourceTypeId
                AND avd.source_category_id IN (${lstSourceCategory})
                AND av.voucher_date <= :toDate
                AND av.company_id = :companyId
                AND av.posted_by > :postedByParam
                AND av.project_id IN (${lstProject}) AND ${coaIdParams}
            ) AS source

            FULL OUTER JOIN (
                SELECT SUM(amount_dr)amount_dr, SUM(amount_cr) amount_cr, source_id
                FROM acc_voucher_details avd
                LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                WHERE avd.source_type_id = :sourceTypeId
                AND avd.source_category_id IN (${lstSourceCategory})
                AND av.voucher_date BETWEEN :fromDate AND :toDate
                AND av.company_id = :companyId
                AND av.posted_by > :postedByParam
                AND av.project_id IN (${lstProject}) AND ${coaIdParams}
                GROUP BY source_id
            ) AS avd
            ON avd.source_id = source.source_id

            FULL OUTER JOIN (
                SELECT  avd.source_id, SUM(avd.amount_dr-avd.amount_cr) AS balance
                FROM acc_voucher_details avd
                LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                WHERE avd.source_type_id = :sourceTypeId
                AND avd.source_category_id IN (${lstSourceCategory})
                AND av.voucher_date < :fromDate
                AND av.company_id = :companyId
                AND av.posted_by > :postedByParam
                AND av.project_id IN (${lstProject}) AND ${coaIdParams}
                GROUP BY avd.source_id order by source_id
            ) AS previous_balance
            ON previous_balance.source_id = source.source_id

            LEFT JOIN supplier supplier ON source.source_id = supplier.id
            LEFT JOIN customer ON source.source_id = customer.id
            LEFT JOIN employee ON source.source_id = employee.id
            LEFT JOIN acc_sub_account ON source.source_id = acc_sub_account.id
            LEFT JOIN item ON source.source_id = item.id
            LEFT JOIN acc_lc lc ON source.source_id = lc.id
            LEFT JOIN acc_ipc ipc ON source.source_id = ipc.id
            LEFT JOIN acc_lease_account ala ON source.source_id = ala.id

            GROUP BY source.source_id, source.source_type_id, avd.amount_dr, avd.amount_cr, acc_sub_account.description,
            customer.full_name, employee.full_name, supplier.name,item.name, lc.lc_no, ipc.ipc_no, ala.institution, previous_balance.balance
            ORDER BY source_name ASC
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
            SELECT count(DISTINCT avd.source_id)
            FROM acc_voucher_details avd
            LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
            WHERE avd.source_type_id = :sourceTypeId
            AND avd.source_category_id IN (${lstSourceCategory})
            AND av.voucher_date <= :toDate
            AND av.company_id = :companyId
            AND av.posted_by > :postedByParam
            AND avd.source_id <> 0
            AND av.project_id IN (${lstProject}) AND ${coaIdParams}
        """

        Map queryParams = [
                sourceTypeCustomer: accSourceTypeCustomer.id,
                sourceTypeEmployee: accSourceTypeEmployee.id,
                sourceTypeSubAccount: accSourceTypeSubAccount.id,
                sourceTypeSupplier: accSourceTypeSupplier.id,
                sourceTypeItem: accSourceTypeItem.id,
                sourceTypeLc: accSourceTypeLc.id,
                sourceTypeIpc: accSourceTypeIpc.id,
                sourceTypeLeaseAccount: accSourceTypeLeaseAccount.id,
                sourceTypeId: sourceTypeId,
                toDate: DateUtility.getSqlDate(toDate),
                fromDate: DateUtility.getSqlDate(fromDate),
                companyId: companyId,
                postedByParam: postedByParam,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> sourceBalanceList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [sourceBalanceList: sourceBalanceList, count: count]
    }

    /**
     * Get list of source category ids
     * @param sourceTypeId -id pf source type
     * @return -a list of source category ids
     */
    private List<Long> listSourceCategoryIds(long sourceTypeId) {
        List lstSourceCategory = []
        List<Long> lstSourceCategoryIds = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(sourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                lstSourceCategory = supplierTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                lstSourceCategory = itemTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                lstSourceCategory = designationCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                lstSourceCategory = ListSubAccount()
                break
            default:
                break
        }
        if (lstSourceCategory.size() > 0) {
            lstSourceCategoryIds = Tools.getIds(lstSourceCategory)
        }
        lstSourceCategoryIds << 0L
        return lstSourceCategoryIds
    }

    private static final String SELECT_SUB_ACCOUNT_QUERY = """
        SELECT DISTINCT coa_id AS id
        FROM acc_sub_account
        WHERE company_id=:companyId
    """

    /**
     * Give Custom Source Category List For Sub Account
     * @return - list of custom Source Category
     */
    private List ListSubAccount() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> lstSubAccount = executeSelectSql(SELECT_SUB_ACCOUNT_QUERY, queryParams)
        return lstSubAccount
    }
}