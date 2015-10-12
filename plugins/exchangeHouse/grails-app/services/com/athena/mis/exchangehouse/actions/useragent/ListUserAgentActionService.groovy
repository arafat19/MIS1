package com.athena.mis.exchangehouse.actions.useragent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListUserAgentActionService extends BaseService implements ActionIntf {

    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private Logger log = Logger.getLogger(getClass())

    private static String PAGE_LOAD_ERROR_MESSAGE = "Failed to get user-agent mapping list"
    private static String USER_AGENT_LIST = "userAgentList"
    private static String NO_DATA_FOUND = "No user-agent mapping found"
    private static final String AGENT_NOT_FOUND = "Selected agent not found"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!params.rp) {
                params.rp = 15
                params.page = 1
            }
            initPager(params)
            if (!parameters.agentId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long agentId = Long.parseLong(parameters.agentId)
            ExhAgent agent = (ExhAgent) exhAgentCacheUtility.read(agentId)
            if (!agent) {
                result.put(Tools.MESSAGE, AGENT_NOT_FOUND)
                return result
            }
            Map resultSet = getUserAgentList(agentId)
            List<GroovyRowResult> userAgentList = resultSet.userAgentList
            int count = resultSet.count
            if (count <= 0) {
                result.put(Tools.MESSAGE, NO_DATA_FOUND)
                return result
            }

            result.put(USER_AGENT_LIST, userAgentList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> userAgentList = (List<GroovyRowResult>) executeResult.get(USER_AGENT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List wrappedUserAgent = wrapListInGridEntityList(userAgentList, start)
            return [page: pageNumber, total: count, rows: wrappedUserAgent]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj.message) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<GroovyRowResult> userAgentList, int start) {
        List lstUserAgent = []
        try {
            int counter = start + 1
            for (int i = 0; i < userAgentList.size(); i++) {
                GroovyRowResult singleRow = userAgentList[i]
                GridEntity obj = new GridEntity()
                obj.id = singleRow.id
                obj.cell = [
                        counter,
                        singleRow.app_user_id,
                        singleRow.app_user_name,
                        singleRow.agent_id
                ]
                lstUserAgent << obj
                counter++
            }
            return lstUserAgent
        } catch (Exception ex) {
            log.error(ex.getMessage())
            lstUserAgent = []
            return lstUserAgent
        }
    }

    private static final String QUERY_FOR_LIST = """
        SELECT aue.id, au.id AS app_user_id, au.username AS app_user_name, aue.entity_id AS agent_id
        FROM app_user_entity aue
        LEFT JOIN app_user au ON au.id = aue.app_user_id
        WHERE aue.entity_type_id = :entityTypeId
        AND aue.entity_id = :agentId
        AND au.enabled = true
        AND au.company_id = :companyId
        ORDER BY au.username
        LIMIT :resultPerPage OFFSET :start
    """

    private static final String QUERY_FOR_COUNT = """
        SELECT COUNT(aue.id) AS count
        FROM app_user_entity aue
        LEFT JOIN app_user au ON au.id = aue.app_user_id
        WHERE aue.entity_type_id = :entityTypeId
        AND aue.entity_id = :agentId
        AND au.enabled = true
        AND au.company_id = :companyId
    """

    private Map getUserAgentList(long agentId) {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.AGENT, companyId)

        Map queryParams = [
                entityTypeId: appUserSysEntityObject.id,
                agentId: agentId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(QUERY_FOR_LIST, queryParams)
        List resultCount = executeSelectSql(QUERY_FOR_COUNT, queryParams)
        int count = resultCount[0].count
        return [userAgentList: result, count: count]
    }
}


