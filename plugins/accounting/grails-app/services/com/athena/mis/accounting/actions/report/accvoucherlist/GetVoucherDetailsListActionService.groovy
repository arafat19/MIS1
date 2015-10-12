package com.athena.mis.accounting.actions.report.accvoucherlist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Get voucher details(pay/receive- cash/bank against specific voucher)
 * after selecting individual voucher
 * For details go through Use-Case doc named 'GetVoucherDetailsListActionService'
 */
class GetVoucherDetailsListActionService extends BaseService implements ActionIntf {

    private static final String VOUCHER_NOT_FOUND = "Voucher not found"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate voucher details list"
    private static final String VOUCHER_DETAILS_LIST = "voucherDetailsList"
    private static final String VOUCHER_DETAILS_LIST_WRAP = "voucherDetailsListWrap"
    private static final String COUNT = "count"
    private static final String VOUCHER_ID = "voucherId"
    private static final String DEFAULT_SORT_NAME = "voucherDate, traceNo"

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    AccSessionUtil accSessionUtil
    /**
     * Check input fields from UI
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.voucherId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            long voucherId = Long.parseLong(params.voucherId.toString())
            AccVoucher accVoucher = AccVoucher.findByIdAndCompanyId(voucherId, accSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])
            if (!accVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND)
                return result
            }
            result.put(VOUCHER_ID, voucherId)
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

            generatePagination(parameterMap)

            long voucherId = (long) preResult.get(VOUCHER_ID)
            List<AccVoucherModel> voucherDetailsList = AccVoucherModel.listByVoucherIdWithOrder(voucherId, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
            int count = (int) AccVoucherModel.listByVoucherId(voucherId, accSessionUtil.appSessionUtil.getAppUser().companyId).count()

            if (count <= 0) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND)
                return result
            }

            List voucherDetailsListWrap = wrapVoucherDetailsList(voucherDetailsList, start)
            result.put(VOUCHER_DETAILS_LIST, voucherDetailsListWrap)
            result.put(COUNT, count)
            // create the map to display label values
            result.put(VOUCHER_ID, voucherId)
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
            List voucherListWrap = (List) executeResult.get(VOUCHER_DETAILS_LIST)
            int count = (int) executeResult.get(COUNT)
            gridOutput = [page: pageNumber, total: count, rows: voucherListWrap]


            result.put(VOUCHER_DETAILS_LIST_WRAP, gridOutput)
            result.put(VOUCHER_ID, executeResult.get(VOUCHER_ID))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            gridOutput = [page: pageNumber, total: 0, rows: []]
            result.put(VOUCHER_DETAILS_LIST_WRAP, gridOutput)
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
            result.put(VOUCHER_DETAILS_LIST_WRAP, gridOutput)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            result.put(VOUCHER_DETAILS_LIST_WRAP, gridOutput)
            return result
        }
    }
    /**
     * Get voucher details list
     * @param voucherDetailsList - voucher details list
     * @param start - starting index
     * @return - a list of wrapped voucher details list
     */
    private List wrapVoucherDetailsList(List<AccVoucherModel> voucherDetailsList, int start) {
        List vouchers = [] as List
        int counter = start + 1
        AccVoucherModel voucherDetails
        GridEntity obj
        for (int i = 0; i < voucherDetailsList.size(); i++) {
            voucherDetails = voucherDetailsList[i]
            obj = new GridEntity()
            obj.id = voucherDetailsList[i].id
            obj.cell = [
                    counter,
                    voucherDetails.coaCode,
                    voucherDetails.coaDescription,
                    voucherDetails.strAmountDr,
                    voucherDetails.strAmountCr,
                    voucherDetails.particulars
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