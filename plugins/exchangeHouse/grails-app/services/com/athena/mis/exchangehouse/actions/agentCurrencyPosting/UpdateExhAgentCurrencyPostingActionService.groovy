package com.athena.mis.exchangehouse.actions.agentCurrencyPosting

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting
import com.athena.mis.exchangehouse.service.ExhAgentCurrencyPostingService
import com.athena.mis.exchangehouse.service.ExhAgentService
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String INSUFFICIENT_BALANCE = "Unable to Update due to insufficient balance"
    private static final String NOT_FOUND_MESSAGE = "Agent Currency Posting Information not found"
    private static final String CURRENCY_POSTING_FAILURE_MESSAGE = "Agent Currency Posting has not been updated"
    private static final String CURRENCY_POSTING_SUCCESS_MSG = "Agent Currency Posting has been successfully updated"
    private static final String EXH_AGENT_CURRENCY_POSTING_OBJ = "exhAgentCurrencyPosting"
    private static final String EXH_AGENT = "exhAgent"

    ExhAgentService exhAgentService
    ExhAgentCurrencyPostingService exhAgentCurrencyPostingService

    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long currencyPostingId = Long.parseLong(parameterMap.id.toString())
            ExhAgentCurrencyPosting exhAgentCurrencyPosting = exhAgentCurrencyPostingService.read(currencyPostingId)
            if (!exhAgentCurrencyPosting) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            ExhAgent exhAgent = (ExhAgent) exhAgentCacheUtility.read(exhAgentCurrencyPosting.agentId)

            double preAmount = exhAgentCurrencyPosting.amount
            double preBalance = exhAgent.balance

            double newAmount = Double.parseDouble(parameterMap.amount.toString())
            double newBalance = preBalance - preAmount + newAmount

            if (newBalance < 0) {
                double amountCredited = Math.abs(newBalance)
                double creditLimit = exhAgent.creditLimit
                if(amountCredited > creditLimit){
                    result.put(Tools.MESSAGE, INSUFFICIENT_BALANCE)
                    return result
                }
            }

            ExhAgentCurrencyPosting exhAgentCurrencyPostingObj = buildAgentCurrencyPosting(parameterMap)
            exhAgent.balance = newBalance.round(2)
            result.put(EXH_AGENT_CURRENCY_POSTING_OBJ, exhAgentCurrencyPostingObj)
            result.put(EXH_AGENT, exhAgent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    @Transactional
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ExhAgentCurrencyPosting exhAgentCurrencyPostingObject = (ExhAgentCurrencyPosting) preResult.get(EXH_AGENT_CURRENCY_POSTING_OBJ)
            ExhAgent exhAgent = (ExhAgent) preResult.get(EXH_AGENT)
            exhAgentCurrencyPostingService.update(exhAgentCurrencyPostingObject)
            if (!exhAgentCurrencyPostingObject) {
                result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
                return result
            }
            ExhAgent updatedObjAgent = updateBalanceAmount(exhAgent)
            exhAgentCacheUtility.update(updatedObjAgent, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXH_AGENT_CURRENCY_POSTING_OBJ, exhAgentCurrencyPostingObject)
            result.put(EXH_AGENT, updatedObjAgent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhAgentCurrencyPosting exhAgentCurrencyPosting = (ExhAgentCurrencyPosting) executeResult.get(EXH_AGENT_CURRENCY_POSTING_OBJ)
            ExhAgent exhAgent = (ExhAgent) executeResult.get(EXH_AGENT)
            String strCreatedOn = DateUtility.getLongDateForUI(exhAgentCurrencyPosting.createdOn)
            String strUpdatedOn = DateUtility.getLongDateForUI(exhAgentCurrencyPosting.updatedOn)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(exhAgentCurrencyPosting.createdBy)
            Currency currency = (Currency) currencyCacheUtility.read(exhAgent.currencyId)
            GridEntity object = new GridEntity()
            object.id = exhAgentCurrencyPosting.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exhAgentCurrencyPosting.id,
                    exhAgent.name,
                    Tools.formatAmountWithoutCurrency(exhAgentCurrencyPosting.amount) + Tools.SINGLE_SPACE + currency.symbol,
                    createdBy.username,
                    strCreatedOn,
                    strUpdatedOn
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, exhAgentCurrencyPosting.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, CURRENCY_POSTING_SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receiveResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, INSUFFICIENT_BALANCE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, INSUFFICIENT_BALANCE)

            return result
        }
    }

    private ExhAgentCurrencyPosting buildAgentCurrencyPosting(GrailsParameterMap parameterMap) {

        ExhAgentCurrencyPosting newCurrencyPostingObject = new ExhAgentCurrencyPosting(parameterMap);
        long exhAgentCurrencyPostingId = Long.parseLong(parameterMap.id.toString())
        ExhAgentCurrencyPosting oldCurrencyPostingObject = exhAgentCurrencyPostingService.read(exhAgentCurrencyPostingId)
        newCurrencyPostingObject.id = exhAgentCurrencyPostingId
        newCurrencyPostingObject.version = Integer.parseInt(parameterMap.version.toString())
        newCurrencyPostingObject.updatedBy = exhSessionUtil.appSessionUtil.getAppUser().id
        newCurrencyPostingObject.updatedOn = new Date()
        newCurrencyPostingObject.agentId = oldCurrencyPostingObject.agentId
        newCurrencyPostingObject.createdBy = oldCurrencyPostingObject.createdBy
        newCurrencyPostingObject.createdOn = oldCurrencyPostingObject.createdOn
        return newCurrencyPostingObject
    }

    private static final String QUERY_SUM_AMOUNT_AGENT = """
                    SELECT COALESCE(SUM(amount),0) sum
                    FROM exh_agent_currency_posting
                    WHERE agent_id = :agentId;
                     """

    private ExhAgent updateBalanceAmount(ExhAgent exhAgent) {
        Map queryParams = [agentId: exhAgent.id]

        List<GroovyRowResult> result = executeSelectSql(QUERY_SUM_AMOUNT_AGENT, queryParams);
        double balance = (double)result[0].sum
        ExhAgent agent = exhAgentService.get(exhAgent.id) // pull from db
        agent.balance = balance
        agent.save(flush: true)
        agent.discard()    // remove r/w pull since object will be passed to cacheUtil
        return agent;
    }
}
