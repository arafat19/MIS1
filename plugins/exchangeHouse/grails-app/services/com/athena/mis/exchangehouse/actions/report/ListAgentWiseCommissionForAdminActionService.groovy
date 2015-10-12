package com.athena.mis.exchangehouse.actions.report

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.exchangehouse.utility.ExhTaskTypeCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show list of agent wise commission for admin.
 * For details go through Use-Case doc named 'ListAgentWiseCommissionForAdminActionService'
 */
class ListAgentWiseCommissionForAdminActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_FAILURE_MSG = "Failed to load agent commission summary report"
    private static final String FROM_DATE_NOT_AVAILABLE = "From date is not available"
    private static final String TO_DATE_NOT_AVAILABLE = "To date is not available"
    private static final String AGENT_TASK_SUMMARY = "agentTaskSummary"

    @Autowired
    ExhTaskTypeCacheUtility exhTaskTypeCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * Check necessary parameters
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!(DateUtility.parseMaskedDate(parameterMap.createdDateFrom))) {
                result.put(Tools.MESSAGE, FROM_DATE_NOT_AVAILABLE)
                return result
            }
            if (!(DateUtility.parseMaskedDate(parameterMap.createdDateTo))) {
                result.put(Tools.MESSAGE, TO_DATE_NOT_AVAILABLE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get list of commission summary for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            Date startDate = DateUtility.parseMaskedDate(parameterMap.createdDateFrom)
            Date endDate = DateUtility.parseMaskedDate(parameterMap.createdDateTo)
            long agentId = Long.parseLong(parameterMap.agentId.toString())
            Map serviceReturn = (Map) getAgentWiseCommissionList(startDate, endDate, agentId)    // get commission summary between dates
            result.put(AGENT_TASK_SUMMARY, serviceReturn)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap commission summary list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            Map agentTaskSummary = (Map) executeResult.get(AGENT_TASK_SUMMARY)
            List<GroovyRowResult> wrappedSummary = wrapAgentCommissionSummary(agentTaskSummary.listSummary, start)
            result = [page: pageNumber, total: agentTaskSummary.count, rows: wrappedSummary]
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * Wrap list of tasks result in grid entity
     * @param lstSummary
     * @param start -starting index of the page
     * @return -list of wrapped tasks
     */
    private List<GroovyRowResult> wrapAgentCommissionSummary(List<GroovyRowResult> lstSummary, int start) {
        List<GroovyRowResult> tasks = []
        String created_on_str
        int counter = start + 1
        for (int i = 0; i < lstSummary.size(); i++) {
            LinkedHashMap task = (LinkedHashMap) lstSummary[i]
            GridEntity obj = new GridEntity()
            obj.id = task.id
            created_on_str = DateUtility.getDateFormatAsString(task.task_created_on)
            obj.cell = [
                    counter,
                    created_on_str,
                    task.count,
                    task.total_amount ? Tools.formatAmountWithoutCurrency(task.total_amount) : 0,
                    task.total_regular_fee ? Tools.formatAmountWithoutCurrency(task.total_regular_fee) : 0,
                    task.total_commission ? Tools.formatAmountWithoutCurrency(task.total_commission) : 0,
                    task.total_discount ? Tools.formatAmountWithoutCurrency(task.total_discount) : 0,
                    task.net_commission ? Tools.formatAmountWithoutCurrency(task.net_commission) : 0,
            ]
            tasks << obj
            counter++
        }
        return tasks
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.message)
            } else {
                result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            }

            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_FAILURE_MSG)
            return result
        }
    }

    /**
     * get commission summary of task between date by agentId
     */
    private LinkedHashMap getAgentWiseCommissionList(Date fromDate, Date toDate, long agentId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity agentTaskObj = (SystemEntity) exhTaskTypeCacheUtility.readByReservedAndCompany(exhTaskTypeCacheUtility.TYPE_AGENT_TASK, companyId)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, companyId)
        SystemEntity exhSentToBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_BANK, companyId)
        SystemEntity exhSentToOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK, companyId)
        SystemEntity exhResolvedByOtherBankSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK, companyId)

        String result_query = """
                SELECT CAST(task.created_on AS date) as task_created_on, count(task.id) as count,
                COALESCE(sum(task.amount_in_local_currency),0) as total_amount,
                COALESCE(sum(task.regular_fee),0) as total_regular_fee,
                COALESCE(sum(task.commission),0) as total_commission,
                COALESCE(sum(task.discount),0) as total_discount,
                COALESCE(sum(task.commission),0) - COALESCE(sum(task.discount),0) as net_commission
            FROM
                 exh_task task
            WHERE task.agent_id=${agentId}
                AND task.task_type_id = ${agentTaskObj.id}
                AND task.current_status IN(${exhNewTaskSysEntityObject.id},
                                    ${exhResolvedByOtherBankSysEntityObject.id},
                                    ${exhSentToBankSysEntityObject.id},
                                    ${exhSentToOtherBankSysEntityObject.id})
                AND task.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}'
                GROUP BY task_created_on
                ORDER BY task_created_on asc LIMIT ${resultPerPage} OFFSET ${start}
        """

        String queryCount = """
               SELECT  COUNT(DISTINCT CAST(task.created_on as date))  AS count
            FROM
                 exh_task task
            WHERE task.agent_id=${agentId}
                AND task.task_type_id = ${agentTaskObj.id}
                AND task.current_status IN(${exhNewTaskSysEntityObject.id},
                                    ${exhResolvedByOtherBankSysEntityObject.id},
                                    ${exhSentToBankSysEntityObject.id},
                                    ${exhSentToOtherBankSysEntityObject.id})
                AND task.created_on BETWEEN '${DateUtility.getFromDateWithSecond(fromDate)}' AND '${DateUtility.getToDateWithSecond(toDate)}'

        """


        List<GroovyRowResult> resultSummary = executeSelectSql(result_query)
        List<GroovyRowResult> resultCount = executeSelectSql(queryCount)
        int count = resultCount[0].count
        return [count: count, listSummary: resultSummary]
    }
}
