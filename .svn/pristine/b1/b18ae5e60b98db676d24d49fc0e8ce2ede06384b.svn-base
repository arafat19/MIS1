package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Search appUser and show specific list of user for grid
 *  For details go through Use-Case doc named 'SearchAppUserActionService'
 */
class SearchAppUserActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserService appUserService
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    EmployeeCacheUtility employeeCacheUtility

    /**
     * Check if user has access to search user or not
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            // only admin role type user can search user
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
     * Get appUser list for grid through specific search
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            initSearch(params)  // initialize parameters for flexGrid
            return appUserService.search(this) // get list and count of appUser by search keyword
        } catch (Exception e) {
            log.error(e.getMessage())
            result = [appUserList: null, count: 0]
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
     * Wrap appUser list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List lstAppUser = wrapAppUserList(executeResult.appUserList, start) // wrap user list in grid entity
            result.put(Tools.PAGE, pageNumber)
            result.put(Tools.TOTAL, executeResult.count)
            result.put(Tools.ROWS, lstAppUser)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.PAGE, pageNumber)
            result.put(Tools.TOTAL, 0)
            result.put(Tools.ROWS, null)
            return result
        }
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
}
