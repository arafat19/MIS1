package com.athena.mis.inventory.service

import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvInventoryTransaction
import com.athena.mis.utility.DateUtility

class InvInventoryTransactionService extends BaseService {

    static transactional = false

    /**
     * Method to read InventoryTransaction object by id
     * @param id -InvInventoryTransaction.id
     * @return -InvInventoryTransaction object
     */
    public InvInventoryTransaction read(long id) {
        InvInventoryTransaction invInventoryTransaction = InvInventoryTransaction.read(id)
        return invInventoryTransaction
    }

    private static final String INV_INVENTORY_TRANSACTION_CREATE_QUERY = """
            INSERT INTO inv_inventory_transaction(
                    id, version, budget_id, comments, company_id, created_by, created_on, inventory_id,
                    inv_production_line_item_id, inventory_type_id, item_count, project_id,
                    transaction_entity_id, transaction_entity_type_id, transaction_id, transaction_date,
                    transaction_type_id, updated_by, updated_on, is_approved)
            VALUES (NEXTVAL('inv_inventory_transaction_id_seq'), :version, :budgetId, :comments, :companyId,
                    :createdBy, :createdOn, :inventoryId, :invProductionLineItemId, :inventoryTypeId, :itemCount,
                    :projectId, :transactionEntityId, :transactionEntityTypeId, :transactionId, :transactionDate,
                    :transactionTypeId, :updatedBy, :updatedOn, :isApproved)
    """
    /**
     * Method to save InvInventoryTransaction object
     * @param invInventoryTransaction -InvInventoryTransaction object
     * @return-newly created invInventoryTransaction object
     */
    public InvInventoryTransaction create(InvInventoryTransaction invInventoryTransaction) {
        Map queryParams = [
                version: 0,
                budgetId: invInventoryTransaction.budgetId,
                createdOn: DateUtility.getSqlDateWithSeconds(invInventoryTransaction.createdOn),
                transactionDate: DateUtility.getSqlDate(invInventoryTransaction.transactionDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransaction.updatedOn),
                comments: invInventoryTransaction.comments,
                companyId: invInventoryTransaction.companyId,
                createdBy: invInventoryTransaction.createdBy,
                inventoryId: invInventoryTransaction.inventoryId,
                invProductionLineItemId: invInventoryTransaction.invProductionLineItemId,
                inventoryTypeId: invInventoryTransaction.inventoryTypeId,
                itemCount: invInventoryTransaction.itemCount,
                projectId: invInventoryTransaction.projectId,
                transactionEntityId: invInventoryTransaction.transactionEntityId,
                transactionEntityTypeId: invInventoryTransaction.transactionEntityTypeId,
                transactionId: invInventoryTransaction.transactionId,
                transactionTypeId: invInventoryTransaction.transactionTypeId,
                updatedBy: invInventoryTransaction.updatedBy,
                isApproved: invInventoryTransaction.isApproved
        ]

        List result = executeInsertSql(INV_INVENTORY_TRANSACTION_CREATE_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException("Fail to create inventory transaction")
        }
        int inventoryTransactionId = (int) result[0][0]
        invInventoryTransaction.id = inventoryTransactionId
        return invInventoryTransaction
    }

    private static final String INV_INVENTORY_TRANSACTION_UPDATE_QUERY = """
            UPDATE inv_inventory_transaction
                    SET  version = :newVersion,
                         budget_id = :budgetId,
                         comments = :comments,
                         inv_production_line_item_id = :invProductionLineItemId,
                         inventory_type_id = :inventoryTypeId,
                         inventory_id = :inventoryId,
                         item_count = :itemCount,
                         project_id = :projectId,
                         transaction_entity_id = :transactionEntityId,
                         transaction_id = :transactionId,
                         transaction_date = :transactionDate,
                         updated_by = :updatedBy,
                         updated_on = :updatedOn
                    WHERE id = :id AND
                    version = :version
    """
    /**
     * Method to update invInventoryTransaction object
     * @param invInventoryTransaction -InvInventoryTransaction object
     * @return -updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(InvInventoryTransaction invInventoryTransaction) {
        Map queryParams = [
                id: invInventoryTransaction.id,
                version: invInventoryTransaction.version,
                newVersion: invInventoryTransaction.version + 1,
                transactionDate: DateUtility.getSqlDate(invInventoryTransaction.transactionDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransaction.updatedOn),
                budgetId: invInventoryTransaction.budgetId,
                comments: invInventoryTransaction.comments,
                invProductionLineItemId: invInventoryTransaction.invProductionLineItemId,
                inventoryTypeId: invInventoryTransaction.inventoryTypeId,
                inventoryId: invInventoryTransaction.inventoryId,
                itemCount: invInventoryTransaction.itemCount,
                projectId: invInventoryTransaction.projectId,
                transactionEntityId: invInventoryTransaction.transactionEntityId,
                transactionId: invInventoryTransaction.transactionId,
                updatedBy: invInventoryTransaction.updatedBy
        ]
        int updateCount = executeUpdateSql(INV_INVENTORY_TRANSACTION_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update inventory transaction")
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM inv_inventory_transaction
        WHERE  id=:id
    """
    /**
     * Method to delete invInventoryTransaction object
     * @param id -InvInventoryTransaction.id
     * @return -if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at invInventoryTransactionService.delete')
        }
        return Boolean.TRUE
    }

    /**
     * Method to read production-parent object by consumption-parent-id (Used in InventoryProductionWithConsumption)
     * @param id -InvInventoryTransaction.transactionId
     * @return -InvInventoryTransaction object
     */
    public InvInventoryTransaction readByTransactionId(long id) {
        InvInventoryTransaction invTransactionProduction = InvInventoryTransaction.findByTransactionId(id, [readOnly: true])
        return invTransactionProduction
    }
}
