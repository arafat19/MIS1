package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetBacklogListTagLibActionService
import com.athena.mis.utility.Tools

class PtBacklogListTagLib {
    static namespace = "pt"

    GetBacklogListTagLibActionService getBacklogListTagLibActionService

    /**
     * Render backlog list according to module
     * @attr id REQUIRED -id of html component
     * @attr module_id REQUIRED -id of module
     * @attr url REQUIRED -url to reload backlog list
     */
    def backlogList = { attrs, body ->
        Map preResult = (Map) getBacklogListTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getBacklogListTagLibActionService.execute(null, preResult)
        out << html
    }
}
