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
 *  Show list of acc voucher pay bank(cheque) for grid
 *  For details go through Use-Case doc named 'ListAccVoucherPayBankActionService'
 */
class ListAccVoucherPayBankActionService extends BaseService implements ActionIntf {

    SystemEntityService systemEntityService
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String SERVER_ERROR_MESSAGE = "Fail to get voucher list"
    private static final String VOUCHER_LIST = "voucherList"
    private static final String COUNT = "count"

    private Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get acc voucher list of pay bank(cheque) for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap) // initialize parameters for flexGrid

            // Get voucher list of pay bank(cheque) by using dynamic finder
            SystemEntity voucherType = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, accSessionUtil.appSessionUtil.getCompanyId())
            List<AccVoucher> voucherList = AccVoucher.findAllByVoucherTypeIdAndCompanyId(voucherType.id, accSessionUtil.appSessionUtil.getCompanyId(), [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int total = (int) AccVoucher.countByVoucherTypeIdAndCompanyId(voucherType.id, accSessionUtil.appSessionUtil.getCompanyId())

            result.put(VOUCHER_LIST, voucherList)
            result.put(COUNT, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
     * Wrap acc voucher list of pay bank(cheque)for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj  // cast map returned from execute method
            int count = (int) receiveResult.get(COUNT)
            List<AccVoucher> voucherList = (List<AccVoucher>) receiveResult.get(VOUCHER_LIST)
            List voucherListWrap = wrapVoucherInGridEntityList(voucherList, start)
            result = [page: pageNumber, total: count, rows: voucherListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of acc voucher for pay bank(cheque) in grid entity
     * @param voucherList -list of acc voucher for pay bank(cheque) object(s)
     * @param start -starting index of the page
     * @return -list of wrapped accVouchers pay bank
     */
    private List wrapVoucherInGridEntityList(List<AccVoucher> voucherList, int start) {
        List vouchers = []
        int counter = start + 1
        AccVoucher voucher
        GridEntity obj
        for (int i = 0; i < voucherList.size(); i++) {
            voucher = voucherList[i]
            obj = new GridEntity()
            obj.id = voucher.id
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
                    voucher.chequeNo
            ]
            vouchers << obj
            counter++
        }
        return vouchers
    }
}
