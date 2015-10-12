package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.GetRmsTaskDetailsTagLibActionService
import com.athena.mis.utility.Tools

class RmsTaskDetailsTagLib {

    static namespace = "rms"

    GetRmsTaskDetailsTagLibActionService getRmsTaskDetailsTagLibActionService

    /**
     * Render html of task details
     * @attr id - id of html component
     * @attr property_name - property of RmsTask (Ex. id, pinNo, refNo)
     * @attr property_value - value to search with (Ex. 123, EHA1)
     * @attr task_object - RmsTask object
     * @attr from_date - start date
     * @attr to_date - end date
     * @attr url REQUIRED - url to reload tag lib
     */

    def taskDetails = { attrs, body ->
        Map preResult = (Map) getRmsTaskDetailsTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getRmsTaskDetailsTagLibActionService.execute(null, preResult)
        out << html
    }
}
