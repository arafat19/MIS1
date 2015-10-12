package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvProductionDetails
import com.athena.mis.inventory.service.InvProductionDetailsService
import com.athena.mis.inventory.utility.InvProductionDetailsCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Delete Inventory Production Details
class DeleteInvProductionDetailsActionService extends BaseService implements ActionIntf {

    InvProductionDetailsService invProductionDetailsService
    @Autowired
    InvProductionDetailsCacheUtility invProductionDetailsCacheUtility

    private static final String DELETE_PRODUCTION_DETAILS_SUCCESS_MESSAGE = "Production details has been deleted successfully"
    private static final String DELETE_PRODUCTION_DETAILS_FAILURE_MESSAGE = "Production details could not be deleted, Refresh the production details list"
    private static final String PRODUCTION_DETAILS_OBJ = "productionDetails"
    private static final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " Inventory transaction is associated with this item"

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long ProductionDetailsId = Long.parseLong(params.id.toString())
            InvProductionDetails invProductionDetails = (InvProductionDetails) invProductionDetailsCacheUtility.read(ProductionDetailsId)
            if (!invProductionDetails) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            Map preResult = (Map) hasAssociation(invProductionDetails)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }
            result.put(PRODUCTION_DETAILS_OBJ, invProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRODUCTION_DETAILS_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object Parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            InvProductionDetails invProductionDetails = (InvProductionDetails) preResult.get(PRODUCTION_DETAILS_OBJ)
            Boolean deleteResult = invProductionDetailsService.delete(invProductionDetails.id)
            invProductionDetailsCacheUtility.delete(invProductionDetails.id)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('failed to delete production details')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRODUCTION_DETAILS_FAILURE_MESSAGE)
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
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.MESSAGE, DELETE_PRODUCTION_DETAILS_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRODUCTION_DETAILS_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, DELETE_PRODUCTION_DETAILS_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_PRODUCTION_DETAILS_FAILURE_MESSAGE)
            return result
        }
    }

    private LinkedHashMap hasAssociation(InvProductionDetails invProductionDetails) {

        LinkedHashMap result = new LinkedHashMap()
        long invProductionLintItemId = invProductionDetails.productionLineItemId
        long materialId = invProductionDetails.materialId
        Integer count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = countProductionDetails(invProductionLintItemId, materialId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION)
            return result
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String PROD_DETAILS_QUERY = """
        SELECT COUNT(itd.id)
        FROM inv_inventory_transaction_details itd
        LEFT JOIN inv_inventory_transaction it ON it.id = itd.inventory_transaction_id
        WHERE it.inv_production_line_item_id=:invProductionLintItemId
        AND itd.item_id=:materialId
    """

    private int countProductionDetails(long invProductionLintItemId, long materialId) {
        Map queryParams = [
                invProductionLintItemId: invProductionLintItemId,
                materialId: materialId
        ]
        List results = executeSelectSql(PROD_DETAILS_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}