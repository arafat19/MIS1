package com.athena.mis.application.controller

import com.athena.mis.application.actions.item.*
import com.athena.mis.application.entity.Item
import com.athena.mis.utility.UIConstants
import grails.converters.JSON
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class ItemController {
    static allowedMethods = [
            listItemByItemTypeId: "POST", select: "POST",

            createFixedAssetItem: "POST", showFixedAssetItem: "POST",
            updateFixedAssetItem: "POST", deleteFixedAssetItem: "POST",
            listFixedAssetItem: "POST",

            showNonInventoryItem: "POST", listNonInventoryItem: "POST",
            deleteNonInventoryItem: "POST", createNonInventoryItem: "POST",
            updateNonInventoryItem: "POST",

            showInventoryItem: "POST", listInventoryItem: "POST",
            deleteInventoryItem: "POST", createInventoryItem: "POST",
            updateInventoryItem: "POST"
    ]

    SelectItemActionService selectItemActionService

    //Fixed Asset Item
    ShowItemCategoryFixedAssetActionService showItemCategoryFixedAssetActionService
    CreateItemCategoryFixedAssetActionService createItemCategoryFixedAssetActionService
    UpdateItemCategoryFixedAssetActionService updateItemCategoryFixedAssetActionService
    DeleteItemCategoryFixedAssetActionService deleteItemCategoryFixedAssetActionService
    ListItemCategoryFixedAssetActionService listItemCategoryFixedAssetActionService
    SearchItemCategoryFixedAssetActionService searchItemCategoryFixedAssetActionService

    GetItemListByItemTypeActionService getItemListByItemTypeActionService

    //Non Inventory Item
    ShowItemCategoryNonInvActionService showItemCategoryNonInvActionService
    ListItemCategoryNonInvActionService listItemCategoryNonInvActionService
    CreateItemCategoryNonInvActionService createItemCategoryNonInvActionService
    UpdateItemCategoryNonInvActionService updateItemCategoryNonInvActionService
    SearchItemCategoryNonInvActionService searchItemCategoryNonInvActionService
    DeleteItemCategoryNonInvActionService deleteItemCategoryNonInvActionService

    // Inventory item
    ShowItemCategoryInventoryActionService showItemCategoryInventoryActionService
    ListItemCategoryInventoryActionService listItemCategoryInventoryActionService
    CreateItemCategoryInventoryActionService createItemCategoryInventoryActionService
    UpdateItemCategoryInventoryActionService updateItemCategoryInventoryActionService
    SearchItemCategoryInventoryActionService searchItemCategoryInventoryActionService
    DeleteItemCategoryInventoryActionService deleteItemCategoryInventoryActionService

    def select() {
        Map result
        Map executeResult = (Map) selectItemActionService.execute(params, null);
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) selectItemActionService.buildFailureResultForUI(executeResult);
        } else {
            result = (Map) selectItemActionService.buildSuccessResultForUI(executeResult);
        }
        render result as JSON
    }

    //---------- Start : Fixed Asset Item ----------
    def showFixedAssetItem() {
        Map preResult
        Map result
        Map executeResult
        Boolean hasAccess
        Boolean isError
        preResult = (Map) showItemCategoryFixedAssetActionService.executePreCondition(null, null)
        hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        executeResult = (Map) showItemCategoryFixedAssetActionService.execute(params, null)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showItemCategoryFixedAssetActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) showItemCategoryFixedAssetActionService.buildFailureResultForUI(executeResult)
        }
        render(view: '/application/item/showFixedAsset', model: [output: result as JSON])
    }

    def createFixedAssetItem() {
        Map result
        Boolean isError

        Map preResult = (Map) createItemCategoryFixedAssetActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createItemCategoryFixedAssetActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Map executeResult = (Map) createItemCategoryFixedAssetActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createItemCategoryFixedAssetActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) createItemCategoryFixedAssetActionService.buildFailureResultForUI(executeResult)
        }

        render(result as JSON)
    }

    def updateFixedAssetItem() {
        Map result
        Boolean isError

        Map preResult = (Map) updateItemCategoryFixedAssetActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateItemCategoryFixedAssetActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) updateItemCategoryFixedAssetActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateItemCategoryFixedAssetActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateItemCategoryFixedAssetActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def deleteFixedAssetItem() {
        Map preResult
        Map result
        Map executeResult
        Boolean isError

        preResult = (Map) deleteItemCategoryFixedAssetActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteItemCategoryFixedAssetActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        executeResult = (Map) deleteItemCategoryFixedAssetActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) deleteItemCategoryFixedAssetActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) deleteItemCategoryFixedAssetActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def listFixedAssetItem() {
        Boolean isError
        Map result
        Map executeResult
        if (params.query) {
            executeResult = (Map) searchItemCategoryFixedAssetActionService.execute(params, null)
            if (executeResult) {
                result = (Map) searchItemCategoryFixedAssetActionService.buildSuccessResultForUI(executeResult)
            } else {
                result = (Map) searchItemCategoryFixedAssetActionService.buildFailureResultForUI(executeResult)
            }

        } else {
            executeResult = (Map) listItemCategoryFixedAssetActionService.execute(params, null)
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listItemCategoryFixedAssetActionService.buildFailureResultForUI(executeResult)
            } else {
                result = (Map) listItemCategoryFixedAssetActionService.buildSuccessResultForUI(executeResult)
            }
        }
        render(result as JSON)
    }

    //---------- End : Fixed Asset Item ----------

    //---------- Start : Non-Inventory Item ----------
    def showNonInventoryItem() {
        Map result
        Boolean isError
        Map preResult = (Map) showItemCategoryNonInvActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showItemCategoryNonInvActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showItemCategoryNonInvActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showItemCategoryNonInvActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/application/item/showNonInventory', model: [output: result as JSON])
    }

    def createNonInventoryItem() {
        Map result
        Boolean isError

        Map preResult = (Map) createItemCategoryNonInvActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createItemCategoryNonInvActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Map executeResult = (Map) createItemCategoryNonInvActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createItemCategoryNonInvActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createItemCategoryNonInvActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    def updateNonInventoryItem() {
        Map result
        Boolean isError

        Map preResult = (Map) updateItemCategoryNonInvActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateItemCategoryNonInvActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) updateItemCategoryNonInvActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateItemCategoryNonInvActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateItemCategoryNonInvActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def deleteNonInventoryItem() {
        Map result
        Map preResult = (Map) deleteItemCategoryNonInvActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteItemCategoryNonInvActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        boolean deleteResult = ((Boolean) deleteItemCategoryNonInvActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteItemCategoryNonInvActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteItemCategoryNonInvActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def listNonInventoryItem() {
        Map preResult;
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            preResult = (Map) searchItemCategoryNonInvActionService.executePreCondition(null, null)
            Boolean hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }

            executeResult = (Map) searchItemCategoryNonInvActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchItemCategoryNonInvActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchItemCategoryNonInvActionService.buildFailureResultForUI(executeResult);
            }
        } else {
            preResult = (Map) listItemCategoryNonInvActionService.executePreCondition(null, null)
            Boolean hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }

            executeResult = (Map) listItemCategoryNonInvActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listItemCategoryNonInvActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) listItemCategoryNonInvActionService.buildSuccessResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }
    //---------- End : Non-Inventory Item ----------

    //---------- Start : Inventory Item ----------
    def showInventoryItem() {
        Map result
        Boolean isError
        Map preResult = (Map) showItemCategoryInventoryActionService.executePreCondition(null, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }
        Map executeResult = (Map) showItemCategoryInventoryActionService.execute(params, null);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) showItemCategoryInventoryActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) showItemCategoryInventoryActionService.buildFailureResultForUI(executeResult);
        }
        render(view: '/application/item/showInventory', model: [output: result as JSON])
    }

    def createInventoryItem() {
        Map result
        Boolean isError

        Map preResult = (Map) createItemCategoryInventoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess;
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) createItemCategoryInventoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        Map executeResult = (Map) createItemCategoryInventoryActionService.execute(null, preResult);
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) createItemCategoryInventoryActionService.buildSuccessResultForUI(executeResult);
        } else {
            result = (Map) createItemCategoryInventoryActionService.buildFailureResultForUI(executeResult);
        }

        render(result as JSON)
    }

    def updateInventoryItem() {
        Map result
        Boolean isError

        Map preResult = (Map) updateItemCategoryInventoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) updateItemCategoryInventoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return
        }

        Map executeResult = (Map) updateItemCategoryInventoryActionService.execute(null, preResult)
        isError = (Boolean) executeResult.isError
        if (!isError.booleanValue()) {
            result = (Map) updateItemCategoryInventoryActionService.buildSuccessResultForUI(executeResult)
        } else {
            result = (Map) updateItemCategoryInventoryActionService.buildFailureResultForUI(executeResult)
        }
        render(result as JSON)
    }

    def deleteInventoryItem() {
        Map result
        Map preResult = (Map) deleteItemCategoryInventoryActionService.executePreCondition(params, null)
        Boolean hasAccess = (Boolean) preResult.hasAccess
        if (!hasAccess.booleanValue()) {
            redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
            return
        }

        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            result = (Map) deleteItemCategoryInventoryActionService.buildFailureResultForUI(preResult)
            render(result as JSON)
            return;
        }

        boolean deleteResult = ((Boolean) deleteItemCategoryInventoryActionService.execute(params, null));
        if (deleteResult.booleanValue()) {
            result = (Map) deleteItemCategoryInventoryActionService.buildSuccessResultForUI(null);
        } else {
            result = (Map) deleteItemCategoryInventoryActionService.buildFailureResultForUI(null);
        }
        render(result as JSON)
    }

    def listInventoryItem() {
        Map preResult;
        Boolean isError
        Map result;
        Map executeResult;
        if (params.query) {
            preResult = (Map) searchItemCategoryInventoryActionService.executePreCondition(null, null)
            Boolean hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }

            executeResult = (Map) searchItemCategoryInventoryActionService.execute(params, null);
            if (executeResult) {
                result = (Map) searchItemCategoryInventoryActionService.buildSuccessResultForUI(executeResult);
            } else {
                result = (Map) searchItemCategoryInventoryActionService.buildFailureResultForUI(executeResult);
            }
        } else {
            preResult = (Map) listItemCategoryInventoryActionService.executePreCondition(null, null)
            Boolean hasAccess = (Boolean) preResult.hasAccess
            if (!hasAccess.booleanValue()) {
                redirect(url: createLink(uri: '/' + UIConstants.REDIRECT_NO_ACCESS_PAGE))
                return
            }

            executeResult = (Map) listItemCategoryInventoryActionService.execute(params, null);
            isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result = (Map) listItemCategoryInventoryActionService.buildFailureResultForUI(executeResult);
            } else {
                result = (Map) listItemCategoryInventoryActionService.buildSuccessResultForUI(executeResult);
            }
        }
        render(result as JSON);
    }
    //---------- End : Inventory Item ----------

    // get item list by item type id(used in AccLc & AccLeaseAccount CRUD)
    def listItemByItemTypeId() {
        Map result
        String output

        Map executeResult = (Map) getItemListByItemTypeActionService.execute(params, null)
        Boolean isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            result = (Map) getItemListByItemTypeActionService.buildFailureResultForUI(executeResult)
            output = result as JSON
            render output
        }
        output = executeResult as JSON
        render output
    }

    //Build Item Object
    private Item buildItem(GrailsParameterMap params) {
        Item item = new Item(params);
        return item
    }
}
