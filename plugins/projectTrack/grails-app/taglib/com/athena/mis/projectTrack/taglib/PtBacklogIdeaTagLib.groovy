package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetBacklogIdeaTagLibActionService
import com.athena.mis.utility.Tools

class PtBacklogIdeaTagLib {
    static namespace = "pt"

    GetBacklogIdeaTagLibActionService getBacklogIdeaTagLibActionService

    /**
     * Render idea of backlog by backlog ID
     * @attr id REQUIRED -id of html component
     * @attr backlogId REQUIRED -id of backlog
     */
    def backlogIdea = { attrs, body ->
        Map preResult = (Map) getBacklogIdeaTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getBacklogIdeaTagLibActionService.execute(null, preResult)
        out << html
    }
}
