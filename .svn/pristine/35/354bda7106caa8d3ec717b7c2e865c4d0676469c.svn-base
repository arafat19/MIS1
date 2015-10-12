package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetEntityNoteTagLibActionService
import com.athena.mis.utility.Tools

class EntityNoteTagLib {

    static namespace = "app"

    GetEntityNoteTagLibActionService getEntityNoteTagLibActionService

    /**
     * Render task details
     * @attr id REQUIRED - id of html component
     * @attr entity_id - property of task (Ex. id, pinNo, refNo)
     * @attr entity_type_id REQUIRED - value to search with (Ex. 123, EHA1)
     * @attr order - Ordering asc/desc
     * @attr template - template of listView
     * @attr result_per_page  - no of notes to show (5,10,15)
     * @attr url  - url to reload entity notes
     */

    def entityNote = { attrs, body ->
        Map preResult = (Map) getEntityNoteTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getEntityNoteTagLibActionService.execute(null, preResult)
        out << html
    }
}
