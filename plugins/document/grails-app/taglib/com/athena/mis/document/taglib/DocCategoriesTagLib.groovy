package com.athena.mis.document.taglib

import com.athena.mis.document.actions.taglib.DocCategoriesDetailsTaglibActionService
import com.athena.mis.document.actions.taglib.DocCategoriesManagersTaglibActionService
import com.athena.mis.document.actions.taglib.DocCountSubCategoriesTaglibActionService
import com.athena.mis.document.actions.taglib.ListDocCategoriesTaglibActionService
import com.athena.mis.utility.Tools

class DocCategoriesTagLib {

    static namespace = "doc"

    ListDocCategoriesTaglibActionService listDocCategoriesTaglibActionService
    DocCategoriesDetailsTaglibActionService docCategoriesDetailsTaglibActionService
    DocCountSubCategoriesTaglibActionService docCountSubCategoriesTaglibActionService
    DocCategoriesManagersTaglibActionService docCategoriesManagersTaglibActionService

    /**
     * Render lisView of my categories
     * example: <doc:listMyCategories name="listView"></doc:listMyCategories>
     * @attr name REQUIRED - name & id of html component
     * @attr onclick - on click event call
     */

    def listMyCategories = { attrs, body ->
        Map preResult = (Map) listDocCategoriesTaglibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String output = listDocCategoriesTaglibActionService.execute(preResult, null)
        out << output
    }

    /**
     * Render count of sub categories
     * example: <doc:countSubCategories category_id="${params.categoryId ? params.categoryId : 0L}"></doc:countSubCategories>
     * @attr category_id REQUIRED - id of html component
     */
    def countSubCategories = { attrs, body ->
        Map preResult = (Map) docCountSubCategoriesTaglibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String output = docCountSubCategoriesTaglibActionService.execute(preResult, null)
        out << output
    }

    /**
     * Render details of categories
     * example: <doc:categoriesDetails category_id="${params.categoryId ? params.categoryId : 0L}"></doc:categoriesDetails>
     * @attr category_id REQUIRED - id of html component
     */
    def categoriesDetails = { attrs, body ->
        Map preResult = (Map) docCategoriesDetailsTaglibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String output = docCategoriesDetailsTaglibActionService.execute(preResult, null)
        out << output
    }

    /**
     * Render lisView of my categories
     * example: <doc:myCategories name="listView"></doc:myCategories>
     * @attr category_id REQUIRED - name & id of html component
     */
    def categoryManagers = { attrs, body ->
        Map preResult = (Map) docCategoriesManagersTaglibActionService.executePreCondition(attrs, null)
        Boolean isError = preResult.isError
        if (isError.booleanValue()) {
            out << Tools.EMPTY_SPACE
            return
        }
        String output = docCategoriesManagersTaglibActionService.execute(preResult, null)
        out << output
    }

}
