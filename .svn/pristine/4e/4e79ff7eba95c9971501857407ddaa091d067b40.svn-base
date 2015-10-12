package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Resolve only SENT_TO_OTHER_BANK task(s) object for Other bank user
 *  For details go through Use-Case doc named 'ExhResolveTaskOtherBankActionService'
 */
class ExhResolveTaskOtherBankActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    ExhTaskService exhTaskService
    ExhTaskTraceService exhTaskTraceService

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private static final String SUCCESS_MESSAGE = "Task(s) successfully resolved"
    private static final String FAILURE_MESSAGE = "Failed to resolve Task(s)"
    private static final String LST_TASKS = "lstTasks"

    /**
     * Get parameters from UI and check pre condition
     * 1. check between taskIds from UI and size of task from DB by taskIds
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            List<ExhTask> lstTasks = []
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            List ids = parameterMap.ids.split(Tools.UNDERSCORE)
            List<Long> lstTaskIds = []

            // Get List of long IDs
            for (int i = 0; i < ids.size(); i++) {
                lstTaskIds << Long.parseLong(ids[i].toString())
            }

            lstTasks = readForResolveByOtherBank(lstTaskIds)      // get resolved task by lstTaskIds

            if (lstTasks.size() != lstTaskIds.size()) {
                return null
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(LST_TASKS, lstTasks)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * execute following activities
     * 1. Resolved one or more task through its status e.g. RESOLVED_BY_OTHER_BANK (2000206)
     * 2. Update task trace
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

            Map preResult = (Map) obj          // cast map returned from previous method
            List<ExhTask> lstTasks = (List<ExhTask>) preResult.get(LST_TASKS)
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()

            for (int i = 0; i < lstTasks.size(); i++) {
                ExhTask task = lstTasks[i]
                SystemEntity resolvedByOtherStatus = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(ExhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)
                task.currentStatus = resolvedByOtherStatus.id
                exhTaskService.updateForResolvedByOtherBank(task)                    // update task
                boolean saveTrace = exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
                // save task trace in DB
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(LST_TASKS, lstTasks)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to resolve Task')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            Map previousResult = (Map) obj
            List<ExhTask> lstTasks = (List<ExhTask>) previousResult.get(LST_TASKS)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, lstTasks.size() + Tools.SINGLE_SPACE + SUCCESS_MESSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj && obj.message) {
                result.put(Tools.MESSAGE, obj.message)
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }

    }

    /**
     * Get list of Task with status sent to other bank
     * @param taskIds
     * @return -list of Task
     */
    private List<ExhTask> readForResolveByOtherBank(List<Long> taskIds) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        return ExhTask.findAllByIdInListAndCurrentStatus(
                taskIds,
                exhSentToOtherBankSysEntityObject.id,
                [readOnly: true]
        )
    }
}
