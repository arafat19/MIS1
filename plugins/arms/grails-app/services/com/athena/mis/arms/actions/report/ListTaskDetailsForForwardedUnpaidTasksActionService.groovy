package com.athena.mis.arms.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class ListTaskDetailsForForwardedUnpaidTasksActionService extends BaseService implements ActionIntf{

    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE="Task details could not found"
    private static final String BRANCH_ID="branchId"
    private static final String LST_TASKS="lstTasks"
    private static final String GRID_OBJ="gridObj"

    /**
     * GetSerialized parameters from UI
     * @param parameters-params from UI
     * @param obj-N/A
     * @return- a map containing all object necessary to execute
     */
    public Object executePreCondition(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            GrailsParameterMap parameterMap=(GrailsParameterMap) parameters
            if(!parameterMap.rp){
                parameterMap.rp=15
            }
            initPager(parameterMap)
            if(!parameterMap.branchId){
                result.put(Tools.MESSAGE,DEFAULT_ERROR_MESSAGE)
                return result
            }
            Long branchId= Long.parseLong(parameterMap.branchId)
            result.put(BRANCH_ID,branchId)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Get forwarded task lists group by bank & branch
     * @param parameters- parameters returned from executePreCondition
     * @param obj-N/A
     * @return- a map containing all object necessary for buildSuccessResult
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try{
            Map executeResult=(Map)parameters
            long branchId=(long)executeResult.get(BRANCH_ID)
            SystemEntity processForward=(SystemEntity)rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.FORWARD,rmsSessionUtil.appSessionUtil.getCompanyId())
            SystemEntity decisionApprove=(SystemEntity)rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED,rmsSessionUtil.appSessionUtil.getCompanyId())
            Map detailsResult=lstTaskDetailsForForward(processForward.id,decisionApprove.id,branchId,this)
            List<GroovyRowResult> lstTasks=(List<GroovyRowResult>)detailsResult.lstForwardedUnpaidTasks
            int count=(int)detailsResult.count
            result.put(LST_TASKS,lstTasks)
            result.put(Tools.COUNT,count)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    /**
     * Do nothing for executePostCondition
     */
    public Object executePostCondition(Object parameters, Object obj){
        return null
    }

    /**
     * Build gridObj for UI
     * @param obj-returned from execute
     * @return- a map containing all object necessary for UI
     */
    public Object buildSuccessResultForUI(Object obj){
        Map result= new LinkedHashMap()
        try{
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstForwardedTasks = (List<GroovyRowResult>) executeResult.get(LST_TASKS)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapForwardedTasks(lstForwardedTasks, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ,gridObj)
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }catch(Exception ex){
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            result.put(Tools.MESSAGE,DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure message for UI
     * @param obj- returned from previous method may be null
     * @return- failure message to indicate success event
     */
    public Object buildFailureResultForUI(Object obj){
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

    /**
     * Wrap forwarded tasks with details to show in UI
     * @param lstForwardedTasks-list of forwarded tasks with respect to bank,branch
     * @param start- baseService.start
     * @return- lstWrappedTask
     */
    private List wrapForwardedTasks(List<GroovyRowResult> lstForwardedTasks, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstForwardedTasks.size(); i++) {
            GroovyRowResult eachRow= lstForwardedTasks[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.ref_no,
                    eachRow.amount,
                    eachRow.beneficiary_name,
                    eachRow.instrument,
                    eachRow.payment_method,
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }
    private static final String LST_FOR_FORWARDED_TASK_DETAILS="""
        SELECT t.id,ref_no, amount, beneficiary_name, se.key instrument, s.key payment_method
        FROM rms_task t
        LEFT JOIN system_entity se ON t.instrument_type_id=se.id
        LEFT JOIN system_entity s ON t.payment_method=s.id
        WHERE t.mapping_branch_id=:branchId
        AND process_type_id=:processTypeId
        AND current_status=:currentStatus
        AND t.company_id=:companyId
        LIMIT :resultPerPage OFFSET :start
    """
    private static final String COUNT_FOR_FORWARDED_TASK_DETAILS="""
        SELECT COUNT(t.id)
        FROM rms_task t
        LEFT JOIN system_entity se ON t.instrument_type_id=se.id
        LEFT JOIN system_entity s ON t.payment_method=s.id
        WHERE t.mapping_branch_id=:branchId
        AND process_type_id=:processTypeId
        AND current_status=:currentStatus
        AND t.company_id=:companyId
    """
    /**
     * Get list of forwarded tasks
     * @param processTypeId- forward process
     * @param currentStatusId- decision approved status
     * @param branchId-mappingBranchId
     * @param baseService-BaseService
     * @return- a map containing list of tasks and count
     */
    private Map lstTaskDetailsForForward(long processTypeId,long currentStatusId,long branchId,BaseService baseService){
        long companyId=rmsSessionUtil.appSessionUtil.getCompanyId()
        Map queryParamsForLst=[
                processTypeId:processTypeId,
                currentStatus:currentStatusId,
                branchId:branchId,
                resultPerPage:baseService.resultPerPage,
                start:baseService.start,
                companyId:companyId
        ]
        List<GroovyRowResult> lstForwardedUnpaidTasks=(List<GroovyRowResult>)executeSelectSql(LST_FOR_FORWARDED_TASK_DETAILS,queryParamsForLst)
        Map queryParamsForCount=[
                processTypeId:processTypeId,
                currentStatus:currentStatusId,
                branchId:branchId,
                companyId:companyId
        ]
        List<GroovyRowResult> lstResult=(List<GroovyRowResult>)executeSelectSql(COUNT_FOR_FORWARDED_TASK_DETAILS,queryParamsForCount)
        int count=(int)lstResult[0][0]
        return [lstForwardedUnpaidTasks:lstForwardedUnpaidTasks,count:count]

    }
}
