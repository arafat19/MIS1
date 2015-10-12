package com.athena.mis.inventory.service

import com.athena.mis.BaseService
import com.athena.mis.inventory.entity.InvInventoryTransactionDetails
import com.athena.mis.utility.DateUtility

class InvInventoryTransactionDetailsService extends BaseService {

    static transactional = false

    /**
     * Method to read InventoryTransactionDetails object by id
     * @param id -InventoryTransactionDetails.id
     * @return -InventoryTransactionDetails object
     */
    public InvInventoryTransactionDetails read(long id) {
        InvInventoryTransactionDetails invInventoryTransactionDetails = InvInventoryTransactionDetails.read(id)
        return invInventoryTransactionDetails
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS_CREATE_QUERY = """
            INSERT INTO inv_inventory_transaction_details(
                    id, version, acknowledged_by, actual_quantity, approved_by,
                    comments, created_by, created_on, fifo_quantity, inventory_id, inventory_transaction_id,
                    inventory_type_id, lifo_quantity, item_id, mrf_no, rate, shrinkage,
                    fixed_asset_id, fixed_asset_details_id,
                    stack_measurement, supplied_quantity, supplier_chalan, transaction_date,
                    transaction_details_id, updated_by, updated_on,
                    vehicle_id, vehicle_number,transaction_type_id,adjustment_parent_id,
                    is_current,is_increase,approved_on,overhead_cost,invoice_acknowledged_by)
            VALUES (NEXTVAL('inv_inventory_transaction_details_id_seq'),
                    :version, :acknowledgedBy, :actualQuantity, :approvedBy,
                    :comments, :createdBy, :createdOn, :fifoQuantity, :inventoryId,
                    :inventoryTransactionId,
                    :inventoryTypeId, :lifoQuantity, :itemId, :mrfNo, :rate, :shrinkage,
                    :fixedAssetId, :fixedAssetDetailsId,
                    :stackMeasurement, :suppliedQuantity, :supplierChalan, :transactionDate,
                    :transactionDetailsId, :updatedBy,
                    :updatedOn,:vehicleId, :vehicleNumber,:transactionTypeId,:adjustmentParentId,:isCurrent,:isIncrease,
                    :approvedOn,:overheadCost, :invoiceAcknowledgedBy)
                   """
    /**
     * Method to save InvInventoryTransactionDetails object
     * @param invInventoryTransactionDetails -invInventoryTransactionDetails object
     * @return-newly created invInventoryTransactionDetails object
     */
    public InvInventoryTransactionDetails create(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                version: invInventoryTransactionDetails.version,
                createdOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.createdOn),
                transactionDate: DateUtility.getSqlDate(invInventoryTransactionDetails.transactionDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn),
                approvedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.approvedOn),
                acknowledgedBy: invInventoryTransactionDetails.acknowledgedBy,
                actualQuantity: invInventoryTransactionDetails.actualQuantity,
                approvedBy: invInventoryTransactionDetails.approvedBy,
                comments: invInventoryTransactionDetails.comments,
                createdBy: invInventoryTransactionDetails.createdBy,
                fifoQuantity: invInventoryTransactionDetails.fifoQuantity,
                inventoryId: invInventoryTransactionDetails.inventoryId,
                inventoryTransactionId: invInventoryTransactionDetails.inventoryTransactionId,
                inventoryTypeId: invInventoryTransactionDetails.inventoryTypeId,
                lifoQuantity: invInventoryTransactionDetails.lifoQuantity,
                itemId: invInventoryTransactionDetails.itemId,
                mrfNo: invInventoryTransactionDetails.mrfNo,
                rate: invInventoryTransactionDetails.rate,
                shrinkage: invInventoryTransactionDetails.shrinkage,
                fixedAssetId: invInventoryTransactionDetails.fixedAssetId,
                fixedAssetDetailsId: invInventoryTransactionDetails.fixedAssetDetailsId,
                stackMeasurement: invInventoryTransactionDetails.stackMeasurement,
                suppliedQuantity: invInventoryTransactionDetails.suppliedQuantity,
                supplierChalan: invInventoryTransactionDetails.supplierChalan,
                transactionDetailsId: invInventoryTransactionDetails.transactionDetailsId,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                vehicleId: invInventoryTransactionDetails.vehicleId,
                vehicleNumber: invInventoryTransactionDetails.vehicleNumber,
                transactionTypeId: invInventoryTransactionDetails.transactionTypeId,
                adjustmentParentId: invInventoryTransactionDetails.adjustmentParentId,
                isCurrent: invInventoryTransactionDetails.isCurrent,
                isIncrease: invInventoryTransactionDetails.isIncrease,
                overheadCost: invInventoryTransactionDetails.overheadCost,
                invoiceAcknowledgedBy: invInventoryTransactionDetails.invoiceAcknowledgedBy
        ]
        List result = executeInsertSql(INV_INVENTORY_TRANSACTION_DETAILS_CREATE_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException("Fail to create inventory transaction details")
        }
        int inventoryTransactionDetailsId = (int) result[0][0]
        invInventoryTransactionDetails.id = inventoryTransactionDetailsId
        return invInventoryTransactionDetails
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY = """
                    UPDATE inv_inventory_transaction_details
                        SET  version = :newVersion,
                             acknowledged_by = :acknowledgedBy,
                             actual_quantity = :actualQuantity,
                             comments = :comments,
                             fifo_quantity = :fifoQuantity,
                             lifo_quantity = :lifoQuantity,
                             item_id = :itemId,
                             mrf_no = :mrfNo,
                             rate = :rate,
                             shrinkage = :shrinkage,
                             stack_measurement = :stackMeasurement,
                             supplied_quantity = :suppliedQuantity,
                             supplier_chalan = :supplierChalan,
                             transaction_details_id = :transactionDetailsId,
                             updated_by = :updatedBy,
                             transaction_date = :transactionDate,
                             updated_on = :updatedOn,
                             vehicle_id = :vehicleId,
                             vehicle_number = :vehicleNumber
                    WHERE id = :id AND
                        version = :version
                        """
    /**
     * Method to update invInventoryTransactionDetails object
     * @param invInventoryTransactionDetails -invInventoryTransactionDetails object
     * @return -updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(InvInventoryTransactionDetails invInventoryTransactionDetails) {
        Map queryParams = [
                id: invInventoryTransactionDetails.id,
                version: invInventoryTransactionDetails.version,
                newVersion: invInventoryTransactionDetails.version + 1,
                transactionDate: DateUtility.getSqlDate(invInventoryTransactionDetails.transactionDate),
                updatedOn: DateUtility.getSqlDateWithSeconds(invInventoryTransactionDetails.updatedOn),
                acknowledgedBy: invInventoryTransactionDetails.acknowledgedBy,
                actualQuantity: invInventoryTransactionDetails.actualQuantity,
                comments: invInventoryTransactionDetails.comments,
                fifoQuantity: invInventoryTransactionDetails.fifoQuantity,
                lifoQuantity: invInventoryTransactionDetails.lifoQuantity,
                itemId: invInventoryTransactionDetails.itemId,
                mrfNo: invInventoryTransactionDetails.mrfNo,
                rate: invInventoryTransactionDetails.rate,
                shrinkage: invInventoryTransactionDetails.shrinkage,
                stackMeasurement: invInventoryTransactionDetails.stackMeasurement,
                suppliedQuantity: invInventoryTransactionDetails.suppliedQuantity,
                supplierChalan: invInventoryTransactionDetails.supplierChalan,
                transactionDetailsId: invInventoryTransactionDetails.transactionDetailsId,
                updatedBy: invInventoryTransactionDetails.updatedBy,
                vehicleId: invInventoryTransactionDetails.vehicleId,
                vehicleNumber: invInventoryTransactionDetails.vehicleNumber
        ]
        int updateCount = executeUpdateSql(INV_INVENTORY_TRANSACTION_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update inventory transaction details")
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
        DELETE FROM inv_inventory_transaction_details
        WHERE  id=:id
    """
    /**
     * Method to delete invInventoryTransactionDetails object
     * @param id -invInventoryTransactionDetails.id
     * @return -if deleteCount <= 0 then throw exception to rollback transaction; otherwise return true
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to delete transaction details")
        }
        return Boolean.TRUE
    }

}