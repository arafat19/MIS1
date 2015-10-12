package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.arms.entity.RmsTask
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Get list of task for map
 *  For details go through Use-Case doc named 'ListTaskForMapTaskActionService'
 */
class ListTaskForMapTaskActionService extends BaseService implements ActionIntf {

    RmsTaskService rmsTaskService

    private Logger log = Logger.getLogger(getClass())

    private static final String LST_TASK = "lstTask"
    private static final String FAILURE_MESSAGE = "Failed to load task";
    private static final String GRID_OBJ = "gridObj";
    private static final String ON = "on";

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get task list
     * 2. Get count of task
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.rp) params.rp = 15
            if (!params.page) params.page = 1
            initPager(params)
            long exhHouseId = Long.parseLong(params.exhHouseId)
            long currentStatus = Long.parseLong(params.currentStatus)
            long paymentMethod = Long.parseLong(params.paymentMethod)
            boolean isRevised = false
            if(params.isRevised && (params.isRevised.toString().equals(ON))) {
                isRevised = true
            }
            long taskListId = Long.parseLong(params.taskListId)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            Map resultTask = rmsTaskService.listTaskByStatus(exhHouseId, currentStatus, paymentMethod, isRevised, taskListId, fromDate, toDate, this)
            List<RmsTask> listTask = (List<RmsTask>) resultTask.listOfTasks
            int count = (Integer) resultTask.count
            result.put(LST_TASK, listTask)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * Wrap task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<RmsTask> lstTask = (List<RmsTask>) executeResult.get(LST_TASK)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapTask(lstTask, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ, gridObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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

    /**
     * Wrap list of task in grid entity
     * @param lstTask -list of task object
     * @param start -starting index of the page
     * @return -list of wrapped task
     */
    private List wrapTask(List<RmsTask> lstTask, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTask.size(); i++) {
            RmsTask task = lstTask[i]
            GridEntity obj = new GridEntity()
            obj.id = task.id
            obj.cell = [
                    counter,
                    task.id,
                    task.refNo,
                    task.amount,
                    DateUtility.getLongDateForUI(task.valueDate),
                    task.getFullOutletName()
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }
}
