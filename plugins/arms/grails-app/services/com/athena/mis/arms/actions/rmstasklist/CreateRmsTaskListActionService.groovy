package com.athena.mis.arms.actions.rmstasklist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.service.RmsTaskTraceService
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Create new taskList object and update task object
 *  For details go through Use-Case doc named 'CreateRmsTaskListActionService'
 */
class CreateRmsTaskListActionService extends BaseService implements ActionIntf {

    RmsTaskListService rmsTaskListService
    RmsTaskService rmsTaskService
    RmsTaskTraceService rmsTaskTraceService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_LIST_OBJ = "taskList"
    private static final String TASK_LIST_CREATE_FAILURE_MSG = "Task list has not been saved"
    private static final String TASK_LIST_CREATE_SUCCESS_MSG = "Task list has been successfully saved"
    private static final String NAME_EXIST_MESSAGE = "Same task list name is exist for another exchange house"
    private static final String REFRESH_GRID = "Refresh grid and try again"
    private static final String LST_TASK_IDS = "lstTaskIds"
    private static final String LST_TASK = "lstTask"
    private static final String TASK_IDS = "taskIds"
    private static final String ON = "on"
    private static final String TRANSACTION_FOR_THIS_LIST_IS_CLOSED = "Transaction day for this list is closed"

    /**
     * 1. Get parameters from UI and build taskList object
     * 2. Check existence of taskList name
     * 3. Get list of task id by exchangeHouseId
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
            long exchangeHouseId = Long.parseLong(params.exchangeHouseId)
            long currentStatus = Long.parseLong(params.currentStatus)
            String name = params.name

            //checkTransactionDay-flag to decide weather to check list's transactionDay open/close
            //check transaction of list if list name already exists
            boolean checkTransactionDay=true
            RmsTaskList rmsTaskList = rmsTaskListService.findByNameIlike(name)
            if(!rmsTaskList){
                checkTransactionDay=false
            }
            if (rmsTaskList && (rmsTaskList.exchangeHouseId != exchangeHouseId)) {
                result.put(Tools.MESSAGE, NAME_EXIST_MESSAGE)
                return result
            }
            if (!rmsTaskList) {
                rmsTaskList = buildTaskList(params)     // build taskList object
            }
            boolean applyToAll = false
            if (params.applyToAllTask && (params.applyToAllTask.toString().equals(ON))) {
                applyToAll = true
            }
            List<Long> lstTaskIds = getTaskIds(params, currentStatus, applyToAll)
            List<RmsTask> lstRmsTask = rmsTaskService.findAllByCurrentStatusAndIdInList(currentStatus, lstTaskIds)
            if (lstRmsTask.size() != lstTaskIds.size()) {
                result.put(Tools.MESSAGE, REFRESH_GRID)
                return result
            }
            boolean isTransactionDayClose=false
            if(checkTransactionDay){
                isTransactionDayClose=checkTransactionDayClose(rmsTaskList)
            }
            if(isTransactionDayClose){
                result.put(Tools.MESSAGE,TRANSACTION_FOR_THIS_LIST_IS_CLOSED)
                return result
            }
            result.put(TASK_LIST_OBJ, rmsTaskList)
            result.put(LST_TASK_IDS, lstTaskIds)
            result.put(LST_TASK, lstRmsTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_LIST_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * 1. Save taskList object in DB
     * 2. Update task for included in list
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            SystemEntity includedInList = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.INCLUDED_IN_LIST, rmsSessionUtil.appSessionUtil.getCompanyId())
            LinkedHashMap preResult = (LinkedHashMap) obj
            List<Long> lstTaskIds = (List<Long>) preResult.get(LST_TASK_IDS)
            RmsTaskList taskList = (RmsTaskList) preResult.get(TASK_LIST_OBJ)
            List<RmsTask> lstRmsTask = (List<RmsTask>) preResult.get(LST_TASK)
            if (taskList.id <= 0) {
                rmsTaskListService.create(taskList)     // save new taskList object in DB
            }
            rmsTaskService.updateForIncludedInList(lstTaskIds, includedInList.id, Boolean.FALSE, taskList.id, null)
            createRmsTaskTrace(lstRmsTask, includedInList.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(TASK_LIST_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_LIST_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
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
        result.put(Tools.MESSAGE, TASK_LIST_CREATE_SUCCESS_MSG)
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
            result.put(Tools.MESSAGE, TASK_LIST_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_LIST_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * 1. Get task ids from DB or UI
     * 2. Make list of long id
     * @param params -serialized parameters from UI
     * @return -list of task ids
     */
    private List<Long> getTaskIds(GrailsParameterMap params, long currentStatus, boolean applyToAll) {
        List taskIds
        List<Long> lstTaskIds = []
        if (applyToAll) {
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            long exchangeHouseId = Long.parseLong(params.exchangeHouseId)
            boolean isRevised = false
            if (params.isRevised && (params.isRevised.toString().equals(ON))) {
                isRevised = true
            }
            taskIds = rmsTaskService.getTaskIds(exchangeHouseId, currentStatus, fromDate, toDate, isRevised)
            for (int i = 0; i < taskIds.size(); i++) {
                lstTaskIds << Long.parseLong(taskIds[i].id.toString())
            }
        } else {
             lstTaskIds=Tools.getIdsFromParams(params,TASK_IDS)
        }
        return lstTaskIds
    }

    /**
     * Build new taskList object
     * @param parameterMap -serialized parameters from UI
     * @return -new taskList object
     */
    private RmsTaskList buildTaskList(GrailsParameterMap parameterMap) {
        RmsTaskList taskList = new RmsTaskList(parameterMap)
        AppUser user = rmsSessionUtil.appSessionUtil.getAppUser()
        taskList.createdBy = user.id
        taskList.createdOn = new Date()
        taskList.companyId = user.companyId
        return taskList
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
