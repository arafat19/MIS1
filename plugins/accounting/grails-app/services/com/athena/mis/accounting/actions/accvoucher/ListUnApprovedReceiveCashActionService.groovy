package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of un-approved receive cash for grid
 *  For details go through Use-Case doc named 'ListUnApprovedReceiveCashActionService'
 */
class ListUnApprovedReceiveCashActionService extends BaseService implements ActionIntf {

    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String SERVER_ERROR_MESSAGE = "Fail to get un-posted receive cash list"
    private static final String VOUCHER_LIST = "voucherList"
    private static final String COUNT = "count"
    private static final String GRID_OBJ = "gridObj"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of un-approved receive cash for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.rp) {
                parameterMap.rp = 10
                parameterMap.page = 1
            }
            initPager(parameterMap) // initialize parameters for flexGrid
            long companyId = accSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity voucherTypeObj = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.RECEIVED_VOUCHER_CASH_ID, companyId)

            // Get un-approved receive cash using dynamic finder
            List<AccVoucher> voucherList = AccVoucher.findAllByVoucherTypeIdAndIsVoucherPostedAndCompanyId(voucherTypeObj.id, false, companyId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
            int total = (int) AccVoucher.countByVoucherTypeIdAndIsVoucherPostedAndCompanyId(voucherTypeObj.id, false, companyId)

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
     * do nothing for pre operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap un-approved receive cash for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from execute method
            int count = (int) receiveResult.get(COUNT)
            List<AccVoucher> voucherList = (List<AccVoucher>) receiveResult.get(VOUCHER_LIST)
            List voucherListWrap = wrapVoucherInGridEntityList(voucherList, start)
            Map gridObj = [page: pageNumber, total: count, rows: voucherListWrap]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj   // cast map returned from previous method
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
     * Wrap list of un-approved receive cash in grid entity
     * @param voucherList -list of un-approved receive cash object(s)
     * @param start -starting index of the page
     * @return -list of wrapped un-approved receive cash
     */
    private List wrapVoucherInGridEntityList(List<AccVoucher> voucherList, int start) {
        List vouchers = [] as List
        int counter = start + 1
        AccVoucher voucher
        GridEntity obj
        for (int i = 0; i < voucherList.size(); i++) {
            voucher = voucherList[i]
            obj = new GridEntity()
            AppUser appUser = (AppUser) appUserCacheUtility.read(voucher.createdBy)
            obj.id = voucherList[i].id
            obj.cell = [
                    counter,
                    voucher.traceNo,
                    Tools.makeAmountWithThousandSeparator(voucher.amount),
                    DateUtility.getLongDateForUI(voucher.voucherDate),
                    appUser.username
            ]
            vouchers << obj
            counter++
        }
        return vouchers
    }
}