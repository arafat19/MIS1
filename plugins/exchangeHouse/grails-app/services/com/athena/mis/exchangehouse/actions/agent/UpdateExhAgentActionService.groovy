package com.athena.mis.exchangehouse.actions.agent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.service.ExhAgentService
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateExhAgentActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_FAILURE_MESSAGE = "Agent could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Agent has been updated successfully"
    private static final String AGENT_ALREADY_EXISTS = "Same Agent Name already exists"
    private static final String EXH_AGENT_OBJECT = "exhAgent"
    private static final String INSUFFICIENT_BALANCE = "Unable to Update due to insufficient balance"


    ExhAgentService exhAgentService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long agentId = Long.parseLong(parameterMap.id.toString())
            ExhAgent exhAgent = ExhAgent.findByNameIlikeAndIdNotEqual(parameterMap.name, agentId, [readOnly: true])
            if (exhAgent) {
                result.put(Tools.MESSAGE, AGENT_ALREADY_EXISTS)
                return result
            }

            exhAgent = (ExhAgent) exhAgentCacheUtility.read(agentId)
            double preBalance = exhAgent.balance
            double newAmountCreditLimit = Double.parseDouble(parameterMap.creditLimit.toString())
            if (preBalance < 0) {
                double sum = preBalance + newAmountCreditLimit
                if (sum < 0) {
                    result.put(Tools.MESSAGE, INSUFFICIENT_BALANCE)
                    return result
                }
            }

            exhAgent.creditLimit = newAmountCreditLimit
            ExhAgent exhAgentObj = buildExhAgentObject(exhAgent, parameterMap)
            result.put(EXH_AGENT_OBJECT, exhAgentObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            ExhAgent exhAgentObject = (ExhAgent) preResult.get(EXH_AGENT_OBJECT)
            exhAgentService.update(exhAgentObject)
            exhAgentCacheUtility.update(exhAgentObject, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXH_AGENT_OBJECT, exhAgentObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            ExhAgent exhAgent = (ExhAgent) executeResult.get(EXH_AGENT_OBJECT)
            Currency currency = (Currency) currencyCacheUtility.read(exhAgent.currencyId)
            GridEntity object = new GridEntity()
            object.id = exhAgent.id
            object.cell = [
                    Tools.LABEL_NEW,
                    exhAgent.id,
                    exhAgent.name,
                    exhAgent.address,
                    exhAgent.city,
                    exhAgent.phone,
                    currency.symbol,
                    exhAgent.balance,
                    exhAgent.creditLimit

            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, exhAgent.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)

            return result
        }
    }

    private ExhAgent buildExhAgentObject(ExhAgent existingObject, GrailsParameterMap parameterMap) {
        ExhAgent newObject = new ExhAgent(parameterMap)
        existingObject.name = newObject.name
        existingObject.address = newObject.address
        if (parameterMap.commissionLogic) {
            existingObject.commissionLogic = newObject.commissionLogic      // for super admin
        }
        existingObject.city = newObject.city
        existingObject.phone = newObject.phone
        existingObject.creditLimit = newObject.creditLimit
        existingObject.updatedBy = exhSessionUtil.appSessionUtil.getAppUser().id
        existingObject.updatedOn = new Date()
        return existingObject
    }
}
