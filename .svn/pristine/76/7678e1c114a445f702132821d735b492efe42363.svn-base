/*  delete the record of how much raw materials is consumed against
    how much finished product(s) of specific inventory */
package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.inventory.service.InvInventoryTransactionDetailsService
import com.athena.mis.inventory.service.InvInventoryTransactionService
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class for deleting unapprovedInventoryProduction(parents & children)
 *  For details go through Use-Case doc named 'DeleteForInvProductionWithConsumptionActionService'
 */
class DeleteForInvProductionWithConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PRODUCTION_NOT_FOUND_MASSAGE = "Selected inventory production not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to delete inventory production"
    private static final String INVALID_INPUT_MASSAGE = "Failed to delete inventory production due to invalid ID"
    private static final String SUCCESS_MASSAGE = "Production has been successfully deleted"
    private static final String ENTITY_CONSUMPTION = "entityConsumption"
    private static final String ENTITY_PRODUCTION = "entityProduction"
    private static final String LST_CONSUMPTION_DETAILS = "lstConsumptionDetails"
    private static final String LST_PRODUCTION_DETAILS = "lstProductionDetails"
    private static final String APPROVED_PRODUCTION_DELETE_PROHIBITED = "Approved production can not be deleted."

    InvInventoryTransactionService invInventoryTransactionService
    InvInventoryTransactionDetailsService invInventoryTransactionDetailsService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil

    /**
     * validate different criteria to delete unapprovedInventoryProductionDetails(parent). Such as :
     *      Check existence of InvInventoryTransactions(Both consumption & production Obj)
     *      Check approval of any child
     *      Check availableAmount to delete etc.
     *
     * @Params parameters -Receives InvInventoryTransaction(consumption Id) from UI
     * @Params obj -N/A
     *
     * @Return -a map containing all objects necessary for execute (Parents(invTranConsumption & invTranProduction), Children(invTranConsumptionDetails & invTranProductionDetails))
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if(!parameterMap.id){   //Check parameter(consumed-parent-id) sent from UI
                result.put(Tools.MESSAGE, INVALID_INPUT_MASSAGE)
                return result
            }

            Long consumptionId = Long.parseLong(parameterMap.id.toString())
            InvInventoryTransaction consumptionTransaction = (InvInventoryTransaction) invInventoryTransactionService.read(consumptionId)
            if (!consumptionTransaction) { //Check existence of consumption parent object
                result.put(Tools.MESSAGE, PRODUCTION_NOT_FOUND_MASSAGE)
                return result
            }

            int approveCount = getApproveCountForProdWithCons(consumptionId)
            if (approveCount > 0) { //For any child if approved_by > 0 AND is_current = true, then parent could not be deleted
                result.put(Tools.MESSAGE, APPROVED_PRODUCTION_DELETE_PROHIBITED)
                return result
            }

            // Retrieve production id By consumption id
            InvInventoryTransaction productionTransaction = (InvInventoryTransaction) readProductionByConsumptionId(consumptionTransaction.id)
            if (!productionTransaction) {
                result.put(Tools.MESSAGE, PRODUCTION_NOT_FOUND_MASSAGE)
                return result
            }

            //Get list of consumption and production details(children)
            List<GroovyRowResult> lstConsumptionDetails = listProductionDetailsForDelete(consumptionTransaction)
            List<GroovyRowResult> lstProductionDetails = listProductionDetailsForDelete(productionTransaction)

            //Check availability of each materials only for Production delete
            String stockError
            for (int i = 0; i < lstProductionDetails.size(); i++) {
                //Check Available-Stock of a particular item in an inventory to delete
                stockError = checkStockForProductionMaterial(lstProductionDetails[i])
                if (stockError) {
                    result.put(Tools.MESSAGE, stockError)
                    return result
                }
            }

            result.put(ENTITY_CONSUMPTION, consumptionTransaction)
            result.put(ENTITY_PRODUCTION, productionTransaction)
            result.put(LST_CONSUMPTION_DETAILS, lstConsumptionDetails)
            result.put(LST_PRODUCTION_DETAILS, lstProductionDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     *  Method to delete parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails) of both Consumption and Production
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from executePreCondition which contains parents(InvInventoryTransaction) & children(InvInventoryTransactionDetails)
     *
     * @Return -map contains isError(true/false)
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map receiveResult = (Map) obj
            InvInventoryTransaction consumptionTransaction = (InvInventoryTransaction) receiveResult.get(ENTITY_CONSUMPTION)
            InvInventoryTransaction productionTransaction = (InvInventoryTransaction) receiveResult.get(ENTITY_PRODUCTION)
            List<GroovyRowResult> lstConsumptionDetails = (List<GroovyRowResult>) receiveResult.get(LST_CONSUMPTION_DETAILS)
            List<GroovyRowResult> lstProductionDetails = (List<GroovyRowResult>) receiveResult.get(LST_PRODUCTION_DETAILS)

            //delete parents(consumptionTransaction, productionTransaction) & corresponding children(lstConsumptionDetails, lstProductionDetails)
            boolean success = deleteForProductionWithConsumption(consumptionTransaction, productionTransaction, lstConsumptionDetails, lstProductionDetails)
            if (!success) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            //@todo:rollback
            throw new RuntimeException('Failed to delete inventory production')
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * @Params obj -N/A
     * @Return -a map containing delete success mesasge
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, SUCCESS_MASSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }


    /**
     * build failure result in case of any error
     * @Param obj -map returned from previous methods, can be null
     * @Return -a map containing isError = true & relevant error message to delete production-with-consumption
     */
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    /**
     * Method to check available material stock to delete in an inventory of a particular Finished-Item OR Production-Materials
     * @Param oldProdDetails -InvTransactionDetails Object which will be deleted
     *
     * @Return -if Stock is not available then return specific message otherwise return null
     */
    private String checkStockForProductionMaterial(Object oldProdDetails) {
        long itemId = Long.parseLong(oldProdDetails.item_id.toString())
        long inventoryId = Long.parseLong(oldProdDetails.inventory_id.toString())
        double oldQuantity = Double.parseDouble(oldProdDetails.actual_quantity.toString())
        double consumableStockQuantity = getConsumableStock(inventoryId, itemId)

        if (consumableStockQuantity < oldQuantity) {//Because consumable stock contains unapproved production
            Item item = (Item) itemCacheUtility.read(itemId)
            return "Insuficiant stock of ${item.name}"
        }
        return null
    }

    private static final String CONSUMABLE_STOCK_QUERY = """
        SELECT   consumeable_stock
        FROM vw_inv_inventory_consumable_stock
        WHERE inventory_id=:inventoryId
          AND item_id=:itemId
                      """

    /**
     * Method to get available(consumable) stock of a particular Finished-Item OR Production-Materials to delete in an inventory
     * @Param inventoryId -InvInventory.id
     * @Param itemId -Item.id
     *
     * @Return -consumableStock of a particular item in a particular inventory
     */
    private double getConsumableStock(long inventoryId, long itemId) {
        Map queryParams = [
                inventoryId: inventoryId,
                itemId: itemId
        ]
        List<GroovyRowResult> result = executeSelectSql(CONSUMABLE_STOCK_QUERY, queryParams)
        if (result.size() > 0) {
            return (double) result[0].consumeable_stock
        }
        return 0.0d
    }

    /**
     * Method to get productionObject By consumption id
     * @Param consumptionId -InvInventoryTransaction.id
     *
     * @Return -InvInventoryTransaction object exists then return InvInventoryTransaction object
     *          otherwise return null
     */
    private InvInventoryTransaction readProductionByConsumptionId(long consumptionId) {
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypePro = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_PRODUCTION, companyId)

        String query = """
        SELECT id, version FROM inv_inventory_transaction
        WHERE transaction_type_id = :typeProduction
        AND transaction_id = :transactionId
        """
        Map queryParams = [
                typeProduction: transactionTypePro.id,
                transactionId: consumptionId
        ]
        List result = executeSelectSql(query, queryParams)
        InvInventoryTransaction productionTransaction = new InvInventoryTransaction()
        if (result && result.size() > 0) {
            productionTransaction.id = result[0].id
            productionTransaction.version = result[0].version
            return productionTransaction
        }
        return null
    }

    /**
     * Method to get consumption/production Details objects
     * @Param invTransaction -InvInventoryTransaction object
     *
     * @Return -GroovyRowResult containing consumption/production Details objects
     */
    private List<GroovyRowResult> listProductionDetailsForDelete(InvInventoryTransaction invTransaction) {
        String query = """
        SELECT details.id, details.item_id, details.actual_quantity, details.inventory_id
        FROM inv_inventory_transaction_details details
        WHERE details.inventory_transaction_id = :parentId
        """
        Map queryParams = [parentId: invTransaction.id]
        List<GroovyRowResult> result = executeSelectSql(query, queryParams)
        return result
    }

    /**
     * Method to get count of approved and isCurrent children
     * @Param inventoryTransactionId -InvInventoryTransaction.id
     *
     * @Return -(int)total
     */
    private static final String SELECT_QUERY = """
             SELECT COUNT(COALESCE(id,0)) AS count FROM inv_inventory_transaction_details
                 WHERE inventory_transaction_id = :inventoryTransactionId AND
                       approved_by > 0 AND is_current = true """
    private int getApproveCountForProdWithCons(long inventoryTransactionId) {

        Map queryParams = [
                inventoryTransactionId: inventoryTransactionId
        ]
        List<GroovyRowResult> countResult = executeSelectSql(SELECT_QUERY, queryParams)
        int total = countResult[0].count
        return total
    }

    /**
     * Method to delete InvInventoryTransaction(Parents) and InvInventoryTransactionDetails(Children)
     *
     * @Param transactionCon -InvInventoryTransaction (Consumption parent-object)
     * @Param transactionProd -InvInventoryTransaction (Production parent-object)
     * @Param lstConsumptionDetails -list of InvInventoryTransactionConsumptionDetails (Consumption children-objects)
     * @Param lstProductionDetails -list of InvInventoryTransactionProductionDetails (Consumption production-objects)
     *
     * @Return boolean -if transactions successfully completed then return true, false otherwise
     */
    private boolean deleteForProductionWithConsumption(InvInventoryTransaction transactionCon, InvInventoryTransaction transactionProd,
                                                       List<GroovyRowResult> lstConsumptionDetails, List<GroovyRowResult> lstProductionDetails) {

        // delete the child objects for consumption then delete parent
        for (int i = 0; i < lstConsumptionDetails.size(); i++) {
            // delete consumption details and increase material in stock
            GroovyRowResult consumptionDetails = lstConsumptionDetails[i]
            long consumptionDetailsId = (long) consumptionDetails.id
            invInventoryTransactionDetailsService.delete(consumptionDetailsId) //delete consumption child
        }
        invInventoryTransactionService.delete(transactionCon.id) //delete consumption parent

        // delete the child objects for production then delete parent
        for (int i = 0; i < lstProductionDetails.size(); i++) {
            // delete production details and decrease material in stock
            // stock available already checked in precondition
            GroovyRowResult productionDetails = lstProductionDetails[i]
            long productionDetailsId = (long) productionDetails.id
            invInventoryTransactionDetailsService.delete(productionDetailsId)  //delete production child
        }
        invInventoryTransactionService.delete(transactionProd.id) //delete production partent

        return true
    }


}