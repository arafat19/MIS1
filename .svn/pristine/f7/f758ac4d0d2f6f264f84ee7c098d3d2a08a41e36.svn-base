package com.athena.mis.arms.actions.rmstasklist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class ListForManageTaskListActionService extends BaseService implements ActionIntf{

    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE="Task(s) not found"
    private static final String LST_TASK ="lstTask"
    private static final String GRID_OBJ ="gridObj"

    /**
     * Do nothing for execute pre condition
     */
    public Object executePreCondition(Object parameters, Object obj){
       return null
    }

    /**
     * Do nothing for execute post condition
     */
    public  Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * Ger serialized parameters from UI
     * @param parameters-params
     * @param obj-N/A
     * Search task list based on parameters
     * @return task list and count
     */
    @Transactional(readOnly = true)
    public  Object execute(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            GrailsParameterMap parameterMap=(GrailsParameterMap) parameters
            initPager(parameterMap)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate)
            long currentStatus= Long.parseLong(parameterMap.currentStatus)
            long exchangeHouseId= Long.parseLong(parameterMap.exchangeHouseId)
            long taskListId= Long.parseLong(parameterMap.taskListId)
            Map searchResult= (Map) searchTaskForManageTaskList(currentStatus,exchangeHouseId,taskListId,fromDate,toDate,this)
            List<GroovyRowResult> lstTask= searchResult.lstOfTasks
            int count= searchResult.count
            result.put(LST_TASK,lstTask)
            result.put(Tools.COUNT,count)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Wrap task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstTaskList = (List<GroovyRowResult>) executeResult.get(LST_TASK)
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
     * @return -a map containing isError = true & relevant error message to display on page load
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
     * @param lstTask -list of tasks object(s)
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */

    private List wrapTaskList(List<GroovyRowResult> lstTask, int start) {
        List lstWrappedTaskList = []
        int counter = start + 1
            for (int i = 0; i < lstTask.size(); i++) {
                String mappingDecision= Tools.EMPTY_SPACE
                SystemEntity process= (SystemEntity) rmsProcessTypeCacheUtility.read(lstTask[i].process_type_id)
                SystemEntity instrument= (SystemEntity) rmsInstrumentTypeCacheUtility.read(lstTask[i].instrument_type_id)
                if(process){
                   mappingDecision =process.value +" ,"+instrument.value
                }
            GridEntity obj = new GridEntity()
            obj.id = lstTask[i].id
            obj.cell = [
                    counter,
                    lstTask[i].ref_no,
                    lstTask[i].account_no,
                    lstTask[i].beneficiary_name,
                    lstTask[i].amount,
                    lstTask[i].mapping_bank_info,
                    mappingDecision
            ]
            lstWrappedTaskList << obj
            counter++
        }
        return lstWrappedTaskList
    }



    private static final String QUERY_FOR_MANAGE_LIST="""
            SELECT task.id, task.ref_no,task.beneficiary_name, task.amount,task.account_no,
            bank_branch.name ||', '|| bank.name ||', ' ||district.name mapping_bank_info,
            task.process_type_id, task.instrument_type_id
            FROM rms_task_list list
            LEFT JOIN rms_task task ON task.task_list_id=list.id
            LEFT JOIN rms_exchange_house exh ON task.exchange_house_id = exh.id
            LEFT JOIN bank ON task.mapping_bank_id= bank.id
            LEFT JOIN bank_branch ON task.mapping_branch_id= bank_branch.id
            LEFT JOIN district ON task.mapping_district_id= district.id
            WHERE task.current_status=:currentStatus
            AND list.exchange_house_id=:exchangeHouseId
            AND task.task_list_id=:taskListId
            AND task.created_on BETWEEN :fromDate AND :toDate
            limit :resultPerPage
            OFFSET :start
    """
    private static final String QUERY_FOR_COUNT_MANAGE_LIST="""
            SELECT COUNT(task.id)
            FROM rms_task_list list
            LEFT JOIN rms_task task ON task.task_list_id=list.id
            LEFT JOIN rms_exchange_house exh ON task.exchange_house_id = exh.id
            WHERE task.current_status=:currentStatus
            AND task.created_on BETWEEN :fromDate AND :toDate
            AND list.exchange_house_id=:exchangeHouseId
            AND task.task_list_id=:taskListId
    """

    /**
     * Search task list
     * count total no of tasks
     * @param currentStatus- currentStatus of rmsTask
     * @param exchangeHouseId - exchange house id of rmsTaskList
     * @param taskListId -taskListId of rmsTask
     * @param fromDate -create date
     * @param toDate -create date
     * @param baseService- BasesService
     * @return task list and count
     */
    private Map searchTaskForManageTaskList(long currentStatus,long exchangeHouseId,long taskListId,Date fromDate, Date toDate, BaseService baseService){
        Map queryParams=[
                currentStatus :currentStatus,
                exchangeHouseId :exchangeHouseId,
                taskListId:taskListId,
                resultPerPage:baseService.resultPerPage,
                start: baseService.start,
                fromDate:DateUtility.getSqlDateWithSeconds(fromDate),
                toDate:DateUtility.getSqlDateWithSeconds(toDate)

        ]
        List<GroovyRowResult> lstOfTasks = (List<GroovyRowResult>) executeSelectSql(QUERY_FOR_MANAGE_LIST,queryParams)

        Map queryParamsCount=[
                currentStatus :currentStatus,
                exchangeHouseId :exchangeHouseId,
                taskListId:taskListId,
                fromDate:DateUtility.getSqlDateWithSeconds(fromDate),
                toDate:DateUtility.getSqlDateWithSeconds(toDate)

        ]
        List<GroovyRowResult> result=executeSelectSql(QUERY_FOR_COUNT_MANAGE_LIST,queryParamsCount)
        int count= (int) result[0][0]

        return [lstOfTasks:lstOfTasks, count:count]
    }
}
