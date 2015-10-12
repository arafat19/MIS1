package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsExchangeHouseCurrencyPostingService
import com.athena.mis.arms.service.RmsExchangeHouseService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class CancelRmsTaskActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    RmsTaskTraceService rmsTaskTraceService
    RmsTaskService rmsTaskService
    RmsExchangeHouseService rmsExchangeHouseService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    private final Logger log = Logger.getLogger(getClass())

    private static final String CANCEL_TASK_FAILURE_MESSAGE = "Task can not be canceled"
    private static final String TASK_IDS = "taskIds"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String NEW_TASK_CAN_NOT_BE_CANCELED = "New task can not be canceled"
    private static final String DISBURSED_TASK_CAN_NOT_BE_CANCELED = "Disbursed task can not be canceled"
    private static final String TASK_IS_ALREADY_CANCELED = "Task is already canceled"
    private static final String TASK_CANCELED_SUCCESSFULLY = "Task has been canceled successfully"
    private static final String REFRESH_PAGE = "Refresh page"
    private static final String EXCHANGE_HOUSE_ID = "exchangeHouseId"
    private static final String CURRENT_STATUS = "currentStatus"
    private static final String REVISION_NOTE = "revisionNote"

    /**
     * Get serialized parameters from UI
     * 1.new task, disbursed and canceled task can not be canceled
     * 2.hasNavTaskId- flag to indicate cancel task from manage task UI
     * 3.navTaskId-taskId
     * @param parameters -params
     * @param obj -N/A
     * @return-a map containing all object necessary for execute method
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long currentStatus = 0l
            List<Long> lstTaskIds = []
            List<RmsTask> lstRmsTask = []
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity newTask = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.NEW_TASK, companyId)
            SystemEntity disbursedTask = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DISBURSED, companyId)
            SystemEntity cancelTask = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.CANCELED, companyId)
            if (parameterMap.navTaskId) {
                Long taskId = Long.parseLong(parameterMap.navTaskId)
                lstTaskIds << taskId
                RmsTask rmsTask = rmsTaskService.read(taskId)
                lstRmsTask << rmsTask
                result.put(EXCHANGE_HOUSE_ID, rmsTask.exchangeHouseId)
                currentStatus = rmsTask.currentStatus
            } else {
                lstTaskIds = Tools.getIdsFromParams(parameterMap, TASK_IDS)
                currentStatus = Long.parseLong(parameterMap.currentStatus)
                lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds)
                if (lstRmsTask.size() != lstTaskIds.size()) {
                    result.put(Tools.MESSAGE, REFRESH_PAGE)
                    return result
                }
                long exchangeHouseId = Long.parseLong(parameterMap.exchangeHouseId)
                result.put(EXCHANGE_HOUSE_ID, exchangeHouseId)
            }
            if (currentStatus == newTask.id) {
                result.put(Tools.MESSAGE, NEW_TASK_CAN_NOT_BE_CANCELED)
                return result
            }
            if (currentStatus == disbursedTask.id) {
                result.put(Tools.MESSAGE, DISBURSED_TASK_CAN_NOT_BE_CANCELED)
                return result
            }
            //check weather task is already canceled
            if (currentStatus == cancelTask.id) {
                result.put(Tools.MESSAGE, TASK_IS_ALREADY_CANCELED)
                return result
            }
            if(!parameterMap.revisionNote){
                result.put(Tools.MESSAGE,Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            String revisionNote=parameterMap.revisionNote
            result.put(REVISION_NOTE, revisionNote)
            result.put(CURRENT_STATUS, currentStatus)
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, CANCEL_TASK_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }
    /**
     * Do nothing for executePostCondition
     * @param parameters
     * @param obj
     * @return
     */

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * update rmsTask status, update exhHouse balance and update exhHouseCurrPosting
     * @param parameters -parameters returned from executePreCondition
     * @param obj -N/A
     * @return- a map based on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) parameters
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            String revisionNote = (String) executeResult.get(REVISION_NOTE)
            Long currentStatus = (Long) executeResult.get(CURRENT_STATUS)
            List<Long> lstTaskIds = (List<Long>) executeResult.get(LST_TASK_IDS)
            List<RmsTask> lstRmsTask = (List<RmsTask>) executeResult.get(LST_RMS_TASK)
            long exchangeHouseId = (long) executeResult.get(EXCHANGE_HOUSE_ID)
            SystemEntity cancelled = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.CANCELED, companyId)
            SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
            rmsTaskService.updateRmsTaskStatus(lstTaskIds, cancelled.id, Boolean.FALSE, revisionNote)
            createRmsTaskTrace(lstRmsTask, cancelled.id)
            if (currentStatus == decisionApproved.id) {
                updateRmsExhCurrencyPostingAndBalance(lstRmsTask, exchangeHouseId)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(CANCEL_TASK_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CANCEL_TASK_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build success message
     * @param obj -returned from execute
     * @return-a message to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map executeResult = (Map) obj
        result.put(Tools.MESSAGE, TASK_CANCELED_SUCCESSFULLY)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * Build failure message
     * @param obj -returned from execute method
     * @return-show failure message to indicate failure message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        Map executeResult = (Map) obj
        if(obj) {
            String msg = executeResult.get(Tools.MESSAGE)
            if(msg) {
                result.put(Tools.MESSAGE, msg)
            } else {
                result.put(Tools.MESSAGE, CANCEL_TASK_FAILURE_MESSAGE)
            }
        }
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        return result
    }

    private RmsExchangeHouseCurrencyPosting buildExhHouseCurPosting(RmsTask rmsTask) {
        AppUser user = rmsSessionUtil.appSessionUtil.getAppUser()
        RmsExchangeHouseCurrencyPosting exhHouseCurPosting = new RmsExchangeHouseCurrencyPosting()
        exhHouseCurPosting.exchangeHouseId = rmsTask.exchangeHouseId
        exhHouseCurPosting.amount = rmsTask.amount
        exhHouseCurPosting.taskId = rmsTask.id
        exhHouseCurPosting.companyId = user.companyId
        exhHouseCurPosting.createdBy = user.id
        exhHouseCurPosting.createdOn = new Date()
        return exhHouseCurPosting
    }

    private void updateRmsExhCurrencyPostingAndBalance(List<RmsTask> lstRmsTask, long exhId) {
        for (int i = 0; i < lstRmsTask.size(); i++) {
            RmsTask rmsTask = lstRmsTask[i]
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = buildExhHouseCurPosting(rmsTask)
            rmsExchangeHouseCurrencyPostingService.save(exhHouseCurPosting)
        }
        double amount = rmsExchangeHouseCurrencyPostingService.getBalanceAmount(exhId)
        rmsExchangeHouseService.updateBalance(exhId, amount)
        rmsExchangeHouseCacheUtility.updateBalance(exhId, amount)
    }

    private void createRmsTaskTrace(List<RmsTask> lstRmsTask, long currentStatus) {
        for (int i = 0; i < lstRmsTask.size(); i++) {
            RmsTask rmsTask = lstRmsTask[i]
            rmsTask.previousStatus = rmsTask.currentStatus
            rmsTask.currentStatus = currentStatus
            rmsTask.isRevised = Boolean.FALSE
            rmsTaskTraceService.create(rmsTask)
        }
    }
}
