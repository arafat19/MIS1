package com.athena.mis.accounting.entity

class AccLc {
    long id             // primary key (Auto generated by its own sequence)
    int version
    long itemId         //Item.id
    long supplierId     //Supplier.id
    long companyId      //Company.id
    double amount       // LC Amount

    String lcNo          //LC Number
    String bank          //Bank Name

    Date createdOn       // Object creation DateTime
    long createdBy       // AppUser.id
    Date updatedOn       // Object updated DateTime
    long updatedBy       // AppUser.id


    static mapping = {
        id generator: 'sequence', params: [sequence: 'acc_lc_id_seq']
        createdBy index: 'acc_lc_created_by_idx'
        updatedBy index: 'acc_lc_updated_by_idx'
        supplierId index: 'acc_lc_supplier_idx'
        itemId index: 'acc_lc_item_idx'
        companyId index: 'acc_lc_company_id_idx'
    }

    static constraints = {
        lcNo(nullable: false)
        itemId(nullable: false)
        supplierId(nullable: false)
        amount(nullable: false)
        updatedOn(nullable: true)
        companyId(nullable: false)
    }
}
