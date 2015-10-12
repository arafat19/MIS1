package com.athena.mis.application.taglib

import com.athena.mis.application.actions.taglib.GetDropDownSystemEntityTagLibActionService
import com.athena.mis.utility.Tools

class SystemEntityDropDownTagLib {

    static namespace = "app"

    GetDropDownSystemEntityTagLibActionService getDropDownSystemEntityTagLibActionService

    /**
     * Render html select of systemEntity for the given systemEntityType id
     * example: <app:dropDownSystemEntity typeId="${SystemEntityTypeCacheUtility.TYPE_UNIT}"></app:dropDownSystemEntity>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr typeId REQUIRED - id of systemEntityType domain
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText -No selection text (Default is Please Select...)
     * @attr showHints -Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr elements - List of individual elements to populate (reserved SystemEntity.id)
     * (objects should be available in List of given type)
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */

    def dropDownSystemEntity = { attrs, body ->
        Map preResult = (Map) getDropDownSystemEntityTagLibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String html = getDropDownSystemEntityTagLibActionService.execute(preResult, null)
        out << html
    }
}
