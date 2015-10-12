package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetSystemEntityByReservedTagLibActionService
import com.athena.mis.utility.Tools

class SystemEntityByReservedTagLib {

    static namespace = "app"

    GetSystemEntityByReservedTagLibActionService getSystemEntityByReservedTagLibActionService

    /**
     * Renders the id of system entity in a hidden field
     * example: <app:systemEntityByReserved typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_TYPE}"
     *          reservedId="${ContentTypeCacheUtility.CONTENT_TYPE_DOCUMENT_ID}"></app:systemEntityByReserved>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr typeId REQUIRED - type of system entity
     * @attr reservedId REQUIRED - reserved id of system entity
     */
    def systemEntityByReserved = { attrs, body ->
        Map preResult = (Map) getSystemEntityByReservedTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getSystemEntityByReservedTagLibActionService.execute(preResult, null)
        out << html
    }
}
