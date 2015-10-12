package com.athena.mis.arms.actions.report

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
 *  Get list of list wise status
 *  For details go through Use-Case doc named 'ListForListWiseStatusReportActionService'
 */
class ListForListWiseStatusReportActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static final String LST_TASK = "lstTask"
    private static final String FAILURE_MESSAGE = "Failed to load task";
    private static final String GRID_OBJ = "gridObj";

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. Get task list
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
            long exhHouseId = Long.parseLong(params.exhHouseId)
            long taskListId = Long.parseLong(params.taskListId)
            Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(params.toDate)
            List<GroovyRowResult> listTask = listWiseStatus(exhHouseId, taskListId, fromDate, toDate)
            result.put(LST_TASK, listTask)
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
            List<GroovyRowResult> lstTask = (List<GroovyRowResult>) executeResult.get(LST_TASK)
            List lstWrappedTask = wrapTask(lstTask)
            Map gridObj = [rows: lstWrappedTask]
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
    private List wrapTask(List<GroovyRowResult> lstTask) {
        List lstWrappedTask = []
        int total = 0
        int totalAmount=0
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult task = lstTask[i]
            GridEntity obj = new GridEntity()
            obj.cell = [
                    task.task_status,
                    task.count,
                    task.total_amount
            ]
            total += Integer.parseInt(task.count.toString())
            totalAmount += Double.parseDouble(task.total_amount.toString())
            lstWrappedTask << obj
        }
        GridEntity footerObj = new GridEntity()
        footerObj.cell = [
                "<span style='font-weight: bold;'>Total</span>",
                "<span style='font-weight: bold;'>" + total + "</span>",
                "<span style='font-weight: bold;'>" + totalAmount + "</span>"
        ]
        lstWrappedTask << footerObj
        return lstWrappedTask
    }

    private static final String QUERY_STR =  """
        SELECT se.id, se.key task_status, COUNT(task.id) count, sum(task.amount) total_amount
        FROM rms_task task
            LEFT JOIN system_entity se ON se.id = task.current_status
        WHERE task.company_id = :companyId
            AND task.task_list_id = :taskListId
            AND task.created_on BETWEEN :fromDate AND :toDate
            AND task.exchange_house_id = :exchangeHouseId
        GROUP BY se.id, se.key
        ORDER BY se.id
    """
    /**
     * Get list of Task object
     * @param baseService
     * @return -list of Task list
     */
    private List<GroovyRowResult> listWiseStatus(long exhHouseId, long taskListId, Date fromDate, Date toDate) {
        Map queryParams = [
                exchangeHouseId: exhHouseId,
                taskListId: taskListId,
                companyId: rmsSessionUtil.appSessionUtil.getCompanyId(),
                fromDate: DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlToDateWithSeconds(toDate)
        ]
        List<GroovyRowResult> lstTasks = executeSelectSql(QUERY_STR, queryParams)
        return lstTasks
    }
}
