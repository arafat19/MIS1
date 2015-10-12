package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetThemeContentTagLibActionService

/**
 * ThemeTagLib methods used to render theme content(text,css etc.) for a given key
 */
class ThemeTagLib {

    static namespace = "app"

    GetThemeContentTagLibActionService getThemeContentTagLibActionService

    /**
     * Shows different contents of theme e.g. companyImage, copyright text
     * attr takes the attribute 'name'
     * example: <app:themeContent name="copyrightText"></app:themeContent>
     *
     * @attr name REQUIRED -the key name
     * @attr css -if true, then theme content will be enclosed by style tag
     */

    def themeContent = { attrs, body ->
        String output = getThemeContentTagLibActionService.execute(attrs, request)
        out << output
    }

}
