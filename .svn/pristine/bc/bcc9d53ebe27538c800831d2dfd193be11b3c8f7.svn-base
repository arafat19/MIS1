package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.service.AccVoucherService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Voucher post and show in the grid with its result
 *  For details go through Use-Case doc named 'PostAccVoucherActionService'
 */
class PostAccVoucherActionService extends BaseService implements ActionIntf {

    private static final String DEFAULT_ERROR_MESSAGE = "Fail to post voucher"
    private static final String ALREADY_POST_ERROR_MESSAGE = "Voucher already posted"
    private static final String VOUCHER_POST_SUCCESS_MSG = "Voucher has been successfully posted"
    private static final String VOUCHER_NOT_FOUND_MASSAGE = "Selected voucher is not found"
    private static final String NEW = "New"
    private static final String ACC_VOUCHER = "accVoucher"
    private Logger log = Logger.getLogger(getClass())

    AccVoucherService accVoucherService
    @Autowired
    AccSessionUtil accSessionUtil
    /**
     * Get parameters from UI and make a voucher posted
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)           // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long accVoucherId = Long.parseLong(parameterMap.id.toString())
            AccVoucher accVoucher = accVoucherService.read(accVoucherId)         // get voucher object
            if (!accVoucher) {
                result.put(Tools.MESSAGE, VOUCHER_NOT_FOUND_MASSAGE)
                return result
            }

            if (accVoucher.isVoucherPosted) {        // check voucher whether it is already posted or not
                result.put(Tools.MESSAGE, ALREADY_POST_ERROR_MESSAGE)
                return result
            }
            accVoucher.postedBy = accSessionUtil.appSessionUtil.getAppUser().id        // posted by login user
            accVoucher.isVoucherPosted = true

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
     * update voucher object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
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
            AccVoucher updatedAccVoucher = updateVoucherForPost(accVoucher)      // update voucher object
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

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show status(posted- YES) in the grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned form execute method
            AccVoucher voucher = (AccVoucher) executeResult.get(ACC_VOUCHER)
            GridEntity object = new GridEntity()        // build grid object
            object.id = voucher.id
            object.cell = [NEW, voucher.traceNo]
            Map resultMap = [entity: object, version: voucher.version]

            result.put(Tools.MESSAGE, VOUCHER_POST_SUCCESS_MSG)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj     // cast map returned form previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private static final String UPDATE_QUERY = """
                    UPDATE acc_voucher SET
                          version =:newVersion,
                          is_voucher_posted= :isVoucherPosted,
                          posted_by =:postedBy
                      WHERE
                          id =:id AND
                          version= :version
                          """
    /**
     * Update voucher object
     * @param accVoucher -object of voucher
     * @return -updated voucher object
     */
    private AccVoucher updateVoucherForPost(AccVoucher accVoucher) {

        Map queryParams = [
                newVersion: accVoucher.version + 1,
                isVoucherPosted: accVoucher.isVoucherPosted,
                postedBy: accVoucher.postedBy,
                id: accVoucher.id,
                version: accVoucher.version
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
        }
        accVoucher.version = accVoucher.version + 1
        return accVoucher
    }
}