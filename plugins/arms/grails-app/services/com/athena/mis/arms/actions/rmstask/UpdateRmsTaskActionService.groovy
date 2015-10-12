package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsPaymentMethodCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Update Task object
 *  For details go through Use-Case doc named 'UpdateRmsTaskActionService'
 */
class UpdateRmsTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String OBJ_NOT_FOUND = "Selected task not found"
    private static final String TASK_OBJ = "task"
    private static final String TASK_UPDATE_FAILURE_MESSAGE = "Task could not be updated"
    private static final String TASK_UPDATE_SUCCESS_MESSAGE = "Task has been updated successfully"
    private static final String REF_NO_SHOULD_BE_UNIQUE = "Ref No should be unique"
    private static final String PIN_NO_SHOULD_BE_UNIQUE = "PIN No should be unique"
    private static final String EXH_CHANGED = "Exchange House cannot be changed"
    private static final String PAY_CHANGED = "Payment Method cannot be changed"
    private static final String AMOUNT_CHANGED = "Amount cannot be changed of this task"
    private static final String DISBURSED_TASK_CAN_NOT_BE_UPDATED = "Disbursed task can not be updated"
    private static final String CANCELED_TASK_CAN_NOT_BE_UPDATED = "Canceled task can not be updated"

    /**
     * Get parameters from UI and build Task object for update
     * 1. Check validity for input
     * 2. Check existence of Task object
     * 3. Build Task object with new parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskId = Long.parseLong(parameterMap.id)
            RmsTask oldTask = rmsTaskService.read(taskId)     // Get Task object from DB
            if (!oldTask) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            Boolean isExhUser = Boolean.parseBoolean(parameterMap.isExhUser.toString())
            if (isExhUser.booleanValue()) {
                long exhId = rmsSessionUtil.getUserExchangeHouseId()
                if (oldTask.exchangeHouseId != exhId) {
                    result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                    return result
                }
            }
            RmsTask existingTask = new RmsTask()
            existingTask.properties = oldTask.properties        // save existing task for some preCheck
            RmsTask task = buildTaskObject(parameterMap, oldTask)
            String refErrMsg = checkDuplicateRefNo(task)
            if (refErrMsg) {
                result.put(Tools.MESSAGE, refErrMsg)
                return result
            }
            String pinErrMsg = checkDuplicatePinNo(parameterMap, task)
            if (pinErrMsg) {
                result.put(Tools.MESSAGE, pinErrMsg)
                return result
            }

            String errorMsg = checkTaskModification(existingTask, task)
            if (errorMsg) {
                result.put(Tools.MESSAGE, errorMsg)
                return result
            }
            result.put(TASK_OBJ, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Update Task object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            RmsTask task = (RmsTask) preResult.get(TASK_OBJ)
            rmsTaskService.update(task)                      // update new Task object in DB
            result.put(TASK_OBJ, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build grid object to show a single row (updated object) in grid
     * 1. Get ExchangeHouse object by id
     * 2. Get SystemEntity object by id for paymentMethod
     * 3. build success message
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj               // cast map returned from execute method
            RmsTask task = (RmsTask) executeResult.get(TASK_OBJ)
            long exchangeHouseId = task.exchangeHouseId
            // Get ExchangeHouse object by id
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)
            long payMethodId = task.paymentMethod
            SystemEntity paymentMethod = (SystemEntity) rmsPaymentMethodCacheUtility.read(payMethodId)
            GridEntity object = new GridEntity()        // build grid object
            object.id = task.id
            object.cell = [
                    Tools.LABEL_NEW,
                    task.id,
                    task.refNo,
                    task.amount,
                    DateUtility.getLongDateForUI(task.valueDate),
                    task.beneficiaryName,
                    task.getFullOutletName(),
                    paymentMethod.key,
                    DateUtility.getLongDateForUI(task.createdOn),
                    exchangeHouse.name
            ]
            result.put(Tools.MESSAGE, TASK_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, task.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Make existing/old object Up-to-date with new parameters
     * @param parameterMap -serialized parameters from UI
     * @param oldTask -old Task object
     * @return -updated Task object
     */
    private RmsTask buildTaskObject(GrailsParameterMap parameterMap, RmsTask oldTask) {
        RmsTask newTask = new RmsTask(parameterMap)
        oldTask.refNo = newTask.refNo
        oldTask.amount = newTask.amount
        oldTask.valueDate = DateUtility.parseMaskedDate(parameterMap.valueDate)
        oldTask.beneficiaryName = newTask.beneficiaryName
        oldTask.beneficiaryAddress = newTask.beneficiaryAddress
        oldTask.beneficiaryPhone = newTask.beneficiaryPhone
        oldTask.accountNo = newTask.accountNo
        oldTask.outletBank = newTask.outletBank
        oldTask.outletBranch = newTask.outletBranch
        oldTask.outletDistrict = newTask.outletDistrict
        oldTask.pinNo = newTask.pinNo
        oldTask.identityType = newTask.identityType
        oldTask.identityNo = newTask.identityNo
        oldTask.senderName = newTask.senderName
        oldTask.senderMobile = newTask.senderMobile
        oldTask.amountInLocalCurrency = newTask.amountInLocalCurrency
        oldTask.localCurrencyId = newTask.localCurrencyId
        oldTask.paymentMethod = newTask.paymentMethod
        Boolean isExhUser = Boolean.parseBoolean(parameterMap.isExhUser.toString())
        if (!isExhUser.booleanValue()) {
            oldTask.exchangeHouseId = newTask.exchangeHouseId
        }
        oldTask.countryId = newTask.countryId

        return oldTask
    }

    private String checkTaskModification(RmsTask oldTask, RmsTask newTask) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity includedInList = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.INCLUDED_IN_LIST, companyId)
        SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_TAKEN, companyId)
        SystemEntity decisionApp = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
        SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.DISBURSED, companyId)
        SystemEntity canceled = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.CANCELED, companyId)
        long currentStatus = oldTask.currentStatus
        if ((currentStatus == disbursed.id)) { //disburse task can not be edited
            return DISBURSED_TASK_CAN_NOT_BE_UPDATED
        }
        if ((currentStatus == canceled.id)) { //disburse task can not be edited
            return CANCELED_TASK_CAN_NOT_BE_UPDATED
        }
        if (currentStatus == includedInList.id) {
            if (oldTask.exchangeHouseId != newTask.exchangeHouseId) {
                return EXH_CHANGED
            }
        }
        if ((currentStatus == decisionTaken.id) || (currentStatus == decisionApp.id)) {
            if (oldTask.paymentMethod != newTask.paymentMethod) {
                return PAY_CHANGED
            }
        }
        if ((currentStatus == decisionApp.id)) {
            if (oldTask.amount != newTask.amount) {
                return AMOUNT_CHANGED
            }
        }
        return null
    }

    private String checkDuplicateRefNo(RmsTask task) {
        int countRef = rmsTaskService.countByRefNoForUpdate(task)
        if (countRef > 0) {
            return REF_NO_SHOULD_BE_UNIQUE
        }
        return null
    }

    private String checkDuplicatePinNo(GrailsParameterMap parameterMap, RmsTask task) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        long paymentMethod = Long.parseLong(parameterMap.paymentMethod)
        SystemEntity cashCollectionPaymentMethod = (SystemEntity) rmsPaymentMethodCacheUtility.readByReservedAndCompany(rmsPaymentMethodCacheUtility.CASH_COLLECTION_ID, companyId)
        if (paymentMethod != cashCollectionPaymentMethod.id) {
            return null
        }
        int countPin = rmsTaskService.countByPinNoAndExchangeHouseIdAndIdNotEqual(task)
        if (countPin > 0) {
            return PIN_NO_SHOULD_BE_UNIQUE
        }
        return null
    }
}
