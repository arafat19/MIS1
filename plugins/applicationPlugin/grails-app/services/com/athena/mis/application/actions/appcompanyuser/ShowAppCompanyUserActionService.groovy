package com.athena.mis.application.actions.appcompanyuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for company user(appUser) CRUD and list of company user for grid
 *  For details go through Use-Case doc named 'ShowAppCompanyUserActionService'
 */
class ShowAppCompanyUserActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = 'Failed to load company user page'
    private static final String GRID_OBJECT = "gridObject"

    AppUserService appUserService
    @Autowired
    CompanyCacheUtility companyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Check if user has access to view company user or not
     * @param params -N/A
     * @param obj -N/A
     * @return -a map containing hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            // only development role type user can view company user
            if (appSessionUtil.getAppUser().isConfigManager) {
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
     * Get company user(appUser) list for grid
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            List lstAppUser = appUserService.findAllByIsCompanyUser(true) // get list of company user(appUser)
            int count = appUserService.countByIsCompanyUser(true)
            lstAppUser = wrapAppUserList(lstAppUser, start) // wrap company user list in grid entity
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            Map gridObject = [page: pageNumber, total: count, rows: lstAppUser]
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
     * Do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Do nothing for build failure result for UI
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
                    appUser.enabled ? Tools.YES : Tools.NO,
                    appUser.accountLocked ? Tools.YES : Tools.NO,
                    appUser.accountExpired ? Tools.YES : Tools.NO,
                    company.name
            ]
            lstWrappedAppUser << obj
            counter++
        }
        return lstWrappedAppUser
    }
}
