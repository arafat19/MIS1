package com.athena.mis.procurement.actions.purchaserequestdetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.procurement.entity.ProcPurchaseRequestDetails
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Search Purchase Request Details by selected param and display in the grid
 * For details go through Use-Case doc named 'SearchPurchaseRequestDetailsActionService'
 */
class SearchPurchaseRequestDetailsActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    AppUserCacheUtility appUserCacheUtility

    private static final String PURCHASE_REQUEST_LIST_WRAP = "purchaseRequestListWrap"
    private static final String FAILURE_MSG = "Fail to get purchase request list"
    private static final String COUNT = "count"
    private static final String ITEM = "itemId"
    private static final String ITEM_TYPE_ID = "itemTypeId"

    /**
     * @return -a map containing isError(True/False) & hasAccess True
     */
    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
    /**
     * Get purchase request details list
     * 1. pull purchase request list by query param
     * @param params - serialized parameters from UI
     * @param obj -N/A
     * @return - purchase request details list
     */
    @Transactional(readOnly = true)
    Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(params)
            List<Long> lstMatchedItemIds
            List<ProcPurchaseRequestDetails> purchaseRequestDetailsList = []
            int count = 0
            long purchaseRequestId = Long.parseLong(parameterMap.purchaseRequestId.toString())
            if (purchaseRequestId > 0) {
                switch (queryType) {
                    case ITEM:
                        List<Item> lstMatchedItem = (List<Item>) itemCacheUtility.search(itemCacheUtility.NAME, query)
                        lstMatchedItemIds = [0l]
                        for (int i = 0; i < lstMatchedItem.size(); i++) {
                            lstMatchedItemIds << lstMatchedItem[i].id
                        }

                        purchaseRequestDetailsList = ProcPurchaseRequestDetails.findAllByPurchaseRequestIdAndItemIdInList(purchaseRequestId, lstMatchedItemIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                        count = ProcPurchaseRequestDetails.countByPurchaseRequestIdAndItemIdInList(purchaseRequestId, lstMatchedItemIds)
                        break
                    case ITEM_TYPE_ID:
                        List<ItemType> lstMatchedItemTypes = (List<ItemType>) itemTypeCacheUtility.search(itemTypeCacheUtility.NAME, query)
                        List<Item> lstMatchedItems = []
                        lstMatchedItemIds = [0l]
                        //get item
                        for (int i = 0; i < lstMatchedItemTypes.size(); i++) {
                            lstMatchedItems = itemCacheUtility.listByItemTypeId(Long.parseLong(lstMatchedItemTypes[i].id.toString()))
                            //get item id
                            for (int j = 0; j < lstMatchedItems.size(); j++) {
                                lstMatchedItemIds << lstMatchedItems[j].id
                            }
                        }

                        purchaseRequestDetailsList = ProcPurchaseRequestDetails.findAllByPurchaseRequestIdAndItemIdInList(purchaseRequestId, lstMatchedItemIds, [offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true])
                        count = ProcPurchaseRequestDetails.countByPurchaseRequestIdAndItemIdInList(purchaseRequestId, lstMatchedItemIds)
                        break
                    default:
                        purchaseRequestDetailsList = ProcPurchaseRequestDetails.search(purchaseRequestId, queryType, query).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder, readOnly: true)
                        count = ProcPurchaseRequestDetails.search(purchaseRequestId, queryType, query).count()
                }
            }
            List purchaseRequestListWrap = (List) wrapPurchaseRequestListInGridEntityList(purchaseRequestDetailsList, start)
            result.put(PURCHASE_REQUEST_LIST_WRAP, purchaseRequestListWrap)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }

    }
    /**
     * Get wrapped purchase request details list
     * 1. Wrap grid object
     * 2. Show success message
     * @param purchaseOrderDetailsList -map from execute method
     * @param start -starting index for grid
     * @return -a map containing all objects necessary for grid view
     * -map contains isError(true/false) depending on method success
     */
    private List wrapPurchaseRequestListInGridEntityList(List<ProcPurchaseRequestDetails> purchaseRequestDetailsList, int start) {
        List purchaseRequestDetailList = [] as List
        int counter = start + 1
        Item item
        ItemType itemType
        AppUser systemUser
        for (int i = 0; i < purchaseRequestDetailsList.size(); i++) {
            ProcPurchaseRequestDetails prDetails = purchaseRequestDetailsList[i]
            GridEntity obj = new GridEntity()
            obj.id = prDetails.id
            item = (Item) itemCacheUtility.read(prDetails.itemId)
            systemUser = (AppUser) appUserCacheUtility.read(prDetails.createdBy)
            itemType = (ItemType) itemTypeCacheUtility.read(item.itemTypeId)
            obj.cell = [
                    counter,
                    prDetails.id,
                    itemType.name,
                    item.name,
                    Tools.formatAmountWithoutCurrency(prDetails.quantity) + Tools.SINGLE_SPACE + item.unit,
                    Tools.makeAmountWithThousandSeparator(prDetails.rate),
                    Tools.makeAmountWithThousandSeparator((prDetails.quantity * prDetails.rate)),
                    DateUtility.getLongDateForUI(prDetails.createdOn),
                    systemUser.username

            ]
            purchaseRequestDetailList << obj
            counter++
        }

        return purchaseRequestDetailList
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap for grid
     * @param obj - object from execute method
     * @return - wrapped purchase request details object
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(COUNT)
            List purchaseRequestList = (List) receiveResult.get(PURCHASE_REQUEST_LIST_WRAP)
            result = [page: pageNumber, total: count, rows: purchaseRequestList]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }
}