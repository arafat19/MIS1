package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search pay bank voucher and show specific list of pay cash voucher
 *  For details go through Use-Case doc named 'SearchAccVoucherPayBankActionService'
 */
class SearchAccVoucherPayBankActionService extends BaseService implements ActionIntf {

    SystemEntityService systemEntityService
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String SERVER_ERROR_MESSAGE = "Fail to search voucher list"
    private static final String INVALID_AMOUNT = "Please enter digits in amount field"
    private static final String INVALID_INSTRUMENT_ID = "Please enter digits in instrument no"
    private static final String VOUCHER_LIST = "voucherList"
    private static final String COUNT = "count"
    private static final String SEARCH_BY_AMOUNT = "amount"
    private static final String SEARCH_BY_INSTRUMENT = "instrumentId"

    private Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        // do nothing for pre operation
        return null
    }
    /**
     * Get pay bank voucher list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)        // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            initSearch(parameterMap)          // initialize parameters for flexGrid

            def queryObject   // assign query object based on query type

            if (queryType == SEARCH_BY_AMOUNT) {
                try {
                    double amount = Double.parseDouble(query.toString())
                    queryObject = new Double(amount)
                } catch (Exception e) {
                    result.put(Tools.MESSAGE, INVALID_AMOUNT)
                    return result
                }
            } else if (queryType == SEARCH_BY_INSTRUMENT) {
                try {
                    long instrumentId = Long.parseLong(query.toString())
                    queryObject = new Long(instrumentId)
                } catch (Exception e) {
                    result.put(Tools.MESSAGE, INVALID_INSTRUMENT_ID)
                    return result
                }
            } else {
                queryObject = query
            }
            // pull voucher list of specific type
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity voucherTypeObj = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, companyId)
            List<AccVoucher> voucherList = AccVoucher.searchByVoucherType(voucherTypeObj.id, queryType, queryObject, companyId).list(offset: start, max: resultPerPage, sort: this.sortColumn, order: this.sortOrder, readOnly: true)
            // pull total number voucher of specific type
            int total = AccVoucher.searchByVoucherType(voucherTypeObj.id, queryType, queryObject, companyId).count()

            result.put(VOUCHER_LIST, voucherList)
            result.put(COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }
    /**
     * Wrap pay bank voucher  list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List<AccVoucher> voucherList = (List<AccVoucher>) receiveResult.get(VOUCHER_LIST)
            List voucherListWrap = wrapVoucherInGridEntityList(voucherList, start)
            result = [page: pageNumber, total: count, rows: voucherListWrap]
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Wrap list of pay bank voucher in grid entity
     * @param voucherList -list of pay bank voucher object(s)
     * @param start -starting index of the page
     * @return -list of wrapped pay bank voucher
     */
    private List wrapVoucherInGridEntityList(List<AccVoucher> voucherList, int start) {
        List vouchers = [] as List
        int counter = start + 1
        AccVoucher voucher
        GridEntity obj
        for (int i = 0; i < voucherList.size(); i++) {
            voucher = voucherList[i]
            obj = new GridEntity()
            obj.id = voucherList[i].id
            String instrument = null
            if (voucher.instrumentId > 0) {
                SystemEntity instrumentTypeName = systemEntityService.read(voucher.instrumentTypeId)
                instrument = instrumentTypeName.key + Tools.COLON + Tools.SINGLE_SPACE + voucher.instrumentId
            }
            obj.cell = [
                    counter,
                    voucher.id,
                    voucher.traceNo,
                    Tools.makeAmountWithThousandSeparator(voucher.amount),
                    voucher.drCount,
                    voucher.crCount,
                    DateUtility.getLongDateForUI(voucher.voucherDate),
                    voucher.isVoucherPosted ? Tools.YES : Tools.NO,
                    instrument,
                    voucher.chequeNo ? voucher.chequeNo : Tools.EMPTY_SPACE
            ]
            vouchers << obj
            counter++
        }
        return vouchers
    }
}
