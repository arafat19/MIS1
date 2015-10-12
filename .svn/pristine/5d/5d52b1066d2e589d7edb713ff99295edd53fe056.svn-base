package com.athena.mis.arms.actions.rmstasklist

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsTaskList
import com.athena.mis.arms.service.RmsTaskListService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class RenameTaskListActionService extends BaseService implements ActionIntf{

    RmsTaskListService rmsTaskListService

    private final Logger log = Logger.getLogger(getClass())

    private static final String TASK_LIST_RENAME_FAILURE_MESSAGE="Task list is not renamed"
    private static final String ERROR_FOR_INVALID_INPUT="Error occurred for invalid input"
    private static final String LIST_RENAME_SUCCESS_MESSAGE="Task list has been renamed successfully"
    private static final String LIST_ALREADY_EXISTS="List name already exists"
    private static final String TASK_LIST_NAME="taskListName"
    private static final String RMS_TASK_LIST="rmsTaskList"
    private static final String TRANSACTION_FOR_THIS_LIST_IS_CLOSED = "List is not renamed."+"\n"+"Transaction day of this list is closed."

    /**
     * Get serialized parameters from UI
     * @param parameters-params
     * @param obj-N/A
     * @return-a map containing all object necessary for execute
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            GrailsParameterMap parameterMap=(GrailsParameterMap) parameters
            if(!parameterMap.taskListId){
                result.put(Tools.MESSAGE, ERROR_FOR_INVALID_INPUT)
                return result
            }
            if(!parameterMap.taskListName){
                result.put(Tools.MESSAGE, ERROR_FOR_INVALID_INPUT)
                return result
            }
            long taskListId= Long.parseLong(parameterMap.taskListId)
            RmsTaskList rmsTaskList= rmsTaskListService.read(taskListId)
            boolean isTransactionDayClose=false
            if(rmsTaskList){
                isTransactionDayClose=checkTransactionDayClose(rmsTaskList)
            }
            if(isTransactionDayClose){
                result.put(Tools.MESSAGE,TRANSACTION_FOR_THIS_LIST_IS_CLOSED)
                return result
            }
            String taskListName=parameterMap.taskListName
            RmsTaskList taskList=(RmsTaskList)rmsTaskListService.findByNameIlike(taskListName)
            if(taskList && (taskList.id!=taskListId)){
                result.put(Tools.MESSAGE,LIST_ALREADY_EXISTS)
                return result
            }
            result.put(TASK_LIST_NAME,taskListName)
            result.put(RMS_TASK_LIST,rmsTaskList)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, TASK_LIST_RENAME_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    /**
     * Do nothing for execute postCondition
     */

    public Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * update rmsTaskList name
     * @param parameters-parameters from executePreCondition
     * @param obj-N/A
     * @return-true/false based on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            Map executeResult=(Map)parameters
            String listName=(String)executeResult.get(TASK_LIST_NAME)
            RmsTaskList rmsTaskList=(RmsTaskList)executeResult.get(RMS_TASK_LIST)
            rmsTaskListService.updateForRenameList(listName,rmsTaskList)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch(Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE,TASK_LIST_RENAME_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            return result
        }
    }

    /**
     * Build success result
     * @param obj-null
     * @return-success message to indicate success event
     */
    public  Object buildSuccessResultForUI(Object obj){
        Map result=new LinkedHashMap()
        result.put(Tools.MESSAGE,LIST_RENAME_SUCCESS_MESSAGE)
        result.put(Tools.IS_ERROR,Boolean.FALSE)
        return result
    }

    /**
     * Build failure message
     * @param obj-N/A
     * @return-failure message to indicate failure event
     */
    public  Object buildFailureResultForUI(Object obj){
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
            result.put(Tools.MESSAGE, TASK_LIST_RENAME_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, TASK_LIST_RENAME_FAILURE_MESSAGE)
            return result
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
