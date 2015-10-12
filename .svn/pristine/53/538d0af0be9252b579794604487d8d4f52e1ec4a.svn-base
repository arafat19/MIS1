package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcPurchaseOrderDetails
import com.athena.mis.utility.DateUtility

class PurchaseOrderDetailsService extends BaseService {

    static transactional = false

    /**
     * Method to save ProcPurchaseOrderDetails object
     * @param purchaseOrderDetails - object of ProcPurchaseOrderDetails
     * @return - newly created object of ProcPurchaseOrderDetails
     */
    public ProcPurchaseOrderDetails create(ProcPurchaseOrderDetails purchaseOrderDetails) {
        ProcPurchaseOrderDetails newPoDetails = purchaseOrderDetails.save()
        return newPoDetails
    }

    private static final String PROC_PURCHASE_ORDER_DETAILS_UPDATE_QUERY = """
                      UPDATE proc_purchase_order_details SET
                          quantity=:quantity,
                          vat_tax=:vatTax,
                          rate=:rate,
                          item_id=:itemId,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy,
                          comments=:comments,
                          version=:newVersion
                      WHERE
                          id=:id AND
                          version=:version
        """

    /**
     * Method to update ProcPurchaseOrderDetails object
     * @param purchaseOrderDetails - object of ProcPurchaseOrderDetails
     * @return - updateCount ( intValue ) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcPurchaseOrderDetails purchaseOrderDetails) {
        Map queryParams = [
                id: purchaseOrderDetails.id,
                version: purchaseOrderDetails.version,
                newVersion: purchaseOrderDetails.version + 1,
                quantity: purchaseOrderDetails.quantity,
                vatTax: purchaseOrderDetails.vatTax,
                rate: purchaseOrderDetails.rate,
                comments: purchaseOrderDetails.comments,
                itemId: purchaseOrderDetails.itemId,
                updatedOn: DateUtility.getSqlDateWithSeconds(purchaseOrderDetails.updatedOn),
                updatedBy: purchaseOrderDetails.updatedBy
        ]
        int updateCount = executeUpdateSql(PROC_PURCHASE_ORDER_DETAILS_UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update PO Details")
        }
        return updateCount
    }

    /**
     * Method to read ProcPurchaseOrderDetails object by id
     * @param id - ProcPurchaseOrderDetails.id
     * @return - object of ProcPurchaseOrderDetails
     */
    public ProcPurchaseOrderDetails read(long id) {
        return (ProcPurchaseOrderDetails) ProcPurchaseOrderDetails.read(id)
    }

    private static final String DELETE_QUERY = """
                                            DELETE FROM proc_purchase_order_details
                                                WHERE id=:podId
                                        """

    /**
     * Method to delete ProcPurchaseOrderDetails object
     * @param purchaseOrderDetails - object of ProcPurchaseOrderDetails
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(ProcPurchaseOrderDetails purchaseOrderDetails) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [podId: purchaseOrderDetails.id])
        if (deleteCount <= 0) {
            throw new RuntimeException('Purchase Order Details failed to delete')
        }
        return deleteCount
    }
}