package com.athena.mis.arms.actions.rmstasklist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Get list of taskList
 *  For details go through Use-Case doc named 'ListTaskListForSearchActionService'
 */
class ListTaskListForSearchActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil1
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String LST_TASK_LIST = "lstTaskList"
    private static final String FAILURE_MESSAGE = "Failed to load task list";
    private static final String NOT_FOUND = "Task list not found";
    private static final String GRID_OBJ = "gridObj";

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
            initPager(params)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            Map searchResult = mapTaskListForSearch(fromDate, toDate, this)
            List<GroovyRowResult> lstTaskList = (List<GroovyRowResult>) searchResult.lstTaskList
            if(lstTaskList.size() == 0) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                return result
            }
            Integer count = (Integer) searchResult.count
            result.put(LST_TASK_LIST, lstTaskList)
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
            List<GroovyRowResult> lstTaskList = (List<GroovyRowResult>) executeResult.get(LST_TASK_LIST)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTaskList = wrapTaskList(lstTaskList, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTaskList]
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
    private List wrapTaskList(List<GroovyRowResult> lstTask, int start) {
        List lstWrappedTaskList = []
        int counter = start + 1
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult taskList = lstTask[i]
            GridEntity obj = new GridEntity()
            obj.id = taskList.id
            obj.cell = [
                    counter,
                    taskList.id,
                    taskList.name,
                    DateUtility.getLongDateForUI(taskList.created_on),
                    taskList.exchange_house,
                    taskList.total_task,
                    taskList.total_amount
            ]
            lstWrappedTaskList << obj
            counter++
        }
        return lstWrappedTaskList
    }

    private static final String QUERY_STR = """
        SELECT task_list.id, task_list.name AS name, task_list.created_on created_on,
                exchange_house.name exchange_house, COUNT(task.id) total_task, SUM(task.amount) total_amount
        FROM rms_task_list task_list
            LEFT JOIN rms_exchange_house exchange_house ON task_list.exchange_house_id = exchange_house.id
            LEFT JOIN rms_task task ON task_list.id = task.task_list_id
        WHERE task_list.created_on BETWEEN :fromDate AND :toDate
            AND task_list.company_id = :companyId
        GROUP BY task_list.id, task_list.name, task_list.created_on, exchange_house.name
        ORDER BY task_list.id
        LIMIT :resultPerPage OFFSET :start
    """
    private static final String COUNT_QUERY = """
        SELECT COUNT(task_list.id) count
        FROM rms_task_list task_list
        WHERE task_list.created_on BETWEEN :fromDate AND :toDate
            AND task_list.company_id = :companyId
    """
    /**
     * Get list of taskList
     * @param fromDate
     * @param toDate
     * @param baseService
     * @return -a map containing taskList list and count
     */
    private Map mapTaskListForSearch(Date fromDate, Date toDate, BaseService baseService) {
        Map queryParams = [
                fromDate: DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlToDateWithSeconds(toDate),
                companyId: rmsSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstTaskList = executeSelectSql(QUERY_STR, queryParams)
        int count = executeSelectSql(COUNT_QUERY, queryParams).first().count

        return [lstTaskList : lstTaskList, count : count]
    }
}
