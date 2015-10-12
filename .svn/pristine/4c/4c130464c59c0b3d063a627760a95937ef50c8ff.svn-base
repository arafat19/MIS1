package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import grails.plugins.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 * Check user password for reset request map
 * For details go through Use-Case doc named 'CheckUserPasswordActionService'
 */
class CheckUserPasswordActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String FAILURE_MSG = 'Failed to check password'
    private static final String INVALID_PASSWORD = 'Invalid password'

    /**
     * Check if user has access to this feature
     * @param params -parameters from UI
     * @param obj -N/A
     * @return a map containing isError(true/false) and hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            // only admin role type user can access this feature
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap parameters = (GrailsParameterMap) params
            // check required parameter
            if (!parameters.pass) {
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get logged in user and check input password with user password
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) and relevant message depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            String testPass = springSecurityService.encodePassword(params.pass) // encode password
            AppUser appUser = appSessionUtil.getAppUser()   // get logged in user
            // check if input password matches with user password
            if (!testPass.equals(appUser.password)) {
                result.put(Tools.MESSAGE, INVALID_PASSWORD)
                return result
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
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
     * Do nothing for build success result
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap previousResult = (LinkedHashMap) obj  // cast map returned from previous method
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (previousResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
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

