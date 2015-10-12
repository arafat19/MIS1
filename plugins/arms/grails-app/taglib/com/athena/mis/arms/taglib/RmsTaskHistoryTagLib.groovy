package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.GetRmsTaskHistoryTagLibActionService
import com.athena.mis.utility.Tools

class RmsTaskHistoryTagLib {
    static namespace = "rms"

    GetRmsTaskHistoryTagLibActionService getRmsTaskHistoryTagLibActionService

    /**
     * Render html of task history
     * @attr id - id of html component
     * @attr propertyName REQUIRED - property of RmsTask (Ex. id, pinNo, refNo)
     * @attr propertyValue REQUIRED - value to search with (Ex. 123, EHA1)
     * @attr fromDate - start date
     * @attr toDate - end date
     */

    def taskHistory = { attrs, body ->
        Map preResult = (Map) getRmsTaskHistoryTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getRmsTaskHistoryTagLibActionService.execute(null, preResult)
        out << html
    }
}
