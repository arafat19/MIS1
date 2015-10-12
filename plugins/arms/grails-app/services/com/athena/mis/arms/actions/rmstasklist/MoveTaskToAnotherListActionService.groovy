package com.athena.mis.arms.actions.rmstasklist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsExchangeHouseCurrencyPosting
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.*
import com.athena.mis.arms.utility.RmsExchangeHouseCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class MoveTaskToAnotherListActionService extends BaseService implements ActionIntf {

    RmsTaskListService rmsTaskListService
    RmsTaskService rmsTaskService
    RmsTaskTraceService rmsTaskTraceService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsExchangeHouseService rmsExchangeHouseService

    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DOUBLE_QUOTE = "\""
    private static final String TASK_ID = "taskId"
    private static final String LST_TASK_ID = "lstTaskId"
    private static final String TASK_LIST_NAME = "taskListName"
    private static
    final String SAME_LIST_NAME_EXISTS_FOR_ANOTHER_EXH_HOUSE = "Same list name exists for another exchange house"
    private static
    final String TRANSACTION_FOR_THIS_LIST_IS_CLOSED = "List is not moved.Transaction day is closed of list"
    private static final String FAILURE_MESSAGE = "Task is not moved to another list."
    private static final String HAS_RMS_TASK_LIST = "hasRmsTaskList"
    private static final String RMS_TASK_LIST = "rmsTaskList"
    private static final String SUCCESS_MESSAGE = "Task(s) moved successfully"
    private static final String EXH_HOUSE_ID = "exchangeHouseId"
    private static final String REFRESH_GRID = "Refresh grid and try again"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String IS_DECISION_APPROVED = "isDecisionApproved"
    private static final String REVISION_NOTE_MSG = "Forcefully removed from"
    private static final String REVISION_NOTE = "revisionNote"
    private static final String DISBURSED_TASK_CAN_NOT_BE_MOVE_FROM_LIST = "Disburse task can not be move from list"
    private static final String SAME_TASK_LIST_CAN_NOT_MOVE="Task(s) already belongs to same task list"

    /**
     * Get serialized parameters from UI
     * 1.task can not be moved to any list if list's transaction day is closed
     * 2.task can not be moved from any list if list's transaction day is closed
     * @param parameters -params
     * @param obj -N/A
     * @return-a map containing all object necessary for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(IS_DECISION_APPROVED, Boolean.FALSE)
            result.put(HAS_RMS_TASK_LIST, Boolean.FALSE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.taskListName) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!parameterMap.exchangeHouseId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!parameterMap.currentStatus) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!parameterMap.taskListId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DISBURSED, companyId)
            SystemEntity decisionApproved = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)

            //disbursed task can not be removed from list
            long currentStatus = Long.parseLong(parameterMap.currentStatus)
            if (currentStatus == disbursed.id) {
                result.put(Tools.MESSAGE, DISBURSED_TASK_CAN_NOT_BE_MOVE_FROM_LIST)
                return result
            }
            //IS_DECISION_APPROVED- flag for updating exh_house_currency_positing and updating exh_house balance
            if (currentStatus == decisionApproved.id) {
                result.put(IS_DECISION_APPROVED, Boolean.TRUE)
            }

            List<Long> lstTaskId = Tools.getIdsFromParams(parameterMap, TASK_ID)
            List<RmsTask> lstRmsTask = (List<RmsTask>) rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskId)
            if (lstRmsTask.size() != lstTaskId.size()) {
                result.put(Tools.MESSAGE, REFRESH_GRID)
                return result
            }
            //task can not be moved from any list which transaction day is closed
            Long taskListId = Long.parseLong(parameterMap.taskListId)
            RmsTaskList rmsList = rmsTaskListService.read(taskListId)
            boolean isTransactionDayCloseFromList = checkTransactionDayClose(rmsList)
            if (isTransactionDayCloseFromList) {
                result.put(Tools.MESSAGE, TRANSACTION_FOR_THIS_LIST_IS_CLOSED + Tools.SINGLE_SPACE + DOUBLE_QUOTE + rmsList.name + DOUBLE_QUOTE)
                return result
            }
            //task can't be moved to another list if same list name exists in another exchange house
            long exchangeHouseId = Long.parseLong(parameterMap.exchangeHouseId)
            String taskListName = parameterMap.taskListName
            RmsTaskList rmsTaskList = rmsTaskListService.findByNameIlike(taskListName)
            if (rmsTaskList && (rmsTaskList.exchangeHouseId != exchangeHouseId)) {
                result.put(Tools.MESSAGE, SAME_LIST_NAME_EXISTS_FOR_ANOTHER_EXH_HOUSE)
                return result
            }
            //task can't be moved to same list
            if(rmsTaskList && (rmsTaskList.id == taskListId)){
                result.put(Tools.MESSAGE,SAME_TASK_LIST_CAN_NOT_MOVE)
                return result
            }
            //task can not be moved to any list if transaction day of the corresponding list is closed
            boolean isTransactionDayCloseToList = false
            if (rmsTaskList) {
                isTransactionDayCloseToList = checkTransactionDayClose(rmsTaskList)
            }
            if (isTransactionDayCloseToList) {
                result.put(Tools.MESSAGE, TRANSACTION_FOR_THIS_LIST_IS_CLOSED + Tools.SINGLE_SPACE + DOUBLE_QUOTE + taskListName + DOUBLE_QUOTE)
                return result
            }
            //if same list name exists with same exh_house no new list will be created
            if (rmsTaskList) {
                result.put(HAS_RMS_TASK_LIST, Boolean.TRUE)
                result.put(RMS_TASK_LIST, rmsTaskList)
            }
            //previous list name from taskListDropDown for revision note purpose
            String revisionNote = REVISION_NOTE_MSG + Tools.SINGLE_SPACE + rmsList.name

            result.put(REVISION_NOTE, revisionNote)
            result.put(EXH_HOUSE_ID, exchangeHouseId)
            result.put(LST_TASK_ID, lstTaskId)
            result.put(TASK_LIST_NAME, taskListName)
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1.Build task list if task list does not exist
     * 2.Update rmsTask currentStatus, isRevised, taskListId & revisionNote
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return-a map containing true/false based on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) parameters
            List<Long> lstTaskIds = (List<Long>) executeResult.get(LST_TASK_ID)
            List<RmsTask> lstRmsTask = (List<RmsTask>) executeResult.get(LST_RMS_TASK)
            String taskListName = (String) executeResult.get(TASK_LIST_NAME)
            long exchangeHouseId = (long) executeResult.get(EXH_HOUSE_ID)
            Boolean isDecisionApproved = (Boolean) executeResult.get(IS_DECISION_APPROVED)
            String revisionNote = (String) executeResult.get(REVISION_NOTE)
            RmsTaskList rmsTaskList = null
            Boolean hasRmsTaskList = (Boolean) executeResult.get(HAS_RMS_TASK_LIST)
            //new taskList is created if not exists
            if (!hasRmsTaskList.booleanValue()) {
                RmsTaskList newRmsTaskList = buildRmsTaskList(taskListName, exchangeHouseId)
                rmsTaskList = rmsTaskListService.create(newRmsTaskList)
            } else {
                rmsTaskList = (RmsTaskList) executeResult.get(RMS_TASK_LIST)  //if taskList exists, get the previous one
            }
            SystemEntity includedInList = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.INCLUDED_IN_LIST, rmsSessionUtil.appSessionUtil.getCompanyId())
            //update rmsTask
            rmsTaskService.updateForIncludedInList(lstTaskIds, includedInList.id, Boolean.FALSE, rmsTaskList.id, revisionNote)
            //update exhHouseCurrencyPosting & exhHouseBalance for decisionApproved task(s)
            if (isDecisionApproved.booleanValue()) {
                updateRmsExhCurrencyPostingAndBalance(lstRmsTask, exchangeHouseId)
            }
            //create rmsTaskTrace
            createRmsTaskTrace(lstRmsTask, includedInList.id)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Build success result for UI
     * @param obj -N/A
     * @return-success message to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * Build failure result for UI
     * @param obj -returned from previous method may be null
     * @return-failure message to indicate success event
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_FOR_CHECK_TRANSACTION_DAY_CLOSE = """
        SELECT COUNT(list.id) FROM rms_task_list list
        LEFT JOIN rms_transaction_day day
        ON day.transaction_date = date(list.created_on)
        WHERE day.closed_on is null
        AND list.id=:id
        AND list.company_id=:companyId
    """
    /**
     * Check if the transactionDay of the corresponding list is closed
     * @param rmsTaskList - RmsTaskList obj
     * @return-true / f a l s e based on transactionDay close/open
     */
    private boolean checkTransactionDayClose(RmsTaskList rmsTaskList) {
        Map queryParams = [
                id: rmsTaskList.id,
                companyId: rmsTaskList.companyId
        ]
        List<GroovyRowResult> lstResults = executeSelectSql(QUERY_FOR_CHECK_TRANSACTION_DAY_CLOSE, queryParams)
        int count = (int) lstResults[0][0]
        if (count > 0) {
            return false
        }
        return true
    }

    /**
     * Build rmsTaskList for new list
     * @param taskListName -new list name
     * @param exchangeHouseId - exchange_house_id of list
     * @return
     */
    private RmsTaskList buildRmsTaskList(String taskListName, long exchangeHouseId) {
        RmsTaskList newRmsTaskList = new RmsTaskList()
        newRmsTaskList.name = taskListName
        newRmsTaskList.exchangeHouseId = exchangeHouseId
        newRmsTaskList.companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        newRmsTaskList.createdBy = rmsSessionUtil.appSessionUtil.getAppUser().id
        newRmsTaskList.createdOn = new Date()
        return newRmsTaskList
    }

    /**
     * Build exchangeHouseCurrencyPosting obj
     * @param rmsTask - RmsTask obj
     * @return exhHouseCurPosting
     */
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
    /**
     * 1.Update exchangeHouseCurrencyPosting for decision approved task
     * 2.Update exchangeHouseBalance for decision approved task
     * @param lstRmsTask - list of RmsTask obj
     * @param exhId -exchangeHouseId of the task(s)
     */
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
            lstRmsTask[i].currentStatus = currentStatus
            lstRmsTask[i].isRevised = Boolean.FALSE
        }
        rmsTaskTraceService.create(lstRmsTask)
    }
}
