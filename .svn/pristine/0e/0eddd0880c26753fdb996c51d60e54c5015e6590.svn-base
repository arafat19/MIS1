package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.ThemeCacheUtility
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.Theme
import com.athena.mis.application.utility.CompanyCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.servlet.http.HttpServletRequest

class GetThemeContentTagLibActionService extends BaseService implements ActionIntf {

    @Autowired
    ThemeCacheUtility themeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    CompanyCacheUtility companyCacheUtility


    private static final String STR_NAME = 'name'
    private static final String STR_CSS = 'css'
    private static final String STYLE_TAG_START = "<style>\n"
    private static final String STYLE_TAG_END = "\n</style>"

    private Logger log = Logger.getLogger(getClass())


    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }


    public Object execute(Object parameters, Object obj) {
        try {
            Map attrs = (Map) parameters
            String name = attrs.get(STR_NAME)
            HttpServletRequest request = (HttpServletRequest) obj
            long companyId = getCompanyId(request)
            if (companyId == 0) {
                return Tools.EMPTY_SPACE
            }
            Theme theme = themeCacheUtility.readByKeyAndCompany(name, companyId)
            if (!theme) {
                return Tools.EMPTY_SPACE
            }
            // now check if CSS content
            boolean isCss = false   // default value
            if (attrs.css) {
                isCss = Boolean.parseBoolean(attrs.get(STR_CSS).toString())
            }
            if (!isCss) {
                return theme.value

            }
            // enclose style tag
            return STYLE_TAG_START + theme.value + STYLE_TAG_END

        } catch (Exception e) {
            log.error(e.message)
            return Tools.EMPTY_SPACE
        }
    }


    private long getCompanyId(HttpServletRequest request) {
        AppUser appUser = appSessionUtil.getAppUser()
        if (appUser) {
            return appUser.companyId
        }
        String fullUrl = Tools.getFullUrl(request, false)    // retrieve url with www
        Company company = companyCacheUtility.readByWebUrl(fullUrl) // compare with www
        if (company) {
            return company.id
        }
        // if company not found try to retrieve url without www
        fullUrl = Tools.getFullUrl(request, true)
        company = companyCacheUtility.readByWebUrlWithoutWWW(fullUrl)     // compare without www
        if (company) {
            return company.id
        } else {
            return 0L
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

}
