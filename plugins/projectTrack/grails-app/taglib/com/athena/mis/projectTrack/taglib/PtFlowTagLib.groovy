package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetFlowActionService
import com.athena.mis.utility.Tools

class PtFlowTagLib {
    static namespace = "pt"

    GetFlowActionService getFlowActionService

    /**
     * Render task details
     * @attr id REQUIRED -id of html component
     * @attr backlog_id REQUIRED -id of backlog
     * @attr order -Ordering asc/desc
     * @attr template -template of listView
     * @attr result_per_page -no of flow to show (5,10,15)
     * @attr url -url to reload flow
     */
    def flow = { attrs, body ->
        Map preResult = (Map) getFlowActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getFlowActionService.execute(null, preResult)
        out << html
    }
}
