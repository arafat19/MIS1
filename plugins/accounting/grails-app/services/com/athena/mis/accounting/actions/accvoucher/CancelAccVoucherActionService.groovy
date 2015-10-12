package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccCancelledVoucher
import com.athena.mis.accounting.entity.AccCancelledVoucherDetails
import com.athena.mis.accounting.entity.AccVoucher
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.service.AccCancelledVoucherDetailsService
import com.athena.mis.accounting.service.AccCancelledVoucherService
import com.athena.mis.accounting.service.AccVoucherService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CancelAccVoucherActionService extends BaseService implements ActionIntf {

    AccVoucherService accVoucherService
    AccCancelledVoucherService accCancelledVoucherService
    AccCancelledVoucherDetailsService accCancelledVoucherDetailsService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DELETE_VOUCHER_SUCCESS_MESSAGE = "Voucher has been moved successfully"
    private static final String DELETE_VOUCHER_FAILURE_MESSAGE = "Voucher could not be moved, please refresh the page"
    private static final String POSTED_VOUCHER = "Posted voucher could not be moved to Trash"
    private static final String PRIVILEGE_ERROR = "You are not privileged to moved to Trash the voucher"
    private static final String VOUCHER_OBJ = "invSiteTransaction"
    private static final String DELETED = "moved"
    private static final String DEFAULT_ERROR_MESSAGE = "Could not moved voucher"
    private static final String VOUCHER_DETAILS_DELETE_ERROR_MSG = "moved"

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
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj

            GrailsParameterMap params = (GrailsParameterMap) parameters
            AccVoucher accVoucher = (AccVoucher) preResult.get(VOUCHER_OBJ)
            String cancelReason = params.reason
            AccCancelledVoucher accCancelledVoucher = buildAccCancelledVoucherObject(accVoucher, cancelReason)
            accCancelledVoucherService.create(accCancelledVoucher)

            List<AccVoucherDetails> lstVoucherDetails = AccVoucherDetails.findAllByVoucherId(accVoucher.id, [readOnly: true])
            if (lstVoucherDetails.size() > 0) {
                for (int i = 0; i < lstVoucherDetails.size(); i++) {
                    AccVoucherDetails purchaseOrderDetails = lstVoucherDetails[i]
                    copyVoucherDetails(purchaseOrderDetails)
                }
            }
            // now delete corresponding children of accVoucher from acc_voucher_details domain
            deleteDetailsForVoucherDelete(accVoucher.id)

            accVoucherService.delete(accVoucher.id) // delete accVoucher object from DB

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

    private AccCancelledVoucher buildAccCancelledVoucherObject(AccVoucher accVoucher, String reason) {

        AccCancelledVoucher cancelledVoucher = new AccCancelledVoucher()
            cancelledVoucher.id = accVoucher.id
            cancelledVoucher.voucherTypeId = accVoucher.voucherTypeId
            cancelledVoucher.financialYear = accVoucher.financialYear
            cancelledVoucher.financialMonth = accVoucher.financialMonth
            cancelledVoucher.isVoucherPosted = accVoucher.isVoucherPosted
            cancelledVoucher.note = accVoucher.note
            cancelledVoucher.traceNo = accVoucher.traceNo
            cancelledVoucher.voucherDate = accVoucher.voucherDate
            cancelledVoucher.amount = accVoucher.amount
            cancelledVoucher.drCount = accVoucher.drCount
            cancelledVoucher.crCount = accVoucher.crCount
            cancelledVoucher.chequeNo = accVoucher.chequeNo
            cancelledVoucher.chequeDate = accVoucher.chequeDate
            cancelledVoucher.postedBy = accVoucher.postedBy
            cancelledVoucher.instrumentTypeId = accVoucher.instrumentTypeId
            cancelledVoucher.instrumentId = accVoucher.instrumentId

            cancelledVoucher.createdOn = accVoucher.createdOn
            cancelledVoucher.createdBy = accVoucher.createdBy
            cancelledVoucher.updatedOn = accVoucher.updatedOn ? accVoucher.updatedOn : null
            cancelledVoucher.updatedBy = accVoucher.updatedBy

            cancelledVoucher.projectId = accVoucher.projectId
            cancelledVoucher.companyId = accVoucher.companyId

            cancelledVoucher.cancelledBy = accSessionUtil.appSessionUtil.getAppUser().id
            cancelledVoucher.cancelledOn = new Date()
            cancelledVoucher.cancelReason = reason
            cancelledVoucher.voucherCount = accVoucher.voucherCount
        return cancelledVoucher
    }

    private void copyVoucherDetails(AccVoucherDetails accVoucherDetails) {
        AccCancelledVoucherDetails cancelledPODetails = buildCancelledVoucherDetailsObject(accVoucherDetails)
        accCancelledVoucherDetailsService.create(cancelledPODetails)
    }

    private AccCancelledVoucherDetails buildCancelledVoucherDetailsObject(AccVoucherDetails voucherDetails) {

        AccCancelledVoucherDetails cancelledVoucherDetails = new AccCancelledVoucherDetails()

            cancelledVoucherDetails.id = voucherDetails.id
            cancelledVoucherDetails.voucherId = voucherDetails.voucherId
            cancelledVoucherDetails.projectId = voucherDetails.projectId
            cancelledVoucherDetails.rowId = voucherDetails.rowId
            cancelledVoucherDetails.coaId = voucherDetails.coaId
            cancelledVoucherDetails.groupId = voucherDetails.groupId
            cancelledVoucherDetails.sourceId = voucherDetails.sourceId
            cancelledVoucherDetails.sourceTypeId = voucherDetails.sourceTypeId
            cancelledVoucherDetails.sourceCategoryId = voucherDetails.sourceCategoryId
            cancelledVoucherDetails.divisionId = voucherDetails.divisionId
            cancelledVoucherDetails.createdOn = voucherDetails.createdOn
            cancelledVoucherDetails.createdBy = voucherDetails.createdBy
            cancelledVoucherDetails.amountDr = voucherDetails.amountDr
            cancelledVoucherDetails.amountCr = voucherDetails.amountCr
            cancelledVoucherDetails.particulars = voucherDetails.particulars

        return cancelledVoucherDetails
    }
}
