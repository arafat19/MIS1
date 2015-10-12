package com.athena.mis.application.taglib

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired

/**
 * Retrieve different property values of session user
 */
class SessionUserTagLib {

    static namespace = "app"

    @Autowired
    AppSessionUtil appSessionUtil

    private static final String USER_NAME = "username"
    private static final String SHORT_USER_NAME = "shortUserName"
    private static final String PROPERTY = "property"

    /**
     * Shows logged-in user information within tag
     * attr takes the attribute 'property' (which exists in AppUser domain)
     * example: <app:sessionUser property="username"></app:sessionUser>
     *
     * @attr property REQUIRED -the property name of AppUse domain
     */
    def sessionUser = { attrs, body ->

        String property = attrs.remove(PROPERTY)

        AppUser appUser = appSessionUtil.getAppUser()
        if (!appUser) {
            out << Tools.EMPTY_SPACE
            return
        }
        String output = null
        switch (property) {
            case USER_NAME:
                output = appUser.username
                break
            case SHORT_USER_NAME:
                output = appUser.username.tokenize(Tools.SINGLE_SPACE).first()
                break
            default:
                output = Tools.EMPTY_SPACE   // default value
        }
        out << output
    }
}
