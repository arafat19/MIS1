package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class ListForViewCancelTaskActionService extends BaseService implements ActionIntf{

    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private Logger log=Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE="Task(s) list not found"
    private static final String FROM_DATE="fromDate"
    private static final String TO_DATE="toDate"
    private static final String LST_TASKS="lstTasks"
    private static final String GRID_OBJ="gridObj"

    /**
     * Get serialized parameters from UI
     * @param parameters-params
     * @param obj-N/A
     * @return-a map containing all object necessary for execute
     */
    public Object executePreCondition(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            GrailsParameterMap parameterMap=(GrailsParameterMap)parameters
            if(!parameterMap.rp){
                parameterMap.rp=15
            }
            initPager(parameterMap)
            Date fromDate=DateUtility.parseMaskedFromDate(parameterMap.fromDate)
            Date toDate=DateUtility.parseMaskedToDate(parameterMap.toDate)
            result.put(FROM_DATE,fromDate)
            result.put(TO_DATE,toDate)
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
     * Do nothing for executePostCondition
     */

    public  Object executePostCondition(Object parameters, Object obj){
        return null
    }
    /**
     * Get list of canceled tasks from rms_task
     * @param parameters-parameters from executePreCondition
     * @param obj-N/A
     * @return-a map containing all object necessary for buildSuccessForUI
     */
    @Transactional(readOnly = true)
    public  Object execute(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            Map executeResult=(Map) parameters
            Date fromDate=(Date)executeResult.get(FROM_DATE)
            Date toDate=(Date)executeResult.get(TO_DATE)
            Map lstResult=lstForCancelTasks(fromDate,toDate,this)
            List<GroovyRowResult>lstTasks=lstResult.lstTasks
            int count=lstResult.count
            result.put(LST_TASKS,lstTasks)
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
     * Build Success result for UI
     * Wrap obj gor grid
     * @param obj-obj returned from execute
     * @return-true/false and gridObj
     */
    public Object buildSuccessResultForUI(Object obj){
        Map result = new LinkedHashMap()
        try{
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstTasks = (List<GroovyRowResult>) executeResult.get(LST_TASKS)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapTask(lstTasks, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ,gridObj)
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
     * Build failure result for UI
     * @param obj-returned from previous method may be null
     * @return- failure message to indicate failure event
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
     * Wrap task for gridObj
     * @param lstTask- task list
     * @param start- baseService start
     * @return-wrapped obj
     */
    private List wrapTask(List<GroovyRowResult> lstTask, int start) {
        List lstWrappedRmsTask = []
        int counter = start + 1
        for (int i = 0; i < lstTask.size(); i++) {
            GroovyRowResult eachRow = lstTask[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.ref_no,
                    eachRow.mapping_bank_branch_district_info,
                    eachRow.process,
                    eachRow.instrument,
                    eachRow.payment_method,
                    eachRow.amount,
                    eachRow.beneficiary_name,
                    eachRow.revision_note

            ]
            lstWrappedRmsTask << obj
            counter++
        }
        return lstWrappedRmsTask
    }

    private static final String QUERY_LIST_FOR_CANCEL_TASKS="""
        SELECT t.id,t.ref_no, b.name||' ,'|| br.name||' ,'|| d.name mapping_bank_branch_district_info,t.revision_note,
        se.key process, s.key instrument, sys.key payment_method, t.amount,t.beneficiary_name
        FROM rms_task t LEFT JOIN bank b ON t.mapping_bank_id=b.id
            LEFT JOIN bank_branch br ON t.mapping_branch_id=br.id
            LEFT JOIN district d ON t.mapping_district_id=d.id
            LEFT JOIN system_entity se ON t.process_type_id=se.id
            LEFT JOIN system_entity s ON t.instrument_type_id=s.id
            LEFT JOIN system_entity sys ON t.payment_method=sys.id
        WHERE t.current_status=:currentStatus
        AND t.created_on BETWEEN :fromDate AND :toDate
        AND t.company_id=:companyId
        ORDER BY t.id asc
        LIMIT :resultPerPage OFFSET :start
    """
    private static final COUNT_QUERY_FOR_CANCEL_TASKS="""
        SELECT COUNT(t.id)
        FROM rms_task t LEFT JOIN bank b ON t.mapping_bank_id=b.id
            LEFT JOIN bank_branch br ON t.mapping_branch_id=br.id
            LEFT JOIN district d ON t.mapping_district_id=d.id
            LEFT JOIN system_entity se ON t.process_type_id=se.id
            LEFT JOIN system_entity s ON t.instrument_type_id=s.id
            LEFT JOIN system_entity sys ON t.payment_method=sys.id
        WHERE t.current_status=:currentStatus
        AND t.created_on BETWEEN :fromDate AND :toDate
        AND t.company_id=:companyId
    """

    /**
     * Get all canceled task lists
     * @param fromDate-start date
     * @param toDate-end date
     * @param baseService- baseService
     * @return- a map containing lstTasks and count
     */
    private Map lstForCancelTasks(Date fromDate, Date toDate, BaseService baseService){

        long companyId=rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity cancelTask= (SystemEntity)rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.CANCELED,companyId)
        Map queryParamsForLst=[
                fromDate:DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate:DateUtility.getSqlToDateWithSeconds(toDate),
                currentStatus:cancelTask.id,
                resultPerPage:baseService.resultPerPage,
                start:baseService.start,
                companyId:companyId
        ]
        List<GroovyRowResult> lstTasks=executeSelectSql(QUERY_LIST_FOR_CANCEL_TASKS,queryParamsForLst)
        Map queryParamsForCount=[
                fromDate:DateUtility.getSqlFromDateWithSeconds(fromDate),
                toDate:DateUtility.getSqlToDateWithSeconds(toDate),
                currentStatus:cancelTask.id,
                companyId: companyId
        ]
        List<GroovyRowResult> lstResults=executeSelectSql(COUNT_QUERY_FOR_CANCEL_TASKS,queryParamsForCount)
        int count=(int)lstResults[0][0]
        Map results=[lstTasks:lstTasks,count:count]
        return results
    }
}
