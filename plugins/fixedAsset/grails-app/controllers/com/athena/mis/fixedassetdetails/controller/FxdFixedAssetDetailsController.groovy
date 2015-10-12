package com.athena.mis.fixedassetdetails.controller

import com.athena.mis.fixedasset.actions.fixedassetdetails.*
import grails.converters.JSON

class FxdFixedAssetDetailsController {

    static allowedMethods = [
            create: "POST",
            select: "POST",
            update: "POST",
            delete: 'POST',
            list: "POST",
            getFixedAssetList: "POST"
    ];

    CreateFixedAssetDetailsActionService createFixedAssetDetailsActionService
    DeleteFixedAssetDetailsActionService deleteFixedAssetDetailsActionService
    GetFixedAssetListByPOIdActionService getFixedAssetListByPOIdActionService
    ListFixedAssetDetailsActionService listFixedAssetDetailsActionService
    SearchFixedAssetDetailsActionService searchFixedAssetDetailsActionService
    SelectFixedAssetDetailsActionService selectFixedAssetDetailsActionService
    ShowFixedAssetDetailsActionService showFixedAssetDetailsActionService
    UpdateFixedAssetDetailsActionService updateFixedAssetDetailsActionService
    /**
     *
     * @return - render fixed asset details page with fxd details list, po list, owner type list
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showFixedAssetDetailsActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) showFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render(view: '/fixedAsset/fxdFixedAssetDetails/show', model: [output: output])
    }
    /**
     *
     * @return - newly created fixed asset details object
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createFixedAssetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createFixedAssetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) createFixedAssetDetailsActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) createFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - object of selected fixed asset details
     */
    def select() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) selectFixedAssetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectFixedAssetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) selectFixedAssetDetailsActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) selectFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) selectFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return  list of fixed asset details including updated one
     */
    def update() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) updateFixedAssetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateFixedAssetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) updateFixedAssetDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) updateFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) updateFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return- fixed asset details object excluding deleted one
     */
    def delete() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        preResult = (LinkedHashMap) deleteFixedAssetDetailsActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteFixedAssetDetailsActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }

        executeResult = (LinkedHashMap) deleteFixedAssetDetailsActionService.execute(params, preResult);
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) deleteFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) deleteFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }
    /**
     *
     * @return - list of fixed asset details
     */
    def list() {
        Map preResult;
        Boolean hasAccess;
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            executeResult = (Map) searchFixedAssetDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) searchFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
            }

        } else {
            executeResult = (Map) listFixedAssetDetailsActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (!isError.booleanValue()) {
                result = (Map) listFixedAssetDetailsActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) listFixedAssetDetailsActionService.buildFailureResultForUI(executeResult);
            }
        }
        render(result as JSON)
    }
    /**
     *
     * @return - list of fixed asset details(items)
     */
    def getFixedAssetList() {
        LinkedHashMap executeResult
        LinkedHashMap result
        LinkedHashMap preResult
        Boolean isError
        String output

        preResult = (LinkedHashMap) getFixedAssetListByPOIdActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getFixedAssetListByPOIdActionService.buildFailureResultForUI(preResult);
            output = result as JSON
            render output
            return;
        }
        executeResult = (LinkedHashMap) getFixedAssetListByPOIdActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) getFixedAssetListByPOIdActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (LinkedHashMap) getFixedAssetListByPOIdActionService.buildSuccessResultForUI(executeResult);
        }
        output = result as JSON
        render output
    }

}
