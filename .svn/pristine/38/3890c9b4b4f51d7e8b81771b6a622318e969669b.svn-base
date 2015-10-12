package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select appUSer object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAppUserActionService'
 */
class SelectAppUserActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String USER_NOT_FOUND = 'Selected user not found'
    private static final String ERROR_SELECTING_USER = 'Failed to select user'

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Check if user has access to select AppUser or not
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            // only admin role type user can select AppUser
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get appUSer object from cache utility by userId
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long userId = Long.parseLong(params.id.toString())
            // get appUSer object from cache utility by userId
            AppUser appUser = (AppUser) appUserCacheUtility.read(userId)
            // check if appUSer object exists or not
            if (appUser) {
                result.put(Tools.IS_ERROR, Boolean.FALSE)
                result.put(Tools.ENTITY, appUser)
            } else {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, USER_NOT_FOUND)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SELECTING_USER)
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
     * Build a map with appUSer object & other necessary properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map executeResult = (Map) obj   // cast map returned from execute method
        AppUser appUser = (AppUser) executeResult.get(Tools.ENTITY)
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, appUser)
            result.put(Tools.VERSION, appUser.version)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SELECTING_USER)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ERROR_SELECTING_USER)
            }
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ERROR_SELECTING_USER)
            return result
        }
    }
}
