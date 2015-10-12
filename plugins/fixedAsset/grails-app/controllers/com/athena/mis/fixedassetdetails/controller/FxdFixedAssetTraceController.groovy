package com.athena.mis.fixedassetdetails.controller

import com.athena.mis.fixedasset.actions.fixedassettrace.*
import grails.converters.JSON

class FxdFixedAssetTraceController {

    static allowedMethods = [
            show: "POST",
            create: "POST",
            list: "POST",
            getItemList: "POST"
    ];

    ShowForFixedAssetTraceActionService showForFixedAssetTraceActionService
    CreateForFixedAssetTraceActionService createForFixedAssetTraceActionService
    ListForFixedAssetTraceActionService listForFixedAssetTraceActionService
    SearchForFixedAssetTraceActionService searchForFixedAssetTraceActionService
    GetItemListByFixedAssetItemIdActionService getItemListByFixedAssetItemIdActionService
    /**
     *
     * @return - render fixed asset trace page with fxd details list
     */
    def show() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        executeResult = (LinkedHashMap) showForFixedAssetTraceActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) showForFixedAssetTraceActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render(view: '/fixedAsset/fxdFixedAssetTrace/show', model: [output: output])
            return;
        }
        result = (LinkedHashMap) showForFixedAssetTraceActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render(view: '/fixedAsset/fxdFixedAssetTrace/show', model: [output: output])
    }
    /**
     *
     * @return - newly created fixed asset trace object
     */
    def create() {
        LinkedHashMap preResult
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output
        preResult = (LinkedHashMap) createForFixedAssetTraceActionService.executePreCondition(params, null)
        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForFixedAssetTraceActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }

        executeResult = (LinkedHashMap) createForFixedAssetTraceActionService.execute(params, preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (LinkedHashMap) createForFixedAssetTraceActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
            return
        }

        result = (LinkedHashMap) createForFixedAssetTraceActionService.buildSuccessResultForUI(executeResult)
        output = result as JSON
        render output
    }
    /**
     *
     * @return - list of fixed asset trace
     */
    def list() {
        LinkedHashMap executeResult
        LinkedHashMap result
        Boolean isError
        String output

        // if it's a search request
        if (params.query) {
            executeResult = (LinkedHashMap) searchForFixedAssetTraceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) searchForFixedAssetTraceActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (LinkedHashMap) searchForFixedAssetTraceActionService.buildSuccessResultForUI(executeResult)
            }
        } else { // normal listing
            executeResult = (LinkedHashMap) listForFixedAssetTraceActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (LinkedHashMap) listForFixedAssetTraceActionService.buildFailureResultForUI(executeResult)
                output = result as JSON
                render output
                return;
            }
            result = (LinkedHashMap) listForFixedAssetTraceActionService.buildSuccessResultForUI(executeResult)
        }

        output = result as JSON
        render output
    }

    /**
     * Get List of Items When a Category (Fixed Asset) select at Drop Down
     */
    def getItemList() {
        String output
        Map result
        Map preResult = (Map) getItemListByFixedAssetItemIdActionService.executePreCondition(params, null)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) getItemListByFixedAssetItemIdActionService.buildFailureResultForUI(preResult)
            output = result as JSON
            render output
            return
        }
        Map executeResult = ((Map) getItemListByFixedAssetItemIdActionService.execute(params, null))
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getItemListByFixedAssetItemIdActionService.buildFailureResultForUI(executeResult)
        } else {
            result = (Map) getItemListByFixedAssetItemIdActionService.buildSuccessResultForUI(executeResult)
        }
        output = result as JSON
        render output
    }
}
