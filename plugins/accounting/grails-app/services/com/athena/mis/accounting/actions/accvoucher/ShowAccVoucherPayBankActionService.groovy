package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.utility.AccInstrumentTypeCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.accounting.utility.AccVoucherTypeCoaCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show pay bank(cheque) voucher in the grid
 *  For details go through Use-Case doc named 'ShowAccVoucherPayBankActionService'
 */
class ShowAccVoucherPayBankActionService extends BaseService implements ActionIntf {

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load voucher page"
    private static final String LST_VOUCHERS = "voucherList"
    private static final String GRID_OBJ_VOUCHER = "gridObjVoucher"
    private static final String SERVICE_RETURN = "serviceReturn"
    private static final String PAYMENT_VOUCHER_BANK_OBJ = "paymentVoucherBank"
    private static final String LST_DEBIT_COA_VOUCHER_TYPE = "lstCreditCoaVoucherType"
    private static final String CUSTOM_NAME = "customName"

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get pay bank voucher list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)             // initialize parameters for flexGrid
            // pull system entity object
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity voucherType = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, companyId)

            List<AccVoucher> voucherList = []
            int count = 0
            if (parameterMap.traceNo) {               // retrieve voucher list if there is any trace number
                String traceNo = parameterMap.traceNo.toString()
                voucherList = AccVoucher.findAllByVoucherTypeIdAndTraceNoAndCompanyId(voucherType.id, traceNo, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = (int) AccVoucher.countByVoucherTypeIdAndTraceNoAndCompanyId(voucherType.id, traceNo, companyId)
            } else {                // retrieve voucher list without trace number
                voucherList = AccVoucher.findAllByVoucherTypeIdAndCompanyId(voucherType.id, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                count = (int) AccVoucher.countByVoucherTypeIdAndCompanyId(voucherType.id, companyId)
            }
            Map serviceReturn = [voucherList: voucherList, count: count]

            result.put(SERVICE_RETURN, serviceReturn)
            result.put(PAYMENT_VOUCHER_BANK_OBJ, voucherType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap pay bank(cheque) voucher list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj           // cast received object form execute method
            Map serviceReturn = (Map) executeResult.get(SERVICE_RETURN)
            List<AccVoucher> lstVoucher = (List<AccVoucher>) serviceReturn.get(LST_VOUCHERS)
            int count = (int) serviceReturn.get(Tools.COUNT)
            List gridListVoucher = wrapVoucher(lstVoucher, start)
            Map gridObjVoucher = [page: pageNumber, total: count, rows: gridListVoucher]
            SystemEntity accVoucherTypePayBank = (SystemEntity) executeResult.get(PAYMENT_VOUCHER_BANK_OBJ)
            result.put(GRID_OBJ_VOUCHER, gridObjVoucher)
            List lstCoaVoucherType = accVoucherTypeCoaCacheUtility.listCoa(accVoucherTypePayBank)
            lstCoaVoucherType = Tools.listForKendoDropdown(lstCoaVoucherType, CUSTOM_NAME, null)
            result.put(LST_DEBIT_COA_VOUCHER_TYPE, lstCoaVoucherType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of pay bank(cheque) voucher in grid entity
     * @param voucherList -list of pay bank(cheque) voucher object(s)
     * @param start -starting index of the page
     * @return -list of wrapped pay bank(cheque) voucher
     */
    private List wrapVoucher(List<AccVoucher> voucherList, int start) {
        List vouchers = [] as List
        int counter = start + 1
        AccVoucher voucher
        GridEntity obj
        for (int i = 0; i < voucherList.size(); i++) {
            voucher = voucherList[i]
            obj = new GridEntity()
            obj.id = voucher.id
            String instrument = null
            if (voucher.instrumentId > 0) {
                SystemEntity instrumentTypeName = (SystemEntity) accInstrumentTypeCacheUtility.read(voucher.instrumentTypeId)
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
