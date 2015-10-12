package com.athena.mis.exchangehouse.actions.useragent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhUserAgentCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CreateUserAgentActionService extends BaseService implements ActionIntf {

    private  Logger log = Logger.getLogger(getClass())

    AppUserEntityService appUserEntityService
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ExhUserAgentCacheUtility exhUserAgentCacheUtility

    private static final String INVALID_INPUT = 'Failed to create user-agent mapping due to invalid input'
    private static final String FAILURE_MESSAGE = 'Failed to create user-agent mapping'
    private static final String SUCCESS_MESSAGE = 'User-Agent mapping successfully saved'
    private static final String USER_AGENT = 'userAgent'
    private static final String SORT_ON_ID = 'id'

    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            if (!exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            if (!parameterMap.userId || !parameterMap.agentId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            AppUserEntity userAgent = buildAppUserEntity(parameterMap)
            result.put(USER_AGENT, userAgent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            AppUserEntity userAgent = (AppUserEntity) preResult.get(USER_AGENT)
            AppUserEntity newUserAgent = appUserEntityService.create(userAgent)
            exhUserAgentCacheUtility.add(newUserAgent, SORT_ON_ID, exhUserAgentCacheUtility.SORT_ORDER_DESCENDING)
            result.put(USER_AGENT, newUserAgent)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            AppUserEntity userAgent = (AppUserEntity) executeResult.get(USER_AGENT)
            GridEntity objGrid = new GridEntity()
            AppUser user = (AppUser) appUserCacheUtility.read(userAgent.appUserId)
            objGrid.id = userAgent.id
            objGrid.cell = [
                    Tools.LABEL_NEW,
                    user.id,
                    user.username,
                    userAgent.entityId
            ]
            result.put(Tools.ENTITY, objGrid)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
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
            if (obj.message) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    private AppUserEntity buildAppUserEntity(GrailsParameterMap parameterMap) {
        AppUserEntity appUserEntity = new AppUserEntity(parameterMap)
        appUserEntity.appUserId = Long.parseLong(parameterMap.userId.toString())
        appUserEntity.entityId = Long.parseLong(parameterMap.agentId.toString())
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.AGENT, exhSessionUtil.appSessionUtil.getCompanyId())
        appUserEntity.entityTypeId = appUserSysEntityObject.id
        return appUserEntity
    }
}

