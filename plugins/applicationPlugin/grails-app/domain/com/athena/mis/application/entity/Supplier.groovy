/**
 * Module Name - Application
 * Purpose - Supplier Information
 * */

package com.athena.mis.application.entity

class Supplier {
    String name         //Supplier Name
    String address      //Supplier Address
    String accountName  //Supplier Bank Account Name
    String bankName     //Supplier Bank Name
    String bankAccount  //Supplier Bank Account Number
    int itemCount       // no of item for this supplier
    long supplierTypeId //SystemEntity.id (e.g. Material Provider, Service Provider etc)
    long companyId      //Company.id
    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy = 0L     // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'supplier_id_seq']
        companyId index: 'supplier_company_id_idx'
        supplierTypeId index: 'supplier_supplier_type_id_idx'
        createdBy index: 'supplier_created_by_idx'
        updatedBy index: 'supplier_updated_by_idx'
    }

    static constraints = {
        name(blank: false, nullable: false)
        bankAccount(nullable: true)
        accountName(nullable: false)
        bankName(nullable: true)
        address(nullable: true)
        supplierTypeId(nullable: false)
        companyId(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }
}
