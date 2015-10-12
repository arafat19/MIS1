package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetSysConfigUserRegistrationActionService
import com.athena.mis.application.actions.taglib.ShowSysConfigTagLibActionService
import com.athena.mis.utility.Tools

class SystemConfigurationTagLib {

    static namespace = "app"

    ShowSysConfigTagLibActionService showSysConfigTagLibActionService
    GetSysConfigUserRegistrationActionService getSysConfigUserRegistrationActionService

    /**
     * Renders the value if system configuration exists
     * example: <app:showSysConfig key="key" pluginId="1"></app:showSysConfig>
     *
     * @attr key REQUIRED - key of system configuration
     * @attr pluginId REQUIRED - plugin id
     */
    def showSysConfig = { attrs, body ->
        String strValue = showSysConfigTagLibActionService.execute(attrs, null)
        if (!strValue) {
            out << Tools.EMPTY_SPACE
            return
        }
        out << strValue
    }

/**
 * Renders the internal content (link to user registration page)
 * if Exh Module installed & if new user registration is enabled in Exh sysConfig
 */
    def isUserRegistrationEnabled = { attrs, body ->
        Boolean isEnabled = getSysConfigUserRegistrationActionService.execute(attrs, request)
        if (!isEnabled.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        out << body()
    }





}
