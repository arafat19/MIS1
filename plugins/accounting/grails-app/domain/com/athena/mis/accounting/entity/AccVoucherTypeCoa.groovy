/**
 * Module Name - Accounting
 * Purpose - Entity of accounting voucher type chart of account
 **/

package com.athena.mis.accounting.entity

class AccVoucherTypeCoa {
    long id                     // primary key (Auto generated by its own sequence)
    int version
    long accVoucherTypeId       // SystemEntity.id (e.g: Payment Voucher-Bank, Payment Voucher-Cash)
    long coaId                  // AccChartOfAccount.id

    long createdBy              // AppUser.id
    Date createdOn              // Object creation DateTime
    long updatedBy              // AppUser.id
    Date updatedOn              // Object updated DateTime
    long companyId              // Company.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'acc_voucher_type_coa_id_seq']

        coaId index: 'acc_voucher_type_coa_coa_id_idx'
        accVoucherTypeId index: 'acc_voucher_type_coa_acc_voucher_type_id_idx'
        createdBy index: 'acc_voucher_type_coa_created_by_idx'
        updatedBy index: 'acc_voucher_type_coa_updated_by_idx'
        companyId index: 'acc_voucher_type_coa_company_id_idx'

    }

    static constraints = {
        accVoucherTypeId(nullable: false)
        coaId(nullable: false)
        updatedOn(nullable: true)
        companyId(nullable: false)
    }
}
