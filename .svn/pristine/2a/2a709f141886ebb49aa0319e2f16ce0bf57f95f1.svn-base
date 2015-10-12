package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhBeneficiaryService
import com.athena.mis.exchangehouse.service.ExhCustomerService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.exchangehouse.utility.ExhPaymentMethodCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Approved customer task for for cashier
 * For details go through Use-Case doc named 'ExhApproveTaskForCashierActionService'
 */
class ExhApproveTaskForCashierActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String APPROVED_SUCCESS = "Task successfully approved"
    private static final String APPROVED_FAILURE = "Task can not be approved"
    private static final String TASK_NOT_FOUND_ERROR = "Task not found. Refresh grid and try again"
    private static final String TASK_ALREADY_APPROVED = "Task already approved"
    private static final String TASK_UNPAID = "Task is unpaid"
    private static final String BENEFICIARY_APPROVED_MSG = "Beneficiary is not approved"
    private static final String CUSTOMER_IS_BLOCKED = "Customer is blocked"
    private static final String TASK = 'task'
    private static final String SUCCESS = "success"
    private static final String IS_CONFIRMATION_ISSUE = "isConfirmationIssue"

    ExhTaskService exhTaskService
    ExhTaskTraceService exhTaskTraceService
    ExhBeneficiaryService exhBeneficiaryService
    ExhCustomerService exhCustomerService

    @Autowired
    ExhPaymentMethodCacheUtility exhPaymentMethodCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * Get parameters from UI and check pre condition
     * 1. pull task by id & check its exist or not
     * 2. check if gateway(payPoint) payment done
     * 3. check task status, if found as NEW then show message show already approved
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {

        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE) // set default value
            long taskId = Long.parseLong(params.id.toString())

            ExhTask task = exhTaskService.read(taskId)
            if (!task) {                               // check task exist or not
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_ERROR)
                return result
            }
            ExhCustomer exhCustomer=exhCustomerService.read(task.customerId)
            if(exhCustomer.isBlocked){              //check if customer is block or not, block customer's task can not be approved
                result.put(Tools.MESSAGE,CUSTOMER_IS_BLOCKED)
                return result
            }
            if (!task.isGatewayPaymentDone) {         // check payment-done in case of payPoint
                result.put(Tools.MESSAGE, TASK_UNPAID)
                return result
            }

            ExhBeneficiary beneficiary = exhBeneficiaryService.read(task.beneficiaryId)
            if (beneficiary.approvedBy == 0) {
                result.put(Tools.MESSAGE, BENEFICIARY_APPROVED_MSG)
                return result
            }

            SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
            if (task.currentStatus == exhNewTaskSysEntityObject.id) {         // check task status
                result.put(Tools.MESSAGE, TASK_ALREADY_APPROVED)
                return result
            }

            result.put(TASK, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
            return result
        }
    }

    /**
     * execute following activities
     * 1. Update task task status as NEW
     * 2. Save task trace into DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj        // cast map returned from previous method
            ExhTask task = (ExhTask) preResult.get(TASK)
            updateForSentToExhForCustomer(task)   // update customer's tasks' status from STATUS_UN_APPROVED to STATUS_NEW_TASK
            exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)     // save task trace

            result.put(TASK, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(APPROVED_FAILURE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
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
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            ExhTask task = (ExhTask) obj.task
            GridEntity object = new GridEntity()       // build grid entity object
            object.id = task.id
            String payMethod = exhPaymentMethodCacheUtility.read(task.paymentMethod).key
            Double amount_gbp = task.amountInLocalCurrency
            Double temp_total_due = amount_gbp + task.regularFee - task.discount
            double total_due = temp_total_due.round(2)
            String status = exhTaskStatusCacheUtility.read(task.currentStatus).key
            object.cell = [Tools.LABEL_NEW, task.id, task.refNo, task.currentStatus,
                    task.amountInForeignCurrency, amount_gbp, total_due,
                    task.customerName, task.beneficiaryName, payMethod, status]
            result = [isError: Boolean.FALSE, entity: object, version: task.version, message: APPROVED_SUCCESS];
            return result


        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
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
            if (obj) {
                if (receiveResult.get(IS_CONFIRMATION_ISSUE)) {
                    result.put(IS_CONFIRMATION_ISSUE, Boolean.TRUE)
                    result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
                    result.put(Tools.IS_ERROR, Boolean.FALSE)
                    return result
                }
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, APPROVED_FAILURE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, APPROVED_FAILURE)
            return result
        }
    }

    /**
     * Update task status as NEW for customer task
     * @param task
     * @return task by update count
     */
    private Integer updateForSentToExhForCustomer(ExhTask task) {
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        String query = """
                UPDATE exh_task SET
                version = version+1,
                approved_by = :approvedBy,
                approved_on = :approvedOn,
                current_status = :statusNew
                WHERE id = :taskId
            """
        Date approveOn = new Date()
        long status_new_task = exhNewTaskSysEntityObject.id
        long appUserId = exhSessionUtil.appSessionUtil.getAppUser().id
        Map queryParams = [taskId: task.id, statusNew: status_new_task,
                approvedBy: appUserId, approvedOn: task.approvedOn]

        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount < 1) {
            throw new RuntimeException("Failed to update Task")
        }
        task.version = task.version + 1
        task.approvedBy = exhSessionUtil.appSessionUtil.getAppUser().id
        task.approvedOn = approveOn
        task.currentStatus = status_new_task
        return (new Integer(updateCount))
    }
}
