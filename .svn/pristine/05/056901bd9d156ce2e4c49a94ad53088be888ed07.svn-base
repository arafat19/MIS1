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
 *  Revise approved task
 *  For details go through Use-Case doc named 'ReviseTaskActionService'
 */
class ReviseTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskListService rmsTaskListService
    RmsTaskTraceService rmsTaskTraceService
    RmsExchangeHouseCurrencyPostingService rmsExchangeHouseCurrencyPostingService
    RmsExchangeHouseService rmsExchangeHouseService
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsExchangeHouseCacheUtility rmsExchangeHouseCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String REVISE_TASK_FAILURE_MSG = "Failed to revise task"
    private static final String REVISE_TASK_SUCCESS_MSG = "Task has been successfully revised"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String REVISION_NOTE = "revisionNote"
    private static final String REFRESH_PAGE = "Please refresh page"
    private static final String REVISE_TASK_FAILED = "Already disbursed task cannot be revised"
    private static final String LST_RMS_TASK = "lstRmsTask"
    private static final String TASK_IDS = "taskIds"

    /**
     * 1. Get parameters from UI
     * 2. Get list of task id
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            List<Long> lstTaskIds = Tools.getIdsFromParams(params,TASK_IDS)
            String revisionNote = params.revisionNote
            long currentStatus = Long.parseLong(params.currentStatus)
            SystemEntity disbursed = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DISBURSED, rmsSessionUtil.appSessionUtil.getCompanyId())
            if (currentStatus == disbursed.id) {
                result.put(Tools.MESSAGE, REVISE_TASK_FAILED)
                return result
            }
            List<RmsTask> lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds)
            if (lstRmsTask.size() != lstTaskIds.size()) {
                result.put(Tools.MESSAGE, REFRESH_PAGE)
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
            result.put(Tools.MESSAGE, REVISE_TASK_FAILURE_MSG)
            return result
        }
    }

    /**
     * Update approved task for revise
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
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

            switch (currentStatus) {
                case (includedInList.id):
                    currentStatus = newTask.id
                    rmsTaskService.updateForIncludedInList(lstTaskIds, currentStatus, Boolean.TRUE, 0L, revisionNote)
                    break
                case (decisionTaken.id):
                    currentStatus = includedInList.id
                    rmsTaskService.updateRmsTaskStatus(lstTaskIds, currentStatus, Boolean.TRUE, revisionNote)
                    break
                case (decisionApprove.id):
                    currentStatus = decisionTaken.id
                    rmsTaskService.updateRmsTaskStatus(lstTaskIds, currentStatus, Boolean.TRUE, revisionNote)
                    updateExhBalanceAndCurrencyPosting(lstRmsTask, rmsTask.exchangeHouseId)
                    break
                default:
                    break
            }
            createRmsTaskTrace(lstRmsTask, currentStatus)
            //rmsTaskService.updateForReviseApprovedTask(lstTaskIds,currentStatus,revisionNote)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(REVISE_TASK_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVISE_TASK_FAILURE_MSG)
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
     * Build success message
     * @param obj -N/A
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(false)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.MESSAGE, REVISE_TASK_SUCCESS_MSG)
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        return result
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
            result.put(Tools.MESSAGE, REVISE_TASK_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, REVISE_TASK_FAILURE_MSG)
            return result
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

    private void createRmsTaskTrace(List<RmsTask> lstRmsTask, long currentStatus) {
        for(int i=0;i<lstRmsTask.size();i++) {
            RmsTask rmsTask = lstRmsTask[i]
            rmsTask.previousStatus = rmsTask.currentStatus
            rmsTask.currentStatus = currentStatus
            rmsTask.isRevised = Boolean.TRUE
            rmsTaskTraceService.create(rmsTask)
        }
    }

}
