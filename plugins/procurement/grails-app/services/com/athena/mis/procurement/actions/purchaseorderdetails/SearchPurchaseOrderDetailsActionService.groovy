package com.athena.mis.procurement.actions.purchaseorderdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search Purchase Order by Details selected param and display in the grid
 * For details go through Use-Case doc named 'SearchPurchaseOrderDetailsActionService'
 */
class SearchPurchaseOrderDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass());

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    private static final String PURCHASE_ORDER_NOT_FOUND = "Purchase Order not found"
    private static final String SERVER_ERROR_MESSAGE = "Fail to get purchase order details"
    private static final String PURCHASE_ORDER_DETAILS_LIST = "purchaseOrderDetailsList"
    private static final String COUNT = "count"
    private static final String ITEM_ID = "itemId"
    private static final String ITEM_TYPE_ID = "itemTypeId"

    /**
     * 1. check po existence
     * @param parameters - serialize parameters from UI
     * @param obj -N/A
     * @return - isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            long purchaseOrderId = Long.parseLong(params.purchaseOrderId.toString())

            if (purchaseOrderId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, PURCHASE_ORDER_NOT_FOUND)
                return result;
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Get purchase order details list
     * 1. pull purchase order list by query param
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - purchase order details list
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.sortname) || (params.sortname.toString().equals(super.ID))) {
                params.sortname = DEFAULT_SORT_COLUMN; ;
            }
            initSearch(params);
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long purchaseOrderId = Long.parseLong(parameterMap.purchaseOrderId.toString())
            List<ProcPurchaseOrderDetails> purchaseOrderDetailsList = []
            int count = 0
            Map serviceReturn

            switch (queryType) {
                case ITEM_ID:
                    List<Item> itemList = (List<Item>) itemCacheUtility.search(itemCacheUtility.NAME, query)
                    List<Long> itemIdList = [0l]
                    for (int i = 0; i < itemList.size(); i++) {
                        itemIdList[i] = itemList[i].id
                    }
                    purchaseOrderDetailsList = ProcPurchaseOrderDetails.findAllByPurchaseOrderIdAndItemIdInList(purchaseOrderId, itemIdList, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseOrderDetails.countByPurchaseOrderIdAndItemIdInList(purchaseOrderId, itemIdList)
                    break;
                case ITEM_TYPE_ID:
                    List<ItemType> lstMatchedItemTypes = (List<ItemType>) itemTypeCacheUtility.search(itemTypeCacheUtility.NAME, query)
                    List<Item> lstMatchedItems = []
                    List<Long> lstMatchedItemIds = [0l]
                    //get item
                    for (int i = 0; i < lstMatchedItemTypes.size(); i++) {
                        lstMatchedItems = itemCacheUtility.listByItemTypeId(Long.parseLong(lstMatchedItemTypes[i].id.toString()))
                        //get item id
                        for (int j = 0; j < lstMatchedItems.size(); j++) {
                            lstMatchedItemIds << lstMatchedItems[j].id
                        }
                    }
                    purchaseOrderDetailsList = ProcPurchaseOrderDetails.findAllByPurchaseOrderIdAndItemIdInList(purchaseOrderId, lstMatchedItemIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseOrderDetails.countByPurchaseOrderIdAndItemIdInList(purchaseOrderId, lstMatchedItemIds)
                    break;
                default:
                    purchaseOrderDetailsList = ProcPurchaseOrderDetails.findAllByPurchaseOrderId(purchaseOrderId, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                    count = ProcPurchaseOrderDetails.countByPurchaseOrderId(purchaseOrderId)
            }

            result.put(PURCHASE_ORDER_DETAILS_LIST, purchaseOrderDetailsList)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return null
        }

    }
    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }
    /**
     * Wrap for grid
     * @param obj - object from execute method
     * @return - wrapped purchase order details object
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map output = null
        try {
            Map executeResult = (Map) obj
            List<ProcPurchaseOrderDetails> purchaseOrderDetailsList = (List<ProcPurchaseOrderDetails>) executeResult.purchaseOrderDetailsList
            int count = (int) executeResult.count
            def purchaseOrderDetails = wrapPurchaseOrderDetailsListInGridEntityList(purchaseOrderDetailsList, start)
            return [page: pageNumber, total: count, rows: purchaseOrderDetails]
        } catch (Exception ex) {
            log.error(ex.getMessage());
            output = [page: pageNumber, total: 0, rows: null]
            return output
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                failureResult.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            log.error(ex.getMessage());
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, SERVER_ERROR_MESSAGE)
            return failureResult
        }
    }
    /**
     * Get wrapped purchase order details list
     * 1. Wrap grid object
     * 2. Show success message
     * @param purchaseOrderDetailsList -map from execute method
     * @param start starting index for grid
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    private List wrapPurchaseOrderDetailsListInGridEntityList(List<ProcPurchaseOrderDetails> purchaseOrderDetailsList, int start) {
        List purchaseOrders = [] as List
        int counter = start + 1
        String valDate
        Item item
        ItemType itemType
        String quantityWithUnit
        String createdOn
        AppUser createdBy
        for (int i = 0; i < purchaseOrderDetailsList.size(); i++) {
            ProcPurchaseOrderDetails purchaseOrderDetails = purchaseOrderDetailsList[i]
            GridEntity obj = new GridEntity()
            obj.id = purchaseOrderDetails.id
            item = (Item) itemCacheUtility.read(purchaseOrderDetails.itemId)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            quantityWithUnit = Tools.formatAmountWithoutCurrency(purchaseOrderDetails.quantity) + Tools.SINGLE_SPACE + item.unit
            createdOn = DateUtility.getLongDateForUI(purchaseOrderDetails.createdOn)
            createdBy = (AppUser) appUserCacheUtility.read(purchaseOrderDetails.createdBy)
            double total = purchaseOrderDetails.quantity * purchaseOrderDetails.rate
            obj.cell = [
                    counter,
                    purchaseOrderDetails.id,
                    itemType.name,
                    item.name,
                    quantityWithUnit,
                    Tools.makeAmountWithThousandSeparator(purchaseOrderDetails.rate),
                    Tools.makeAmountWithThousandSeparator(total),
                    createdBy.username,
                    createdOn
            ]
            purchaseOrders << obj
            counter++
        }

        return purchaseOrders
    }
}
