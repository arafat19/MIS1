package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.service.InvProductionDetailsService
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Update Inventory Production Details
class UpdateInvProductionDetailsActionService extends BaseService implements ActionIntf {

    private static final String UPDATE_FAILURE_MESSAGE = "Fail to update Production Details"
    private static final String UPDATE_SUCCESS_MESSAGE = "Production Details has been updated successfully"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String PRODUCTION_DETAILS = "productionDetails"
    private static final String ITEM_ALREADY_EXISTS = "Selected item already exists"

    private final Logger log = Logger.getLogger(getClass())

    InvProductionDetailsService invProductionDetailsService
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvProductionDetails invProductionDetails = (InvProductionDetails) obj
            invProductionDetails.validate()
            if (invProductionDetails.hasErrors()) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }

            long id = invProductionDetails.id
            long materialId = invProductionDetails.materialId
            long productionLineItemId = invProductionDetails.productionLineItemId
            long companyId = invSessionUtil.appSessionUtil.getCompanyId()
            invProductionDetails.companyId = companyId
            int count = InvProductionDetails.countByMaterialIdAndProductionLineItemIdAndIdNotEqualAndCompanyId(materialId, productionLineItemId, id, companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, ITEM_ALREADY_EXISTS)
                return result
            }
            SystemEntity rawMaterial = (SystemEntity) invProductionItemTypeCacheUtility.readByReservedAndCompany(invProductionItemTypeCacheUtility.TYPE_RAW_MATERIAL_ID, companyId)
            if (invProductionDetails.productionItemTypeId == rawMaterial.id) {
                invProductionDetails.overheadCost = 0d
            }
            result.put(PRODUCTION_DETAILS, invProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.FALSE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvProductionDetails invProductionDetails = (InvProductionDetails) preResult.get(PRODUCTION_DETAILS)
            invProductionDetailsService.update(invProductionDetails)
            invProductionDetailsCacheUtility.update(invProductionDetails, invProductionDetailsCacheUtility.SORT_BY_LINE_ITEM_ID, invProductionDetailsCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PRODUCTION_DETAILS, invProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            InvProductionDetails invProductionDetails = (InvProductionDetails) executeResult.get(PRODUCTION_DETAILS)
            SystemEntity invProductionItemType = (SystemEntity) invProductionItemTypeCacheUtility.read(invProductionDetails.productionItemTypeId)
            Item item = (Item) itemCacheUtility.read(invProductionDetails.materialId)
            GridEntity object = new GridEntity()
            object.id = invProductionDetails.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invProductionDetails.id,
                    invProductionItemType.key,
                    item.name,
                    Tools.makeAmountWithThousandSeparator(invProductionDetails.overheadCost)
            ]
            Map resultMap = [entity: object, version: invProductionDetails.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(PRODUCTION_DETAILS, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }
}