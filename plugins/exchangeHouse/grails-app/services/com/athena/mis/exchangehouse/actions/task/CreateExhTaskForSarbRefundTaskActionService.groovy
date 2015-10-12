package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhTaskService
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * Create new exh task for sarb refund task
 * for details go through use-case named "CreateExhTaskForSarbRefundTaskActionService"
 */
class CreateExhTaskForSarbRefundTaskActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    ExhTaskService exhTaskService

    private static final String AMOUNT_EXCEED = "Refund amount exceeds task amount"
    private static final String SUCCESS_MSG = "Refund amount created successfully"
    private static final String FAILURE_MSG = "Failed to create refund amount"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Check if total refund amount exceeds task amount and create new task
     * @param parameters - taskId, refundAmount
     * @param obj
     * @return
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map params = (Map) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            long taskId = (long) params.taskId
            double refundAmount = (double) params.refundAmount
            ExhTask task = exhTaskService.read(taskId)
            String msg = checkTaskAmount(task, refundAmount)
            if (msg) {
                result.put(Tools.MESSAGE, msg)
                return result
            }
            ExhTask newTask = buildNewExhTask(task, refundAmount)
            exhTaskService.create(newTask)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * check if refund amount exceed task amount
     */
    private String checkTaskAmount(ExhTask task, double refundAmount) {
        String query = """
            select COALESCE(sum(amount_in_local_currency), 0) as amount from exh_task where refund_task_id=:taskId
        """
        Map queryParam = [taskId: task.id]
        List result = executeSelectSql(query, queryParam)
        double amount = result[0].amount
        if (amount + refundAmount > task.amountInLocalCurrency) {
            return AMOUNT_EXCEED
        }
        return null
    }

    /**
     * build new exh task for refund task
     */
    private ExhTask buildNewExhTask(ExhTask task, double refundAmount) {
        ExhTask newTask = new ExhTask()
        SystemEntity refundTaskStatus = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(ExhTaskStatusCacheUtility.STATUS_REFUND_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        newTask.properties = task.properties
        newTask.version = 0
        newTask.refundTaskId = task.id
        newTask.amountInLocalCurrency = refundAmount
        newTask.amountInForeignCurrency = refundAmount * task.conversionRate
        newTask.currentStatus = refundTaskStatus.id
        newTask.createdOn = new Date()
        newTask.userId = exhSessionUtil.appSessionUtil.appUser.id
        newTask.exhGain = 0.0d
        return newTask
    }
}
