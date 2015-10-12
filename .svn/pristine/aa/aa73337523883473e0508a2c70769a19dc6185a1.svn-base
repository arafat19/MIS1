package com.athena.mis.accounting.actions.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete Iou Slip object from DB
 *  For details go through Use-Case doc named 'DeleteAccIouSlipActionService'
 */
class DeleteAccIouSlipActionService extends BaseService implements ActionIntf {

    AccIouSlipService accIouSlipService

    private static final String DELETE_ACC_IOU_SLIP_SUCCESS_MESSAGE = "IOU slip has been deleted successfully"
    private static final String DELETE_ACC_IOU_SLIP_FAILURE_MESSAGE = "IOU slip could not be deleted, please refresh the IOU slip list"
    private static final String APPROVED_ERROR = "Approved IOU slip could not be deleted"
    private static final String DEFAULT_ERROR_MESSAGE = "IOU slip could not be deleted"
    private static final String PURPOSE_ERROR = " purpose(s) has association with the IOU slip"
    private static final String NOT_FOUND_ERROR = "IOU slip not found"
    private static final String ACC_IOU_SLIP = "AccIouSlip"
    private static final String DELETED = "deleted"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Checking pre condition and association before deleting the accIouSlip object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)     // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long accIouSlipId = Long.parseLong(params.id.toString())

            AccIouSlip accIouSlip = accIouSlipService.read(accIouSlipId)  // get accIouSlip object
            // check whether the accIouSlip object exists or not
            if (!accIouSlip) {
                result.put(Tools.MESSAGE, NOT_FOUND_ERROR)
                return result
            }

            // check association of accIouSlip with Iou Purpose
            if (accIouSlip.purposeCount > 0) {
                result.put(Tools.MESSAGE, accIouSlip.purposeCount.toString() + PURPOSE_ERROR)
                return result
            }

            // check the selected iou slip is approved or not
            if (accIouSlip.approvedBy > 0) {
                result.put(Tools.MESSAGE, APPROVED_ERROR)
                return result
            }

            result.put(ACC_IOU_SLIP, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_IOU_SLIP_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete accIouSlip object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AccIouSlip accIouSlip = (AccIouSlip) receivedResult.get(ACC_IOU_SLIP)

            accIouSlipService.delete(accIouSlip.id)    // delete accIouSlip object from DB

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_IOU_SLIP_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_ACC_IOU_SLIP_SUCCESS_MESSAGE)
            result.put(DELETED, Boolean.TRUE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_IOU_SLIP_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_ACC_IOU_SLIP_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_ACC_IOU_SLIP_FAILURE_MESSAGE)
            return result
        }
    }
}
