package com.athena.mis.accounting.actions.report.accvoucherlist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.model.AccVoucherTypeModel
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Retrieve voucher list within specific date range
 * For details go through Use-Case doc named 'SearchForVoucherListActionService'
 */
class SearchForVoucherListActionService extends BaseService implements ActionIntf {

    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String VOUCHER_NOT_FOUND = "Voucher not found within given dates."
    private static final String VOUCHER_TYPE_NOT_FOUND = "Voucher Type not found."
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate voucher list."
    private static final String VOUCHER_LIST = "voucherList"
    private static final String VOUCHER_LIST_WRAP = "voucherListWrap"
    private static final String COUNT = "count"
    private static final String VOUCHER_TYPE_ID = "voucherTypeId"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String IS_POSTED = "isPosted"
    private static final String DEFAULT_SORT_NAME = "voucherDate, traceNo"

    private Logger log = Logger.getLogger(getClass())
    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.voucherTypeId || !params.fromDate || !params.toDate) {
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
            int voucherTypeId = Integer.parseInt(params.voucherTypeId.toString())
            SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(voucherTypeId)
            if (!accVoucherType) {
                result.put(Tools.MESSAGE, VOUCHER_TYPE_NOT_FOUND)
                return result
            }
            result.put(VOUCHER_TYPE_ID, voucherTypeId)
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
     * Get voucher details list
     * @param parameters -serialized parameters UI
     * @param obj -N/A
     * @return - a map containing voucher details list, voucher id and error msg
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            LinkedHashMap preResult = (LinkedHashMap) obj
            List<AccVoucherTypeModel> voucherList
            int count
            generatePagination(parameterMap)

            int voucherTypeId = Integer.parseInt(preResult.get(VOUCHER_TYPE_ID).toString())
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            if (parameterMap.isPosted.equals(Tools.EMPTY_SPACE)) {
                voucherList = AccVoucherTypeModel.listByVoucherTypeAndDateWithOrder(voucherTypeId, fromDate, toDate, accSessionUtil.appSessionUtil.getAppUser().companyId).list(offset: start, max: resultPerPage)
                count = AccVoucherTypeModel.listByVoucherTypeAndDate(voucherTypeId, fromDate, toDate, accSessionUtil.appSessionUtil.getAppUser().companyId).count()
            } else {
                boolean isPosted = Boolean.parseBoolean(parameterMap.isPosted.toString())
                voucherList = AccVoucherTypeModel.listByVoucherTypeAndDateAndPostedWithOrder(isPosted, voucherTypeId, fromDate, toDate, accSessionUtil.appSessionUtil.getAppUser().companyId).list(offset: start, max: resultPerPage)
                count = AccVoucherTypeModel.listByVoucherTypeAndDateAndPosted(isPosted, voucherTypeId, fromDate, toDate, accSessionUtil.appSessionUtil.getAppUser().companyId).count()
            }

            if (count <= 0) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND)
                return result
            }

            List voucherListWrap = wrapVoucherList(voucherList, start)
            result.put(VOUCHER_LIST, voucherListWrap)
            result.put(COUNT, count)
            // create the map to display label values
            result.put(FROM_DATE, fromDate)
            result.put(TO_DATE, toDate)
            result.put(VOUCHER_TYPE_ID, voucherTypeId)
            result.put(IS_POSTED, parameterMap.isPosted)
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
     * Wrap voucher details list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        Map gridOutput
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List voucherListWrap = (List) executeResult.get(VOUCHER_LIST)
            int count = (int) executeResult.get(COUNT)
            gridOutput = [page: pageNumber, total: count, rows: voucherListWrap]

            Date fromDate = (Date) executeResult.get(FROM_DATE)
            Date toDate = (Date) executeResult.get(TO_DATE)

            result.put(VOUCHER_LIST_WRAP, gridOutput)
            result.put(IS_POSTED, executeResult.get(IS_POSTED))
            result.put(VOUCHER_TYPE_ID, executeResult.get(VOUCHER_TYPE_ID))
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            gridOutput = [page: pageNumber, total: 0, rows: []]
            result.put(VOUCHER_LIST_WRAP, gridOutput)
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
        Map gridOutput = [page: pageNumber, total: 0, rows: []]
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            result.put(VOUCHER_LIST_WRAP, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            result.put(VOUCHER_LIST_WRAP, gridOutput)
            return result
        }
    }
    /**
     * Get voucher details list
     * @param voucherDetailsList - voucher details list
     * @param start - starting index
     * @return - a list of wrapped voucher details list
     */
    private List wrapVoucherList(List<AccVoucherTypeModel> voucherList, int start) {
        List vouchers = [] as List

        int counter = start + 1
        AccVoucherTypeModel voucher
        GridEntity obj
        for (int i = 0; i < voucherList.size(); i++) {
            voucher = voucherList[i]
            obj = new GridEntity()
            obj.id = voucher.voucherId
            obj.cell = [
                    counter,
                    voucher.strVoucherDate,
                    voucher.voucherId,
                    voucher.traceNo,
                    voucher.strAmount,
                    voucher.isVoucherPosted ? Tools.YES : Tools.NO
            ]
            vouchers << obj
            counter++
        }
        return vouchers

    }
    /**
     * For pagination only
     * @param params- default parameters
     */
    private void generatePagination(GrailsParameterMap params) {

        if (!params.page || !params.rp) {
            params.page = 1
            params.rp = 20
            params.currentCount = 0
            params.sortname = DEFAULT_SORT_NAME
            params.sortorder = this.sortOrder
        }
        initSearch(params)
    }
}