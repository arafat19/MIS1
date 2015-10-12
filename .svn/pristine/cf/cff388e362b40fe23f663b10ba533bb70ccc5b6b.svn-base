package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDateControlTagLibActionService
import com.athena.mis.utility.Tools

class DateControlTagLib {

    static namespace = "app"

    GetDateControlTagLibActionService getDateControlTagLibActionService

    /**
     * Renders date control
     * example: <app:dateControl value="${toDate}"></app:dateControl>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr value - date value to be shown (default is current date)
     * @attr onchange - method to call on change
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr diffWithCurrent - difference with current date
     * @attr placeholder - text to be shown
     * @attr disabled - disables the field
     */
    def dateControl = { attrs, body ->
        Map preResult = (Map) getDateControlTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDateControlTagLibActionService.execute(preResult, null)
        out << html
    }
}
