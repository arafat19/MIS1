package com.athena.mis.inventory.actions.invproductionlineitem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvProductionLineItemService
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

//Update Inventory Production Line Item
class UpdateInvProductionLineItemActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvProductionLineItemService invProductionLineItemService
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String FAILURE_MESSAGE = "Production line item could not be updated"
    private static final String SUCCESS_MESSAGE = "Production line item has been updated successfully"
    private static final String INV_PRODUCTION_LINE_ITEM = "invProductionLineItem"
    private static final String PRODUCTION_LINE_ITEM_EXIST_MSG = "Production line item name already exist"

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvProductionLineItem InvProductionLineItemInstance = (InvProductionLineItem) obj
            int duplicateName = InvProductionLineItem.countByNameIlikeAndIdNotEqualAndCompanyId(InvProductionLineItemInstance.name, InvProductionLineItemInstance.id, InvProductionLineItemInstance.companyId)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, PRODUCTION_LINE_ITEM_EXIST_MSG)
                return result
            }
            InvProductionLineItemInstance.validate()
            if (InvProductionLineItemInstance.hasErrors()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(INV_PRODUCTION_LINE_ITEM, InvProductionLineItemInstance)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) preResult.get(INV_PRODUCTION_LINE_ITEM)
            invProductionLineItemService.update(invProductionLineItem)
            invProductionLineItemCacheUtility.update(invProductionLineItem, invProductionLineItemCacheUtility.SORT_ON_NAME, invProductionLineItemCacheUtility.SORT_ORDER_ASCENDING)
            result.put(INV_PRODUCTION_LINE_ITEM, invProductionLineItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            return new Integer(0)
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            InvProductionLineItem productionLineItem = (InvProductionLineItem) executeResult.get(INV_PRODUCTION_LINE_ITEM)

            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            SystemEntity rawMaterial = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_RAW_MATERIAL_ID, companyId)
            SystemEntity finishedProduct = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID, companyId)

            long id = productionLineItem.id
            int countRawMaterial = InvProductionDetails.countByProductionLineItemIdAndProductionItemTypeId(id, rawMaterial.id)
            int countFinishedMaterial = InvProductionDetails.countByProductionLineItemIdAndProductionItemTypeId(id, finishedProduct.id)

            GridEntity object = new GridEntity()
            object.id = productionLineItem.id
            object.cell = [
                    Tools.LABEL_NEW,
                    productionLineItem.id,
                    productionLineItem.name,
                    countRawMaterial,
                    countFinishedMaterial
            ]

            Map resultMap = [entity: object, version: productionLineItem.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(INV_PRODUCTION_LINE_ITEM, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
}
