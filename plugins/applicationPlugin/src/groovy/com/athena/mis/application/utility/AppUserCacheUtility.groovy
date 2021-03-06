package com.athena.mis.application.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('appUserCacheUtility')
class AppUserCacheUtility extends ExtendedCacheUtility {

    @Autowired
    AppUserService appUserService

    public static final String SORT_ON_NAME = "username";

    public void init() {
        List list = appUserService.listForUtility();
        super.setList(list)
    }

    public AppUser readByLoginId(String loginId) {
        List<AppUser> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            AppUser appUser = (AppUser) lstTemp[i]
            if (appUser.loginId.equals(loginId)) {
                return appUser
            }
        }
        return null
    }

    public AppUser readByLoginIdAndIdNotEqual(String loginId, long id) {
        List<AppUser> lstTemp = super.list()
        for (int i = 0; i < lstTemp.size(); i++) {
            AppUser appUser = (AppUser) lstTemp[i]
            if (appUser.loginId.equals(loginId) && appUser.id != id) {
                return appUser
            }
        }
        return null
    }

    /**
     * Used in customer sign up, where session user is not available
     * @param loginId
     * @param companyId
     * @return appUser object
     */
    public AppUser readByLoginId(String loginId, long companyId) {
        List<AppUser> lstTemp = super.list(companyId)
        for (int i = 0; i < lstTemp.size(); i++) {
            AppUser appUser = (AppUser) lstTemp[i]
            if (appUser.loginId.equals(loginId)) {
                return appUser
            }
        }
        return null
    }

    public List<AppUser> enableUserList() {
        List<AppUser> lstTemp = super.list()
        List<AppUser> lstAppUser = []
        for (int i = 0; i < lstTemp.size(); i++) {
            AppUser appUser = (AppUser) lstTemp[i]
            if (appUser.enabled) {
                lstAppUser << appUser
            }
        }
        return lstAppUser
    }

    public Map findByIsCompanyUser(BaseService baseService) {
        List<AppUser> lstTemp = super.list()
        List<AppUser> lstAppUser = []
        for (int i = 0; i < lstTemp.size(); i++) {
            AppUser appUser = (AppUser) lstTemp[i]
            if (!appUser.isCompanyUser) {
                lstAppUser << appUser
            }
        }
        int end = lstAppUser.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstAppUser.size()
        List lstResult = lstAppUser.subList(baseService.start, end)
        return [lstAppUser: lstResult, count: lstAppUser.size()]
    }
}
