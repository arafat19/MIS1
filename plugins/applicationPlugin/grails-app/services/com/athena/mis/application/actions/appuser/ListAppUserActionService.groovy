package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show list of appUsers for grid
 *  For details go through Use-Case doc named 'ListAppUserActionService'
 */
class ListAppUserActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get appUser list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            initPager(params)  // initialize parameters for flexGrid
            return list()   // return a map containing list and count of appUser
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
            result.put(Tools.ROWS, lstAppUser.size() > 0 ? lstAppUser : null)
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

    /**
     * Get list and count of appUser
     * @return -a map containing list and count of appUser
     */
    private LinkedHashMap list() {
        Map listResult = appUserCacheUtility.findByIsCompanyUser(this)
        List<AppUser> appUserList = listResult.lstAppUser
        int count = listResult.count
        return [appUserList: appUserList, count: count]
    }
}
