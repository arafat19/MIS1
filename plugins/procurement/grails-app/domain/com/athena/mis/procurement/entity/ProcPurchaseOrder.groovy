/**
 * Module Name - Procurement
 * Purpose - Entity of procurement purchase order
 * */

package com.athena.mis.procurement.entity

import com.athena.mis.utility.Tools

class ProcPurchaseOrder {
    long id                             // primary key (Auto generated by its own sequence)
    int version
    long projectId                      // Project.id
    long purchaseRequestId              // ProcPurchaseRequest.id
    int paymentMethodId                 // SystemEntity.id
    String modeOfPayment
    Date createdOn                      // Object Creation DateTime
    long createdBy                      // AppUser.id
    Date updatedOn                     // Object updated DateTime
    long updatedBy                      // AppUser.id
    String comments
    long approvedByDirectorId           // AppUser.id
    long approvedByProjectDirectorId    // AppUser.id
    int trCostCount                     // no of transport cost
    double trCostTotal                   // total transport cost for this PO
    long supplierId                     // Supplier.id
    int itemCount = 0
    double totalPrice = 0.0d             // total price of po items (eg sum(quantity*rate)-Discount-Vat/Tax+Tr.Cost))
    double discount = 0.0d
    double totalVatTax = 0.0d            // total VatTax of this PO (eg: sum(vatTax))
    long companyId                       // Company.id
    boolean sentForApproval

    static mapping = {
        id generator: 'sequence', params: [sequence: 'proc_purchase_order_id_seq']
        trCostTotal sqlType: "numeric(16,4)"
        totalPrice sqlType: "numeric(16,4)"
        discount sqlType: "numeric(16,4)"
        totalVatTax sqlType: "numeric(16,4)"

        projectId index: 'proc_purchase_order_project_id_idx'
        approvedByDirectorId index: 'proc_purchase_order_approved_by_director_id_idx'
        approvedByProjectDirectorId index: 'proc_purchase_order_approved_by_project_director_id_idx'
        supplierId index: 'proc_purchase_order_supplier_id_idx'
        companyId index: 'proc_purchase_order_company_id_idx'
        createdOn index: 'proc_purchase_order_created_on_idx'

        purchaseRequestId index: 'proc_purchase_order_purchase_request_id_idx'
        paymentMethodId index: 'proc_purchase_order_payment_method_id_idx'
        createdBy index: 'proc_purchase_order_created_by_idx'
        updatedBy index: 'proc_purchase_order_updated_by_idx'
    }

    static constraints = {
        projectId(nullable: false);
        purchaseRequestId(nullable: false);
        paymentMethodId(nullable: false);
        modeOfPayment(nullable: false);
        createdOn(nullable: false);
        createdBy(nullable: false);
        updatedOn(nullable: true);
        updatedBy(nullable: false);
        comments(nullable: true);
        approvedByDirectorId(nullable: false);
        approvedByProjectDirectorId(nullable: false);
        trCostCount(nullable: false);
        trCostTotal(nullable: false);
        supplierId(nullable: false);
        totalPrice(nullable: false);
        discount(nullable: false);
        totalVatTax(nullable: false);
    }

    static namedQueries = {
        searchByPurchaseRequestId { long purchaseRequestId, String queryType, String query ->
            eq("purchaseRequestId", purchaseRequestId)
            ilike(queryType, Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            setReadOnly(true)
        }

        searchByProjectIds { List<Long> projectIds, String queryType, String query ->
            eq("projectId", projectIds)
            ilike(queryType, Tools.PERCENTAGE + query + Tools.PERCENTAGE)
            setReadOnly(true)
        }

        upapprovedPOListByProjectIds { List<Long> projectIds ->
            'in'("projectId", projectIds)
            or {
                eq("approvedByDirectorId", 0L)
                eq("approvedByProjectDirectorId", 0L)
            }
            setReadOnly(true)
        }
    }
}
