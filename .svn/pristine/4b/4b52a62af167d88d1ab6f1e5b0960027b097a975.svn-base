/**
 * Module  - Procurement
 * Purpose - Entity that contains all properties of ProcCancelledPODetails
 */

package com.athena.mis.procurement.entity

class ProcCancelledPODetails {
    long id                         // ProcPurchaseOrderDetails.id
    int version                     // ProcPurchaseOrderDetails.version
    long purchaseOrderId            // ProcPurchaseOrder.id (Parent)
    long projectId                  // Project.id
    long purchaseRequestId          // ProcPurchaseRequest.id
    long purchaseRequestDetailsId   // ProcPurchaseRequestDetails.id
    double quantity                 // quantity of PO details
    double rate                     // rate of item
    long itemId                     // Item.id
    double storeInQuantity          // store in quantity of Item
    Date createdOn                  // Object creation DateTime
    long createdBy                  // AppUser.id
    Date updatedOn                  // Object updated DateTime
    long updatedBy                  // AppUser.id
    String comments                 // comments
    long companyId                  // Company.id
    double fixedAssetDetailsCount   // fixed asset details count
    double vatTax                   // amount of vat tax

    static mapping = {
        id generator: 'assigned'
    }

    static constraints = {
        updatedOn(nullable: true);
        comments(nullable: true);
    }
}
