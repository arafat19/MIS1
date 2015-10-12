package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
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
 *  Create new Task object
 *  For details go through Use-Case doc named 'CreateRmsTaskActionService'
 */
class CreateRmsTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskTraceService rmsTaskTraceService
    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsPaymentMethodCacheUtility rmsPaymentMethodCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil1

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_OBJ = "task"
    private static final String TASK_CREATE_FAILURE_MSG = "Task has not been saved"
    private static final String TASK_CREATE_SUCCESS_MSG = "Task has been successfully saved"
    private static final String REF_NO_SHOULD_BE_UNIQUE = "Ref No should be unique"
    private static final String PIN_NO_SHOULD_BE_UNIQUE = "PIN number should be unique"

    /**
     * Get parameters from UI and build Task object
     * 1. Build Task object
     * 2.
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            RmsTask task = buildTaskObject(parameterMap)
            String errMsg = checkDuplicateRefNo(task)
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            errMsg = checkDuplicatePinNo(parameterMap, task)
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            result.put(TASK_OBJ, task)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_CREATE_FAILURE_MSG)
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
     * Save Task object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            RmsTask task = (RmsTask) preResult.get(TASK_OBJ)
            RmsTask savedRmsTaskObj = rmsTaskService.create(task)    // save new Task object in DB
            rmsTaskTraceService.create(task)                         // save task trace obj
            result.put(TASK_OBJ, savedRmsTaskObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. Get ExchangeHouse object by id
     * 2. Get SystemEntity object by id for paymentMethod
     * 3. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj       // cast map returned from execute method
            RmsTask task = (RmsTask) executeResult.get(TASK_OBJ)
            long exchangeHouseId = task.exchangeHouseId
            // Get ExchangeHouse object by id
            RmsExchangeHouse exchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)
            long payMethodId = task.paymentMethod
            SystemEntity paymentMethod = (SystemEntity) rmsPaymentMethodCacheUtility.read(payMethodId)
            GridEntity object = new GridEntity()                // build grid object
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
            result.put(Tools.MESSAGE, TASK_CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_CREATE_FAILURE_MSG)
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
                LinkedHashMap preResult = (LinkedHashMap) obj               // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, TASK_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new Task object
     * @param parameterMap -serialized parameters from UI
     * @return -new Task object
     */
    private RmsTask buildTaskObject(GrailsParameterMap parameterMap) {
        boolean isExhUser = parameterMap.isExhUser ? true : false
        if (isExhUser) {
            parameterMap.exchangeHouseId = rmsSessionUtil.getUserExchangeHouseId()
        }
        long exchangeHouseId = Long.parseLong(parameterMap.exchangeHouseId.toString())
        RmsExchangeHouse rmsExchangeHouse = (RmsExchangeHouse) rmsExchangeHouseCacheUtility.read(exchangeHouseId)


        RmsTask task = new RmsTask(parameterMap)
        long taskStatus = rmsTaskStatusCacheUtility.NEW_TASK    //default value
        if (isExhUser) {
            taskStatus = rmsTaskStatusCacheUtility.PENDING_TASK
            task.exchangeHouseId = rmsExchangeHouse.id
            task.countryId = rmsExchangeHouse.countryId
        }
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity currentStatus = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(taskStatus, companyId)
        task.currentStatus = currentStatus.id
        task.refNo = setTaskRefNoPrefix(rmsExchangeHouse, parameterMap.refNo)
        task.valueDate = DateUtility.parseMaskedDate(parameterMap.valueDate)
        task.isRevised = false
        task.previousStatus = 0L
        task.createdOn = new Date()
        task.companyId = companyId
        task.currencyId = 0L
        task.mappingBankId = 0L
        task.mappingBranchId = 0L
        task.mappingDistrictId = 0L
        task.processTypeId = 0L
        task.instrumentTypeId = 0L
        task.mappedOn = null
        task.approvedOn = null
        task.taskListId = 0L
        task.revisionNote = null
        task.commissionDetailsId = 0L
        return task
    }

    // Transaction Ref no Prefix will be added only if it is numeric
    public static final String REF_NO_PATTERN_FOR_PREFIX = "^\\d*\$"

    public static String setTaskRefNoPrefix(RmsExchangeHouse exH, String refNo) {
        refNo = refNo.trim()
        if (refNo.matches(REF_NO_PATTERN_FOR_PREFIX)) {
            return exH.code + refNo
        } else {
            return refNo
        }
    }

    private String checkDuplicateRefNo(RmsTask task) {
        int countRef = rmsTaskService.countByRefNoAndCompanyId(task)
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
        int countPin = rmsTaskService.countByPinNoAndExchangeHouseId(task)
        if (countPin > 0) {
            return PIN_NO_SHOULD_BE_UNIQUE
        }
        return null
    }

}
