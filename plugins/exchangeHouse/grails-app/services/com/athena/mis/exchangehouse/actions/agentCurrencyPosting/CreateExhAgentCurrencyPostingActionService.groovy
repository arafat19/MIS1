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

class CreateExhAgentCurrencyPostingActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String CURRENCY_POSTING_FAILURE_MESSAGE = "Agent Currency Posting has not been saved."
    private static final String CURRENCY_POSTING_SUCCESS_MSG = "Agent Currency Posting has been successfully saved."
    private static final String DEFAULT_ERROR_MESSAGE = "Can't create Agent Currency Posting"
    private static final String EXH_AGENT_CURRENCY_POSTING = "exhAgentCurrencyPosting"
    private static final String EXH_AGENT = "exhAgent"

    ExhAgentCurrencyPostingService exhAgentCurrencyPostingService
    ExhAgentService exhAgentService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            ExhAgentCurrencyPosting currencyPostingBuildObj = buildAgentCurrencyPosting(parameterMap)
            long agentId = Long.parseLong(parameterMap.agentId)
            ExhAgent exhAgentBuildObj = exhAgentService.get(agentId)    // r/w pull for further update
            result.put(EXH_AGENT_CURRENCY_POSTING, currencyPostingBuildObj)
            result.put(EXH_AGENT, exhAgentBuildObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            ExhAgentCurrencyPosting exhAgentCurrencyPostingObject = (ExhAgentCurrencyPosting) preResult.get(EXH_AGENT_CURRENCY_POSTING)
            ExhAgent exhAgentObject = (ExhAgent) preResult.get(EXH_AGENT)
            ExhAgentCurrencyPosting newCurrencyPostingObj = exhAgentCurrencyPostingService.create(exhAgentCurrencyPostingObject)
            if (!newCurrencyPostingObj) {
                result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
                return result
            }
            updateBalanceAmount(exhAgentObject)
            exhAgentCacheUtility.update(exhAgentObject, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXH_AGENT_CURRENCY_POSTING, exhAgentCurrencyPostingObject)
            result.put(EXH_AGENT, exhAgentObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhAgentCurrencyPosting exhAgentCurrencyPosting = (ExhAgentCurrencyPosting) executeResult.get(EXH_AGENT_CURRENCY_POSTING)
            ExhAgent exhAgent = (ExhAgent) executeResult.get(EXH_AGENT)
            String strCreatedOn = DateUtility.getLongDateForUI(exhAgentCurrencyPosting.createdOn)
            String strUpdatedOn = DateUtility.getLongDateForUI(exhAgentCurrencyPosting.updatedOn)
            AppUser createdBy = (AppUser) appUserCacheUtility.read(exhAgentCurrencyPosting.createdBy)
            Currency currency = (Currency)currencyCacheUtility.read(exhAgent.currencyId)
            String exhAgentAmount = Tools.formatAmountWithoutCurrency(exhAgentCurrencyPosting.amount) + Tools.SINGLE_SPACE + currency.symbol
            GridEntity object = new GridEntity()
            object.id = exhAgentCurrencyPosting.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exhAgentCurrencyPosting.id,
                    exhAgent.name,
                    exhAgentAmount,
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

    @Override
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CURRENCY_POSTING_FAILURE_MESSAGE)
            return result
        }
    }

    private ExhAgentCurrencyPosting buildAgentCurrencyPosting(GrailsParameterMap params) {
        ExhAgentCurrencyPosting exhAgentCurrencyPosting = new ExhAgentCurrencyPosting(params);
        long agentId = Long.parseLong(params.agentId.toString())
        ExhAgent exhAgent = (ExhAgent) exhAgentCacheUtility.read(agentId)
        exhAgentCurrencyPosting.currencyId = exhAgent.currencyId
        exhAgentCurrencyPosting.amount = Double.parseDouble(params.amount.toString())
        exhAgentCurrencyPosting.createdBy = exhSessionUtil.appSessionUtil.getAppUser().id
        exhAgentCurrencyPosting.updatedBy = 0L
        exhAgentCurrencyPosting.createdOn = new Date()
        exhAgentCurrencyPosting.updatedOn = null
        exhAgentCurrencyPosting.taskId = 0
        return exhAgentCurrencyPosting
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
        exhAgent.balance = balance
        exhAgent.save(flush: true)
        exhAgent.discard()    // remove r/w pull since object will be passed to cacheUtil
        return exhAgent;
    }
}
