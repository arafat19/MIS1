/**
 * Module  - Procurement
 * Purpose - Entity that contains all properties of ProcCancelledPO
 */

package com.athena.mis.procurement.entity

class ProcCancelledPO {
    long id                             // ProcPurchaseOrder.id
    int version                         // ProcPurchaseOrder.version
    long projectId                      // Project.id
    long purchaseRequestId              // ProcPurchaseRequest.id
    int paymentMethodId                 // SystemEntity.id
    String modeOfPayment                // mode of payment
    Date createdOn                      // Object Creation DateTime
    long createdBy                      // AppUser.id
    Date updatedOn                      // Object updated DateTime
    long updatedBy                      // AppUser.id
    String comments                     // comments
    long approvedByDirectorId           // AppUser.id
    long approvedByProjectDirectorId    // AppUser.id
    int trCostCount                     // no. of transport cost
    double trCostTotal                  // total transport cost for this PO
    long supplierId                     // Supplier.id
    int itemCount                       // no. of item
    double totalPrice                   // total price of po items (eg sum(quantity*rate)-Discount-Vat/Tax+Tr.Cost))
    double discount                     // discount amount
    double totalVatTax                  // total VatTax of this PO (eg: sum(vatTax))
    long companyId                      // Company.id
    boolean sentForApproval             // boolean vale (true/false)
    long cancelledBy                    // AppUser.id
    Date cancelledOn                    // Object cancelled DateTime
    String cancelReason                 // reason of cancellation

    static mapping = {
        id generator: 'assigned'
    }

    static constraints = {
        updatedOn(nullable: true);
        comments(nullable: true);
    }
}
