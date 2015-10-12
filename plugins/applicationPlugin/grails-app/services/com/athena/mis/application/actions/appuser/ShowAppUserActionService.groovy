package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for appUser CRUD and list of user for grid
 *  For details go through Use-Case doc named 'ShowAppUserActionService'
 */
class ShowAppUserActionService extends BaseService implements ActionIntf{

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = 'Failed to load user page'
    private static final String GRID_OBJECT = "gridObject"

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Check if user has access to view AppUser or not
     * @param params -N/A
     * @param obj -N/A
     * @return -a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            // only development role type user can view AppUser
            if (appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            return result
        }
    }

    /**
     * Get appUser list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map receiveResult = list()  // get list of appUser
            List lstAppUser = wrapAppUserList(receiveResult.appUserList, start) // wrap user list in grid entity
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map gridObject = [page: pageNumber, total: receiveResult.count, rows: lstAppUser]
            result.put(GRID_OBJECT, gridObject)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
     * Do nothing for build failure result
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of appUser in grid entity
     * @param lstAppUser -list of appUser
     * @param start -starting index of the page
     * @return -list of wrapped appUser
     */
    private List wrapAppUserList(List<AppUser> lstAppUser, int start) {
        List lstWrappedAppUser = []
        int counter = start + 1
        lstAppUser.each { appUser ->
            Employee employee = (Employee) employeeCacheUtility.read(appUser.employeeId)
            GridEntity obj = new GridEntity()
            obj.id = appUser.id
            obj.cell = [
                    counter,
                    appUser.id,
                    appUser.username,
                    appUser.loginId,
                    appUser.enabled? Tools.YES : Tools.NO,
                    appUser.accountLocked? Tools.YES : Tools.NO,
                    appUser.accountExpired? Tools.YES : Tools.NO,
                    employee ? employee.fullName : Tools.EMPTY_SPACE
            ]
            lstWrappedAppUser << obj
            counter++
        }
        return lstWrappedAppUser
    }

    /**
     * Get list and count of appUser
     * @return -a map containing list and count of appUser
     */
    private LinkedHashMap list() {
        Map searchResult = appUserCacheUtility.findByIsCompanyUser(this)
        List<AppUser> appUserList = searchResult.lstAppUser
        int count = searchResult.count
        return [appUserList: appUserList, count: count]
    }
}
