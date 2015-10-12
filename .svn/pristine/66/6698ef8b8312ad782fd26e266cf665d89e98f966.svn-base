package com.athena.mis.exchangehouse.actions.useragent

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.exchangehouse.utility.ExhUserAgentCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateUserAgentActionService extends BaseService implements ActionIntf {

    private  Logger log = Logger.getLogger(getClass())

    AppUserEntityService appUserEntityService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhUserAgentCacheUtility exhUserAgentCacheUtility

    private static final String INVALID_INPUT = 'Failed to update user-agent-company mapping due to invalid input'
    private static final String FAILURE_MESSAGE = 'Failed to update user-agent-company mapping'
    private static final String SUCCESS_MESSAGE = 'User-agent-company mapping updated successfully'
    private static final String OBJECT_NOT_FOUND = 'User-agent-company mapping not found'
    private static final String USER_AGENT_OBJ = 'userAgentObj'
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
            if (!parameterMap.id || !parameterMap.userId || !parameterMap.agentId) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(parameterMap.id.toString())
            AppUserEntity existingAppUserEntity = appUserEntityService.read(id)
            if (!existingAppUserEntity) {
                result.put(Tools.MESSAGE, OBJECT_NOT_FOUND)
                return result
            }

            long userId = Long.parseLong(parameterMap.userId.toString())
            existingAppUserEntity.appUserId = userId
            result.put(USER_AGENT_OBJ, existingAppUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj
            AppUserEntity appUserEntity = (AppUserEntity) preResult.get(USER_AGENT_OBJ)
            appUserEntityService.update(appUserEntity)
            exhUserAgentCacheUtility.update(appUserEntity, SORT_ON_ID, exhUserAgentCacheUtility.SORT_ORDER_DESCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(USER_AGENT_OBJ, appUserEntity)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            AppUserEntity userAgent = (AppUserEntity) executeResult.get(USER_AGENT_OBJ)
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
        result.put(Tools.IS_ERROR, Boolean.TRUE)
        try {
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
}

