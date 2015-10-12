/**
 * Module Name - Procurement
 * Purpose - Entity of procurement purchase order details
 * */


package com.athena.mis.procurement.entity

class ProcPurchaseOrderDetails {
    long id                         // primary key (Auto generated by its own sequence)
    int version
    long purchaseOrderId            // ProcPurchaseOrder.id (Parent)
    long projectId                  // Project.id
    long purchaseRequestId          // ProcPurchaseRequest.id
    long purchaseRequestDetailsId   // ProcPurchaseRequestDetails.id
    double quantity
    double rate                     // rate of item
    long itemId                     // Item.id
    double storeInQuantity           // store in quantity of Item - Type Material
    Date createdOn                   // Object creation DateTime
    long createdBy                  // AppUser.id
    Date updatedOn                  // Object updated DateTime
    long updatedBy                  // AppUser.id
    String comments
    long companyId                   // Company.id
    double fixedAssetDetailsCount    // fixed asset details count
    double vatTax = 0.0d

    static mapping = {
        id generator: 'sequence', params: [sequence: 'proc_purchase_order_details_id_seq']
        quantity sqlType: "numeric(16,4)"
        rate sqlType: "numeric(16,4)"
        storeInQuantity sqlType: "numeric(16,4)"
        fixedAssetDetailsCount sqlType: "numeric(16,4)"
        vatTax sqlType: "numeric(16,4)"

        itemId index: 'proc_purchase_order_details_item_id_idx'
        purchaseOrderId index: 'proc_purchase_order_details_purchase_order_id_idx'
        quantity index: 'proc_purchase_order_details_quantity_idx'
        rate index: 'proc_purchase_order_details_rate_idx'

        projectId index: 'proc_purchase_order_details_project_id_idx'
        purchaseRequestId index: 'proc_purchase_order_details_purchase_request_id_idx'
        purchaseRequestDetailsId index: 'proc_purchase_order_details_purchase_request_details_id_idx'
        createdBy index: 'proc_purchase_order_details_created_by_idx'
        updatedBy index: 'proc_purchase_order_details_updated_by_idx'
        companyId index: 'proc_purchase_order_details_company_id_idx'
    }

    static constraints = {
        purchaseOrderId(nullable: false);
        projectId(nullable: false);
        purchaseRequestId(nullable: false);
        purchaseRequestDetailsId(nullable: false);
        quantity(nullable: false);
        rate(nullable: false);
        storeInQuantity(nullable: false);
        fixedAssetDetailsCount(nullable: false);
        vatTax(nullable: false);
        itemId(nullable: false);
        createdOn(nullable: false);
        createdBy(nullable: false);
        updatedOn(nullable: true);
        updatedBy(nullable: false);
        comments(nullable: true);
    }
}
