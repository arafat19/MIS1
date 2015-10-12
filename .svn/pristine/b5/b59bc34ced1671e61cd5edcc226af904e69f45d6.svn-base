package com.athena.mis.inventory.actions.invproductionlineitem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

// Search Inventory Production Line Item
class SearchInvProductionLineItemActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search Production Line Item grid"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    public Object execute(Object params, Object obj = null) {
        try {
            if ((!params.sortname) || (params.sortname.toString().equals(ID))) {
                params.sortname = invProductionLineItemCacheUtility.SORT_ON_NAME
                params.sortorder = invProductionLineItemCacheUtility.SORT_ORDER_ASCENDING
            }
            initSearch(params)
            Map searchResult = invProductionLineItemCacheUtility.search(queryType, query, this)
            List<InvProductionLineItem> invProductionLineItemList = searchResult.list
            int count = searchResult.count
            return [invProductionLineItemList: invProductionLineItemList, count: count]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj
            List<InvProductionLineItem> invProductionLineItemList = (List<InvProductionLineItem>) executeResult.invProductionLineItemList
            int count = (int) executeResult.count
            List invProductionLineItem = wrapListInGridEntityList(invProductionLineItemList, start)
            return [page: pageNumber, total: count, rows: invProductionLineItem]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<InvProductionLineItem> invProductionLineItemList, int start) {
        List ProductionLineItems = []
        int counter = start + 1

        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity rawMaterial = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_RAW_MATERIAL_ID, companyId)
        SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)

        for (int i = 0; i < invProductionLineItemList.size(); i++) {
            InvProductionLineItem invProductionLineItem = invProductionLineItemList[i]

            long id = invProductionLineItem.id
            int countRawMaterial = InvProductionDetails.countByProductionLineItemIdAndProductionItemTypeId(id, rawMaterial.id)
            int countFinishedMaterial = InvProductionDetails.countByProductionLineItemIdAndProductionItemTypeId(id, finishedProduct.id)

            GridEntity obj = new GridEntity()
            obj.id = invProductionLineItem.id
            obj.cell = [
                    counter,
                    invProductionLineItem.id,
                    invProductionLineItem.name,
                    countRawMaterial,
                    countFinishedMaterial
            ]
            ProductionLineItems << obj
            counter++
        }
        return ProductionLineItems
    }
}
