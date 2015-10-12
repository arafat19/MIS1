package com.athena.mis.exchangehouse.actions.useragent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhUserAgentCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteUserAgentActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserEntityService appUserEntityService
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhUserAgentCacheUtility exhUserAgentCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String ENTITY_NOT_FOUND_MESSAGE = "User-Agent mapping not found"
    private static final String SUCCESS_MESSAGE = "User-Agent has been deleted successfully"
    private static final String FAILURE_MESSAGE = "Failed to delete user-agent mapping"
    private static final String USER_AGENT_OBJ = "userAgentObj"
    private static final String LST_APP_USER = "lstAppUser"

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long id = Long.parseLong(params.id.toString())
            AppUserEntity userAgent = appUserEntityService.read(id)
            if (!userAgent) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_MESSAGE)
                return result
            }
            result.put(USER_AGENT_OBJ, userAgent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            AppUserEntity userAgent = (AppUserEntity) preResult.get(USER_AGENT_OBJ)
            result.put(USER_AGENT_OBJ, userAgent)
            appUserEntityService.delete(userAgent.id)
            exhUserAgentCacheUtility.delete(userAgent.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete user-agent mapping')
            return Boolean.FALSE
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AppUserEntity userAgent = (AppUserEntity) executeResult.get(USER_AGENT_OBJ)
            List<GroovyRowResult> lstAppUser = listAppUserForDropDown(userAgent)
            lstAppUser = Tools.listForKendoDropdown(lstAppUser, null, null)
            result.put(LST_APP_USER, lstAppUser)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
        )
        ORDER BY username
    """

    private List listAppUserForDropDown(AppUserEntity userEntity) {
        Map queryParams = [
                entityTypeId: userEntity.entityTypeId,
                entityId: userEntity.entityId,
                companyId: exhSessionUtil.appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstAppUser = executeSelectSql(QUERY_FOR_APP_USER, queryParams)
        return lstAppUser
    }
}

