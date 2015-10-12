package com.athena.mis.arms.actions.rmstasklist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppUserService
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

class RemoveTaskFromListActionService extends BaseService implements ActionIntf{

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    RmsExchangeHouseService rmsExchangeHouseService
    AppUserService appUserService

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_IDS = "taskIds"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String REVISION_NOTE = "revisionNote"
    private static final String REMOVE_FROM_LIST_FAILED = "Task is not removed from list"
    private static final String REMOVED_FROM_LIST_REVISION_NOTE = "Removed from manage task list by"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String REMOVE_FROM_LIST_SUCCESS_MESSAGE = "Task has been removed from list successfully"
    private static final String REFRESH_GRID = "Refresh grid and try again"
    private static final String TRANSACTION_FOR_THIS_LIST_IS_CLOSED = "List is not removed."+"\n"+"Transaction day of this list is closed."

    /**
     * Get serialized parameters from UI
     * @param parameters-params
     * @param obj-N/A
     * @return- a map necessary for execute method
     */
    @Transactional(readOnly = true)
    public  Object executePreCondition(Object parameters, Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if(!params.taskListId){
                result.put(Tools.MESSAGE,Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            List<Long> lstTaskIds = Tools.getIdsFromParams(params,TASK_IDS)
            long currentStatus = Long.parseLong(params.currentStatus)
            String revisionNote= params.revisionNote
            List<RmsTask> lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds)
            if (lstRmsTask.size() != lstTaskIds.size()) {
                result.put(Tools.MESSAGE, REFRESH_GRID)
                return result
            }
            long taskListId=Long.parseLong(params.taskListId)
            RmsTaskList rmsTaskList=rmsTaskListService.read(taskListId)
            boolean isTransactionDayClose=false
            if(rmsTaskList){
                isTransactionDayClose=checkTransactionDayClose(rmsTaskList)
            }
            if(isTransactionDayClose){
                result.put(Tools.MESSAGE,TRANSACTION_FOR_THIS_LIST_IS_CLOSED)
                return result
            }
            result.put(LST_RMS_TASK, lstRmsTask)
            result.put(REVISION_NOTE, revisionNote)
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REMOVE_FROM_LIST_FAILED)
            return result
        }
    }

    /**
     * Do nothing for executePreCondition
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * Update rmsTask - currentStatus, isRevised & revisionNote
     * @param parameters-returned from executePreCondition
     * @param obj-N/A
     * @return-a map containing true/false based on method success
     */
    @Transactional
    public  Object execute(Object parameters, Object obj){
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj       // cast map returned from executePreCondition method
            List<Long> lstTaskIds = (List<Long>) preResult.get(LST_TASK_IDS)
            String revisionNote = (String) preResult.get(REVISION_NOTE)
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(LST_RMS_TASK)
            RmsTask rmsTask = (RmsTask) lstRmsTask[0]
            long currentStatus = rmsTask.currentStatus
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity newTask = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.NEW_TASK, companyId)
            SystemEntity includedInList = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.INCLUDED_IN_LIST, companyId)
            SystemEntity decisionTaken = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_TAKEN, companyId)
            SystemEntity decisionApprove = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
            long newCurrentStatus = newTask.id
            switch (currentStatus) {
                case (includedInList.id):
                    rmsTaskService.updateForIncludedInList(lstTaskIds, newCurrentStatus, Boolean.TRUE, 0L, revisionNote)
                    break
                case (decisionTaken.id):
                    rmsTaskService.updateForIncludedInList(lstTaskIds, newCurrentStatus, Boolean.TRUE, 0L,revisionNote)
                    break
                case (decisionApprove.id):
                    rmsTaskService.updateForIncludedInList(lstTaskIds, newCurrentStatus, Boolean.TRUE, 0L, revisionNote)
                    updateExhBalanceAndCurrencyPosting(lstRmsTask, rmsTask.exchangeHouseId)
                    break
            }
            createRmsTaskTrace(lstRmsTask, newCurrentStatus)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(REMOVE_FROM_LIST_FAILED)
            result.put(Tools.MESSAGE, REMOVE_FROM_LIST_FAILED)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Build success message
     * @param obj-N/A
     * @return-show success message to indicate success event
     */
    public  Object buildSuccessResultForUI(Object obj){
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.MESSAGE, REMOVE_FROM_LIST_SUCCESS_MESSAGE)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * Build failure message in case of any error
     * @param obj-returned from previous method may be null
     * @return-show failure message to indicate failure message
     */

    public  Object buildFailureResultForUI(Object obj){
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
            result.put(Tools.MESSAGE, REMOVE_FROM_LIST_FAILED)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REMOVE_FROM_LIST_FAILED)
            return result
        }
    }

    private void createRmsTaskTrace(List<RmsTask> lstRmsTask, long currentStatus) {
        for(int i=0;i<lstRmsTask.size();i++) {
            RmsTask rmsTask = lstRmsTask[i]
            rmsTask.previousStatus = rmsTask.currentStatus
            rmsTask.currentStatus = currentStatus
            rmsTask.isRevised = Boolean.TRUE
            rmsTaskTraceService.create(rmsTask)
        }
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
        exhHouseCurPosting.amount = rmsTask.amount
        exhHouseCurPosting.taskId = rmsTask.id
        exhHouseCurPosting.companyId = user.companyId
        exhHouseCurPosting.createdBy = user.id
        exhHouseCurPosting.createdOn = new Date()
        return exhHouseCurPosting
    }

    /**
     * Update exchangeHouseCurrencyPosting and exchange balance
     * @param lstTaskIds
     */
    private void updateExhBalanceAndCurrencyPosting(List<RmsTask> lstRmsTask, long exhHouseId) {
        for (int j = 0; j < lstRmsTask.size(); j++) {
            RmsExchangeHouseCurrencyPosting exhHouseCurPosting = buildExhHouseCurPosting(lstRmsTask[j])
            rmsExchangeHouseCurrencyPostingService.save(exhHouseCurPosting)
        }
        double amount = rmsExchangeHouseCurrencyPostingService.getBalanceAmount(exhHouseId)
        rmsExchangeHouseService.updateBalance(exhHouseId, amount)
        rmsExchangeHouseCacheUtility.updateBalance(exhHouseId, amount)
    }

    private static final String QUERY_FOR_CHECK_TRANSACTION_DAY_CLOSE="""
        SELECT COUNT(list.id) FROM rms_task_list list
        LEFT JOIN rms_transaction_day day
        ON day.transaction_date = date(list.created_on)
        WHERE day.closed_on is null
        AND list.id=:id
        AND list.company_id=:companyId
    """
    private boolean checkTransactionDayClose(RmsTaskList rmsTaskList){
        Map queryParams=[
                id:rmsTaskList.id,
                companyId:rmsTaskList.companyId
        ]
        List<GroovyRowResult> lstResults=executeSelectSql(QUERY_FOR_CHECK_TRANSACTION_DAY_CLOSE,queryParams)
        int count=(int)lstResults[0][0]
        if(count>0){
            return false
        }
        return true
    }
}


