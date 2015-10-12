package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Send task to Exchange House for Customer
 * For details go through Use-Case doc named 'ExhSendTaskToExhForCustomerActionService'
 */
class ExhSendTaskToExhForCustomerActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SENT_TO_EXCHANGE_HOUSE_SUCCESS = "Task(s) successfully sent to exchange house"
    private static final String SENT_TO_EXCHANGE_HOUSE_FAILURE = "Task(s) sent to exchange house failed"
    private static final String TASK_NOT_FOUND_ERROR = "Task not found. Refresh grid and try again"
    private static final String LST_TASKS = 'lstTasks'
    private static final String SUCCESS = "success"
    private static String IS_CONFIRMATION_ISSUE = "isConfirmationIssue"


    ExhTaskTraceService exhTaskTraceService

    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * Get parameters from UI and check pre condition
     * 1. parse id and put into list
     * 2. pull list of task(s) by id
     * 3. check size of pulled task and size of params ids
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {

        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            result.put(SUCCESS, Boolean.FALSE)
            List ids = parameterMap.ids.split(Tools.UNDERSCORE)
            List<Long> lstTaskIds = []

            // Get List of long IDs
            for (int i = 0; i < ids.size(); i++) {
                lstTaskIds << Long.parseLong(ids[i].toString())
            }

            List<ExhTask> lstTasks = readForSentToExHouse(lstTaskIds)

            if (lstTasks.size() != lstTaskIds.size()) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_ERROR)
                return result
            }

            result.put(SUCCESS, Boolean.TRUE)
            result.put(LST_TASKS, lstTasks)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            return result
        }
    }

    /**
     * execute following activities
     * 1. update one or more task through its status
     * 2. Save task trace into DB
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
            Map preResult = (Map) obj              // cast map returned from previous method
            List<ExhTask> lstTasks = (List<ExhTask>) preResult.get(LST_TASKS)

            processExchangeHouseTask(lstTasks)      // update customer's tasks' status from STATUS_PENDING_TASK to STATUS_UN_APPROVED
            result.put(SUCCESS, Boolean.TRUE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(SENT_TO_EXCHANGE_HOUSE_FAILURE)
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
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
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_SUCCESS)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (receiveResult.get(IS_CONFIRMATION_ISSUE)) {
                    result.put(IS_CONFIRMATION_ISSUE, Boolean.TRUE)
                    result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
                    result.put(Tools.IS_ERROR, Boolean.FALSE)
                    return result
                }
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            return result
        }
    }

    /**
     * 1. Set task status with UN_APPROVED
     * 2. Build & Save agent currency posting into DB
     * 3. Save task trace into DB
     * @param lstTasks -list of task
     */
    private void processExchangeHouseTask(List<ExhTask> lstTasks) {
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, exhSessionUtil.appSessionUtil.getCompanyId())
        updateForSentToExhForCustomer(lstTasks)
        for (ExhTask task in lstTasks) {
            task.currentStatus = exhStatusUnApprovedSysEntityObject.id
            exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
        }
    }

    /**
     * Get task(s) of pending by its ids
     * @param taskIds -list of taskId
     * @return lstTasks -list of task
     */
    private List<ExhTask> readForSentToExHouse(List<Long> taskIds) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)

        List<ExhTask> lstTasks = ExhTask.findAllByIdInListAndCurrentStatus(
                taskIds, exhPendingTaskSysEntityObject.id,
                [readOnly: true]
        )
        return lstTasks
    }

    /**
     * Update task task status as N_APPROVED
     * @param lstTasks
     * @return updateCount
     */
    private Integer updateForSentToExhForCustomer(List<ExhTask> lstTasks) {
        List<Long> lstIds = lstTasks.collect { it.id }
        String strIds = Tools.buildCommaSeparatedStringOfIds(lstIds)
        SystemEntity exhStatusUnApprovedSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_UN_APPROVED, exhSessionUtil.appSessionUtil.getCompanyId())
        String query = """
                UPDATE exh_task SET
                version = version+1,
                current_status = :statusNew
                WHERE id IN (${strIds})
            """
        Map queryParams = [statusNew: exhStatusUnApprovedSysEntityObject.id]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task")
        }
        return (new Integer(updateCount))
    }

}
