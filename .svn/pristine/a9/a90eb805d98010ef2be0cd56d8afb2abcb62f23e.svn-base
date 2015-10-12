package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.ItemCategoryCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvProductionLineItemService
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Show Inventory Production Details
class ShowInvProductionDetailsActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load production details page"
    private static final String PRODUCTION_DETAILS_LIST = "productionDetailsList"
    private static final String PRODUCTION_LINE_ITEM_NOT_FOUND = "Production line item not found"
    private static final String PRODUCTION_LINE_ITEM_INFO = "productionLineItemInfo"
    private static final String PRODUCTION_LINE_ITEM_ID = "productionLineItemId"
    private static final String RAW_MATERIAL_LIST = "rawMaterialList"
    private static final String FINISHED_MATERIAL_LIST = "finishedMaterialList"
    private static final String COUNT = "count"

    InvProductionLineItemService invProductionLineItemService
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    ItemCategoryCacheUtility itemCategoryCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long productionLineItemId = Long.parseLong(params.productionLineItemId.toString())
            if (productionLineItemId < 0) {
                result.put(Tools.IS_ERROR, Boolean.TRUE)
                result.put(Tools.MESSAGE, PRODUCTION_LINE_ITEM_NOT_FOUND)
                return result
            }
            result.put(PRODUCTION_LINE_ITEM_ID, productionLineItemId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception   ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            long productionLineItemId = Long.parseLong(preResult.get(PRODUCTION_LINE_ITEM_ID).toString())
            InvProductionLineItem invProductionLineItem = invProductionLineItemService.read(productionLineItemId)
            if (!invProductionLineItem) {
                result.put(Tools.MESSAGE, PRODUCTION_LINE_ITEM_NOT_FOUND)
                return result
            }
            initPager(params)
            Map lstResult = invProductionDetailsCacheUtility.listByProductionItemTypeId(productionLineItemId, this)
            List<InvProductionDetails> invProductionDetailsList = lstResult.list
            int count = lstResult.count
            InvProductionLineItem productionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(invProductionLineItem.id)
            Map productionLineItemInfo = [productionLineItemid: productionLineItem.id, productionLineItemname: productionLineItem.name]

            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            List<GroovyRowResult> rawMaterialList = getRawMaterialList(itemCategoryCacheUtility.INVENTORY, companyId)
            List<GroovyRowResult> finishedMaterialList = getFinishedMaterialList(itemCategoryCacheUtility.INVENTORY, companyId)

            result.put(PRODUCTION_DETAILS_LIST, invProductionDetailsList)
            result.put(PRODUCTION_LINE_ITEM_INFO, productionLineItemInfo)
            result.put(RAW_MATERIAL_LIST, rawMaterialList)
            result.put(FINISHED_MATERIAL_LIST, finishedMaterialList)

            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List productionDetailsList = (List) executeResult.get(PRODUCTION_DETAILS_LIST)
            List<GroovyRowResult> rawMaterialList = (List<GroovyRowResult>) executeResult.get(RAW_MATERIAL_LIST)
            List<GroovyRowResult> finishedMaterialList = (List<GroovyRowResult>) executeResult.get(FINISHED_MATERIAL_LIST)
            Map productionLineItemInfo = (LinkedHashMap) executeResult.get(PRODUCTION_LINE_ITEM_INFO)
            int count = (int) executeResult.get(COUNT)
            List productionDetailsListWrap = wrapListInGridEntityList(productionDetailsList, start)
            Map gridOutput = [page: pageNumber, total: count, rows: productionDetailsListWrap]
            result.put(PRODUCTION_LINE_ITEM_INFO, productionLineItemInfo)
            result.put(PRODUCTION_DETAILS_LIST, gridOutput)
            result.put(RAW_MATERIAL_LIST, Tools.listForKendoDropdown(rawMaterialList, null, null))
            result.put(FINISHED_MATERIAL_LIST, Tools.listForKendoDropdown(finishedMaterialList, null, null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<InvProductionDetails> productionDetailsList, int start) {
        List productionDetails = [] as List
        int counter = start + 1

        for (int i = 0; i < productionDetailsList.size(); i++) {
            SystemEntity invProductionItemType = (SystemEntity) invProductionItemTypeCacheUtility.read(productionDetailsList[i].productionItemTypeId)
            Item item = (Item) itemCacheUtility.read(productionDetailsList[i].materialId)
            GridEntity obj = new GridEntity()
            obj.id = productionDetailsList[i].id
            obj.cell = [
                    counter,
                    productionDetailsList[i].id,
                    invProductionItemType.key,
                    item.name,
                    Tools.makeAmountWithThousandSeparator(productionDetailsList[i].overheadCost)
            ]
            productionDetails << obj
            counter++
        }
        return productionDetails
    }

    private static final String LST_RAW_MAT_QUERY = """
            SELECT id, name FROM item
            WHERE category_id=:itemCategoryInventoryId
              AND company_id=:companyId
            ORDER BY name
        """

    private List<GroovyRowResult> getRawMaterialList(long itemCategoryInventoryId, long companyId) {
        SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryInventoryId, companyId)
        Map queryParams = [
                itemCategoryInventoryId: itemSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> rawMaterialList = executeSelectSql(LST_RAW_MAT_QUERY, queryParams)
        return rawMaterialList
    }

    private static final String LST_FINISHED_RAW_MAT_QUERY = """
        SELECT id, name FROM item
        WHERE category_id=:itemCategoryInventoryId AND
              is_finished_product = TRUE AND
              company_id=:companyId
        ORDER BY name
    """

    private List<GroovyRowResult> getFinishedMaterialList(long itemCategoryInventoryId, long companyId) {
        SystemEntity itemSysEntityObject = (SystemEntity) itemCategoryCacheUtility.readByReservedAndCompany(itemCategoryInventoryId, companyId)
        Map queryParams = [
                itemCategoryInventoryId: itemSysEntityObject.id,
                companyId: companyId
        ]
        List<GroovyRowResult> finishedMaterialList = executeSelectSql(LST_FINISHED_RAW_MAT_QUERY, queryParams)
        return finishedMaterialList
    }
}