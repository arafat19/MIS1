package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.EntityNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.EntityNoteService
import com.athena.mis.application.utility.NoteEntityTypeCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhAgentCurrencyPostingService
import com.athena.mis.exchangehouse.service.ExhAgentService
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.exchangehouse.utility.ExhTaskTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Cancel only new task(s) object for Admin
 *  For details go through Use-Case doc named 'ExhCancelTaskActionService'
 */
class ExhCancelTaskActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String CANCELLED_TASK_SUCCESS = "Task has been successfully cancelled."
    private static final String CANCELLED_TASK_FAILURE = "Failed to cancel task."
    private static final String ONLY_NEW_TASK_CAN_BE_CANCELLED = "Only new task(s) can be cancelled."
    private static final String INVALID_INPUTS = "Error occurred for invalid inputs"
    private static final String LIST_OF_TASK = "lstTasks"
    private static final String CANCEL_REASON = "cancelReason"
    private static final String ERROR_AGENT_INFO_UPDATE = 'Error occurred while updating agent information'

    ExhTaskService exhTaskService
    ExhTaskTraceService exhTaskTraceService
    ExhAgentService exhAgentService
    ExhAgentCurrencyPostingService exhAgentCurrencyPostingService
    EntityNoteService entityNoteService

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    NoteEntityTypeCacheUtility noteEntityTypeCacheUtility

    /**
     * Get parameters from UI and check pre condition
     * 1. check has access type of Admin
     * 2. check required parameters
     * 3. pull list of task(s) by id
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap preResult = new LinkedHashMap()
        List<ExhTask> lstTasks = []
        try {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {        // check access for cancel task
                preResult.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return preResult
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if ((!params.ids) || (!params.reason)) {          // check required params
                preResult.put(Tools.MESSAGE, INVALID_INPUTS)
                return preResult
            }
            String cancelReason = params.reason.toString()
            List ids = params.ids.split(Tools.UNDERSCORE)
            List<Long> lstTaskIds = []

            // Get List of long IDs
            for (int i = 0; i < ids.size(); i++) {
                lstTaskIds << Long.parseLong(ids[i].toString())
            }
            lstTasks = readForCancelTask(lstTaskIds)          // get list of task(s) for cancel

            if (lstTasks.size() != lstTaskIds.size()) {
                preResult.put(Tools.MESSAGE, ONLY_NEW_TASK_CAN_BE_CANCELLED)
                return preResult
            }

            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            preResult.put(LIST_OF_TASK, lstTasks)
            preResult.put(CANCEL_REASON, cancelReason)
            return preResult
        } catch (Exception e) {
            log.error(e.getMessage())
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, CANCELLED_TASK_FAILURE)
            return preResult
        }
    }

    /**
     * execute following activities
     * 1. Cancel one or more task through its status
     * 2. Build EntityNote for Task
     * 3. Save cancel reason into EntityNote in DB
     * 4. if task type of AGENT, then build agent currency posting and update currency posting & agent balance
     * 5. at last save task trace in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj               // cast map returned from previous method
            List<ExhTask> lstTasks = (List<ExhTask>) preResult.get(LIST_OF_TASK)
            String cancelReason = preResult.get(CANCEL_REASON)
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)

            for (int i = 0; i < lstTasks.size(); i++) {      //@todo: update task with a single query
                ExhTask task = lstTasks[i]
                exhTaskService.cancelTask(task)                       // task cancel by its status
                EntityNote noteObj = buildTaskNote(task, cancelReason)      // build entity note object
                entityNoteService.create(noteObj)                           // save entity note
                if (task.taskTypeId == agentTaskObj.id) {         // check task type of agent
                    exhAgentCurrencyPostingService.create(buildAgentCurrencyPosting(task))
                    updateBalanceForAgent(task)                                     // update agent balance
                }
                exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CANCELLED_TASK_SUCCESS)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(CANCELLED_TASK_FAILURE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CANCELLED_TASK_FAILURE)
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
        Map result
        try {
            result = [isError: obj.get(Tools.IS_ERROR), message: obj.get(Tools.MESSAGE)]
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result = [isError: true, message: CANCELLED_TASK_FAILURE]
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, true)
            if (obj && obj.message) {
                result.put(Tools.MESSAGE, obj.message)
            } else {
                result.put(Tools.MESSAGE, CANCELLED_TASK_FAILURE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, CANCELLED_TASK_FAILURE)
            return result
        }
    }

    private static final String QUERY_UPDATE_AGENT_BALANCE =
            """
            UPDATE
                exh_agent
            SET
                version = :newVersion,
                balance = :newBalance
            WHERE
                id = :id AND
                version = :version AND
                balance = :balance
        """

    /**
     * Update agent balance after canceling task
     * @param task
     * @return agent cache utility
     */
    private void updateBalanceForAgent(ExhTask task) {
        ExhAgent agent = (ExhAgent) exhAgentCacheUtility.read(task.agentId)

        double newBalance = agent.balance + task.amountInLocalCurrency + task.regularFee
        newBalance = newBalance.round(2)

        Map queryParams = [
                id: agent.id,
                version: agent.version,
                newVersion: agent.version + 1,
                newBalance: newBalance,
                balance: agent.balance
        ]

        int updateCount = executeUpdateSql(QUERY_UPDATE_AGENT_BALANCE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException(ERROR_AGENT_INFO_UPDATE)
        }
        agent.version = agent.version + 1
        agent.balance = newBalance
        exhAgentCacheUtility.update(agent, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
    }

    /**
     * Get list of task(s) by its id which is status NEW
     * @param taskIds
     * @return lstTasks -list of task(s)
     */
    private List<ExhTask> readForCancelTask(List<Long> taskIds) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        List<ExhTask> lstTasks = ExhTask.findAllByIdInListAndCompanyIdAndCurrentStatus(
                taskIds,
                companyId,
                exhNewTaskSysEntityObject.id,
                [readOnly: true]
        )
        return lstTasks
    }

    /**
     * Build currency posting object
     * @param task -ExhTask object
     * @return exhAgentCurrencyPosting
     */
    private ExhAgentCurrencyPosting buildAgentCurrencyPosting(ExhTask task) {
        ExhAgentCurrencyPosting exhAgentCurrencyPosting = new ExhAgentCurrencyPosting();
        long agentId = task.agentId
        ExhAgent exhAgent = exhAgentService.read(agentId)
        exhAgentCurrencyPosting.agentId = agentId
        exhAgentCurrencyPosting.currencyId = exhAgent.currencyId
        exhAgentCurrencyPosting.amount = (task.amountInLocalCurrency + task.regularFee).round(2)
        exhAgentCurrencyPosting.createdBy = exhSessionUtil.appSessionUtil.getAppUser().id
        exhAgentCurrencyPosting.updatedBy = 0L
        exhAgentCurrencyPosting.createdOn = new Date()
        exhAgentCurrencyPosting.updatedOn = null
        exhAgentCurrencyPosting.taskId = task.id
        return exhAgentCurrencyPosting
    }

    /**
     * Build entity note object
     * @param task -ExhTask object
     * @param note -cancel reason
     * @return -entityNote object
     */

    private EntityNote buildTaskNote(ExhTask task, String note) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        // pull note entity type(Task) object
        SystemEntity noteEntityTypeTask = (SystemEntity) noteEntityTypeCacheUtility.readByReservedAndCompany(noteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_TASK, companyId)

        EntityNote entityNote = new EntityNote()
        entityNote.entityId = task.id
        entityNote.entityTypeId = noteEntityTypeTask.id
        entityNote.note = note
        entityNote.companyId = companyId
        entityNote.pluginId = PluginConnector.EXCHANGE_HOUSE_ID
        entityNote.createdBy = exhSessionUtil.appSessionUtil.getAppUser().id
        entityNote.createdOn = new Date()
        entityNote.updatedBy = 0L
        entityNote.updatedOn = null
        return entityNote
    }

}
