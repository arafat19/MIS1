package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvProductionDetailsService
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Create Inventory Production Details
class CreateInvProductionDetailsActionService extends BaseService implements ActionIntf {

    InvProductionDetailsService invProductionDetailsService
    @Autowired
    InvProductionItemTypeCacheUtility invProductionItemTypeCacheUtility
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    private static final String PRODUCTION_DETAILS_CREATE_FAILURE_MSG = "Production Details has not been saved"
    private static final String PRODUCTION_DETAILS_CREATE_SUCCESS_MSG = "Production Details has been successfully saved"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Given inputs are not valid"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create Production Details"
    private static final String PRODUCTION_DETAILS = "productionDetails"
    private static final String ITEM_ALREADY_EXISTS = "Selected item already exists"

    private final Logger log = Logger.getLogger(getClass())

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
            invProductionDetails.companyId = invSessionUtil.appSessionUtil.getCompanyId()
            int count = InvProductionDetails.countByMaterialIdAndProductionLineItemIdAndCompanyId(invProductionDetails.materialId,
                    invProductionDetails.productionLineItemId,invProductionDetails.companyId)
            if (count > 0) {
                result.put(Tools.MESSAGE, ITEM_ALREADY_EXISTS)
                return result
            }

            result.put(PRODUCTION_DETAILS, invProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvProductionDetails invProductionDetails = (InvProductionDetails) preResult.get(PRODUCTION_DETAILS)
            InvProductionDetails newProductionDetails = invProductionDetailsService.create(invProductionDetails)
            invProductionDetailsCacheUtility.add(invProductionDetails, invProductionDetailsCacheUtility.SORT_BY_LINE_ITEM_ID, invProductionDetailsCacheUtility.SORT_ORDER_ASCENDING)
            result.put(PRODUCTION_DETAILS, newProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(PRODUCTION_DETAILS_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_DETAILS_CREATE_FAILURE_MSG)
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
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(invProductionDetails.productionLineItemId)
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
            result.put(Tools.MESSAGE, PRODUCTION_DETAILS_CREATE_SUCCESS_MSG)
            result.put(PRODUCTION_DETAILS, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_DETAILS_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, PRODUCTION_DETAILS_CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PRODUCTION_DETAILS_CREATE_FAILURE_MSG)
            return result
        }
    }
}
