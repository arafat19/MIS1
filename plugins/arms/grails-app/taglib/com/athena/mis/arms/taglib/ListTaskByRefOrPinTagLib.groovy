package com.athena.mis.arms.taglib

import com.athena.mis.arms.actions.taglib.ListTaskByRefOrPinTagLibActionService
import com.athena.mis.utility.Tools

class ListTaskByRefOrPinTagLib {

    static namespace = "rms"

    ListTaskByRefOrPinTagLibActionService listTaskByRefOrPinTagLibActionService

    /**
     * Render grid of task list
     * @attr property_name - property of RmsTask (Ex. id, pinNo, refNo)
     * @attr property_value - value to search with (Ex. 123, EHA1)
     * @attr task_list - List of RmsTask
     * @attr from_date - start date
     * @attr to_date - end date
     */

    def listTaskByRefOrPin = { attrs, body ->
        Map preResult = (Map) listTaskByRefOrPinTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = listTaskByRefOrPinTagLibActionService.execute(null, preResult)
        out << html
    }
}
