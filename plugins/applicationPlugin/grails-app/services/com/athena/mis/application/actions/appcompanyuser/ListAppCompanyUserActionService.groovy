package com.athena.mis.application.actions.appcompanyuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of company users(appUsers) for grid
 *  For details go through Use-Case doc named 'ListAppCompanyUserActionService'
 */
class ListAppCompanyUserActionService extends BaseService implements ActionIntf {

    AppUserService appUserService
    @Autowired
    CompanyCacheUtility companyCacheUtility

    private Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get company user(appUser) list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result
        try {
            initPager(params)  // initialize parameters for flexGrid
            List lstAppUser = appUserService.findAllByIsCompanyUser(true) // get list of company user(appUser)
            int count = appUserService.countByIsCompanyUser(true)  // get count of total company user(appUser)
            return [appUserList: lstAppUser, count: count]
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
     * Wrap company user(appUser) list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj   // cast map returned from execute method
            List lstAppUser = wrapAppUserList(executeResult.appUserList, start)   // wrap company user list in grid entity
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
     * Wrap list of company user(appUser) in grid entity
     * @param lstAppUser -list of company user(appUser)
     * @param start -starting index of the page
     * @return -list of wrapped company user(appUser)
     */
    private List wrapAppUserList(List<AppUser> lstAppUser, int start) {
        List lstWrappedAppUser = []
        int counter = start + 1
        for (int i = 0; i < lstAppUser.size(); i++) {
            AppUser appUser = lstAppUser[i]
            Company company = (Company) companyCacheUtility.read(appUser.companyId)
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
                    company.name
            ]
            lstWrappedAppUser << obj
            counter++
        }
        return lstWrappedAppUser
    }
}
