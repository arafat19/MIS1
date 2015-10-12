package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.*
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update AppUserEntity object and grid data
 *  For details go through Use-Case doc named 'UpdateAppUserEntityActionService'
 */
class UpdateAppUserEntityActionService extends BaseService implements ActionIntf {

    AppUserEntityService appUserEntityService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    UserProjectCacheUtility userProjectCacheUtility
    @Autowired
    UserGroupCacheUtility userGroupCacheUtility
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired
    UserBankBranchCacheUtility userBankBranchCacheUtility

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "App user entity object has been updated successfully"
    private static final String UPDATE_FAILURE_MESSAGE = "Could not update app user entity object"
    private static final String APP_USER_ENTITY = "appUserEntity"
    private static final String OBJ_NOT_FOUND_MSG = "AppUserEntity object not found"

    /**
     * 1. check required parameters
     * 2. check existence of AppUserEntity object
     * 3. build AppUserEntity object for update
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing necessary objects for execute
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long appUserEntityId = Long.parseLong(parameterMap.id)
            AppUserEntity oldAppUserEntity = appUserEntityService.read(appUserEntityId)
            if (!oldAppUserEntity) {
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MSG)
                return result
            }
            // build AppUserEntity object to update
            AppUserEntity appUserEntity = buildAppUserEntityObject(parameterMap, oldAppUserEntity)
            result.put(APP_USER_ENTITY, appUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Save AppUserEntity object (user entity mapping) in DB
     * @param parameters -N/A
     * @param obj -AppUserEntity send from executePreCondition method
     * @return -newly created AppUserEntity object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AppUserEntity appUserEntity = (AppUserEntity) preResult.get(APP_USER_ENTITY)
            appUserEntityService.update(appUserEntity)
            updateCacheUtility(appUserEntity)
            result.put(APP_USER_ENTITY, appUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created AppUserEntity object to show on grid
     * @param obj -newly created AppUserEntity object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            AppUserEntity appUserEntity = (AppUserEntity) executeResult.get(APP_USER_ENTITY)
            AppUser user = (AppUser) appUserCacheUtility.read(appUserEntity.appUserId)
            GridEntity object = new GridEntity()
            object.id = appUserEntity.id
            object.cell = [
                    Tools.LABEL_NEW,
                    appUserEntity.id,
                    user.username
            ]
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
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

    /**
     * build AppUserEntity object to update in DB
     * @param parameterMap -serialized parameters from UI
     * @param oldAppUserEntity -object of AppUserEntity
     * @return -AppUserEntity object
     */
    private AppUserEntity buildAppUserEntityObject(GrailsParameterMap parameterMap, AppUserEntity oldAppUserEntity) {
        AppUserEntity appUserEntity = new AppUserEntity(parameterMap)
        oldAppUserEntity.appUserId = appUserEntity.appUserId
        return oldAppUserEntity
    }

    /**
     * Update related cache utility
     * @param appUserEntity -object of AppUserEntity
     */
    private void updateCacheUtility(AppUserEntity appUserEntity) {
        SystemEntity systemEntity = (SystemEntity) appUserEntityTypeCacheUtility.read(appUserEntity.entityTypeId)
        switch (systemEntity.reservedId) {
            case appUserEntityTypeCacheUtility.PROJECT:
                userProjectCacheUtility.update(appUserEntity, userProjectCacheUtility.SORT_ON_ID, userProjectCacheUtility.SORT_ORDER_ASCENDING)
                break
            case appUserEntityTypeCacheUtility.GROUP:
                userGroupCacheUtility.update(appUserEntity, userGroupCacheUtility.SORT_ON_NAME, userGroupCacheUtility.SORT_ORDER_DESCENDING)
                break
            case appUserEntityTypeCacheUtility.INVENTORY:
                inventoryImplService.updateUserInventoryInCache(appUserEntity)
                break
            case appUserEntityTypeCacheUtility.BANK_BRANCH:
                userBankBranchCacheUtility.update(appUserEntity, userBankBranchCacheUtility.SORT_ON_ID, userBankBranchCacheUtility.SORT_ORDER_ASCENDING)
                break
            case appUserEntityTypeCacheUtility.EXCHANGE_HOUSE:
                armsImplService.updateAppUserExchangeHouse(appUserEntity)
                break
            default:
                break
        }
    }
}
