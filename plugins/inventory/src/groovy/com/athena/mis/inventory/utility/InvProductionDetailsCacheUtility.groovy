package com.athena.mis.inventory.utility

import com.athena.mis.BaseService
import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.service.InvProductionDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('invProductionDetailsCacheUtility')
class InvProductionDetailsCacheUtility extends ExtendedCacheUtility {
    @Autowired
    InvProductionDetailsService invProductionDetailsService
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    static final String SORT_BY_LINE_ITEM_ID = "productionLineItemId"

    public void init() {
        List list = invProductionDetailsService.list()
        super.setList(list)
    }

    public Map listByProductionItemTypeId(long productionLineItemId, BaseService baseService) {
        List<InvProductionDetails> productionItemTypeList = super.list()
        List<InvProductionDetails> lstSearchResult = []

        int listSize = productionItemTypeList.size()
        int i = 0
        while (i < listSize) {
            if (productionItemTypeList[i].productionLineItemId == productionLineItemId) {
                lstSearchResult << productionItemTypeList[i]
            }
            i++
        }
        int end = lstSearchResult.size() > (baseService.start + baseService.resultPerPage) ? (baseService.start + baseService.resultPerPage) : lstSearchResult.size()
        List lstResult = lstSearchResult.subList(baseService.start, end)
        return [list: lstResult, count: lstSearchResult.size()]
    }

    private static final String LINE_ITEM_ID = 'productionLineItemId'
    private static final String LINE_ITEM_TYPE_ID = 'productionItemTypeId'
    private static final String MATERIAL_ID = 'materialId'

    // return a map containing IDs of Raw-materials and Finished materials for a given lineItem
    public Map getBothMaterialsByLineItem(long lineItem) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity rawMaterial = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_RAW_MATERIAL_ID, companyId)
        SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)

        List listMain = super.list()
        List lstLineItem = listMain.findAll { it.properties.get(LINE_ITEM_ID) == lineItem }
        List lstRaw = lstLineItem.findAll { it.properties.get(LINE_ITEM_TYPE_ID) == rawMaterial.id }
        List lstFinished = lstLineItem.findAll { it.properties.get(LINE_ITEM_TYPE_ID) == finishedProduct.id }
        Map result = [lstRaw: lstRaw.collect { it.materialId }, lstFinished: lstFinished.collect { it.materialId }]
        return result
    }

    public Object getProdDetailsByLineItemAndItemId(long lineItemId, long itemId) {
        List listMain = super.list()
        for (int i = 0; i < listMain.size(); i++) {
            if ((listMain[i].productionLineItemId == lineItemId) && (listMain[i].materialId == itemId)) {
                return listMain[i]
            }
        }
        return null
    }

    public Map getFinishedMaterialsByLineItem(long lineItem) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)

        List listMain = super.list()
        List lstLineItem = listMain.findAll { it.properties.get(LINE_ITEM_ID) == lineItem }
        List lstFinished = lstLineItem.findAll { it.properties.get(LINE_ITEM_TYPE_ID) == finishedProduct.id }
        Map result = [lstFinished: lstFinished]
        return result
    }
}
