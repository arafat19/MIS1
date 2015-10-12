package com.athena.mis.exchangehouse.actions.useragent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhUserAgentCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class SelectUserAgentActionService extends BaseService implements ActionIntf {

    private static final String MAPPING_NOT_FOUND_MASSAGE = "Selected User-Agent Mapping not found"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to select User-Agent Mapping"
    private static final String LST_APP_USER = "lstAppUser"

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ExhUserAgentCacheUtility exhUserAgentCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long id = Long.parseLong(parameterMap.id.toString())
            AppUserEntity appUserEntity = (AppUserEntity) exhUserAgentCacheUtility.read(id)
            if (!appUserEntity) {
                result.put(Tools.MESSAGE, MAPPING_NOT_FOUND_MASSAGE)
                return result
            }
            List<GroovyRowResult> lstAppUser = (List<GroovyRowResult>) listAppUserForDropDown(appUserEntity)
            result.put(LST_APP_USER, Tools.listForKendoDropdown(lstAppUser, null, null))
            result.put(Tools.ENTITY, appUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, MAPPING_NOT_FOUND_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap receivedResult = (LinkedHashMap) obj
                String receivedMessage = receivedResult.get(Tools.MESSAGE)
                if (receivedMessage) {
                    result.put(Tools.MESSAGE, receivedMessage)
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private static final String QUERY_FOR_APP_USER = """
        SELECT id, username || ' (' || id || ')' as name
        FROM app_user
        WHERE company_id = :companyId
        AND enabled = true
        AND id NOT IN
        (
            SELECT app_user_id
            FROM app_user_entity
            WHERE entity_type_id = :entityTypeId
            AND entity_id = :entityId
            AND company_id = :companyId
            AND app_user_id <> :id
        )
        ORDER BY username
    """

    private List listAppUserForDropDown(AppUserEntity userEntity) {
        Map queryParams = [
                id: userEntity.appUserId,
                entityTypeId: userEntity.entityTypeId,
                entityId: userEntity.entityId,
                companyId: exhSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAppUser = executeSelectSql(QUERY_FOR_APP_USER, queryParams)
        return lstAppUser
    }
}

