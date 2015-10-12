package com.athena.mis.arms.actions.rmstask

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

/**
 *  Search specific list of Task
 *  For details go through Use-Case doc named 'SearchRmsTaskActionService'
 */
class SearchRmsTaskActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil
    @Autowired
    RmsTaskStatusCacheUtility rmsTaskStatusCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load task"
    private static final String LST_TASK = "lstTasks"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get Task list through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            initSearch(parameterMap)          // initialize parameters
            boolean isExhUser = false
            if(parameterMap.isExhUser) {
                isExhUser = true
            }
            Map searchResult = search(this, isExhUser)           // Search Task from DB
            List<GroovyRowResult> lstTasks = (List<GroovyRowResult>) searchResult.lstTasks
            Integer count = (Integer) searchResult.count
            result.put(LST_TASK, lstTasks)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap Task list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstTasks = (List<GroovyRowResult>) executeResult.get(LST_TASK)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedTask = wrapTask(lstTasks, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedTask]
            return output
        } catch (Exception ex) {
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
     * Wrap list of Task in grid entity
     * @param lstTasks -list of Task object(s)
     * @param start -starting index of the page
     * @return -list of wrapped Task
     */
    private List wrapTask(List<GroovyRowResult> lstTasks, int start) {
        List lstWrappedTask = []
        int counter = start + 1
        for (int i = 0; i < lstTasks.size(); i++) {
            GroovyRowResult groovyRowResult = lstTasks[i]
            GridEntity obj = new GridEntity()
            obj.id = groovyRowResult.id
            obj.cell = [
                    counter,
                    groovyRowResult.id,
                    groovyRowResult.ref_no,
                    groovyRowResult.amount,
                    DateUtility.getLongDateForUI(groovyRowResult.value_date),
                    groovyRowResult.beneficiary_name,
                    groovyRowResult.outlet,
                    groovyRowResult.payment_method,
                    DateUtility.getLongDateForUI(groovyRowResult.created_on),
                    groovyRowResult.exchange_house
            ]
            lstWrappedTask << obj
            counter++
        }
        return lstWrappedTask
    }

    /**
     * Get Task list and count of search through specific search
     * @param baseService
     * @return -a map containing Task list and search count
     */
    private Map search(BaseService baseService, boolean isExhUser) {
        long companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity newTaskObj = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.NEW_TASK, companyId)
        String strExh = Tools.EMPTY_SPACE
        String strStatus = "AND task.current_status = ${newTaskObj.id}"
        if(isExhUser) {
            SystemEntity pendingTaskObj = (SystemEntity) rmsTaskStatusCacheUtility.readByReservedAndCompany(RmsTaskStatusCacheUtility.PENDING_TASK, companyId)
            long exhId = rmsSessionUtil.getUserExchangeHouseId()
            strExh = "AND task.exchange_house_id = ${exhId}"
            strStatus = "AND task.current_status = ${pendingTaskObj.id}"
        }
        String searchQuery = """
            SELECT task.id, task.version, task.ref_no, task.amount, task.value_date, task.beneficiary_name,
                    task.outlet_bank || ', ' || task.outlet_branch || ', ' || task.outlet_district as outlet, payMethod.key payment_method,
                    task.created_on, eh.name exchange_house
            FROM rms_task task
                LEFT JOIN rms_exchange_house eh ON task.exchange_house_id = eh.id
                LEFT JOIN system_entity payMethod ON task.payment_method = payMethod.id
            WHERE task.task_list_id = 0
                AND task.company_id = :companyId
                AND ${baseService.queryType} ilike :query
                ${strExh} ${strStatus}
            ORDER BY ref_no
            LIMIT :resultPerPage OFFSET :start
        """
        String countQuery = """
            SELECT COUNT(task.id)
            FROM rms_task task
            WHERE task.task_list_id = 0
                AND task.company_id = :companyId
                AND ${baseService.queryType} ilike :query
                ${strExh} ${strStatus}
        """
        Map queryParams = [
                query: Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE,
                companyId: rmsSessionUtil.appSessionUtil.getCompanyId(),
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstTasks = executeSelectSql(searchQuery, queryParams)
        int count = (int) executeSelectSql(countQuery, queryParams).first().count

        return [lstTasks : lstTasks, count : count.toInteger()]
    }
}
