package com.athena.mis.inventory.actions.invproductionlineitem

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvProductionLineItem
import com.athena.mis.inventory.service.InvProductionLineItemService
import com.athena.mis.inventory.utility.InvProductionLineItemCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Delete Inventory Production Line Item
class DeleteInvProductionLineItemActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    InvProductionLineItemService invProductionLineItemService
    @Autowired
    InvProductionLineItemCacheUtility invProductionLineItemCacheUtility

    private static final String SUCCESS_MESSAGE = "Production line item has been deleted successfully"
    private static final String FAILURE_MESSAGE = "Production line item could not be deleted, refresh the Production line item list"
    private static final String PRODUCTION_LINE_ITEM_HAS_ITEM = "Production line item could not be deleted due to the existence of its item(s)"

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long invProductionLineItemId = Long.parseLong(params.id.toString())
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(invProductionLineItemId)
            if (!invProductionLineItem) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            int invProductionDetails = readByProductionLineItemId(invProductionLineItemId)

            if (invProductionDetails > 0) {
                result.put(Tools.MESSAGE, PRODUCTION_LINE_ITEM_HAS_ITEM)
                return result
            }

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
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            long invProductionLineItemId = Long.parseLong(parameterMap.id.toString())
            Boolean deleteResult = (Boolean) invProductionLineItemService.delete(invProductionLineItemId)
            InvProductionLineItem invProductionLineItem = (InvProductionLineItem) invProductionLineItemCacheUtility.read(invProductionLineItemId)
            invProductionLineItemCacheUtility.delete(invProductionLineItem.id)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete production line item')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
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
                if (preResult.message) {
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

    private static final String PROD_LINE_ITM_QUERY = """
        SELECT
        COUNT(id)
        FROM inv_production_details
        WHERE production_line_item_id=:productionLineItemId
    """

    private int readByProductionLineItemId(long productionLineItemId) {
        Map queryParams = [
                productionLineItemId: productionLineItemId
        ]
        List results = executeSelectSql(PROD_LINE_ITM_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}
