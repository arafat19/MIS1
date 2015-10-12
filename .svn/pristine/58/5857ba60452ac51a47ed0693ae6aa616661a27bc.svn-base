package com.athena.mis.application.config

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Theme
import com.athena.mis.application.service.ThemeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('themeCacheUtility')
class ThemeCacheUtility extends ExtendedCacheUtility {
    // theme key constants
    public static final String KEY_WELCOME_TEXT = "app.welcomeTitle"
    public static final String KEY_COMPANY_LOGO_LEFTMENU = "app.leftMenu.CompanyLogo"
    public static final String KEY_COMPANY_NAME = "app.companyName"
    public static final String KEY_COMPANY_COPYRIGHT_LEFTMENU = "app.leftMenu.companyCopyright"
    public static final String KEY_COMPANY_COPYRIGHT_LOGIN = "app.login.companyCopyright"
    public static final String KEY_PRODUCT_NAME = "app.login.productName"
    public static final String KEY_COMPANY_WEBSITE = "app.companyWebsite"
    public static final String KEY_IMG_LOGIN_TOP_RIGHT = "app.login.imgTopRight"
    public static final String KEY_IMG_TOP_PANEL_LEFT = "app.imgTopPanelLeft"
    public static final String KEY_IMG_TOP_PANEL_RIGHT = "app.imgTopPanelRight"
    public static final String KEY_CSS_MAIN_COMPONENTS = "app.cssMainComponents"
    public static final String KEY_CSS_BOOTSTRAP_CUSTOM = "app.cssBootstrapCustom"
    public static final String KEY_CSS_KENDO_CUSTOM = "app.cssKendoCustom"
    public static final String KEY_LOGIN_PAGE_CAUTION = "app.login.pageCaution"
    public static final String KEY_BUSINESS_SUPPORT = "app.login.businessSupport"
    public static final String KEY_ADVERTISING_PHRASE = "app.login.advertisingPhrase"
    public static final String KEY_KENDO_THEME = "app.kendoTheme"

    @Autowired
    ThemeService themeService

    public void init() {
        List list = themeService.list()
        if (list) {
            super.setList(list)
        }
    }

    public Theme readByKeyAndCompany(String key, long companyId) {
        List<Theme> lstTheme = (List<Theme>) list(companyId)
        for (int i = 0; i < lstTheme.size(); i++) {
            if ((lstTheme[i].key.equals(key))) {
                return lstTheme[i]
            }
        }
        return null
    }

    public Theme findById(long themeId) {
        List<Theme> lstTheme = (List<Theme>) list()
        for (int i = 0; i < lstTheme.size(); i++) {
            if (lstTheme[i].id == themeId) {
                return lstTheme[i]
            }
        }
        return null
    }
}
