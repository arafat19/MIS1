package com.athena.mis.projecttrack.taglib

import com.athena.mis.projecttrack.actions.taglib.GetAcceptanceCriteriaActionService
import com.athena.mis.utility.Tools

class PtAcceptanceCriteriaTagLib {
    static namespace = "pt"

    GetAcceptanceCriteriaActionService getAcceptanceCriteriaActionService

    /**
     * Render task details
     * @attr id REQUIRED -id of html component
     * @attr backlog_id REQUIRED -id of backlog
     * @attr type_id REQUIRED -id of acceptance criteria type (e.g. pre-condition, post-condition, business logic)
     * @attr template -template of listView
     * @attr url -url to reload acceptance criteria
     */
    def acceptanceCriteria = { attrs, body ->
        Map preResult = (Map) getAcceptanceCriteriaActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getAcceptanceCriteriaActionService.execute(null, preResult)
        out << html
    }
}
