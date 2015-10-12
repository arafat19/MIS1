package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.service.AccVoucherService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Delete Voucher (accVoucher) object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAccVoucherActionService'
 */
class DeleteAccVoucherActionService extends BaseService implements ActionIntf {

    AccVoucherService accVoucherService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DELETE_VOUCHER_SUCCESS_MESSAGE = "Voucher has been deleted successfully"
    private static final String DELETE_VOUCHER_FAILURE_MESSAGE = "Voucher could not be deleted, please refresh the page"
    private static final String POSTED_VOUCHER = "Posted voucher could not be deleted"
    private static final String PRIVILEGE_ERROR = "You are not privileged to delete the voucher"
    private static final String VOUCHER_OBJ = "invSiteTransaction"
    private static final String DELETED = "deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not delete voucher"
    private static final String VOUCHER_DETAILS_DELETE_ERROR_MSG = "deleted"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Checking pre condition and association before deleting the account Voucher (accVoucher) object
     * @param parameters - parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)  // default value

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long voucherId = Long.parseLong(params.id.toString())

            AccVoucher accVoucher = accVoucherService.read(voucherId)
            // check whether selected accVoucher object exists or not
            if (!accVoucher) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            // check whether selected accVoucher object is posted or not
            if (accVoucher.isVoucherPosted) {
                result.put(Tools.MESSAGE, POSTED_VOUCHER)
                return result
            }
            // Access check for delete, only CFO role has authority to delete account voucher (accVoucher)
            boolean hasPrivilege = accSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_CFO)
            if (!hasPrivilege) {
                result.put(Tools.MESSAGE, PRIVILEGE_ERROR)
                return result
            }

            result.put(VOUCHER_OBJ, accVoucher)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VOUCHER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete accVoucher object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            AccVoucher accVoucher = (AccVoucher) preResult.get(VOUCHER_OBJ)
            accVoucherService.delete(accVoucher) // delete accVoucher object from DB

            // now delete corresponding children of accVoucher from acc_voucher_details domain
            deleteDetailsForVoucherDelete(accVoucher.id)

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VOUCHER_FAILURE_MESSAGE)
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
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VOUCHER_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VOUCHER_FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_VOUCHER_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_VOUCHER_FAILURE_MESSAGE)
            return result
        }
    }


    private static final String QUERY_DELETE = """
            DELETE FROM acc_voucher_details
            WHERE voucher_id = :voucherId
        """
    /**
     * Delete all voucher details for a given voucherId
     * @param voucherId -id of accVoucher object
     * @return -it returns nothing
     */
    private void deleteDetailsForVoucherDelete(long voucherId) {
        Map queryParams = [voucherId: voucherId]
        int deleteCount = executeUpdateSql(QUERY_DELETE, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException(VOUCHER_DETAILS_DELETE_ERROR_MSG)
        }
    }
}
