package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.service.AccVoucherService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional
/**
 *  Un-post specific posted voucher
 *  For details go through Use-Case doc named 'UnPostVoucherActionService'
 */
class UnPostVoucherActionService extends BaseService implements ActionIntf {

    AccVoucherService accVoucherService

    private final Logger log = Logger.getLogger(getClass())
    private static final String DEFAULT_ERROR_MESSAGE = "Fail to un-post voucher"
    private static final String ALREADY_UNPOST_ERROR_MESSAGE = "Voucher is already un-posted"
    private static final String ACC_VOUCHER_UNPOST_SUCCESS_MSG = "Voucher has been successfully un-posted"
    private static final String VOUCHER_NOT_FOUND_MASSAGE = "Selected voucher is not found"
    private static final String NEW = "New"
    private static final String ACC_VOUCHER = "accVoucher"
    /**
     * Get parameters from UI and make a posted voucher un-post
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)       // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long accVoucherId = Long.parseLong(parameterMap.id.toString())
            AccVoucher accVoucher = accVoucherService.read(accVoucherId)       // build voucher object
            if (!accVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND_MASSAGE)
                return result
            }

            if (!accVoucher.isVoucherPosted) {
                result.put(Tools.MESSAGE, ALREADY_UNPOST_ERROR_MESSAGE)
                return result
            }
            accVoucher.postedBy = 0                // assign default value for postedBy
            accVoucher.isVoucherPosted = false

            result.put(ACC_VOUCHER, accVoucher)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Update voucher object (posted--> un-posted)
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccVoucher accVoucher = (AccVoucher) preResult.get(ACC_VOUCHER)
            AccVoucher updatedAccVoucher = updateVoucherForUnPost(accVoucher)    // posted voucher turned into un-posted
            result.put(ACC_VOUCHER, updatedAccVoucher)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }
    /**
     * Show newly updated voucher object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = new LinkedHashMap()
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            executeResult = (LinkedHashMap) obj
            AccVoucher voucher = (AccVoucher) executeResult.get(ACC_VOUCHER)
            GridEntity object = new GridEntity()
            object.id = voucher.id
            object.cell = [NEW, voucher.traceNo]
            Map resultMap = [entity: object, version: voucher.version]

            result.put(Tools.MESSAGE, ACC_VOUCHER_UNPOST_SUCCESS_MSG)
            result.put(ACC_VOUCHER, resultMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            log.error(ex.getMessage())
            return result
        }
    }
    private static final String QUERY_UPDATE = """
                    UPDATE acc_voucher SET
                          version =:newVersion,
                          is_voucher_posted= :isVoucherPosted,
                          posted_by =:postedBy
                      WHERE
                          id =:id AND
                          version= :version
                          """
    /**
     * For update voucher object
     * @param accVoucher - voucher object
     * @return -newly updated voucher object
     */
    private AccVoucher updateVoucherForUnPost(AccVoucher accVoucher) {
        Map queryParams = [
                newVersion: accVoucher.version + 1,
                isVoucherPosted: accVoucher.isVoucherPosted,
                postedBy: accVoucher.postedBy,
                id: accVoucher.id,
                version: accVoucher.version
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
        }
        accVoucher.version = accVoucher.version + 1
        return accVoucher
    }
}
