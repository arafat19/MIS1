package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetBugListTagLibActionService
import com.athena.mis.utility.Tools

class PtBugListTagLib {
    static namespace = "pt"

    GetBugListTagLibActionService getBugListTagLibActionService

    /**
     * Render bug list according to module
     * @attr id REQUIRED -id of html component
     * @attr module_id REQUIRED -id of module
     * @attr url REQUIRED -url to reload bug list
     */
    def bugList = { attrs, body ->
        Map preResult = (Map) getBugListTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getBugListTagLibActionService.execute(null, preResult)
        out << html
    }
}
