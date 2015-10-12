package com.athena.mis.exchangehouse.actions.agent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Country
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.CountryCacheUtility
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

class CreateExhAgentActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_COMMISSION_LOGIC_FOR_ADMIN = 'return amount * 0.0'
    private static final String CREATE_SUCCESS_MESSAGE = "Agent has been successfully saved."
    private static final String CREATE_FAILURE_MESSAGE = "Agent has not been saved"
    private static final String AGENT_ALREADY_EXISTS = "Same Agent Name already exists."
    private static final String EXH_AGENT_OBJECT = "exhAgent"

    ExhAgentService exhAgentService

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    CountryCacheUtility countryCacheUtility
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            ExhAgent exhAgent = ExhAgent.findByNameIlike(parameterMap.name, [readOnly: true])
            if (exhAgent) {
                result.put(Tools.MESSAGE, AGENT_ALREADY_EXISTS)
                return result
            }
            ExhAgent exhAgentObj = buildExhAgentObject(parameterMap)
            result.put(EXH_AGENT_OBJECT, exhAgentObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            ExhAgent exhAgentObject = (ExhAgent) preResult.get(EXH_AGENT_OBJECT)
            ExhAgent receiveExhAgentObject = exhAgentService.create(exhAgentObject)
            exhAgentCacheUtility.add(receiveExhAgentObject, exhAgentCacheUtility.SORT_ON_NAME, exhAgentCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXH_AGENT_OBJECT, receiveExhAgentObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            ExhAgent exhAgent = (ExhAgent) receiveResult.get(EXH_AGENT_OBJECT)
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
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MESSAGE)
            return result
        }
    }

    private ExhAgent buildExhAgentObject(GrailsParameterMap params) {
        ExhAgent exhAgent = new ExhAgent(params)
        Company company = (Company) companyCacheUtility.read(exhSessionUtil.appSessionUtil.getAppUser().companyId)
        Currency currency = (Currency) currencyCacheUtility.read(company.currencyId)
        Country country = (Country) countryCacheUtility.read(company.countryId)
        if (params.commissionLogic) {
            exhAgent.commissionLogic = params.commissionLogic     // for super admin
        } else {
            exhAgent.commissionLogic = DEFAULT_COMMISSION_LOGIC_FOR_ADMIN      // for admin
        }
        exhAgent.companyId = company.id
        exhAgent.countryId = country.id
        exhAgent.currencyId = currency.id
        exhAgent.balance = 0d
        exhAgent.createdBy = exhSessionUtil.appSessionUtil.getAppUser().id
        exhAgent.createdOn = new Date();
        return exhAgent
    }
}
