package com.athena.mis.exchangehouse.taglib

import com.athena.mis.exchangehouse.actions.taglib.CheckExhSysConfigTagLibActionService
import com.athena.mis.utility.Tools

class ExhSystemConfigurationTagLib {

    static namespace = "exh"

    CheckExhSysConfigTagLibActionService checkExhSysConfigTagLibActionService

    /**
     * Renders the body if system configuration exists
     * example: <exh:checkSysConfig key="key" value="value"></exh:checkSysConfig>
     *
     * @attr key REQUIRED - key of system configuration
     * @attr value REQUIRED - value of system configuration
     */
    def checkSysConfig = { attrs, body ->
        Boolean hasSysConfig = checkExhSysConfigTagLibActionService.execute(attrs, null)
        if (hasSysConfig.booleanValue()) {
            out << body()
        } else {
            out << Tools.EMPTY_SPACE
        }
    }
}
