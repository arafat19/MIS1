package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting
import com.athena.mis.exchangehouse.entity.ExhTask
import com.athena.mis.exchangehouse.service.ExhAgentCurrencyPostingService
import com.athena.mis.exchangehouse.service.ExhAgentService
import com.athena.mis.exchangehouse.service.ExhTaskTraceService
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Send task to Exchange House for Agent
 * For details go through Use-Case doc named 'ExhSendTaskToExchangeHouseActionService'
 */
class ExhSendTaskToExchangeHouseActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SENT_TO_EXCHANGE_HOUSE_SUCCESS = "Task(s) successfully sent to exchange house"
    private static final String SENT_TO_EXCHANGE_HOUSE_FAILURE = "Task(s) sent to exchange house failed"
    private static final String INSUFFICIENT_BALANCE_ERROR = "Insufficient balance in agent's account"
    private static final String CREDIT_LIMIT_CONFIRMATION_MESSAGE = "Task amount exceeds available balance. \nDo you want to use available credit limit "
    private static final String TASK_NOT_FOUND_ERROR = "Task not found. Refresh grid and try again"
    private static final String LST_TASKS = 'lstTasks'
    private static final String TOTAL = 'total'
    private static final String AGENT = 'agent'
    private static final String SUCCESS = "success"
    private static String IS_CONFIRMATION_ISSUE = "isConfirmationIssue"


    ExhTaskTraceService exhTaskTraceService
    ExhAgentCurrencyPostingService exhAgentCurrencyPostingService
    ExhAgentService exhAgentService

    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhTaskStatusCacheUtility exhTaskStatusCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility

    /**
     * Get parameters from UI and check pre condition
     * 1. pull list of task by ids
     * 2. get total amount of local currency in pulled list of task required parameters
     * 3. pull list of task(s) by id
     * 4. pull agent from cache utility and check balance, if insufficient balance return and show message
     * 5. check confirmation issues
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {

        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap)params
            result.put(SUCCESS, Boolean.FALSE)
            List ids = parameterMap.ids.split(Tools.UNDERSCORE)
            List<Long> lstTaskIds = []

            // Get List of long IDs
            for (int i = 0; i < ids.size(); i++) {
                lstTaskIds << Long.parseLong(ids[i].toString())
            }

            List<ExhTask> lstTasks = readForSentToExHouse(lstTaskIds)       // get list of task(s)
            if (lstTasks.size() != lstTaskIds.size()) {
                result.put(Tools.MESSAGE, TASK_NOT_FOUND_ERROR)
                return result
            }

            double totalAmount = getTotal(lstTasks)

            long agentId = lstTasks.first().agentId
            ExhAgent agent = (ExhAgent) exhAgentCacheUtility.read(agentId)
            double totBalance = agent.balance + agent.creditLimit
            if (totalAmount > totBalance) {
                result.put(Tools.MESSAGE, INSUFFICIENT_BALANCE_ERROR)
                return result
            }

            boolean isConfirmed = Boolean.parseBoolean(parameterMap.isConfirmed.toString())

            if ((totalAmount > agent.balance) && (!isConfirmed)) {                 // check agent balance and confirmation
                Currency currency = (Currency) currencyCacheUtility.read(agent.currencyId)
                String message = CREDIT_LIMIT_CONFIRMATION_MESSAGE + totBalance.toString() + Tools.SINGLE_SPACE + currency.symbol + Tools.SINGLE_SPACE + Tools.QUESTION_SIGN
                result.put(IS_CONFIRMATION_ISSUE, Boolean.TRUE)
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.MESSAGE, message)
                return result
            }

            result.put(SUCCESS, Boolean.TRUE)
            result.put(LST_TASKS, lstTasks)
            result.put(TOTAL, totalAmount)
            result.put(AGENT, agent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            return result
        }
    }

    /**
     * Get total amount in local currency of task(s) e.g AUD, GBP
     * @param lstTasks - list of task
     * @return total - sum of amountInLocalCurrency and regular fee
     */
    private double getTotal(List<ExhTask> lstTasks) {
        double total = 0
        for (lstTask in lstTasks) {
            total = total + lstTask.amountInLocalCurrency + lstTask.regularFee
        }
        return total
    }

    /**
     * execute following activities
     * 1. update one or more task through its status
     * 2. save agent currency posting
     * 3. Save task trace into DB
     * 4. update agent balance
     * 5. at last update agent cache utility
     * This method is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map objMap = (Map) obj             // cast map returned from previous method
            List<ExhTask> lstTasks = (List<ExhTask>) objMap.get(LST_TASKS)
            double total = (double) objMap.get(TOTAL)
            ExhAgent agent = (ExhAgent) objMap.get(AGENT)

            processExchangeHouseTask(lstTasks)       // update agent's tasks' status from STATUS_PENDING_TASK to NEW_TASK

            updateBalanceForAgent(agent, total)       // update balance of Agent
            exhAgentCacheUtility.update(agent, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
            result.put(SUCCESS, Boolean.TRUE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(SENT_TO_EXCHANGE_HOUSE_FAILURE)
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            return result
        }
    }

    /**
     *  do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show success message
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_SUCCESS)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(SUCCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                if (receiveResult.get(IS_CONFIRMATION_ISSUE)) {
                    result.put(IS_CONFIRMATION_ISSUE, Boolean.TRUE)
                    result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
                    result.put(Tools.IS_ERROR, Boolean.FALSE)
                    return result
                }
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SENT_TO_EXCHANGE_HOUSE_FAILURE)
            return result
        }
    }

    /**
     * 1. Set task status with NEW
     * 2. Build & Save agent currency posting into DB
     * 3. Save task trace into DB
     * @param lstTasks -list of task
     */
    private void processExchangeHouseTask(List<ExhTask> lstTasks) {
        updateForSentToExchangeHouse(lstTasks)
        for (int i = 0; i < lstTasks.size(); i++) {
            ExhTask task = lstTasks[i]
            SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
            task.currentStatus = exhNewTaskSysEntityObject.id           // set task status as NEW
            exhAgentCurrencyPostingService.create(buildAgentCurrencyPosting(task))    // build & save currency posting
            exhTaskTraceService.create(task, new Date(), Tools.ACTION_UPDATE)
        }
    }

    /**
     * Get task(s) of pending by its ids
     * @param taskIds -list of taskId
     * @return lstTasks -list of task
     */
    private List<ExhTask> readForSentToExHouse(List<Long> taskIds) {
        long companyId = exhSessionUtil.appSessionUtil.getAppUser().companyId
        SystemEntity exhPendingTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_PENDING_TASK, companyId)

        List<ExhTask> lstTasks = ExhTask.findAllByIdInListAndCurrentStatus(
                taskIds,
                exhPendingTaskSysEntityObject.id,
                [readOnly: true]
        )
        return lstTasks
    }

    /**
     * Update task task status as NEW
     * @param lstTasks
     * @return updateCount
     */
    private Integer updateForSentToExchangeHouse(List<ExhTask> lstTasks) {
        List<Long> lstIds = lstTasks.collect { it.id }
        String strIds = Tools.buildCommaSeparatedStringOfIds(lstIds)
        SystemEntity exhNewTaskSysEntityObject = (SystemEntity) exhTaskStatusCacheUtility.readByReservedAndCompany(exhTaskStatusCacheUtility.STATUS_NEW_TASK, exhSessionUtil.appSessionUtil.getCompanyId())
        String query = """
                UPDATE exh_task SET
                version = version+1,
                current_status = :statusNew
                WHERE id IN (${strIds})
            """
        Map queryParams = [statusNew: exhNewTaskSysEntityObject.id]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Task")
        }
        return (new Integer(updateCount))
    }

    /**
     * Update Agent balance
     * @param agent - ExhAgent object
     * @param total -
     */
    private void updateBalanceForAgent(ExhAgent agent, double total) {
        String query = """
            UPDATE exh_agent
            SET
                version=:newVersion,
                balance=:newBalance
            WHERE
                id=:id AND
                version=:version AND
                balance=:balance
        """

        double newBalance = agent.balance - total
        newBalance = newBalance.round(2)
        Map queryParams = [
                id: agent.id,
                version: agent.version,
                newVersion: agent.version + 1,
                newBalance: newBalance,
                balance: agent.balance
        ]

        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating agent information')
        }
        agent.version = agent.version + 1
        agent.balance = newBalance
    }

    /**
     * Build ExhAgentCurrencyPosting
     * @param task - ExhTask object
     * @return exhAgentCurrencyPosting -ExhAgentCurrencyPosting object
     */
    private ExhAgentCurrencyPosting buildAgentCurrencyPosting(ExhTask task) {
        ExhAgentCurrencyPosting exhAgentCurrencyPosting = new ExhAgentCurrencyPosting();
        long agentId = Long.parseLong(task.agentId.toString())
        ExhAgent exhAgent = exhAgentService.read(agentId)
        exhAgentCurrencyPosting.agentId = agentId
        exhAgentCurrencyPosting.currencyId = exhAgent.currencyId
        exhAgentCurrencyPosting.amount = -(task.amountInLocalCurrency + task.regularFee)
        exhAgentCurrencyPosting.createdBy = exhSessionUtil.appSessionUtil.getAppUser().id
        exhAgentCurrencyPosting.updatedBy = 0L
        exhAgentCurrencyPosting.createdOn = new Date()
        exhAgentCurrencyPosting.updatedOn = null
        exhAgentCurrencyPosting.taskId = task.id
        return exhAgentCurrencyPosting
    }
}
