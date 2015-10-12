/* delete inventory after association check
*  - placed in application plugin */
package com.athena.mis.inventory.actions.invinventory

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventory
import com.athena.mis.inventory.service.InvInventoryService
import com.athena.mis.inventory.utility.InvInventoryCacheUtility
import com.athena.mis.inventory.utility.InvTransactionEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.inventory.utility.InvUserInventoryCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteInvInventoryActionService extends BaseService implements ActionIntf {
    InvInventoryService invInventoryService
    @Autowired
    InvInventoryCacheUtility invInventoryCacheUtility
    @Autowired
    InvUserInventoryCacheUtility invUserInventoryCacheUtility
    @Autowired
    InvTransactionEntityTypeCacheUtility invTransactionEntityTypeCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String DELETE_INVENTORY_SUCCESS_MESSAGE = "Inventory has been deleted successfully"
    private static final String DELETE_INVENTORY_FAILURE_MESSAGE = "Inventory could not be deleted"
    private static final String HAS_SUPPLIER_IN_ASSOCIATION_MESSAGE = " SUPPLIER-IN is associated with selected inventory"
    private static final String HAS_INVENTORY_IN_ASSOCIATION_MESSAGE = " INVENTORY-IN is associated with selected inventory"
    private static final String HAS_INVENTORY_OUT_ASSOCIATION_MESSAGE = " INVENTORY-OUT is associated with selected inventory"
    private static final String HAS_INVENTORY_CONSUMPTION_ASSOCIATION_MESSAGE = " INVENTORY-CONSUMPTION is associated with selected inventory"
    private static final String HAS_INVENTORY_PRODUCTION_ASSOCIATION_MESSAGE = " INVENTORY-PRODUCTION is associated with selected inventory"
    private static final String HAS_QS_DETAILS_ASSOCIATION_MESSAGE = " QS Details is associated with selected inventory"
    private static final String HAS_USER_INVENTORY_ASSOCIATION_MESSAGE = "Can not delete this inventory due to user's mapping with selected inventory"
    private static final String INVENTORY_OBJ = "inventory"

    private final Logger log = Logger.getLogger(getClass())

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!invSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }

            GrailsParameterMap params = (GrailsParameterMap) parameters
            long inventoryId = Long.parseLong(params.id.toString())

            InvInventory invInventory = (InvInventory) invInventoryCacheUtility.read(inventoryId)

            if (!invInventory) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Map preResult = (Map) hasAssociation(invInventory)

            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)

            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(INVENTORY_OBJ, invInventory)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object Parameters, Object obj) {
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            InvInventory invInventory = (InvInventory) receivedResult.get(INVENTORY_OBJ)
            Boolean result = invInventoryService.delete(invInventory.id)
            invInventoryCacheUtility.delete(invInventory.id)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete inventory')
            return Boolean.FALSE
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_INVENTORY_SUCCESS_MESSAGE]
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
            result.put(Tools.MESSAGE, DELETE_INVENTORY_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_INVENTORY_FAILURE_MESSAGE)
            return result
        }
    }

    private LinkedHashMap hasAssociation(InvInventory invInventory) {
        LinkedHashMap result = new LinkedHashMap()
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()

        long inventoryId = invInventory.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        count = countInventoryInFromSupplier(inventoryId, companyId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_SUPPLIER_IN_ASSOCIATION_MESSAGE)
            return result
        }

        count = countInventoryInFromInventory(inventoryId, companyId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_INVENTORY_IN_ASSOCIATION_MESSAGE)
            return result
        }

        count = countInventoryOutFromInventory(inventoryId, companyId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_INVENTORY_OUT_ASSOCIATION_MESSAGE)
            return result
        }

        count = countInventoryConsumption(inventoryId, companyId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_INVENTORY_CONSUMPTION_ASSOCIATION_MESSAGE)
            return result
        }

        count = countInventoryProduction(inventoryId, companyId)
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_INVENTORY_PRODUCTION_ASSOCIATION_MESSAGE)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
            count = countQsMeasurement(inventoryId)
            if (count.intValue() > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_QS_DETAILS_ASSOCIATION_MESSAGE)
                return result
            }
        }

        count = invUserInventoryCacheUtility.countByInventoryId(inventoryId)
        if (count > 0) {
            result.put(Tools.MESSAGE, HAS_USER_INVENTORY_ASSOCIATION_MESSAGE)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String INVENTORY_IN_FROM_SUPPLIER_COUNT_QUERY = """
            SELECT COUNT(id)
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
                  transaction_entity_type_id=:transactionEntityTypeId AND
                  transaction_type_id=:transactionTypeId
    """

    private int countInventoryInFromSupplier(long inventoryId, long companyId) {
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntitySupplier = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_SUPPLIER, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionEntityTypeId: transactionEntitySupplier.id,
                transactionTypeId: transactionTypeIn.id
        ]
        List results = executeSelectSql(INVENTORY_IN_FROM_SUPPLIER_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String INVENTORY_IN_FROM_INVENTORY_COUNT_QUERY = """
            SELECT COUNT(id)
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
                  transaction_entity_type_id=:transactionEntityTypeId AND
                  transaction_type_id=:transactionTypeId
    """

    private int countInventoryInFromInventory(long inventoryId, long companyId) {
        SystemEntity transactionTypeIn = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_IN, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionEntityTypeId: transactionEntityInventory.id,
                transactionTypeId: transactionTypeIn.id
        ]
        List results = executeSelectSql(INVENTORY_IN_FROM_INVENTORY_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String INVENTORY_OUT_FROM_INVENTORY_COUNT_QUERY = """
            SELECT COUNT(id)
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
                  transaction_entity_type_id=:transactionEntityTypeId AND
                  transaction_type_id=:transactionTypeId
    """

    private int countInventoryOutFromInventory(long inventoryId, long companyId) {
        SystemEntity transactionTypeOut = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_OUT, companyId)
        SystemEntity transactionEntityInventory = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_INVENTORY, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionEntityTypeId: transactionEntityInventory.id,
                transactionTypeId: transactionTypeOut.id
        ]
        List results = executeSelectSql(INVENTORY_OUT_FROM_INVENTORY_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String INVENTORY_CONSUMPTION_COUNT_QUERY = """
            SELECT COUNT(id)
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
                  transaction_entity_type_id=:transactionEntityTypeId AND
                  transaction_type_id=:transactionTypeId
    """

    private int countInventoryConsumption(long inventoryId, long companyId) {
        SystemEntity transactionTypeConsumption = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionEntityTypeId: transactionEntityNone.id,
                transactionTypeId: transactionTypeConsumption.id
        ]
        List results = executeSelectSql(INVENTORY_CONSUMPTION_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String INVENTORY_PRODUCTION_COUNT_QUERY = """
            SELECT COUNT(id)
            FROM inv_inventory_transaction
            WHERE inventory_id=:inventoryId AND
                  transaction_entity_type_id=:transactionEntityTypeId AND
                  transaction_type_id=:transactionTypeId
    """

    private int countInventoryProduction(long inventoryId, long companyId) {
        SystemEntity transactionTypeProduction = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)
        SystemEntity transactionEntityNone = (SystemEntity) invTransactionEntityTypeCacheUtility.readByReservedAndCompany(invTransactionEntityTypeCacheUtility.ENTITY_TYPE_NONE, companyId)
        Map queryParams = [
                inventoryId: inventoryId,
                transactionEntityTypeId: transactionEntityNone.id,
                transactionTypeId: transactionTypeProduction.id
        ]
        List results = executeSelectSql(INVENTORY_PRODUCTION_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QS_MEAS_COUNT_QUERY = """
            SELECT COUNT(id) FROM qs_measurement
            WHERE site_id=:inventoryId
    """

    private int countQsMeasurement(long inventoryId) {
        Map queryParams = [inventoryId: inventoryId]
        List results = executeSelectSql(QS_MEAS_COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}
