package com.athena.mis.document.controller

import com.athena.mis.document.actions.subcategory.AddOrRemoveSubCategoryFavouriteActionService
import com.athena.mis.document.actions.subcategory.CreateDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.DeleteDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.ListDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.ListSubCategoryFavouriteActionService
import com.athena.mis.document.actions.subcategory.SearchDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.SearchSubCategoryFavouriteActionService
import com.athena.mis.document.actions.subcategory.SelectDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.ShowDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.ShowDocSubCategoryForAllUserActionService
import com.athena.mis.document.actions.subcategory.UpdateDocSubCategoryActionService
import com.athena.mis.document.actions.subcategory.UploadDocSubCategoryDocumentActionService
import com.athena.mis.document.actions.subcategory.ViewDocMySubCategoryDetailsActionService
import grails.converters.JSON

class DocSubCategoryController {

    static allowedMethods = [show  : 'post', create: 'post', select: 'post',
                             update: 'post', delete: 'post', list: 'post', showMySubCategories: 'post', dropDownSubCategoryReload: 'post']

    ShowDocSubCategoryActionService showDocSubCategoryActionService
    CreateDocSubCategoryActionService createDocSubCategoryActionService
    SelectDocSubCategoryActionService selectDocSubCategoryActionService
    UpdateDocSubCategoryActionService updateDocSubCategoryActionService
    DeleteDocSubCategoryActionService deleteDocSubCategoryActionService
    ListDocSubCategoryActionService listDocSubCategoryActionService
    SearchDocSubCategoryActionService searchDocSubCategoryActionService
    ViewDocMySubCategoryDetailsActionService viewDocMySubCategoryDetailsActionService
    UploadDocSubCategoryDocumentActionService uploadDocSubCategoryDocumentActionService
    ShowDocSubCategoryForAllUserActionService showDocSubCategoryForAllUserActionService
    ListSubCategoryFavouriteActionService listSubCategoryFavouriteActionService
    SearchSubCategoryFavouriteActionService searchSubCategoryFavouriteActionService
    AddOrRemoveSubCategoryFavouriteActionService addOrRemoveSubCategoryFavouriteActionService

    /*
     * Show Sub Category List
     * */

    def show() {
        Map preResult, executeResult, result
        Boolean isError

        preResult = (Map) showDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) showDocSubCategoryActionService.buildFailureResultForUI(preResult)
            render(view: '/document/docSubCategory/show', model: [modelJson: result as JSON])
            return
        }
        executeResult = (Map) showDocSubCategoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/document/docSubCategory/show', model: [modelJson: result as JSON])
    }

    /*
    * Create A New Sub Category
    * */

    def create() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) createDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createDocSubCategoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(output)
            return
        }

        executeResult = (Map) createDocSubCategoryActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /*
    * Update a sub category
    * */

    def update() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) updateDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateDocSubCategoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) updateDocSubCategoryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * Delete a sub category
    * */

    def delete() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) deleteDocSubCategoryActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteDocSubCategoryActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        executeResult = (Map) deleteDocSubCategoryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }

    /*
    * List/Search sub Category
    * */

    def list() {
        Map executeResult, result
        String output
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchDocSubCategoryActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchDocSubCategoryActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listDocSubCategoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listDocSubCategoryActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
            }
        }
        output = result as JSON
        render output
    }
    /*
    * Select a sub Category from grid
    * */

    def select() {
        Map executeResult, result
        Boolean isError

        executeResult = (Map) selectDocSubCategoryActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) selectDocSubCategoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) selectDocSubCategoryActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    /*
    * Show My Sub Categories for ListView
    * */

    def showSubCategories() {
//        GrailsParameterMap parameters = (GrailsParameterMap) params
        println "params > " + params
        render(view: '/document/docMySubCategory/show', model: [categoryId: params.categoryId, subCategoryId: params.subCategoryId ? params.subCategoryId : 0L])
    }

    /*
    * View My Sub Category Details
    * */

    def viewSubCategoryDetails() {
        render(template: '/document/docMySubCategory/mySubCategoryDetails')

        /*  Map executeResult, result
          Boolean isError

          executeResult = (Map) viewDocMySubCategoryDetailsActionService.execute(params, null)
          isError = (Boolean) executeResult.isError
          if (!isError.booleanValue()) {
              result = (Map) viewDocMySubCategoryDetailsActionService.buildSuccessResultForUI(executeResult)
          } else {
              result = (Map) viewDocMySubCategoryDetailsActionService.buildFailureResultForUI(executeResult)
          }
          render(template: '/document/docMySubCategory/mySubCategoryDetails', model: [subCategoryDetails: result.subCategoryDetails])*/
    }

    /*
    * Upload Sub Category Documents
    * */

    def uploadDocSubCategoryDocument() {
        Map preResult, executeResult, result
        Boolean isError
        String output

        preResult = (Map) uploadDocSubCategoryDocumentActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) uploadDocSubCategoryDocumentActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render(output)
            return
        }

        executeResult = (Map) uploadDocSubCategoryDocumentActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) uploadDocSubCategoryDocumentActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) uploadDocSubCategoryDocumentActionService.buildFailureResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    def dropDownSubCategoryReload() {
        render doc.dropDownSubCategory(params)
    }

    /*
    * Show sub category For All Generic Users
    * */

    def showSubCategory() {
        Map executeResult, result
        Boolean isError
        String output
        flash.message = null

        executeResult = (Map) showDocSubCategoryForAllUserActionService.execute(params, request)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showDocSubCategoryForAllUserActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showDocSubCategoryForAllUserActionService.buildFailureResultForUI(executeResult)
        }
        output = result as JSON
        render(view: '/document/docSubCategory/showForGenericUser', model: [modelJson: output])
    }

    def listSubCategoryFavourite() {
        Map executeResult, result
        Boolean isError
        if (params.query) {
            executeResult = (Map) searchSubCategoryFavouriteActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) searchSubCategoryFavouriteActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) searchSubCategoryFavouriteActionService.buildSuccessResultForUI(executeResult)
            }
        } else {
            executeResult = (Map) listSubCategoryFavouriteActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listSubCategoryFavouriteActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) listSubCategoryFavouriteActionService.buildSuccessResultForUI(executeResult);
            }
        }
        render(result as JSON)
    }

    def addOrRemoveSubCategoryFavourite() {
        Map preResult, executeResult, result
        Boolean isError

        preResult = (Map) addOrRemoveSubCategoryFavouriteActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) addOrRemoveSubCategoryFavouriteActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }
        executeResult = (Map) addOrRemoveSubCategoryFavouriteActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) addOrRemoveSubCategoryFavouriteActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) addOrRemoveSubCategoryFavouriteActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

}
