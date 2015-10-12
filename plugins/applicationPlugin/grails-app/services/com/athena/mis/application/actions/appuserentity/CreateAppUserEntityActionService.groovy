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
 *  Create new AppUserEntity object and show in grid
 *  For details go through Use-Case doc named 'CreateAppUserEntityActionService'
 */
class CreateAppUserEntityActionService extends BaseService implements ActionIntf {

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

    private static final String SAVE_SUCCESS_MESSAGE = "App user entity object has been saved successfully"
    private static final String SAVE_FAILURE_MESSAGE = "Can not save app user entity object"
    private static final String APP_USER_ENTITY = "appUserEntity"

    /**
     * 1. check required parameters
     * 2. build AppUserEntity object for create
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing necessary objects for execute
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            // check required parameters
            if ((!parameterMap.entityTypeId) || (!parameterMap.entityId)) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            // build AppUserEntity object to create
            AppUserEntity appUserEntity = buildAppUserEntityObject(parameterMap)
            result.put(APP_USER_ENTITY, appUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
            AppUserEntity newAppUserEntity = appUserEntityService.create(appUserEntity)
            updateCacheUtility(newAppUserEntity)
            result.put(APP_USER_ENTITY, newAppUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(SAVE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, SAVE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SAVE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build AppUserEntity object to save in DB
     * @param parameterMap -serialized parameters from UI
     * @return -AppUserEntity object
     */
    private AppUserEntity buildAppUserEntityObject(GrailsParameterMap parameterMap) {
        AppUserEntity appUserEntity = new AppUserEntity(parameterMap)
        return appUserEntity
    }

    /**
     * Update related cache utility
     * @param appUserEntity -object of AppUserEntity
     */
    private void updateCacheUtility(AppUserEntity appUserEntity) {
        SystemEntity systemEntity = (SystemEntity) appUserEntityTypeCacheUtility.read(appUserEntity.entityTypeId)
        switch (systemEntity.reservedId) {
            case appUserEntityTypeCacheUtility.PROJECT:
                userProjectCacheUtility.add(appUserEntity, userProjectCacheUtility.SORT_ON_ID, userProjectCacheUtility.SORT_ORDER_ASCENDING)
                break
            case appUserEntityTypeCacheUtility.GROUP:
                userGroupCacheUtility.add(appUserEntity, userGroupCacheUtility.SORT_ON_NAME, userGroupCacheUtility.SORT_ORDER_DESCENDING)
                break
            case appUserEntityTypeCacheUtility.INVENTORY:
                inventoryImplService.addUserInventoryInCache(appUserEntity)
                break
            case appUserEntityTypeCacheUtility.BANK_BRANCH:
                userBankBranchCacheUtility.add(appUserEntity, userBankBranchCacheUtility.SORT_ON_ID,userBankBranchCacheUtility.SORT_ORDER_ASCENDING)
                break
            case appUserEntityTypeCacheUtility.EXCHANGE_HOUSE:
                armsImplService.addAppUserExchangeHouse(appUserEntity)
                break
            default:
                break
        }
    }
}
