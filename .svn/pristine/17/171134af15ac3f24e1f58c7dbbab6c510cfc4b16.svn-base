package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  send Task to bank
 *  For details go through Use-Case doc named 'SendRmsTaskToBankActionService'
 */
class SendRmsTaskToBankActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService
    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to send task"
    private static final String TASK_DELETE_SUCCESS_MSG = "Task has been successfully sent"
    private static final String TASK_IDS = "taskIds"
    private static final String LST_TASKS = "lstTasks"
    private static final String REFRESH_PAGE = "Task status mismatched, refresh grid and try again"

    /**
     * Checking pre condition before send
     * 1. Check validity for input
     * 2. Check existence of Task object
     * @param parameters -parameters from UI
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
            if (!params.taskIds) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            List<Long> lstTaskIds = Tools.getIdsFromParams(params,TASK_IDS)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity pendingTask = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.PENDING_TASK, companyId)

            List<RmsTask> lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(pendingTask.id,lstTaskIds)
            if(lstRmsTask.size()!=lstTaskIds.size()) {
                result.put(Tools.MESSAGE,REFRESH_PAGE)
                return result
            }
            SystemEntity newTaskStatus = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.NEW_TASK, companyId)

            lstRmsTask.each {
                it.previousStatus = it.currentStatus
                it.currentStatus = newTaskStatus.id
            }
            result.put(TASK_IDS, lstTaskIds)
            result.put(LST_TASKS, lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
     * Update Task object from DB
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(LST_TASKS)
            List<Long> lstTaskIds = (List<Long>) preResult.get(TASK_IDS)
            long newTaskStatus = lstRmsTask[0].currentStatus     // since task properties already set
            rmsTaskService.updateRmsTaskForSentToBank(lstTaskIds,newTaskStatus)
            rmsTaskTraceService.create(lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build success message for send
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.IS_ERROR, Boolean.FALSE)
        result.put(Tools.MESSAGE, TASK_DELETE_SUCCESS_MSG)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
