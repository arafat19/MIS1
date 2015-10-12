package com.athena.mis.exchangehouse.actions.agentCurrencyPosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting
import com.athena.mis.exchangehouse.service.ExhAgentCurrencyPostingService
import com.athena.mis.exchangehouse.service.ExhAgentService
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {

    private static final String DELETE_SUCCESS_MSG = "Agent Currency Posting has been successfully deleted"
    private static final String DELETE_FAILURE_MSG = "Agent Currency Posting has not been deleted"
    private static final String ENTITY_NOT_FOUND_MSG = "Agent Currency Posting not found"
    private static final String INSUFFICIENT_BALANCE_MESSAGE = "Unable to Delete due to insufficient balance"
    private static final String EXH_AGENT = "exhAgent"
    private static final String DELETED = "deleted"

    private final Logger log = Logger.getLogger(getClass())

    ExhAgentCurrencyPostingService exhAgentCurrencyPostingService
    ExhAgentService exhAgentService
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {

            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long currencyPostingId = Long.parseLong(parameterMap.id.toString())
            ExhAgentCurrencyPosting exhAgentCurrencyPosting = exhAgentCurrencyPostingService.read(currencyPostingId)
            if (!exhAgentCurrencyPosting) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_MSG)
                return result
            }
            ExhAgent exhAgent = (ExhAgent) exhAgentCacheUtility.read(exhAgentCurrencyPosting.agentId)
            String balanceCheckMsg = checkCreditLimit(exhAgent, exhAgentCurrencyPosting.amount)
            if (balanceCheckMsg!=null) {
                result.put(Tools.MESSAGE, balanceCheckMsg)
                return result
            }
            result.put(EXH_AGENT, exhAgent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long currencyPostingId = Long.parseLong(parameterMap.id.toString())

            ExhAgent exhAgent = (ExhAgent) preResult.get(EXH_AGENT)

            exhAgentCurrencyPostingService.delete(currencyPostingId)

            ExhAgent updatedAgent = updateBalanceAmount(exhAgent)
            exhAgentCacheUtility.update(updatedAgent, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, DELETE_SUCCESS_MSG)
        return result
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receiveResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_FAILURE_MSG)
            return result
        }
    }

    private static final String QUERY_SUM_AMOUNT_AGENT = """
                    SELECT COALESCE(SUM(amount),0) sum
                    FROM exh_agent_currency_posting
                    WHERE agent_id = :agentId;
                     """

    private ExhAgent updateBalanceAmount(ExhAgent exhAgent) {
        Map queryParams = [agentId: exhAgent.id]

        List<GroovyRowResult> result = executeSelectSql(QUERY_SUM_AMOUNT_AGENT, queryParams)
        double balance = (double)result[0].sum
        ExhAgent agent = exhAgentService.get(exhAgent.id) // pull from db
        agent.balance = balance
        agent.save(flush: true)
        agent.discard()    // remove r/w pull since object will be passed to cacheUtil
        return agent
    }

    private String checkCreditLimit(ExhAgent exhAgent, double amount){
        if(exhAgent.balance > amount){
            return null
        }
        double newBalance = exhAgent.balance - amount
        double amountCredited = Math.abs(newBalance)
        double creditLimit = exhAgent.creditLimit
        if(amountCredited > creditLimit){
            return INSUFFICIENT_BALANCE_MESSAGE
        }
        return null
    }
}
