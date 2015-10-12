package com.athena.mis.exchangehouse.actions.agent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.service.ExhAgentService
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteExhAgentActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String DELETE_SUCCESS_MSG = "Agent has been successfully deleted"
    private static final String DELETE_FAILURE_MSG = "Agent has not been deleted"
    private static final String ENTITY_NOT_FOUND_MSG = "Agent not found"
    private static final String HAS_ASSOCIATION_CURRENCY_POSTING = " Currency Posting(s) is associated with selected agent-company"
    private static final String HAS_ASSOCIATION_TASK = " Task(s) is associated with selected agent-company"
    private static final String HAS_ASSOCIATION_CUSTOMER = " Customer(s) is associated with selected agent-company"
    private static final String HAS_ASSOCIATION_USER = " User(s) is associated with selected agent-company"

    private static final String DELETED = "deleted"

    ExhAgentService exhAgentService

    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil


    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long exhAgentId = Long.parseLong(parameterMap.id.toString())
            ExhAgent exhAgent = (ExhAgent) exhAgentCacheUtility.read(exhAgentId)
            if (!exhAgent) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_MSG)
                return result
            }
            // todo > follow this logic impl through project
            String hasAssociationResult = hasAssociation(exhAgent)
            if (hasAssociationResult!=null) {
                result.put(Tools.MESSAGE, hasAssociationResult)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
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
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long exhAgentId = Long.parseLong(parameterMap.id.toString())
            exhAgentService.delete(exhAgentId)
            exhAgentCacheUtility.delete(exhAgentId)
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
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

    private String hasAssociation(ExhAgent exhAgent) {
        int count = 0
        String message

        count = countCurrencyPosting(exhAgent.id)
        if (count > 0) {
            message = count.toString() + HAS_ASSOCIATION_CURRENCY_POSTING
            return message
        }

        count = countTask(exhAgent.id)
        if (count > 0) {
            message = count.toString() + HAS_ASSOCIATION_TASK
            return message
        }

        count = countCustomer(exhAgent.id)
        if (count > 0) {
            message = count.toString() + HAS_ASSOCIATION_CUSTOMER
            return message
        }

        count = countAppUserEntity(exhAgent.id)
        if (count > 0) {
            message = count.toString() + HAS_ASSOCIATION_USER
            return message
        }
        return null
    }

    private static final String QUERY_ASSOCIATION_CURR_POSTING = """
            SELECT COUNT(agent_id) AS count
            FROM exh_agent_currency_posting
            WHERE agent_id = :agentId
            """

    private int countCurrencyPosting(long agentId) {
        List results = executeSelectSql(QUERY_ASSOCIATION_CURR_POSTING, [agentId: agentId])
        int count = results[0].count
        return count
    }

    private static final String QUERY_ASSOCIATION_CUSTOMER_POSTING = """
            SELECT COUNT(agent_id) AS count
            FROM exh_customer
            WHERE agent_id = :agentId
            """

    private int countCustomer(long agentId) {
        List results = executeSelectSql(QUERY_ASSOCIATION_CUSTOMER_POSTING, [agentId: agentId])
        int count = results[0].count
        return count
    }

    private static final String QUERY_ASSOCIATION_TASK_POSTING = """
            SELECT COUNT(agent_id) AS count
            FROM exh_task
            WHERE agent_id = :agentId
            """

    private int countTask(long agentId) {
        List results = executeSelectSql(QUERY_ASSOCIATION_TASK_POSTING, [agentId: agentId])
        int count = results[0].count
        return count
    }

    private static final String QUERY_ASSOCIATION_APP_USER_ENTITY = """
            SELECT COUNT(app_user_id) AS count
            FROM app_user_entity
            WHERE entity_id = :entityId AND
            entity_type_id = :entityTypeId
            """

    private int countAppUserEntity(long agentId) {
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.AGENT, exhSessionUtil.appSessionUtil.getCompanyId())
        Map queryParams=[
                entityId: agentId,
                entityTypeId:appUserSysEntityObject.id
        ]

        List results = executeSelectSql(QUERY_ASSOCIATION_APP_USER_ENTITY, queryParams)
        int count = results[0].count
        return count
    }
}
