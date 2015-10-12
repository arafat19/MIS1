package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.*
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new exchangeHouseCurrencyPosting object and update task object
 *  For details go through Use-Case doc named 'ApproveRmsTaskActionService'
 */
class ApproveRmsTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    RmsExchangeHouseService rmsExchangeHouseService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_APPROVE_FAILURE_MSG = "Task has not been approved"
    private static final String TASK_APPROVE_SUCCESS_MSG = "Task has been successfully approved"
    private static final String AMOUNT_EXCEED_MSG = "Task(s) amount exceed exchange house balance"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String EXH_HOUSE_ID = "exhHouseId"
    private static final String REFRESH_PAGE = "Task status mismatched, refresh grid and try again"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String TASK_IDS = "taskIds"

    /**
     * 1. Get parameters from UI
     * 2. Get list of task id by exchangeHouseId
     * 3. Check exchange house balance
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
            GrailsParameterMap params = (GrailsParameterMap) parameters
            List<Long> lstTaskIds = Tools.getIdsFromParams(params,TASK_IDS)
            long currentStatus=  Long.parseLong(params.currentStatus)
            List<RmsTask> lstRmsTask= rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus,lstTaskIds)
            if(lstRmsTask.size()!=lstTaskIds.size()){
                result.put(Tools.MESSAGE,REFRESH_PAGE)
                return result
            }

            long exhHouseId = Long.parseLong(params.exhHouseId)
            if(checkBalance(exhHouseId, lstTaskIds)) {
                result.put(Tools.MESSAGE, AMOUNT_EXCEED_MSG)
                return result
            }
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(EXH_HOUSE_ID, exhHouseId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_APPROVE_FAILURE_MSG)
            return result
        }
    }

    /**
     * 1. Save exchangeHouseCurrencyPosting object in DB
     * 2. Update task for approve
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, rmsSessionUtil.appSessionUtil.getCompanyId())
            LinkedHashMap preResult = (LinkedHashMap) obj
            List<Long> lstTaskIds = (List<Long>) preResult.get(LST_TASK_IDS)
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(LST_RMS_TASK)
            long exhId = (long) preResult.get(EXH_HOUSE_ID)
            updateRmsExhCurrencyPostingAndBalance(lstRmsTask, exhId)
            rmsTaskService.updateRmsTaskStatus(lstTaskIds,decisionApproved.id, Boolean.FALSE, null)
            createRmsTaskTrace(lstRmsTask, decisionApproved.id)
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(EXH_HOUSE_ID, exhId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(TASK_APPROVE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_APPROVE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for executePostCondition
     * @param parameters
     * @param obj
     * @return
     */
    Object executePostCondition(Object parameters, Object obj) {
            return null
    }

    /**
     * Build success message
     * @param obj -N/A
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(false)
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.MESSAGE, TASK_APPROVE_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, TASK_APPROVE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_APPROVE_FAILURE_MSG)
            return result
        }
    }



    private boolean checkBalance(long exhHouseId, List<Long> lstTaskIds) {
        boolean isExceed = Boolean.FALSE
        double tasksAmount = rmsTaskService.getSelectedTasksAmount(lstTaskIds)
        double totalSelectedTaskAmount = tasksAmount
        double exhHouseAmount = rmsExchangeHouseCurrencyPostingService.getBalanceAmount(exhHouseId)
        double totalExhHouseAmount = exhHouseAmount
        if(totalExhHouseAmount < totalSelectedTaskAmount) {
            isExceed = Boolean.TRUE
        }
        return isExceed
    }

    /**
     * Build new RmsExchangeHouseCurrencyPosting object
     * @param exhHouseId
     * @param taskId
     * @return -new RmsExchangeHouseCurrencyPosting object
     */
    private RmsExchangeHouseCurrencyPosting buildExhHouseCurPosting(RmsTask rmsTask) {
        AppUser user = rmsSessionUtil.appSessionUtil.getAppUser()
        RmsExchangeHouseCurrencyPosting exhHouseCurPosting = new RmsExchangeHouseCurrencyPosting()
        exhHouseCurPosting.exchangeHouseId = rmsTask.exchangeHouseId
        exhHouseCurPosting.amount = 0 - rmsTask.amount
        exhHouseCurPosting.taskId = rmsTask.id
        exhHouseCurPosting.companyId = user.companyId
        exhHouseCurPosting.createdBy = user.id
        exhHouseCurPosting.createdOn = new Date()
        return exhHouseCurPosting
    }

    private void updateRmsExhCurrencyPostingAndBalance(List<RmsTask> lstRmsTask, long exhId) {
        for(int i = 0; i < lstRmsTask.size(); i++) {
            RmsTask rmsTask = lstRmsTask[i]
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = buildExhHouseCurPosting(rmsTask)
            rmsExchangeHouseCurrencyPostingService.save(exhHouseCurPosting)
        }
        double amount= rmsExchangeHouseCurrencyPostingService.getBalanceAmount(exhId)
        rmsExchangeHouseService.updateBalance(exhId, amount)
        rmsExchangeHouseCacheUtility.updateBalance(exhId, amount)
    }

    private void createRmsTaskTrace(List<RmsTask> lstRmsTask, long currentStatus) {
        for(int i=0;i<lstRmsTask.size();i++) {
            RmsTask rmsTask = lstRmsTask[i]
            rmsTask.previousStatus = rmsTask.currentStatus
            rmsTask.currentStatus = currentStatus
            rmsTask.isRevised = Boolean.FALSE
            rmsTaskTraceService.create(rmsTask)
        }
    }
}
