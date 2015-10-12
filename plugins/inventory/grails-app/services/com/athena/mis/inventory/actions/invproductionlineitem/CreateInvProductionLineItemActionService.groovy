package com.athena.mis.inventory.actions.invproductionlineitem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvProductionLineItemService
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Create Inventory Production Line Item
class CreateInvProductionLineItemActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvProductionLineItemService invProductionLineItemService
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility

    private static final String FAILURE_MESSAGE = 'Production line item could not be saved'
    private static final String SUCCESS_MESSAGE = 'Production line item has been saved successfully'
    private static final String INV_PRODUCTION_LINE_ITEM = "invProductionLineItem"
    private static final String PRODUCTION_LINE_ITEM_EXIST_MSG = "Production line item name already exist"

    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) obj

            //check duplicate Production line item Name
            int duplicateName = checkUniqueProductionLineItemNameCreate(invProductionLineItem)
            if (duplicateName > 0) {
                result.put(Tools.MESSAGE, PRODUCTION_LINE_ITEM_EXIST_MSG)
                return result
            }

            invProductionLineItem.validate()
            if (invProductionLineItem.hasErrors()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            result.put(INV_PRODUCTION_LINE_ITEM, invProductionLineItem)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvProductionLineItem invProductionLineItemInstance = (InvProductionLineItem) preResult.get(INV_PRODUCTION_LINE_ITEM)
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemService.create(invProductionLineItemInstance)
            invProductionLineItemCacheUtility.add(invProductionLineItem, invProductionLineItemCacheUtility.SORT_ON_NAME, invProductionLineItemCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(INV_PRODUCTION_LINE_ITEM, invProductionLineItem)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvProductionLineItem invProductionLineItemInstance = (InvProductionLineItem) preResult.get(INV_PRODUCTION_LINE_ITEM)
            GridEntity object = new GridEntity()
            object.id = invProductionLineItemInstance.id
            object.cell = [
                    Tools.LABEL_NEW,
                    invProductionLineItemInstance.id,
                    invProductionLineItemInstance.name,
                    Tools.STR_ZERO,
                    Tools.STR_ZERO
            ]
            Map resultMap = [entity: object, version: invProductionLineItemInstance.version]
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj.message) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    private static final String SELECT_QUERY = """
            SELECT COUNT(id) AS count
            FROM inv_production_line_item
            WHERE name ilike :name AND
                  company_id =:companyId
    """

    // check unique Production Line Item  for create
    private int checkUniqueProductionLineItemNameCreate(InvProductionLineItem invProductionLineItem) {
        Map queryParams = [
                name: invProductionLineItem.name,
                companyId: invProductionLineItem.companyId
        ]
        List results = executeSelectSql(SELECT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}
