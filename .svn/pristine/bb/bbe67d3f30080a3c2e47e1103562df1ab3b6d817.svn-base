package com.athena.mis.arms.actions.report


import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.arms.service.RmsTaskService
import com.athena.mis.arms.utility.RmsProcessTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired


class ListTaskForForwardUnpaidTaskActionService extends BaseService implements ActionIntf{

    private final Logger log = Logger.getLogger(getClass())

    RmsTaskService rmsTaskService
    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility
    @Autowired
    RmsProcessTypeCacheUtility rmsProcessTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE="Task could not found"
    private static final String FROM_DATE ="formDate"
    private static final String TO_DATE="toDate"
    private static final String LST_FORWARDED_TASKS ="lstForwardedTasks"
    private static final String GRID_OBJ="gridObj"


    /**
     * Get serialized parameters From UI
     * @param parameters- parameters from UI
     * @param obj-N/A
     * @return true/false
     */

    public  Object executePreCondition(Object parameters, Object obj){
        Map result = new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap= (GrailsParameterMap) parameters
            if(!parameterMap.rp){
                parameterMap.rp=15
            }
            initPager(parameterMap)
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate)
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate)
            result.put(FROM_DATE,fromDate)
            result.put(TO_DATE,toDate)
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
     * Do nothing for executePost condition
     */
    public  Object executePostCondition(Object parameters, Object obj){
            return null
    }
    /**
     * Search task details based createdOn
     * @param parameters- parameters from executePreCondition
     * @param obj-N/A
     * @return task entity and true/false
     */

    @Transactional(readOnly = true)
    public  Object execute(Object parameters, Object obj){
        Map result = new LinkedHashMap()
        try{
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            Map executeResult= (Map) parameters
            Date fromDate= (Date)executeResult.get(FROM_DATE)
            Date toDate= (Date)executeResult.get(TO_DATE)
            long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity currentStatus= (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(rmsTaskStatusCacheUtility.DECISION_APPROVED, companyId)
            long currentStatusId= currentStatus.id
            SystemEntity forwardProcess= (SystemEntity)rmsProcessTypeCacheUtility.readByReservedAndCompany(rmsProcessTypeCacheUtility.FORWARD, companyId)
            long processTypeId= forwardProcess.id
            Map searchResult= listForForwardedUnpaidTask(currentStatusId,processTypeId,fromDate,toDate,this)
            List<GroovyRowResult> lstForwardedTasks= searchResult.lstForwardedUnpaidTasks
            int count=searchResult.count
            result.put(LST_FORWARDED_TASKS,lstForwardedTasks)
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
     * Wrap rmsTask list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public  Object buildSuccessResultForUI(Object obj){
        Map result = new LinkedHashMap()
        try{
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstForwardedTasks = (List<GroovyRowResult>) executeResult.get(LST_FORWARDED_TASKS)
            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapForwardedTasks(lstForwardedTasks, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedTask]
            result.put(GRID_OBJ,gridObj)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
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
     * Wrap list of lstRmsTask in grid entity
     * @param lstRmsTask -list of rmsTask object(s)
     * @param start -starting index of the page
     * @return -list of wrapped rmsTask
     */
    private List wrapForwardedTasks(List<GroovyRowResult> lstForwardedTasks, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstForwardedTasks.size(); i++) {
            GroovyRowResult eachRow= lstForwardedTasks[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.branch_id
            obj.cell = [
                    counter,
                    eachRow.mapping_bank_branch_and_district_info,
                    eachRow.total_task,
                    eachRow.total_amount
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }
    private static final String LST_FOR_FORWARDED_UNPAID_TASKS="""
        SELECT b.name||','|| br.name||','||d.name mapping_bank_branch_and_district_info,
        COUNT(t.id) total_task,SUM(t.amount) total_amount, t.mapping_branch_id branch_id
        FROM rms_task t
            LEFT JOIN bank b ON t.mapping_bank_id=b.id
            LEFT JOIN bank_branch br ON t.mapping_branch_id=br.id
            LEFT JOIN district d ON t.mapping_district_id=d.id
        WHERE t.process_type_id=:processTypeId
        AND current_status=:currentStatus
        AND t.created_on BETWEEN :fromDate AND :toDate
        AND t.company_id=:companyId
        GROUP BY b.name, br.name,d.name ,t.mapping_branch_id
        LIMIT :resultPerPage OFFSET :start
    """
    private static final String COUNT_FOR_FORWARDED_UNPAID_TASKS="""
        SELECT COUNT(t.id)FROM rms_task t
            LEFT JOIN bank b ON t.mapping_bank_id=b.id
            LEFT JOIN bank_branch br ON t.mapping_branch_id=br.id
            LEFT JOIN district d ON t.mapping_district_id=d.id
        WHERE t.process_type_id=:processTypeId
        AND current_status=:currentStatus
        AND t.created_on BETWEEN :fromDate AND :toDate
        AND t.company_id=:companyId
    """
    private Map listForForwardedUnpaidTask(long currentStatusId,long processTypeId,Date fromDate,Date toDate,BaseService baseService){
            long companyId=rmsSessionUtil.appSessionUtil.getCompanyId()
            Map queryParamsForLst=[
                    processTypeId:processTypeId,
                    currentStatus:currentStatusId,
                    fromDate:DateUtility.getSqlFromDateWithSeconds(fromDate),
                    toDate:DateUtility.getSqlToDateWithSeconds(toDate),
                    resultPerPage:baseService.resultPerPage,
                    start:baseService.start,
                    companyId:companyId
            ]
            List<GroovyRowResult> lstForwardedUnpaidTasks=(List<GroovyRowResult>)executeSelectSql(LST_FOR_FORWARDED_UNPAID_TASKS,queryParamsForLst)
            Map queryParamsForCount=[
                    processTypeId:processTypeId,
                    currentStatus:currentStatusId,
                    fromDate:DateUtility.getSqlFromDateWithSeconds(fromDate),
                    toDate:DateUtility.getSqlToDateWithSeconds(toDate),
                    companyId:companyId
            ]
            List<GroovyRowResult> lstResult=(List<GroovyRowResult>)executeSelectSql(COUNT_FOR_FORWARDED_UNPAID_TASKS,queryParamsForCount)
            int count=(int)lstResult[0][0]
            return [lstForwardedUnpaidTasks:lstForwardedUnpaidTasks,count:count]
    }
}
