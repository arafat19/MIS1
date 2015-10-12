package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Change user password in DB and update cache utility accordingly
 *  For details go through Use-Case doc named 'ChangeUserPasswordActionService'
 */
class ChangeUserPasswordActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserService appUserService
    SpringSecurityService springSecurityService

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String APP_USER = 'appUser'
    private static final String WRONG_PASSWORD_MSG = 'Invalid Password'
    private static final String INVALID_INPUT_MSG = 'Error occurred due to invalid input'
    private static final String SUCCESS_MSG = 'Password changed successfully'
    private static final String FAILURE_MSG = 'Failed to change password'

    /**
     * Get logged in user and check existence of user
     * Check required parameters
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) and hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            AppUser appUser = appSessionUtil.getAppUser()   // get logged in user
            // check if user exists or not
            if (!appUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap parameters = (GrailsParameterMap) params
            // check required parameters
            if ((!parameters.oldPassword) || (!parameters.newPassword)) {
                return result
            }
            result.put(APP_USER, appUser)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Check if input for old password is correct or not
     * Get AppUser object from cache utility by id
     * Save new password in DB and update cache utility accordingly
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) and relevant message depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj   // cast map returned from executePreCondition method
            GrailsParameterMap params = (GrailsParameterMap) parameters
            AppUser userInstance = (AppUser) preResult.get(APP_USER)
            // check if input for old password is correct or not
            String oldPass = springSecurityService.encodePassword(params.oldPassword) // encode password
            if (!oldPass.equals(userInstance.password)) {
                result.put(Tools.MESSAGE, WRONG_PASSWORD_MSG)
                return result
            }
            // get AppUser object from cache utility by id
            AppUser appUser = (AppUser) appUserCacheUtility.read(userInstance.id)
            appUser.password = springSecurityService.encodePassword(params.newPassword) // encode password
            appUser.validate()  // validate AppUser object
            if (appUser.hasErrors()) {
                result.put(Tools.MESSAGE, INVALID_INPUT_MSG)
                return result
            }
            appUserService.updatePassword(appUser)  // save new password in DB
            appUser.version = appUser.version + 1
            // update cache utility accordingly and keep the data sorted
            appUserCacheUtility.update(appUser, appUserCacheUtility.SORT_ON_NAME, appUserCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build a map with isError false and success message
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
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
            LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
}
