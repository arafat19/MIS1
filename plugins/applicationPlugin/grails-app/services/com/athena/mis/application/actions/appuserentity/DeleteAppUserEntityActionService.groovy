package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.UserBankBranchCacheUtility
import com.athena.mis.application.utility.UserGroupCacheUtility
import com.athena.mis.application.utility.UserProjectCacheUtility
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppUserEntity object from db and corresponding cache utility
 *  For details go through Use-Case doc named 'DeleteAppUserEntityActionService'
 */
class DeleteAppUserEntityActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserEntityService appUserEntityService
    @Autowired
    UserProjectCacheUtility userProjectCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    UserGroupCacheUtility userGroupCacheUtility
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired
    UserBankBranchCacheUtility userBankBranchCacheUtility

    private static final String NOT_FOUND_MESSAGE = "Selected app user entity object not found"
    private static final String SUCCESS_MESSAGE = "AppUserEntity object has been deleted successfully"
    private static final String FAILURE_MESSAGE = "Failed to delete app user entity object"
    private static final String APP_USER_ENTITY = "appUserEntity"
    private static final String DELETED = "deleted"

    /**
     * 1. check existence of required parameters
     * 2. check existence of AppUserEntity object
     * @param params -parameter from UI
     * @param obj -N/A
     * @return -AppUserEntity object and isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check existence of required parameter
            if (!params.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long appUserEntityId = Long.parseLong(params.id.toString())
            AppUserEntity appUserEntity = appUserEntityService.read(appUserEntityId)
            // check existence of AppUserEntity object
            if (!appUserEntity) {
                result.put(Tools.MESSAGE, NOT_FOUND_MESSAGE)
                return result
            }
            result.put(APP_USER_ENTITY, appUserEntity)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete AppUserEntity object from db and corresponding cache utility
     * @param parameters -N/A
     * @param obj -contains AppUserEntity object send from executePreCondition method
     * @return -contains boolean value(true/false)
     */
    @Transactional
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj   // cast map returned from executePreCondition method
            AppUserEntity appUserEntity = (AppUserEntity) preResult.get(APP_USER_ENTITY)
            appUserEntityService.delete(appUserEntity.id) //delete object from DB
            updateCacheUtility(appUserEntity)  //delete object from cache
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Contains delete success message to show on UI
     * @param obj -N/A
     * @return -a map contains boolean value(true/false) and delete success message
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(DELETED, Boolean.TRUE)
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

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update related cache utility
     * @param appUserEntity -object of AppUserEntity
     */
    private void updateCacheUtility(AppUserEntity appUserEntity) {
        SystemEntity systemEntity = (SystemEntity) appUserEntityTypeCacheUtility.read(appUserEntity.entityTypeId)
        switch (systemEntity.reservedId) {
            case appUserEntityTypeCacheUtility.PROJECT:
                userProjectCacheUtility.delete(appUserEntity.id)
                break
            case appUserEntityTypeCacheUtility.GROUP:
                userGroupCacheUtility.delete(appUserEntity.id)
                break
            case appUserEntityTypeCacheUtility.INVENTORY:
                inventoryImplService.deleteUserInventoryFromCache(appUserEntity.id)
                break
            case appUserEntityTypeCacheUtility.BANK_BRANCH:
                userBankBranchCacheUtility.delete(appUserEntity.id)
                break
            case appUserEntityTypeCacheUtility.EXCHANGE_HOUSE:
                armsImplService.deleteAppUserExchangeHouse(appUserEntity.id)
                break
            default:
                break
        }
    }
}
